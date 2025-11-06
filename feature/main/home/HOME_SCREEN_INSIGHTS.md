# Home Screen Charger Insights Feature

## Overview
This feature provides instant insights about EV chargers on the home screen, making it easy for users to quickly identify reliable chargers and make charging decisions in under 2 seconds.

## Key Features

### 1. Color-Coded Map Markers
Chargers are visually distinguished by reliability scores:
- **Green** (>= 70): High reliability - trusted by community
- **Yellow/Orange** (40-70): Medium reliability - some community data
- **Red** (< 40): Low reliability - limited positive feedback
- **Gray** (0): No community data yet

### 2. Quick Filters
Users can instantly filter chargers by:
- **Nearby**: Sorted by distance (default)
- **Most Reliable**: Sorted by reliability score (highest first)
- **Recently Updated**: Sorted by latest community contributions
- **Available Now**: Shows only currently available chargers

### 3. Charger Insight Cards
Each charger displays comprehensive information at a glance:
- Charger name and location
- Distance from user
- Reliability rating (⭐⭐⭐⭐ out of 5)
- Availability status (✅ Available / ❌ In Use)
- Connector type and power (e.g., "CCS 50kW")
- Community data (photos count, reviews count)

### 4. Community Stats Banner
Scrollable banner showing:
- "🎉 45 contributions today!"
- "👥 1,234 active users this month"
- "📸 5,678 photos uploaded"
- "✅ 98% chargers verified"

### 5. Search Functionality
- Compact search bar on home screen
- Search by: charger name, location, city, PIN code, landmark
- Voice search support (Tamil + English)
- Opens dedicated search dialog

### 6. Quick Actions FAB
Floating Action Button with expandable menu:
- **Contribute**: Opens contribution screen to add photos/reviews
- **Report Issue**: Quick feedback for charger problems

## User Flow

```
User opens app
    ↓
Sees community stats banner
    ↓
Views color-coded map (compact view)
    ↓
Applies filter (e.g., "Most Reliable")
    ↓
Scrolls through charger cards
    ↓
Identifies green markers (high reliability)
    ↓
Taps charger card for details
    ↓
Makes charging decision (< 2 seconds)
```

## Technical Implementation

### Files Created

#### Components
1. **QuickFilters.kt** - Filter chips for sorting/filtering chargers
2. **CommunityStatsBar.kt** - Scrollable stats banner
3. **ChargerInsightCard.kt** - Detailed charger card with instant insights
4. **SearchBar.kt** - Compact search bar component
5. **QuickActionsFAB.kt** - Expandable floating action button
6. **ColorCodedMarker.kt** - Color-coded map markers

#### Screens
7. **EnhancedHomeContent.kt** - New home screen layout with all components

### Integration
The enhanced home screen is integrated into `HomeContent.kt` with a toggle parameter:
```kotlin
HomeScreenContent(
    useEnhancedView = true // Toggle between old and new UI
)
```

### Color Coding Logic
```kotlin
fun getMarkerColorFromReliability(reliabilityScore: Float): Color {
    return when {
        reliabilityScore >= 70 -> Green    // High reliability
        reliabilityScore >= 40 -> Orange   // Medium reliability
        reliabilityScore > 0 -> Red        // Low reliability
        else -> Gray                        // No data
    }
}
```

### Filtering Logic
```kotlin
when (selectedFilter) {
    NEARBY -> stations.sortedBy { it.distance() }
    MOST_RELIABLE -> stations.sortedByDescending { it.reliabilityScore }
    RECENTLY_UPDATED -> stations.sortedByDescending { it.lastScoreUpdate }
    AVAILABLE_NOW -> stations.filter { it.isAvailable }
}
```

## Performance Optimizations

1. **Lazy Loading**: Charger cards use `LazyColumn` for efficient scrolling
2. **Remember**: All expensive computations are wrapped in `remember`
3. **Filtering**: Client-side filtering for instant response
4. **Compact Map**: 250dp height to balance visibility and performance
5. **Caching**: Map state and charger data are cached

## Future Enhancements

1. **Voice Search**: Implement Tamil + English voice recognition
2. **Search History**: Save and display recent searches
3. **Offline Mode**: Cache charger data for offline viewing
4. **Real-time Updates**: WebSocket for live charger availability
5. **Dynamic Stats**: Fetch community stats from API
6. **Personalization**: User preferences for default filter

## Screenshots Location
`/screenshots/home-screen-insights/`
- `home_screen_overview.png`
- `filter_by_reliable.png`
- `charger_card_detail.png`
- `community_stats.png`
- `quick_actions_fab.png`

## Testing
Run the preview composables:
- `EnhancedHome_Preview()` in EnhancedHomeContent.kt
- Individual component previews in each file

## API Integration Notes
The following fields from PowerSource model are used:
- `reliabilityScore: Float` - Main reliability metric (0-100)
- `lastScoreUpdate: Long?` - Timestamp of last score update
- `isAvailable: Boolean` - Current availability status
- `distance(): Double` - Distance from user in km
- `media: List<Media>` - Community photos
- `rating: Double` - Average rating (0-5)

## Accessibility
- All icons have content descriptions
- Touch targets are at least 48dp
- High contrast colors for reliability indicators
- Screen reader support for all text elements

## Localization
- Supports RTL languages (Arabic)
- Tamil and English support for all text
- Date/time formatting respects locale
