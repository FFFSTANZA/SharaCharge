package com.SharaSpot.core.data.repositories

import android.net.Uri
import com.SharaSpot.core.model.api.ApiStatus
import com.SharaSpot.core.model.contribution.Contribution
import com.SharaSpot.core.model.contribution.ContributionSummary
import com.SharaSpot.core.model.contribution.CreateContributionRequest
import com.powerly.core.model.reliability.ReliabilityScore

interface ContributionRepository {

    /**
     * Creates a new contribution
     *
     * @param request The contribution request data
     * @return [ApiStatus] results containing the created contribution
     */
    suspend fun createContribution(request: CreateContributionRequest): ApiStatus<Contribution>

    /**
     * Gets all contributions for a specific charger
     *
     * @param chargerId The charger ID
     * @return [ApiStatus] results containing list of contributions
     */
    suspend fun getContributions(chargerId: String): ApiStatus<List<Contribution>>

    /**
     * Gets contributions by user
     *
     * @param userId The user ID
     * @return [ApiStatus] results containing list of user's contributions
     */
    suspend fun getUserContributions(userId: String): ApiStatus<List<Contribution>>

    /**
     * Gets contribution summary for a charger
     *
     * @param chargerId The charger ID
     * @return [ApiStatus] results containing contribution summary
     */
    suspend fun getContributionSummary(chargerId: String): ApiStatus<ContributionSummary>

    /**
     * Uploads a photo to Firebase Storage
     *
     * @param imageUri The local image URI
     * @param chargerId The charger ID
     * @return [ApiStatus] results containing the uploaded photo URL
     */
    suspend fun uploadPhoto(imageUri: Uri, chargerId: String): ApiStatus<String>

    /**
     * Validates/confirms a contribution (thumbs up or thumbs down)
     *
     * @param contributionId The contribution ID to validate
     * @param userId The user performing the validation
     * @param isValidation True for validation (thumbs up), false for invalidation (thumbs down)
     * @return [ApiStatus] results containing the updated contribution
     */
    suspend fun validateContribution(
        contributionId: String,
        userId: String,
        isValidation: Boolean
    ): ApiStatus<Contribution>

    /**
     * Check if user has reached daily validation limit
     *
     * @param userId The user ID
     * @return [ApiStatus] results with validation count today
     */
    suspend fun getDailyValidationCount(userId: String): ApiStatus<Int>

    /**
     * Get validation statistics for a user
     *
     * @param userId The user ID
     * @return [ApiStatus] results containing validation stats
     */
    suspend fun getValidationStats(userId: String): ApiStatus<ValidationStats>

    /**
     * Get top validators leaderboard
     *
     * @param limit Number of top validators to return
     * @param period Period for leaderboard (week, month, all-time)
     * @return [ApiStatus] results containing list of top validators
     */
    suspend fun getTopValidators(
        limit: Int = 10,
        period: LeaderboardPeriod = LeaderboardPeriod.WEEK
    ): ApiStatus<List<ValidatorLeaderboardEntry>>

    /**
     * Awards EVCoins to user for contribution
     *
     * @param userId The user ID
     * @param amount The amount of EVCoins to award
     * @return [ApiStatus] results indicating success or failure
     */
    suspend fun awardEVCoins(userId: String, amount: Int): ApiStatus<Boolean>

    /**
     * Calculate reliability score for a charger
     *
     * This calculates the overall reliability score based on:
     * - Photo count (max 20 points)
     * - Review count (max 20 points)
     * - Average rating (max 20 points)
     * - Recent contributions (max 20 points)
     * - Validation confidence (max 20 points)
     *
     * @param chargerId The charger ID
     * @return [ApiStatus] results containing the calculated reliability score
     */
    suspend fun calculateReliabilityScore(chargerId: String): ApiStatus<ReliabilityScore>

    /**
     * Update reliability score in Firebase for a charger
     *
     * @param chargerId The charger ID
     * @param reliabilityScore The calculated reliability score
     * @return [ApiStatus] results indicating success or failure
     */
    suspend fun updateReliabilityScore(
        chargerId: String,
        reliabilityScore: ReliabilityScore
    ): ApiStatus<Boolean>
}

/**
 * Validation statistics for a user
 */
data class ValidationStats(
    val userId: String,
    val totalValidations: Int,
    val validationsThisMonth: Int,
    val validationsThisWeek: Int,
    val validationsToday: Int,
    val evCoinsEarned: Int,
    val hasBadge: Boolean // Data Guardian badge (100+ validations)
)

/**
 * Leaderboard entry for top validators
 */
data class ValidatorLeaderboardEntry(
    val userId: String,
    val userName: String,
    val validationCount: Int,
    val rank: Int,
    val evCoinsEarned: Int
)

/**
 * Leaderboard period
 */
enum class LeaderboardPeriod {
    WEEK,
    MONTH,
    ALL_TIME
}
