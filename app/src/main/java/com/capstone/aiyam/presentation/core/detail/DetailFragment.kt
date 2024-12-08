package com.capstone.aiyam.presentation.core.detail

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.capstone.aiyam.R
import com.capstone.aiyam.databinding.FragmentDetailBinding
import com.capstone.aiyam.domain.model.Classification
import com.google.android.material.bottomsheet.BottomSheetBehavior

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        val classification = DetailFragmentArgs.fromBundle(requireArguments()).Classification
        bindViews(classification)

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
    }

    @SuppressLint("SetTextI18n")
    private fun bindViews(classification: Classification) { binding.apply {
        if (classification.deadChicken) {
            chickenValue.text = "Dead Chicken Detected"
            chickenValueDesc.text = "Immediate action required to maintain flock health."
            icon.setImageResource(R.drawable.peti_mati)
        } else {
            chickenValue.text = "Chickens Alive and Well!"
            chickenValueDesc.text = "No immediate action necessary—flock in good health."
            icon.setImageResource(R.drawable.peti_hidup)
        }


        Glide.with(requireContext()).load(classification.mediaUrl).into(headerImage)

        signInButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
