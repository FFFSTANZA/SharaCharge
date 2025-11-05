package com.powerly.lib.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.firebase.ktx.Firebase

/**
 * RemoteConfigManager for SharaSpot
 *
 * Manages Firebase Remote Config for dynamic app configuration.
 * Key features:
 * - GST feature flag
 * - Platform fee configuration
 * - Pricing limits
 * - Migration announcements
 */
object RemoteConfigManager {

    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    // Configuration Keys
    private const val KEY_GST_ENABLED = "GST_ENABLED"
    private const val KEY_PLATFORM_FEE_PERCENTAGE = "PLATFORM_FEE_PERCENTAGE"
    private const val KEY_GST_RATE = "GST_RATE"
    private const val KEY_MIN_CHARGING_AMOUNT = "MIN_CHARGING_AMOUNT"
    private const val KEY_MAX_CHARGING_AMOUNT = "MAX_CHARGING_AMOUNT"
    private const val KEY_ENABLE_PLATFORM_FEE = "ENABLE_PLATFORM_FEE"
    private const val KEY_RAZORPAY_KEY_ID = "RAZORPAY_KEY_ID"
    private const val KEY_SHOW_GST_MIGRATION_BANNER = "SHOW_GST_MIGRATION_BANNER"
    private const val KEY_GST_MIGRATION_DATE = "GST_MIGRATION_DATE"
    private const val KEY_RECEIPT_FOOTER_TEXT = "RECEIPT_FOOTER_TEXT"

    // Default values
    private val defaults = mapOf(
        KEY_GST_ENABLED to false,
        KEY_PLATFORM_FEE_PERCENTAGE to 5.0,
        KEY_GST_RATE to 18.0,
        KEY_MIN_CHARGING_AMOUNT to 10.0,
        KEY_MAX_CHARGING_AMOUNT to 10000.0,
        KEY_ENABLE_PLATFORM_FEE to true,
        KEY_RAZORPAY_KEY_ID to "rzp_test_xxxxxx",
        KEY_SHOW_GST_MIGRATION_BANNER to false,
        KEY_GST_MIGRATION_DATE to "",
        KEY_RECEIPT_FOOTER_TEXT to "This is a payment receipt and not a tax invoice. SharaSpot is not registered under GST. All prices shown are inclusive and final."
    )

    /**
     * Initialize Remote Config with default values and settings
     * Call this in Application.onCreate()
     */
    fun initialize() {
        // Set config settings
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600 // 1 hour for production
            // For development/testing, use smaller interval:
            // minimumFetchIntervalInSeconds = 0
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        // Set default values
        remoteConfig.setDefaultsAsync(defaults)

        // Initial fetch and activate
        fetchAndActivate()
    }

