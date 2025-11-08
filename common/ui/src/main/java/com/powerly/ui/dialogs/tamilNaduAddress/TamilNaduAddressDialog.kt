package com.SharaSpot.ui.dialogs.tamilNaduAddress

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.powerly.core.model.location.AddressType
import com.powerly.core.model.location.TamilNaduAddress
import com.SharaSpot.lib.data.PinCodeValidator
import com.SharaSpot.lib.data.TamilNaduCitiesData
import com.SharaSpot.lib.data.TamilNaduDistrictsData
import com.SharaSpot.resources.R
import com.SharaSpot.ui.dialogs.MyDialogState
import com.SharaSpot.ui.dialogs.MyScreenBottomSheet
import com.SharaSpot.ui.screen.MyScreen
import com.SharaSpot.ui.screen.ScreenHeader

/**
 * Dialog for inputting Tamil Nadu addresses with district, city, and PIN code
 */
@Composable
fun TamilNaduAddressDialog(
    state: MyDialogState,
    initialAddress: TamilNaduAddress? = null,
    onSaveAddress: (TamilNaduAddress) -> Unit
) {
    MyScreenBottomSheet(state = state) {
        TamilNaduAddressScreen(
            initialAddress = initialAddress,
            onSave = {
                state.dismiss()
                onSaveAddress(it)
            },
            onClose = { state.dismiss() }
        )
    }
}

/**
 * Screen for Tamil Nadu address input
 */
