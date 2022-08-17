package com.example.cryptomarket.domain.repository

interface FilterOptionsRepository {

    fun saveSelectedCurrencyOption(currency: String)
    fun saveSelectedSortByOption(sortBy: String)
    fun saveSelectedOrderByOption(orderBy: String)
    fun saveSelectedPricePercentageChangeOption(pricePercentageChange: String)
    fun getSelectedCurrencyOption(): String
    fun getSelectedSortByOption(): String
    fun getSelectedOrderByOption(): String
    fun getSelectedPricePercentageChangeOption(): String
    fun getOrderingOptions(): Map<String, String>
    fun getSortingOptions(): Map<String, String>
    fun getCurrencyOptions(): Map<String, String>
    fun getPricePercentageChangeOptions(): Map<String, String>
    fun getChartDaysFilterOptions(): Map<String, String>
}