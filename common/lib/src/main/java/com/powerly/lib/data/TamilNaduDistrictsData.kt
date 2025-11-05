package com.powerly.lib.data

/**
 * Data class representing a Tamil Nadu district
 * @param name District name in English
 * @param code District code for administrative purposes
 * @param pinCodeRanges List of PIN code ranges for this district
 * @param headquarters District headquarters city
 */
data class TamilNaduDistrict(
    val name: String,
    val code: String,
    val pinCodeRanges: List<IntRange>,
    val headquarters: String
)

/**
 * Comprehensive list of all 38 districts in Tamil Nadu (as of 2024)
 * Organized alphabetically with their respective PIN code ranges and headquarters
 */
object TamilNaduDistrictsData {

    /**
     * All 38 districts of Tamil Nadu with their details
     */
    val ALL_DISTRICTS = listOf(
        TamilNaduDistrict(
            name = "Ariyalur",
            code = "AR",
            pinCodeRanges = listOf(621704..621716),
            headquarters = "Ariyalur"
        ),
        TamilNaduDistrict(
            name = "Chengalpattu",
            code = "CG",
            pinCodeRanges = listOf(603001..603406),
            headquarters = "Chengalpattu"
        ),
        TamilNaduDistrict(
            name = "Chennai",
            code = "CH",
            pinCodeRanges = listOf(600001..600129),
            headquarters = "Chennai"
        ),
        TamilNaduDistrict(
            name = "Coimbatore",
            code = "CB",
            pinCodeRanges = listOf(641001..642205),
            headquarters = "Coimbatore"
        ),
        TamilNaduDistrict(
            name = "Cuddalore",
            code = "CD",
            pinCodeRanges = listOf(607001..608902),
            headquarters = "Cuddalore"
        ),
        TamilNaduDistrict(
            name = "Dharmapuri",
            code = "DH",
            pinCodeRanges = listOf(635001..636813),
            headquarters = "Dharmapuri"
        ),
        TamilNaduDistrict(
            name = "Dindigul",
            code = "DG",
            pinCodeRanges = listOf(624001..624712),
            headquarters = "Dindigul"
        ),
        TamilNaduDistrict(
            name = "Erode",
            code = "ER",
            pinCodeRanges = listOf(638001..638703),
            headquarters = "Erode"
        ),
        TamilNaduDistrict(
            name = "Kallakurichi",
            code = "KK",
            pinCodeRanges = listOf(606201..606302),
            headquarters = "Kallakurichi"
        ),
        TamilNaduDistrict(
            name = "Kanchipuram",
            code = "KC",
            pinCodeRanges = listOf(631501..632602),
            headquarters = "Kanchipuram"
        ),
        TamilNaduDistrict(
            name = "Kanyakumari",
            code = "KK",
            pinCodeRanges = listOf(629001..629901),
            headquarters = "Nagercoil"
        ),
        TamilNaduDistrict(
            name = "Karur",
            code = "KR",
            pinCodeRanges = listOf(639001..639207),
            headquarters = "Karur"
        ),
        TamilNaduDistrict(
            name = "Krishnagiri",
            code = "KG",
            pinCodeRanges = listOf(635001..635853),
            headquarters = "Krishnagiri"
        ),
        TamilNaduDistrict(
            name = "Madurai",
            code = "MD",
            pinCodeRanges = listOf(625001..625706),
            headquarters = "Madurai"
        ),
        TamilNaduDistrict(
            name = "Mayiladuthurai",
            code = "MY",
            pinCodeRanges = listOf(609001..609804),
            headquarters = "Mayiladuthurai"
        ),
        TamilNaduDistrict(
            name = "Nagapattinam",
            code = "NG",
            pinCodeRanges = listOf(611001..611112),
            headquarters = "Nagapattinam"
        ),
        TamilNaduDistrict(
            name = "Namakkal",
            code = "NK",
            pinCodeRanges = listOf(637001..638183),
            headquarters = "Namakkal"
        ),
        TamilNaduDistrict(
            name = "Nilgiris",
            code = "NL",
            pinCodeRanges = listOf(643001..643253),
            headquarters = "Udhagamandalam (Ooty)"
        ),
        TamilNaduDistrict(
            name = "Perambalur",
            code = "PR",
            pinCodeRanges = listOf(621101..621220),
            headquarters = "Perambalur"
        ),
        TamilNaduDistrict(
            name = "Pudukkottai",
            code = "PK",
            pinCodeRanges = listOf(622001..622507),
            headquarters = "Pudukkottai"
        ),
        TamilNaduDistrict(
            name = "Ramanathapuram",
            code = "RM",
            pinCodeRanges = listOf(623001..623712),
            headquarters = "Ramanathapuram"
        ),
        TamilNaduDistrict(
            name = "Ranipet",
            code = "RP",
            pinCodeRanges = listOf(632401..632519),
            headquarters = "Ranipet"
        ),
        TamilNaduDistrict(
            name = "Salem",
            code = "SL",
            pinCodeRanges = listOf(636001..637411),
            headquarters = "Salem"
        ),
        TamilNaduDistrict(
            name = "Sivagangai",
            code = "SG",
            pinCodeRanges = listOf(630001..630612),
            headquarters = "Sivagangai"
        ),
        TamilNaduDistrict(
            name = "Tenkasi",
            code = "TK",
            pinCodeRanges = listOf(627801..627859),
            headquarters = "Tenkasi"
        ),
        TamilNaduDistrict(
            name = "Thanjavur",
            code = "TJ",
            pinCodeRanges = listOf(613001..614807),
            headquarters = "Thanjavur"
        ),
        TamilNaduDistrict(
            name = "Theni",
            code = "TN",
            pinCodeRanges = listOf(625001..625602),
            headquarters = "Theni"
        ),
        TamilNaduDistrict(
            name = "Thoothukudi (Tuticorin)",
            code = "TT",
            pinCodeRanges = listOf(628001..628952),
            headquarters = "Thoothukudi"
        ),
        TamilNaduDistrict(
            name = "Tiruchirappalli (Trichy)",
            code = "TR",
            pinCodeRanges = listOf(620001..621313),
            headquarters = "Tiruchirappalli"
        ),
        TamilNaduDistrict(
            name = "Tirunelveli",
            code = "TV",
            pinCodeRanges = listOf(627001..627953),
            headquarters = "Tirunelveli"
        ),
        TamilNaduDistrict(
            name = "Tirupathur",
            code = "TP",
            pinCodeRanges = listOf(635601..635901),
            headquarters = "Tirupathur"
        ),
        TamilNaduDistrict(
            name = "Tiruppur",
            code = "TI",
            pinCodeRanges = listOf(638656..642205),
            headquarters = "Tiruppur"
        ),
        TamilNaduDistrict(
            name = "Tiruvallur",
            code = "TA",
            pinCodeRanges = listOf(600052..602105),
            headquarters = "Tiruvallur"
        ),
        TamilNaduDistrict(
            name = "Tiruvannamalai",
            code = "TM",
            pinCodeRanges = listOf(606601..606807),
            headquarters = "Tiruvannamalai"
        ),
        TamilNaduDistrict(
            name = "Tiruvarur",
            code = "TU",
            pinCodeRanges = listOf(610001..610207),
            headquarters = "Tiruvarur"
        ),
        TamilNaduDistrict(
            name = "Vellore",
            code = "VL",
            pinCodeRanges = listOf(632001..632602),
            headquarters = "Vellore"
        ),
        TamilNaduDistrict(
            name = "Viluppuram",
            code = "VP",
            pinCodeRanges = listOf(604001..605851),
            headquarters = "Viluppuram"
        ),
        TamilNaduDistrict(
            name = "Virudhunagar",
            code = "VR",
            pinCodeRanges = listOf(626001..626204),
            headquarters = "Virudhunagar"
        )
    )

