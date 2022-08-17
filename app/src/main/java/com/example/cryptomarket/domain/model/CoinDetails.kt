package com.example.cryptomarket.domain.model

data class CoinDetails(
    val id: String,
    val name: String,
    val symbol: String,
    val description: CoinDetailsDescription,
    val marketData: CoinDetailsMarketData
)

data class CoinDetailsDescription(
    val en: String
)

data class CoinDetailsMarketData(
    val currentPrice: CoinDetailsMarketDataCurrentPrice,
    val priceChangePercentage24h: Double
)

data class CoinDetailsMarketDataCurrentPrice(
    val usd: Double,
    val eur: Double,
    val btc: Double,
    val gbp: Double,
    val cad: Double,
    val jpy: Double,
    val eth: Double
)