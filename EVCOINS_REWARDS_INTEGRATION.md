# EVCoins Rewards System - Integration Guide

## Overview

The EVCoins reward system has been implemented to gamify user contributions in SharaCharge. This document explains how to integrate the system with existing features.

## Components

### 1. Data Models
- **`UserRewards`**: User's reward profile (coins, rank, badges, stats)
- **`CoinTransaction`**: Individual coin transaction record
- **`RewardRank`**: Bronze, Silver, Gold, Platinum ranks
- **`RewardBadge`**: Achievement badges

**Location:** `core/model/src/main/java/com/powerly/core/model/rewards/`

### 2. Repository
- **`RewardsRepository`**: Interface for reward operations
- **`RewardsRepositoryImpl`**: Implementation with in-memory storage (ready for Firebase)

**Location:**
- Interface: `core/data/src/main/java/com/powerly/core/data/repositories/RewardsRepository.kt`
- Implementation: `core/data/src/main/java/com/powerly/core/data/repoImpl/RewardsRepositoryImpl.kt`

### 3. ViewModel
- **`RewardsViewModel`**: Manages rewards state and operations

**Location:** `feature/main/account/src/main/java/com/powerly/account/rewards/RewardsViewModel.kt`

### 4. UI Components
- **`RewardsCard`**: Display rewards in profile
- **`TransactionHistoryScreen`**: View transaction history
- **`CoinEarningAnimation`**: Animated coin award notification
- **`BadgesShowcase`**: Display earned badges

**Location:** `feature/main/account/src/main/java/com/powerly/account/rewards/`

## EVCoins Earning Schedule

| Action | Base Coins | Bonus |
|--------|-----------|-------|
| Add Photo | 20 | +50 if first to charger |
| Write Review | 30 | +50 if first to charger |
| Report Wait Time | 10 | +50 if first to charger |
| Verify Plug | 25 | +50 if first to charger |
| Update Status | 15 | +50 if first to charger |
| Validate Contribution | 5 | Max 10/day |
| Daily Check-in | 5 | Once per day |

## Rank System

- 🥉 **Bronze**: 0-500 coins
- 🥈 **Silver**: 500-1500 coins
- 🥇 **Gold**: 1500-3000 coins
- 💎 **Platinum**: 3000+ coins

## Badges

1. **First Timer** - First contribution
2. **Photographer** - 50 photos uploaded
3. **Reviewer Pro** - 25 reviews written
4. **Data Guardian** - 100 validations
5. **Early Bird** - First to add new charger
6. **Community Hero** - 1000 EVCoins earned
7. **Tamil Nadu Explorer** - Contributed to 10 different cities

## Integration Instructions

### Step 1: Inject RewardsRepository and RewardsViewModel

In your ViewModel or Screen:

```kotlin
@KoinViewModel
class YourViewModel(
    private val rewardsRepository: RewardsRepository
) : ViewModel() {
    // Your code
}
```

Or in a Composable:

```kotlin
@Composable
fun YourScreen() {
    val rewardsViewModel: RewardsViewModel = koinViewModel()
    // Your code
}
```

### Step 2: Award Coins for Contributions

When a user makes a contribution, call the rewards repository:

```kotlin
// In ContributionViewModel or wherever contributions are created
private fun createContribution(request: CreateContributionRequest) {
    viewModelScope.launch {
        val result = contributionRepository.createContribution(request)

        when (result) {
            is ApiStatus.Success -> {
                val contribution = result.data

                // Award EVCoins using the new rewards system
                rewardsRepository.awardCoinsForContribution(
                    userId = contribution.userId,
                    contributionType = contribution.type,
                    chargerId = contribution.chargerId,
                    chargerName = "Charger Name", // Get from PowerSource
                    contributionId = contribution.id,
                    isFirstToCharger = false, // Check if first contribution
                    cityName = "Chennai" // Optional: for Tamil Nadu Explorer badge
                ).onSuccess { awardResult ->
                    // Show animation
                    _coinAwardAnimation.value = awardResult
                }
            }
        }
    }
}
```

### Step 3: Display Rewards in Profile

In `ProfileScreen.kt` or profile content:

```kotlin
@Composable
fun ProfileScreenWithRewards(userId: String) {
    val rewardsViewModel: RewardsViewModel = koinViewModel()
    val userRewards by rewardsViewModel.userRewards

    LaunchedEffect(userId) {
        rewardsViewModel.loadUserRewards(userId)
    }

    Column {
        // Rewards Card
        RewardsCard(
            userRewards = userRewards,
            onClickViewHistory = { /* Navigate to history */ },
            onClickDailyCheckIn = {
                rewardsViewModel.claimDailyCheckIn(userId)
            }
        )

        // Badges
        if (userRewards != null) {
            BadgesShowcase(
                earnedBadges = userRewards.badges
            )
        }

        // Rest of profile UI
    }
}
```

