# Contribution System Documentation

## Overview

The SharaCharge contribution system enables users to contribute valuable information about charging stations and earn EVCoins as rewards. This creates a community-driven database of real-time charger information.

## Features

### 1. Contribution Types

| Type | Description | EVCoins Reward | Icon |
|------|-------------|----------------|------|
| **Photo** | Upload photos of charger, plugs, location, or parking | 20 | 📸 |
| **Review** | Rate and review charging experience | 30 | ⭐ |
| **Wait Time** | Report current waiting time | 10 | ⏱️ |
| **Plug Check** | Verify plug compatibility and status | 25 | 🔌 |
| **Status Update** | Report current charger availability | 15 | ✅ |

### 2. Key Features

- **Instant Rewards**: Users immediately receive EVCoins upon successful contribution
- **Simple UI**: Maximum 2 taps to contribute
- **Photo Compression**: Automatic image compression to 1024x1024, 80% quality
- **Wait Time Expiry**: Wait time reports expire after 2 hours
- **Validation System**: Other users can validate contributions

## Architecture

### Data Models

Location: `/core/model/src/main/java/com/powerly/core/model/contribution/`

**Main Models:**

```kotlin
data class Contribution(
    val id: String,
    val chargerId: String,
    val userId: String,
    val userName: String,
    val type: ContributionType,
    val timestamp: Long,
    val evCoinsEarned: Int,
    // Type-specific fields...
)

enum class ContributionType {
    PHOTO,
    REVIEW,
    WAIT_TIME,
    PLUG_CHECK,
    STATUS_UPDATE
}
```

**Supporting Models:**

- `ChargerStatus`: AVAILABLE, BUSY, NOT_WORKING, MAINTENANCE
- `WaitTimeOption`: ZERO, FIVE, TEN, FIFTEEN, THIRTY_PLUS
- `PlugType`: CCS, CHADEMO, TYPE_2, BHARAT_DC, TYPE_1
- `PhotoCategory`: CHARGER, PLUG, PARKING, LOCATION, OTHER

### Repository Layer

Location: `/core/data/src/main/java/com/powerly/core/data/repositories/ContributionRepository.kt`

**Key Methods:**

```kotlin
suspend fun createContribution(request: CreateContributionRequest): ApiStatus<Contribution>
suspend fun getContributions(chargerId: String): ApiStatus<List<Contribution>>
suspend fun uploadPhoto(imageUri: Uri, chargerId: String): ApiStatus<String>
suspend fun awardEVCoins(userId: String, amount: Int): ApiStatus<Boolean>
```

**Implementation Note**: Currently uses in-memory storage as a mock. To integrate with Firebase:

1. Uncomment Firebase dependencies in `ContributionRepositoryImpl`
2. Add Firebase Firestore and Storage instances
3. Uncomment Firebase code in repository methods

### ViewModel

Location: `/feature/power-source/src/main/java/com/powerly/powerSource/contribution/ContributionViewModel.kt`

**Key Features:**

- Manages contribution state (Idle, Submitting, Uploading, Success, Error)
- Handles image compression before upload
- Awards EVCoins automatically on successful contribution
- Provides type-safe methods for each contribution type

### UI Components

Location: `/feature/power-source/src/main/java/com/powerly/powerSource/contribution/`

**Screens:**

1. `ContributeBottomSheet.kt` - Main entry point showing all contribution options
2. `PhotoContributionScreen.kt` - Camera/gallery picker with category selection
3. `ReviewContributionScreen.kt` - Rating slider and comment input
4. `WaitTimeContributionScreen.kt` - Quick time selection with optional queue length
5. `PlugCheckContributionScreen.kt` - Plug type, working status, power output
6. `StatusUpdateContributionScreen.kt` - Current charger status selection

### Utilities

**Image Compression Manager**

Location: `/common/lib/src/main/java/com/powerly/lib/ImageCompressionManager.kt`

Features:
- Automatic resize to 1024x1024 max dimension
- 80% JPEG compression
- EXIF orientation handling
- Coroutine-based async processing

## Firebase Structure

### Firestore Collection: `contributions`

```json
{
  "id": "contrib123",
  "chargerId": "charger456",
  "userId": "user789",
  "userName": "Raj Kumar",
  "type": "PHOTO",
  "timestamp": 1699200000000,
  "evCoinsEarned": 20,
  "photoUrl": "https://...",
  "photoCategory": "CHARGER",
  "rating": 4.5,
  "comment": "Great charging experience",
  "waitTimeMinutes": 10,
  "queueLength": 2,
  "plugType": "CCS",
  "plugWorking": true,
  "powerOutput": "50kW",
  "vehicleTested": "Tata Nexon EV",
  "chargerStatus": "AVAILABLE",
  "validationCount": 0
}
```

