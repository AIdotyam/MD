package com.capstone.aiyam.data.repository

import com.capstone.aiyam.domain.model.Classification
import com.capstone.aiyam.data.remote.ChickenService
import com.capstone.aiyam.domain.repository.ChickenRepository
import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.domain.repository.UserRepository
import com.capstone.aiyam.utils.withToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class ChickenRepositoryImpl @Inject constructor(
    private val chickenService: ChickenService,
    private val userRepository: UserRepository
) : ChickenRepository {
    private val user = userRepository.getFirebaseUser()

    override fun classifyChicken(file: File, mediaType: String): Flow<ResponseWrapper<Classification>> = flow {
        emit(ResponseWrapper.Loading)

        val requestBody = file.asRequestBody(mediaType.toMediaTypeOrNull())
        val multipartBody =  MultipartBody.Part.createFormData("file", file.name, requestBody)

        try {
            val chicken = withToken(user, userRepository::getFirebaseToken) {
                chickenService.postChicken(it, multipartBody)
            }
            emit(ResponseWrapper.Success(chicken.data))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }

    override fun getHistories(): Flow<ResponseWrapper<List<Classification>>> = flow {
        emit(ResponseWrapper.Loading)
        try {
            val histories = withToken(user, userRepository::getFirebaseToken) {
                chickenService.getHistories(it)
            }
            emit(ResponseWrapper.Success(histories.data))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }
}
