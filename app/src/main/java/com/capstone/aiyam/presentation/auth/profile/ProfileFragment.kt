package com.capstone.aiyam.presentation.auth.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
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
import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.databinding.FragmentProfileBinding
import com.capstone.aiyam.domain.model.AuthorizationResponse
import com.capstone.aiyam.domain.model.TargetAlerts
import com.capstone.aiyam.presentation.shared.CustomAlertDialog
import com.capstone.aiyam.utils.gone
import com.capstone.aiyam.utils.visible
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
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
                message = "Are you sure you want to sign out?",
                negativeButtonClick = {}
            ) {
                viewModel.signOut()
                showToast("Successfully signed out")
                ProfileFragmentDirections.actionProfileFragmentToSigninFragment().let {
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

    private fun handleEmailNotification() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.getEmailNotificationSetting().collect { isEnabled ->
                    binding.emailNotificationSwitch.isChecked = isEnabled
                    binding.emailNotificationSwitch.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked != isEnabled) {
                            lifecycleScope.launch {
                                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                                    if (isChecked) {
                                        viewModel.enableEmailAlerts().collect {
                                            handleSwitch(it)
                                        }
                                    } else {
                                        viewModel.disableEmailAlerts().collect {
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

                    if (it.isNotEmpty()) {
                        binding.cardButtonPhoneNumber.gone()
                        binding.cardButtonDeletePhoneNumber.visible()
                    } else {
                        binding.cardButtonPhoneNumber.visible()
                        binding.cardButtonDeletePhoneNumber.gone()
                    }
                }
            }
        }

        binding.cardButtonPhoneNumber.setOnClickListener {
            ProfileFragmentDirections.actionProfileFragmentToPhoneFragment().let {
                findNavController().navigate(it)
            }
        }

        binding.cardButtonDeletePhoneNumber.setOnClickListener {
            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                    viewModel.deleteNumber().collect{
                        handleSwitch(it)
                    }
                }
            }
        }

        binding.cardButtonTelegram.setOnClickListener {
            performCopyAndRedirect()
        }
    }

    private fun performCopyAndRedirect() {
        val textToCopy = when(val user = viewModel.getUser()) {
            is AuthorizationResponse.Success -> {
                user.user.uid
            }
            else -> {
                ""
            }
        }
        val copySuccessful = copyTextToClipboard(textToCopy)

        if (copySuccessful) {
            redirectToTelegram()
        } else {
            Toast.makeText(requireContext(), "Failed to copy your id.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun copyTextToClipboard(text: String): Boolean {
        return try {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", text)
            clipboard.setPrimaryClip(clip)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun redirectToTelegram() {
        val telegramUsername = "MissRose_bot"
        val telegramUri = Uri.parse("tg://resolve?domain=$telegramUsername")
        val intent = Intent(Intent.ACTION_VIEW, telegramUri)

        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            val webUri = Uri.parse("https://t.me/$telegramUsername")
            val webIntent = Intent(Intent.ACTION_VIEW, webUri)
            if (webIntent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(webIntent)
            } else {
                Toast.makeText(requireContext(), "Unable to open Telegram.", Toast.LENGTH_SHORT).show()
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
