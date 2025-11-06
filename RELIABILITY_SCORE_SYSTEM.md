# Charger Reliability Score System

## Overview

The Charger Reliability Score System provides users with a comprehensive, data-driven metric to quickly identify reliable and well-maintained EV charging stations. The score ranges from 0-100 and is displayed as a 1-5 star rating with a detailed breakdown.

## Score Calculation

The reliability score is calculated based on **5 key factors**, each contributing a maximum of 20 points (total 100 points):

### 1. Photo Count (max 20 points)
**Formula:** `(photoCount / 5) × 20` (capped at 20)

- Measures visual documentation of the charger
- More photos = better information for users
- Helps users verify location and condition
- **Example:** 8 photos = (8/5) × 20 = 16 points

### 2. Review Count (max 20 points)
**Formula:** `(reviewCount / 10) × 20` (capped at 20)

- Measures community engagement
- More reviews = more reliable data
- Indicates popular/frequently used chargers
- **Example:** 15 reviews = (15/10) × 20 = 20 points

### 3. Average Rating (max 20 points)
**Formula:** `avgRating × 4`

- Measures user satisfaction (1-5 stars)
- Higher rating = better user experience
- Most direct quality indicator
- **Example:** 4.2 rating = 4.2 × 4 = 16.8 points

### 4. Recent Contributions (max 20 points)
**Formula:** `(contributionsLast7Days / 5) × 20` (capped at 20)

- Measures data freshness
- Recent activity indicates current status
- Helps identify active vs abandoned chargers
- **Example:** 3 contributions in last 7 days = (3/5) × 20 = 12 points

### 5. Validation Confidence (max 20 points)
**Formula:** `avgConfidenceScore × 20`

- Measures community verification
- Higher confidence = more trustworthy data
- Based on thumbs up/down validation
- **Example:** 0.8 average confidence = 0.8 × 20 = 16 points

## Star Rating Conversion

| Total Score | Star Rating | Badge |
|------------|-------------|-------|
| 90-100 | ⭐⭐⭐⭐⭐ (5/5) | Community Trusted ⭐ |
| 75-89 | ⭐⭐⭐⭐ (4/5) | Verified ✅ |
| 50-74 | ⭐⭐⭐ (3/5) | Community Reviewed 👥 |
| 25-49 | ⭐⭐ (2/5) | Needs Reviews 📝 |
| 1-24 | ⭐ (1/5) | Needs Reviews 📝 |
| 0 | (0/5) | Needs Reviews 📝 |

## Score Breakdown Levels

Each component is displayed with a level indicator:

| Percentage | Level | Icon | Color |
|-----------|-------|------|-------|
| 80-100% | Excellent | 🟢 | Green |
| 60-79% | Good | 🟡 | Light Green |
| 40-59% | Fair | 🟠 | Orange |
| 1-39% | Poor | 🔴 | Red |
| 0% | No Data | ⚪ | Gray |

## Example Calculation

**Sample Charger:**
- 8 community photos
- 15 reviews
- 4.2 average rating
- 3 contributions in last 7 days
- 0.8 average validation confidence

**Score Breakdown:**
```
Photo Score:      (8/5) × 20 = 16.0/20 (Excellent 🟢)
Review Score:     (15/10) × 20 = 20.0/20 (Excellent 🟢)
Rating Score:     4.2 × 4 = 16.8/20 (Excellent 🟢)
Freshness Score:  (3/5) × 20 = 12.0/20 (Good 🟡)
Validation Score: 0.8 × 20 = 16.0/20 (Excellent 🟢)

Total Score: 80.8/100 = ⭐⭐⭐⭐ (4/5)
Badge: Community Trusted ⭐
```

## UI Display

### 1. Charger Card (Compact View)
```
┌─────────────────────────────────────┐
│ Reliability Score: ⭐⭐⭐⭐ (4/5)    │
│ Community Trusted ⭐                │
│ Tap to view breakdown               │
└─────────────────────────────────────┘
```

### 2. Breakdown Modal (Detailed View)
```
┌─────────────────────────────────────────┐
│ Reliability Score Breakdown             │
│                                         │
│ Overall Score: 80.8/100                 │
│                                         │
│ 📸 Photos: Excellent 🟢 16.0/20         │
│ ████████████████░░░░ 80%                │
│                                         │
│ ⭐ Reviews: Excellent 🟢 20.0/20        │
│ ████████████████████ 100%               │
│                                         │
│ 🌟 Rating: Excellent 🟢 16.8/20         │
│ ████████████████░░░░ 84%                │
│                                         │
│ 🕐 Freshness: Good 🟡 12.0/20           │
│ ████████████░░░░░░░░ 60%                │
│                                         │
│ ✅ Verified: Excellent 🟢 16.0/20       │
│ ████████████████░░░░ 80%                │
└─────────────────────────────────────────┘
```

