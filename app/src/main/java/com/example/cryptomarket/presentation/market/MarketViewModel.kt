package com.example.cryptomarket.presentation.market

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.cryptomarket.domain.currency_format.PriceCurrencyFormat
import com.example.cryptomarket.domain.filter_options.CoinsFilterCategories
import com.example.cryptomarket.domain.model.Coin
import com.example.cryptomarket.domain.repository.CryptoRepository
import com.example.cryptomarket.domain.repository.FilterOptionsRepository
import com.example.cryptomarket.navigation.BottomNavigationBarVisibility
import com.example.cryptomarket.util.CoinsFilterOptionPricePercentageChangeKeys
import com.example.cryptomarket.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.NumberFormat
import javax.inject.Inject

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val cryptoRepository: CryptoRepository,
    private val filterOptionsRepository: FilterOptionsRepository,
    private val priceCurrencyFormat: PriceCurrencyFormat
) : ViewModel() {
    private val _state = mutableStateOf(MarketState())
    val state: State<MarketState> = _state

    var coinsData: Flow<PagingData<Coin>>
    var watchlistData: Flow<PagingData<Coin>>

    private var order = ""

    init {
        getAllWatchlistCoins()
        _state.value = _state.value.copy(
            selectedSortOption = filterOptionsRepository.getSelectedSortByOption(),
            selectedOrderOption = filterOptionsRepository.getSelectedOrderByOption(),
            selectedCurrencyOption = filterOptionsRepository.getSelectedCurrencyOption(),
            selectedPricePercentageChangeOption = filterOptionsRepository.getSelectedPricePercentageChangeOption()
        )
        configureOrder()
        coinsData = getCoins()
        watchlistData = getWatchlist()
    }


    fun onEvent(event: MarketEvent) {
        when (event) {
            is MarketEvent.Refresh -> {
                refreshData(event.refresh)
            }
            is MarketEvent.OpenFilterOptionsMenu -> {
                BottomNavigationBarVisibility.showBottomNavigationBar.value = false
                _state.value = _state.value.copy(
                    showFilterOptionsMenu = true,
                    selectedFilterOptionCategory = event.selectedCoinsFilterCategory
                )
            }
            is MarketEvent.CloseFilterOptionsMenu -> {
                BottomNavigationBarVisibility.showBottomNavigationBar.value = true
                _state.value = _state.value.copy(
                    showFilterOptionsMenu = false
                )
            }
            is MarketEvent.FilterOptionsItemSelected -> {
                filterOptionsItemSelected(
                    selectedOption = event.option
                )
            }
            is MarketEvent.AddRemoveCoinWatchList -> {
                addRemoveCoinWatchList(coin = event.coin)
            }
            is MarketEvent.RemoveCoinFromWatchList -> {
                removeCoinFromWatchList(coin = event.coin)
            }
            is MarketEvent.PagerPageChange -> {
                onPagerPageChange(page = event.page)
            }
            is MarketEvent.LoadingCoins -> {
                _state.value = _state.value.copy(
                    isLoadingCoins = event.loading
                )
            }
            is MarketEvent.LoadingWatchlist -> {
                _state.value = _state.value.copy(
                    isLoadingWatchlist = event.loading
                )
            }
            is MarketEvent.OnError -> {
                _state.value = _state.value.copy(
                    error = event.error
                )
            }
        }
    }

    private fun configureOrder() {
        order = "${_state.value.selectedSortOption}_${_state.value.selectedOrderOption}"
    }

    private fun getCoins(): Flow<PagingData<Coin>> {
        return cryptoRepository.getCoinsPriceData(
            itemsPerPage = 50,
            initialPageNumber = 1,
            order = order,
            currency = _state.value.selectedCurrencyOption,
            priceChange = _state.value.selectedPricePercentageChangeOption
        ).cachedIn(viewModelScope)
    }

    private fun getWatchlist(): Flow<PagingData<Coin>> {
        return cryptoRepository.getWatchlistData(
            itemsPerPage = 50,
            initialPageNumber = 1,
            order = order,
            currency = _state.value.selectedCurrencyOption,
            priceChange = _state.value.selectedPricePercentageChangeOption
        ).cachedIn(viewModelScope)
    }

    private fun refreshData(refresh: Boolean) {
        when (_state.value.pagerPage) {
            0 -> {
                _state.value = _state.value.copy(
                    error = null,
                    refreshCoins = refresh,
                )
            }
            1 -> {
                _state.value = _state.value.copy(
                    error = null,
                    refreshWatchlist = refresh,
                    changesInWatchlist = false,
                )
            }
        }
    }

    private fun reloadData() {
        _state.value = _state.value.copy(
            changesInWatchlist = false,
            error = null
        )
        configureOrder()
        coinsData = getCoins()
        watchlistData = getWatchlist()
    }

    private fun filterOptionsItemSelected(
        selectedOption: String
    ) {
        when (_state.value.selectedFilterOptionCategory) {
            CoinsFilterCategories.CURRENCY -> {
                _state.value = _state.value.copy(
                    selectedCurrencyOption = selectedOption,
                    showFilterOptionsMenu = false
                )
                filterOptionsRepository.saveSelectedCurrencyOption(selectedOption)
            }
            CoinsFilterCategories.ORDER_BY -> {
                _state.value = _state.value.copy(
                    selectedOrderOption = selectedOption,
                    showFilterOptionsMenu = false
                )
                filterOptionsRepository.saveSelectedOrderByOption(selectedOption)
            }
            CoinsFilterCategories.SORT_BY -> {
                _state.value = _state.value.copy(
                    selectedSortOption = selectedOption,
                    showFilterOptionsMenu = false
                )
                filterOptionsRepository.saveSelectedSortByOption(selectedOption)
            }
            CoinsFilterCategories.PRICE_PERCENTAGE_CHANGE -> {
                _state.value = _state.value.copy(
                    selectedPricePercentageChangeOption = selectedOption,
                    showFilterOptionsMenu = false
                )
                filterOptionsRepository.saveSelectedPricePercentageChangeOption(selectedOption)
            }
        }
        BottomNavigationBarVisibility.showBottomNavigationBar.value = true
        reloadData()
    }


    private fun addRemoveCoinWatchList(coin: Coin) {
        viewModelScope.launch {
            if (!_state.value.watchlistCoinsIds.contains(coin.id)) {
                when (val result = cryptoRepository.insertCoinInWatchlist(coin)) {
                    is Result.Failure -> {
                        _state.value = _state.value.copy(
                            error = result.error
                        )
                    }
                    is Result.Loading -> Unit
                    is Result.Success -> {
                        _state.value = _state.value.copy(
                            changesInWatchlist = true
                        )
                    }
                }
            } else {
                when (val result = cryptoRepository.removeCoinFromWatchlist(coin.id)) {
                    is Result.Failure -> {
                        _state.value = _state.value.copy(
                            error = result.error
                        )
                    }
                    is Result.Loading -> Unit
                    is Result.Success -> {
                        _state.value = _state.value.copy(
                            changesInWatchlist = true
                        )
                    }
                }
            }
        }
    }

    private fun removeCoinFromWatchList(coin: Coin) {
        viewModelScope.launch {
            if (_state.value.watchlistCoinsIds.contains(coin.id)) {
                cryptoRepository.removeCoinFromWatchlist(coin.id)
            }
        }
        refreshData(refresh = true)
    }

    private fun onPagerPageChange(page: Int) {
        _state.value = _state.value.copy(
            pagerPage = page
        )
        if (_state.value.changesInWatchlist && page == 1) {
            refreshData(refresh = true)
        }
    }

    private fun getAllWatchlistCoins() {
        viewModelScope.launch {
            cryptoRepository.getAllWatchlistCoinsIds().collect {
                _state.value = _state.value.copy(
                    watchlistCoinsIds = it
                )
            }
        }
    }

    fun getCurrencyFormat(price: Double): String {
        val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance()
        val cryptoFormat = DecimalFormat("#.########")
        currencyFormat.minimumFractionDigits = 2
        currencyFormat.maximumFractionDigits = 9
        cryptoFormat.minimumFractionDigits = 2
        return priceCurrencyFormat.formatCurrency(
            currencyFormat = currencyFormat,
            cryptoFormat = cryptoFormat,
            price = price,
            selectedCurrencyOption = _state.value.selectedCurrencyOption
        )
    }

    fun getPricePercentageChange(coin: Coin): String {
        val dec = DecimalFormat("#.##")
        dec.positiveSuffix = "%"
        dec.negativeSuffix = "%"
        when (_state.value.selectedPricePercentageChangeOption) {
            CoinsFilterOptionPricePercentageChangeKeys.CHANGE_1_HOUR -> {
                return dec.format(coin.pricePercentageChange1h)
            }
            CoinsFilterOptionPricePercentageChangeKeys.CHANGE_24_HOURS -> {
                return dec.format(coin.pricePercentageChange24h)
            }
            CoinsFilterOptionPricePercentageChangeKeys.CHANGE_7_DAYS -> {
                return dec.format(coin.pricePercentageChange7d)
            }
            CoinsFilterOptionPricePercentageChangeKeys.CHANGE_30_DAYS -> {
                return dec.format(coin.pricePercentageChange30d)
            }
            CoinsFilterOptionPricePercentageChangeKeys.CHANGE_1_YEAR -> {
                return dec.format(coin.pricePercentageChange1y)
            }
        }
        return coin.pricePercentageChange1h.toString()
    }

    fun changeInWatchlist() {
        _state.value = _state.value.copy(
            changesInWatchlist = true
        )
    }

    fun getCurrencyOptions(): Map<String, String> {
        return filterOptionsRepository.getCurrencyOptions()
    }

    fun getSortingOptions(): Map<String, String> {
        return filterOptionsRepository.getSortingOptions()
    }

    fun getOrderingOptions(): Map<String, String> {
        return filterOptionsRepository.getOrderingOptions()
    }

    fun getPricePercentageChangeOptions(): Map<String, String> {
        return filterOptionsRepository.getPricePercentageChangeOptions()
    }
}