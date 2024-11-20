package com.capstone.aiyam.data.repository

import com.capstone.aiyam.domain.model.AuthorizationResponse
import com.capstone.aiyam.domain.repository.AuthorizationRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthorizationRepositoryImpl @Inject constructor (
    private val auth: FirebaseAuth
) : AuthorizationRepository {

    override fun getUser(): Flow<AuthorizationResponse> = flow {
        emit(AuthorizationResponse.Loading)
        auth.currentUser?.let {
            user -> emit(AuthorizationResponse.Success(user))
        } ?: run {
            emit(AuthorizationResponse.Error("User not found"))
        }
    }
}
