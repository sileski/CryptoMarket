package com.example.cryptomarket.domain.currency_format

import com.example.cryptomarket.util.CoinsFilterOptionCurrencyKeys
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class PriceCurrencyFormatImpl : PriceCurrencyFormat {

    override fun formatCurrency(
        currencyFormat: NumberFormat,
        cryptoFormat: DecimalFormat,
        price: Double,
        selectedCurrencyOption: String
    ): String {
        return when (selectedCurrencyOption) {
            CoinsFilterOptionCurrencyKeys.USD -> {
                currencyFormat.currency = Currency.getInstance("USD")
                currencyFormat.format(price)
            }
            CoinsFilterOptionCurrencyKeys.EUR -> {
                currencyFormat.currency = Currency.getInstance("EUR")
                currencyFormat.format(price)
            }
            CoinsFilterOptionCurrencyKeys.BTC -> {
                "${cryptoFormat.format(price)}BTC"
            }
            CoinsFilterOptionCurrencyKeys.GBP -> {
                currencyFormat.currency = Currency.getInstance("GBP")
                currencyFormat.format(price)
            }
            CoinsFilterOptionCurrencyKeys.CAD -> {
                currencyFormat.currency = Currency.getInstance("CAD")
                currencyFormat.format(price)
            }
            CoinsFilterOptionCurrencyKeys.JPY -> {
                currencyFormat.currency = Currency.getInstance("JPY")
                currencyFormat.format(price)
            }
            CoinsFilterOptionCurrencyKeys.ETH -> {
                "${cryptoFormat.format(price)}ETH"
            }
            else -> {
                price.toString()
            }
        }
    }
}