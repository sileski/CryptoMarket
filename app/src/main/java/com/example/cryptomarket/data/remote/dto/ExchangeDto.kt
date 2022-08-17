package com.example.cryptomarket.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ExchangeDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("country")
    val country: String?,
    @SerializedName("trust_score")
    val trustScore: Int?,
    @SerializedName("trust_score_rank")
    val trustScoreRank: Int?
)
