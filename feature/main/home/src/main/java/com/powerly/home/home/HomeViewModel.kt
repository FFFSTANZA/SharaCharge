package com.SharaSpot.home.home

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import com.SharaSpot.core.data.model.SourceStatus
import com.SharaSpot.core.data.repositories.PowerSourceRepository
import com.SharaSpot.core.model.SharaSpot.PowerSource
import com.SharaSpot.core.network.DeviceHelper
import com.SharaSpot.lib.CONSTANTS.POWER_SOURCE_ID
import com.SharaSpot.lib.managers.StorageManager
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class HomeViewModel (
    private val powerSourceRepository: PowerSourceRepository,
    private val deviceHelper: DeviceHelper,
    private val storageManager: StorageManager
) : ViewModel() {

    val isLoggedIn: Boolean get() = storageManager.isLoggedIn

    suspend fun getPowerSourceFromDeepLink(intent: Intent): PowerSource? {
        return if (intent.hasExtra(POWER_SOURCE_ID)) {
            val identifier = intent.getStringExtra(POWER_SOURCE_ID).orEmpty()
            Log.i(TAG, "deep-link-identifier - $identifier")
            intent.removeExtra(POWER_SOURCE_ID)
            val it = powerSourceRepository.getPowerSourceByIdentifier(identifier)
            if (it is SourceStatus.Success) it.powerSource
            else null
        } else null
    }

    fun getSupportNumber(): String? = deviceHelper.supportNumber

    companion object {
        private const val TAG = "HomeViewModel"
    }
}