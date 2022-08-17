package com.example.cryptomarket.presentation.cryptocurrency_details


import android.text.Spanned
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptomarket.domain.currency_format.PriceCurrencyFormat
import com.example.cryptomarket.domain.model.Coin
import com.example.cryptomarket.domain.repository.CryptoRepository
import com.example.cryptomarket.domain.repository.FilterOptionsRepository
import com.example.cryptomarket.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.NumberFormat
import javax.inject.Inject
import kotlin.reflect.full.declaredMemberProperties

@HiltViewModel
class CryptocurrencyDetailsViewModel @Inject constructor(
    private val cryptoRepository: CryptoRepository,
    private val filterOptionsRepository: FilterOptionsRepository,
    private val priceCurrencyFormat: PriceCurrencyFormat
) : ViewModel() {
    private val _state = mutableStateOf(CryptocurrencyDetailsState())
    val state: State<CryptocurrencyDetailsState> = _state

    init {
        _state.value = _state.value.copy(
            selectedCurrencyOption = filterOptionsRepository.getSelectedCurrencyOption(),
            selectedChartDaysFilterOption = getChartDaysFilterOptions().keys.first()
        )
    }

    fun onEvent(event: CryptocurrencyDetailsEvent) {
        when (event) {
            is CryptocurrencyDetailsEvent.AddRemoveCoinWatchList -> {
                addRemoveCoinWatchList(
                    coinId = event.coinId,
                    coinName = event.coinName,
                    coinSymbol = event.coinSymbol
                )
            }
            is CryptocurrencyDetailsEvent.SelectedDaysFilterOption -> {
                selectedDaysChartFilterOption(
                    daysFilterOption = event.daysFilterOption,
                    coinId = event.coinId
                )
            }
        }
    }

    fun getCoinDetails(coinId: String) {
        viewModelScope.launch {
            cryptoRepository.getCoinDetails(coinId = coinId).collect {
                when (it) {
                    is Result.Failure -> {
                        _state.value = _state.value.copy(
                            isLoadingDetails = false,
                            errorDetails = it.error
                        )
                    }
                    is Result.Loading -> {
                        _state.value = _state.value.copy(
                            isLoadingDetails = true,
                            errorDetails = null
                        )
                    }
                    is Result.Success -> {
                        _state.value = _state.value.copy(
                            isLoadingDetails = false,
                            coinDetails = it.data,
                        )
                    }
                }
            }
        }
    }

    fun getChartData(coinId: String) {
        viewModelScope.launch {
            cryptoRepository.getCoinChartData(
                coinId = coinId,
                currency = _state.value.selectedCurrencyOption,
                days = _state.value.selectedChartDaysFilterOption
            ).collect { it ->
                when (it) {
                    is Result.Failure -> {
                        _state.value = _state.value.copy(
                            isLoadingChart = false,
                            errorChart = it.error
                        )
                    }
                    is Result.Loading -> {
                        _state.value = _state.value.copy(
                            isLoadingChart = true,
                            errorChart = null
                        )
                    }
                    is Result.Success -> {
                        it.data?.let {
                            _state.value = _state.value.copy(
                                chartData = it,
                                isLoadingChart = false
                            )
                            calculatePricePercentageDifference()
                        }
                    }
                }
            }
        }
    }

    fun checkIfCoinIsInWatchlist(coinId: String) {
        viewModelScope.launch {
            cryptoRepository.isCoinInWatchlist(coinId = coinId).collect {
                _state.value = _state.value.copy(
                    isCoinInWatchlist = it
                )
                if (_state.value.initialIsCoinInWatchlist == null) {
                    _state.value = _state.value.copy(
                        initialIsCoinInWatchlist = it
                    )
                }
            }
        }
    }

    private fun addRemoveCoinWatchList(coinId: String, coinName: String, coinSymbol: String) {
        viewModelScope.launch {
            if (!_state.value.isCoinInWatchlist) {
                val coin = Coin(id = coinId, name = coinName, symbol = coinSymbol)
                cryptoRepository.insertCoinInWatchlist(coin)
            } else {
                cryptoRepository.removeCoinFromWatchlist(coinId)
            }
        }
    }

    private fun selectedDaysChartFilterOption(
        daysFilterOption: String,
        coinId: String
    ) {
        _state.value = _state.value.copy(
            chartData = null,
            selectedChartDaysFilterOption = daysFilterOption,
            priceDifference = 0.0
        )
        getChartData(coinId = coinId)
    }

    fun getCoinPrice(): String {
        val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance()
        val cryptoFormat = DecimalFormat("#.########")
        currencyFormat.minimumFractionDigits = 2
        currencyFormat.maximumFractionDigits = 9
        cryptoFormat.minimumFractionDigits = 2
        _state.value.coinDetails?.marketData?.currentPrice?.let { it ->
            it::class.declaredMemberProperties.forEach { field ->
                if (field.name == _state.value.selectedCurrencyOption) {
                    val price = field.getter.call(it).toString().toDouble()
                    return priceCurrencyFormat.formatCurrency(
                        currencyFormat = currencyFormat,
                        cryptoFormat = cryptoFormat,
                        price = price,
                        selectedCurrencyOption = _state.value.selectedCurrencyOption
                    )
                }
            }
        }
        return (0.0).toString()
    }

    fun getCurrencyFormat(price: Double): String {
        val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance()
        val cryptoFormat = DecimalFormat("#.########")
        currencyFormat.minimumFractionDigits = 2
        if (price >= 1000) {
            currencyFormat.maximumFractionDigits = 2
        } else if (price >= 100) {
            currencyFormat.maximumFractionDigits = 3
        } else if (price >= 1) {
            currencyFormat.maximumFractionDigits = 4
        } else {
            currencyFormat.maximumFractionDigits = 9
        }
        cryptoFormat.minimumFractionDigits = 2
        return priceCurrencyFormat.formatCurrency(
            currencyFormat = currencyFormat,
            cryptoFormat = cryptoFormat,
            price = price,
            selectedCurrencyOption = _state.value.selectedCurrencyOption
        )
    }


    private fun calculatePricePercentageDifference() {
        val chartData = _state.value.chartData
        chartData?.let {
            val difference = chartData[chartData.size - 1].close - chartData[0].open
            val percentage = (difference / chartData[0].open) * 100
            _state.value = _state.value.copy(
                priceDifference = String.format("%.2f", percentage).toDouble()
            )
        }
    }

    fun convertFromHtmlToText(text: String): Spanned? {
        val convertedText = text.replace("\n", "<br>")
        return HtmlCompat.fromHtml(convertedText, HtmlCompat.FROM_HTML_MODE_COMPACT)
    }

    fun getChartDaysFilterOptions(): Map<String, String> {
        return filterOptionsRepository.getChartDaysFilterOptions()
    }
}