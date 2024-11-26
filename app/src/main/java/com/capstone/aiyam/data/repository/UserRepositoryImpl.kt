package com.capstone.aiyam.data.repository

import com.capstone.aiyam.domain.model.AuthorizationResponse
import com.capstone.aiyam.domain.model.TokenResponse
import com.capstone.aiyam.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor (
    private val auth: FirebaseAuth
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
        emit(TokenResponse.Loading)
        user.getIdToken(true).await().token?.let {
            emit(TokenResponse.Success(it))
        } ?: run {
            emit(TokenResponse.Failed)
        }
    }
}
