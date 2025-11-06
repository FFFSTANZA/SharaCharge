package com.powerly.core.model.contribution

/**
 * Represents a user contribution to a charging station
 */
data class Contribution(
    val id: String,
    val chargerId: String,
    val userId: String,
    val userName: String,
    val type: ContributionType,
    val timestamp: Long,
    val evCoinsEarned: Int,

    // Type-specific data
    val photoUrl: String? = null,
    val photoCategory: String? = null, // "charger", "plug", "parking", "location"
    val rating: Float? = null,
    val comment: String? = null,
    val waitTimeMinutes: Int? = null,
    val queueLength: Int? = null, // Number of vehicles waiting
    val plugType: String? = null, // "CCS", "CHAdeMO", "Type 2", "Bharat DC", "Type 1"
    val plugWorking: Boolean? = null,
    val powerOutput: String? = null, // "3.3kW", "7kW", "50kW", etc.
    val vehicleTested: String? = null, // Optional vehicle model
    val chargerStatus: ChargerStatus? = null,

    // Validation fields
    val validationCount: Int = 0, // Total validations (validated - invalidated)
    val validatedBy: List<String> = emptyList(), // UserIds who validated (thumbs up)
    val invalidatedBy: List<String> = emptyList(), // UserIds who invalidated (thumbs down)
    val confidenceScore: Float = 0.0f, // 0.0 to 1.0 based on validations and time decay
    val lastValidatedAt: Long = 0L // Most recent validation timestamp
)

/**
 * Types of contributions users can make
 */
enum class ContributionType(val displayName: String, val evCoinsReward: Int, val icon: String) {
    PHOTO("Add Photo", 20, "📸"),
    REVIEW("Write Review", 30, "⭐"),
    WAIT_TIME("Report Wait Time", 10, "⏱️"),
    PLUG_CHECK("Verify Plug", 25, "🔌"),
    STATUS_UPDATE("Update Status", 15, "✅");

    companion object {
        fun fromString(value: String): ContributionType? {
            return values().find { it.name == value }
        }
    }
}

/**
 * Current status of a charging station
 */
enum class ChargerStatus(val displayName: String) {
    AVAILABLE("Available"),
    BUSY("Busy"),
    NOT_WORKING("Not Working"),
    MAINTENANCE("Under Maintenance"),
    UNKNOWN("Unknown");

    companion object {
        fun fromString(value: String): ChargerStatus? {
            return values().find { it.name == value }
        }
    }
}

/**
 * Wait time option for quick selection
 */
enum class WaitTimeOption(val minutes: Int, val displayName: String) {
    ZERO(0, "0 min"),
    FIVE(5, "5 min"),
    TEN(10, "10 min"),
    FIFTEEN(15, "15 min"),
    THIRTY_PLUS(30, "30+ min");

    companion object {
        fun fromMinutes(minutes: Int): WaitTimeOption {
            return when {
                minutes == 0 -> ZERO
                minutes <= 5 -> FIVE
                minutes <= 10 -> TEN
                minutes <= 15 -> FIFTEEN
                else -> THIRTY_PLUS
            }
        }
    }
}

/**
 * Plug type supported by charging stations
 */
enum class PlugType(val displayName: String) {
    CCS("CCS"),
    CHADEMO("CHAdeMO"),
    TYPE_2("Type 2"),
    BHARAT_DC("Bharat DC"),
    TYPE_1("Type 1");

    companion object {
        fun fromString(value: String): PlugType? {
            return values().find { it.name == value || it.displayName == value }
        }
    }
}

/**
 * Photo category for contributions
 */
enum class PhotoCategory(val displayName: String) {
    CHARGER("Charger"),
    PLUG("Plug"),
    PARKING("Parking"),
    LOCATION("Location"),
    OTHER("Other");

    companion object {
        fun fromString(value: String): PhotoCategory? {
            return values().find { it.name == value || it.displayName == value }
        }
    }
}

/**
 * Data class for creating a new contribution
 */
data class CreateContributionRequest(
    val chargerId: String,
    val type: ContributionType,

    // Optional fields based on contribution type
    val photoUrl: String? = null,
    val photoCategory: String? = null,
    val rating: Float? = null,
    val comment: String? = null,
    val waitTimeMinutes: Int? = null,
    val queueLength: Int? = null,
    val plugType: String? = null,
    val plugWorking: Boolean? = null,
    val powerOutput: String? = null,
    val vehicleTested: String? = null,
    val chargerStatus: ChargerStatus? = null
)

/**
 * Summary of contributions for a charging station
 */
data class ContributionSummary(
    val chargerId: String,
    val totalContributions: Int,
    val totalPhotos: Int,
    val averageRating: Float?,
    val latestWaitTime: WaitTimeInfo?,
    val currentStatus: ChargerStatus?,
    val plugStatusList: List<PlugStatusInfo>,
    val recentContributions: List<Contribution>
)

/**
 * Latest wait time information
 */
data class WaitTimeInfo(
    val minutes: Int,
    val queueLength: Int?,
    val timestamp: Long,
    val isStale: Boolean // True if older than 2 hours
) {
    companion object {
        const val STALE_THRESHOLD_MILLIS = 2 * 60 * 60 * 1000L // 2 hours
    }
}

/**
 * Plug compatibility status
 */
data class PlugStatusInfo(
    val plugType: String,
    val isWorking: Boolean,
    val powerOutput: String?,
    val lastVerified: Long,
    val verificationCount: Int
)
