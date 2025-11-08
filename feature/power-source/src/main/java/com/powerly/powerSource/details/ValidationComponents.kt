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
import com.powerly.core.model.contribution.ContributionType
import com.powerly.core.model.contribution.ConfidenceLevel
import com.powerly.core.model.contribution.getConfidenceLevel
import com.powerly.core.model.contribution.getValidationPrompt
import com.powerly.core.model.contribution.formatRelativeTime
import com.powerly.core.model.contribution.needsValidation
import com.SharaSpot.ui.containers.MyRow

/**
 * Validation buttons for different contribution types
 */
@Composable
fun ValidationButtons(
    contribution: Contribution,
    currentUserId: String,
    onValidate: (contributionId: String, isValidation: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // Don't show validation buttons for own contributions
    if (contribution.userId == currentUserId) return

    val hasValidated = currentUserId in contribution.validatedBy
    val hasInvalidated = currentUserId in contribution.invalidatedBy

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (contribution.type) {
            ContributionType.PHOTO -> {
                ValidationButton(
                    icon = "👍",
                    text = "Accurate",
                    isSelected = hasValidated,
                    count = contribution.validatedBy.size,
                    onClick = { onValidate(contribution.id, true) }
                )
                ValidationButton(
                    icon = "👎",
                    text = "Not Accurate",
                    isSelected = hasInvalidated,
                    count = contribution.invalidatedBy.size,
                    onClick = { onValidate(contribution.id, false) },
                    isNegative = true
                )
            }
            ContributionType.REVIEW -> {
                ValidationButton(
                    icon = "👍",
                    text = "Helpful",
                    isSelected = hasValidated,
                    count = contribution.validatedBy.size,
                    onClick = { onValidate(contribution.id, true) }
                )
            }
            ContributionType.WAIT_TIME,
            ContributionType.PLUG_CHECK,
            ContributionType.STATUS_UPDATE -> {
                ValidationButton(
                    icon = "✅",
                    text = if (contribution.type == ContributionType.WAIT_TIME) "Still Accurate"
                           else if (contribution.type == ContributionType.PLUG_CHECK) "Confirmed"
                           else "Current",
                    isSelected = hasValidated,
                    count = contribution.validatedBy.size,
                    onClick = { onValidate(contribution.id, true) }
                )
            }
        }

        // Show validation count
        if (contribution.validatedBy.isNotEmpty() || contribution.invalidatedBy.isNotEmpty()) {
            Text(
                text = "• ${contribution.validationCount} votes",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

/**
 * Individual validation button
 */
@Composable
private fun ValidationButton(
    icon: String,
    text: String,
    isSelected: Boolean,
    count: Int,
    onClick: () -> Unit,
    isNegative: Boolean = false
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = when {
            isSelected && isNegative -> Color(0xFFFFEBEE)
            isSelected -> Color(0xFFE8F5E9)
            else -> Color.White
        },
        border = BorderStroke(
            1.dp,
            when {
                isSelected && isNegative -> Color(0xFFEF5350)
                isSelected -> Color(0xFF4CAF50)
                else -> MaterialTheme.colorScheme.outline
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                fontSize = 14.sp
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = when {
                    isSelected && isNegative -> Color(0xFFD32F2F)
                    isSelected -> Color(0xFF2E7D32)
                    else -> MaterialTheme.colorScheme.secondary
                },
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            if (count > 0) {
                Text(
                    text = "($count)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

/**
 * Confidence badge showing verification status
 */
@Composable
fun ConfidenceBadge(
    contribution: Contribution,
    modifier: Modifier = Modifier
) {
    val confidenceLevel = contribution.getConfidenceLevel()

    if (confidenceLevel == ConfidenceLevel.MEDIUM && contribution.validatedBy.size < 3) {
        return // Don't show badge for normal items without significant validation
    }

    Surface(
        modifier = modifier,
        color = when (confidenceLevel) {
            ConfidenceLevel.HIGH -> Color(0xFF4CAF50)
            ConfidenceLevel.MEDIUM -> Color(0xFFFFA726)
            ConfidenceLevel.LOW -> Color(0xFFEF5350)
        },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = confidenceLevel.icon,
                fontSize = 12.sp
            )
            Text(
                text = confidenceLevel.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Validation info showing last verified time and count
 */
@Composable
fun ValidationInfo(
    contribution: Contribution,
    modifier: Modifier = Modifier
) {
    if (contribution.lastValidatedAt == 0L && contribution.validatedBy.isEmpty()) return

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "✓",
            fontSize = 12.sp,
            color = Color(0xFF4CAF50)
        )

        val infoText = when {
            contribution.lastValidatedAt > 0L && contribution.validatedBy.isNotEmpty() -> {
                "Verified by ${contribution.validatedBy.size} user${if (contribution.validatedBy.size == 1) "" else "s"} • Last ${formatRelativeTime(contribution.lastValidatedAt)}"
            }
            contribution.validatedBy.isNotEmpty() -> {
                "Verified by ${contribution.validatedBy.size} user${if (contribution.validatedBy.size == 1) "" else "s"}"
            }
            contribution.lastValidatedAt > 0L -> {
                "Last verified ${formatRelativeTime(contribution.lastValidatedAt)}"
            }
            else -> null
        }

        infoText?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

/**
 * Smart prompt to encourage validation
 */
@Composable
fun ValidationPrompt(
    contribution: Contribution,
    modifier: Modifier = Modifier
) {
    val promptMessage = contribution.getValidationPrompt() ?: return

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFFFFF9C4),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFFFDD835))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "💡",
                fontSize = 20.sp
            )
            Text(
                text = promptMessage,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF827717),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Validation stats for a user's contribution activity
 */
@Composable
fun ValidationStatsCard(
    totalValidations: Int,
    validationsThisMonth: Int,
    evCoinsEarned: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🛡️ Your Validation Activity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ValidationStatItem(
                    value = totalValidations.toString(),
                    label = "Total\nValidations",
                    icon = "✅"
                )
                ValidationStatItem(
                    value = validationsThisMonth.toString(),
                    label = "This\nMonth",
                    icon = "📅"
                )
                ValidationStatItem(
                    value = evCoinsEarned.toString(),
                    label = "EVCoins\nEarned",
                    icon = "🪙"
                )
            }

            // Progress to next badge
            if (totalValidations < 100) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Progress to 'Data Guardian' Badge",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Medium
                    )
                    LinearProgressIndicator(
                        progress = totalValidations / 100f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF4CAF50),
                        trackColor = Color(0xFFC8E6C9)
                    )
                    Text(
                        text = "${100 - totalValidations} more validations to unlock badge",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            } else {
                // Badge earned
                Surface(
                    color = Color(0xFFFFD700),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🏆 Data Guardian Badge Earned!",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF827717)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ValidationStatItem(
    value: String,
    label: String,
    icon: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = icon,
            fontSize = 28.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Daily validation limit indicator
 */
@Composable
fun DailyValidationLimitIndicator(
    validationsToday: Int,
    maxValidations: Int,
    modifier: Modifier = Modifier
) {
    if (validationsToday >= maxValidations) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            color = Color(0xFFFFEBEE),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color(0xFFEF5350))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚠️",
                    fontSize = 20.sp
                )
                Text(
                    text = "Daily validation limit reached ($maxValidations/$maxValidations). Come back tomorrow!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFC62828)
                )
            }
        }
    } else if (validationsToday >= maxValidations - 2) {
        // Warning when approaching limit
        Text(
            text = "⚠️ ${maxValidations - validationsToday} validations left today",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFFFF6F00),
            modifier = modifier
        )
    }
}

/**
 * Enhanced photo card with validation overlay
 */
@Composable
fun PhotoCardWithValidation(
    contribution: Contribution,
    currentUserId: String,
    onPhotoClick: (String) -> Unit,
    onValidate: (contributionId: String, isValidation: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(160.dp)
            .height(200.dp)
            .clickable { onPhotoClick(contribution.photoUrl ?: "") },
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Photo content here (using existing NetworkImage component)

            // Confidence badge
            ConfidenceBadge(
                contribution = contribution,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )

            // Validation buttons overlay
            if (contribution.userId != currentUserId) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ValidationButtons(
                        contribution = contribution,
                        currentUserId = currentUserId,
                        onValidate = onValidate,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
