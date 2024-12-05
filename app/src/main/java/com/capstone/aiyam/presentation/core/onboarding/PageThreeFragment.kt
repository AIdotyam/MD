package com.capstone.aiyam.presentation.core.onboarding

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.capstone.aiyam.R
import com.capstone.aiyam.databinding.FragmentOnboardingBinding
import com.capstone.aiyam.databinding.FragmentPageThreeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PageThreeFragment : Fragment() {
    private var _binding: FragmentPageThreeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OnboardingViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPageThreeBinding.inflate(inflater, container, false)

        val viewPager = requireActivity().findViewById<ViewPager2>(R.id.viewPagerOnboarding)

        binding.btnPrevious.setOnClickListener {
            viewPager.currentItem = 1
        }

        binding.btnNext.text = "Start"
        binding.btnNext.setOnClickListener {
            viewModel.saveOnboarding()

            val action = OnboardingFragmentDirections.actionOnboardingFragmentToSigninFragment()
            findNavController().navigate(action)
        }

        return binding.root
    }
}
