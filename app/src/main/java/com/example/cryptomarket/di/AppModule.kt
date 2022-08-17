package com.example.cryptomarket.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.cryptomarket.data.local.CryptoDao
import com.example.cryptomarket.data.local.CryptoDatabase
import com.example.cryptomarket.domain.currency_format.PriceCurrencyFormat
import com.example.cryptomarket.domain.currency_format.PriceCurrencyFormatImpl
import com.example.cryptomarket.util.FILTER_OPTIONS_SHARED_PREFERENCES
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideCryptoDatabase(
        @ApplicationContext context: Context
    ): CryptoDatabase {
        return Room.databaseBuilder(
            context, CryptoDatabase::class.java, "crypto-database"
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideCryptoDao(cryptoDatabase: CryptoDatabase): CryptoDao {
        return cryptoDatabase.cryptoDao()
    }

    @Singleton
    @Named(FILTER_OPTIONS_SHARED_PREFERENCES)
    @Provides
    fun provideFilterOptionsSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("filter_options_shared_pref", Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun priceCurrencyFormat(): PriceCurrencyFormat {
        return PriceCurrencyFormatImpl()
    }
}