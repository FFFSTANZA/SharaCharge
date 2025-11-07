# 🎨 SharaSpot Design System

A minimalist, consistent design system for the SharaSpot Android app — Tamil Nadu's community-powered EV charging station discovery platform.

---

## 🎯 Design Philosophy

**Less is more.** The SharaSpot design system focuses on:
- **Clarity** — Clean interfaces with plenty of breathing room
- **Consistency** — Same patterns across all screens
- **Focus** — Limited palette keeps attention on content
- **Accessibility** — High contrast, readable typography

---

## 🎨 Color System

### Minimalist 3-Color Palette

The entire app uses just **3 primary colors** for maximum consistency and focus:

```kotlin
object SharaSpotColors {
    val Primary = Color(0xFF00C853)        // Electric Green
    val Background = Color.White           // Screen background
    val OnBackground = Color(0xFF212121)   // Text (almost black)
    val Surface = Color(0xFFFAFAFA)        // Card background
    val Outline = Color(0xFFE0E0E0)        // Borders/dividers
    val Error = Color(0xFFE53935)          // Error states
}
```

### Color Usage Guidelines

| Color | Usage | Examples |
|-------|-------|----------|
| **Primary (Electric Green)** | CTAs, active states, highlights | Primary buttons, active tabs, success messages |
| **Background (White)** | Main screen backgrounds | Screen canvas, creating breathing room |
| **OnBackground (Dark Gray)** | Text and icons | Body text, headings, primary content |
| **Surface (Off White)** | Cards and elevated surfaces | Cards, dialogs, contained elements |
| **Outline (Light Gray)** | Borders and dividers | Card borders, subtle separators |
| **Error (Red)** | Error states | Error messages, destructive actions |

### Implementation

```kotlin
// ✅ DO: Use MaterialTheme colors
Text(
    text = "Find Chargers",
    color = MaterialTheme.colorScheme.onBackground
)

Button(
    onClick = { },
    colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary
    )
) { Text("Charge Now") }

// ❌ DON'T: Hardcode colors
Text(
    text = "Find Chargers",
    color = Color(0xFF212121)  // Avoid!
)
```

---

## 📝 Typography System

### 4-Style Typography Hierarchy

A clean, focused typography system with just **4 text styles**:

```kotlin
fun SharaSpotTypography(isArabic: Boolean): Typography {
    return Typography(
        // Hero titles - 32sp Bold
        displayLarge = TextStyle(
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 40.sp
        ),

        // Section headers - 24sp SemiBold
        headlineMedium = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 32.sp
        ),

        // Primary body text - 16sp Normal
        bodyLarge = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 24.sp
        ),

        // Secondary text - 14sp Normal
        bodyMedium = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 20.sp
        )
    )
}
```

### Typography Usage Guidelines

| Style | Usage | Examples |
|-------|-------|----------|
| **displayLarge** | Hero titles, screen headers | "Find EV Chargers in Tamil Nadu" |
| **headlineMedium** | Section headers, card titles | "Nearby Chargers", "Your Rewards" |
| **bodyLarge** | Primary body text, button labels | Descriptions, button text, list items |
| **bodyMedium** | Secondary text, captions | Metadata, helper text, labels |

### Implementation

```kotlin
// ✅ DO: Use MaterialTheme typography
Text(
    text = "Find EV Chargers in Tamil Nadu",
    style = MaterialTheme.typography.displayLarge
)

Text(
    text = "Community-powered charging station discovery",
    style = MaterialTheme.typography.bodyLarge
)

// ❌ DON'T: Hardcode text sizes
Text(
    text = "Find EV Chargers",
    fontSize = 32.sp,  // Avoid!
    fontWeight = FontWeight.Bold  // Avoid!
)
```

---

## 📏 Spacing System

### 8dp Grid System

All spacing follows a consistent **8dp grid** for predictable, harmonious layouts:

```kotlin
object Spacing {
    val xs = 4.dp   // Extra small - Tight spacing
    val s = 8.dp    // Small - Compact lists
    val m = 16.dp   // Medium - PRIMARY SPACING
    val l = 24.dp   // Large - Section separations
    val xl = 32.dp  // Extra large - Major sections
}
```

### Spacing Usage Guidelines

| Value | Usage | Examples |
|-------|-------|----------|
| **xs (4dp)** | Tight spacing | Icon padding, chip spacing |
| **s (8dp)** | Compact spacing | List item gaps, small separations |
| **m (16dp)** | Standard spacing | Screen padding, card content, default gaps |
| **l (24dp)** | Section spacing | Between major sections |
| **xl (32dp)** | Hero spacing | Screen headers, prominent elements |

