package com.capstone.aiyam.di

import com.capstone.aiyam.data.repository.AuthenticationRepositoryImpl
import com.capstone.aiyam.data.repository.UserRepositoryImpl
import com.capstone.aiyam.domain.repository.AuthenticationRepository
import com.capstone.aiyam.domain.repository.UserRepository
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
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ) : UserRepository
}
