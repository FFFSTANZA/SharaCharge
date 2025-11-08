package com.SharaSpot.home.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.powerly.core.model.powerly.PowerSource
import com.SharaSpot.resources.R
import com.SharaSpot.ui.theme.MyColors
import com.powerly.core.model.reliability.ReliabilityScore
import com.powerly.core.model.reliability.calculateBasicReliabilityScore
import kotlin.math.roundToInt

/**
 * Charger insight card showing quick information
 */
@Composable
fun ChargerInsightCard(
    powerSource: PowerSource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val reliabilityScore = if (powerSource.reliabilityScore > 0) {
        ReliabilityScore(totalScore = powerSource.reliabilityScore)
    } else {
        calculateBasicReliabilityScore(
            photoCount = powerSource.media.size,
            reviewCount = 0, // Will be from API
            avgRating = powerSource.rating
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header: Name and Distance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = powerSource.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MyColors.grey900
                    )
                    if (powerSource.address?.displayName != null) {
                        Text(
                            text = powerSource.address.displayName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MyColors.grey700,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Distance badge
                Row(
                    modifier = Modifier
                        .background(
                            color = MyColors.lightBlue,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_near_me_24),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = String.format("%.1f km", powerSource.distance()),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Reliability Score
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Reliability:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MyColors.grey700
                )

                // Star rating
                repeat(reliabilityScore.starRating) {
                    Text(text = "⭐", fontSize = 16.sp)
                }
                repeat(5 - reliabilityScore.starRating) {
                    Text(text = "☆", fontSize = 16.sp, color = MyColors.grey200)
                }

                Text(
                    text = "(${reliabilityScore.starRating}/5)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MyColors.grey900
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Status indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Availability status
                StatusIndicator(
                    icon = if (powerSource.isAvailable) R.drawable.ic_baseline_check_circle_24
                          else R.drawable.ic_baseline_error_24,
                    text = if (powerSource.isAvailable) "Available" else "In Use",
                    color = if (powerSource.isAvailable) MyColors.green else MyColors.red
                )

                // Connector type
                powerSource.maxConnector?.let { connector ->
                    StatusIndicator(
                        icon = R.drawable.ic_baseline_power_24,
                        text = "${connector.name ?: "CCS"} ${connector.maxPower.roundToInt()}kW",
                        color = MyColors.grey700
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Community data
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CommunityDataItem(
                    icon = "📸",
                    count = powerSource.media.size
                )
                CommunityDataItem(
                    icon = "⭐",
                    count = powerSource.rating.roundToInt(),
                    suffix = " rating"
                )
            }
        }
    }
}

@Composable
private fun StatusIndicator(
    icon: Int,
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = color
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun CommunityDataItem(
    icon: String,
    count: Int,
    suffix: String = "",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = icon, fontSize = 16.sp)
        Text(
            text = "$count$suffix",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MyColors.grey700
        )
    }
}

/**
 * Get reliability color based on score
 */
fun getReliabilityColor(score: Float): Color {
    return when {
        score >= 70 -> MyColors.green  // High reliability
        score >= 40 -> Color(0xFFF79E1B)  // Medium reliability (yellow/orange)
        score > 0 -> MyColors.red  // Low reliability
        else -> MyColors.grey200  // No data
    }
}
