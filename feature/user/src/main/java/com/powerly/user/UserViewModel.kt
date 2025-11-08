package com.SharaSpot.user

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SharaSpot.core.data.repositories.AppRepository
import com.powerly.core.model.location.Country
import com.powerly.core.model.user.DeviceBody
import com.SharaSpot.core.network.DeviceHelper
import com.SharaSpot.lib.managers.CountryManager
import com.SharaSpot.lib.managers.NotificationsManager
import com.SharaSpot.lib.managers.StorageManager
import com.SharaSpot.user.reminder.ReminderManager
import org.koin.android.annotation.KoinViewModel
import kotlinx.coroutines.launch


@KoinViewModel
class UserViewModel (
    private val countryManager: CountryManager,
    private val appRepository: AppRepository,
    private val reminderManager: ReminderManager,
    private val notificationsManager: NotificationsManager,
    private val deviceHelper: DeviceHelper,
    private val storageManager: StorageManager
) : ViewModel() {

    val country = mutableStateOf<Country?>(null)
    val isLoggedIn: Boolean get() = storageManager.isLoggedIn

    fun setCountry(country: Country) {
        this.country.value = country
    }

    fun updateDevice() {
        viewModelScope.launch {
            val body = DeviceBody(
                imei = storageManager.imei(),
                lang = storageManager.currentLanguage,
                token = notificationsManager.getToken(),
                appVersion = deviceHelper.appVersion,
                deviceModel = deviceHelper.deviceModel,
                deviceVersion = deviceHelper.deviceVersion,
                deiceType = deviceHelper.deviceType
            )
            appRepository.updateDevice(body)
        }
    }

    fun detectCountry() {
        viewModelScope.launch {
            country.value = countryManager.detectActualCountry()
            Log.v("UserViewModel", "country - ${country.value}")
        }
    }


    fun cancelRegistrationReminders() {
        if (storageManager.isLoggedIn)
            reminderManager.cancelRegistrationReminders()
    }

    fun initRegistrationReminders() {
        if (storageManager.showRegisterNotification) {
            reminderManager.initRegistrationReminders()
            storageManager.showRegisterNotification = false
        }
    }


    val appVersion: String
        get() = String.format(
            "Version : %s %s",
            deviceHelper.buildType,
            deviceHelper.appVersion
        )

    val supportNumber: String get() = deviceHelper.supportNumber

}