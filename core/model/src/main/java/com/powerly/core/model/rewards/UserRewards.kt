package com.powerly.core.model.rewards

/**
 * Represents a user's reward profile
 */
data class UserRewards(
    val userId: String,
    val totalCoins: Int = 0,
    val coinsThisMonth: Int = 0,
    val contributionCount: Int = 0,
    val validationCount: Int = 0,
    val badges: List<String> = emptyList(),
    val rank: RewardRank = RewardRank.BRONZE,
    val dailyValidationCount: Int = 0,
    val lastValidationDate: String? = null, // Format: "YYYY-MM-DD"
    val lastCheckInDate: String? = null, // Format: "YYYY-MM-DD"
    val citiesContributed: Set<String> = emptySet(),
    val chargersContributed: Set<String> = emptySet(),
    val lastUpdated: Long = System.currentTimeMillis()
) {
    /**
     * Get progress to next rank (0.0 to 1.0)
     */
    fun getProgressToNextRank(): Float {
        val currentThreshold = rank.minCoins
        val nextThreshold = rank.nextRank()?.minCoins ?: rank.minCoins

        if (nextThreshold == currentThreshold) return 1.0f

        return ((totalCoins - currentThreshold).toFloat() /
                (nextThreshold - currentThreshold).toFloat()).coerceIn(0f, 1f)
    }

    /**
     * Get coins needed to reach next rank
     */
    fun getCoinsToNextRank(): Int {
        val nextThreshold = rank.nextRank()?.minCoins ?: return 0
        return (nextThreshold - totalCoins).coerceAtLeast(0)
    }

    /**
     * Check if user can earn daily check-in reward
     */
    fun canCheckInToday(): Boolean {
        val today = getCurrentDateString()
        return lastCheckInDate != today
    }

    /**
     * Check if user can validate (daily limit: 10)
     */
    fun canValidateToday(): Boolean {
        val today = getCurrentDateString()
        return lastValidationDate != today || dailyValidationCount < 10
    }

    /**
     * Check if user has earned a specific badge
     */
    fun hasBadge(badge: RewardBadge): Boolean {
        return badges.contains(badge.id)
    }

    private fun getCurrentDateString(): String {
        return java.time.LocalDate.now().toString() // "YYYY-MM-DD"
    }
}

/**
 * Reward rank system based on total coins earned
 */
enum class RewardRank(
    val displayName: String,
    val emoji: String,
    val minCoins: Int,
    val color: String // Hex color for UI
) {
    BRONZE("Bronze", "🥉", 0, "#CD7F32"),
    SILVER("Silver", "🥈", 500, "#C0C0C0"),
    GOLD("Gold", "🥇", 1500, "#FFD700"),
    PLATINUM("Platinum", "💎", 3000, "#E5E4E2");

    companion object {
        /**
         * Get rank based on total coins
         */
        fun fromCoins(coins: Int): RewardRank {
            return when {
                coins >= PLATINUM.minCoins -> PLATINUM
                coins >= GOLD.minCoins -> GOLD
                coins >= SILVER.minCoins -> SILVER
                else -> BRONZE
            }
        }
    }

    /**
     * Get the next rank or null if already at highest
     */
    fun nextRank(): RewardRank? {
        return when (this) {
            BRONZE -> SILVER
            SILVER -> GOLD
            GOLD -> PLATINUM
            PLATINUM -> null
        }
    }
}

/**
 * Achievement badges users can earn
 */
enum class RewardBadge(
    val id: String,
    val displayName: String,
    val description: String,
    val emoji: String,
    val requirement: BadgeRequirement
) {
    FIRST_TIMER(
        "first_timer",
        "First Timer",
        "Make your first contribution",
        "🎉",
        BadgeRequirement.FirstContribution
    ),
    PHOTOGRAPHER(
        "photographer",
        "Photographer",
        "Upload 50 photos",
        "📸",
        BadgeRequirement.PhotoCount(50)
    ),
    REVIEWER_PRO(
        "reviewer_pro",
        "Reviewer Pro",
        "Write 25 reviews",
        "⭐",
        BadgeRequirement.ReviewCount(25)
    ),
    DATA_GUARDIAN(
        "data_guardian",
        "Data Guardian",
        "Validate 100 contributions",
        "🛡️",
        BadgeRequirement.ValidationCount(100)
    ),
    EARLY_BIRD(
        "early_bird",
        "Early Bird",
        "Be the first to add a new charger",
        "🐦",
        BadgeRequirement.FirstToNewCharger
    ),
    COMMUNITY_HERO(
        "community_hero",
        "Community Hero",
        "Earn 1000 EVCoins",
        "🦸",
        BadgeRequirement.TotalCoins(1000)
    ),
    TAMIL_NADU_EXPLORER(
        "tamil_nadu_explorer",
        "Tamil Nadu Explorer",
        "Contribute to 10 different cities",
        "🗺️",
        BadgeRequirement.CitiesCount(10)
    );

    companion object {
        fun fromId(id: String): RewardBadge? {
            return values().find { it.id == id }
        }

        /**
         * Get all badges as a list
         */
        fun getAllBadges(): List<RewardBadge> = values().toList()
    }
}

/**
 * Requirements for earning badges
 */
sealed class BadgeRequirement {
    object FirstContribution : BadgeRequirement()
    object FirstToNewCharger : BadgeRequirement()
    data class PhotoCount(val count: Int) : BadgeRequirement()
    data class ReviewCount(val count: Int) : BadgeRequirement()
    data class ValidationCount(val count: Int) : BadgeRequirement()
    data class TotalCoins(val coins: Int) : BadgeRequirement()
    data class CitiesCount(val cities: Int) : BadgeRequirement()
}
