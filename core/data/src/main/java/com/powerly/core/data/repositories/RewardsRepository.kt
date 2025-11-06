package com.powerly.core.data.repositories

import com.powerly.core.model.contribution.ContributionType
import com.powerly.core.model.rewards.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing user rewards and EVCoins
 */
interface RewardsRepository {

    /**
     * Get user's reward profile
     */
    suspend fun getUserRewards(userId: String): Result<UserRewards>

    /**
     * Observe user's reward profile for real-time updates
     */
    fun observeUserRewards(userId: String): Flow<UserRewards?>

    /**
     * Award coins to a user for a specific action
     */
    suspend fun awardCoins(request: AwardCoinsRequest): Result<AwardCoinsResult>

    /**
     * Award coins for a contribution (convenience method)
     */
    suspend fun awardCoinsForContribution(
        userId: String,
        contributionType: ContributionType,
        chargerId: String,
        chargerName: String,
        contributionId: String,
        isFirstToCharger: Boolean = false,
        cityName: String? = null
    ): Result<AwardCoinsResult>

    /**
     * Award coins for daily check-in
     */
    suspend fun awardDailyCheckIn(userId: String): Result<AwardCoinsResult>

    /**
     * Award coins for validating a contribution
     */
    suspend fun awardValidationCoins(
        userId: String,
        contributionId: String,
        chargerId: String?,
        chargerName: String?
    ): Result<AwardCoinsResult>

    /**
     * Get user's transaction history
     */
    suspend fun getTransactions(
        userId: String,
        period: TransactionPeriod = TransactionPeriod.ALL_TIME,
        limit: Int = 50
    ): Result<List<CoinTransaction>>

    /**
     * Observe user's transactions for real-time updates
     */
    fun observeTransactions(
        userId: String,
        limit: Int = 50
    ): Flow<List<CoinTransaction>>

    /**
     * Get transaction summary for a period
     */
    suspend fun getTransactionSummary(
        userId: String,
        period: TransactionPeriod
    ): Result<TransactionSummary>

    /**
     * Check and award badges based on user's activity
     */
    suspend fun checkAndAwardBadges(userId: String): Result<List<RewardBadge>>

    /**
     * Get leaderboard of top users by coins
     */
    suspend fun getLeaderboard(limit: Int = 10): Result<List<UserRewards>>

    /**
     * Get user's rank position in leaderboard
     */
    suspend fun getUserRankPosition(userId: String): Result<Int>

    /**
     * Reset daily validation count (called at start of new day)
     */
    suspend fun resetDailyValidationCount(userId: String): Result<Unit>

    /**
     * Increment contribution count
     */
    suspend fun incrementContributionCount(userId: String): Result<Unit>

    /**
     * Add city to user's contributed cities
     */
    suspend fun addContributedCity(userId: String, cityName: String): Result<Unit>

    /**
     * Add charger to user's contributed chargers
     */
    suspend fun addContributedCharger(userId: String, chargerId: String): Result<Unit>

    /**
     * Reset monthly coins (called at start of new month)
     */
    suspend fun resetMonthlyCoins(): Result<Unit>
}
