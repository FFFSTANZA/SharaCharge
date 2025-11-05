package com.SharaSpot.core.data.model

import com.SharaSpot.core.model.SharaSpot.Session
import com.SharaSpot.core.model.util.Message

sealed class SessionStatus {
    data class Error(val msg: Message) : SessionStatus()
    data class Success(val sessions: List<Session>) : SessionStatus()
    data object Loading : SessionStatus()
}