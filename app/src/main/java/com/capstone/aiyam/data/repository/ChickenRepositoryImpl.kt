package com.capstone.aiyam.data.repository

import com.capstone.aiyam.data.dto.Classification
import com.capstone.aiyam.data.remote.ChickenService
import com.capstone.aiyam.utils.ResponseWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ChickenRepositoryImpl @Inject constructor(
    private val chickenService: ChickenService
){
    fun classifyChicken(): Flow<ResponseWrapper<Classification>> = flow {
        emit(ResponseWrapper.Loading)



        chickenService.postChicken()
    }
}