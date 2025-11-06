package com.powerly.core.model.rewards

/**
 * Represents a single EVCoins transaction
 */
data class CoinTransaction(
    val id: String,
    val userId: String,
    val amount: Int, // Positive for earned, negative for spent
    val type: TransactionType,
    val reason: String, // Human-readable description, e.g., "Added photo to Shell Charger, Chennai"
    val chargerId: String? = null, // Related charger if applicable
    val chargerName: String? = null,
    val contributionId: String? = null, // Related contribution if applicable
    val timestamp: Long = System.currentTimeMillis(),
    val metadata: Map<String, String> = emptyMap() // Additional context
)

/**
 * Types of coin transactions
 */
enum class TransactionType(val displayName: String, val emoji: String) {
    EARNED("Earned", "✅"),
    SPENT("Spent", "💸"),
    BONUS("Bonus", "🎁"),
    REFUND("Refund", "↩️"),
    ADJUSTMENT("Adjustment", "⚙️");

    companion object {
        fun fromString(value: String): TransactionType? {
            return values().find { it.name == value }
        }
    }
}

/**
 * Summary of transactions for a time period
 */
data class TransactionSummary(
    val totalEarned: Int,
    val totalSpent: Int,
    val totalBonus: Int,
    val netChange: Int,
    val transactionCount: Int,
    val period: TransactionPeriod
)

/**
 * Time period for transaction filtering
 */
enum class TransactionPeriod(val displayName: String) {
    THIS_WEEK("This Week"),
    THIS_MONTH("This Month"),
    ALL_TIME("All Time");

    companion object {
        fun fromString(value: String): TransactionPeriod? {
            return values().find { it.name == value }
        }
    }
}

/**
 * Request to award coins to a user
 */
data class AwardCoinsRequest(
    val userId: String,
    val amount: Int,
    val type: TransactionType,
    val reason: String,
    val chargerId: String? = null,
    val chargerName: String? = null,
    val contributionId: String? = null,
    val metadata: Map<String, String> = emptyMap()
)

/**
 * Result of awarding coins
 */
data class AwardCoinsResult(
    val success: Boolean,
    val transaction: CoinTransaction?,
    val newTotalCoins: Int,
    val badgesEarned: List<RewardBadge> = emptyList(),
    val rankChanged: Boolean = false,
    val newRank: RewardRank? = null,
    val errorMessage: String? = null
)
