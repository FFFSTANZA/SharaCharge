package com.SharaSpot.home.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.SharaSpot.resources.R
import com.SharaSpot.ui.theme.SharaSpotColors

/**
 * Filter types for charger display
 */
enum class ChargerFilter(val displayName: String, val icon: Int? = null) {
    NEARBY("Nearby", R.drawable.baseline_near_me_24),
    MOST_RELIABLE("Most Reliable", R.drawable.ic_baseline_verified_24),
    RECENTLY_UPDATED("Recently Updated", R.drawable.ic_baseline_schedule_24),
    AVAILABLE_NOW("Available Now", R.drawable.ic_baseline_check_circle_24)
}

/**
 * Quick filters bar for home screen
 */
@Composable
fun QuickFiltersBar(
    selectedFilter: ChargerFilter,
    onFilterSelected: (ChargerFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ChargerFilter.values().forEach { filter ->
            FilterChip(
                selected = filter == selectedFilter,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        text = filter.displayName,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                leadingIcon = filter.icon?.let { iconRes ->
                    {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = null,
                            tint = if (filter == selectedFilter) Color.White else MaterialTheme.colorScheme.secondary
                        )
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondary,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White,
                    containerColor = SharaSpotColors.Surface,
                    labelColor = SharaSpotColors.OnBackground
                ),
                shape = RoundedCornerShape(20.dp),
                border = if (filter == selectedFilter) null else FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = false,
                    borderColor = SharaSpotColors.Outline
                )
            )
        }
    }
}
