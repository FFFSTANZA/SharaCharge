package com.SharaSpot.ui.theme

import androidx.compose.ui.unit.dp

/**
 * SharaSpot Design System - Spacing Scale
 *
 * A consistent 8dp grid system for predictable layouts and breathing room.
 * All spacing in the app should use these values for visual consistency.
 *
 * Usage:
 * ```
 * modifier = Modifier.padding(Spacing.m)  // 16dp padding
 * verticalArrangement = Arrangement.spacedBy(Spacing.s)  // 8dp spacing
 * ```
 */
object Spacing {
    /**
     * Extra small spacing - 4dp
     * Used for: Tight spacing, icon padding, subtle separations
     */
    val xs = 4.dp

    /**
     * Small spacing - 8dp
     * Used for: Compact lists, chip spacing, small gaps
     */
    val s = 8.dp

    /**
     * Medium spacing - 16dp (PRIMARY SPACING)
     * Used for: Standard padding, card content, screen edges
     */
    val m = 16.dp

    /**
     * Large spacing - 24dp
     * Used for: Section separations, prominent elements
     */
    val l = 24.dp

    /**
     * Extra large spacing - 32dp
     * Used for: Major sections, screen headers, hero elements
     */
    val xl = 32.dp
}