    /**
     * Get district names as a list for dropdowns
     */
    val DISTRICT_NAMES: List<String> = ALL_DISTRICTS.map { it.name }

    /**
     * Map of district names to their details
     */
    val DISTRICT_MAP: Map<String, TamilNaduDistrict> =
        ALL_DISTRICTS.associateBy { it.name }

    /**
     * Map of district codes to their details
     */
    val DISTRICT_CODE_MAP: Map<String, TamilNaduDistrict> =
        ALL_DISTRICTS.associateBy { it.code }

    /**
     * Find district by PIN code
     * @param pinCode The PIN code to search for
     * @return The district that contains this PIN code, or null if not found
     */
    fun getDistrictByPinCode(pinCode: String): TamilNaduDistrict? {
        val pin = pinCode.toIntOrNull() ?: return null
        return ALL_DISTRICTS.find { district ->
            district.pinCodeRanges.any { range -> pin in range }
        }
    }

    /**
     * Find district by name (case-insensitive)
     * @param name The district name to search for
     * @return The district if found, null otherwise
     */
    fun getDistrictByName(name: String): TamilNaduDistrict? {
        return ALL_DISTRICTS.find { it.name.equals(name, ignoreCase = true) }
    }

    /**
     * Find district by code
     * @param code The district code to search for
     * @return The district if found, null otherwise
     */
    fun getDistrictByCode(code: String): TamilNaduDistrict? {
        return DISTRICT_CODE_MAP[code.uppercase()]
    }

    /**
     * Check if a PIN code belongs to Tamil Nadu
     * @param pinCode The PIN code to check
     * @return True if the PIN code is in Tamil Nadu range
     */
    fun isTamilNaduPinCode(pinCode: String): Boolean {
        return getDistrictByPinCode(pinCode) != null
    }
}
