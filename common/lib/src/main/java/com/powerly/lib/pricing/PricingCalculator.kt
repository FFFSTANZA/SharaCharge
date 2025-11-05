package com.powerly.lib.pricing

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.round

/**
 * PricingCalculator for SharaSpot
 *
 * Simple, transparent pricing model without GST registration.
 * All prices are inclusive with no hidden fees.
 *
 * Business Model:
 * - SharaSpot operates below ₹40 lakhs threshold (no GST registration required)
 * - All pricing is inclusive and transparent
 * - Cannot issue GST invoices
 * - Provides payment receipts only
 */
object PricingCalculator {

    /**
     * Calculate the base charging cost based on energy consumed and rate per kWh
     *
     * @param energyKwh Energy consumed in kilowatt-hours
     * @param ratePerKwh Rate per kilowatt-hour in rupees
     * @return Charging cost in rupees (rounded to 2 decimal places)
     */
    fun calculateChargingCost(energyKwh: Double, ratePerKwh: Double): Double {
        require(energyKwh >= 0) { "Energy consumed cannot be negative" }
        require(ratePerKwh >= 0) { "Rate per kWh cannot be negative" }

        val cost = energyKwh * ratePerKwh
        return roundToTwoDecimals(cost)
    }

    /**
     * Calculate platform fee (SharaSpot commission)
     *
     * @param amount Base amount to calculate fee on
     * @param feePercent Fee percentage (default 5% = 0.05)
     * @return Platform fee in rupees (rounded to 2 decimal places)
     */
    fun calculatePlatformFee(amount: Double, feePercent: Double = 0.05): Double {
        require(amount >= 0) { "Amount cannot be negative" }
        require(feePercent >= 0 && feePercent <= 1) { "Fee percent must be between 0 and 1" }

        val fee = amount * feePercent
        return roundToTwoDecimals(fee)
    }

    /**
     * Calculate total amount including all charges
     *
     * @param chargingCost Base charging cost
     * @param platformFee Platform commission fee
     * @return Total amount in rupees (rounded to 2 decimal places)
     */
    fun calculateTotal(chargingCost: Double, platformFee: Double): Double {
        require(chargingCost >= 0) { "Charging cost cannot be negative" }
        require(platformFee >= 0) { "Platform fee cannot be negative" }

        val total = chargingCost + platformFee
        return roundToTwoDecimals(total)
    }

    /**
     * Calculate all pricing components at once
     *
     * @param energyKwh Energy consumed in kilowatt-hours
     * @param ratePerKwh Rate per kilowatt-hour in rupees
     * @param feePercent Platform fee percentage (default 5%)
     * @return PricingBreakdown object with all components
     */
    fun calculateFullBreakdown(
        energyKwh: Double,
        ratePerKwh: Double,
        feePercent: Double = 0.05
    ): PricingBreakdown {
        val chargingCost = calculateChargingCost(energyKwh, ratePerKwh)
        val platformFee = calculatePlatformFee(chargingCost, feePercent)
        val total = calculateTotal(chargingCost, platformFee)

        return PricingBreakdown(
            energyKwh = energyKwh,
            ratePerKwh = ratePerKwh,
            chargingCost = chargingCost,
            platformFee = platformFee,
            totalAmount = total
        )
    }

    /**
     * Format amount in Indian Rupees with proper formatting
     *
     * @param amount Amount to format
     * @return Formatted string (e.g., "₹1,234.56")
     */
    fun formatINR(amount: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        return formatter.format(amount)
    }

    /**
     * Format amount with 2 decimal places (without currency symbol)
     *
     * @param amount Amount to format
     * @return Formatted string (e.g., "1,234.56")
     */
    fun formatAmount(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
        formatter.minimumFractionDigits = 2
        formatter.maximumFractionDigits = 2
        return formatter.format(amount)
    }

    /**
     * Round amount to 2 decimal places
     */
    private fun roundToTwoDecimals(value: Double): Double {
        return round(value * 100) / 100
    }
}

/**
 * Data class representing complete pricing breakdown
 */
data class PricingBreakdown(
    val energyKwh: Double,
    val ratePerKwh: Double,
    val chargingCost: Double,
    val platformFee: Double,
    val totalAmount: Double
) {
    /**
     * Get formatted breakdown for display
     */
    fun getFormattedBreakdown(): Map<String, String> {
        return mapOf(
            "energy" to "${PricingCalculator.formatAmount(energyKwh)} kWh",
            "rate" to PricingCalculator.formatINR(ratePerKwh),
            "chargingCost" to PricingCalculator.formatINR(chargingCost),
            "platformFee" to PricingCalculator.formatINR(platformFee),
            "total" to PricingCalculator.formatINR(totalAmount)
        )
    }
}
