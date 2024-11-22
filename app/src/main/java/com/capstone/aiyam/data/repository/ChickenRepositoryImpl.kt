package com.capstone.aiyam.data.repository

import com.capstone.aiyam.domain.model.Classification
import com.capstone.aiyam.data.remote.ChickenService
import com.capstone.aiyam.domain.repository.ChickenRepository
import com.capstone.aiyam.utils.ResponseWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class ChickenRepositoryImpl @Inject constructor(
    private val chickenService: ChickenService
) : ChickenRepository {
    override fun classifyChicken(file: File, mediaType: String): Flow<ResponseWrapper<Classification>> = flow {
        emit(ResponseWrapper.Loading)

        val requestBody = file.asRequestBody(mediaType.toMediaTypeOrNull())
        val multipartBody =  MultipartBody.Part.createFormData("file", file.name, requestBody)

        try {
            val response = chickenService.postChicken(multipartBody)
            emit(ResponseWrapper.Success(response))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }
}
