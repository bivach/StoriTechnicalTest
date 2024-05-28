package com.storitechnicaltest.core.di

import com.storitechnicaltest.core.data.FirebaseDataSource
import com.storitechnicaltest.core.data.FirebaseDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindFirebaseDataSource(
        dataSource: FirebaseDataSourceImpl
    ): FirebaseDataSource
}
