package com.SharaSpot.powerSource.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.powerly.core.model.contribution.Contribution
import com.powerly.core.model.reliability.*
import com.SharaSpot.ui.containers.MyColumn
import com.SharaSpot.ui.containers.MyRow
import com.SharaSpot.ui.containers.MySurfaceColumn
import com.SharaSpot.ui.extensions.asBorder
import com.SharaSpot.ui.theme.MyColors

/**
 * Main reliability score card displayed prominently on the charger detail page
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReliabilityScoreCard(
    reliabilityScore: ReliabilityScore,
    photoCount: Int,
    reviewCount: Int,
    onViewBreakdown: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showBreakdown by remember { mutableStateOf(false) }

    MySurfaceColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { showBreakdown = true },
        color = Color.White,
        border = 1.dp.asBorder,
        spacing = 12.dp
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reliability Score",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Trust badge
            TrustBadge(badge = reliabilityScore.trustBadge)
        }

        // Score display with stars
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Star rating
            Column(horizontalAlignment = Alignment.Start) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = reliabilityScore.toStarDisplay(),
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "${String.format("%.1f", reliabilityScore.totalScore)}/100",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // Quick stats
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$photoCount Photos",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "$reviewCount Reviews",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // Tap to view breakdown hint
        Text(
            text = "Tap to view breakdown",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }

    // Breakdown modal
    if (showBreakdown) {
        ReliabilityScoreBreakdownSheet(
            reliabilityScore = reliabilityScore,
            onDismiss = { showBreakdown = false }
        )
    }
}

/**
 * Trust badge indicator
 */
@Composable
fun TrustBadge(
    badge: TrustBadge,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (badge) {
        TrustBadge.EXCELLENT -> Color(0xFF4CAF50) // Green
        TrustBadge.GOOD -> Color(0xFF2196F3) // Blue
        TrustBadge.FAIR -> Color(0xFFFF9800) // Orange
        TrustBadge.NEEDS_DATA -> Color(0xFF9E9E9E) // Gray
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = badge.icon,
                fontSize = 14.sp
            )
            Text(
                text = badge.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Breakdown modal showing detailed score components
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReliabilityScoreBreakdownSheet(
    reliabilityScore: ReliabilityScore,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Text(
                text = "Reliability Score Breakdown",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "This score helps you identify reliable and well-maintained charging stations based on community data.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Overall score
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Overall Score",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${String.format("%.1f", reliabilityScore.totalScore)}/100",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Component breakdown
            ScoreBreakdownItem(
                icon = "📸",
                label = "Photos",
                score = reliabilityScore.photoScore,
                maxScore = 20f,
                level = reliabilityScore.getPhotoLevel()
            )

            ScoreBreakdownItem(
                icon = "⭐",
                label = "Reviews",
                score = reliabilityScore.reviewScore,
                maxScore = 20f,
                level = reliabilityScore.getReviewLevel()
            )

            ScoreBreakdownItem(
                icon = "🌟",
                label = "Rating",
                score = reliabilityScore.ratingScore,
                maxScore = 20f,
                level = reliabilityScore.getRatingLevel()
            )

            ScoreBreakdownItem(
                icon = "🕐",
                label = "Freshness",
                score = reliabilityScore.freshnessScore,
                maxScore = 20f,
                level = reliabilityScore.getFreshnessLevel()
            )

            ScoreBreakdownItem(
                icon = "✅",
                label = "Verified",
                score = reliabilityScore.validationScore,
                maxScore = 20f,
                level = reliabilityScore.getValidationLevel()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Close button
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Close")
            }
        }
    }
}

/**
 * Individual score component in breakdown
 */
@Composable
fun ScoreBreakdownItem(
    icon: String,
    label: String,
    score: Float,
    maxScore: Float,
    level: ScoreLevel
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = icon,
                    fontSize = 20.sp
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = level.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = getLevelColor(level)
                )
                Text(
                    text = level.icon,
                    fontSize = 16.sp
                )
                Text(
                    text = "${String.format("%.1f", score)}/$maxScore",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Progress bar
        LinearProgressIndicator(
            progress = (score / maxScore).coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = getLevelColor(level),
            trackColor = Color.LightGray.copy(alpha = 0.3f)
        )
    }
}

/**
 * Get color based on score level
 */
@Composable
fun getLevelColor(level: ScoreLevel): Color {
    return when (level) {
        ScoreLevel.EXCELLENT -> Color(0xFF4CAF50) // Green
        ScoreLevel.GOOD -> Color(0xFF8BC34A) // Light Green
        ScoreLevel.FAIR -> Color(0xFFFF9800) // Orange
        ScoreLevel.POOR -> Color(0xFFF44336) // Red
        ScoreLevel.NONE -> Color(0xFF9E9E9E) // Gray
    }
}

/**
 * Compact reliability score display for list items
 */
@Composable
fun CompactReliabilityScore(
    reliabilityScore: ReliabilityScore,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Stars
        Text(
            text = "⭐".repeat(reliabilityScore.starRating),
            fontSize = 14.sp
        )
        if (reliabilityScore.starRating < 5) {
            Text(
                text = "☆".repeat(5 - reliabilityScore.starRating),
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        // Score
        Text(
            text = "(${reliabilityScore.starRating}/5)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}
