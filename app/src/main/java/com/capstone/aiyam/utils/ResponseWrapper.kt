package com.capstone.aiyam.utils

sealed class ResponseWrapper<out R> private constructor() {
    data class Success<out T>(val data: T) : ResponseWrapper<T>()
    data class Error(val error: String) : ResponseWrapper<Nothing>()
    data object Loading : ResponseWrapper<Nothing>()
}