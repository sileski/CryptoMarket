package com.example.cryptomarket.data.mappter

import com.example.cryptomarket.data.local.CoinEntity
import com.example.cryptomarket.data.local.WatchlistEntity
import com.example.cryptomarket.data.remote.dto.CoinDto
import com.example.cryptomarket.domain.model.Coin

fun CoinDto.toCoin(): Coin {
    return Coin(
        id = id,
        name = name,
        symbol = symbol,
        image = image,
        currentPrice = currentPrice,
        pricePercentageChange1h = pricePercentageChange1h,
        pricePercentageChange24h = pricePercentageChange24h,
        pricePercentageChange7d = pricePercentageChange7d,
        pricePercentageChange30d = pricePercentageChange30d,
        pricePercentageChange1y = pricePercentageChange1y
    )
}

fun CoinDto.toCoinEntity(): CoinEntity {
    return CoinEntity(
        coin_id = id,
        name = name,
        symbol = symbol,
    )
}

fun Coin.toWatchlistEntity(): WatchlistEntity {
    return WatchlistEntity(
        coin_id = id,
        name = name,
        symbol = symbol
    )
}

fun CoinEntity.toCoin(): Coin {
    return Coin(
        id = coin_id,
        name = name,
        symbol = symbol
    )
}
