package com.example.cryptomarket.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CoinDetailsDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("description")
    val description: CoinDetailsDescriptionDto,
    @SerializedName("market_data")
    val marketData: CoinDetailsMarketDataDto
)

data class CoinDetailsDescriptionDto(
    @SerializedName("en")
    val en: String
)

data class CoinDetailsMarketDataDto(
    @SerializedName("current_price")
    val currentPrice: CoinDetailsMarketDataCurrentPriceDto,
    @SerializedName("price_change_percentage_24h")
    val priceChangePercentage24h: Double
)

data class CoinDetailsMarketDataCurrentPriceDto(
    @SerializedName("usd")
    val usd: Double,
    @SerializedName("eur")
    val eur: Double,
    @SerializedName("btc")
    val btc: Double,
    @SerializedName("gbp")
    val gbp: Double,
    @SerializedName("cad")
    val cad: Double,
    @SerializedName("jpy")
    val jpy: Double,
    @SerializedName("eth")
    val eth: Double
)
