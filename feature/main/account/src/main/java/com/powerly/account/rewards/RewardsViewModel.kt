package com.SharaSpot.account.rewards

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SharaSpot.core.data.repositories.RewardsRepository
import com.powerly.core.model.contribution.ContributionType
import com.powerly.core.model.rewards.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

/**
 * ViewModel for managing user rewards and EVCoins
 */
@KoinViewModel
class RewardsViewModel(
    private val rewardsRepository: RewardsRepository
) : ViewModel() {

    // UI State
    val userRewards = mutableStateOf<UserRewards?>(null)
    val transactions = mutableStateOf<List<CoinTransaction>>(emptyList())
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    val successMessage = mutableStateOf<String?>(null)

    // Recent coin award animation state
    val recentCoinAward = mutableStateOf<CoinAwardAnimation?>(null)

    // Transaction filter
    val selectedPeriod = mutableStateOf(TransactionPeriod.ALL_TIME)

    /**
     * Load user rewards for a given user ID
     */
    fun loadUserRewards(userId: String) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            rewardsRepository.getUserRewards(userId)
                .onSuccess { rewards ->
                    userRewards.value = rewards
                    Log.d(TAG, "Loaded rewards: $rewards")
                }
                .onFailure { error ->
                    errorMessage.value = error.message ?: "Failed to load rewards"
                    Log.e(TAG, "Error loading rewards", error)
                }

            isLoading.value = false
        }
    }

    /**
     * Observe user rewards for real-time updates
     */
    fun observeUserRewards(userId: String): StateFlow<UserRewards?> {
        return rewardsRepository.observeUserRewards(userId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
    }

    /**
     * Load transaction history
     */
    fun loadTransactions(userId: String, period: TransactionPeriod = TransactionPeriod.ALL_TIME) {
        viewModelScope.launch {
            rewardsRepository.getTransactions(userId, period)
                .onSuccess { txns ->
                    transactions.value = txns
                    selectedPeriod.value = period
                    Log.d(TAG, "Loaded ${txns.size} transactions for $period")
                }
                .onFailure { error ->
                    errorMessage.value = error.message ?: "Failed to load transactions"
                    Log.e(TAG, "Error loading transactions", error)
                }
        }
    }

    /**
     * Award coins for a contribution with animation
     */
    fun awardCoinsForContribution(
        userId: String,
        contributionType: ContributionType,
        chargerId: String,
        chargerName: String,
        contributionId: String,
        isFirstToCharger: Boolean = false,
        cityName: String? = null
    ) {
        viewModelScope.launch {
            rewardsRepository.awardCoinsForContribution(
                userId = userId,
                contributionType = contributionType,
                chargerId = chargerId,
                chargerName = chargerName,
                contributionId = contributionId,
                isFirstToCharger = isFirstToCharger,
                cityName = cityName
            ).onSuccess { result ->
                // Update rewards state
                userRewards.value = userRewards.value?.copy(
                    totalCoins = result.newTotalCoins
                )

                // Trigger coin animation
                showCoinAnimation(
                    amount = result.transaction?.amount ?: 0,
                    reason = result.transaction?.reason ?: "",
                    badgesEarned = result.badgesEarned,
                    rankChanged = result.rankChanged,
                    newRank = result.newRank
                )

                // Reload full rewards
                loadUserRewards(userId)
                loadTransactions(userId, selectedPeriod.value)

                Log.d(TAG, "Awarded coins: ${result.transaction?.amount}")
            }.onFailure { error ->
                errorMessage.value = error.message ?: "Failed to award coins"
                Log.e(TAG, "Error awarding coins", error)
            }
        }
    }

    /**
     * Award daily check-in coins
     */
    fun claimDailyCheckIn(userId: String) {
        viewModelScope.launch {
            isLoading.value = true

            rewardsRepository.awardDailyCheckIn(userId)
                .onSuccess { result ->
                    showCoinAnimation(
                        amount = result.transaction?.amount ?: 0,
                        reason = "Daily check-in",
                        badgesEarned = result.badgesEarned
                    )
                    loadUserRewards(userId)
                    loadTransactions(userId, selectedPeriod.value)
                    successMessage.value = "Daily check-in complete! +5 EVCoins"
                }
                .onFailure { error ->
                    errorMessage.value = error.message ?: "Failed to claim daily check-in"
                }

            isLoading.value = false
        }
    }

    /**
     * Award validation coins
     */
    fun awardValidationCoins(
        userId: String,
        contributionId: String,
        chargerId: String?,
        chargerName: String?
    ) {
        viewModelScope.launch {
            rewardsRepository.awardValidationCoins(
                userId = userId,
                contributionId = contributionId,
                chargerId = chargerId,
                chargerName = chargerName
            ).onSuccess { result ->
                showCoinAnimation(
                    amount = result.transaction?.amount ?: 0,
                    reason = "Validation",
                    badgesEarned = result.badgesEarned
                )
                loadUserRewards(userId)
                loadTransactions(userId, selectedPeriod.value)
            }.onFailure { error ->
                errorMessage.value = error.message ?: "Validation limit reached"
            }
        }
    }

    /**
     * Get transaction summary for a period
     */
    fun getTransactionSummary(userId: String, period: TransactionPeriod) {
        viewModelScope.launch {
            rewardsRepository.getTransactionSummary(userId, period)
                .onSuccess { summary ->
                    Log.d(TAG, "Summary for $period: $summary")
                }
                .onFailure { error ->
                    Log.e(TAG, "Error getting summary", error)
                }
        }
    }

    /**
     * Show coin earning animation
     */
    private fun showCoinAnimation(
        amount: Int,
        reason: String,
        badgesEarned: List<RewardBadge> = emptyList(),
        rankChanged: Boolean = false,
        newRank: RewardRank? = null
    ) {
        recentCoinAward.value = CoinAwardAnimation(
            amount = amount,
            reason = reason,
            badgesEarned = badgesEarned,
            rankChanged = rankChanged,
            newRank = newRank,
            timestamp = System.currentTimeMillis()
        )

        // Auto-clear after animation duration
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000) // 3 seconds
            if (recentCoinAward.value?.timestamp == recentCoinAward.value?.timestamp) {
                recentCoinAward.value = null
            }
        }
    }

    /**
     * Dismiss coin animation
     */
    fun dismissCoinAnimation() {
        recentCoinAward.value = null
    }

    /**
     * Clear messages
     */
    fun clearMessages() {
        errorMessage.value = null
        successMessage.value = null
    }

    companion object {
        private const val TAG = "RewardsViewModel"
    }
}

/**
 * Data class for coin award animation state
 */
data class CoinAwardAnimation(
    val amount: Int,
    val reason: String,
    val badgesEarned: List<RewardBadge>,
    val rankChanged: Boolean,
    val newRank: RewardRank?,
    val timestamp: Long
)
