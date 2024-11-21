package com.capstone.aiyam.data.repository

import com.capstone.aiyam.domain.model.AuthorizationResponse
import com.capstone.aiyam.domain.repository.AuthorizationRepository
import com.google.firebase.auth.FirebaseAuth
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
}
