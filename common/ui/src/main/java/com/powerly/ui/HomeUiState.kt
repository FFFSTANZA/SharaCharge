package com.SharaSpot.ui

import androidx.compose.runtime.mutableStateOf
import com.SharaSpot.core.network.DeviceHelper

class HomeUiState(deviceHelper: DeviceHelper? = null) {
    val appVersion = String.format(
        "Version : %s %s", deviceHelper?.buildType, deviceHelper?.appVersion
    )
    val languageCode = mutableStateOf("en")
    val languageName = mutableStateOf("english")
    val isLoggedIn = mutableStateOf(false)
    val supportMap = mutableStateOf(deviceHelper?.supportMap ?: false)
    val userName = mutableStateOf("")
    val balance = mutableStateOf("0.00")
    val currency = mutableStateOf("INR")
}

