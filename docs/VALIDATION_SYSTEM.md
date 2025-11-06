# Community Validation System Documentation

## Overview

The Community Validation System allows users to validate (upvote) existing contributions to confirm accuracy. The more validations a contribution receives, the more trusted the data becomes. This creates a self-regulating ecosystem where the community collectively maintains data quality.

## Features

### 1. Validation Types

Different contribution types have different validation prompts:

| Contribution Type | Validation Prompt | Icon |
|------------------|------------------|------|
| Photo | "Is this accurate?" 👍/👎 | Photo verification |
| Review | "Was this helpful?" 👍 | Review helpfulness |
| Wait Time | "Still accurate?" ✅ | Time-sensitive data |
| Plug Check | "Confirmed working?" ✅ | Equipment status |
| Status Update | "Current status correct?" ✅ | Availability status |

### 2. Data Model

The `Contribution` model has been enhanced with validation fields:

```kotlin
data class Contribution(
    // ... existing fields
    val validationCount: Int = 0,          // Net validations (validated - invalidated)
    val validatedBy: List<String> = emptyList(),     // UserIds who validated
    val invalidatedBy: List<String> = emptyList(),   // UserIds who invalidated
    val confidenceScore: Float = 0.0f,    // 0.0 to 1.0 confidence
    val lastValidatedAt: Long = 0L        // Most recent validation timestamp
)
```

### 3. Confidence Scoring Algorithm

Contributions receive a confidence score calculated using:

```kotlin
confidenceScore = (validations - invalidations) / (validations + invalidations + 1)
timeDecay = 1.0 - (hoursSinceContribution / 720)  // Decay over 30 days
finalScore = confidenceScore × timeDecay × 0.7 + (validationCount / 100) × 0.3
```

**Examples:**
- 10 validations, 0 invalid, 2 hours old = **0.95 confidence (HIGH)**
- 5 validations, 2 invalid, 48 hours old = **0.55 confidence (MEDIUM)**
- 1 validation, 1 invalid, 720 hours old = **0.15 confidence (LOW)**

### 4. Confidence Levels & Visual Indicators

| Level | Threshold | Badge | Color | Description |
|-------|-----------|-------|-------|-------------|
| 🟢 HIGH | > 0.7 | "Verified" | Green | Highly trusted |
| 🟡 MEDIUM | 0.4-0.7 | Normal display | Yellow | Standard |
| 🔴 LOW | < 0.4 | "Needs verification" | Red | Requires review |

### 5. Time-Based Staleness

Different contribution types have different staleness thresholds:

- **Wait Time**: 2 hours
- **Plug Status**: 7 days
- **Status Update**: 2 hours
- **Photos/Reviews**: 30 days

When data becomes stale, users see prompts like:
- "⚠️ Data may be outdated"
- "💡 Is the wait time still accurate?"

## Gamification & Rewards

### EVCoin Rewards

Users earn **5 EVCoins per validation** with the following limits:
- Max **10 validations per day** (prevents spam)
- Cannot validate own contributions
- Must be unique per contribution (can change vote)

### Badges

- **Data Guardian Badge**: Unlock at 100 total validations
- Progress indicator shows path to badge

### Leaderboard

Top validators are ranked by period:
- **This Week**: Last 7 days
- **This Month**: Current calendar month
- **All Time**: Lifetime stats

