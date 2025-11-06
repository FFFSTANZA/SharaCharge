package com.SharaSpot.core.data.repositories

import android.net.Uri
import com.SharaSpot.core.model.api.ApiStatus
import com.SharaSpot.core.model.contribution.Contribution
import com.SharaSpot.core.model.contribution.ContributionSummary
import com.SharaSpot.core.model.contribution.CreateContributionRequest

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
     * Validates/confirms a contribution
     *
     * @param contributionId The contribution ID to validate
     * @return [ApiStatus] results indicating success or failure
     */
    suspend fun validateContribution(contributionId: String): ApiStatus<Boolean>

    /**
     * Awards EVCoins to user for contribution
     *
     * @param userId The user ID
     * @param amount The amount of EVCoins to award
     * @return [ApiStatus] results indicating success or failure
     */
    suspend fun awardEVCoins(userId: String, amount: Int): ApiStatus<Boolean>
}
