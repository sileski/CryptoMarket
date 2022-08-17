package com.example.cryptomarket.presentation.exchanges

data class ExchangesState(
    val isLoading: Boolean = true,
    val error: String? = null,
    var refresh: Boolean = false
)