package com.powerly.core.model.location

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Tamil Nadu specific address model with district and PIN code support.
 * This model is tailored for the Tamil Nadu market with proper validation
 * for 6-digit PIN codes and district-based organization.
 */
data class TamilNaduAddress(
    @SerializedName("address_line_1")
    val addressLine1: String,

    @SerializedName("address_line_2")
    val addressLine2: String? = null,

    @SerializedName("city")
    val city: String,

    @SerializedName("district")
    val district: String,

    @SerializedName("pin_code")
    val pinCode: String,  // 6-digit PIN code (600001-643253 for Tamil Nadu)

    @SerializedName("landmark")
    val landmark: String? = null,

    @SerializedName("address_type")
    val addressType: AddressType = AddressType.HOME,

    @SerializedName("state")
    val state: String = "Tamil Nadu",

    @SerializedName("country_code")
    val countryCode: String = "IN"
) : Serializable {

    /**
     * Returns the formatted full address for display
     */
    val fullAddress: String
        get() = buildString {
            append(addressLine1)
            addressLine2?.let { append(", $it") }
            landmark?.let { append(", $it") }
            append(", $city")
            append(", $district")
            append(", Tamil Nadu")
            append(" - $pinCode")
        }

    /**
     * Returns a short address for compact display
     */
    val shortAddress: String
        get() = buildString {
            append(addressLine1)
            append(", $city")
            append(" - $pinCode")
        }

    /**
     * Converts to legacy MyAddress format for backward compatibility
     */
    fun toMyAddress(): MyAddress = MyAddress(
        addressLine1 = addressLine1,
        addressLine2 = addressLine2,
        addressLine3 = landmark,
        city = city,
        state = "Tamil Nadu",
        zipcode = pinCode,
        countryCode = countryCode
    )

    companion object {
        /**
         * Creates TamilNaduAddress from legacy MyAddress
         */
        fun fromMyAddress(
            myAddress: MyAddress,
            district: String,
            addressType: AddressType = AddressType.HOME
        ): TamilNaduAddress = TamilNaduAddress(
            addressLine1 = myAddress.addressLine1 ?: "",
            addressLine2 = myAddress.addressLine2,
            city = myAddress.city ?: "",
            district = district,
            pinCode = myAddress.zipcode ?: "",
            landmark = myAddress.addressLine3,
            addressType = addressType,
            state = "Tamil Nadu",
            countryCode = myAddress.countryCode ?: "IN"
        )
    }
}

/**
 * Enum for address types in Tamil Nadu addresses
 */
enum class AddressType {
    @SerializedName("HOME")
    HOME,

    @SerializedName("WORK")
    WORK,

    @SerializedName("OTHER")
    OTHER;

    fun displayName(): String = when (this) {
        HOME -> "Home"
        WORK -> "Work"
        OTHER -> "Other"
    }
}
