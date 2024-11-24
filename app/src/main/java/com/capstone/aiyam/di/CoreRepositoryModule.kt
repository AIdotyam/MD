package com.capstone.aiyam.di

import com.capstone.aiyam.data.repository.AlertRepositoryImpl
import com.capstone.aiyam.data.repository.ChickenRepositoryImpl
import com.capstone.aiyam.domain.repository.AlertRepository
import com.capstone.aiyam.domain.repository.ChickenRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindChickenRepository(
        chickenRepositoryImpl: ChickenRepositoryImpl
    ) : ChickenRepository

    @Binds
    @Singleton
    abstract fun bindAlertRepository(
        alertRepositoryImpl: AlertRepositoryImpl
    ) : AlertRepository
}
