package com.example.cryptomarket.di

import com.example.cryptomarket.data.repository.CryptoRepositoryImpl
import com.example.cryptomarket.data.repository.FilterOptionsRepositoryImpl
import com.example.cryptomarket.domain.repository.CryptoRepository
import com.example.cryptomarket.domain.repository.FilterOptionsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindCryptoRepository(
        cryptoRepositoryImpl: CryptoRepositoryImpl
    ): CryptoRepository

    @Singleton
    @Binds
    abstract fun bindFilterOptionsRepository(
        filterOptionsRepositoryImpl: FilterOptionsRepositoryImpl
    ): FilterOptionsRepository
}
