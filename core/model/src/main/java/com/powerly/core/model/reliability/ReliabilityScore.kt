package com.powerly.core.model.reliability

import com.powerly.core.model.contribution.Contribution
import com.powerly.core.model.contribution.ContributionType
import kotlin.math.min

/**
 * Reliability Score Calculation for EV Chargers
 *
 * Calculates a comprehensive reliability score (0-100) based on:
 * 1. Photo count (max 20 points)
 * 2. Review count (max 20 points)
 * 3. Average rating (max 20 points)
 * 4. Recent contributions (max 20 points)
 * 5. Validation confidence (max 20 points)
 */

/**
 * Data class representing the reliability score breakdown
 */
data class ReliabilityScore(
    val totalScore: Float = 0f,  // 0-100
    val photoScore: Float = 0f,  // 0-20
    val reviewScore: Float = 0f,  // 0-20
    val ratingScore: Float = 0f,  // 0-20
    val freshnessScore: Float = 0f,  // 0-20 (recent contributions)
    val validationScore: Float = 0f,  // 0-20 (average confidence)
    val lastUpdated: Long = System.currentTimeMillis()
) {
    /**
     * Get star rating (1-5 stars)
     */
    val starRating: Int
        get() = when {
            totalScore >= 90 -> 5
            totalScore >= 75 -> 4
            totalScore >= 50 -> 3
            totalScore >= 25 -> 2
            totalScore > 0 -> 1
            else -> 0
        }

    /**
     * Get trust badge
     */
    val trustBadge: TrustBadge
        get() = TrustBadge.fromScore(totalScore)

    /**
     * Get breakdown level for each component
     */
    fun getPhotoLevel(): ScoreLevel = ScoreLevel.fromScore(photoScore, 20f)
    fun getReviewLevel(): ScoreLevel = ScoreLevel.fromScore(reviewScore, 20f)
    fun getRatingLevel(): ScoreLevel = ScoreLevel.fromScore(ratingScore, 20f)
    fun getFreshnessLevel(): ScoreLevel = ScoreLevel.fromScore(freshnessScore, 20f)
    fun getValidationLevel(): ScoreLevel = ScoreLevel.fromScore(validationScore, 20f)
}

/**
 * Trust badge levels
 */
enum class TrustBadge(val displayName: String, val icon: String) {
    EXCELLENT("Community Trusted", "⭐"),
    GOOD("Verified", "✅"),
    FAIR("Community Reviewed", "👥"),
    NEEDS_DATA("Needs Reviews", "📝");

    companion object {
        fun fromScore(score: Float): TrustBadge {
            return when {
                score >= 80 -> EXCELLENT
                score >= 60 -> GOOD
                score >= 40 -> FAIR
                else -> NEEDS_DATA
            }
        }
    }
}

/**
 * Score level for breakdown display
 */
enum class ScoreLevel(val displayName: String, val icon: String) {
    EXCELLENT("Excellent", "🟢"),
    GOOD("Good", "🟡"),
    FAIR("Fair", "🟠"),
    POOR("Poor", "🔴"),
    NONE("No Data", "⚪");

    companion object {
        fun fromScore(score: Float, maxScore: Float): ScoreLevel {
            val percentage = (score / maxScore) * 100
            return when {
                percentage >= 80 -> EXCELLENT
                percentage >= 60 -> GOOD
                percentage >= 40 -> FAIR
                percentage > 0 -> POOR
                else -> NONE
            }
        }
    }
}

/**
 * Calculate reliability score for a charger
 *
 * @param photoCount Number of community photos
 * @param reviewCount Number of reviews
 * @param avgRating Average rating (0-5)
 * @param contributions List of all contributions
 * @return ReliabilityScore with breakdown
 */
fun calculateReliabilityScore(
    photoCount: Int,
    reviewCount: Int,
    avgRating: Double,
    contributions: List<Contribution>
): ReliabilityScore {
    // 1. Photo score (max 20): photos / 5 × 20
    val photoScore = min((photoCount / 5f) * 20f, 20f)

    // 2. Review score (max 20): reviews / 10 × 20
    val reviewScore = min((reviewCount / 10f) * 20f, 20f)

    // 3. Rating score (max 20): avgRating × 4
    val ratingScore = (avgRating * 4).toFloat().coerceIn(0f, 20f)

    // 4. Freshness score (max 20): contributions in last 7 days / 5 × 20
    val recentContribs = countRecentContributions(contributions, days = 7)
    val freshnessScore = min((recentContribs / 5f) * 20f, 20f)

    // 5. Validation score (max 20): avg confidence × 20
    val avgConfidence = calculateAverageConfidence(contributions)
    val validationScore = (avgConfidence * 20f).coerceIn(0f, 20f)

    // Calculate total score (average of all components)
    val totalScore = (photoScore + reviewScore + ratingScore + freshnessScore + validationScore)

    return ReliabilityScore(
        totalScore = totalScore,
        photoScore = photoScore,
        reviewScore = reviewScore,
        ratingScore = ratingScore,
        freshnessScore = freshnessScore,
        validationScore = validationScore,
        lastUpdated = System.currentTimeMillis()
    )
}

/**
 * Count contributions in the last N days
 */
private fun countRecentContributions(
    contributions: List<Contribution>,
    days: Int = 7,
    currentTime: Long = System.currentTimeMillis()
): Int {
    val cutoffTime = currentTime - (days * 24 * 60 * 60 * 1000L)
    return contributions.count { it.timestamp >= cutoffTime }
}

/**
 * Calculate average confidence score across all contributions
 */
private fun calculateAverageConfidence(contributions: List<Contribution>): Float {
    if (contributions.isEmpty()) return 0f

    // Filter out very old contributions (older than 30 days)
    val recentContribs = contributions.filter { contribution ->
        val daysSince = (System.currentTimeMillis() - contribution.timestamp) / (1000 * 60 * 60 * 24)
        daysSince <= 30
    }

    if (recentContribs.isEmpty()) return 0f

    val totalConfidence = recentContribs.sumOf { it.confidenceScore.toDouble() }
    return (totalConfidence / recentContribs.size).toFloat()
}

/**
 * Calculate reliability score from basic metrics (without contributions)
 * Used when contributions data is not available
 */
fun calculateBasicReliabilityScore(
    photoCount: Int,
    reviewCount: Int,
    avgRating: Double
): ReliabilityScore {
    // Photo score (max 20)
    val photoScore = min((photoCount / 5f) * 20f, 20f)

    // Review score (max 20)
    val reviewScore = min((reviewCount / 10f) * 20f, 20f)

    // Rating score (max 20)
    val ratingScore = (avgRating * 4).toFloat().coerceIn(0f, 20f)

    // No data for freshness and validation
    val freshnessScore = 0f
    val validationScore = 0f

    // Calculate total (weighted - only count available components)
    val totalScore = photoScore + reviewScore + ratingScore

    return ReliabilityScore(
        totalScore = totalScore,
        photoScore = photoScore,
        reviewScore = reviewScore,
        ratingScore = ratingScore,
        freshnessScore = freshnessScore,
        validationScore = validationScore,
        lastUpdated = System.currentTimeMillis()
    )
}

/**
 * Extension function to format reliability score for display
 */
fun ReliabilityScore.toDisplayString(): String {
    return "${String.format("%.1f", totalScore)}/100"
}

/**
 * Extension function to get star display
 */
fun ReliabilityScore.toStarDisplay(): String {
    val filledStars = "⭐".repeat(starRating)
    val emptyStars = "☆".repeat(5 - starRating)
    return "$filledStars$emptyStars ($starRating/5)"
}
