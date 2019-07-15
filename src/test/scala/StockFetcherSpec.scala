import akka.actor.ActorSystem
import org.scalatest.{FlatSpec, Matchers}

class StockFetcherSpec extends FlatSpec with Matchers {
  implicit val actorSystem = ActorSystem("Test")

  behavior of "StockFetcher"

  it should "return historical records from Yahoo Finance when getStockHistory is invoked" in {
    val stockFetcher = StockFetcher()

    val ticker = "FB"
    val startDate = 0L
    val endDate = 1563226210
    val interval = "1d"

    val stockDaos = stockFetcher
      .getStockHistory(ticker, startDate, endDate, interval)
      .unsafeRunSync()

    stockDaos.size should be > 0
  }
}