    /**
     * Fetch latest config from Firebase and activate
     */
    fun fetchAndActivate(onComplete: ((Boolean) -> Unit)? = null) {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    println("Remote Config fetched. Updated: $updated")
                    onComplete?.invoke(true)
                } else {
                    println("Remote Config fetch failed: ${task.exception?.message}")
                    onComplete?.invoke(false)
                }
            }
    }

    /**
     * Force fetch (bypass cache)
     * Use sparingly to avoid throttling
     */
    fun forceFetch(onComplete: ((Boolean) -> Unit)? = null) {
        remoteConfig.fetch(0)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    remoteConfig.activate()
                    onComplete?.invoke(true)
                } else {
                    onComplete?.invoke(false)
                }
            }
    }

    // ==================== GST Configuration ====================

    /**
     * Check if GST is enabled
     * When true: show GST breakdown, generate tax invoices
     * When false: simple pricing, payment receipts only
     */
    fun isGstEnabled(): Boolean {
        return remoteConfig.getBoolean(KEY_GST_ENABLED)
    }

    /**
     * Get GST rate (as decimal)
     * Example: 18.0% → 0.18
     */
    fun getGstRate(): Double {
        return remoteConfig.getDouble(KEY_GST_RATE) / 100.0
    }

    /**
     * Get GST rate percentage (for display)
     * Example: 18.0
     */
    fun getGstRatePercentage(): Double {
        return remoteConfig.getDouble(KEY_GST_RATE)
    }

    // ==================== Platform Fee Configuration ====================

    /**
     * Check if platform fee should be charged
     */
    fun isPlatformFeeEnabled(): Boolean {
        return remoteConfig.getBoolean(KEY_ENABLE_PLATFORM_FEE)
    }

    /**
     * Get platform fee percentage (as decimal)
     * Example: 5.0% → 0.05
     */
    fun getPlatformFeePercentage(): Double {
        return remoteConfig.getDouble(KEY_PLATFORM_FEE_PERCENTAGE) / 100.0
    }

    /**
     * Get platform fee percentage (for display)
     * Example: 5.0
     */
    fun getPlatformFeePercentageDisplay(): Double {
        return remoteConfig.getDouble(KEY_PLATFORM_FEE_PERCENTAGE)
    }

    // ==================== Pricing Limits ====================

    /**
     * Minimum allowed charging amount in rupees
     */
    fun getMinChargingAmount(): Double {
        return remoteConfig.getDouble(KEY_MIN_CHARGING_AMOUNT)
    }

    /**
     * Maximum allowed charging amount in rupees
     */
    fun getMaxChargingAmount(): Double {
        return remoteConfig.getDouble(KEY_MAX_CHARGING_AMOUNT)
    }

    /**
     * Validate if amount is within allowed limits
     */
    fun isAmountValid(amount: Double): Boolean {
        return amount >= getMinChargingAmount() && amount <= getMaxChargingAmount()
    }

    /**
     * Get validation error message for invalid amount
     */
    fun getAmountValidationError(amount: Double): String? {
        return when {
            amount < getMinChargingAmount() ->
                "Minimum charging amount is ₹${getMinChargingAmount()}"
            amount > getMaxChargingAmount() ->
                "Maximum charging amount is ₹${getMaxChargingAmount()}"
            else -> null
        }
    }

    // ==================== Payment Configuration ====================

    /**
     * Get Razorpay Key ID for payment processing
     */
    fun getRazorpayKeyId(): String {
        return remoteConfig.getString(KEY_RAZORPAY_KEY_ID)
    }

    // ==================== Migration Configuration ====================

    /**
     * Check if GST migration banner should be shown
     */
    fun shouldShowGstMigrationBanner(): Boolean {
        return remoteConfig.getBoolean(KEY_SHOW_GST_MIGRATION_BANNER)
    }

    /**
     * Get GST migration date (ISO 8601 format)
     * Example: "2025-12-31"
     */
    fun getGstMigrationDate(): String {
        return remoteConfig.getString(KEY_GST_MIGRATION_DATE)
    }

    /**
     * Check if there's a pending GST migration
     */
    fun hasPendingGstMigration(): Boolean {
        return shouldShowGstMigrationBanner() && getGstMigrationDate().isNotEmpty()
    }

    // ==================== Receipt Configuration ====================

    /**
     * Get footer text for receipts/invoices
     */
    fun getReceiptFooterText(): String {
        return remoteConfig.getString(KEY_RECEIPT_FOOTER_TEXT)
    }

    // ==================== Utility Methods ====================

    /**
     * Get all current config values (for debugging)
     */
    fun getAllConfigValues(): Map<String, Any> {
        return mapOf(
            "gstEnabled" to isGstEnabled(),
            "platformFeeEnabled" to isPlatformFeeEnabled(),
            "platformFeePercentage" to getPlatformFeePercentageDisplay(),
            "gstRate" to getGstRatePercentage(),
            "minChargingAmount" to getMinChargingAmount(),
            "maxChargingAmount" to getMaxChargingAmount(),
            "razorpayKeyId" to getRazorpayKeyId(),
            "showGstMigrationBanner" to shouldShowGstMigrationBanner(),
            "gstMigrationDate" to getGstMigrationDate()
        )
    }

    /**
     * Print current config to console (for debugging)
     */
    fun printConfig() {
        println("=== SharaSpot Remote Config ===")
        getAllConfigValues().forEach { (key, value) ->
            println("$key: $value")
        }
        println("================================")
    }
}
