package com.SharaSpot.core.data.model

import com.SharaSpot.core.model.SharaSpot.PowerSource
import com.SharaSpot.core.model.util.Message

sealed class SourcesStatus {
    data class Error(val msg: Message) : SourcesStatus()
    data class Success(val sources: List<PowerSource>) : SourcesStatus()
    data object Loading : SourcesStatus()
}

sealed class SourceStatus {
    data class Error(val msg: Message) : SourceStatus()
    data class Success(val powerSource: PowerSource) : SourceStatus()
    data object Loading : SourceStatus()
}
