package com.capstone.aiyam.data.repository

import com.capstone.aiyam.data.dto.CreateFarmerRequest
import com.capstone.aiyam.data.dto.GoogleRequest
import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.data.dto.TargetRequest
import com.capstone.aiyam.data.dto.UpdateNameRequest
import com.capstone.aiyam.data.remote.FarmerService
import com.capstone.aiyam.domain.model.AuthorizationResponse
import com.capstone.aiyam.domain.model.Farmer
import com.capstone.aiyam.domain.model.TargetAlerts
import com.capstone.aiyam.domain.model.TokenResponse
import com.capstone.aiyam.domain.repository.UserRepository
import com.capstone.aiyam.utils.withToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor (
    private val auth: FirebaseAuth,
    private val farmerService: FarmerService
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
        user.getIdToken(true).await().token?.let {
            emit(TokenResponse.Success(it))
        } ?: run {
            emit(TokenResponse.Failed)
        }
    }

    override fun createFarmer(uid: String, name: String, email: String): Flow<ResponseWrapper<Farmer>> = flow {
        emit(ResponseWrapper.Loading)
        try {
            val farmer = withToken(
                getFirebaseUser(), ::getFirebaseToken
            ) {
                farmerService.createFarmer(it, CreateFarmerRequest(uid, name, email))
            }
            emit(ResponseWrapper.Success(farmer.data))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }

    override fun googleLogin(): Flow<ResponseWrapper<GoogleRequest>> = flow {
        emit(ResponseWrapper.Loading)
        try {
            val user = withToken(
                getFirebaseUser(), ::getFirebaseToken
            ) {
                farmerService.loginGoogle(GoogleRequest(it))
            }
            emit(ResponseWrapper.Success(user.data))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }

    override fun updateFarmer(name: String): Flow<ResponseWrapper<Farmer>> = flow {
        emit(ResponseWrapper.Loading)
        try {
            val farmer = withToken(
                getFirebaseUser(), ::getFirebaseToken
            ) {
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
            val targetAlerts = withToken(
                getFirebaseUser(), ::getFirebaseToken
            ) {
                farmerService.getTargetAlerts(it)
            }
            emit(ResponseWrapper.Success(targetAlerts.data))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }

    override fun createTargetAlerts(phoneNumber: String, email: String): Flow<ResponseWrapper<TargetAlerts>> = flow {
        emit(ResponseWrapper.Loading)
        try {
            val targetAlerts = withToken(
                getFirebaseUser(), ::getFirebaseToken
            ) {
                farmerService.createTargetAlerts(it, TargetRequest(phoneNumber, email))
            }
            emit(ResponseWrapper.Success(targetAlerts.data))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }

    override fun updateTargetAlerts(phoneNumber: String?, email: String?): Flow<ResponseWrapper<TargetAlerts>> = flow {
        emit(ResponseWrapper.Loading)
        try {
            val targetAlerts = withToken(
                getFirebaseUser(), ::getFirebaseToken
            ) {
                farmerService.updateTargetAlerts(it, TargetRequest(phoneNumber, email))
            }
            emit(ResponseWrapper.Success(targetAlerts.data))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }
}
