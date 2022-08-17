package com.example.cryptomarket.presentation.cryptocurrency_search

sealed class CryptocurrencySearchEvent {
    data class SearchCoins(val query: String) : CryptocurrencySearchEvent()
    object ClearSearchQuery : CryptocurrencySearchEvent()
}
