package com.powerly.lib.data

/**
 * Data class representing a city in Tamil Nadu
 * @param name City name in English
 * @param district District to which the city belongs
 * @param pinCodes List of PIN codes for this city
 * @param tier City tier (1 for major metros, 2 for important cities, 3 for smaller towns)
 * @param latitude Geographic latitude
 * @param longitude Geographic longitude
 */
data class TamilNaduCity(
    val name: String,
    val district: String,
    val pinCodes: List<String>,
    val tier: Int,
    val latitude: Double,
    val longitude: Double
)

/**
 * Comprehensive list of major cities in Tamil Nadu organized by district
 * Includes tier-1 and tier-2 cities for complete coverage
 */
object TamilNaduCitiesData {

    /**
     * All major cities in Tamil Nadu organized by importance
     */
    val ALL_CITIES = listOf(
        // TIER 1 - Major Metropolitan Cities
        TamilNaduCity(
            name = "Chennai",
            district = "Chennai",
            pinCodes = listOf("600001", "600002", "600003", "600004", "600005", "600006", "600007", "600008", "600009", "600010", "600011", "600012", "600013", "600014", "600015", "600016", "600017", "600018", "600020", "600021", "600024", "600025", "600028", "600029", "600030", "600031", "600032", "600033", "600034", "600035", "600036", "600037", "600039", "600040", "600041", "600042", "600079", "600081", "600082", "600083", "600084", "600085", "600086", "600087", "600088", "600089", "600090", "600091", "600092", "600093", "600094", "600095", "600096", "600097", "600098", "600099", "600100", "600101", "600102", "600103", "600104", "600105", "600106", "600107", "600108", "600113", "600114", "600115", "600116", "600117", "600118", "600119", "600120", "600121", "600122", "600123", "600124", "600125", "600126", "600127", "600128", "600129"),
            tier = 1,
            latitude = 13.0827,
            longitude = 80.2707
        ),
        TamilNaduCity(
            name = "Coimbatore",
            district = "Coimbatore",
            pinCodes = listOf("641001", "641002", "641003", "641004", "641005", "641006", "641007", "641008", "641009", "641010", "641011", "641012", "641013", "641014", "641015", "641016", "641017", "641018", "641019", "641020", "641021", "641022", "641023", "641024", "641025", "641026", "641027", "641028", "641029", "641030", "641031", "641032", "641033", "641034", "641035", "641036", "641037", "641038", "641039", "641041", "641042", "641043", "641044", "641045", "641046", "641047", "641048", "641049", "641050"),
            tier = 1,
            latitude = 11.0168,
            longitude = 76.9558
        ),
        TamilNaduCity(
            name = "Madurai",
            district = "Madurai",
            pinCodes = listOf("625001", "625002", "625003", "625004", "625005", "625006", "625007", "625008", "625009", "625010", "625011", "625012", "625013", "625014", "625015", "625016", "625017", "625018", "625019", "625020", "625021", "625022", "625023"),
            tier = 1,
            latitude = 9.9252,
            longitude = 78.1198
        ),
        TamilNaduCity(
            name = "Tiruchirappalli",
            district = "Tiruchirappalli (Trichy)",
            pinCodes = listOf("620001", "620002", "620003", "620004", "620005", "620006", "620007", "620008", "620009", "620010", "620011", "620012", "620013", "620014", "620015", "620016", "620017", "620018", "620019", "620020", "620021", "620022", "620023", "620024"),
            tier = 1,
            latitude = 10.7905,
            longitude = 78.7047
        ),
        TamilNaduCity(
            name = "Salem",
            district = "Salem",
            pinCodes = listOf("636001", "636002", "636003", "636004", "636005", "636006", "636007", "636008", "636009", "636010", "636011", "636012", "636013", "636014", "636015", "636016"),
            tier = 1,
            latitude = 11.6643,
            longitude = 78.1460
        ),
        TamilNaduCity(
            name = "Tiruppur",
            district = "Tiruppur",
            pinCodes = listOf("641601", "641602", "641603", "641604", "641605", "641606", "641607", "641608", "641687"),
            tier = 1,
            latitude = 11.1085,
            longitude = 77.3411
        ),

        // TIER 2 - Important Cities
        TamilNaduCity(
            name = "Erode",
            district = "Erode",
            pinCodes = listOf("638001", "638002", "638003", "638004", "638005", "638006", "638007", "638008", "638009", "638010", "638011", "638012"),
            tier = 2,
            latitude = 11.3410,
            longitude = 77.7172
        ),
        TamilNaduCity(
            name = "Tirunelveli",
            district = "Tirunelveli",
            pinCodes = listOf("627001", "627002", "627003", "627004", "627005", "627006", "627007", "627008", "627009", "627010", "627011", "627012"),
            tier = 2,
            latitude = 8.7139,
            longitude = 77.7567
        ),
        TamilNaduCity(
            name = "Vellore",
            district = "Vellore",
            pinCodes = listOf("632001", "632002", "632003", "632004", "632005", "632006", "632007", "632008", "632009", "632010", "632011", "632012", "632014"),
            tier = 2,
            latitude = 12.9165,
            longitude = 79.1325
        ),
        TamilNaduCity(
            name = "Thoothukudi",
            district = "Thoothukudi (Tuticorin)",
            pinCodes = listOf("628001", "628002", "628003", "628004", "628005", "628006", "628007", "628008"),
            tier = 2,
            latitude = 8.7642,
            longitude = 78.1348
        ),
        TamilNaduCity(
            name = "Thanjavur",
            district = "Thanjavur",
            pinCodes = listOf("613001", "613002", "613003", "613004", "613005", "613006", "613007", "613008", "613009", "613010"),
            tier = 2,
            latitude = 10.7870,
            longitude = 79.1378
        ),
        TamilNaduCity(
            name = "Dindigul",
            district = "Dindigul",
            pinCodes = listOf("624001", "624002", "624003", "624004", "624005", "624006"),
            tier = 2,
            latitude = 10.3673,
            longitude = 77.9803
        ),
        TamilNaduCity(
            name = "Nagercoil",
            district = "Kanyakumari",
            pinCodes = listOf("629001", "629002", "629003", "629004"),
            tier = 2,
            latitude = 8.1774,
            longitude = 77.4341
        ),
        TamilNaduCity(
            name = "Kanchipuram",
            district = "Kanchipuram",
            pinCodes = listOf("631501", "631502", "631503", "631561"),
            tier = 2,
            latitude = 12.8342,
            longitude = 79.7036
        ),
        TamilNaduCity(
            name = "Karur",
            district = "Karur",
            pinCodes = listOf("639001", "639002", "639003", "639004", "639005", "639006", "639007"),
            tier = 2,
            latitude = 10.9601,
            longitude = 78.0766
        ),
        TamilNaduCity(
            name = "Udhagamandalam",
            district = "Nilgiris",
            pinCodes = listOf("643001", "643002", "643003", "643004", "643005", "643006"),
            tier = 2,
            latitude = 11.4102,
            longitude = 76.6950
        ),
        TamilNaduCity(
            name = "Hosur",
            district = "Krishnagiri",
            pinCodes = listOf("635109", "635110", "635114", "635126"),
            tier = 2,
            latitude = 12.7409,
            longitude = 77.8253
        ),
        TamilNaduCity(
            name = "Tambaram",
            district = "Chengalpattu",
            pinCodes = listOf("600045", "600059", "600073", "600074"),
            tier = 2,
            latitude = 12.9229,
            longitude = 80.1275
        ),
        TamilNaduCity(
            name = "Ambattur",
            district = "Chennai",
            pinCodes = listOf("600053", "600058", "600062"),
            tier = 2,
            latitude = 13.0989,
            longitude = 80.1620
        ),
        TamilNaduCity(
            name = "Cuddalore",
            district = "Cuddalore",
            pinCodes = listOf("607001", "607002", "607003", "607004", "607005"),
            tier = 2,
            latitude = 11.7480,
            longitude = 79.7714
        ),
        TamilNaduCity(
            name = "Kumbakonam",
            district = "Thanjavur",
            pinCodes = listOf("612001", "612002", "612003", "612004"),
            tier = 2,
            latitude = 10.9617,
            longitude = 79.3881
        ),
        TamilNaduCity(
            name = "Tiruvannamalai",
            district = "Tiruvannamalai",
            pinCodes = listOf("606601", "606602", "606603", "606604"),
            tier = 2,
            latitude = 12.2303,
            longitude = 79.0747
        ),
        TamilNaduCity(
            name = "Rajapalayam",
            district = "Virudhunagar",
            pinCodes = listOf("626117", "626108"),
            tier = 2,
            latitude = 9.4500,
            longitude = 77.5500
        ),
        TamilNaduCity(
            name = "Pollachi",
            district = "Coimbatore",
            pinCodes = listOf("642001", "642002", "642003", "642004", "642005"),
            tier = 2,
            latitude = 10.6580,
            longitude = 77.0080
        ),
        TamilNaduCity(
            name = "Pudukkottai",
            district = "Pudukkottai",
            pinCodes = listOf("622001", "622002", "622003", "622004"),
            tier = 2,
            latitude = 10.3797,
            longitude = 78.8205
        ),
        TamilNaduCity(
            name = "Sivakasi",
            district = "Virudhunagar",
            pinCodes = listOf("626123", "626124", "626130", "626189"),
            tier = 2,
            latitude = 9.4500,
            longitude = 77.8000
        ),
        TamilNaduCity(
            name = "Avadi",
            district = "Tiruvallur",
            pinCodes = listOf("600054", "600055", "600056", "600071"),
            tier = 2,
            latitude = 13.1147,
            longitude = 80.1018
        ),
        TamilNaduCity(
            name = "Tiruvallur",
            district = "Tiruvallur",
            pinCodes = listOf("602001", "602002", "602003"),
            tier = 2,
            latitude = 13.1258,
            longitude = 79.9094
        ),
        TamilNaduCity(
            name = "Namakkal",
            district = "Namakkal",
            pinCodes = listOf("637001", "637002", "637003"),
            tier = 2,
            latitude = 11.2189,
            longitude = 78.1677
        ),
        TamilNaduCity(
            name = "Krishnagiri",
            district = "Krishnagiri",
            pinCodes = listOf("635001", "635002"),
            tier = 2,
            latitude = 12.5186,
            longitude = 78.2137
        ),
        TamilNaduCity(
            name = "Dharmapuri",
            district = "Dharmapuri",
            pinCodes = listOf("636701", "636702", "636703", "636704", "636705"),
            tier = 2,
            latitude = 12.1275,
            longitude = 78.1582
        ),
        TamilNaduCity(
            name = "Ranipet",
            district = "Ranipet",
            pinCodes = listOf("632401", "632402", "632403", "632404"),
            tier = 2,
            latitude = 12.9249,
            longitude = 79.3333
        ),
        TamilNaduCity(
            name = "Viluppuram",
            district = "Viluppuram",
            pinCodes = listOf("605601", "605602", "605603"),
            tier = 2,
            latitude = 11.9401,
            longitude = 79.4861
        ),
        TamilNaduCity(
            name = "Chengalpattu",
            district = "Chengalpattu",
            pinCodes = listOf("603001", "603002", "603003", "603004"),
            tier = 2,
            latitude = 12.6917,
            longitude = 79.9759
        ),
        TamilNaduCity(
            name = "Tirupathur",
            district = "Tirupathur",
            pinCodes = listOf("635601", "635602"),
            tier = 2,
            latitude = 12.4971,
            longitude = 78.5720
        ),
        TamilNaduCity(
            name = "Tenkasi",
            district = "Tenkasi",
            pinCodes = listOf("627811", "627812", "627813"),
            tier = 2,
            latitude = 8.9597,
            longitude = 77.3152
        ),
        TamilNaduCity(
            name = "Ariyalur",
            district = "Ariyalur",
            pinCodes = listOf("621704", "621705", "621706"),
            tier = 2,
            latitude = 11.1401,
            longitude = 79.0782
        ),
        TamilNaduCity(
            name = "Perambalur",
            district = "Perambalur",
            pinCodes = listOf("621212", "621213", "621214"),
            tier = 2,
            latitude = 11.2324,
            longitude = 78.8795
        ),
        TamilNaduCity(
            name = "Kallakurichi",
            district = "Kallakurichi",
            pinCodes = listOf("606202", "606203", "606204"),
            tier = 2,
            latitude = 11.7380,
            longitude = 78.9594
        ),
        TamilNaduCity(
            name = "Mayiladuthurai",
            district = "Mayiladuthurai",
            pinCodes = listOf("609001", "609002", "609003"),
            tier = 2,
            latitude = 11.1028,
            longitude = 79.6545
        )
    )

