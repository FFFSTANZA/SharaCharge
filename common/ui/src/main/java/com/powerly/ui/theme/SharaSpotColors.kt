package com.SharaSpot.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * SharaSpot Design System - Minimalist Color Palette
 *
 * A clean, focused color system with just 3 primary colors for consistent branding.
 * Electric Green represents energy and sustainability for EV charging.
 */
object SharaSpotColors {
    /**
     * Primary brand color - Electric Green
     * Used for: CTAs, active states, highlights, success states
     */
    val Primary = Color(0xFF00C853)

    /**
     * Screen background - Pure White
     * Used for: Main screen backgrounds, creating breathing room
     */
    val Background = Color.White

    /**
     * Text color - Almost Black (high contrast)
     * Used for: Body text, headings, primary content
     */
    val OnBackground = Color(0xFF212121)

    /**
     * Card/Surface background - Off White
     * Used for: Cards, elevated surfaces, contained elements
     */
    val Surface = Color(0xFFFAFAFA)

    /**
     * Borders and dividers - Light Gray
     * Used for: Card borders, dividers, subtle separators
     */
    val Outline = Color(0xFFE0E0E0)

    /**
     * Error states - Material Red
     * Used for: Error messages, destructive actions, validation errors
     */
    val Error = Color(0xFFE53935)
}
