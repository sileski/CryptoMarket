package com.example.cryptomarket.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CoinDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("current_price")
    val currentPrice: Double,
    @SerializedName("price_change_percentage_1h_in_currency")
    val pricePercentageChange1h: Double = 0.0,
    @SerializedName("price_change_percentage_24h_in_currency")
    val pricePercentageChange24h: Double = 0.0,
    @SerializedName("price_change_percentage_7d_in_currency")
    val pricePercentageChange7d: Double = 0.0,
    @SerializedName("price_change_percentage_30d_in_currency")
    val pricePercentageChange30d: Double = 0.0,
    @SerializedName("price_change_percentage_1y_in_currency")
    val pricePercentageChange1y: Double = 0.0
)
