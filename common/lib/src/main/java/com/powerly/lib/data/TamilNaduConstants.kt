package com.powerly.lib.data

import com.SharaSpot.core.model.location.Target

/**
 * Constants for Tamil Nadu location defaults
 */
object TamilNaduConstants {

    /**
     * Default center location for Tamil Nadu (Chennai - Capital)
     */
    val DEFAULT_CENTER = Target(
        latitude = 13.0827,
        longitude = 80.2707
    )

    /**
     * Tamil Nadu state boundaries for map bounds
     */
    object Bounds {
        const val NORTH_LATITUDE = 13.5733  // Northernmost point
        const val SOUTH_LATITUDE = 8.0883   // Southernmost point (Kanyakumari)
        const val EAST_LONGITUDE = 80.3428  // Easternmost point
        const val WEST_LONGITUDE = 76.2239  // Westernmost point (Nilgiris)
    }

    /**
     * Major city coordinates for quick reference
     */
    object Cities {
        val CHENNAI = Target(latitude = 13.0827, longitude = 80.2707)
        val COIMBATORE = Target(latitude = 11.0168, longitude = 76.9558)
        val MADURAI = Target(latitude = 9.9252, longitude = 78.1198)
        val TIRUCHIRAPPALLI = Target(latitude = 10.7905, longitude = 78.7047)
        val SALEM = Target(latitude = 11.6643, longitude = 78.1460)
        val TIRUPPUR = Target(latitude = 11.1085, longitude = 77.3411)
        val ERODE = Target(latitude = 11.3410, longitude = 77.7172)
        val VELLORE = Target(latitude = 12.9165, longitude = 79.1325)
    }

    /**
     * Default zoom levels for Tamil Nadu map
     */
    object Zoom {
        const val STATE_LEVEL = 7f      // View entire state
        const val CITY_LEVEL = 11f      // View city level
        const val DISTRICT_LEVEL = 9f   // View district level
        const val STREET_LEVEL = 15f    // View street level
    }

    /**
     * State name constant
     */
    const val STATE_NAME = "Tamil Nadu"

    /**
     * Country code
     */
    const val COUNTRY_CODE = "IN"

    /**
     * PIN code range
     */
    object PinCodeRange {
        const val MIN = 600001
        const val MAX = 643253
    }
}
