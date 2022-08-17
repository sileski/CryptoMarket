package com.example.cryptomarket.presentation.exchanges

sealed class ExchangesEvent {
    data class LoadingExchanges(val loading: Boolean) : ExchangesEvent()
    data class OnRefresh(val refresh: Boolean) : ExchangesEvent()
    data class OnError(val error: String) : ExchangesEvent()
}