package com.capstone.aiyam.presentation.core.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstone.aiyam.databinding.FragmentHistoryBinding
import com.google.android.material.tabs.TabLayoutMediator

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        val pagerAdapter = HistoryPagerAdapter(requireActivity())

        val viewPager = binding.viewPager
        val tabs = binding.tabs

        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = TAB_TITLES[position]
        }.attach()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val TAB_TITLES = arrayOf("Alerts", "Scans")
    }
}
