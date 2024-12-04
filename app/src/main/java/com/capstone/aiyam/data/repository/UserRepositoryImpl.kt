package com.capstone.aiyam.data.repository

import android.app.Application
import android.util.Log
import com.capstone.aiyam.data.dto.DataWrapper
import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.data.dto.TargetRequest
import com.capstone.aiyam.data.dto.UpdateNameRequest
import com.capstone.aiyam.data.remote.FarmerService
import com.capstone.aiyam.domain.model.AuthorizationResponse
import com.capstone.aiyam.domain.model.Farmer
import com.capstone.aiyam.domain.model.TargetAlerts
import com.capstone.aiyam.domain.model.TokenResponse
import com.capstone.aiyam.domain.repository.SettingsPreferencesRepository
import com.capstone.aiyam.domain.repository.UserRepository
import com.capstone.aiyam.utils.withToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor (
    private val context: Application,
    private val auth: FirebaseAuth,
    private val messaging: FirebaseMessaging,
    private val farmerService: FarmerService,
    private val settingsPreferencesRepository: SettingsPreferencesRepository
) : UserRepository {
    override fun getFirebaseUser(): AuthorizationResponse {
        return auth.currentUser?.let { user ->
            AuthorizationResponse.Success(user)
        } ?: run {
            AuthorizationResponse.Error("User not found")
        }
    }

    override fun firebaseSignOut() = auth.signOut()

    override fun getFirebaseToken(user: FirebaseUser): Flow<TokenResponse> = flow {
        user.getIdToken(false).await().token?.let {
            emit(TokenResponse.Success(it))
        } ?: run {
            emit(TokenResponse.Failed)
        }
    }

    override fun getPushToken(): Flow<TokenResponse> = flow {
        messaging.token.await()?.let {
            emit(TokenResponse.Success(it))
        } ?: run {
            emit(TokenResponse.Failed)
        }
    }

    override fun updateFarmer(name: String): Flow<ResponseWrapper<Farmer>> = flow {
        emit(ResponseWrapper.Loading)
        try {
            val farmer = withToken(getFirebaseUser(), ::getFirebaseToken) {
                farmerService.updateFarmer(it, UpdateNameRequest(name))
            }

            emit(ResponseWrapper.Success(farmer.data))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }

    override fun getTargetAlerts(): Flow<ResponseWrapper<TargetAlerts>> = flow {
        emit(ResponseWrapper.Loading)
        try {
            val targetAlerts = withToken(getFirebaseUser(), ::getFirebaseToken) {
                farmerService.getTargetAlerts(it)
            }

            targetAlerts.data.phoneNumber?.let { phone ->
                settingsPreferencesRepository.savePhoneNumberSetting(phone)
            } ?: run {
                settingsPreferencesRepository.savePhoneNumberSetting("")
            }

            targetAlerts.data.email?.let {
                settingsPreferencesRepository.saveEmailNotificationSetting(true)
            } ?: run {
                settingsPreferencesRepository.saveEmailNotificationSetting(false)
            }

            emit(ResponseWrapper.Success(targetAlerts.data))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }

    override fun createTargetAlerts(phoneNumber: String?): Flow<ResponseWrapper<TargetAlerts>> = flow {
        emit(ResponseWrapper.Loading)
        try {
            var targetAlerts: DataWrapper<TargetAlerts>? = null
            when(val user = getFirebaseUser()) {
                is AuthorizationResponse.Success -> {
                    user.user.getIdToken(false).await().token?.let {
                        targetAlerts = farmerService.createTargetAlerts(it, TargetRequest(phoneNumber, user.user.email))
                    } ?: run {
                        emit(ResponseWrapper.Error("Failed to create target alerts"))
                        return@flow
                    }
                }
                is AuthorizationResponse.Error -> {
                    emit(ResponseWrapper.Error(user.message))
                    return@flow
                }
            }

            targetAlerts?.let {
                it.data.phoneNumber?.let { phone ->
                    settingsPreferencesRepository.savePhoneNumberSetting(phone)
                } ?: run {
                    settingsPreferencesRepository.savePhoneNumberSetting("")
                }

                emit(ResponseWrapper.Success(it.data))
            } ?: run {
                emit(ResponseWrapper.Error("Failed to create target alerts"))
                return@flow
            }
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }

    override fun updateEmailAlerts(email: String?): Flow<ResponseWrapper<TargetAlerts>> = flow {
        emit(ResponseWrapper.Loading)
        try {
            val phoneNumber = settingsPreferencesRepository.getPhoneNumberSetting().first().takeIf { it.isNotEmpty() }
            val targetAlerts = withToken(getFirebaseUser(), ::getFirebaseToken) {
                farmerService.updateTargetAlerts(it, TargetRequest(phoneNumber, email))
            }

            targetAlerts.data.email?.let {
                settingsPreferencesRepository.saveEmailNotificationSetting(true)
            } ?: run {
                settingsPreferencesRepository.saveEmailNotificationSetting(false)
            }

            emit(ResponseWrapper.Success(targetAlerts.data))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }

    override fun updateNumberAlerts(number: String?): Flow<ResponseWrapper<TargetAlerts>> = flow {
        emit(ResponseWrapper.Loading)
        try {
            var targetAlerts: DataWrapper<TargetAlerts>? = null
            when(val user = getFirebaseUser()) {
                is AuthorizationResponse.Success -> {
                    user.user.getIdToken(false).await().token?.let {
                        targetAlerts = farmerService.updateTargetAlerts(it, TargetRequest(number, user.user.email))
                    } ?: run {
                        emit(ResponseWrapper.Error("Failed to create target alerts"))
                        return@flow
                    }
                }
                is AuthorizationResponse.Error -> {
                    emit(ResponseWrapper.Error(user.message))
                    return@flow
                }
            }

            targetAlerts?.let {
                it.data.phoneNumber?.let { number ->
                    settingsPreferencesRepository.savePhoneNumberSetting(number)
                } ?: run {
                    settingsPreferencesRepository.savePhoneNumberSetting("")
                }

                emit(ResponseWrapper.Success(it.data))
            } ?: run {
                emit(ResponseWrapper.Error("Failed to update phone number"))
                return@flow
            }
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }
}
