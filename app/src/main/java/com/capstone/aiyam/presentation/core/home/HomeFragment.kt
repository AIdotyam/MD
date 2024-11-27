package com.capstone.aiyam.presentation.core.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.capstone.aiyam.R
import com.capstone.aiyam.data.remote.DashboardData
import com.capstone.aiyam.data.remote.RetrofitInstance
import com.capstone.aiyam.databinding.FragmentHomeBinding
import com.github.mikephil.charting.data.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        // Fetch and display data from API
        fetchDashboardData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchDashboardData() {
        RetrofitInstance.api.getDashboardData().enqueue(object : Callback<DashboardData> {
            override fun onResponse(call: Call<DashboardData>, response: Response<DashboardData>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        setupDailyBarChart(it.daily_count)
                        setupMortalityLineChart(it.mortality_trends)
                    }
                } else {
                    // Handle response error
                }
            }

            override fun onFailure(call: Call<DashboardData>, t: Throwable) {
                // Handle API call failure (e.g., log error or show Toast)
            }
        })
    }

    private fun setupDailyBarChart(data: List<Int>) {
        val entries = data.mapIndexed { index, value -> BarEntry(index.toFloat(), value.toFloat()) }
        val dataSet = BarDataSet(entries, "Daily Count").apply {
            color = Color.BLUE
        }
        binding.dailyBarChart.data = BarData(dataSet)
        binding.dailyBarChart.invalidate() // Refresh the chart
    }

    private fun setupMortalityLineChart(data: List<Int>) {
        val entries = data.mapIndexed { index, value -> Entry(index.toFloat(), value.toFloat()) }
        val dataSet = LineDataSet(entries, "Mortality Trends").apply {
            color = Color.RED
            setCircleColor(Color.RED)
        }
        binding.mortalityLineChart.data = LineData(dataSet)
        binding.mortalityLineChart.invalidate() // Refresh the chart
    }
}
