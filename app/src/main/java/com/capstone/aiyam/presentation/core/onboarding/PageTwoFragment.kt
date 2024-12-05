package com.capstone.aiyam.presentation.core.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.capstone.aiyam.R
import com.capstone.aiyam.databinding.FragmentPageTwoBinding

class PageTwoFragment : Fragment() {
    private var _binding: FragmentPageTwoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPageTwoBinding.inflate(inflater, container, false)

        val viewPager = requireActivity().findViewById<ViewPager2>(R.id.viewPagerOnboarding)

        binding.btnPrevious.setOnClickListener {
            viewPager.currentItem = 0
        }

        binding.btnNext.setOnClickListener {
            viewPager.currentItem = 2
        }

        return binding.root
    }
}
