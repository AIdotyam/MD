package com.capstone.aiyam.presentation.core.classificationhistory

import androidx.fragment.app.viewModels
import android.os.Bundle
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
import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.databinding.FragmentClassificationHistoryBinding
import com.capstone.aiyam.domain.model.Classification
import com.capstone.aiyam.presentation.core.history.HistoryFragmentDirections
import com.capstone.aiyam.utils.gone
import com.capstone.aiyam.utils.parseDateToEnglish
import com.capstone.aiyam.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ClassificationHistoryFragment : Fragment() {
    private var _binding: FragmentClassificationHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ClassificationHistoryAdapter

    private val viewModel: ClassificationHistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClassificationHistoryBinding.inflate(inflater, container, false)
        initializeAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeHistories()
        getHistories()
        observeRefresh()
    }

    private fun observeRefresh() {
        binding.refreshLayout.setOnRefreshListener {
            showRefresh(true)
            getHistories()
        }
    }

    private fun observeHistories() { lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
            viewModel.histories.collect { response ->
                handleHistories(response)
            }
        }
    }}

    private fun handleHistories(response: ResponseWrapper<List<Classification>>) {
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

    private fun getHistories() { lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
            viewModel.fetchHistories()
        }
    }}

    private fun handleSuccess(histories: List<Classification>) {
        showLoading(false)
        showRefresh(false)
        adapter.submitList(groupHistoriesByDate(histories))
    }

    private fun groupHistoriesByDate(histories: List<Classification>): List<ClassificationHistoryDisplayItem> {
        val groupedItems = mutableListOf<ClassificationHistoryDisplayItem>()
        var lastDate: String? = null

        histories.forEach { history ->
            val date = history.createdAt.split("T")[0].parseDateToEnglish()
            if (date != lastDate) {
                groupedItems.add(ClassificationHistoryDisplayItem.Header(date))
                lastDate = date
            }
            groupedItems.add(ClassificationHistoryDisplayItem.Item(history))
        }

        return groupedItems
    }

    private fun initializeAdapter() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.alertsHistoryRecyclerView.layoutManager = layoutManager

        adapter = ClassificationHistoryAdapter {
            val action = HistoryFragmentDirections.actionHistoryFragmentToDetailFragment(it)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
