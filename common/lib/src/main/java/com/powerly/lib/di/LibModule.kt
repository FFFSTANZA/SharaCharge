package com.SharaSpot.lib.di

import android.app.Application
import android.content.Context
import com.SharaSpot.core.analytics.EventsManager
import com.SharaSpot.core.analytics.UserIdentifier
import com.SharaSpot.core.analyticsImpl.EventsManagerImpl
import com.SharaSpot.core.network.DeviceHelper
import com.SharaSpot.lib.managers.StorageManager
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.SharaSpot.lib")
class LibModule {
    @Single
    fun provideContext(application: Application): Context = application

    @Single
    fun provideEventsManager(
        applicationContext: Context,
        storageManager: StorageManager,
        deviceHelper: DeviceHelper
    ): EventsManager {
        val identifier: UserIdentifier? = storageManager.userDetails?.let { userDetails ->
            UserIdentifier(
                id = userDetails.id.toString(),
                name = userDetails.fullName,
                email = userDetails.email
            )
        }
        return EventsManagerImpl(
            context = applicationContext,
            isDebug = deviceHelper.isDebug,
            identifier = identifier
        )
    }
}
