package com.example.cryptomarket.presentation.cryptocurrency_search

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptomarket.domain.repository.CryptoRepository
import com.example.cryptomarket.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CryptocurrencySearchViewModel @Inject constructor(
    val repository: CryptoRepository
) : ViewModel() {
    private val _state = mutableStateOf(CryptoCurrencySearchState())
    val state: State<CryptoCurrencySearchState> = _state

    private var searchJob: Job? = null

    fun onEvent(event: CryptocurrencySearchEvent) {
        when (event) {
            is CryptocurrencySearchEvent.SearchCoins -> {
                onSearchQueryChange(query = event.query)
            }
            CryptocurrencySearchEvent.ClearSearchQuery -> {
                _state.value = _state.value.copy(
                    searchQuery = ""
                )
            }
        }
    }

    private fun onSearchQueryChange(query: String) {
        searchJob?.cancel()
        _state.value = _state.value.copy(
            searchQuery = query.lowercase()
        )
        _state.value.searchQuery.let {
            if (it.isNotEmpty()) {
                searchCoins(query = it)
            } else {
                _state.value = _state.value.copy(
                    coins = emptyList(),
                )
            }
        }
    }

    private fun searchCoins(query: String) {
        searchJob = viewModelScope.launch {
            delay(500L)
            repository.searchCoins(query).collect {
                when (it) {
                    is Result.Success -> {
                        it.data?.let {
                            _state.value = _state.value.copy(
                                coins = it,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                    is Result.Failure -> {
                        it.error?.let { error ->
                            _state.value = _state.value.copy(
                                error = error,
                                isLoading = false
                            )
                        }
                    }
                    is Result.Loading -> {
                        _state.value = _state.value.copy(
                            isLoading = true,
                            coins = emptyList(),
                            error = null
                        )
                    }
                }
            }
        }
    }
}