## Implementation

### 1. Data Model

**PowerSource.kt** (Updated)
```kotlin
data class PowerSource(
    // ... existing fields ...
    @SerializedName("reliability_score") val reliabilityScore: Float = 0f,
    @SerializedName("reliability_score_breakdown") val reliabilityScoreBreakdown: ReliabilityScoreBreakdown? = null,
    @SerializedName("last_score_update") val lastScoreUpdate: Long? = null
)

data class ReliabilityScoreBreakdown(
    @SerializedName("photo_score") val photoScore: Float = 0f,
    @SerializedName("review_score") val reviewScore: Float = 0f,
    @SerializedName("rating_score") val ratingScore: Float = 0f,
    @SerializedName("freshness_score") val freshnessScore: Float = 0f,
    @SerializedName("validation_score") val validationScore: Float = 0f
)
```

### 2. Calculation Utility

**Location:** `core/model/src/main/java/com/powerly/core/model/reliability/ReliabilityScore.kt`

Key functions:
- `calculateReliabilityScore()` - Main calculation function
- `calculateBasicReliabilityScore()` - Simplified version (without contributions)
- `ReliabilityScore.toStarDisplay()` - Format as stars
- `ReliabilityScore.toDisplayString()` - Format as "80.8/100"

### 3. UI Components

**Location:** `feature/power-source/src/main/java/com/powerly/powerSource/details/ReliabilityScoreComponents.kt`

Available composables:
- `ReliabilityScoreCard()` - Main card with tap-to-expand
- `ReliabilityScoreBreakdownSheet()` - Detailed breakdown modal
- `TrustBadge()` - Badge display
- `CompactReliabilityScore()` - For list items
- `ScoreBreakdownItem()` - Individual score component

### 4. Repository Methods

**ContributionRepository.kt**

```kotlin
// Calculate score for a charger
suspend fun calculateReliabilityScore(chargerId: String): ApiStatus<ReliabilityScore>

// Update score in Firebase
suspend fun updateReliabilityScore(
    chargerId: String,
    reliabilityScore: ReliabilityScore
): ApiStatus<Boolean>
```

### 5. Firebase Integration

#### Firestore Document Structure

**Collection: `power-sources/{chargerId}`**
```json
{
  "id": "charger_123",
  "title": "Tesla Supercharger",
  "reliability_score": 80.8,
  "reliability_score_breakdown": {
    "photo_score": 16.0,
    "review_score": 20.0,
    "rating_score": 16.8,
    "freshness_score": 12.0,
    "validation_score": 16.0
  },
  "last_score_update": "2025-11-06T12:00:00Z"
}
```

#### Cloud Functions

**Location:** `firebase/functions/updateReliabilityScores.js`

Available functions:
1. **Scheduled Update** (`updateReliabilityScores`)
   - Runs daily at 2:00 AM
   - Updates all chargers in batches
   - Deploy: `firebase deploy --only functions:updateReliabilityScores`

2. **Real-time Update** (`updateReliabilityScoreOnContribution`)
   - Triggers on contribution create/update
   - Updates single charger immediately
   - Automatic deployment

3. **Manual Update** (`manualUpdateReliabilityScore`)
   - Callable from app
   - Updates specific charger on demand
   - Usage:
   ```kotlin
   val functions = Firebase.functions
   val data = hashMapOf("chargerId" to "charger_123")
   functions.getHttpsCallable("manualUpdateReliabilityScore")
       .call(data)
       .await()
   ```

4. **Statistics** (`getReliabilityScoreStats`)
   - Returns score distribution
   - Analytics and monitoring

### 6. Integration Example

**In PowerSourceViewModel:**

```kotlin
class PowerSourceViewModel(
    private val contributionRepository: ContributionRepository
) : ViewModel() {

    private val _reliabilityScore = MutableStateFlow<ReliabilityScore?>(null)
    val reliabilityScore: StateFlow<ReliabilityScore?> = _reliabilityScore

    fun loadReliabilityScore(chargerId: String) {
        viewModelScope.launch {
            when (val result = contributionRepository.calculateReliabilityScore(chargerId)) {
                is ApiStatus.Success -> _reliabilityScore.value = result.data
                is ApiStatus.Error -> {
                    // Handle error
                }
            }
        }
    }
}
```

