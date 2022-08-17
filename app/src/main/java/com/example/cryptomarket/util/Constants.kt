package com.example.cryptomarket.util

const val FILTER_OPTIONS_SHARED_PREFERENCES = "FILTER_OPTIONS_SHARED_PREFS"
const val CHANGES_IN_WATCHLIST = "changes_in_watchlist"

object CoinsFilterOptionsSharedPrefsKeys {
    const val SELECTED_CURRENCY = "selected_currency"
    const val SELECTED_SORT = "selected_sort"
    const val SELECTED_ORDER = "selected_order"
    const val SELECTED_PRICE_PERCENTAGE_CHANGE = "selected_price_percentage_change"
}

object CoinsFilterOptionSortKeys {
    const val MARKET_CAP = "market_cap"
    const val VOLUME = "volume"
    const val ID = "id"
}

object CoinsFilterOptionSortValues {
    const val MARKET_CAP = "Market Cap"
    const val VOLUME = "Volume"
    const val ID = "Coin ID"
}

object CoinsFilterOptionOrderingKeys {
    const val ASCENDING = "asc"
    const val DESCENDING = "desc"
}

object CoinsFilterOptionOrderingValues {
    const val ASCENDING = "Ascending"
    const val DESCENDING = "Descending"
}

object CoinsFilterOptionCurrencyKeys {
    const val USD = "usd"
    const val EUR = "eur"
    const val BTC = "btc"
    const val GBP = "gbp"
    const val CAD = "cad"
    const val JPY = "jpy"
    const val ETH = "eth"
}

object CoinsFilterOptionCurrencyValues {
    const val USD = "USD"
    const val EUR = "EUR"
    const val BTC = "BTC"
    const val GBP = "GBP"
    const val CAD = "CAD"
    const val JPY = "JPY"
    const val ETH = "ETH"
}

object CoinsFilterOptionPricePercentageChangeKeys {
    const val CHANGE_1_HOUR = "1h"
    const val CHANGE_24_HOURS = "24h"
    const val CHANGE_7_DAYS = "7d"
    const val CHANGE_30_DAYS = "30d"
    const val CHANGE_1_YEAR = "1y"
}

object CoinsFilterOptionPricePercentageChangeValues {
    const val CHANGE_1_HOUR = "1h"
    const val CHANGE_24_HOURS = "24h"
    const val CHANGE_7_DAYS = "7d"
    const val CHANGE_30_DAYS = "30d"
    const val CHANGE_1_YEAR = "1y"
}

object ChartDaysFilterFilterOptionKeys {
    const val HOURS_24 = "1"
    const val DAYS_7 = "7"
    const val DAYS_14 = "14"
    const val MONTH_1 = "30"
    const val MONTH_3 = "90"
    const val MONTH_6 = "180"
    const val YEAR_1 = "365"
    const val MAX = "max"
}

object ChartDaysFilterFilterOptionValues {
    const val HOURS_24 = "24h"
    const val DAYS_7 = "7d"
    const val DAYS_14 = "14d"
    const val MONTH_1 = "1m"
    const val MONTH_3 = "3m"
    const val MONTH_6 = "6m"
    const val YEAR_1 = "1y"
    const val MAX = "All"
}
