package com.capstone.aiyam.presentation.core.home

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.capstone.aiyam.data.remote.DashboardData
import com.capstone.aiyam.data.remote.RetrofitInstance
import com.capstone.aiyam.databinding.FragmentHomeBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
        val dummyData: List<Int> = List(12) { (1..100).random() }
        val dummyData2: List<Int> = List(12) { (1..100).random() }

        RetrofitInstance.api.getDashboardData().enqueue(object : Callback<DashboardData> {
            override fun onResponse(call: Call<DashboardData>, response: Response<DashboardData>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        setupDailyBarChart(dummyData)
                        setupMortalityLineChart(dummyData2)
                    }
                } else {
                    setupDailyBarChart(dummyData)
                    setupMortalityLineChart(dummyData2)
                    // Launch a coroutine to change the charts after 2 seconds
                    lifecycleScope.launch {
                        delay(2000) // Delay for 2 seconds
                        // Generate new dummy data
                        val newDummyData: List<Int> = List(12) { (1..100).random() }
                        val newDummyData2: List<Int> = List(12) { (1..100).random() }

                        // Update the charts with new data
                        setupDailyBarChart(newDummyData)
                        setupMortalityLineChart(newDummyData2)

                        delay(2000) // Delay for 2 seconds

                        // Generate new dummy data
                        val newDummyData3: List<Int> = List(12) { (1..100).random() }
                        val newDummyData4: List<Int> = List(12) { (1..100).random() }

                        // Update the charts with new data
                        setupDailyBarChart(newDummyData3)
                        setupMortalityLineChart(newDummyData4)
                    }
                }
            }

            override fun onFailure(call: Call<DashboardData>, t: Throwable) {
                setupDailyBarChart(dummyData)
                setupMortalityLineChart(dummyData2)
            }
        })
    }

    private fun setupDailyBarChart(data: List<Int>) {
        val entries = data.mapIndexed { index, value -> BarEntry(index.toFloat(), value.toFloat()) }
        val dataSet = BarDataSet(entries, "Daily Count").apply {
            color = Color.BLUE
        }

        binding.dailyBarChart.data = BarData(dataSet)
        binding.dailyBarChart.invalidate()
    }

    private fun setupMortalityLineChart(data: List<Int>) {
        val entries = data.mapIndexed { index, value -> Entry(index.toFloat(), value.toFloat()) }
        val dataSet = LineDataSet(entries, "Mortality Trends").apply {
            color = Color.RED
            setCircleColor(Color.RED)
        }

        binding.mortalityLineChart.data = LineData(dataSet)
        binding.mortalityLineChart.invalidate()
    }
}