Top 3 positions receive special medals:
- 🥇 Gold (#1)
- 🥈 Silver (#2)
- 🥉 Bronze (#3)

## User Experience

### Validation Flow

1. User views a contribution (photo, review, etc.)
2. If not their own, validation buttons appear
3. User taps "👍 Accurate" or "👎 Not Accurate"
4. System checks daily limit
5. Updates contribution and awards EVCoins
6. Shows success toast: "+5 EVCoins earned! 🎉"

### Smart Prompts

Context-aware validation prompts appear when:
- Wait time data is > 2 hours old
- Plug status is > 7 days old
- Photos have < 3 validations
- Confidence score is below medium threshold

Example prompts:
- "💡 Is the wait time still accurate?"
- "💡 Are these plugs still working?"
- "💡 Help verify this information"

## Implementation Details

### Core Files Created/Modified

#### Data Model
- `core/model/contribution/Contribution.kt` - Updated with validation fields
- `core/model/contribution/ValidationUtils.kt` - NEW: Utilities and calculations

#### Repository Layer
- `core/data/repositories/ContributionRepository.kt` - Added validation methods
- `core/data/repoImpl/ContributionRepositoryImpl.kt` - Implemented validation logic

#### UI Components
- `feature/power-source/details/ValidationComponents.kt` - NEW: Validation UI
- `feature/power-source/details/CommunityContributionsComponents.kt` - Updated with validation support

#### ViewModel & Screens
- `feature/power-source/validation/ValidationViewModel.kt` - NEW: Validation state management
- `feature/power-source/validation/ValidationLeaderboardScreen.kt` - NEW: Leaderboard UI

### Key Functions

#### Confidence Calculation
```kotlin
fun calculateConfidenceScore(
    validationCount: Int,
    invalidationCount: Int,
    timestamp: Long,
    currentTime: Long = System.currentTimeMillis()
): Float
```

#### Validation Update
```kotlin
suspend fun validateContribution(
    contributionId: String,
    userId: String,
    isValidation: Boolean
): ApiStatus<Contribution>
```

#### Stats & Leaderboard
```kotlin
suspend fun getValidationStats(userId: String): ApiStatus<ValidationStats>
suspend fun getTopValidators(limit: Int, period: LeaderboardPeriod): ApiStatus<List<ValidatorLeaderboardEntry>>
```

## Firebase Integration (Production)

### Firestore Collections

**contributions** (updated):
```json
{
  "id": "contrib_123",
  "validatedBy": ["user1", "user2", "user3"],
  "invalidatedBy": ["user4"],
  "validationCount": 2,
  "confidenceScore": 0.85,
  "lastValidatedAt": Timestamp
}
```

**validations** (new):
```json
{
  "id": "valid_456",
  "userId": "user1",
  "contributionId": "contrib_123",
  "timestamp": Timestamp,
  "isValidation": true
}
```

### Batch Operations

Validation updates use batch writes for atomicity:
1. Update contribution document
2. Create validation record
3. Award EVCoins to user

Example:
```kotlin
val batch = firestore.batch()

// Update contribution
batch.update(contributionRef, updates)

// Record validation
batch.set(validationRef, validationData)

// Award EVCoins
batch.update(userRef, "evCoins", FieldValue.increment(5))

batch.commit().await()
```

## UI Components Reference

### ValidationButtons
Shows appropriate validation buttons based on contribution type:
```kotlin
@Composable
fun ValidationButtons(
    contribution: Contribution,
    currentUserId: String,
    onValidate: (contributionId: String, isValidation: Boolean) -> Unit
)
```

### ConfidenceBadge
Displays confidence level indicator:
```kotlin
@Composable
fun ConfidenceBadge(contribution: Contribution)
```

### ValidationInfo
Shows verification statistics:
```kotlin
@Composable
fun ValidationInfo(contribution: Contribution)
```

### ValidationPrompt
Smart prompt for stale data:
```kotlin
@Composable
fun ValidationPrompt(contribution: Contribution)
```

### ValidationStatsCard
User's validation progress:
```kotlin
@Composable
fun ValidationStatsCard(
    totalValidations: Int,
    validationsThisMonth: Int,
    evCoinsEarned: Int
)
```

## Testing

### Mock Data

The implementation includes mock data for testing:
- Sample contributions with varying validation counts
- In-memory validation history tracking
- Simulated daily limits

### Test Scenarios

1. **Happy Path**: User validates contribution successfully
2. **Daily Limit**: User reaches 10 validations in a day
3. **Own Contribution**: User attempts to validate own contribution
4. **Toggle Vote**: User changes from thumbs up to thumbs down
5. **Confidence Scoring**: Verify score calculations
6. **Stale Data**: Test time-based prompts

## Future Enhancements

### Phase 2
- Push notifications for validation milestones
- Seasonal leaderboard competitions
- Team validation challenges
- Advanced fraud detection

### Phase 3
- AI-assisted validation suggestions
- Contribution quality scores
- Trusted validator program
- Community moderation tools

## API Endpoints (Backend Integration)

When connecting to a backend API, implement these endpoints:

```
POST   /api/contributions/{id}/validate
GET    /api/validations/stats/{userId}
GET    /api/validations/leaderboard?period={week|month|all}
GET    /api/validations/daily-count/{userId}
```

## Security Considerations

1. **Rate Limiting**: Daily limit prevents abuse
2. **Self-Validation Prevention**: Users cannot validate own contributions
3. **Vote Tracking**: Each user can only vote once per contribution (can change vote)
4. **Server-Side Validation**: All checks must be enforced server-side
5. **Timestamp Integrity**: Use server timestamps to prevent manipulation

## Migration Guide

To enable the validation system in an existing app:

1. **Update Data Model**: Add validation fields to Contribution
2. **Database Migration**: Add new fields with default values
3. **Update UI**: Integrate ValidationComponents into contribution displays
4. **Initialize ViewModel**: Add ValidationViewModel to Koin modules
5. **Configure Firebase**: Set up new Firestore rules and indexes
6. **Test Migration**: Verify existing contributions work with new fields

## Support & Troubleshooting

### Common Issues

**Validation not working:**
- Check user authentication
- Verify repository implementation
- Check daily limit status

**Confidence score incorrect:**
- Verify timestamp is in milliseconds
- Check time decay calculation
- Ensure validation counts are accurate

**Leaderboard empty:**
- Verify validation history is being recorded
- Check period calculation logic
- Ensure user data is available

---

**Version**: 1.0
**Last Updated**: 2025-11-06
**Author**: SharaCharge Development Team
