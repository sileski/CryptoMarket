package com.example.cryptomarket.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.cryptomarket.data.local.CryptoDao
import com.example.cryptomarket.data.mappter.toCoin
import com.example.cryptomarket.data.mappter.toCoinDetails
import com.example.cryptomarket.data.mappter.toCoinEntity
import com.example.cryptomarket.data.mappter.toWatchlistEntity
import com.example.cryptomarket.data.paging.CoinsPriceSource
import com.example.cryptomarket.data.paging.ExchangesSource
import com.example.cryptomarket.data.paging.WatchlistCoinsSource
import com.example.cryptomarket.data.remote.CoinGeckoApi
import com.example.cryptomarket.domain.model.Coin
import com.example.cryptomarket.domain.model.CoinDetails
import com.example.cryptomarket.domain.model.CoinOhlc
import com.example.cryptomarket.domain.model.Exchange
import com.example.cryptomarket.domain.repository.CryptoRepository
import com.example.cryptomarket.util.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class CryptoRepositoryImpl @Inject constructor(
    private val api: CoinGeckoApi,
    private val dao: CryptoDao,
) : CryptoRepository {

    private var fetchCoinsFromRemote = true

    override fun getCoinsPriceData(
        itemsPerPage: Int,
        initialPageNumber: Int,
        order: String,
        currency: String,
        priceChange: String
    ): Flow<PagingData<Coin>> {
        return Pager(
            config = PagingConfig(
                pageSize = 1,
                enablePlaceholders = true,
            ),
            pagingSourceFactory = {
                CoinsPriceSource(
                    api = api,
                    itemsPerPage = itemsPerPage,
                    initialPageNumber = initialPageNumber,
                    order = order,
                    currency = currency,
                    priceChange = priceChange
                )
            }
        ).flow
    }

    override fun getWatchlistData(
        itemsPerPage: Int,
        initialPageNumber: Int,
        order: String,
        currency: String,
        priceChange: String
    ): Flow<PagingData<Coin>> {
        return Pager(
            config = PagingConfig(
                pageSize = 1,
                enablePlaceholders = true,
            ),
            pagingSourceFactory = {
                WatchlistCoinsSource(
                    api = api,
                    dao = dao,
                    itemsPerPage = itemsPerPage,
                    initialPageNumber = initialPageNumber,
                    order = order,
                    currency = currency,
                    priceChange = priceChange
                )
            }
        ).flow
    }

    override fun getExchangesData(
        itemsPerPage: Int,
        initialPageNumber: Int
    ): Flow<PagingData<Exchange>> {
        return Pager(
            config = PagingConfig(
                pageSize = 1,
                enablePlaceholders = true,
            ),
            pagingSourceFactory = {
                ExchangesSource(
                    api = api,
                    itemsPerPage = itemsPerPage,
                    initialPageNumber = initialPageNumber,
                )
            }
        ).flow
    }

    override suspend fun getCoinDetails(coinId: String): Flow<Result<CoinDetails>> {
        return flow {
            try {
                emit(Result.Loading())
                while (true) {
                    val coinDetails = api.getCoinDetails(id = coinId)
                    emit(Result.Success(data = coinDetails.toCoinDetails()))
                    delay(1 * 60000)
                }
            } catch (e: Exception) {
                emit(Result.Failure(error = e.message))
            }
        }
    }

    override suspend fun removeCoinFromWatchlist(coinId: String): Result<Unit> {
        return try {
            dao.deleteWatchlistCoin(coinId = coinId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(error = e.message)
        }
    }

    override suspend fun insertCoinInWatchlist(coin: Coin): Result<Unit> {
        return try {
            dao.insertCoinInWatchlist(coin.toWatchlistEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(error = e.message)
        }
    }

    override suspend fun searchCoins(query: String): Flow<Result<List<Coin>>> {
        return flow {
            emit(Result.Loading())
            try {
                if (fetchCoinsFromRemote) {
                    dao.clearCoins()
                    val allCoins = api.getAllCoins()
                    if (allCoins.isNotEmpty()) {
                        fetchCoinsFromRemote = false
                        dao.insertCoins(allCoins.map { it.toCoinEntity() })
                    }
                }
                val searchCoins = dao.searchCoins(query = query)
                if (searchCoins.isNotEmpty()) {
                    emit(Result.Success(data = searchCoins.map { it.toCoin() }))
                } else {
                    emit(Result.Failure(error = "No search results found"))
                }
            } catch (e: Exception) {
                emit(Result.Failure(e.message))
            }
        }
    }

    override suspend fun getCoinChartData(
        coinId: String,
        currency: String,
        days: String
    ): Flow<Result<List<CoinOhlc>>> {
        return flow {
            emit(Result.Loading())
            try {
                val data = api.getCoinChartData(id = coinId, currency = currency, days = days)
                if (data.isNotEmpty()) {
                    val list = data.asSequence().map {
                        val date = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(it[0].toLong()),
                            ZoneId.systemDefault()
                        )
                        CoinOhlc(
                            date = date,
                            open = it[1],
                            high = it[2],
                            low = it[3],
                            close = it[4]
                        )
                    }.toList()
                    emit(Result.Success(data = list))
                } else {
                    emit(Result.Failure(error = "Empty data"))
                }
            } catch (e: Exception) {
                emit(Result.Failure(error = e.message))
            }
        }
    }

    override fun getAllWatchlistCoinsIds(): Flow<List<String>> {
        return dao.getWatchlistCoinsIds()
    }

    override fun isCoinInWatchlist(coinId: String): Flow<Boolean> {
        return dao.isCoinInWatchlist(coinId = coinId)
    }
}