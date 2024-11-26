package com.capstone.aiyam.domain.repository

import com.capstone.aiyam.domain.model.AuthorizationResponse
import com.capstone.aiyam.domain.model.TokenResponse
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getFirebaseUser(): AuthorizationResponse
    fun firebaseSignOut()
    fun getFirebaseToken(user: FirebaseUser): Flow<TokenResponse>
}
