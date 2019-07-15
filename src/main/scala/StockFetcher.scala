import cats.effect.IO
import play.api.libs.ws.WSCookie
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.ExecutionContext

case class StockFetcher(
  implicit executionContext: ExecutionContext,
  standaloneAhcWSClient: StandaloneAhcWSClient
) {

  def getStockHistory(
    ticker: String,
    startDate: Long,
    endDate: Long,
    interval: String
  ): IO[Seq[StockDf]] = {
    for {
      session <- getSession(ticker)
      crumb <- IO(extractCrumb(session._2))
      stockHistory <- getData(session._1, crumb, ticker, startDate, endDate, interval)
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

  private def getSession(ticker: String): IO[(WSCookie, String)] = {
    IO.fromFuture {
      IO {
        standaloneAhcWSClient
          .url(s"https://finance.yahoo.com/quote/$ticker/history")
          .addHttpHeaders(("charset", "utf-8"))
          .get
      }
    }.map(session => (session.cookies.head, session.body))
  }

  private def getData(
    wsCookie: WSCookie,
    crumb: String,
    ticker: String,
    startDate: Long,
    endDate: Long,
    interval: String
  ): IO[Seq[StockDf]] = {
    IO.fromFuture {
      IO {
        standaloneAhcWSClient
          .url(s"https://query1.finance.yahoo.com/v7/finance/download/$ticker?period1=${startDate.toString}&period2=${endDate.toString}&interval=$interval&events=history&crumb=$crumb")
          .addHttpHeaders(("charset", "utf-8"))
          .addCookies(wsCookie)
          .get
      }
    }.map(data => StockDf.mapDataToDf(data.body))
  }

}
