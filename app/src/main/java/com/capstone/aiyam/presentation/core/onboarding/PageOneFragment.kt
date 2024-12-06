package com.capstone.aiyam.presentation.core.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.capstone.aiyam.R
import com.capstone.aiyam.databinding.FragmentPageOneBinding
import com.capstone.aiyam.databinding.FragmentPageThreeBinding
import com.capstone.aiyam.utils.gone

class PageOneFragment : Fragment() {
    private var _binding: FragmentPageOneBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPageOneBinding.inflate(inflater, container, false)

        val viewPager = requireActivity().findViewById<ViewPager2>(R.id.viewPagerOnboarding)


        binding.btnNext.setOnClickListener {
            viewPager.currentItem = 1
        }

        return binding.root
    }
}
