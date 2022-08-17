package com.example.cryptomarket.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CryptoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoins(coins: List<CoinEntity>)

    @Query("DELETE FROM coins")
    suspend fun clearCoins()

    @Query(
        """
        SELECT * 
        FROM coins 
        WHERE LOWER(:query) == symbol OR LOWER(name) LIKE LOWER(:query) || "%" 
         """
    )
    suspend fun searchCoins(query: String): List<CoinEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinInWatchlist(coin: WatchlistEntity)

    @Query("SELECT * FROM watchlist")
    suspend fun getWatchlistCoins(): List<WatchlistEntity>

    @Query("SELECT coin_id FROM watchlist")
    fun getWatchlistCoinsIds(): Flow<List<String>>

    @Query("DELETE FROM watchlist WHERE coin_id == :coinId")
    suspend fun deleteWatchlistCoin(coinId: String)

    @Query("SELECT EXISTS (SELECT * FROM watchlist WHERE coin_id == :coinId)")
    fun isCoinInWatchlist(coinId: String): Flow<Boolean>

}