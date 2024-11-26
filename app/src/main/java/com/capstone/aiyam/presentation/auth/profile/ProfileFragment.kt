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
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.capstone.aiyam.R
import com.capstone.aiyam.databinding.FragmentProfileBinding
import com.capstone.aiyam.domain.model.AuthorizationResponse
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint

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

        when(val result = viewModel.getUser()) {
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
            alertDialog().show()
        }

        binding.phoneNumberButton.setOnClickListener {
            ProfileFragmentDirections.actionProfileFragmentToPhoneFragment().let {
                findNavController().navigate(it)
            }
        }
    }

    private fun alertDialog(): AlertDialog {
        val dialogView = requireActivity().layoutInflater.inflate(R.layout.custom_dialog, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val positiveButton = dialogView.findViewById<Button>(R.id.dialog_positive_button)
        val negativeButton = dialogView.findViewById<Button>(R.id.dialog_negative_button)

        positiveButton.setOnClickListener {
            viewModel.signOut()
            showToast("Successfully signed out")
            dialog.dismiss()
            ProfileFragmentDirections.actionProfileFragmentToSplashFragment().let {
                findNavController().navigate(it)
            }
        }

        negativeButton.setOnClickListener {
            dialog.dismiss()
        }

        return dialog
    }

    private fun handleUser(user: FirebaseUser) { binding.apply {
        Glide.with(requireContext())
            .load(user.photoUrl)
            .placeholder(R.drawable.baseline_people_24)
            .error(R.drawable.baseline_people_24)
            .circleCrop()
            .into(profileImage)

        username.text = user.displayName
        emailInput.text = user.email
        phoneInput.text = user.phoneNumber ?: ""
    }}

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
