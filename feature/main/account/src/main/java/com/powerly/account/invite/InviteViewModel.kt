package com.SharaSpot.account.invite

import androidx.lifecycle.ViewModel
import com.SharaSpot.core.network.DeviceHelper
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class InviteViewModel (
    private val deviceHelper: DeviceHelper
) : ViewModel() {
    val appLink: String get() = deviceHelper.appLink
}