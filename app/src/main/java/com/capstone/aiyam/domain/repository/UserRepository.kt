package com.capstone.aiyam.domain.repository

import com.capstone.aiyam.data.dto.GoogleRequest
import com.capstone.aiyam.data.dto.ResponseWrapper
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
    fun createFarmer(uid: String, name: String, email: String): Flow<ResponseWrapper<Farmer>>
    fun googleLogin(): Flow<ResponseWrapper<GoogleRequest>>
    fun updateFarmer(name: String): Flow<ResponseWrapper<Farmer>>
    fun getTargetAlerts(): Flow<ResponseWrapper<TargetAlerts>>
    fun createTargetAlerts(phoneNumber: String, email: String): Flow<ResponseWrapper<TargetAlerts>>
    fun updateTargetAlerts(phoneNumber: String?, email: String?): Flow<ResponseWrapper<TargetAlerts>>
}
