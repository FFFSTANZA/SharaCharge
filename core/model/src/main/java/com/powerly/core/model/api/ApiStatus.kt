package com.SharaSpot.core.model.api

import com.SharaSpot.core.model.util.Message

sealed class ApiStatus<out T> {
    data class Error(val msg: Message) : ApiStatus<Nothing>()
    data class Success<T>(val data: T) : ApiStatus<T>()
    data object Loading : ApiStatus<Nothing>()

    val isSuccessful: Boolean get() = this is Success
}