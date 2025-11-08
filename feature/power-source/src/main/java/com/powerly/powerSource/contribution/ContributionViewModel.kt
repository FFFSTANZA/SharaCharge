package com.SharaSpot.powerSource.contribution

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SharaSpot.core.data.repositories.ContributionRepository
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.contribution.*
import com.SharaSpot.lib.ImageCompressionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

/**
 * ViewModel for managing contribution-related operations
 */
@KoinViewModel
class ContributionViewModel(
    private val contributionRepository: ContributionRepository,
    private val imageCompressionManager: ImageCompressionManager
) : ViewModel() {

    private val _contributionState = MutableStateFlow<ContributionState>(ContributionState.Idle)
    val contributionState = _contributionState.asStateFlow()

    private val _contributions = MutableStateFlow<List<Contribution>>(emptyList())
    val contributions = _contributions.asStateFlow()

    private val _contributionSummary = MutableStateFlow<ContributionSummary?>(null)
    val contributionSummary = _contributionSummary.asStateFlow()

    /**
     * Creates a photo contribution
     */
    fun addPhotoContribution(
        chargerId: String,
        imageUri: Uri,
        category: PhotoCategory
    ) {
        viewModelScope.launch {
            _contributionState.value = ContributionState.Uploading(0)

            // Compress image
            val compressionResult = imageCompressionManager.compressImage(imageUri)
            if (compressionResult.isFailure) {
                _contributionState.value = ContributionState.Error("Failed to compress image")
                return@launch
            }

            _contributionState.value = ContributionState.Uploading(50)

            // Upload to Firebase Storage
            val uploadResult = contributionRepository.uploadPhoto(
                Uri.fromFile(compressionResult.getOrNull()),
                chargerId
            )

            when (uploadResult) {
                is ApiStatus.Success -> {
                    _contributionState.value = ContributionState.Uploading(75)

                    // Create contribution record
                    val request = CreateContributionRequest(
                        chargerId = chargerId,
                        type = ContributionType.PHOTO,
                        photoUrl = uploadResult.data,
                        photoCategory = category.name
                    )

                    createContribution(request)
                }
                is ApiStatus.Error -> {
                    _contributionState.value = ContributionState.Error(uploadResult.message)
                }
            }

            // Cleanup temp file
            compressionResult.getOrNull()?.delete()
        }
    }

    /**
     * Creates a review contribution
     */
    fun addReview(
        chargerId: String,
        rating: Float,
        comment: String
    ) {
        val request = CreateContributionRequest(
            chargerId = chargerId,
            type = ContributionType.REVIEW,
            rating = rating,
            comment = comment
        )
        createContribution(request)
    }

    /**
     * Reports wait time
     */
    fun reportWaitTime(
        chargerId: String,
        waitTime: WaitTimeOption,
        queueLength: Int?
    ) {
        val request = CreateContributionRequest(
            chargerId = chargerId,
            type = ContributionType.WAIT_TIME,
            waitTimeMinutes = waitTime.minutes,
            queueLength = queueLength
        )
        createContribution(request)
    }

    /**
     * Verifies plug compatibility
     */
    fun verifyPlug(
        chargerId: String,
        plugType: PlugType,
        isWorking: Boolean,
        powerOutput: String?,
        vehicleTested: String?
    ) {
        val request = CreateContributionRequest(
            chargerId = chargerId,
            type = ContributionType.PLUG_CHECK,
            plugType = plugType.name,
            plugWorking = isWorking,
            powerOutput = powerOutput,
            vehicleTested = vehicleTested
        )
        createContribution(request)
    }

    /**
     * Updates charger status
     */
    fun updateStatus(
        chargerId: String,
        status: ChargerStatus
    ) {
        val request = CreateContributionRequest(
            chargerId = chargerId,
            type = ContributionType.STATUS_UPDATE,
            chargerStatus = status
        )
        createContribution(request)
    }

    /**
     * Generic function to create a contribution
     */
    private fun createContribution(request: CreateContributionRequest) {
        viewModelScope.launch {
            _contributionState.value = ContributionState.Submitting

            val result = contributionRepository.createContribution(request)

            when (result) {
                is ApiStatus.Success -> {
                    val contribution = result.data

                    // Award EVCoins
                    contributionRepository.awardEVCoins(
                        contribution.userId,
                        contribution.evCoinsEarned
                    )

                    // Add to local list
                    _contributions.value = listOf(contribution) + _contributions.value

                    _contributionState.value = ContributionState.Success(
                        contribution = contribution,
                        evCoinsEarned = contribution.evCoinsEarned
                    )

                    // Refresh summary
                    loadContributionSummary(request.chargerId)
                }
                is ApiStatus.Error -> {
                    _contributionState.value = ContributionState.Error(result.message)
                }
            }
        }
    }

    /**
     * Loads contributions for a charger
     */
    fun loadContributions(chargerId: String) {
        viewModelScope.launch {
            val result = contributionRepository.getContributions(chargerId)
            if (result is ApiStatus.Success) {
                _contributions.value = result.data
            }
        }
    }

    /**
     * Loads contribution summary
     */
    fun loadContributionSummary(chargerId: String) {
        viewModelScope.launch {
            val result = contributionRepository.getContributionSummary(chargerId)
            if (result is ApiStatus.Success) {
                _contributionSummary.value = result.data
            }
        }
    }

    /**
     * Validates a contribution
     */
    fun validateContribution(contributionId: String) {
        viewModelScope.launch {
            contributionRepository.validateContribution(contributionId)
        }
    }

    /**
     * Resets the contribution state
     */
    fun resetState() {
        _contributionState.value = ContributionState.Idle
    }
}

/**
 * UI state for contribution operations
 */
sealed class ContributionState {
    object Idle : ContributionState()
    object Submitting : ContributionState()
    data class Uploading(val progress: Int) : ContributionState()
    data class Success(val contribution: Contribution, val evCoinsEarned: Int) : ContributionState()
    data class Error(val message: String) : ContributionState()
}
