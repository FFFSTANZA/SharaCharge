package com.SharaSpot.home.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SharaSpot.ui.theme.SharaSpotColors

/**
 * Community stat data
 */
data class CommunityStat(
    val emoji: String,
    val text: String
)

/**
 * Scrollable community stats banner
 */
@Composable
fun CommunityStatsBar(
    stats: List<CommunityStat>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SharaSpotColors.InfoLight)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        stats.forEach { stat ->
            StatCard(stat = stat)
        }
    }
}

@Composable
private fun StatCard(stat: CommunityStat) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stat.emoji,
                fontSize = 20.sp
            )
            Text(
                text = stat.text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = SharaSpotColors.OnBackground
            )
        }
    }
}

/**
 * Get default community stats
 * In production, these would come from the API
 */
fun getDefaultCommunityStats(): List<CommunityStat> {
    return listOf(
        CommunityStat("🎉", "45 contributions today!"),
        CommunityStat("👥", "1,234 active users this month"),
        CommunityStat("📸", "5,678 photos uploaded"),
        CommunityStat("✅", "98% chargers verified")
    )
}
