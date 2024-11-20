package com.capstone.aiyam.di

import com.capstone.aiyam.data.repository.AuthenticationRepositoryImpl
import com.capstone.aiyam.data.repository.AuthorizationRepositoryImpl
import com.capstone.aiyam.domain.repository.AuthenticationRepository
import com.capstone.aiyam.domain.repository.AuthorizationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthenticationRepository(
        authenticationRepositoryImpl: AuthenticationRepositoryImpl
    ) : AuthenticationRepository

    @Binds
    @Singleton
    abstract fun bindAuthorizationRepository(
        authorizationRepositoryImpl: AuthorizationRepositoryImpl
    ) : AuthorizationRepository
}