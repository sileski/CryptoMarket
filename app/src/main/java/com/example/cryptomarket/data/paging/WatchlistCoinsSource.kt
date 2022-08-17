package com.example.cryptomarket.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.cryptomarket.data.local.CryptoDao
import com.example.cryptomarket.data.mappter.toCoin
import com.example.cryptomarket.data.remote.CoinGeckoApi
import com.example.cryptomarket.domain.model.Coin

class WatchlistCoinsSource(
    private val api: CoinGeckoApi,
    private val dao: CryptoDao,
    private val currency: String,
    private val order: String,
    private val initialPageNumber: Int,
    private val itemsPerPage: Int,
    private val priceChange: String
) : PagingSource<Int, Coin>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Coin> {
        return try {
            val page: Int = (params.key ?: initialPageNumber)
            val coins = dao.getWatchlistCoins()
            val coinsIds = coins.joinToString(separator = ",") { it.coin_id }

            if (coinsIds.isNotEmpty()) {
                val response = api.getCoinsPrice(
                    currency = currency,
                    coinsIds = coinsIds,
                    order = order,
                    page = page,
                    itemsPerPage = itemsPerPage,
                    priceChange = priceChange
                )
                LoadResult.Page(
                    data = response.map { it.toCoin() },
                    prevKey = if (page == initialPageNumber) null else page.minus(1),
                    nextKey = if (response.isEmpty()) null else page.plus(1)
                )
            } else {
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Coin>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}