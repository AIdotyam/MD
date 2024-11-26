package com.capstone.aiyam.presentation.auth.phone

import androidx.lifecycle.ViewModel
import com.capstone.aiyam.domain.repository.AuthenticationRepository
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PhoneViewModel @Inject constructor (
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {
    fun sendOtp(phoneNumber: String, verificationCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks) {
        authenticationRepository.sendOtp(phoneNumber, verificationCallback)
    }

    fun linkPhoneNumber(credential: PhoneAuthCredential) = authenticationRepository.linkPhoneNumber(credential)
}
