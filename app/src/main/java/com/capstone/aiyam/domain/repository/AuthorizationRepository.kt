package com.capstone.aiyam.domain.repository

import com.capstone.aiyam.domain.model.AuthorizationResponse
import com.capstone.aiyam.domain.model.TokenResponse
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthorizationRepository {
    fun getUser(): AuthorizationResponse
    fun signOut()
    fun getToken(user: FirebaseUser): Flow<TokenResponse>
}
