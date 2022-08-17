package com.example.cryptomarket.presentation.exchanges

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.cryptomarket.domain.model.Exchange
import com.example.cryptomarket.domain.repository.CryptoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ExchangesViewModel @Inject constructor(
    private val cryptoRepository: CryptoRepository
) : ViewModel() {
    private val _state = mutableStateOf(ExchangesState())
    val state: State<ExchangesState> = _state

    val exchangesData: Flow<PagingData<Exchange>> = cryptoRepository.getExchangesData(
        itemsPerPage = 100,
        initialPageNumber = 1,
    ).cachedIn(viewModelScope)

    fun onEvent(event: ExchangesEvent) {
        when (event) {
            is ExchangesEvent.LoadingExchanges -> {
                _state.value = _state.value.copy(
                    isLoading = event.loading,
                )
            }
            is ExchangesEvent.OnRefresh -> {
                _state.value = _state.value.copy(
                    refresh = event.refresh,
                    error = null
                )
            }
            is ExchangesEvent.OnError -> {
                _state.value = _state.value.copy(
                    error = event.error
                )
            }
        }
    }
}