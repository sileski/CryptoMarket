package com.example.cryptomarket.domain.model

import java.time.LocalDateTime

data class CoinOhlc(
    val date: LocalDateTime,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double
)
