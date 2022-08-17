package com.example.cryptomarket.presentation.market

import com.example.cryptomarket.domain.filter_options.CoinsFilterCategories
import com.example.cryptomarket.domain.model.Coin

sealed class MarketEvent {
    data class Refresh(val refresh: Boolean) : MarketEvent()
    data class OpenFilterOptionsMenu(val selectedCoinsFilterCategory: CoinsFilterCategories) :
        MarketEvent()

    object CloseFilterOptionsMenu : MarketEvent()
    data class FilterOptionsItemSelected(val option: String) : MarketEvent()
    data class AddRemoveCoinWatchList(val coin: Coin) : MarketEvent()
    data class RemoveCoinFromWatchList(val coin: Coin) : MarketEvent()
    data class PagerPageChange(val page: Int) : MarketEvent()
    data class LoadingCoins(val loading: Boolean) : MarketEvent()
    data class OnError(val error: String) : MarketEvent()
    data class LoadingWatchlist(val loading: Boolean) : MarketEvent()
}
