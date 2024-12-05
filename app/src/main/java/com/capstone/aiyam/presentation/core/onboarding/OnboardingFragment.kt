package com.capstone.aiyam.presentation.core.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstone.aiyam.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment() {
    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)

        val fragmentList = arrayListOf(
            PageOneFragment(),
            PageTwoFragment(),
            PageThreeFragment()
        )

        val adapter = OnboardingPagerAdapter(fragmentList, childFragmentManager, lifecycle)

        binding.viewPagerOnboarding.adapter = adapter
        binding.indicator.attachTo(binding.viewPagerOnboarding)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
