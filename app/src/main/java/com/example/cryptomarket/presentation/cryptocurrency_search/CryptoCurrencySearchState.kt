package com.example.cryptomarket.presentation.cryptocurrency_search

import com.example.cryptomarket.domain.model.Coin

data class CryptoCurrencySearchState(
    val coins: List<Coin> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