### Firebase Storage Structure

```
chargers/
  {chargerId}/
    photos/
      {photoId}.jpg
```

## User Flow

### Contributing a Photo

1. User taps "Contribute & Earn EVCoins" button on charger detail page
2. Bottom sheet appears showing all contribution types
3. User selects "Add Photo" (20 EVCoins)
4. User selects photo category (Charger, Plug, Parking, Location)
5. User chooses camera or gallery
6. Image is automatically compressed
7. Photo uploads to Firebase Storage
8. Contribution record saved to Firestore
9. User receives +20 EVCoins instantly
10. Toast notification: "+20 EVCoins earned! 🎉"

### Reporting Wait Time

1. User taps "Contribute & Earn EVCoins"
2. Selects "Report Wait Time" (10 EVCoins)
3. Quick selects: 0 min, 5 min, 10 min, 15 min, 30+ min
4. Optional: Enter number of vehicles in queue
5. Taps "Submit Wait Time"
6. +10 EVCoins awarded instantly

## Integration with Charger Detail Page

**Location**: `/feature/power-source/src/main/java/com/powerly/powerSource/details/PowerSourceContent.kt`

The "Contribute & Earn EVCoins" button is added to the SectionCharging area, right below the "How to Charge" button.

**Navigation**:
- Event: `SourceEvents.Contribute`
- Route: `AppRoutes.PowerSource.Contribute(chargerId)`
- Opens: `ContributeBottomSheet` as a dialog

## EVCoins Reward System

### Reward Amounts

- Photo: **20 EVCoins**
- Review: **30 EVCoins** (highest reward for detailed feedback)
- Wait Time: **10 EVCoins**
- Plug Check: **25 EVCoins**
- Status Update: **15 EVCoins**

### Reward Flow

1. User submits contribution
2. `ContributionViewModel` creates contribution record
3. On success, `awardEVCoins()` is called automatically
4. User balance updated in backend
5. Success toast shows "+X EVCoins earned! 🎉"
6. Bottom sheet dismisses

## Future Enhancements

### Short Term

1. Add proper contribution icon (currently using placeholder)
2. Implement contribution history screen
3. Add contribution leaderboard
4. Enable photo editing before upload
5. Add contribution validation by other users

### Medium Term

1. Implement Firebase Storage integration
2. Add photo moderation system
3. Create contribution analytics dashboard
4. Add contribution quality scoring
5. Implement bonus rewards for highly validated contributions

### Long Term

1. AI-powered photo verification
2. Contribution streaks and achievements
3. Premium contributions (verified by staff)
4. Contribution-based user levels
5. Integration with user reputation system

## Testing Checklist

- [ ] Photo upload with compression works
- [ ] All contribution types submit successfully
- [ ] EVCoins awarded correctly for each type
- [ ] Toast notifications appear
- [ ] Bottom sheet dismisses after contribution
- [ ] Navigation works correctly
- [ ] Image compression maintains quality
- [ ] Wait time expiry works (2 hour threshold)
- [ ] Plug status aggregation correct
- [ ] Contribution summary updates

## Known Limitations

1. **Firebase Integration**: Currently using mock implementation. Production requires Firebase setup.
2. **Camera Support**: Temp file creation for camera not fully implemented in PhotoContributionScreen.
3. **Icon**: Using placeholder icon for contribution button.
4. **Image Validation**: No client-side validation for image content.
5. **Offline Support**: No offline queue for contributions.

## Development Notes

### Adding a New Contribution Type

1. Add enum value to `ContributionType` with display name, reward, and icon
2. Add optional fields to `Contribution` data class if needed
3. Create new screen in `/contribution/` folder
4. Add button handler in `ContributeBottomSheet`
5. Implement `ViewModel` method for the new type
6. Update repository if needed

### Customizing EVCoins Rewards

Edit the `evCoinsReward` value in `ContributionType` enum:

```kotlin
enum class ContributionType(val displayName: String, val evCoinsReward: Int, val icon: String) {
    PHOTO("Add Photo", 20, "📸"),  // Change 20 to desired amount
    // ...
}
```

## Support

For issues or questions about the contribution system:

1. Check this documentation
2. Review code comments in contribution package
3. Examine existing implementation patterns
4. Test with mock data first before Firebase integration

---

**Last Updated**: 2025-11-06
**Version**: 1.0.0
**Status**: Development (Mock Implementation)
