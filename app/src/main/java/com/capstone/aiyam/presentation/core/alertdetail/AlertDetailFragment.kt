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
import com.capstone.aiyam.databinding.FragmentHomeBinding
import com.capstone.aiyam.domain.model.Alerts
import com.capstone.aiyam.presentation.core.detail.DetailFragmentArgs
import com.capstone.aiyam.utils.getMimeTypeFromUrl
import com.capstone.aiyam.utils.gone
import com.capstone.aiyam.utils.parseDateTime
import com.capstone.aiyam.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AlertDetailFragment : Fragment() {
    private var _binding: FragmentAlertDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AlertDetailViewModel by viewModels()
    private var player: ExoPlayer? = null

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

        dateTitle.text = alert.createdAt.parseDateTime()

        val mediaUrl = alert.mediaUrl
        val mimeType = mediaUrl.getMimeTypeFromUrl()

        if (mimeType != null && mimeType.startsWith("video")) {
            headerVideo.visibility = View.VISIBLE

            val mediaItem = MediaItem.Builder()
                .setUri(mediaUrl)
                .build()

            player = ExoPlayer.Builder(requireContext()).build().also { exoPlayer ->
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
            }

            player?.playWhenReady = true
            binding.headerVideo.player = player
        } else if (mimeType != null && mimeType.startsWith("image")) {
            headerImage.visibility = View.VISIBLE
            Glide.with(requireContext()).load(mediaUrl).into(headerImage)
        } else {
            headerImage.visibility = View.VISIBLE
            Glide.with(requireContext()).load(R.drawable.baseline_broken_image_24).into(headerImage)
        }
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
        dateTitle.visibility = View.VISIBLE
    }}

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        releasePlayer()
        _binding = null
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }
}
