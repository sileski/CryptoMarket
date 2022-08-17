package com.example.cryptomarket.navigation

const val DETAILS_ID_ARGUMENT_KEY = "coin_id"
const val DETAILS_NAME_ARGUMENT_KEY = "coin_name"
const val DETAILS_SYMBOL_ARGUMENT_KEY = "coin_symbol"

sealed class DetailsScreen(val route: String) {
    object Overview :
        DetailsScreen(route = "overview/{$DETAILS_ID_ARGUMENT_KEY}/{$DETAILS_NAME_ARGUMENT_KEY}/{$DETAILS_SYMBOL_ARGUMENT_KEY}") {
        fun passArguments(coinId: String, coinName: String, coinSymbol: String): String {
            return this.route
                .replace(oldValue = "{$DETAILS_ID_ARGUMENT_KEY}", newValue = coinId)
                .replace(oldValue = "{$DETAILS_NAME_ARGUMENT_KEY}", newValue = coinName)
                .replace(oldValue = "{$DETAILS_SYMBOL_ARGUMENT_KEY}", newValue = coinSymbol)
        }
    }
}