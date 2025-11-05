package com.SharaSpot.core.data.model

import com.SharaSpot.core.model.SharaSpot.Media
import com.SharaSpot.core.model.util.Message

sealed class MediaStatus {
    data class Error(val msg: Message) : MediaStatus()
    data class Success(
        val media: List<Media>,
        val upload: Boolean = false,
        val remove: Boolean = false
    ) : MediaStatus()

    data object Loading : MediaStatus()
}
