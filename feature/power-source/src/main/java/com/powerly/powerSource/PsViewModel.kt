package com.SharaSpot.powerSource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.SharaSpot.core.data.model.SourceStatus
import com.SharaSpot.core.data.repositories.ContributionRepository
import com.SharaSpot.core.data.repositories.PowerSourceRepository
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.contribution.Contribution
import com.powerly.core.model.contribution.ContributionSummary
import com.powerly.core.model.powerly.Media
import com.powerly.core.model.powerly.PowerSource
import com.powerly.core.model.powerly.Review
import com.SharaSpot.lib.managers.StorageManager
import com.SharaSpot.lib.managers.UserLocationManager
import org.koin.android.annotation.KoinViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


/**
 * ViewModel for managing data related to power sources.
 *
 * This ViewModel is responsible for fetching and providing data related to power sources,
 * including details, reviews, and media. It interacts with the `PowerSourceRepository`
 * to retrieve data and exposes it to composable functions.
 */
@KoinViewModel
class PsViewModel (
    private val powerSourceRepository: PowerSourceRepository,
    private val contributionRepository: ContributionRepository,
    private val locationManager: UserLocationManager,
    private val storageManager: StorageManager
) : ViewModel() {

    private var cachedMedia: Pair<String, List<Media>>? = null

    /**
     * The currently selected or active power source.
     */
    private val _powerSourceStatus = MutableStateFlow<SourceStatus>(SourceStatus.Loading)
    val powerSourceStatus = _powerSourceStatus.asStateFlow()

    var powerSource: PowerSource? = null
        private set

    /**
     * Contribution summary for the current power source
     */
    private val _contributionSummary = MutableStateFlow<ApiStatus<ContributionSummary>>(ApiStatus.Loading())
    val contributionSummary = _contributionSummary.asStateFlow()

    /**
     * All contributions for the current power source
     */
    private val _contributions = MutableStateFlow<List<Contribution>>(emptyList())
    val contributions = _contributions.asStateFlow()

    /**
     * Current logged-in user ID from StorageManager
     */
    val currentUserId: String
        get() = storageManager.userId?.toString() ?: "anonymous"

    fun userLocation() = locationManager.getLastLocation()

    /**
     * Retrieves details for a specific power source.
     *
     * This function fetches power source details from the repository and emits the result as a flow.
     * It handles loading and error states, providing a stream of [ApiStatus] objects.
     */
    fun getPowerSource(
        id: String,
        latitude: Double?,
        longitude: Double?
    ) {
        if (powerSource?.id != id) {
            _powerSourceStatus.value = SourceStatus.Loading
            powerSource = null
        }
        viewModelScope.launch {
            val result = powerSourceRepository.getPowerSource(id)
            if (result is SourceStatus.Success) {
                val powerSource = result.powerSource
                val media = if (cachedMedia?.first == id) {
                    cachedMedia!!.second
                } else {
                    val it = powerSourceRepository.getMedia(id)
                    cachedMedia = Pair(id, it)
                    it
                }
                powerSource.apply {
                    this.currency = storageManager.currency
                    this.media = media
                    this.distance(latitude, longitude)
                }
                this@PsViewModel.powerSource = powerSource
                _powerSourceStatus.emit(SourceStatus.Success(powerSource))

                // Fetch contributions data
                loadContributionData(id)
            } else {
                _powerSourceStatus.emit(result)
            }
        }
    }

    /**
     * Loads contribution data for the charger
     */
    private fun loadContributionData(chargerId: String) {
        viewModelScope.launch {
            // Load contribution summary
            val summaryResult = contributionRepository.getContributionSummary(chargerId)
            _contributionSummary.emit(summaryResult)

            // Load all contributions for filtering/display
            val contributionsResult = contributionRepository.getContributions(chargerId)
            if (contributionsResult is ApiStatus.Success) {
                _contributions.emit(contributionsResult.data)
            }
        }
    }

    /**
     * Refresh contribution data
     */
    fun refreshContributions() {
        powerSource?.id?.let { chargerId ->
            loadContributionData(chargerId)
        }
    }

    /**
     * A flow representing the paginated reviews of a specific power source.
     */
    val reviews: Flow<PagingData<Review>>
        get() = powerSourceRepository.getReviews(
            id = powerSource?.id.orEmpty()
        ).cachedIn(viewModelScope)


    val showOnBoardingDialog: Boolean
        get() {
            val show = storageManager.showOnBoarding
            if (show) storageManager.showOnBoarding = false
            return show
        }

    fun navigateToMap(latitude: Double, longitude: Double) {
        locationManager.navigateToMap(latitude, longitude)
    }
}