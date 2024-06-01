package com.storitechnicaltest.core.di

import com.storitechnicaltest.core.data.AuthRepositoryImpl
import com.storitechnicaltest.core.data.TransactionsRepositoryImpl
import com.storitechnicaltest.core.domain.AuthRepository
import com.storitechnicaltest.core.domain.TransactionsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        repository: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTransactionsRepository(
        repository: TransactionsRepositoryImpl
    ): TransactionsRepository
}
