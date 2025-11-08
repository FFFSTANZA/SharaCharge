package com.SharaSpot.home.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.powerly.core.model.powerly.PowerSource
import com.SharaSpot.resources.R
import com.SharaSpot.ui.containers.MyColumn
import com.SharaSpot.ui.theme.MyColors

/**
 * Get marker color based on reliability score
 * - Green: High reliability (>= 70)
 * - Yellow: Medium reliability (40-70)
 * - Red: Low reliability (< 40)
 * - Gray: No community data
 */
fun getMarkerColorFromReliability(reliabilityScore: Float): Color {
    return when {
        reliabilityScore >= 70 -> MyColors.green  // High reliability
        reliabilityScore >= 40 -> Color(0xFFF79E1B)  // Medium reliability (yellow/orange)
        reliabilityScore > 0 -> MyColors.red  // Low reliability
        else -> MyColors.grey200  // No data
    }
}

/**
 * Color-coded marker view based on reliability
 */
@Composable
fun ColorCodedMarkerView(
    powerSource: PowerSource,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    val scale = if (selected) 1.3f else 1f
    val markerColor = getMarkerColorFromReliability(powerSource.reliabilityScore)

    MyColumn(
        spacing = 4.dp,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Rating badge (if available)
        if (powerSource.rating > 0) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.padding(bottom = 2.dp)
            ) {
                Text(
                    text = "⭐ ${powerSource.rating}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // Main marker with color coding
        Box(
            modifier = Modifier
                .size((40 * scale).dp)
                .shadow(elevation = 4.dp, shape = CircleShape)
                .background(color = markerColor, shape = CircleShape)
                .border(
                    width = (2 * scale).dp,
                    color = if (selected) MaterialTheme.colorScheme.secondary else Color.White,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_power_24),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size((20 * scale).dp)
            )
        }
    }
}
