package com.capstone.aiyam.presentation.core.alertdetail

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
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.capstone.aiyam.R
import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.databinding.FragmentAlertDetailBinding
import com.capstone.aiyam.domain.model.Alerts
import com.capstone.aiyam.utils.getMimeTypeFromUrl
import com.capstone.aiyam.utils.gone
import com.capstone.aiyam.utils.parseDateTime
import com.capstone.aiyam.utils.visible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AlertDetailFragment : Fragment() {
    private var _binding: FragmentAlertDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private val viewModel: AlertDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlertDetailBinding.inflate(inflater, container, false)

        val id = AlertDetailFragmentArgs.fromBundle(requireArguments()).id
        loadAlert(id)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.drawerContainer)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        binding.scrollableContainer.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY > 0 && bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            } else if (scrollY == 0) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun loadAlert(id: Int) { lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
            viewModel.getAlertById(id).collect { response ->
                handleAlert(response)
            }
        }
    }}

    private fun handleAlert(response: ResponseWrapper<Alerts>) {
        when (response) {
            is ResponseWrapper.Success -> {
                bindAlert(response.data)
            }
            is ResponseWrapper.Error -> {
                handleError(response.error)
            }
            is ResponseWrapper.Loading -> {
                showLoading(true)
            }
        }
    }

    private fun bindAlert(alert: Alerts) { binding.apply {
        showLoading(false)
        icon.setImageResource(R.drawable.alert_svgrepo_com__1_)
        chickenValueDesc.text = "Please check your chicken's condition immediately"
        dateTitle.text = alert.createdAt.parseDateTime()
        Glide.with(requireContext()).load(alert.mediaUrl).into(headerImage)
    }}

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.lpiLoading.visible()
            handleVisibility(true)
        } else {
            binding.lpiLoading.gone()
            handleVisibility(false)
        }
    }

    private fun handleError(error: String) {
        showLoading(false)
        handleVisibility(false)
        showToast(error)
    }

    private fun handleVisibility(isVisible: Boolean) { binding.apply {
        dateTitle.visibility = if (isVisible) View.GONE else View.VISIBLE
    }}

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
