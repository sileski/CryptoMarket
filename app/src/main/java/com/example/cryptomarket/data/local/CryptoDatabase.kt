package com.example.cryptomarket.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CoinEntity::class, WatchlistEntity::class], version = 2)
abstract class CryptoDatabase : RoomDatabase() {
    abstract fun cryptoDao(): CryptoDao
}