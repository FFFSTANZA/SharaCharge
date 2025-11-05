package com.SharaSpot.home.home

import com.SharaSpot.core.model.SharaSpot.PowerSource
import com.SharaSpot.ui.dialogs.signIn.SignInOptions

internal sealed class HomeEvents {
    data object SliderClick : HomeEvents()
    data class OpenPowerSource(val source: PowerSource) : HomeEvents()
    data class OpenMap(val source: PowerSource? = null) : HomeEvents()
    data object RequestLocation : HomeEvents()
    data object OnLogin : HomeEvents()
    data object OnSupport : HomeEvents()
    data object OnBalance : HomeEvents()
}

sealed class NavigationEvents {
    data class Map(val source: PowerSource?) : NavigationEvents()
    data class SourceDetails(val source: PowerSource) : NavigationEvents()
    data class Login(val options: SignInOptions) : NavigationEvents()
    data object Balance : NavigationEvents()
    data object Support : NavigationEvents()
}

