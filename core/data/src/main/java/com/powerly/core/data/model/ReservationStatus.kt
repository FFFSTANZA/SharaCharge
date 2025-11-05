package com.SharaSpot.core.data.model

import com.SharaSpot.core.model.SharaSpot.Session
import com.SharaSpot.core.model.util.Message

sealed class ReservationStatus {
    data class Error(val msg: Message) : ReservationStatus()
    data object Cancel : ReservationStatus()
    data class Success(val reservation: Session) : ReservationStatus()
    data object Loading : ReservationStatus()
}