package com.SharaSpot.core.data.model

import com.SharaSpot.core.model.location.AppCurrency
import com.SharaSpot.core.model.util.Message

sealed class CurrenciesStatus {
    data class Error(val msg: Message) : CurrenciesStatus()
    data class Success(val data: List<AppCurrency>) : CurrenciesStatus()
    data object Loading : CurrenciesStatus()
}

