package com.capstone.aiyam.data.dto


sealed class SuspendWrapper<out R> private constructor() {
    data class Success<out T>(val data: T) : SuspendWrapper<T>()
    data class Error(val error: String) : SuspendWrapper<Nothing>()
}
