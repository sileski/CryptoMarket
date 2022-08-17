package com.example.cryptomarket.domain.currency_format

import java.text.DecimalFormat
import java.text.NumberFormat

interface PriceCurrencyFormat {
    fun formatCurrency(
        currencyFormat: NumberFormat,
        cryptoFormat: DecimalFormat,
        price: Double,
        selectedCurrencyOption: String
    ): String
}