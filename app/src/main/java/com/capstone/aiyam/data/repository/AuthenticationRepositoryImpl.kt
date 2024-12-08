package com.capstone.aiyam.data.repository

import android.app.Application
import android.util.Log
import androidx.core.net.toUri
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.capstone.aiyam.domain.model.AuthenticationResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import com.capstone.aiyam.BuildConfig
import com.capstone.aiyam.data.dto.CreateFarmerRequest
import com.capstone.aiyam.data.dto.GoogleRequest
import com.capstone.aiyam.data.remote.FarmerService
import com.capstone.aiyam.domain.repository.AuthenticationRepository
import com.capstone.aiyam.utils.createNonce
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val context: Application,
    private val auth: FirebaseAuth,
    private val manager: CredentialManager,
    private val farmerService: FarmerService
): AuthenticationRepository {
    override fun createAccountWithEmail(username: String, email: String, password: String): Flow<AuthenticationResponse> = flow {
        emit(AuthenticationResponse.Loading)
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
            updateUserDisplayName(username)
        } catch (e: FirebaseAuthUserCollisionException) {
            emit(AuthenticationResponse.Error("Email already in use"))
            return@flow
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            emit(AuthenticationResponse.Error("Invalid credentials"))
            return@flow
        } catch (e: CreateCredentialCancellationException) {
            emit(AuthenticationResponse.Error("Cancelled"))
            return@flow
        } catch (e: Exception) {
            emit(AuthenticationResponse.Error("Failed to create account"))
            return@flow
        }

        val user = auth.currentUser
        try {
            val firebaseIdToken = user?.getIdToken(true)?.await()?.token
                ?: throw Exception("Firebase ID Token not found")
            farmerService.createFarmer(firebaseIdToken, CreateFarmerRequest(user.uid, username, email))
            emit(AuthenticationResponse.Success)
        } catch (e: Exception) {
            try { user?.delete()?.await() } catch (de: Exception) {
                de.printStackTrace()
            }

            e.printStackTrace()
            emit(AuthenticationResponse.Error(e.message ?: ""))
            return@flow
        }
    }

    private fun updateUserDisplayName(username: String) {
        auth.currentUser?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(username).build())
    }

    override fun loginWithEmail(email: String, password: String): Flow<AuthenticationResponse> = flow {
        emit(AuthenticationResponse.Loading)
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            emit(AuthenticationResponse.Success)
        } catch (e: Exception) {
            emit(AuthenticationResponse.Error("Sign-in failed: ${e.message}"))
        }
    }

    override fun sendOtp(phoneNumber: String, verificationCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(verificationCallback)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun linkPhoneNumber(credential: PhoneAuthCredential): Flow<AuthenticationResponse> = flow {
        emit(AuthenticationResponse.Loading)
        try {
            auth.currentUser?.linkWithCredential(credential)?.await()
            emit(AuthenticationResponse.Success)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(AuthenticationResponse.Error(e.message ?: ""))
        }
    }

    override fun signInWithGoogle(): Flow<AuthenticationResponse> = flow {
        emit(AuthenticationResponse.Loading)
        val nonce = createNonce()
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.CLIENT_KEY)
            .setAutoSelectEnabled(false)
            .setNonce(nonce)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val credential = manager.getCredential(context, request)
            if (credential.credential !is CustomCredential) {
                throw Exception("Credential not found")
            }

            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.credential.data)

            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

            auth.signInWithCredential(firebaseCredential).await()
        } catch (e: GoogleIdTokenParsingException) {
            emit(AuthenticationResponse.Error(e.message ?: "Error parsing Google ID Token"))
            return@flow
        } catch (e: NoCredentialException) {
            emit(AuthenticationResponse.Error("No credentials available"))
            return@flow
        } catch (e: Exception) {
            emit(AuthenticationResponse.Error(e.message ?: "An unexpected error occurred"))
            return@flow
        }

        try {
            val firebaseIdToken = auth.currentUser?.getIdToken(true)?.await()?.token ?: throw Exception("Firebase ID Token not found")
            farmerService.loginGoogle(GoogleRequest(token = firebaseIdToken))
            emit(AuthenticationResponse.Success)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(AuthenticationResponse.Error(e.message ?: ""))
            return@flow
        }
    }
}
