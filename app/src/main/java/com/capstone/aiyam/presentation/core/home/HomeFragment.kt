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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.capstone.aiyam.databinding.FragmentHomeBinding
import com.capstone.aiyam.domain.model.WeeklySummary
import com.capstone.aiyam.utils.gone
import com.capstone.aiyam.utils.visible
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
        observeLoading()
    }

    private fun observeButtons() { binding.apply {
        btnNext.setOnClickListener {
            viewModel.goToAlertsPreviousPage()
        }

        btnPrevious.setOnClickListener {
            viewModel.goToAlertsNextPage()
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.canNavigateAlertsNext.collect { canNavigate ->
                    if (canNavigate) btnPrevious.visible() else btnPrevious.gone()
                    btnPrevious.isEnabled = canNavigate
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.canNavigateAlertsPrevious.collect { canNavigate ->
                    if (canNavigate) btnNext.visible() else btnNext.gone()
                    btnNext.isEnabled = canNavigate
                }
            }
        }

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshData()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        btnNextScan.setOnClickListener {
            viewModel.goToScansPreviousPage()
        }

        btnPreviousScan.setOnClickListener {
            viewModel.goToScansNextPage()
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.canNavigateScansNext.collect { canNavigate ->
                    if (canNavigate) btnPreviousScan.visible() else btnPreviousScan.gone()
                    btnPreviousScan.isEnabled = canNavigate
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.canNavigateScansPrevious.collect { canNavigate ->
                    if (canNavigate) btnNextScan.visible() else btnNextScan.gone()
                    btnNextScan.isEnabled = canNavigate
                }
            }
        }
    }}

    private fun observeLoading() { binding.apply {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.isLoadingAlerts.collect { isLoading ->
                    if (isLoading) {
                        mortalityTrendsCard.gone()
                        cardTwo.alpha = 0f
                        shimmerCardTwo.alpha = 1f
                        shimmerCardTwo.startShimmer()
                        shimmerLayoutMortality.visible()
                        shimmerLayoutMortality.startShimmer()
                    } else {
                        mortalityTrendsCard.visible()
                        cardTwo.alpha = 1f
                        shimmerCardTwo.alpha = 0f
                        shimmerCardTwo.stopShimmer()
                        shimmerLayoutMortality.gone()
                        shimmerLayoutMortality.stopShimmer()
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.isLoadingScans.collect { isLoading ->
                    if (isLoading) {
                        dailyChickenCard.gone()
                        cardOne.alpha = 0f
                        shimmerCardOne.alpha = 1f
                        shimmerCardOne.startShimmer()
                        shimmerLayoutScans.visible()
                        shimmerLayoutScans.startShimmer()
                    } else {
                        dailyChickenCard.visible()
                        cardOne.alpha = 1f
                        shimmerCardOne.alpha = 0f
                        shimmerCardOne.stopShimmer()
                        shimmerLayoutScans.gone()
                        shimmerLayoutScans.stopShimmer()
                    }
                }
            }
        }
    }}

    @SuppressLint("SetTextI18n")
    private fun observeSummaries() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.alertsCount.collectLatest { count ->
                    binding.alertsValue.text = "$count"
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.scansCount.collectLatest { count ->
                    binding.scannedValue.text = "$count"
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.currentPageAlertsSummaries.collectLatest { summaries ->
                    if (summaries.isNotEmpty()) setupWeeklyAlertsChart(summaries)
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.currentPageScansSummaries.collectLatest { summaries ->
                    if (summaries.isNotEmpty()) setupWeeklyScanChart(summaries)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupWeeklyAlertsChart(data: List<WeeklySummary>) {
        val entries = data.reversed().mapIndexed { index, value ->
            Entry(index.toFloat(), value.count.toFloat())
        }

        val dataSet = LineDataSet(entries, "Alerts Trends").apply {
            color = Color.RED
            setCircleColor(Color.BLACK)
        }

        binding.mortalityLineChart.data = LineData(dataSet)
        binding.mortalityLineChart.invalidate()

        binding.mortalityLabel.text = "${data.last().formattedDate} - ${data[0].formattedDate}"
        binding.mortalityCount.text = "${data.sumOf { it.count }} Alerts |"
    }

    @SuppressLint("SetTextI18n")
    private fun setupWeeklyScanChart(data: List<WeeklySummary>) {
        val entries = data.mapIndexed { index, value ->
            BarEntry(index.toFloat(), value.count.toFloat())
        }

        val dataSet = LineDataSet(entries, "Scan Count").apply {
            color = Color.GREEN
            setCircleColor(Color.BLACK)
        }

        binding.scanLineChart.data = LineData(dataSet)
        binding.scanLineChart.invalidate()

        binding.scanLabel.text = "${data.last().formattedDate} - ${data[0].formattedDate}"
        binding.scanCount.text = "${data.sumOf { it.count }} Scans |"
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
