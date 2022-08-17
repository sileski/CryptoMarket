package com.example.cryptomarket.presentation.market

import com.example.cryptomarket.domain.filter_options.CoinsFilterCategories

data class MarketState(
    val isLoadingCoins: Boolean = true,
    val isLoadingWatchlist: Boolean = true,
    val refreshCoins: Boolean = false,
    val refreshWatchlist: Boolean = false,
    val error: String? = null,
    val showFilterOptionsMenu: Boolean = false,
    val selectedFilterOptionCategory: CoinsFilterCategories? = null,
    val selectedCurrencyOption: String = "",
    val selectedSortOption: String = "",
    val selectedOrderOption: String = "",
    val selectedPricePercentageChangeOption: String = "",
    val watchlistCoinsIds: List<String> = emptyList(),
    val pagerPage: Int = 0,
    val changesInWatchlist: Boolean = false
)
