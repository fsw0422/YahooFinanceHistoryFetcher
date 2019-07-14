import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import org.apache.log4j.varia.NullAppender
import org.joda.time.DateTime
import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.ws.ahc.StandaloneAhcWSClient

class StockFetcherSpec extends FlatSpec with Matchers {
  implicit val actorSystem = ActorSystem("Test")
  implicit val executionContext = actorSystem.dispatcher
  implicit val actorMaterializerSettings = ActorMaterializerSettings(actorSystem)
  implicit val actorMaterializer = ActorMaterializer(actorMaterializerSettings)(actorSystem)
  implicit val standaloneAhcWSClient = StandaloneAhcWSClient()

  behavior of "StockFetcher"

  it should "return historical records from Yahoo Finance when getStockHistory is invoked" in {
    val stockFetcher = StockFetcher()

    val ticker = "FB"
    val startDate = 0L
    val endDate = DateTime.now.getMillis
    val interval = "1d"

    val stockDaos = stockFetcher
      .getStockHistory(ticker, startDate, endDate, interval)
      .unsafeRunSync()

    stockDaos.size should be > 0
  }
}
