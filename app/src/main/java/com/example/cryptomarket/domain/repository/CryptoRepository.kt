package com.example.cryptomarket.domain.repository

import androidx.paging.PagingData
import com.example.cryptomarket.domain.model.Coin
import com.example.cryptomarket.domain.model.CoinDetails
import com.example.cryptomarket.domain.model.CoinOhlc
import com.example.cryptomarket.domain.model.Exchange
import com.example.cryptomarket.util.Result
import kotlinx.coroutines.flow.Flow

interface CryptoRepository {

    suspend fun searchCoins(
        query: String
    ): Flow<Result<List<Coin>>>

    suspend fun removeCoinFromWatchlist(coinId: String): Result<Unit>
    suspend fun insertCoinInWatchlist(coin: Coin): Result<Unit>
    fun isCoinInWatchlist(coinId: String): Flow<Boolean>
    fun getAllWatchlistCoinsIds(): Flow<List<String>>
    suspend fun getCoinChartData(
        coinId: String,
        currency: String,
        days: String
    ): Flow<Result<List<CoinOhlc>>>

    suspend fun getCoinDetails(coinId: String): Flow<Result<CoinDetails>>
    fun getCoinsPriceData(
        itemsPerPage: Int,
        initialPageNumber: Int,
        order: String,
        currency: String,
        priceChange: String
    ): Flow<PagingData<Coin>>

    fun getWatchlistData(
        itemsPerPage: Int,
        initialPageNumber: Int,
        order: String,
        currency: String,
        priceChange: String
    ): Flow<PagingData<Coin>>

    fun getExchangesData(
        itemsPerPage: Int,
        initialPageNumber: Int
    ): Flow<PagingData<Exchange>>
}