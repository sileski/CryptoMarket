package com.example.cryptomarket.domain.model

data class Exchange(
    val id: String,
    val name: String?,
    val url: String?,
    val image: String?,
    val country: String?,
    val trustScore: Int?,
    val trustScoreRank: Int?
)
