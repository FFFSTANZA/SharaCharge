package com.powerly.core.model.contribution

import kotlin.math.max

/**
 * Validation type for different contribution types
 */
enum class ValidationType(val displayName: String, val icon: String) {
    PHOTO_ACCURATE("Is this accurate?", "👍"),
    PHOTO_INACCURATE("Not accurate", "👎"),
    REVIEW_HELPFUL("Helpful", "👍"),
    WAIT_TIME_ACCURATE("Still accurate", "✅"),
    PLUG_CONFIRMED("Confirmed", "✅"),
    STATUS_ACCURATE("Current status correct", "✅")
}

/**
 * Confidence level indicator
 */
enum class ConfidenceLevel(val threshold: Float, val displayName: String, val icon: String) {
    HIGH(0.7f, "Verified", "🟢"),
    MEDIUM(0.4f, "Normal", "🟡"),
    LOW(0.0f, "Needs verification", "🔴");

    companion object {
        fun fromScore(score: Float): ConfidenceLevel {
            return when {
                score >= HIGH.threshold -> HIGH
                score >= MEDIUM.threshold -> MEDIUM
                else -> LOW
            }
        }
    }
}

/**
 * Validation reward constants
 */
object ValidationRewards {
    const val VALIDATION_REWARD = 5 // EVCoins per validation
    const val MAX_VALIDATIONS_PER_DAY = 10 // Prevent spam
}

/**
 * Time decay constants
 */
object TimeDecayConstants {
    const val DECAY_PERIOD_HOURS = 720L // 30 days in hours
    const val STALE_DATA_THRESHOLD_HOURS = 720L // 30 days
    const val WAIT_TIME_STALE_THRESHOLD_HOURS = 2L
    const val PLUG_STATUS_STALE_THRESHOLD_DAYS = 7L
}

/**
 * Calculate confidence score for a contribution
 *
 * Formula:
 * confidenceScore = (validations - invalidations) / (validations + invalidations + 1)
 * timeDecay = 1.0 - (hoursSinceContribution / 720)  // Decay over 30 days
 * finalScore = confidenceScore × timeDecay × 0.7 + (validationCount / 100) × 0.3
 *
 * @param validationCount Number of users who validated
 * @param invalidationCount Number of users who invalidated
 * @param timestamp Contribution timestamp in milliseconds
 * @param currentTime Current time in milliseconds
 * @return Confidence score between 0.0 and 1.0
 */
fun calculateConfidenceScore(
    validationCount: Int,
    invalidationCount: Int,
    timestamp: Long,
    currentTime: Long = System.currentTimeMillis()
): Float {
    // Calculate validation confidence (positive vs negative)
    val totalVotes = validationCount + invalidationCount
    val validationConfidence = if (totalVotes > 0) {
        (validationCount - invalidationCount).toFloat() / (totalVotes + 1)
    } else {
        0.0f
    }

    // Calculate time decay (newer = more confident)
    val hoursSinceContribution = (currentTime - timestamp) / (1000 * 60 * 60).toFloat()
    val timeDecay = max(0.0f, 1.0f - (hoursSinceContribution / TimeDecayConstants.DECAY_PERIOD_HOURS))

    // Combine validation confidence (70%) and validation volume (30%)
    val volumeBonus = (validationCount.toFloat() / 100f).coerceIn(0f, 1f)
    val finalScore = (validationConfidence * timeDecay * 0.7f) + (volumeBonus * 0.3f)

    return finalScore.coerceIn(0.0f, 1.0f)
}

/**
 * Check if data is stale based on contribution type
 */
