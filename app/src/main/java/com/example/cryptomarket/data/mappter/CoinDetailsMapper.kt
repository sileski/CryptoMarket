package com.example.cryptomarket.data.mappter

import com.example.cryptomarket.data.remote.dto.CoinDetailsDescriptionDto
import com.example.cryptomarket.data.remote.dto.CoinDetailsDto
import com.example.cryptomarket.data.remote.dto.CoinDetailsMarketDataCurrentPriceDto
import com.example.cryptomarket.data.remote.dto.CoinDetailsMarketDataDto
import com.example.cryptomarket.domain.model.CoinDetails
import com.example.cryptomarket.domain.model.CoinDetailsDescription
import com.example.cryptomarket.domain.model.CoinDetailsMarketData
import com.example.cryptomarket.domain.model.CoinDetailsMarketDataCurrentPrice

fun CoinDetailsDto.toCoinDetails(): CoinDetails {
    return CoinDetails(
        id = id,
        name = name,
        symbol = symbol,
        description = description.toCoinDetailsDescription(),
        marketData = marketData.toCoinDetailsMarketData()
    )
}

fun CoinDetailsDescriptionDto.toCoinDetailsDescription(): CoinDetailsDescription {
    return CoinDetailsDescription(
        en = en
    )
}

fun CoinDetailsMarketDataDto.toCoinDetailsMarketData(): CoinDetailsMarketData {
    return CoinDetailsMarketData(
        currentPrice = currentPrice.toCoinDetailsMarketDataCurrentPrice(),
        priceChangePercentage24h = priceChangePercentage24h
    )
}

fun CoinDetailsMarketDataCurrentPriceDto.toCoinDetailsMarketDataCurrentPrice(): CoinDetailsMarketDataCurrentPrice {
    return CoinDetailsMarketDataCurrentPrice(
        usd = usd,
        eur = eur,
        btc = btc,
        gbp = gbp,
        cad = cad,
        jpy = jpy,
        eth = eth
    )
}