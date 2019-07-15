import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.{Cookie, RawHeader}
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import akka.http.scaladsl.unmarshalling.Unmarshal
import cats.effect.IO

case class StockFetcher(implicit actorSystem: ActorSystem) {

  def getStockHistory(
    ticker: String,
    startDate: Long,
    endDate: Long,
    interval: String
  ): IO[Seq[StockDf]] = {
    for {
      session <- getSession(ticker)
      crumb <- IO(extractCrumb(session._2))
      stockHistory <- getData(session._1, ticker, startDate, endDate, interval, crumb)
    } yield stockHistory
  }

  private def extractCrumb(sessionBody: String): String = {
    """"CrumbStore":\{"crumb":"([^"]+)"\}"""
      .r("crumb")
      .findFirstMatchIn(sessionBody)
      .get
      .group("crumb")
      .replaceAll("\\u002F", "/")
  }

  private def getSession(ticker: String): IO[(String, String)] = {
    IO.fromFuture(IO {
      val url = String.format("https://finance.yahoo.com/quote/%s/history", ticker)
      val request = HttpRequest(method = HttpMethods.GET, uri = url)
        .withHeaders(RawHeader("charset", "utf-8"))
      Http().singleRequest(request)
        .map(session => {
          (session.headers.head.value, session.entity.toString)
          Unmarshal(session.entity.dataBytes)
        })(actorSystem.dispatcher)
    })
  }

  private def getData(
    cookie: String,
    ticker: String,
    startDate: Long,
    endDate: Long,
    interval: String,
    crumb: String,
  ): IO[Seq[StockDf]] = {
    IO.fromFuture(IO {
      val url = String.format(
        "https://query1.finance.yahoo.com/v7/finance/download/%s?period1=%s&period2=%s&interval=%s&events=history&crumb=%s",
        ticker,
        startDate.toString,
        endDate.toString,
        interval,
        crumb
      )
      val request = HttpRequest(method = HttpMethods.GET, uri = url)
        .withHeaders(
          RawHeader("Cookie", cookie),
          RawHeader("charset", "utf-8")
        )
      Http().singleRequest(request)
        .map(data => StockDf.mapDataToDf(data.entity.toString))(actorSystem.dispatcher)
    })
  }

}
