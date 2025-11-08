package com.SharaSpot.home.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.powerly.core.model.powerly.Connector
import com.powerly.core.model.powerly.PowerSource
import com.SharaSpot.home.home.components.ChargerFilter
import com.SharaSpot.home.home.components.ChargerInsightCard
import com.SharaSpot.home.home.components.CommunityStatsBar
import com.SharaSpot.home.home.components.CompactSearchBar
import com.SharaSpot.home.home.components.QuickAction
import com.SharaSpot.home.home.components.QuickActionsFAB
import com.SharaSpot.home.home.components.QuickFiltersBar
import com.SharaSpot.home.home.components.getDefaultCommunityStats
import com.SharaSpot.ui.HomeUiState
import com.SharaSpot.ui.containers.MySurface
import com.SharaSpot.ui.dialogs.loading.ScreenState
import com.SharaSpot.ui.dialogs.loading.rememberBasicScreenState
import com.SharaSpot.ui.map.MapViewState
import com.SharaSpot.ui.map.MyMapView
import com.SharaSpot.ui.map.PowerSourceMarker
import com.SharaSpot.ui.map.rememberMapState
import com.SharaSpot.ui.screen.MyScreen
import com.SharaSpot.ui.theme.AppTheme

/**
 * Enhanced Home Screen Content with instant insights
 * Features:
 * - Color-coded map with reliability indicators
 * - Quick filters (Nearby, Most Reliable, Recently Updated, Available Now)
 * - Charger cards with instant insights
 * - Community stats banner
 * - Search functionality
 * - Quick actions FAB
 */
@Composable
internal fun EnhancedHomeScreenContent(
    screenState: ScreenState = rememberBasicScreenState(),
    mapState: MapViewState,
    uiState: HomeUiState,
    selectedPowerSource: () -> PowerSource?,
    nearStations: () -> List<PowerSource>,
    uiEvents: (HomeEvents) -> Unit,
) {
    val isLoggedIn by remember { uiState.isLoggedIn }
    val hasLocation by remember { mapState.locationEnabled }
    val supportMap = remember { uiState.supportMap.value }

    var selectedFilter by remember { mutableStateOf(ChargerFilter.NEARBY) }
    var showMapView by remember { mutableStateOf(true) }

    // Filter and sort chargers based on selected filter
    val filteredChargers = remember(nearStations(), selectedFilter) {
        val stations = nearStations()
        when (selectedFilter) {
            ChargerFilter.NEARBY -> stations.sortedBy { it.distance() }
            ChargerFilter.MOST_RELIABLE -> stations.sortedByDescending { it.reliabilityScore }
            ChargerFilter.RECENTLY_UPDATED -> stations.sortedByDescending { it.lastScoreUpdate ?: 0 }
            ChargerFilter.AVAILABLE_NOW -> stations.filter { it.isAvailable }
        }
    }

    MyScreen(
        header = {
            Header(
                loggedIn = isLoggedIn,
                balance = { uiState.balance.value },
                currency = { uiState.currency.value },
                onLogin = { uiEvents(HomeEvents.OnLogin) },
                onSupport = { uiEvents(HomeEvents.OnSupport) },
                onBalance = { uiEvents(HomeEvents.OnBalance) }
            )
        },
        background = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize(),
        spacing = 0.dp,
        screenState = screenState
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)  // Space for FAB
            ) {
                // Community Stats Banner
                item {
                    CommunityStatsBar(stats = getDefaultCommunityStats())
                }

                // Search Bar
                item {
                    CompactSearchBar(
                        onClick = { /* Open search dialog */ },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Quick Filters
                item {
                    QuickFiltersBar(
                        selectedFilter = selectedFilter,
                        onFilterSelected = { selectedFilter = it }
                    )
                }

                // Map View (Compact)
                if (hasLocation && supportMap && showMapView) {
                    item {
                        SectionCompactMap(
                            mapState = mapState,
                            selectedPowerSource = selectedPowerSource,
                            powerSources = nearStations,
                            onMapClick = { uiEvents(HomeEvents.OpenMap()) },
                            onPowerSource = { uiEvents(HomeEvents.OpenMap(it)) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }

                // Charger Cards List
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(
                    items = filteredChargers,
                    key = { it.id }
                ) { charger ->
                    ChargerInsightCard(
                        powerSource = charger,
                        onClick = { uiEvents(HomeEvents.OpenPowerSource(charger)) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Floating Action Button
            QuickActionsFAB(
                onActionClick = { action ->
                    when (action) {
                        QuickAction.CONTRIBUTE -> { /* Navigate to contribution screen */ }
                        QuickAction.REPORT_ISSUE -> { /* Navigate to report screen */ }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .zIndex(10f)
            )
        }
    }
}

/**
 * Compact map view for home screen
 */
@Composable
private fun SectionCompactMap(
    mapState: MapViewState,
    selectedPowerSource: () -> PowerSource?,
    powerSources: () -> List<PowerSource>,
    onPowerSource: (PowerSource) -> Unit,
    onMapClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMap by remember { mutableStateOf(false) }
    MySurface(
        surfaceModifier = modifier.height(250.dp),
        color = Color.White,
        cornerRadius = 12.dp
    ) {
        MyMapView(
            mapState = mapState,
            onMapClick = onMapClick,
            onMapLongClick = onMapClick,
            onMapLoaded = { showMap = true }
        ) {
            powerSources().mapIndexed { index, ps ->
                PowerSourceMarker(
                    powerSource = ps,
                    selected = {
                        (index == 0 && selectedPowerSource() == null)
                                || selectedPowerSource()?.id == ps.id
                    },
                    showRating = true,
                    onClick = { onPowerSource(ps) }
                )
            }
        }
        if (!showMap) MapPlaceHolder()
    }
}

@Preview
@Composable
private fun EnhancedHome_Preview() {
    val ps1 = PowerSource(
        id = "1",
        title = "Station 1",
        distance = 0.02,
        inUse = false,
        reserved = 0,
        onlineStatus = 1,
        isExternal = false,
        reliabilityScore = 85f,
        rating = 4.5,
        connectors = listOf(
            Connector(
                name = "CCS",
                maxPower = 50.0
            )
        )
    )
    val nearPowerSources = listOf(ps1, ps1.copy(id = "2", reliabilityScore = 45f), ps1.copy(id = "3", reliabilityScore = 25f))

    AppTheme {
        EnhancedHomeScreenContent(
            mapState = rememberMapState(
                initialLocation = null,
                locationEnabled = true
            ),
            uiState = HomeUiState().apply {
                isLoggedIn.value = true
                supportMap.value = true
            },
            nearStations = { nearPowerSources },
            selectedPowerSource = { null },
            uiEvents = {}
        )
    }
}
