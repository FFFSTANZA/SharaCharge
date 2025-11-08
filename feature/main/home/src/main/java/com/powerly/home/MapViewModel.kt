package com.SharaSpot.home

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SharaSpot.core.data.model.ActivityResultState
import com.SharaSpot.core.data.model.PermissionsState
import com.SharaSpot.core.data.model.SourcesStatus
import com.SharaSpot.core.data.repositories.PowerSourceRepository
import com.powerly.core.model.location.Target
import com.powerly.core.model.powerly.PowerSource
import com.SharaSpot.lib.managers.UserLocationManager
import com.SharaSpot.lib.usecases.LocationServicesUseCase
import com.SharaSpot.ui.map.initMapViewState
import com.powerly.lib.data.TamilNaduConstants
import org.koin.android.annotation.KoinViewModel
import kotlinx.coroutines.launch


@KoinViewModel
class MyMapViewModel (
    private val powerSourceRepository: PowerSourceRepository,
    private val locationManager: UserLocationManager,
    private val locationServicesUseCase: LocationServicesUseCase
) : ViewModel() {

    val userLocation = mutableStateOf<Target?>(null)

    // Initialize map with Tamil Nadu default location (Chennai)
    // This ensures the map always shows Tamil Nadu region even without user location
    val mapState = initMapViewState(
        initialLocation = userLocation.value ?: TamilNaduConstants.DEFAULT_CENTER,
        zoom = TamilNaduConstants.Zoom.CITY_LEVEL,
        observeLocation = false,
        scrollGesturesEnabled = true,
        zoomGesturesEnabled = true,
        locationEnabled = locationManager.allEnabled
    )


    /**
     * The currently selected [PowerSource] on the map.
     */
    val selectedPowerSource = mutableStateOf<PowerSource?>(null)

    fun setSelectedPowerSource(powerSource: PowerSource?) {
        selectedPowerSource.value = powerSource
    }

    /**
     * A list of [PowerSource]s near the user's current location.
     */
    val nearPowerSources = mutableStateListOf<PowerSource>()
    private val powerSourcesMap = mutableMapOf<String, PowerSource>()


    fun loadPowerSources(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            val result = powerSourceRepository.getNearPowerSources(latitude, longitude)
            when (result) {
                is SourcesStatus.Success -> {
                    displayPowerSources(result.sources)
                }

                else -> {}
            }
        }
    }


    suspend fun loadNearPowerSources() {
        // Use user location if available, otherwise default to Tamil Nadu center (Chennai)
        val target = userLocation.value ?: TamilNaduConstants.DEFAULT_CENTER
        Log.v(TAG, "loadNearPowerSources - (${target.latitude}, ${target.longitude})")
        powerSourcesMap.clear()
        nearPowerSources.clear()
        val result = powerSourceRepository.getNearPowerSources(target.latitude, target.longitude)
        if (result is SourcesStatus.Success) {
            val sources = result.sources
            if (sources.isEmpty()) return
            displayPowerSources(result.sources)
            // sort power sources by distance
            val nearestSource = nearPowerSources.sortedBy { it.distance() }.firstOrNull {
                it.distance() < 10.0 // first ps with distance < 10 km
            } ?: sources.first()                      // or first ps
            mapState.moveCamera(nearestSource.location)
        }
    }

    fun displayPowerSources(powerSources: List<PowerSource>) {
        val userLat = userLocation.value?.latitude
        val userLng = userLocation.value?.longitude
        powerSources.forEach {
            it.distance(userLat, userLng)
            powerSourcesMap[it.id] = it
        }
        nearPowerSources.clear()
        nearPowerSources.addAll(powerSourcesMap.values.toList())
    }

    fun requestLocationServices(
        permissionsState: PermissionsState,
        activityResult: ActivityResultState,
        requestManually: Boolean = false,
        onAllowed: () -> Unit
    ) {
        locationServicesUseCase(
            permissionsState = permissionsState,
            activityResult = activityResult,
            requestManually = requestManually,
            onAllowed = onAllowed
        )
    }

    suspend fun initMap() {
        Log.i(TAG, "initMap")
        mapState.locationEnabled.value = true
        val location = locationManager.requestLocation(forceUpdate = true)
        if (location != null) {
            userLocation.value = location
            mapState.initCenterLocation(location)
        } else {
            // Fallback to Tamil Nadu default location if user location unavailable
            mapState.initCenterLocation(TamilNaduConstants.DEFAULT_CENTER)
        }
    }

    fun updateMapLocation() {
        viewModelScope.launch {
            val location = locationManager.requestLocation(forceUpdate = true)
            if (location != null) mapState.moveCamera(location)
        }
    }

    companion object {
        private const val TAG = "MapViewModel"
    }
}
