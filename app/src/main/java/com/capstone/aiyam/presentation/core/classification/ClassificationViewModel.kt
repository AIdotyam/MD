package com.capstone.aiyam.presentation.core.classification

import androidx.lifecycle.ViewModel
import com.capstone.aiyam.domain.repository.ChickenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ClassificationViewModel @Inject constructor(
    private val chickenRepository: ChickenRepository
) : ViewModel() {
    fun classify(file: File, mediaType: String) = chickenRepository.classifyChicken(file, mediaType)
}
