package com.capstone.aiyam.di

import android.app.Application
import androidx.credentials.CredentialManager
import com.capstone.aiyam.BuildConfig
import com.capstone.aiyam.data.remote.AlertService
import com.capstone.aiyam.data.remote.ChickenService
import com.capstone.aiyam.data.remote.DashboardService
import com.capstone.aiyam.data.remote.FarmerService
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }

    @Provides
    @Singleton
    fun provideCredentialManager(
        context: Application
    ): CredentialManager {
        return CredentialManager.create(context)
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthService(
        retrofit: Retrofit
    ) : ChickenService {
        return retrofit.create(ChickenService::class.java)
    }

    @Provides
    @Singleton
    fun provideAlertService(
        retrofit: Retrofit
    ) : AlertService {
        return retrofit.create(AlertService::class.java)
    }

    @Provides
    @Singleton
    fun provideDashboardService(
        retrofit: Retrofit
    ): DashboardService {
        return retrofit.create(DashboardService::class.java)
    }

    @Provides
    @Singleton
    fun provideFarmerService(
        retrofit: Retrofit
    ): FarmerService {
        return retrofit.create(FarmerService::class.java)
    }
}
