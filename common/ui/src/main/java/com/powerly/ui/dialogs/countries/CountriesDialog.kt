package com.SharaSpot.ui.dialogs.countries

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.powerly.core.model.location.Country
import com.SharaSpot.lib.managers.CountryManager
import com.SharaSpot.ui.dialogs.MyDialogState
import com.SharaSpot.ui.dialogs.MyScreenBottomSheet

@Composable
fun CountriesDialog(
    state: MyDialogState? = null,
    selectedCountry: () -> Country?,
    onSelectCountry: (Country) -> Unit,
    onDismiss: () -> Unit = {}
) {
    MyScreenBottomSheet(state = state, onDismiss = onDismiss) {
        val countries = remember { CountryManager.getCountryList() }
        CountriesDialogContent(
            selectedCountry = selectedCountry,
            countries = countries,
            onSelect = {
                onSelectCountry(it)
                state?.dismiss()
                onDismiss()
            }
        )
    }
}

