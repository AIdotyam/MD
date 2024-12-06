package com.capstone.aiyam.presentation.core.detail

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.capstone.aiyam.R
import com.capstone.aiyam.databinding.FragmentDetailBinding
import com.capstone.aiyam.domain.model.Classification
import com.capstone.aiyam.utils.getMimeTypeFromUrl
import com.capstone.aiyam.utils.parseDateTime

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private var player: ExoPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        // Retrieve the story data passed from the previous fragment
        val classification = DetailFragmentArgs.fromBundle(requireArguments()).Classification
        bindViews(classification)

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun bindViews(classification: Classification) { binding.apply {
        if (classification.deadChicken) {
            predictValue.text = "Dead Chicken Detected"
            predictIcon.setImageResource(R.drawable.skull)
        } else {
            predictValue.text = "No Dead Chicken Detected"
            predictIcon.setImageResource(R.drawable.chicken)
        }

        dateTitle.text = classification.createdAt.parseDateTime()

        val mediaUrl = classification.mediaUrl
        val mimeType = mediaUrl.getMimeTypeFromUrl()

        if (mimeType != null && mimeType.startsWith("video")) {
            headerVideo.visibility = View.VISIBLE

            val mediaItem = MediaItem.Builder()
                .setUri(mediaUrl)
                .build()

            player = ExoPlayer.Builder(requireContext()).build().also { exoPlayer ->
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.playWhenReady = true
                exoPlayer.prepare()
            }

            binding.headerVideo.player = player
        } else if (mimeType != null && mimeType.startsWith("image")) {
            headerImage.visibility = View.VISIBLE
            Glide.with(requireContext()).load(mediaUrl).into(headerImage)
        } else {
            headerImage.visibility = View.VISIBLE
            Glide.with(requireContext()).load(R.drawable.baseline_broken_image_24).into(headerImage)
        }

        backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }}

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
