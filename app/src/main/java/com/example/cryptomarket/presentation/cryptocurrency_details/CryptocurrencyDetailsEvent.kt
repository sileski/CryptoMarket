package com.example.cryptomarket.presentation.cryptocurrency_details

sealed class CryptocurrencyDetailsEvent {
    data class AddRemoveCoinWatchList(
        val coinId: String,
        val coinName: String,
        val coinSymbol: String
    ) : CryptocurrencyDetailsEvent()

    data class SelectedDaysFilterOption(val daysFilterOption: String, val coinId: String) :
        CryptocurrencyDetailsEvent()
}