    /**
     * Cities organized by tier
     */
    val TIER_1_CITIES = ALL_CITIES.filter { it.tier == 1 }
    val TIER_2_CITIES = ALL_CITIES.filter { it.tier == 2 }

    /**
     * Cities organized by district
     */
    val CITIES_BY_DISTRICT: Map<String, List<TamilNaduCity>> =
        ALL_CITIES.groupBy { it.district }

    /**
     * Get city names as a list for dropdowns
     */
    val CITY_NAMES: List<String> = ALL_CITIES.map { it.name }

    /**
     * Map of city names to their details
     */
    val CITY_MAP: Map<String, TamilNaduCity> =
        ALL_CITIES.associateBy { it.name }

    /**
     * Find city by name (case-insensitive)
     * @param name The city name to search for
     * @return The city if found, null otherwise
     */
    fun getCityByName(name: String): TamilNaduCity? {
        return ALL_CITIES.find { it.name.equals(name, ignoreCase = true) }
    }

    /**
     * Find city by PIN code
     * @param pinCode The PIN code to search for
     * @return The city if found, null otherwise
     */
    fun getCityByPinCode(pinCode: String): TamilNaduCity? {
        return ALL_CITIES.find { city ->
            city.pinCodes.any { it == pinCode }
        }
    }

    /**
     * Get cities for a specific district
     * @param district The district name
     * @return List of cities in that district
     */
    fun getCitiesForDistrict(district: String): List<TamilNaduCity> {
        return CITIES_BY_DISTRICT[district] ?: emptyList()
    }

    /**
     * Get city names for a specific district
     * @param district The district name
     * @return List of city names in that district
     */
    fun getCityNamesForDistrict(district: String): List<String> {
        return getCitiesForDistrict(district).map { it.name }
    }

    /**
     * Search cities by name (partial match, case-insensitive)
     * @param query The search query
     * @return List of matching cities
     */
    fun searchCities(query: String): List<TamilNaduCity> {
        return ALL_CITIES.filter { it.name.contains(query, ignoreCase = true) }
    }
}
