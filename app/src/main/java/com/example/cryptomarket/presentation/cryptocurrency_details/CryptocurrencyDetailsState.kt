package com.example.cryptomarket.presentation.cryptocurrency_details

import com.example.cryptomarket.domain.model.CoinDetails
import com.example.cryptomarket.domain.model.CoinOhlc

data class CryptocurrencyDetailsState(
    val chartData: List<CoinOhlc>? = null,
    val coinDetails: CoinDetails? = null,
    val selectedCurrencyOption: String = "",
    val isCoinInWatchlist: Boolean = false,
    val isLoadingChart: Boolean = true,
    val isLoadingDetails: Boolean = true,
    val errorChart: String? = null,
    val errorDetails: String? = null,
    val priceDifference: Double = 0.0,
    val selectedChartDaysFilterOption: String = "",
    val changesInWatchlist: Boolean? = null,
    val initialIsCoinInWatchlist: Boolean? = null
)
