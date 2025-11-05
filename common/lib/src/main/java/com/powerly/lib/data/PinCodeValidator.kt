package com.powerly.lib.data

/**
 * PIN code validation result
 */
sealed class PinCodeValidationResult {
    /**
     * Validation successful
     * @param pinCode The validated PIN code
     * @param district The district associated with this PIN code
     */
    data class Valid(val pinCode: String, val district: TamilNaduDistrict?) : PinCodeValidationResult()

    /**
     * Validation failed
     * @param error The error message
     */
    data class Invalid(val error: String) : PinCodeValidationResult()
}

/**
 * Utility class for validating Tamil Nadu PIN codes
 * PIN codes in Tamil Nadu range from 600001 to 643253
 */
object PinCodeValidator {

    /**
     * Regex pattern for 6-digit PIN code format
     * Tamil Nadu PIN codes start with 6 and range from 600001 to 643253
     */
    private val PIN_CODE_REGEX = Regex("^(6[0-4][0-9]{4})$")

    /**
     * Minimum PIN code for Tamil Nadu
     */
    private const val MIN_PIN_CODE = 600001

    /**
     * Maximum PIN code for Tamil Nadu
     */
    private const val MAX_PIN_CODE = 643253

    /**
     * Validate a PIN code for Tamil Nadu
     * @param pinCode The PIN code to validate
     * @param strict If true, validates against Tamil Nadu districts. If false, only checks format.
     * @return PinCodeValidationResult indicating success or failure
     */
    fun validate(pinCode: String?, strict: Boolean = true): PinCodeValidationResult {
        // Check if PIN code is null or empty
        if (pinCode.isNullOrBlank()) {
            return PinCodeValidationResult.Invalid("PIN code is required")
        }

        // Remove any spaces or special characters
        val cleanPinCode = pinCode.trim().replace("\\s+".toRegex(), "")

        // Check length
        if (cleanPinCode.length != 6) {
            return PinCodeValidationResult.Invalid("PIN code must be 6 digits")
        }

        // Check format using regex
        if (!PIN_CODE_REGEX.matches(cleanPinCode)) {
            return PinCodeValidationResult.Invalid("Invalid PIN code format. Must be 6 digits starting with 60-64")
        }

        // Check range
        val pinCodeInt = cleanPinCode.toIntOrNull()
        if (pinCodeInt == null || pinCodeInt < MIN_PIN_CODE || pinCodeInt > MAX_PIN_CODE) {
            return PinCodeValidationResult.Invalid("PIN code must be between $MIN_PIN_CODE and $MAX_PIN_CODE")
        }

        // If strict mode, verify against Tamil Nadu districts
        if (strict) {
            val district = TamilNaduDistrictsData.getDistrictByPinCode(cleanPinCode)
            if (district == null) {
                return PinCodeValidationResult.Invalid("PIN code not found in Tamil Nadu districts")
            }
            return PinCodeValidationResult.Valid(cleanPinCode, district)
        }

        return PinCodeValidationResult.Valid(cleanPinCode, null)
    }

    /**
     * Check if a PIN code is valid (returns boolean)
     * @param pinCode The PIN code to check
     * @param strict If true, validates against Tamil Nadu districts
     * @return True if valid, false otherwise
     */
    fun isValid(pinCode: String?, strict: Boolean = true): Boolean {
        return validate(pinCode, strict) is PinCodeValidationResult.Valid
    }

    /**
     * Format a PIN code with proper spacing (e.g., "600 001")
     * @param pinCode The PIN code to format
     * @return Formatted PIN code or null if invalid
     */
    fun format(pinCode: String?): String? {
        if (pinCode.isNullOrBlank()) return null

        val cleanPinCode = pinCode.trim().replace("\\s+".toRegex(), "")
        if (cleanPinCode.length != 6) return null

        return "${cleanPinCode.substring(0, 3)} ${cleanPinCode.substring(3)}"
    }

