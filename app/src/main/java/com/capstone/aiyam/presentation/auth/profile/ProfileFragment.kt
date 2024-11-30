package com.capstone.aiyam.presentation.auth.profile

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.capstone.aiyam.R
import com.capstone.aiyam.databinding.FragmentProfileBinding
import com.capstone.aiyam.domain.model.AuthorizationResponse
import com.capstone.aiyam.presentation.shared.CustomAlertDialog
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (val result = viewModel.getUser()) {
            is AuthorizationResponse.Success -> {
                handleUser(result.user)
            }

            is AuthorizationResponse.Error -> {
                showToast(result.message)
                ProfileFragmentDirections.actionProfileFragmentToSplashFragment().let {
                    findNavController().navigate(it)
                }
            }
        }

        binding.signOutIcon.setOnClickListener {
            val dialog = CustomAlertDialog(
                context = requireContext(),
                title = "Sign out",
                message = "Are you sure you want to sign out?",
                negativeButtonClick = {}
            ) {
                viewModel.signOut()
                showToast("Successfully signed out")
                ProfileFragmentDirections.actionProfileFragmentToSplashFragment().let {
                    findNavController().navigate(it)
                }
            }

            dialog.alert().show()
        }

        handlePushNotification()
        handleEmailNotification()
        handlePhoneSetting()
    }

    private fun handlePushNotification() {
        binding.notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.savePushNotificationSetting(isChecked)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {

                // TODO: Add some kind of API call to set notification setting
                //  if action true, send notification token, else remove notification token
                viewModel.getPushNotificationSetting().collect {
                    binding.notificationSwitch.isChecked = it
                }
            }
        }
    }

    private fun handleEmailNotification() {
        binding.emailNotificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveEmailNotificationSetting(isChecked)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {

                // TODO: Add some kind of API call to set notification setting
                //  if action true, send notification token, else remove notification token
                viewModel.getEmailNotificationSetting().collect {
                    binding.emailNotificationSwitch.isChecked = it
                }
            }
        }
    }

    private fun handlePhoneSetting() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.getPhoneNumberSetting().collect {
                    binding.phoneInput.text = it
                }
            }
        }

        binding.cardButtonPhoneNumber.setOnClickListener {
            ProfileFragmentDirections.actionProfileFragmentToPhoneFragment().let {
                findNavController().navigate(it)
            }
        }
    }

    private fun handleUser(user: FirebaseUser) { binding.apply {
        Glide.with(requireContext())
            .load(user.photoUrl)
            .placeholder(R.drawable.baseline_people_24)
            .circleCrop()
            .into(profileImage)

        username.text = user.displayName
        emailInput.text = user.email
    }}

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
