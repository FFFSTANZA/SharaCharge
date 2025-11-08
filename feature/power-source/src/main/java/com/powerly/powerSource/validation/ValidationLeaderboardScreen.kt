package com.SharaSpot.powerSource.validation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.SharaSpot.core.data.repositories.LeaderboardPeriod
import com.SharaSpot.core.data.repositories.ValidatorLeaderboardEntry
import com.powerly.core.model.api.ApiStatus
import com.SharaSpot.ui.containers.MyColumn
import com.SharaSpot.ui.containers.MyRow
import org.koin.androidx.compose.koinViewModel

/**
 * Screen showing top validators leaderboard
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValidationLeaderboardScreen(
    onBackClick: () -> Unit,
    viewModel: ValidationViewModel = koinViewModel()
) {
    var selectedPeriod by remember { mutableStateOf(LeaderboardPeriod.WEEK) }

    LaunchedEffect(selectedPeriod) {
        viewModel.loadTopValidators(period = selectedPeriod)
    }

    val topValidators by viewModel.topValidators.collectAsState()
    val validationStats by viewModel.validationStats.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Validation Leaderboard") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User stats card
            item {
                when (val stats = validationStats) {
                    is ApiStatus.Success -> {
                        ValidationStatsCard(
                            totalValidations = stats.data.totalValidations,
                            validationsThisMonth = stats.data.validationsThisMonth,
                            evCoinsEarned = stats.data.evCoinsEarned
                        )
                    }
                    else -> {}
                }
            }

            // Period selector
            item {
                PeriodSelector(
                    selectedPeriod = selectedPeriod,
                    onPeriodSelected = { selectedPeriod = it }
                )
            }

            // Leaderboard header
            item {
                Text(
                    text = "Top Validators",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Leaderboard content
            when (val validators = topValidators) {
                is ApiStatus.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                is ApiStatus.Success -> {
                    if (validators.data.isEmpty()) {
                        item {
                            EmptyLeaderboardMessage()
                        }
                    } else {
                        items(validators.data) { entry ->
                            LeaderboardEntryCard(entry = entry)
                        }
                    }
                }
                is ApiStatus.Error -> {
                    item {
                        ErrorMessage(message = validators.getMessage())
                    }
                }
            }
        }
    }
}

@Composable
private fun PeriodSelector(
    selectedPeriod: LeaderboardPeriod,
    onPeriodSelected: (LeaderboardPeriod) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LeaderboardPeriod.values().forEach { period ->
                PeriodChip(
                    period = period,
                    isSelected = period == selectedPeriod,
                    onClick = { onPeriodSelected(period) }
                )
            }
        }
    }
}

@Composable
private fun PeriodChip(
    period: LeaderboardPeriod,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val label = when (period) {
        LeaderboardPeriod.WEEK -> "This Week"
        LeaderboardPeriod.MONTH -> "This Month"
        LeaderboardPeriod.ALL_TIME -> "All Time"
    }

    Surface(
        modifier = Modifier.clip(RoundedCornerShape(20.dp)),
        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        onClick = onClick
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.secondary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun LeaderboardEntryCard(entry: ValidatorLeaderboardEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (entry.rank) {
                1 -> Color(0xFFFFD700) // Gold
                2 -> Color(0xFFC0C0C0) // Silver
                3 -> Color(0xFFCD7F32) // Bronze
                else -> Color.White
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (entry.rank <= 3) 4.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank badge
            RankBadge(rank = entry.rank)

            // User info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = entry.userName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (entry.rank <= 3) Color.White else MaterialTheme.colorScheme.primary
                )
                MyRow(spacing = 12.dp) {
                    Text(
                        text = "✅ ${entry.validationCount} validations",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (entry.rank <= 3) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "🪙 ${entry.evCoinsEarned} EVCoins",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (entry.rank <= 3) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // Trophy for top 3
            if (entry.rank <= 3) {
                Text(
                    text = "🏆",
                    fontSize = 28.sp
                )
            }
        }
    }
}

@Composable
private fun RankBadge(rank: Int) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                when (rank) {
                    1 -> Color(0xFFFFA000)
                    2 -> Color(0xFF757575)
                    3 -> Color(0xFF8D6E63)
                    else -> MaterialTheme.colorScheme.primary
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "#$rank",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun EmptyLeaderboardMessage() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "🏆",
                fontSize = 48.sp
            )
            Text(
                text = "No validators yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Be the first to start validating contributions!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ErrorMessage(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "⚠️", fontSize = 24.sp)
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFC62828)
            )
        }
    }
}
