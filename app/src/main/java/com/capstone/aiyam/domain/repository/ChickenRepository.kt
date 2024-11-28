package com.capstone.aiyam.domain.repository

import com.capstone.aiyam.domain.model.Classification
import com.capstone.aiyam.data.dto.ResponseWrapper
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ChickenRepository {
    fun classifyChicken(token: String, file: File, mediaType: String): Flow<ResponseWrapper<Classification>>
    fun getHistories(token: String): Flow<ResponseWrapper<List<Classification>>>
}
