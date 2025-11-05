package com.SharaSpot.core.data.model

import com.SharaSpot.core.model.util.Message

sealed class ReviewOptionsStatus {
    data class Error(val msg: Message) : ReviewOptionsStatus()
    data class Success(val reasons: Map<String, List<String>>) : ReviewOptionsStatus()
    data object Loading : ReviewOptionsStatus()
}