**In PowerSourceScreen:**

```kotlin
@Composable
fun PowerSourceScreen(
    powerSource: PowerSource,
    viewModel: PowerSourceViewModel
) {
    val reliabilityScore by viewModel.reliabilityScore.collectAsState()

    LaunchedEffect(powerSource.id) {
        viewModel.loadReliabilityScore(powerSource.id)
    }

    Column {
        // ... other content ...

        // Reliability Score Card
        reliabilityScore?.let { score ->
            ReliabilityScoreCard(
                reliabilityScore = score,
                photoCount = contributions.count { it.type == ContributionType.PHOTO },
                reviewCount = contributions.count { it.type == ContributionType.REVIEW },
                onViewBreakdown = { /* handled internally */ }
            )
        }

        // ... rest of content ...
    }
}
```

## Benefits

### For Users
✅ **Quick decision making** - Identify reliable chargers at a glance
✅ **Transparency** - See exactly what contributes to the score
✅ **Trust** - Data-driven metric based on community input
✅ **Current information** - Freshness score ensures up-to-date data

### For Charger Owners
✅ **Incentive to improve** - Clear metrics to work towards
✅ **Encourage reviews** - Higher scores with more community engagement
✅ **Competitive advantage** - Stand out with high scores

### For the Platform
✅ **Self-correcting** - Bad chargers naturally get low scores
✅ **Community-driven** - Leverages user contributions
✅ **Gamification** - Encourages user participation
✅ **Data quality** - Validation ensures accuracy

## Sorting and Filtering

### Add Sorting Option

Users can sort charger lists by:
- **Distance** (default)
- **Price** (low to high)
- **Rating** (high to low)
- **Most Reliable** ⭐ (reliability score high to low) - NEW!

**Implementation:**

```kotlin
enum class ChargersSort {
    DISTANCE,
    PRICE,
    RATING,
    RELIABILITY  // NEW
}

fun List<PowerSource>.sortChargers(sortBy: ChargersSort): List<PowerSource> {
    return when (sortBy) {
        ChargersSort.DISTANCE -> sortedBy { it.distance }
        ChargersSort.PRICE -> sortedBy { it.price }
        ChargersSort.RATING -> sortedByDescending { it.rating }
        ChargersSort.RELIABILITY -> sortedByDescending { it.reliabilityScore }
    }
}
```

## Testing

### Unit Tests

**ReliabilityScoreTest.kt**

```kotlin
class ReliabilityScoreTest {

    @Test
    fun `test score calculation with sample data`() {
        val score = calculateReliabilityScore(
            photoCount = 8,
            reviewCount = 15,
            avgRating = 4.2,
            contributions = sampleContributions
        )

        assertEquals(80.8f, score.totalScore, 0.1f)
        assertEquals(4, score.starRating)
        assertEquals(TrustBadge.EXCELLENT, score.trustBadge)
    }

    @Test
    fun `test score with no data returns zero`() {
        val score = calculateReliabilityScore(
            photoCount = 0,
            reviewCount = 0,
            avgRating = 0.0,
            contributions = emptyList()
        )

        assertEquals(0f, score.totalScore)
        assertEquals(0, score.starRating)
    }

    @Test
    fun `test freshness score with recent contributions`() {
        val now = System.currentTimeMillis()
        val recentContributions = listOf(
            Contribution(timestamp = now - 3600_000), // 1 hour ago
            Contribution(timestamp = now - 7200_000), // 2 hours ago
            Contribution(timestamp = now - 86400_000) // 1 day ago
        )

        val score = calculateReliabilityScore(
            photoCount = 0,
            reviewCount = 0,
            avgRating = 0.0,
            contributions = recentContributions
        )

        assertTrue(score.freshnessScore > 0)
    }
}
```

### UI Tests

Test the reliability score components display correctly and respond to user interactions.

## Future Enhancements

1. **Weighted Scores** - Allow customization of factor weights
2. **Location-based Scoring** - Adjust scores based on regional activity
3. **Trend Indicators** - Show if score is improving/declining (↗️ ↘️)
4. **Historical Tracking** - Track score changes over time
5. **Badges** - Special badges for consistently high-scoring chargers
6. **Notifications** - Alert users when nearby chargers score highly

## Support & Feedback

For questions or suggestions about the reliability score system:
- Create an issue in the repository
- Contact the development team
- Check the contribution system documentation

---

**Last Updated:** 2025-11-06
**Version:** 1.0.0
