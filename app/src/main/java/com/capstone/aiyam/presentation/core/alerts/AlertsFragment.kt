package com.capstone.aiyam.presentation.core.alerts

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.aiyam.R
import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.databinding.FragmentAlertsBinding
import com.capstone.aiyam.domain.model.Alerts
import com.capstone.aiyam.presentation.core.history.HistoryFragmentDirections
import com.capstone.aiyam.utils.gone
import com.capstone.aiyam.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AlertsFragment : Fragment() {
    private var _binding: FragmentAlertsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AlertsAdapter

    private val viewModel: AlertsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlertsBinding.inflate(inflater, container, false)
        initializeAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeChips()
        observeAlerts()
        observeRefresh()
    }

    private fun observeRefresh() {
        binding.refreshLayout.setOnRefreshListener {
            showRefresh(true)
            viewModel.fetchAlerts()
            binding.filterChipGroup.clearCheck()
        }
    }

    private fun observeChips() {
        binding.filterChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            val checkedId = checkedIds.firstOrNull()
            when (checkedId) {
                R.id.readChip -> viewModel.setFilterCriteria("read")
                R.id.unreadChip -> viewModel.setFilterCriteria("unread")
                else -> viewModel.setFilterCriteria(null)
            }
        }
    }

    private fun observeAlerts() { lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
            viewModel.filteredAlerts.collect { response ->
                handleAlerts(response)
            }
        }
    }}

    private fun handleAlerts(response: ResponseWrapper<List<Alerts>>) {
        when (response) {
            is ResponseWrapper.Success -> {
                handleSuccess(response.data)
            }
            is ResponseWrapper.Error -> {
                handleError(response.error)
            }
            is ResponseWrapper.Loading -> {
                showLoading(true)
            }
        }
    }

    private fun handleSuccess(alerts: List<Alerts>) {
        showLoading(false)
        showRefresh(false)
        Log.d("Alerts Data", alerts.toString())
        adapter.submitList(groupAlertsByDate(alerts))
    }

    private fun groupAlertsByDate(alerts: List<Alerts>): List<AlertsDisplayItem> {
        val groupedItems = mutableListOf<AlertsDisplayItem>()
        var lastDate: String? = null

        alerts.forEach { alert ->
            val date = alert.createdAt.split("T")[0]
            if (date != lastDate) {
                groupedItems.add(AlertsDisplayItem.Header(date))
                lastDate = date
            }
            groupedItems.add(AlertsDisplayItem.Item(alert))
        }

        return groupedItems
    }

    private fun initializeAdapter() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.alertsHistoryRecyclerView.layoutManager = layoutManager

        adapter = AlertsAdapter {
            val action = HistoryFragmentDirections.actionHistoryFragmentToAlertDetailFragment(it.id)
            findNavController().navigate(action)
        }

        binding.alertsHistoryRecyclerView.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding.lpiLoading.visible() else binding.lpiLoading.gone()
    }

    private fun showRefresh(isRefreshing: Boolean) {
        binding.refreshLayout.isRefreshing = isRefreshing
    }

    private fun handleError(error: String) {
        showLoading(false)
        showRefresh(false)
        showToast(error)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchAlerts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
