package com.example.cryptomarket.data.remote

import com.example.cryptomarket.data.remote.dto.CoinDetailsDto
import com.example.cryptomarket.data.remote.dto.CoinDto
import com.example.cryptomarket.data.remote.dto.ExchangeDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinGeckoApi {

    @GET("/api/v3/coins/list")
    suspend fun getAllCoins(): List<CoinDto>

    @GET("/api/v3/coins/markets")
    suspend fun getCoinsPrice(
        @Query("vs_currency") currency: String,
        @Query("ids") coinsIds: String = "",
        @Query("order") order: String,
        @Query("per_page") itemsPerPage: Int = 50,
        @Query("page") page: Int = 1,
        @Query("price_change_percentage") priceChange: String
    ): List<CoinDto>

    @GET("/api/v3/coins/{id}")
    suspend fun getCoinDetails(
        @Path("id") id: String
    ): CoinDetailsDto

    @GET("/api/v3/coins/{id}/ohlc")
    suspend fun getCoinChartData(
        @Path("id") id: String,
        @Query("vs_currency") currency: String,
        @Query("days") days: String
    ): List<List<Double>>

    @GET("/api/v3/exchanges")
    suspend fun getExchanges(
        @Query("per_page") itemsPerPage: Int = 50,
        @Query("page") page: Int = 1,
    ): List<ExchangeDto>

    companion object {
        const val API_URL = "https://api.coingecko.com/"
    }
}