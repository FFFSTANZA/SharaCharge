package com.SharaSpot.core.model.util

import java.text.NumberFormat
import java.util.Locale

/**
 * Utility class for formatting and parsing Indian Rupees (INR) currency values.
 * Uses the Indian numbering system with proper decimal precision.
 */
object CurrencyFormatter {

    // Currency constants
    const val CURRENCY_CODE = "INR"
    const val CURRENCY_SYMBOL = "₹"

    // Indian locale for proper number formatting
    private val indianLocale = Locale("en", "IN")

    // Number formatter for INR with Indian grouping (1,23,456.78)
    private val currencyFormatter: NumberFormat = NumberFormat.getCurrencyInstance(indianLocale).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }

    // Number formatter without currency symbol
    private val numberFormatter: NumberFormat = NumberFormat.getNumberInstance(indianLocale).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }

    /**
     * Formats a Double amount to INR currency string with the ₹ symbol.
     *
     * Example: 1234.56 -> "₹1,234.56"
     *          123456.78 -> "₹1,23,456.78"
     *
     * @param amount The amount to format
     * @return Formatted currency string with ₹ symbol
     */
    fun formatINR(amount: Double): String {
        return currencyFormatter.format(amount)
    }

    /**
     * Formats a Double amount to INR string without the currency symbol.
     * Useful for calculations or when the symbol is displayed separately.
     *
     * Example: 1234.56 -> "1,234.56"
     *          123456.78 -> "1,23,456.78"
     *
     * @param amount The amount to format
     * @return Formatted number string without currency symbol
     */
    fun formatINRAmount(amount: Double): String {
        return numberFormatter.format(amount)
    }

    /**
     * Parses a formatted INR string to Double.
     * Handles both formats: with and without ₹ symbol.
     *
     * Example: "₹1,234.56" -> 1234.56
     *          "1,23,456.78" -> 123456.78
     *          "₹ 1234.56" -> 1234.56
     *
     * @param formatted The formatted currency string
     * @return Parsed Double value, or 0.0 if parsing fails
     */
    fun parseINR(formatted: String): Double {
        return try {
            // Remove currency symbols, spaces, and grouping separators
            val cleaned = formatted
                .replace(CURRENCY_SYMBOL, "")
                .replace("INR", "")
                .replace(" ", "")
                .replace(",", "")
                .trim()

            cleaned.toDoubleOrNull() ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }

    /**
     * Formats amount with currency code instead of symbol.
     *
     * Example: 1234.56 -> "INR 1,234.56"
     *
     * @param amount The amount to format
     * @return Formatted string with INR code
     */
    fun formatINRWithCode(amount: Double): String {
        return "$CURRENCY_CODE ${formatINRAmount(amount)}"
    }

    /**
     * Formats amount for display in UI with symbol.
     * Ensures consistent formatting across the app.
     *
     * @param amount The amount to format
     * @return Formatted currency string
     */
    fun formatForDisplay(amount: Double): String {
        return formatINR(amount)
    }

    /**
     * Converts paise (smallest unit) to rupees.
     * Razorpay uses paise (1 rupee = 100 paise).
     *
     * Example: 12345 paise -> 123.45 rupees
     *
     * @param paise Amount in paise
     * @return Amount in rupees
     */
    fun paiseToRupees(paise: Int): Double {
        return paise / 100.0
    }

    /**
     * Converts rupees to paise for Razorpay API.
     *
     * Example: 123.45 rupees -> 12345 paise
     *
     * @param rupees Amount in rupees
     * @return Amount in paise
     */
    fun rupeesToPaise(rupees: Double): Int {
        return (rupees * 100).toInt()
    }
}
