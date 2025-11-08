package com.SharaSpot.powerSource.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.powerly.core.model.contribution.Contribution
import com.powerly.core.model.contribution.ContributionSummary
import com.powerly.core.model.contribution.ContributionType
import com.SharaSpot.resources.R
import com.SharaSpot.ui.components.NetworkImage
import com.SharaSpot.ui.components.RatingBar
import com.SharaSpot.ui.containers.MyColumn
import com.SharaSpot.ui.containers.MyRow
import com.SharaSpot.ui.containers.MySurfaceColumn
import com.SharaSpot.ui.extensions.asBorder
import com.SharaSpot.ui.theme.SharaSpotColors
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * Photo gallery with horizontal scrolling and category filters
 */
@Composable
fun CommunityPhotoGallery(
    contributions: List<Contribution>,
    onPhotoClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Charger", "Plugs", "Parking", "Amenities")

    val photoContributions = contributions.filter { it.type == ContributionType.PHOTO && it.photoUrl != null }
    val filteredPhotos = if (selectedCategory == "All") {
        photoContributions
    } else {
        photoContributions.filter {
            it.photoCategory?.lowercase() == selectedCategory.lowercase()
        }
    }

    if (photoContributions.isEmpty()) return

    MySurfaceColumn(
        modifier = modifier.padding(16.dp),
        color = Color.White,
        border = 1.dp.asBorder,
        spacing = 12.dp
    ) {
        // Title
        Text(
            text = "Community Photos",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // Category filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                CategoryChip(
                    label = category,
                    isSelected = category == selectedCategory,
                    onClick = { selectedCategory = category }
                )
            }
        }

        // Photo carousel
        if (filteredPhotos.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                filteredPhotos.forEach { contribution ->
                    PhotoCard(
                        contribution = contribution,
                        onClick = { onPhotoClick(contribution.photoUrl!!) }
                    )
                }
            }
        } else {
            Text(
                text = "No photos in this category",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
        border = if (!isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun PhotoCard(
    contribution: Contribution,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .fillMaxHeight()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            NetworkImage(
                src = contribution.photoUrl ?: "",
                description = "Community photo",
                modifier = Modifier.fillMaxSize(),
                scale = ContentScale.Crop
            )

            // Contributor info overlay
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .background(
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = contribution.userName,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = getRelativeTime(contribution.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

/**
 * Reviews section with average rating and recent reviews
 */
@Composable
fun CommunityReviews(
    summary: ContributionSummary,
    contributions: List<Contribution>,
    currentUserId: String = "current_user", // TODO: Get from auth
    onValidate: (contributionId: String, isValidation: Boolean) -> Unit = { _, _ -> },
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val reviewContributions = contributions
        .filter { it.type == ContributionType.REVIEW && it.rating != null }
        .sortedByDescending { it.timestamp }
        .take(5)

    if (reviewContributions.isEmpty() && summary.averageRating == null) return

    MySurfaceColumn(
        modifier = modifier.padding(16.dp),
        color = Color.White,
        border = 1.dp.asBorder,
        spacing = 12.dp
    ) {
        // Header with average rating
        MyRow(
            modifier = Modifier.fillMaxWidth(),
            spacing = 8.dp,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reviews",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.weight(1f))

            if (summary.averageRating != null) {
                Text(
                    text = String.format("%.1f", summary.averageRating),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    painter = painterResource(id = R.drawable.outline_info_24), // TODO: Use star icon
                    contentDescription = "Rating",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Average rating stars
        if (summary.averageRating != null) {
            RatingBar(
                rating = summary.averageRating.toDouble(),
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Recent reviews
        reviewContributions.forEach { review ->
            ReviewCard(
                contribution = review,
                currentUserId = currentUserId,
                onValidate = onValidate
            )
        }

        // View all button
        if (reviewContributions.isNotEmpty()) {
            TextButton(
                onClick = onViewAllClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "View All Reviews",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ReviewCard(
    contribution: Contribution,
    currentUserId: String = "current_user", // TODO: Get from auth
    onValidate: (contributionId: String, isValidation: Boolean) -> Unit = { _, _ -> }
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Reviewer name, timestamp, and confidence badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = contribution.userName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                ConfidenceBadge(contribution = contribution)
            }
            Text(
                text = getRelativeTime(contribution.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        // Rating stars
        contribution.rating?.let { rating ->
            RatingBar(
                rating = rating.toDouble(),
                starSize = 16.dp
            )
        }

        // Comment
        contribution.comment?.let { comment ->
            Text(
                text = comment,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Validation info
        ValidationInfo(contribution = contribution)

        // Validation buttons
        ValidationButtons(
            contribution = contribution,
            currentUserId = currentUserId,
            onValidate = onValidate
        )

        Divider(modifier = Modifier.padding(top = 8.dp))
    }
}

/**
 * Real-time insights cards
 */
@Composable
fun RealTimeInsightsCards(
    summary: ContributionSummary,
    contributions: List<Contribution> = emptyList(),
    currentUserId: String = "current_user", // TODO: Get from auth
    onValidate: (contributionId: String, isValidation: Boolean) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    MySurfaceColumn(
        modifier = modifier.padding(16.dp),
        color = Color.White,
        border = 1.dp.asBorder,
        spacing = 12.dp
    ) {
        Text(
            text = "Real-Time Insights",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // Wait Time Card
        summary.latestWaitTime?.let { waitTime ->
            val waitTimeContribution = contributions.firstOrNull {
                it.type == ContributionType.WAIT_TIME && it.waitTimeMinutes == waitTime.minutes
            }
            WaitTimeCard(
                minutes = waitTime.minutes,
                queueLength = waitTime.queueLength,
                timestamp = waitTime.timestamp,
                isStale = waitTime.isStale,
                contribution = waitTimeContribution,
                currentUserId = currentUserId,
                onValidate = onValidate
            )
        }

        // Plug Status Card
        if (summary.plugStatusList.isNotEmpty()) {
            PlugStatusCard(
                plugStatusList = summary.plugStatusList,
                contributions = contributions,
                currentUserId = currentUserId,
                onValidate = onValidate
            )
        }

        // Availability Card
        summary.currentStatus?.let { status ->
            val statusContribution = contributions.firstOrNull {
                it.chargerStatus == status
            }
            AvailabilityCard(
                status = status.displayName,
                timestamp = summary.recentContributions
                    .firstOrNull { it.chargerStatus != null }?.timestamp ?: 0L,
                contribution = statusContribution,
                currentUserId = currentUserId,
                onValidate = onValidate
            )
        }
    }
}

@Composable
private fun WaitTimeCard(
    minutes: Int,
    queueLength: Int?,
    timestamp: Long,
    isStale: Boolean,
    contribution: Contribution? = null,
    currentUserId: String = "current_user",
    onValidate: (contributionId: String, isValidation: Boolean) -> Unit = { _, _ -> }
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isStale) Color(0xFFFFF3CD)
                           else if (minutes < 10) Color(0xFFD4EDDA)
                           else Color(0xFFFFF3CD)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⏱️ Current Wait",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$minutes min",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (minutes < 10) Color(0xFF155724) else Color(0xFF856404)
                )
            }

            queueLength?.let {
                Text(
                    text = "$it vehicles in queue",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Text(
                text = if (isStale) "⚠️ Data may be outdated (${getRelativeTime(timestamp)})"
                      else "Updated ${getRelativeTime(timestamp)}",
                style = MaterialTheme.typography.labelSmall,
                color = if (isStale) Color(0xFF856404) else MaterialTheme.colorScheme.secondary
            )

            // Validation prompt for stale data
            if (isStale && contribution != null) {
                ValidationPrompt(contribution = contribution)
            }

            // Validation buttons and info
            contribution?.let {
                ValidationInfo(contribution = it)
                ValidationButtons(
                    contribution = it,
                    currentUserId = currentUserId,
                    onValidate = onValidate
                )
            }
        }
    }
}

@Composable
private fun PlugStatusCard(
    plugStatusList: List<com.SharaSpot.core.model.contribution.PlugStatusInfo>,
    contributions: List<Contribution> = emptyList(),
    currentUserId: String = "current_user",
    onValidate: (contributionId: String, isValidation: Boolean) -> Unit = { _, _ -> }
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE7F3FF)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "🔌 Plug Status",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            plugStatusList.forEach { plugStatus ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${plugStatus.plugType} ${plugStatus.powerOutput ?: ""}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Verified by ${plugStatus.verificationCount} user${if (plugStatus.verificationCount == 1) "" else "s"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "Last ${getRelativeTime(plugStatus.lastVerified)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        Surface(
                            color = if (plugStatus.isWorking) Color(0xFF28A745) else Color(0xFFDC3545),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (plugStatus.isWorking) "✓ Working" else "✗ Not Working",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Find contribution for this plug
                    val plugContribution = contributions.firstOrNull {
                        it.type == ContributionType.PLUG_CHECK &&
                        it.plugType == plugStatus.plugType &&
                        it.lastValidatedAt == plugStatus.lastVerified
                    }

                    // Validation prompt if data is old (> 7 days)
                    val isStale = (System.currentTimeMillis() - plugStatus.lastVerified) > (7 * 24 * 60 * 60 * 1000L)
                    if (isStale && plugContribution != null) {
                        ValidationPrompt(contribution = plugContribution)
                    }

                    // Validation buttons
                    plugContribution?.let {
                        ValidationButtons(
                            contribution = it,
                            currentUserId = currentUserId,
                            onValidate = onValidate
                        )
                    }
                }

                // Divider between plugs
                if (plugStatus != plugStatusList.last()) {
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}

@Composable
private fun AvailabilityCard(
    status: String,
    timestamp: Long,
    contribution: Contribution? = null,
    currentUserId: String = "current_user",
    onValidate: (contributionId: String, isValidation: Boolean) -> Unit = { _, _ -> }
) {
    val (backgroundColor, statusColor) = when (status.lowercase()) {
        "available" -> Color(0xFFD4EDDA) to Color(0xFF155724)
        "busy" -> Color(0xFFFFF3CD) to Color(0xFF856404)
        "not working" -> Color(0xFFF8D7DA) to Color(0xFF721C24)
        else -> Color(0xFFE2E3E5) to Color(0xFF383D41)
    }

    val isStale = (System.currentTimeMillis() - timestamp) > (2 * 60 * 60 * 1000L) // 2 hours

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📍 Availability",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = status,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }

            Text(
                text = if (isStale) "⚠️ Data may be outdated (${getRelativeTime(timestamp)})"
                      else "Last updated ${getRelativeTime(timestamp)}",
                style = MaterialTheme.typography.labelSmall,
                color = if (isStale) Color(0xFF856404) else MaterialTheme.colorScheme.secondary
            )

            // Validation prompt for stale data
            if (isStale && contribution != null) {
                ValidationPrompt(contribution = contribution)
            }

            // Validation buttons and info
            contribution?.let {
                ValidationInfo(contribution = it)
                ValidationButtons(
                    contribution = it,
                    currentUserId = currentUserId,
                    onValidate = onValidate
                )
            }
        }
    }
}

/**
 * Community stats summary
 */
@Composable
fun CommunityStats(
    summary: ContributionSummary,
    modifier: Modifier = Modifier
) {
    MySurfaceColumn(
        modifier = modifier.padding(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        border = 1.dp.asBorder,
        spacing = 8.dp
    ) {
        Text(
            text = "Community Activity",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = "📸",
                value = "${summary.totalPhotos}",
                label = "photos"
            )

            summary.averageRating?.let {
                StatItem(
                    icon = "⭐",
                    value = String.format("%.1f/5", it),
                    label = "from ${summary.totalContributions} reviews"
                )
            }

            StatItem(
                icon = "✓",
                value = "${summary.totalContributions}",
                label = "contributions this month"
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: String,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = icon,
            fontSize = 24.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

/**
 * Helper function to convert timestamp to relative time
 */
private fun getRelativeTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60_000 -> "just now"
        diff < 3600_000 -> "${diff / 60_000} min ago"
        diff < 86400_000 -> "${diff / 3600_000} hours ago"
        diff < 604800_000 -> "${diff / 86400_000} days ago"
        else -> {
            val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
            dateFormat.format(Date(timestamp))
        }
    }
}

/**
 * Badge for highlighting recent contributions (< 24 hours)
 */
@Composable
fun RecentBadge() {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = CircleShape
    ) {
        Text(
            text = "NEW",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}
