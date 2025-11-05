package com.SharaSpot.core.analyticsImpl

import android.content.Context
import com.SharaSpot.core.analytics.EventsManager
import com.SharaSpot.core.analytics.UserIdentifier
import org.koin.core.annotation.Single

@Single
class EventsManagerImpl(
    private val context: Context,
    private val isDebug: Boolean,
    private val identifier: UserIdentifier?
) : EventsManager {
    override fun log(event: String, key: String?, value: String?) {
    }

    override fun updatePushToken(token: String) {
    }

}