fun isDataStale(type: ContributionType, timestamp: Long, currentTime: Long = System.currentTimeMillis()): Boolean {
    val hoursSinceContribution = (currentTime - timestamp) / (1000 * 60 * 60)
    return when (type) {
        ContributionType.WAIT_TIME -> hoursSinceContribution > TimeDecayConstants.WAIT_TIME_STALE_THRESHOLD_HOURS
        ContributionType.PLUG_CHECK -> hoursSinceContribution > (TimeDecayConstants.PLUG_STATUS_STALE_THRESHOLD_DAYS * 24)
        ContributionType.STATUS_UPDATE -> hoursSinceContribution > TimeDecayConstants.WAIT_TIME_STALE_THRESHOLD_HOURS
        else -> hoursSinceContribution > TimeDecayConstants.STALE_DATA_THRESHOLD_HOURS
    }
}

/**
 * Format relative time for last validation
 */
fun formatRelativeTime(timestamp: Long, currentTime: Long = System.currentTimeMillis()): String {
    val diff = currentTime - timestamp
    val minutes = diff / (1000 * 60)
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "$minutes minute${if (minutes == 1L) "" else "s"} ago"
        hours < 24 -> "$hours hour${if (hours == 1L) "" else "s"} ago"
        days < 30 -> "$days day${if (days == 1L) "" else "s"} ago"
        else -> "${days / 30} month${if (days / 30 == 1L) "" else "s"} ago"
    }
}

/**
 * Extension function to update contribution with new validation
 */
fun Contribution.withValidation(userId: String, isValidation: Boolean): Contribution {
    // Check if user already validated or invalidated
    val alreadyValidated = userId in validatedBy
    val alreadyInvalidated = userId in invalidatedBy

    // Remove from both lists first
    val newValidatedBy = validatedBy.filter { it != userId }.toMutableList()
    val newInvalidatedBy = invalidatedBy.filter { it != userId }.toMutableList()

    // Add to appropriate list
    if (isValidation) {
        newValidatedBy.add(userId)
    } else {
        newInvalidatedBy.add(userId)
    }

    // Calculate new confidence score
    val newConfidenceScore = calculateConfidenceScore(
        validationCount = newValidatedBy.size,
        invalidationCount = newInvalidatedBy.size,
        timestamp = timestamp
    )

    // Calculate validation count (validated - invalidated)
    val newValidationCount = newValidatedBy.size - newInvalidatedBy.size

    return this.copy(
        validatedBy = newValidatedBy,
        invalidatedBy = newInvalidatedBy,
        validationCount = newValidationCount,
        confidenceScore = newConfidenceScore,
        lastValidatedAt = System.currentTimeMillis()
    )
}

/**
 * Extension function to get confidence level
 */
fun Contribution.getConfidenceLevel(): ConfidenceLevel {
    return ConfidenceLevel.fromScore(confidenceScore)
}

/**
 * Extension function to check if contribution needs validation
 */
fun Contribution.needsValidation(): Boolean {
    return confidenceScore < ConfidenceLevel.MEDIUM.threshold ||
            isDataStale(type, timestamp) ||
            validatedBy.size < 3
}

/**
 * Extension function to get validation prompt message
 */
fun Contribution.getValidationPrompt(): String? {
    val hoursSinceContribution = (System.currentTimeMillis() - timestamp) / (1000 * 60 * 60)

    return when {
        type == ContributionType.WAIT_TIME && hoursSinceContribution > TimeDecayConstants.WAIT_TIME_STALE_THRESHOLD_HOURS -> {
            "Is the wait time still accurate?"
        }
        type == ContributionType.PLUG_CHECK && hoursSinceContribution > (TimeDecayConstants.PLUG_STATUS_STALE_THRESHOLD_DAYS * 24) -> {
            "Are these plugs still working?"
        }
        type == ContributionType.STATUS_UPDATE && hoursSinceContribution > TimeDecayConstants.WAIT_TIME_STALE_THRESHOLD_HOURS -> {
            "Is the status still current?"
        }
        type == ContributionType.PHOTO && validatedBy.size < 3 -> {
            "Is this photo accurate?"
        }
        confidenceScore < ConfidenceLevel.MEDIUM.threshold -> {
            "Help verify this information"
        }
        else -> null
    }
}
