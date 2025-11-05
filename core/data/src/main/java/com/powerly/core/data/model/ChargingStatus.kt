package com.SharaSpot.core.data.model

import com.SharaSpot.core.model.SharaSpot.Session
import com.SharaSpot.core.model.util.Message

sealed class ChargingStatus {
    data class Error(val msg: Message) : ChargingStatus()
    data class Success(val session: Session) : ChargingStatus()
    data class Stop(val session: Session) : ChargingStatus()
    data object Loading : ChargingStatus()
}
