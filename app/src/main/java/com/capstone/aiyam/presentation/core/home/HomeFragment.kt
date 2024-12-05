package com.capstone.aiyam.presentation.core.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.capstone.aiyam.databinding.FragmentHomeBinding
import com.capstone.aiyam.domain.model.DailySummary
import com.capstone.aiyam.domain.model.WeeklySummary
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
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
        observeButtons()
        observeSummaries()
        observeError()
    }

    private fun observeButtons() { binding.apply {
        btnNext.setOnClickListener {
            viewModel.goToPreviousPage()
        }

        btnPrevious.setOnClickListener {
            viewModel.goToNextPage()
        }

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshData()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.canNavigateNext.collect { canNavigate ->
                btnPrevious.isEnabled = canNavigate
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.canNavigatePrevious.collect { canNavigate ->
                btnNext.isEnabled = canNavigate
            }
        }
    }}

    @SuppressLint("SetTextI18n")
    private fun observeSummaries() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentPageSummaries.collectLatest { summaries ->
                if (summaries.isNotEmpty()) setupWeeklyAlertsChart(summaries)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupWeeklyAlertsChart(data: List<WeeklySummary>) {
        binding.swipeRefreshLayout.isRefreshing = false

        val entries = data.reversed().mapIndexed { index, value ->
            Entry(index.toFloat(), value.count.toFloat())
        }

        val dataSet = LineDataSet(entries, "Alerts Trends").apply {
            color = Color.RED
            setCircleColor(Color.RED)
        }

        binding.mortalityLineChart.data = LineData(dataSet)
        binding.mortalityLineChart.invalidate()

        binding.mortalityLabel.text = "${data.last().formattedDate} - ${data[0].formattedDate}"

        val totalAlerts = data.sumOf { it.count }
        binding.mortalityCount.text = "$totalAlerts Alerts | "
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

    private fun handleError(error: String) {
        binding.swipeRefreshLayout.isRefreshing = false
        showToast(error)
    }

    private fun observeError() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.errorMessage.collect { errorMsg ->
                if (errorMsg != null) {
                    handleError(errorMsg)
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
