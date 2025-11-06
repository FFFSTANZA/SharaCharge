package com.SharaSpot.powerSource.validation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SharaSpot.core.data.repositories.ContributionRepository
import com.SharaSpot.core.data.repositories.LeaderboardPeriod
import com.SharaSpot.core.data.repositories.ValidationStats
import com.SharaSpot.core.data.repositories.ValidatorLeaderboardEntry
import com.SharaSpot.core.model.api.ApiStatus
import com.SharaSpot.core.model.contribution.Contribution
import com.SharaSpot.core.model.contribution.ValidationRewards
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

/**
 * ViewModel for handling contribution validation
 */
@KoinViewModel
class ValidationViewModel(
    private val contributionRepository: ContributionRepository
) : ViewModel() {

    // Current user ID (TODO: Get from auth repository)
    private val currentUserId = "current_user"

    private val _validationState = MutableStateFlow<ValidationState>(ValidationState.Idle)
    val validationState: StateFlow<ValidationState> = _validationState.asStateFlow()

    private val _validationStats = MutableStateFlow<ApiStatus<ValidationStats>>(ApiStatus.Loading)
    val validationStats: StateFlow<ApiStatus<ValidationStats>> = _validationStats.asStateFlow()

    private val _dailyValidationCount = MutableStateFlow(0)
    val dailyValidationCount: StateFlow<Int> = _dailyValidationCount.asStateFlow()

    private val _topValidators = MutableStateFlow<ApiStatus<List<ValidatorLeaderboardEntry>>>(ApiStatus.Loading)
    val topValidators: StateFlow<ApiStatus<List<ValidatorLeaderboardEntry>>> = _topValidators.asStateFlow()

    init {
        loadValidationStats()
        loadDailyValidationCount()
    }

    /**
     * Validate or invalidate a contribution
     */
    fun validateContribution(contributionId: String, isValidation: Boolean) {
        viewModelScope.launch {
            _validationState.value = ValidationState.Validating

            // Check daily limit first
            if (_dailyValidationCount.value >= ValidationRewards.MAX_VALIDATIONS_PER_DAY) {
                _validationState.value = ValidationState.Error("Daily validation limit reached. Come back tomorrow!")
                return@launch
            }

            val result = contributionRepository.validateContribution(
                contributionId = contributionId,
                userId = currentUserId,
                isValidation = isValidation
            )

            when (result) {
                is ApiStatus.Success -> {
                    _validationState.value = ValidationState.Success(
                        contribution = result.data,
                        evCoinsEarned = ValidationRewards.VALIDATION_REWARD,
                        isValidation = isValidation
                    )

                    // Reload stats and count
                    loadValidationStats()
                    loadDailyValidationCount()

                    // Reset to idle after a short delay
                    kotlinx.coroutines.delay(2000)
                    _validationState.value = ValidationState.Idle
                }
                is ApiStatus.Error -> {
                    _validationState.value = ValidationState.Error(result.getMessage())
                }
                else -> {}
            }
        }
    }

    /**
     * Load user's validation statistics
     */
    fun loadValidationStats() {
        viewModelScope.launch {
            _validationStats.value = ApiStatus.Loading
            val result = contributionRepository.getValidationStats(currentUserId)
            _validationStats.value = result
        }
    }

    /**
     * Load daily validation count
     */
    private fun loadDailyValidationCount() {
        viewModelScope.launch {
            val result = contributionRepository.getDailyValidationCount(currentUserId)
            if (result is ApiStatus.Success) {
                _dailyValidationCount.value = result.data
            }
        }
    }

    /**
     * Load top validators leaderboard
     */
    fun loadTopValidators(period: LeaderboardPeriod = LeaderboardPeriod.WEEK, limit: Int = 10) {
        viewModelScope.launch {
            _topValidators.value = ApiStatus.Loading
            val result = contributionRepository.getTopValidators(limit, period)
            _topValidators.value = result
        }
    }

    /**
     * Check if user has reached daily limit
     */
    fun hasReachedDailyLimit(): Boolean {
        return _dailyValidationCount.value >= ValidationRewards.MAX_VALIDATIONS_PER_DAY
    }

    /**
     * Get remaining validations today
     */
    fun getRemainingValidations(): Int {
        return (ValidationRewards.MAX_VALIDATIONS_PER_DAY - _dailyValidationCount.value).coerceAtLeast(0)
    }

    /**
     * Reset validation state
     */
    fun resetState() {
        _validationState.value = ValidationState.Idle
    }
}

/**
 * State for validation operations
 */
sealed class ValidationState {
    object Idle : ValidationState()
    object Validating : ValidationState()
    data class Success(
        val contribution: Contribution,
        val evCoinsEarned: Int,
        val isValidation: Boolean
    ) : ValidationState()
    data class Error(val message: String) : ValidationState()
}
