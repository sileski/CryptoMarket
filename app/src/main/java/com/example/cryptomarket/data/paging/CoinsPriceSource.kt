package com.example.cryptomarket.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.cryptomarket.data.mappter.toCoin
import com.example.cryptomarket.data.remote.CoinGeckoApi
import com.example.cryptomarket.domain.model.Coin

class CoinsPriceSource(
    private val api: CoinGeckoApi,
    private val currency: String,
    private val order: String,
    private val initialPageNumber: Int,
    private val itemsPerPage: Int,
    private val priceChange: String,
) : PagingSource<Int, Coin>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Coin> {
        return try {
            val page: Int = (params.key ?: initialPageNumber)

            val response = api.getCoinsPrice(
                currency = currency,
                order = order,
                coinsIds = "",
                page = page,
                itemsPerPage = itemsPerPage,
                priceChange = priceChange
            )

            LoadResult.Page(
                data = response.map { it.toCoin() },
                prevKey = if (page == initialPageNumber) null else page.minus(1),
                nextKey = if (response.isEmpty()) null else page.plus(1)
            )
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