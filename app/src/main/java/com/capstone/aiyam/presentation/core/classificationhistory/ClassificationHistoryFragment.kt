package com.capstone.aiyam.presentation.core.classificationhistory

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstone.aiyam.databinding.FragmentClassificationHistoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClassificationHistoryFragment : Fragment() {
    private var _binding: FragmentClassificationHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ClassificationHistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClassificationHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
