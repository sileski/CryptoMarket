package com.example.cryptomarket.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchlistEntity(
    @PrimaryKey
    val id: Int? = null,
    val coin_id: String,
    val name: String,
    val symbol: String
)