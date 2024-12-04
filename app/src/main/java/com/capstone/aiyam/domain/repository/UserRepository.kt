package com.capstone.aiyam.domain.repository

import com.capstone.aiyam.data.dto.GoogleRequest
import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.data.dto.SuspendWrapper
import com.capstone.aiyam.domain.model.AuthorizationResponse
import com.capstone.aiyam.domain.model.Farmer
import com.capstone.aiyam.domain.model.TargetAlerts
import com.capstone.aiyam.domain.model.TokenResponse
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getFirebaseUser(): AuthorizationResponse
    fun firebaseSignOut()
    fun getFirebaseToken(user: FirebaseUser): Flow<TokenResponse>
    fun getPushToken(): Flow<TokenResponse>

    fun updateFarmer(name: String): Flow<ResponseWrapper<Farmer>>
    fun getTargetAlerts(): Flow<ResponseWrapper<TargetAlerts>>
    fun createTargetAlerts(phoneNumber: String?): Flow<ResponseWrapper<TargetAlerts>>
    fun updateEmailAlerts(email: String?): Flow<ResponseWrapper<TargetAlerts>>
    fun updateNumberAlerts(number: String?): Flow<ResponseWrapper<TargetAlerts>>
}
