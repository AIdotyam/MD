package com.capstone.aiyam.presentation.auth.phone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.capstone.aiyam.databinding.FragmentOtpBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OtpFragment(
    private val onOtpVerified: (String) -> Unit
) : BottomSheetDialogFragment() {
    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.verifyOtpButton.setOnClickListener {
            val otp = binding.otpEditText.text.toString()
            if (otp.isNotEmpty()) {
                onOtpVerified(otp)
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Please enter OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
