import scala.language.higherKinds

case class StockDf(date: String,
                   open: Double,
                   high: Double,
                   low: Double,
                   close: Double,
                   adjClose: Double,
                   volume: Long)

object StockDf {

  def mapDataToDf(data: String): Seq[StockDf] = {
    for {
      line <- data.split("\n").drop(1) // drop the column names
      stockDf = {
        val words = line.split(",")
        StockDf(
          date = words(0),
          open = words(1).toDouble,
          high = words(2).toDouble,
          low = words(3).toDouble,
          close = words(4).toDouble,
          adjClose = words(5).toDouble,
          volume = words(6).toLong
        )
      }
    } yield stockDf
  }
}
