package com.SharaSpot.ui.theme

import androidx.compose.ui.unit.dp

/**
 * SharaSpot Design System - Corner Radius Scale
 *
 * A consistent corner radius system for a cohesive, premium look.
 * Rounded corners create a softer, more approachable feel.
 *
 * Usage:
 * ```
 * shape = RoundedCornerShape(CornerRadius.medium)
 * ```
 */
object CornerRadius {
    /**
     * No rounding - 0dp
     * Used for: Sharp corners, dividers
     */
    val none = 0.dp

    /**
     * Extra small radius - 4dp
     * Used for: Small chips, badges, tags
     */
    val extraSmall = 4.dp

    /**
     * Small radius - 8dp
     * Used for: Small buttons, input fields
     */
    val small = 8.dp

    /**
     * Medium radius - 12dp (PRIMARY RADIUS)
     * Used for: Cards, buttons, containers
     */
    val medium = 12.dp

    /**
     * Large radius - 16dp
     * Used for: Large cards, prominent elements
     */
    val large = 16.dp

    /**
     * Extra large radius - 24dp
     * Used for: Bottom sheets, modals, hero elements
     */
    val extraLarge = 24.dp

    /**
     * Full rounding - 999dp (creates pill shape)
     * Used for: Circular buttons, pills, fully rounded elements
     */
    val full = 999.dp
}
