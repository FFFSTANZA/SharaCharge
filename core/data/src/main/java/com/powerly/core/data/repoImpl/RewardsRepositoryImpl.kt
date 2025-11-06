package com.SharaSpot.core.data.repoImpl

import com.SharaSpot.core.data.repositories.RewardsRepository
import com.powerly.core.model.contribution.ContributionType
import com.powerly.core.model.rewards.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

/**
 * Implementation of RewardsRepository
 *
 * Note: This is a mock implementation that stores data locally.
 * In production, integrate with Firebase Firestore for persistence.
 */
@Single
class RewardsRepositoryImpl(
    @Named("IO") private val ioDispatcher: CoroutineDispatcher,
    // Uncomment when Firebase is available:
    // private val firestore: FirebaseFirestore,
    // private val auth: FirebaseAuth
) : RewardsRepository {

    // In-memory storage for mock implementation
    private val userRewardsMap = mutableMapOf<String, UserRewards>()
    private val transactionsMap = mutableMapOf<String, MutableList<CoinTransaction>>()

    // State flows for reactive updates
    private val userRewardsFlows = mutableMapOf<String, MutableStateFlow<UserRewards?>>()
    private val transactionFlows = mutableMapOf<String, MutableStateFlow<List<CoinTransaction>>>()

    override suspend fun getUserRewards(userId: String): Result<UserRewards> =
        withContext(ioDispatcher) {
            try {
                val rewards = userRewardsMap.getOrPut(userId) {
                    UserRewards(userId = userId)
                }
                Result.success(rewards)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override fun observeUserRewards(userId: String): Flow<UserRewards?> {
        return userRewardsFlows.getOrPut(userId) {
            MutableStateFlow(userRewardsMap[userId])
        }.asStateFlow()
    }

    override suspend fun awardCoins(request: AwardCoinsRequest): Result<AwardCoinsResult> =
        withContext(ioDispatcher) {
            try {
                // Get current rewards
                val currentRewards = userRewardsMap.getOrPut(request.userId) {
                    UserRewards(userId = request.userId)
                }

                val oldRank = currentRewards.rank
                val newTotalCoins = currentRewards.totalCoins + request.amount

                // Create transaction
                val transaction = CoinTransaction(
                    id = UUID.randomUUID().toString(),
                    userId = request.userId,
                    amount = request.amount,
                    type = request.type,
                    reason = request.reason,
                    chargerId = request.chargerId,
                    chargerName = request.chargerName,
                    contributionId = request.contributionId,
                    metadata = request.metadata
                )

                // Save transaction
                transactionsMap.getOrPut(request.userId) { mutableListOf() }.add(0, transaction)

                // Update user rewards
                val updatedRewards = currentRewards.copy(
                    totalCoins = newTotalCoins,
                    coinsThisMonth = currentRewards.coinsThisMonth + request.amount,
                    rank = RewardRank.fromCoins(newTotalCoins),
                    lastUpdated = System.currentTimeMillis()
                )

                userRewardsMap[request.userId] = updatedRewards

                // Update flows
                userRewardsFlows[request.userId]?.value = updatedRewards
                transactionFlows[request.userId]?.value = transactionsMap[request.userId] ?: emptyList()

                // Check for new badges
                val newBadges = checkForNewBadges(updatedRewards, currentRewards)

                // Award badges if any
                if (newBadges.isNotEmpty()) {
                    val badgesAwarded = updatedRewards.copy(
                        badges = updatedRewards.badges + newBadges.map { it.id }
                    )
                    userRewardsMap[request.userId] = badgesAwarded
                    userRewardsFlows[request.userId]?.value = badgesAwarded
                }

                val newRank = updatedRewards.rank
                val rankChanged = oldRank != newRank

                Result.success(
                    AwardCoinsResult(
                        success = true,
                        transaction = transaction,
                        newTotalCoins = newTotalCoins,
                        badgesEarned = newBadges,
                        rankChanged = rankChanged,
                        newRank = if (rankChanged) newRank else null
                    )
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun awardCoinsForContribution(
        userId: String,
        contributionType: ContributionType,
        chargerId: String,
        chargerName: String,
        contributionId: String,
        isFirstToCharger: Boolean,
        cityName: String?
    ): Result<AwardCoinsResult> = withContext(ioDispatcher) {
        try {
            val baseCoins = contributionType.evCoinsReward
            val bonusCoins = if (isFirstToCharger) 50 else 0
            val totalCoins = baseCoins + bonusCoins

            val reason = buildString {
                append(contributionType.displayName)
                append(" for $chargerName")
                if (isFirstToCharger) {
                    append(" (First contribution! +50 bonus)")
                }
            }

            val request = AwardCoinsRequest(
                userId = userId,
                amount = totalCoins,
                type = if (isFirstToCharger) TransactionType.BONUS else TransactionType.EARNED,
                reason = reason,
                chargerId = chargerId,
                chargerName = chargerName,
                contributionId = contributionId,
                metadata = buildMap {
                    put("contributionType", contributionType.name)
                    put("baseCoins", baseCoins.toString())
                    if (bonusCoins > 0) put("bonusCoins", bonusCoins.toString())
                    if (cityName != null) put("cityName", cityName)
                }
            )

            // Award coins
            val result = awardCoins(request).getOrThrow()

            // Increment contribution count
            incrementContributionCount(userId)

            // Add charger to contributed list
            addContributedCharger(userId, chargerId)

            // Add city if provided
            if (cityName != null) {
                addContributedCity(userId, cityName)
            }

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun awardDailyCheckIn(userId: String): Result<AwardCoinsResult> =
        withContext(ioDispatcher) {
            try {
                val currentRewards = userRewardsMap.getOrPut(userId) {
                    UserRewards(userId = userId)
                }

                // Check if already checked in today
                if (!currentRewards.canCheckInToday()) {
                    return@withContext Result.failure(
                        Exception("Already checked in today")
                    )
                }

                val request = AwardCoinsRequest(
                    userId = userId,
                    amount = 5,
                    type = TransactionType.EARNED,
                    reason = "Daily check-in",
                    metadata = mapOf("action" to "daily_checkin")
                )

                val result = awardCoins(request).getOrThrow()

                // Update check-in date
                val updatedRewards = userRewardsMap[userId]?.copy(
                    lastCheckInDate = getCurrentDateString()
                )
                if (updatedRewards != null) {
                    userRewardsMap[userId] = updatedRewards
                    userRewardsFlows[userId]?.value = updatedRewards
                }

                Result.success(result)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun awardValidationCoins(
        userId: String,
        contributionId: String,
        chargerId: String?,
        chargerName: String?
    ): Result<AwardCoinsResult> = withContext(ioDispatcher) {
        try {
            val currentRewards = userRewardsMap.getOrPut(userId) {
                UserRewards(userId = userId)
            }

            // Check daily validation limit
            if (!currentRewards.canValidateToday()) {
                return@withContext Result.failure(
                    Exception("Daily validation limit reached (10/day)")
                )
            }

            val request = AwardCoinsRequest(
                userId = userId,
                amount = 5,
                type = TransactionType.EARNED,
                reason = "Validated contribution${chargerName?.let { " at $it" } ?: ""}",
                chargerId = chargerId,
                chargerName = chargerName,
                contributionId = contributionId,
                metadata = mapOf("action" to "validation")
            )

            val result = awardCoins(request).getOrThrow()

            // Update validation count
            val today = getCurrentDateString()
            val isNewDay = currentRewards.lastValidationDate != today
            val updatedRewards = userRewardsMap[userId]?.copy(
                validationCount = currentRewards.validationCount + 1,
                dailyValidationCount = if (isNewDay) 1 else currentRewards.dailyValidationCount + 1,
                lastValidationDate = today
            )

            if (updatedRewards != null) {
                userRewardsMap[userId] = updatedRewards
                userRewardsFlows[userId]?.value = updatedRewards
            }

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTransactions(
        userId: String,
        period: TransactionPeriod,
        limit: Int
    ): Result<List<CoinTransaction>> = withContext(ioDispatcher) {
        try {
            val allTransactions = transactionsMap[userId] ?: emptyList()

            val filteredTransactions = when (period) {
                TransactionPeriod.THIS_WEEK -> {
                    val weekAgo = getTimestampDaysAgo(7)
                    allTransactions.filter { it.timestamp >= weekAgo }
                }
                TransactionPeriod.THIS_MONTH -> {
                    val monthAgo = getTimestampDaysAgo(30)
                    allTransactions.filter { it.timestamp >= monthAgo }
                }
                TransactionPeriod.ALL_TIME -> allTransactions
            }

            Result.success(filteredTransactions.take(limit))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeTransactions(userId: String, limit: Int): Flow<List<CoinTransaction>> {
        return transactionFlows.getOrPut(userId) {
            MutableStateFlow(transactionsMap[userId]?.take(limit) ?: emptyList())
        }.asStateFlow()
    }

    override suspend fun getTransactionSummary(
        userId: String,
        period: TransactionPeriod
    ): Result<TransactionSummary> = withContext(ioDispatcher) {
        try {
            val transactions = getTransactions(userId, period, Int.MAX_VALUE).getOrThrow()

            val totalEarned = transactions
                .filter { it.type == TransactionType.EARNED && it.amount > 0 }
                .sumOf { it.amount }

            val totalSpent = transactions
                .filter { it.type == TransactionType.SPENT && it.amount < 0 }
                .sumOf { kotlin.math.abs(it.amount) }

            val totalBonus = transactions
                .filter { it.type == TransactionType.BONUS && it.amount > 0 }
                .sumOf { it.amount }

            val netChange = transactions.sumOf { it.amount }

            Result.success(
                TransactionSummary(
                    totalEarned = totalEarned,
                    totalSpent = totalSpent,
                    totalBonus = totalBonus,
                    netChange = netChange,
                    transactionCount = transactions.size,
                    period = period
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkAndAwardBadges(userId: String): Result<List<RewardBadge>> =
        withContext(ioDispatcher) {
            try {
                val currentRewards = userRewardsMap[userId]
                    ?: return@withContext Result.success(emptyList())

                val newBadges = checkForNewBadges(currentRewards, currentRewards)

                if (newBadges.isNotEmpty()) {
                    val updatedRewards = currentRewards.copy(
                        badges = currentRewards.badges + newBadges.map { it.id }
                    )
                    userRewardsMap[userId] = updatedRewards
                    userRewardsFlows[userId]?.value = updatedRewards
                }

                Result.success(newBadges)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getLeaderboard(limit: Int): Result<List<UserRewards>> =
        withContext(ioDispatcher) {
            try {
                val leaderboard = userRewardsMap.values
                    .sortedByDescending { it.totalCoins }
                    .take(limit)
                Result.success(leaderboard)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getUserRankPosition(userId: String): Result<Int> =
        withContext(ioDispatcher) {
            try {
                val sortedUsers = userRewardsMap.values
                    .sortedByDescending { it.totalCoins }
                val position = sortedUsers.indexOfFirst { it.userId == userId } + 1
                Result.success(if (position > 0) position else -1)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun resetDailyValidationCount(userId: String): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                val currentRewards = userRewardsMap[userId] ?: return@withContext Result.success(Unit)
                val updatedRewards = currentRewards.copy(
                    dailyValidationCount = 0,
                    lastValidationDate = getCurrentDateString()
                )
                userRewardsMap[userId] = updatedRewards
                userRewardsFlows[userId]?.value = updatedRewards
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun incrementContributionCount(userId: String): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                val currentRewards = userRewardsMap.getOrPut(userId) {
                    UserRewards(userId = userId)
                }
                val updatedRewards = currentRewards.copy(
                    contributionCount = currentRewards.contributionCount + 1
                )
                userRewardsMap[userId] = updatedRewards
                userRewardsFlows[userId]?.value = updatedRewards
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun addContributedCity(userId: String, cityName: String): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                val currentRewards = userRewardsMap.getOrPut(userId) {
                    UserRewards(userId = userId)
                }
                val updatedRewards = currentRewards.copy(
                    citiesContributed = currentRewards.citiesContributed + cityName
                )
                userRewardsMap[userId] = updatedRewards
                userRewardsFlows[userId]?.value = updatedRewards
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun addContributedCharger(userId: String, chargerId: String): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                val currentRewards = userRewardsMap.getOrPut(userId) {
                    UserRewards(userId = userId)
                }
                val updatedRewards = currentRewards.copy(
                    chargersContributed = currentRewards.chargersContributed + chargerId
                )
                userRewardsMap[userId] = updatedRewards
                userRewardsFlows[userId]?.value = updatedRewards
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun resetMonthlyCoins(): Result<Unit> = withContext(ioDispatcher) {
        try {
            userRewardsMap.keys.forEach { userId ->
                val currentRewards = userRewardsMap[userId] ?: return@forEach
                val updatedRewards = currentRewards.copy(coinsThisMonth = 0)
                userRewardsMap[userId] = updatedRewards
                userRewardsFlows[userId]?.value = updatedRewards
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Helper methods

    private fun checkForNewBadges(
        updatedRewards: UserRewards,
        previousRewards: UserRewards
    ): List<RewardBadge> {
        val newBadges = mutableListOf<RewardBadge>()

        RewardBadge.values().forEach { badge ->
            // Skip if already earned
            if (updatedRewards.hasBadge(badge)) return@forEach

            val earned = when (badge.requirement) {
                is BadgeRequirement.FirstContribution ->
                    updatedRewards.contributionCount >= 1

                is BadgeRequirement.FirstToNewCharger ->
                    false // This is awarded explicitly when first to contribute to a charger

                is BadgeRequirement.PhotoCount -> {
                    // Note: We'd need to track photo count separately
                    // For now, use contribution count as proxy
                    false
                }

                is BadgeRequirement.ReviewCount -> {
                    // Note: We'd need to track review count separately
                    false
                }

                is BadgeRequirement.ValidationCount ->
                    updatedRewards.validationCount >= badge.requirement.count

                is BadgeRequirement.TotalCoins ->
                    updatedRewards.totalCoins >= badge.requirement.coins

                is BadgeRequirement.CitiesCount ->
                    updatedRewards.citiesContributed.size >= badge.requirement.cities
            }

            if (earned) {
                newBadges.add(badge)
            }
        }

        return newBadges
    }

    private fun getCurrentDateString(): String {
        return LocalDate.now().toString() // "YYYY-MM-DD"
    }

    private fun getTimestampDaysAgo(days: Int): Long {
        return LocalDateTime.now()
            .minusDays(days.toLong())
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }
}