### Implementation

```kotlin
// ✅ DO: Use Spacing object
Column(
    modifier = Modifier.padding(Spacing.m),
    verticalArrangement = Arrangement.spacedBy(Spacing.m)
) { /* content */ }

// ❌ DON'T: Hardcode spacing
Column(
    modifier = Modifier.padding(16.dp),  // Avoid!
    verticalArrangement = Arrangement.spacedBy(16.dp)  // Avoid!
) { /* content */ }
```

---

## 🧱 Component Standards

### Cards

Consistent card style across the app:

```kotlin
Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    shape = RoundedCornerShape(12.dp),
    elevation = CardDefaults.cardElevation(0.dp),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
) {
    Column(modifier = Modifier.padding(Spacing.m)) {
        // Card content
    }
}
```

**Key attributes:**
- 12dp corner radius (soft, modern)
- No elevation (flat design)
- 1dp border (subtle definition)
- Surface background color
- Spacing.m (16dp) internal padding

### Buttons

#### Primary Button
```kotlin
Button(
    onClick = onClick,
    modifier = Modifier.fillMaxWidth().height(48.dp),
    shape = RoundedCornerShape(12.dp)
) {
    Text(
        text = "Charge Now",
        style = MaterialTheme.typography.bodyLarge
    )
}
```

#### Secondary Button
```kotlin
OutlinedButton(
    onClick = onClick,
    modifier = Modifier.fillMaxWidth().height(48.dp),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
    shape = RoundedCornerShape(12.dp)
) {
    Text(
        text = "Learn More",
        style = MaterialTheme.typography.bodyLarge
    )
}
```

**Key attributes:**
- 48dp height (easy to tap)
- 12dp corner radius (consistent with cards)
- Full width on mobile
- bodyLarge typography for labels

### Screen Layout

Standard screen structure:

```kotlin
Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(Spacing.m)
        .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(Spacing.m)
) {
    // Screen content
}
```

**Key attributes:**
- Spacing.m (16dp) edge padding
- Spacing.m vertical spacing between elements
- Vertical scroll when needed
- Consistent across all screens

---

## 🎭 Branding

### App Identity

- **Name:** SharaSpot (everywhere — consistent capitalization)
- **Package:** com.sharaspot (lowercase, no capitals)
- **Tagline:** "Find EV Chargers in Tamil Nadu"
- **Description:** "Community-powered charging station discovery"

### Voice & Tone

- **Clear** — Simple, direct language
- **Helpful** — Focus on user needs
- **Local** — Tamil Nadu context
- **Community-driven** — Emphasize collaboration

---

## ✅ Migration Checklist

When updating existing screens:

- [ ] Replace `MyColors` → `MaterialTheme.colorScheme`
- [ ] Replace hardcoded `.sp` → `MaterialTheme.typography.*`
- [ ] Replace hardcoded `.dp` → `Spacing.*`
- [ ] Remove `MyScreen`/`MySurface` wrappers (use standard Column/Card)
- [ ] Update button corner radius to 12.dp
- [ ] Update card styles (0dp elevation, 1dp border)
- [ ] Use Spacing.m (16dp) as default padding
- [ ] Ensure consistent branding (SharaSpot, not old names)

---

## 📚 Code Examples

### Complete Screen Example

```kotlin
@Composable
fun ChargerListScreen(
    chargers: List<Charger>,
    onChargerClick: (Charger) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.m)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Spacing.m)
    ) {
        // Header
        Text(
            text = "Nearby Chargers",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Find the perfect charging spot",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Charger cards
        chargers.forEach { charger ->
            ChargerCard(
                charger = charger,
                onClick = { onChargerClick(charger) }
            )
        }
    }
}

@Composable
fun ChargerCard(
    charger: Charger,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(Spacing.m),
            verticalArrangement = Arrangement.spacedBy(Spacing.s)
        ) {
            Text(
                text = charger.name,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "${charger.distance}km away",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}
```

---

## 🚀 Benefits

### For Users
- **Faster learning curve** — Consistent patterns are predictable
- **Better readability** — High contrast, clear hierarchy
- **Modern feel** — Clean, minimalist aesthetic

### For Developers
- **Faster development** — Reusable components and patterns
- **Easier maintenance** — Centralized design tokens
- **Better collaboration** — Shared design language

### For the Brand
- **Professional appearance** — Polished, cohesive experience
- **Memorable identity** — Distinctive Electric Green
- **Scalable foundation** — Easy to extend consistently

---

## 📞 Questions?

For design system questions or contributions, see the main repository documentation or open an issue on GitHub.

**Together, we're powering Tamil Nadu's EV future. 🔋⚡**
