package com.capstone.aiyam.data.repository

import com.capstone.aiyam.domain.model.AuthorizationResponse
import com.capstone.aiyam.domain.model.TokenResponse
import com.capstone.aiyam.domain.repository.AuthorizationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthorizationRepositoryImpl @Inject constructor (
    private val auth: FirebaseAuth
) : AuthorizationRepository {

    override fun getUser(): AuthorizationResponse {
        return auth.currentUser?.let { user ->
            AuthorizationResponse.Success(user)
        } ?: run {
            AuthorizationResponse.Error("User not found")
        }
    }

    override fun signOut() = auth.signOut()

    override fun getToken(user: FirebaseUser): Flow<TokenResponse> = flow {
        emit(TokenResponse.Loading)
        user.getIdToken(true).await().token?.let {
            emit(TokenResponse.Success(it))
        } ?: run {
            emit(TokenResponse.Failed)
        }
    }
}
