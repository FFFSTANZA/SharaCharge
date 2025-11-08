package com.SharaSpot.ui.theme

import androidx.compose.ui.unit.dp

/**
 * SharaSpot Design System - Elevation Scale
 *
 * A consistent elevation system for creating depth and hierarchy.
 * Higher elevations create stronger shadows and bring elements forward.
 *
 * Usage:
 * ```
 * elevation = CardDefaults.cardElevation(defaultElevation = Elevation.small)
 * ```
 */
object Elevation {
    /**
     * No elevation - 0dp
     * Used for: Flat elements, disabled states
     */
    val none = 0.dp

    /**
     * Extra small elevation - 1dp
     * Used for: Subtle lift, very light separation
     */
    val extraSmall = 1.dp

    /**
     * Small elevation - 2dp
     * Used for: Cards, containers, light depth
     */
    val small = 2.dp

    /**
     * Medium elevation - 4dp (PRIMARY ELEVATION)
     * Used for: Interactive cards, buttons (when pressed), prominent elements
     */
    val medium = 4.dp

    /**
     * Large elevation - 8dp
     * Used for: Floating action buttons, dropdowns, modals
     */
    val large = 8.dp

    /**
     * Extra large elevation - 12dp
     * Used for: Dialogs, bottom sheets, navigation drawers
     */
    val extraLarge = 12.dp

    /**
     * Max elevation - 16dp
     * Used for: High-priority overlays, tooltips
     */
    val max = 16.dp
}
