package com.example.cryptomarket.data.mappter

import com.example.cryptomarket.data.remote.dto.ExchangeDto
import com.example.cryptomarket.domain.model.Exchange

fun ExchangeDto.toExchange(): Exchange {
    return Exchange(
        id = id,
        name = name,
        url = url,
        image = image,
        country = country,
        trustScore = trustScore,
        trustScoreRank = trustScoreRank
    )
}