### Step 4: Show Coin Earning Animation

In your contribution screen:

```kotlin
@Composable
fun ContributionScreen(
    viewModel: RewardsViewModel = koinViewModel()
) {
    val coinAward by remember { viewModel.recentCoinAward }

    Box {
        // Your contribution UI

        // Overlay coin animation
        CoinEarningAnimation(
            coinAward = coinAward,
            onDismiss = { viewModel.dismissCoinAnimation() }
        )
    }
}
```

### Step 5: Award Validation Coins

When a user validates a contribution:

```kotlin
fun validateContribution(
    userId: String,
    contributionId: String,
    isValid: Boolean
) {
    viewModelScope.launch {
        // Perform validation
        contributionRepository.validateContribution(contributionId, userId, isValid)

        // Award coins for validation
        rewardsRepository.awardValidationCoins(
            userId = userId,
            contributionId = contributionId,
            chargerId = contribution.chargerId,
            chargerName = "Charger Name"
        ).onSuccess { result ->
            // Show success message
        }.onFailure { error ->
            // Handle error (e.g., daily limit reached)
        }
    }
}
```

### Step 6: Daily Check-in

Add daily check-in button in home screen or profile:

```kotlin
Button(
    onClick = { rewardsViewModel.claimDailyCheckIn(userId) },
    enabled = userRewards?.canCheckInToday() ?: false
) {
    Text("✨ Daily Check-in +5")
}
```

### Step 7: Transaction History

Navigate to transaction history screen:

```kotlin
// In your navigation setup
composable("transaction_history") {
    TransactionHistoryScreen(
        userId = currentUserId,
        onBack = { navController.navigateUp() }
    )
}
```

## Firebase Integration (Future)

The system is designed to work with Firebase Firestore. To enable Firebase:

1. Uncomment Firebase dependencies in `RewardsRepositoryImpl.kt`
2. Replace in-memory storage with Firestore operations:

```kotlin
override suspend fun getUserRewards(userId: String): Result<UserRewards> =
    withContext(ioDispatcher) {
        try {
            val doc = firestore.collection("user_rewards")
                .document(userId)
                .get()
                .await()

            val rewards = doc.toObject(UserRewards::class.java)
                ?: UserRewards(userId = userId)
            Result.success(rewards)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
```

### Firestore Collections

**user_rewards/**
```json
{
  "userId": "user123",
  "totalCoins": 450,
  "coinsThisMonth": 150,
  "contributionCount": 35,
  "validationCount": 12,
  "badges": ["first_timer", "photographer"],
  "rank": "SILVER",
  "lastUpdated": 1234567890
}
```

**coin_transactions/**
```json
{
  "id": "txn123",
  "userId": "user123",
  "amount": 20,
  "type": "EARNED",
  "reason": "Added photo to Shell Charger, Chennai",
  "chargerId": "charger456",
  "timestamp": 1234567890
}
```

## Testing

The repository includes in-memory storage for testing. To test:

1. Load user rewards: `rewardsViewModel.loadUserRewards(userId)`
2. Award coins: `rewardsViewModel.awardCoinsForContribution(...)`
3. Check transactions: `rewardsViewModel.loadTransactions(userId)`

## Key Features

✅ **Instant Feedback**: Animated notifications when earning coins
✅ **Progress Tracking**: Visual progress to next rank
✅ **Gamification**: Badges and ranks to encourage engagement
✅ **Transparency**: Full transaction history
✅ **Daily Limits**: Validation limit (10/day) to prevent abuse
✅ **Bonuses**: First contributor bonus (+50 coins)
✅ **Monthly Tracking**: Track monthly earnings

## Notes

- All repository operations use `Result<T>` for error handling
- Animations auto-dismiss after 3 seconds
- Daily validation count resets at midnight (check `canValidateToday()`)
- Ranks automatically update when crossing thresholds
- Badges are checked automatically when awarding coins

## Next Steps

1. ✅ Implement core reward system
2. ✅ Create UI components
3. ✅ Add animations
4. ⏳ Integrate with existing contribution flow (see Step 2)
5. ⏳ Add navigation routes for reward screens
6. ⏳ Enable Firebase persistence
7. ⏳ Add leaderboard screen
8. ⏳ Implement badge-specific tracking (photo count, review count)

---

**Questions?** Check the implementation in:
- `core/model/rewards/`
- `core/data/repositories/RewardsRepository.kt`
- `feature/main/account/rewards/`