@Composable
private fun TamilNaduAddressScreen(
    initialAddress: TamilNaduAddress?,
    onSave: (TamilNaduAddress) -> Unit,
    onClose: () -> Unit
) {
    // Form state
    var addressLine1 by remember { mutableStateOf(initialAddress?.addressLine1 ?: "") }
    var addressLine2 by remember { mutableStateOf(initialAddress?.addressLine2 ?: "") }
    var landmark by remember { mutableStateOf(initialAddress?.landmark ?: "") }
    var city by remember { mutableStateOf(initialAddress?.city ?: "") }
    var district by remember { mutableStateOf(initialAddress?.district ?: "") }
    var pinCode by remember { mutableStateOf(initialAddress?.pinCode ?: "") }
    var addressType by remember { mutableStateOf(initialAddress?.addressType ?: AddressType.HOME) }

    // Validation errors
    var addressLine1Error by remember { mutableStateOf<String?>(null) }
    var cityError by remember { mutableStateOf<String?>(null) }
    var districtError by remember { mutableStateOf<String?>(null) }
    var pinCodeError by remember { mutableStateOf<String?>(null) }

    // District dropdown expanded state
    var districtExpanded by remember { mutableStateOf(false) }

    // City suggestions
    var citySuggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var showCitySuggestions by remember { mutableStateOf(false) }

    // Auto-fill city and district when PIN code is entered
    LaunchedEffect(pinCode) {
        if (pinCode.length == 6) {
            val validationResult = PinCodeValidator.validateAndAutoFill(pinCode)
            if (validationResult != null) {
                val (isValid, districtObj, cityObj) = validationResult
                if (isValid) {
                    districtObj?.let { district = it.name }
                    cityObj?.let { city = it.name }
                    pinCodeError = null
                }
            } else {
                pinCodeError = PinCodeValidator.getErrorMessage(pinCode)
            }
        }
    }

    // Update city suggestions when district changes
    LaunchedEffect(district) {
        if (district.isNotEmpty()) {
            citySuggestions = TamilNaduCitiesData.getCityNamesForDistrict(district)
        }
    }

    MyScreen(
        header = {
            ScreenHeader(
                title = if (initialAddress != null) "Edit Address" else "Add Address",
                onClose = onClose,
                showDivider = true
            )
        },
        spacing = 16.dp,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Address Type Selection
            TamilNaduAddressTypeSelector(
                selectedType = addressType,
                onTypeSelected = { addressType = it }
            )

            // Address Line 1 (required)
            OutlinedTextField(
                value = addressLine1,
                onValueChange = {
                    addressLine1 = it
                    addressLine1Error = if (it.isBlank()) "Address is required" else null
                },
                label = { Text("House/Flat/Building *") },
                placeholder = { Text("Enter house/flat/building number") },
                isError = addressLine1Error != null,
                supportingText = addressLine1Error?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Address Line 2 (optional)
            OutlinedTextField(
                value = addressLine2,
                onValueChange = { addressLine2 = it },
                label = { Text("Street/Area/Locality") },
                placeholder = { Text("Enter street, area, or locality") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Landmark (optional)
            OutlinedTextField(
                value = landmark,
                onValueChange = { landmark = it },
                label = { Text("Landmark") },
                placeholder = { Text("Nearby landmark (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // PIN Code (required, with auto-fill)
            OutlinedTextField(
                value = pinCode,
                onValueChange = {
                    if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                        pinCode = it
                        if (it.length != 6) {
                            pinCodeError = "PIN code must be 6 digits"
                        }
                    }
                },
                label = { Text("PIN Code *") },
                placeholder = { Text("6-digit PIN code") },
                isError = pinCodeError != null,
                supportingText = pinCodeError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // District Dropdown (required)
            ExposedDropdownMenuBox(
                expanded = districtExpanded,
                onExpandedChange = { districtExpanded = !districtExpanded }
            ) {
                OutlinedTextField(
                    value = district,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("District *") },
                    placeholder = { Text("Select district") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = districtExpanded) },
                    isError = districtError != null,
                    supportingText = districtError?.let { { Text(it) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = districtExpanded,
                    onDismissRequest = { districtExpanded = false }
                ) {
                    TamilNaduDistrictsData.DISTRICT_NAMES.forEach { districtName ->
                        DropdownMenuItem(
                            text = { Text(districtName) },
                            onClick = {
                                district = districtName
                                districtError = null
                                districtExpanded = false
                            }
                        )
                    }
                }
            }

            // City Input with suggestions
            OutlinedTextField(
                value = city,
                onValueChange = {
                    city = it
                    cityError = if (it.isBlank()) "City is required" else null

                    // Show suggestions as user types
                    if (it.length >= 2) {
                        citySuggestions = if (district.isNotEmpty()) {
                            TamilNaduCitiesData.getCityNamesForDistrict(district)
                                .filter { cityName -> cityName.contains(it, ignoreCase = true) }
                        } else {
                            TamilNaduCitiesData.searchCities(it).map { cityObj -> cityObj.name }
                        }
                        showCitySuggestions = citySuggestions.isNotEmpty()
                    } else {
                        showCitySuggestions = false
                    }
                },
                label = { Text("City *") },
                placeholder = { Text("Enter city name") },
                isError = cityError != null,
                supportingText = cityError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // City suggestions
            if (showCitySuggestions) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        citySuggestions.take(5).forEach { suggestion ->
                            TextButton(
                                onClick = {
                                    city = suggestion
                                    showCitySuggestions = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(suggestion)
                            }
                        }
                    }
                }
            }
        }

        // Save button
        Button(
            onClick = {
                // Validate all fields
                var hasErrors = false

                if (addressLine1.isBlank()) {
                    addressLine1Error = "Address is required"
                    hasErrors = true
                }

                if (district.isBlank()) {
                    districtError = "District is required"
                    hasErrors = true
                }

                if (city.isBlank()) {
                    cityError = "City is required"
                    hasErrors = true
                }

                if (pinCode.length != 6 || !PinCodeValidator.isValid(pinCode)) {
                    pinCodeError = PinCodeValidator.getErrorMessage(pinCode) ?: "Invalid PIN code"
                    hasErrors = true
                }

                if (!hasErrors) {
                    val address = TamilNaduAddress(
                        addressLine1 = addressLine1,
                        addressLine2 = addressLine2.takeIf { it.isNotBlank() },
                        city = city,
                        district = district,
                        pinCode = pinCode,
                        landmark = landmark.takeIf { it.isNotBlank() },
                        addressType = addressType
                    )
                    onSave(address)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Save Address")
        }
    }
}

/**
 * Address type selector
 */
@Composable
private fun TamilNaduAddressTypeSelector(
    selectedType: AddressType,
    onTypeSelected: (AddressType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AddressType.values().forEach { type ->
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                label = { Text(type.displayName()) }
            )
        }
    }
}
