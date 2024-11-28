package com.capstone.aiyam.presentation.auth.phone

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.capstone.aiyam.databinding.FragmentPhoneBinding
import com.capstone.aiyam.domain.model.AuthenticationResponse
import com.capstone.aiyam.utils.gone
import com.capstone.aiyam.utils.visible
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PhoneFragment : Fragment() {
    private var _binding: FragmentPhoneBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PhoneViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        animate()

        binding.sendButton.setOnClickListener {
            val phoneNumber = binding.phoneNumberEditText.text.toString()
            if (phoneNumber.isEmpty()) {
                showToast("Please enter a valid phone number.")
            } else {
                val phone = "+62$phoneNumber"
                startPhoneVerification(phone)
            }
        }
    }

    private fun startPhoneVerification(phoneNumber: String) {
        val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {}

            override fun onVerificationFailed(e: FirebaseException) {
                e.message?.let { showToast(it) }
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                showOtpBottomSheet(verificationId)
            }
        }

        viewModel.sendOtp(phoneNumber, callback)
    }

    private fun showOtpBottomSheet(verificationId: String) {
        OtpFragment { otp ->
            val credential = verifyCode(verificationId, otp)
            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                    viewModel.linkPhoneNumber(credential).collect {
                        handleOnAuth(it)
                    }
                }
            }
        }.show(parentFragmentManager, OtpFragment::class.java.simpleName)
    }

    private fun handleOnAuth(result: AuthenticationResponse ) {
        when(result) {
            is AuthenticationResponse.Error -> {
                onLoading(false)
                showToast(result.message)
            }
            is AuthenticationResponse.Loading -> {
                onLoading(true)
            }
            is AuthenticationResponse.Success -> {
                onLoading(false)
                showToast("Successfully linked phone number")
                findNavController().popBackStack()
            }
        }
    }

    private fun verifyCode(verificationId: String, code: String) = PhoneAuthProvider.getCredential(verificationId, code)

    private fun onLoading(isLoading: Boolean) {
        if (isLoading) binding.lpiLoading.visible() else binding.lpiLoading.gone()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun animate() { binding.apply {
        ObjectAnimator.ofFloat(illustration, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(title, View.ALPHA, 1f).setDuration(100)
        val subtitle = ObjectAnimator.ofFloat(subtitle, View.ALPHA, 1f).setDuration(100)
        val phoneNumberEditText = ObjectAnimator.ofFloat(phoneNumberEditText, View.ALPHA, 1f).setDuration(100)
        val countryCodeTextView = ObjectAnimator.ofFloat(countryCodeTextView, View.ALPHA, 1f).setDuration(100)
        val sendButton = ObjectAnimator.ofFloat(sendButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title, subtitle,
                phoneNumberEditText,
                countryCodeTextView,
                sendButton
            )
            startDelay = 100
        }.start()
    }}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
