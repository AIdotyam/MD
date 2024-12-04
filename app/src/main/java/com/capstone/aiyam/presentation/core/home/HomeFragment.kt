package com.capstone.aiyam.presentation.core.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.databinding.FragmentHomeBinding
import com.capstone.aiyam.domain.model.DailySummary
import com.capstone.aiyam.domain.model.MonthlySummary
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.init()
        }

        viewModel.getPushToken()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.daily.collect {
                    when(it) {
                        is ResponseWrapper.Success -> {
                            setupDailyBarChart(it.data)
                            binding.swipeRefreshLayout.isRefreshing = false
                        }
                        is ResponseWrapper.Loading -> {

                        }
                        is ResponseWrapper.Error -> {
                            handleError(it.error)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.monthly.collect {
                    when(it) {
                        is ResponseWrapper.Success -> {
                            setupMortalityLineChart(it.data)
                            binding.swipeRefreshLayout.isRefreshing = false
                        }
                        is ResponseWrapper.Loading -> {

                        }
                        is ResponseWrapper.Error -> {
                            handleError(it.error)
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupDailyBarChart(data: List<DailySummary>) {
        val entries = data.mapIndexed { index, value ->
            BarEntry(index.toFloat(), value.chickenCount.toFloat())
        }

        val dataSet = BarDataSet(entries, "Daily Count").apply {
            color = Color.BLUE
        }

        binding.dailyBarChart.data = BarData(dataSet)
        binding.dailyBarChart.invalidate()

        binding.dailyLabel.text = "Weekly Chickens Scanned"
        binding.dailyCount.text = data.sumOf { it.chickenCount }.toString()
    }

    @SuppressLint("SetTextI18n")
    private fun setupMortalityLineChart(data: List<MonthlySummary>) {
        val entries = data.mapIndexed { index, value ->
            Entry(index.toFloat(), value.deadCount.toFloat())
        }

        val dataSet = LineDataSet(entries, "Mortality Trends").apply {
            color = Color.RED
            setCircleColor(Color.RED)
        }

        binding.mortalityLineChart.data = LineData(dataSet)
        binding.mortalityLineChart.invalidate()

        binding.mortalityLabel.text = "Annual Mortality Count"
        binding.mortalityCount.text = data.sumOf { it.deadCount }.toString()
    }

    private fun handleError(error: String) {
        binding.swipeRefreshLayout.isRefreshing = false
        showToast(error)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
