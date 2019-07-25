# Getting

    // Scala 2.11, 2.12 is supported
    "com.github.fsw0422" %% "yahoofinancehistoryfetcher" % "0.2.0"

# Usage

    val ticker = "FB"
    val startDate = 0L
    val endDate = DateTime.now.clicks
    val interval = "1d"
    
    val stockFetcher = StockFetcher()
    val stockHistory: IO[Seq[StockDf]] = stockFetcher.getStockHistory(ticker, startDate, endDate, interval)
    
    // ... compose function and invoke run
