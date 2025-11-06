package com.SharaSpot.account.rewards

/**
 * EXAMPLE: How to integrate EVCoins rewards with contribution flow
 *
 * This file shows example code for integrating the rewards system.
 * Copy relevant sections to your actual ViewModels and Screens.
 */

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.SharaSpot.core.data.repositories.RewardsRepository
import com.SharaSpot.core.data.repositories.ContributionRepository
import com.powerly.core.model.contribution.*
import com.powerly.core.model.api.ApiStatus
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

// ============================================================================
// EXAMPLE 1: Enhanced ContributionViewModel with Rewards
// ============================================================================

class ContributionViewModelWithRewards(
    private val contributionRepository: ContributionRepository,
    private val rewardsRepository: RewardsRepository
) : ViewModel() {

    /**
     * Example: Creating a contribution and awarding EVCoins
     */
    fun createContributionWithRewards(
        userId: String,
        chargerId: String,
        chargerName: String,
        cityName: String,
        request: CreateContributionRequest
    ) {
        viewModelScope.launch {
            // 1. Create the contribution
            val result = contributionRepository.createContribution(request)

            when (result) {
                is ApiStatus.Success -> {
                    val contribution = result.data

                    // 2. Check if this is the first contribution to this charger
                    val contributions = contributionRepository.getContributions(chargerId)
                    val isFirstToCharger = when (contributions) {
                        is ApiStatus.Success -> contributions.data.size == 1
                        else -> false
                    }

                    // 3. Award EVCoins with the new rewards system
                    rewardsRepository.awardCoinsForContribution(
                        userId = userId,
                        contributionType = contribution.type,
                        chargerId = chargerId,
                        chargerName = chargerName,
                        contributionId = contribution.id,
                        isFirstToCharger = isFirstToCharger,
                        cityName = cityName
                    ).onSuccess { awardResult ->
                        // 4. Handle the award result
                        println("Awarded ${awardResult.transaction?.amount} EVCoins!")

                        // Check for new badges
                        if (awardResult.badgesEarned.isNotEmpty()) {
                            println("New badges: ${awardResult.badgesEarned.map { it.displayName }}")
                        }

                        // Check for rank up
                        if (awardResult.rankChanged && awardResult.newRank != null) {
                            println("Rank up to ${awardResult.newRank.displayName}!")
                        }

                        // Show animation (handled by UI)
                    }.onFailure { error ->
                        println("Failed to award coins: ${error.message}")
                    }
                }
                is ApiStatus.Error -> {
                    println("Failed to create contribution: ${result.message}")
                }
            }
        }
    }

    /**
     * Example: Validating a contribution and earning coins
     */
    fun validateContributionWithReward(
        userId: String,
        contributionId: String,
        chargerId: String,
        chargerName: String,
        isValid: Boolean
    ) {
        viewModelScope.launch {
            // 1. Validate the contribution
            contributionRepository.validateContribution(contributionId, userId, isValid)

            // 2. Award validation coins (only for thumbs up)
            if (isValid) {
                rewardsRepository.awardValidationCoins(
                    userId = userId,
                    contributionId = contributionId,
                    chargerId = chargerId,
                    chargerName = chargerName
                ).onSuccess {
                    println("Earned 5 EVCoins for validation!")
                }.onFailure { error ->
                    // Likely hit daily limit
                    println("Validation failed: ${error.message}")
                }
            }
        }
    }
}

// ============================================================================
// EXAMPLE 2: Profile Screen with Rewards
// ============================================================================

@Composable
fun ProfileScreenWithRewardsExample(
    userId: String,
    onNavigateToHistory: () -> Unit
) {
    val rewardsViewModel: RewardsViewModel = koinViewModel()
    val userRewards by remember { rewardsViewModel.userRewards }
    val recentCoinAward by remember { rewardsViewModel.recentCoinAward }

    // Load rewards on screen open
    LaunchedEffect(userId) {
        rewardsViewModel.loadUserRewards(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Rewards Card
        RewardsCard(
            userRewards = userRewards,
            onClickViewHistory = onNavigateToHistory,
            onClickDailyCheckIn = {
                rewardsViewModel.claimDailyCheckIn(userId)
            }
        )

        // Badges Showcase
        if (userRewards != null && userRewards.badges.isNotEmpty()) {
            BadgesShowcase(
                earnedBadges = userRewards.badges
            )
        }

        // Statistics
        if (userRewards != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📊 Statistics", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Total Contributions: ${userRewards.contributionCount}")
                    Text("Validations: ${userRewards.validationCount}")
                    Text("Cities Explored: ${userRewards.citiesContributed.size}")
                }
            }
        }
    }

    // Show coin earning animation
    CoinEarningAnimation(
        coinAward = recentCoinAward,
        onDismiss = { rewardsViewModel.dismissCoinAnimation() }
    )
}