    /**
     * Get district information for a PIN code
     * @param pinCode The PIN code to lookup
     * @return District information or null if not found
     */
    fun getDistrict(pinCode: String?): TamilNaduDistrict? {
        if (pinCode.isNullOrBlank()) return null

        val cleanPinCode = pinCode.trim().replace("\\s+".toRegex(), "")
        return TamilNaduDistrictsData.getDistrictByPinCode(cleanPinCode)
    }

    /**
     * Get city information for a PIN code
     * @param pinCode The PIN code to lookup
     * @return City information or null if not found
     */
    fun getCity(pinCode: String?): TamilNaduCity? {
        if (pinCode.isNullOrBlank()) return null

        val cleanPinCode = pinCode.trim().replace("\\s+".toRegex(), "")
        return TamilNaduCitiesData.getCityByPinCode(cleanPinCode)
    }

    /**
     * Validate and auto-fill district and city based on PIN code
     * @param pinCode The PIN code to validate
     * @return Triple of (isValid, district, city) or null if validation fails
     */
    fun validateAndAutoFill(pinCode: String?): Triple<Boolean, TamilNaduDistrict?, TamilNaduCity?>? {
        val validationResult = validate(pinCode, strict = true)

        return when (validationResult) {
            is PinCodeValidationResult.Valid -> {
                val district = validationResult.district
                val city = getCity(validationResult.pinCode)
                Triple(true, district, city)
            }
            is PinCodeValidationResult.Invalid -> {
                null
            }
        }
    }

    /**
     * Get error message for invalid PIN code
     * @param pinCode The PIN code to validate
     * @return Error message or null if valid
     */
    fun getErrorMessage(pinCode: String?, strict: Boolean = true): String? {
        val result = validate(pinCode, strict)
        return when (result) {
            is PinCodeValidationResult.Invalid -> result.error
            is PinCodeValidationResult.Valid -> null
        }
    }

    /**
     * Check if a PIN code belongs to a specific district
     * @param pinCode The PIN code to check
     * @param districtName The district name to match
     * @return True if PIN code belongs to the district
     */
    fun belongsToDistrict(pinCode: String?, districtName: String): Boolean {
        val district = getDistrict(pinCode)
        return district?.name.equals(districtName, ignoreCase = true)
    }

    /**
     * Get all valid PIN code ranges for Tamil Nadu
     * @return List of PIN code ranges as strings
     */
    fun getValidRanges(): List<String> {
        return listOf(
            "600001-600129 (Chennai)",
            "601001-602105 (Tiruvallur, Kanchipuram)",
            "603001-608902 (Chengalpattu, Viluppuram, Cuddalore)",
            "609001-614807 (Mayiladuthurai, Thanjavur)",
            "620001-630612 (Tiruchirappalli, Pudukkottai, Sivagangai)",
            "635001-643253 (Dharmapuri, Krishnagiri, Salem, Namakkal, Erode, Nilgiris)"
        )
    }

    /**
     * Suggest corrections for common PIN code errors
     * @param pinCode The invalid PIN code
     * @return List of possible corrections
     */
    fun suggestCorrections(pinCode: String?): List<String> {
        if (pinCode.isNullOrBlank()) return emptyList()

        val cleanPinCode = pinCode.trim().replace("\\s+".toRegex(), "")
        val suggestions = mutableListOf<String>()

        // If too short, suggest padding with zeros
        if (cleanPinCode.length < 6 && cleanPinCode.all { it.isDigit() }) {
            val padded = cleanPinCode.padEnd(6, '0')
            if (isValid(padded, strict = false)) {
                suggestions.add(padded)
            }
        }

        // If too long, suggest truncating
        if (cleanPinCode.length > 6 && cleanPinCode.all { it.isDigit() }) {
            val truncated = cleanPinCode.substring(0, 6)
            if (isValid(truncated, strict = false)) {
                suggestions.add(truncated)
            }
        }

        // If starts with 5 instead of 6, suggest correction
        if (cleanPinCode.startsWith("5") && cleanPinCode.length == 6) {
            val corrected = "6${cleanPinCode.substring(1)}"
            if (isValid(corrected, strict = false)) {
                suggestions.add(corrected)
            }
        }

        return suggestions.take(3) // Return max 3 suggestions
    }
}
