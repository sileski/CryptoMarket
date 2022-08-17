package com.example.cryptomarket.data.repository

import android.content.SharedPreferences
import com.example.cryptomarket.domain.repository.FilterOptionsRepository
import com.example.cryptomarket.util.*
import javax.inject.Inject
import javax.inject.Named

class FilterOptionsRepositoryImpl @Inject constructor(
    @Named(FILTER_OPTIONS_SHARED_PREFERENCES)
    private val sharedPreferences: SharedPreferences
) : FilterOptionsRepository {
    override fun saveSelectedCurrencyOption(currency: String) {
        sharedPreferences.edit()
            .putString(CoinsFilterOptionsSharedPrefsKeys.SELECTED_CURRENCY, currency)
            .apply()
    }

    override fun saveSelectedSortByOption(sortBy: String) {
        sharedPreferences.edit().putString(CoinsFilterOptionsSharedPrefsKeys.SELECTED_SORT, sortBy)
            .apply()
    }

    override fun saveSelectedOrderByOption(orderBy: String) {
        sharedPreferences.edit()
            .putString(CoinsFilterOptionsSharedPrefsKeys.SELECTED_ORDER, orderBy)
            .apply()
    }

    override fun saveSelectedPricePercentageChangeOption(pricePercentageChange: String) {
        sharedPreferences.edit().putString(
            CoinsFilterOptionsSharedPrefsKeys.SELECTED_PRICE_PERCENTAGE_CHANGE,
            pricePercentageChange
        ).apply()
    }

    override fun getSelectedCurrencyOption(): String {
        return sharedPreferences.getString(
            CoinsFilterOptionsSharedPrefsKeys.SELECTED_CURRENCY,
            CoinsFilterOptionCurrencyKeys.USD
        ).toString()
    }

    override fun getSelectedSortByOption(): String {
        return sharedPreferences.getString(
            CoinsFilterOptionsSharedPrefsKeys.SELECTED_SORT,
            CoinsFilterOptionSortKeys.MARKET_CAP
        ).toString()
    }

    override fun getSelectedOrderByOption(): String {
        return sharedPreferences.getString(
            CoinsFilterOptionsSharedPrefsKeys.SELECTED_ORDER,
            CoinsFilterOptionOrderingKeys.DESCENDING
        ).toString()
    }

    override fun getSelectedPricePercentageChangeOption(): String {
        return sharedPreferences.getString(
            CoinsFilterOptionsSharedPrefsKeys.SELECTED_PRICE_PERCENTAGE_CHANGE,
            CoinsFilterOptionPricePercentageChangeKeys.CHANGE_1_HOUR
        ).toString()
    }

    override fun getCurrencyOptions(): Map<String, String> {
        return mapOf(
            CoinsFilterOptionCurrencyKeys.USD to CoinsFilterOptionCurrencyValues.USD,
            CoinsFilterOptionCurrencyKeys.EUR to CoinsFilterOptionCurrencyValues.EUR,
            CoinsFilterOptionCurrencyKeys.BTC to CoinsFilterOptionCurrencyValues.BTC,
            CoinsFilterOptionCurrencyKeys.GBP to CoinsFilterOptionCurrencyValues.GBP,
            CoinsFilterOptionCurrencyKeys.CAD to CoinsFilterOptionCurrencyValues.CAD,
            CoinsFilterOptionCurrencyKeys.JPY to CoinsFilterOptionCurrencyValues.JPY,
            CoinsFilterOptionCurrencyKeys.ETH to CoinsFilterOptionCurrencyValues.ETH
        )
    }

    override fun getSortingOptions(): Map<String, String> {
        return mapOf(
            CoinsFilterOptionSortKeys.MARKET_CAP to CoinsFilterOptionSortValues.MARKET_CAP,
            CoinsFilterOptionSortKeys.VOLUME to CoinsFilterOptionSortValues.VOLUME,
            CoinsFilterOptionSortKeys.ID to CoinsFilterOptionSortValues.ID
        )
    }

    override fun getOrderingOptions(): Map<String, String> {
        return mapOf(
            CoinsFilterOptionOrderingKeys.ASCENDING to CoinsFilterOptionOrderingValues.ASCENDING,
            CoinsFilterOptionOrderingKeys.DESCENDING to CoinsFilterOptionOrderingValues.DESCENDING,
        )
    }

    override fun getPricePercentageChangeOptions(): Map<String, String> {
        return mapOf(
            CoinsFilterOptionPricePercentageChangeKeys.CHANGE_1_HOUR to CoinsFilterOptionPricePercentageChangeValues.CHANGE_1_HOUR,
            CoinsFilterOptionPricePercentageChangeKeys.CHANGE_24_HOURS to CoinsFilterOptionPricePercentageChangeValues.CHANGE_24_HOURS,
            CoinsFilterOptionPricePercentageChangeKeys.CHANGE_7_DAYS to CoinsFilterOptionPricePercentageChangeValues.CHANGE_7_DAYS,
            CoinsFilterOptionPricePercentageChangeKeys.CHANGE_30_DAYS to CoinsFilterOptionPricePercentageChangeValues.CHANGE_30_DAYS,
            CoinsFilterOptionPricePercentageChangeKeys.CHANGE_1_YEAR to CoinsFilterOptionPricePercentageChangeValues.CHANGE_1_YEAR,
        )
    }

    override fun getChartDaysFilterOptions(): Map<String, String> {
        return mapOf(
            ChartDaysFilterFilterOptionKeys.HOURS_24 to ChartDaysFilterFilterOptionValues.HOURS_24,
            ChartDaysFilterFilterOptionKeys.DAYS_7 to ChartDaysFilterFilterOptionValues.DAYS_7,
            ChartDaysFilterFilterOptionKeys.DAYS_14 to ChartDaysFilterFilterOptionValues.DAYS_14,
            ChartDaysFilterFilterOptionKeys.MONTH_1 to ChartDaysFilterFilterOptionValues.MONTH_1,
            ChartDaysFilterFilterOptionKeys.MONTH_3 to ChartDaysFilterFilterOptionValues.MONTH_3,
            ChartDaysFilterFilterOptionKeys.MONTH_6 to ChartDaysFilterFilterOptionValues.MONTH_6,
            ChartDaysFilterFilterOptionKeys.YEAR_1 to ChartDaysFilterFilterOptionValues.YEAR_1,
            ChartDaysFilterFilterOptionKeys.MAX to ChartDaysFilterFilterOptionValues.MAX,
        )
    }
}