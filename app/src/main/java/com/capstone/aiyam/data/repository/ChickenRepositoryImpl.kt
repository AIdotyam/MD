package com.capstone.aiyam.data.repository

import android.util.Log
import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.data.remote.ChickenService
import com.capstone.aiyam.domain.model.Classification
import com.capstone.aiyam.domain.repository.ChickenRepository
import com.capstone.aiyam.domain.repository.UserRepository
import com.capstone.aiyam.utils.withToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
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

        repeat(3) { attempt ->
            try {
                val chicken = withToken(user, userRepository::getFirebaseToken) {
                    chickenService.postChicken(it, multipartBody)
                }
                emit(ResponseWrapper.Success(chicken.data))
                return@flow
            } catch (e: Exception) {
                if (attempt == 2) {
                    emit(ResponseWrapper.Error(e.message.toString()))
                    return@flow
                } else {
                    delay(500L)
                }
            }
        }
    }

    override fun getHistories(): Flow<ResponseWrapper<List<Classification>>> = flow {
        emit(ResponseWrapper.Loading)
        try {
            val histories = withToken(user, userRepository::getFirebaseToken) {
                Log.d("ChickenRepositoryImpl Firebase ID Token", it)
                chickenService.getHistories(it)
            }
            emit(ResponseWrapper.Success(histories.data))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }

    override suspend fun warmUp(file: File) {
        val requestBody = file.asRequestBody("text/plain".toMediaTypeOrNull())
        val malformedPart = MultipartBody.Part.createFormData("file", file.name, requestBody)
        try {
            withToken(user, userRepository::getFirebaseToken) {
                chickenService.warmUp(it, malformedPart)
            }
        } catch (e: Exception) {
            Log.e("WarmUp", "Triggered error as expected: ${e.message}")
        }
    }
}