// ============================================================================
// EXAMPLE 3: Contribution Screen with Animation
// ============================================================================

@Composable
fun ContributionScreenWithAnimation(
    chargerId: String,
    chargerName: String
) {
    val rewardsViewModel: RewardsViewModel = koinViewModel()
    val coinAward by remember { rewardsViewModel.recentCoinAward }

    Box(modifier = Modifier.fillMaxSize()) {
        // Your contribution form UI here
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Add Photo, Review, etc.")

            Button(onClick = {
                // After successful contribution
                rewardsViewModel.awardCoinsForContribution(
                    userId = "user123",
                    contributionType = ContributionType.PHOTO,
                    chargerId = chargerId,
                    chargerName = chargerName,
                    contributionId = "contribution123",
                    isFirstToCharger = false
                )
            }) {
                Text("Submit Contribution")
            }
        }

        // Overlay coin animation
        CoinEarningAnimation(
            coinAward = coinAward,
            onDismiss = { rewardsViewModel.dismissCoinAnimation() }
        )
    }
}

// ============================================================================
// EXAMPLE 4: Home Screen with Daily Check-in
// ============================================================================

@Composable
fun HomeScreenWithCheckIn(userId: String) {
    val rewardsViewModel: RewardsViewModel = koinViewModel()
    val userRewards by remember { rewardsViewModel.userRewards }
    val successMessage by remember { rewardsViewModel.successMessage }

    LaunchedEffect(userId) {
        rewardsViewModel.loadUserRewards(userId)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Compact rewards display
        RewardsCardCompact(
            userRewards = userRewards,
            onClick = { /* Navigate to full rewards */ }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Daily check-in button
        if (userRewards?.canCheckInToday() == true) {
            Button(
                onClick = { rewardsViewModel.claimDailyCheckIn(userId) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("✨ Claim Daily Check-in (+5 EVCoins)")
            }
        }

        // Show success message
        if (successMessage != null) {
            Snackbar(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(successMessage!!)
            }

            LaunchedEffect(successMessage) {
                kotlinx.coroutines.delay(3000)
                rewardsViewModel.clearMessages()
            }
        }
    }
}

// ============================================================================
// EXAMPLE 5: Leaderboard Integration (Future)
// ============================================================================

@Composable
fun LeaderboardScreenExample() {
    val rewardsViewModel: RewardsViewModel = koinViewModel()

    // This would need to be implemented in RewardsViewModel:
    // val leaderboard by remember { rewardsViewModel.leaderboard }
    // val userRankPosition by remember { rewardsViewModel.userRankPosition }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("🏆 Top Contributors", style = MaterialTheme.typography.headlineMedium)

        // Display leaderboard
        // leaderboard.forEach { user ->
        //     LeaderboardItem(user)
        // }
    }
}

/**
 * Key Integration Points Summary:
 *
 * 1. When creating contribution:
 *    - Call rewardsRepository.awardCoinsForContribution()
 *    - Show CoinEarningAnimation
 *
 * 2. When validating contribution:
 *    - Call rewardsRepository.awardValidationCoins()
 *    - Handle daily limit error
 *
 * 3. In profile screen:
 *    - Display RewardsCard
 *    - Show BadgesShowcase
 *    - Allow daily check-in
 *
 * 4. In home/main screen:
 *    - Show RewardsCardCompact
 *    - Offer daily check-in button
 *
 * 5. Navigation:
 *    - Add route to TransactionHistoryScreen
 *    - Link from RewardsCard
 *
 * See EVCOINS_REWARDS_INTEGRATION.md for complete documentation.
 */
