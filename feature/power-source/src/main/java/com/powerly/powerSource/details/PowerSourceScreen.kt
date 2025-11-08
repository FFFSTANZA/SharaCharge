package com.SharaSpot.powerSource.details

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.SharaSpot.core.data.model.SourceStatus
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.contribution.Contribution
import com.powerly.core.model.contribution.ContributionSummary
import com.powerly.core.model.powerly.PowerSource
import com.SharaSpot.powerSource.PsViewModel
import com.SharaSpot.ui.HomeUiState
import com.SharaSpot.ui.dialogs.loading.rememberScreenState
import com.SharaSpot.ui.extensions.openCall

private const val TAG = "PowerSourceScreen"

/**
 * Displays the details of a power source.
 *
 * This composable function retrieves the power source details based on the provided `powerSourceId`.
 * It displays the power source information, user's balance, and handles user interactions
 * such as calling the power source or navigating to it.
 *
 * @param powerSourceId The ID of the power source to display.
 * @param uiState The current UI state of the application, providing access to user location and balance.
 * @param viewModel The ViewModel responsible for fetching and managing power source data.
 * @param onNavigate A callback function to handle navigation events triggered by user interactions.
 */
@Composable
fun PowerSourceScreen(
    powerSourceId: String,
    uiState: HomeUiState,
    viewModel: PsViewModel,
    onNavigate: (SourceEvents) -> Unit
) {
    val context = LocalContext.current
    val screenState = rememberScreenState()
    val userLocation = remember { viewModel.userLocation() }
    var powerSource by remember { mutableStateOf<PowerSource?>(null) }
    val balance by remember { uiState.balance }

    // Collect contribution data
    val contributionSummaryState by viewModel.contributionSummary.collectAsState()
    val contributions by viewModel.contributions.collectAsState()

    LaunchedEffect(userLocation) {
        Log.i(TAG, "powerSourceId - $powerSourceId")

        if (viewModel.showOnBoardingDialog) {
            onNavigate(SourceEvents.HowToCharge)
        }
        // load power source details
        viewModel.getPowerSource(
            id = powerSourceId,
            latitude = userLocation?.latitude,
            longitude = userLocation?.longitude,
        )
    }

    LaunchedEffect(Unit) {
        viewModel.powerSourceStatus.collect {
            screenState.loading = it is SourceStatus.Loading
            when (it) {
                is SourceStatus.Error -> {
                    screenState.showMessage(it.msg) {
                        onNavigate(SourceEvents.Close)
                    }
                }

                is SourceStatus.Success -> {
                    powerSource = it.powerSource
                }

                else -> {}
            }
        }
    }

    PowerSourceContent(
        screenState = screenState,
        powerSource = { powerSource },
        balance = balance,
        contributionSummary = contributionSummaryState,
        contributions = contributions,
        uiEvents = { event ->
            when (event) {
                is SourceEvents.CAll -> {
                    context.openCall(event.contact)
                }

                is SourceEvents.DriveToStation -> {
                    viewModel.navigateToMap(
                        latitude = event.target.latitude,
                        longitude = event.target.longitude
                    )
                }

                else -> onNavigate(event)
            }
        }
    )
}

