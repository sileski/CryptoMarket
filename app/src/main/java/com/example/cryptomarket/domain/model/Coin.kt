package com.example.cryptomarket.domain.model

data class Coin(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String = "",
    val currentPrice: Double = 0.0,
    val pricePercentageChange1h: Double = 0.0,
    val pricePercentageChange24h: Double = 0.0,
    val pricePercentageChange7d: Double = 0.0,
    val pricePercentageChange30d: Double = 0.0,
    val pricePercentageChange1y: Double = 0.0
)
