package com.capstone.aiyam.data.repository

import android.app.Application
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
import com.capstone.aiyam.domain.repository.AuthenticationRepository
import com.capstone.aiyam.utils.createNonce
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val context: Application,
    private val auth: FirebaseAuth
): AuthenticationRepository {
    private val manager = CredentialManager.create(context)

    override fun createAccountWithEmail(username: String, email: String, password: String): Flow<AuthenticationResponse> = flow {
        emit(AuthenticationResponse.Loading)
        try {
            manager.createCredential(context, CreatePasswordRequest(email, password))

            auth.createUserWithEmailAndPassword(email, password).await()

            auth.currentUser?.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build()
            )

            emit(AuthenticationResponse.Success)
        } catch (e: CreateCredentialCancellationException) {
            e.printStackTrace()
            emit(AuthenticationResponse.Error("Cancelled"))
        } catch (e: CreateCredentialException) {
            e.printStackTrace()
            emit(AuthenticationResponse.Error("Failed"))
        }
    }

    override fun loginWithEmail(email: String, password: String): Flow<AuthenticationResponse> = flow {
        emit(AuthenticationResponse.Loading)

        try {
            val credentialResponse = manager.getCredential(context, GetCredentialRequest(credentialOptions = listOf(GetPasswordOption())))
            val credential = credentialResponse.credential as? PasswordCredential

            credential?.let {
                if (credential.id == email && credential.password == password) {
                    emit(AuthenticationResponse.Success)
                    return@flow
                }
            }
        } catch (e: GetCredentialException) {
            e.printStackTrace()
        } catch (e: NoCredentialException) {
            e.printStackTrace()
            manager.createCredential(context, CreatePasswordRequest(email, password))
        }

        try {
            auth.signInWithEmailAndPassword(email, password).await()
            emit(AuthenticationResponse.Success)
        } catch (e: Exception) {
            emit(AuthenticationResponse.Error("Sign-in failed: ${e.message}"))
        }
    }

    override fun signInWithGoogle(): Flow<AuthenticationResponse> = flow {
        emit(AuthenticationResponse.Loading)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.CLIENT_KEY)
            .setAutoSelectEnabled(false)
            .setNonce(createNonce())
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val credential = manager.getCredential(context, request).credential

            if (credential !is CustomCredential) {
                emit(AuthenticationResponse.Error("Credential not found"))
            }

            if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                emit(AuthenticationResponse.Error("Invalid credential type"))
            }

            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

            auth.signInWithCredential(firebaseCredential).await()

            emit(AuthenticationResponse.Success)
        } catch (e: GoogleIdTokenParsingException) {
            e.printStackTrace()
            emit(AuthenticationResponse.Error(e.message ?: ""))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(AuthenticationResponse.Error(e.message ?: ""))
        }
    }
}
