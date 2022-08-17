package com.example.cryptomarket.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.cryptomarket.data.mappter.toExchange
import com.example.cryptomarket.data.remote.CoinGeckoApi
import com.example.cryptomarket.domain.model.Exchange
import javax.inject.Inject

class ExchangesSource @Inject constructor(
    private val api: CoinGeckoApi,
    private val initialPageNumber: Int,
    private val itemsPerPage: Int,
) : PagingSource<Int, Exchange>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Exchange> {
        return try {
            val page: Int = (params.key ?: initialPageNumber)

            val response = api.getExchanges(
                page = page,
                itemsPerPage = itemsPerPage
            )

            LoadResult.Page(
                data = response.map { it.toExchange() },
                prevKey = if (page == initialPageNumber) null else page.minus(1),
                nextKey = if (response.isEmpty()) null else page.plus(1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Exchange>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}