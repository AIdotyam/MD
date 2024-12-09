package com.capstone.aiyam.presentation.auth.profile

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.capstone.aiyam.R
import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.databinding.FragmentProfileBinding
import com.capstone.aiyam.domain.model.AuthorizationResponse
import com.capstone.aiyam.domain.model.TargetAlerts
import com.capstone.aiyam.presentation.shared.CustomAlertDialog
import com.capstone.aiyam.utils.gone
import com.capstone.aiyam.utils.visible
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
                ProfileFragmentDirections.actionProfileFragmentToSigninFragment().let {
                    findNavController().navigate(it)
                }
            }
        }

        binding.signOutIcon.setOnClickListener {
            val dialog = CustomAlertDialog(
                context = requireContext(),
                title = "Sign out",
                message = "",
                negativeButtonClick = {}
            ) {
                lifecycleScope.launch {
                    viewModel.signOut()
                    showToast("Successfully signed out")
                    requireActivity().finish()
                }
            }

            dialog.alert().show()
        }

        handlePushNotification()
        handlePhoneSetting()
    }

    private fun handlePushNotification() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.getPushNotificationSetting().collect { isEnabled ->
                    binding.notificationSwitch.isChecked = isEnabled
                    binding.notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked != isEnabled) {
                            lifecycleScope.launch {
                                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                                    if (isChecked) {
                                        viewModel.enablePushAlerts().collect {
                                            handleSwitch(it)
                                        }
                                    } else {
                                        viewModel.disablePushAlerts().collect {
                                            handleSwitch(it)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handlePhoneSetting() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.getPhoneNumberSetting().collect {
                    binding.phoneInput.text = it

                    val isEnabled = it.isNotEmpty()

                    binding.customSwitch.isChecked = isEnabled
                    binding.customSwitch.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked != isEnabled) {
                            if (!isChecked) {
                                lifecycleScope.launch {
                                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                                        viewModel.deleteNumber().collect { event ->
                                            handleSwitch(event)
                                        }
                                    }
                                }
                            } else {
                                ProfileFragmentDirections.actionProfileFragmentToPhoneFragment().let { action ->
                                    findNavController().navigate(action)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleSwitch(target: ResponseWrapper<TargetAlerts>) {
        when (target) {
            is ResponseWrapper.Loading -> {
                binding.lpiLoading.visible()
            }
            is ResponseWrapper.Success -> {
                binding.lpiLoading.gone()
            }
            is ResponseWrapper.Error -> {
                binding.lpiLoading.gone()
            }
        }
    }

    private fun handleUser(user: FirebaseUser) { binding.apply {
        Glide.with(requireContext())
            .load(user.photoUrl)
            .placeholder(R.drawable.baseline_person_24)
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
