package com.capstone.aiyam.domain.repository

import com.capstone.aiyam.domain.model.Classification
import com.capstone.aiyam.utils.ResponseWrapper
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ChickenRepository {
    fun classifyChicken(file: File, mediaType: String): Flow<ResponseWrapper<Classification>>
}
