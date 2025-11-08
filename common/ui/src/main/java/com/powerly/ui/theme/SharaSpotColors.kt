package com.SharaSpot.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * SharaSpot Design System - Premium Color Palette
 *
 * A sophisticated, focused color system with premium accents for a professional feel.
 * Electric Green represents energy and sustainability for EV charging.
 */
object SharaSpotColors {
    // ========== Primary Colors ==========

    /**
     * Primary brand color - Electric Green
     * Used for: CTAs, active states, highlights, success states
     */
    val Primary = Color(0xFF00C853)

    /**
     * Primary variant - Darker Green (for hover/pressed states)
     * Used for: Button press states, active elements
     */
    val PrimaryDark = Color(0xFF00A844)

    /**
     * Primary light - Lighter Green (for backgrounds)
     * Used for: Success backgrounds, light accents
     */
    val PrimaryLight = Color(0xFFE8F5E9)

    // ========== Background & Surface ==========

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
     * Secondary surface - Lighter gray
     * Used for: Nested cards, secondary containers
     */
    val SurfaceVariant = Color(0xFFF5F5F5)

    // ========== Borders & Dividers ==========

    /**
     * Borders and dividers - Light Gray
     * Used for: Card borders, dividers, subtle separators
     */
    val Outline = Color(0xFFE0E0E0)

    /**
     * Subtle borders - Very light gray
     * Used for: Subtle separators, inactive borders
     */
    val OutlineVariant = Color(0xFFF0F0F0)

    // ========== Text Colors ==========

    /**
     * Secondary text - Medium Gray
     * Used for: Secondary text, descriptions, captions
     */
    val TextSecondary = Color(0xFF757575)

    /**
     * Tertiary text - Light Gray
     * Used for: Disabled text, placeholders, hints
     */
    val TextTertiary = Color(0xFF9E9E9E)

    /**
     * Disabled text - Very light gray
     * Used for: Disabled states
     */
    val TextDisabled = Color(0xFFBDBDBD)

    // ========== Semantic Colors ==========

    /**
     * Error states - Material Red
     * Used for: Error messages, destructive actions, validation errors
     */
    val Error = Color(0xFFE53935)

    /**
     * Error light - Light red background
     * Used for: Error backgrounds, error containers
     */
    val ErrorLight = Color(0xFFFFEBEE)

    /**
     * Success - Green (same as Primary for consistency)
     * Used for: Success messages, confirmations
     */
    val Success = Primary

    /**
     * Success light - Light green background
     * Used for: Success backgrounds, success containers
     */
    val SuccessLight = PrimaryLight

    /**
     * Warning - Amber
     * Used for: Warnings, caution states
     */
    val Warning = Color(0xFFFFA726)

    /**
     * Warning light - Light amber background
     * Used for: Warning backgrounds, warning containers
     */
    val WarningLight = Color(0xFFFFF3E0)

    /**
     * Info - Blue
     * Used for: Information messages, hints
     */
    val Info = Color(0xFF42A5F5)

    /**
     * Info light - Light blue background
     * Used for: Info backgrounds, info containers
     */
    val InfoLight = Color(0xFFE3F2FD)

    // ========== Interactive States ==========

    /**
     * Ripple effect - Semi-transparent black
     * Used for: Touch feedback, ripple effects
     */
    val Ripple = Color(0x1F000000)

    /**
     * Hover overlay - Semi-transparent black
     * Used for: Hover states on clickable elements
     */
    val Hover = Color(0x0A000000)

    /**
     * Pressed overlay - Semi-transparent black
     * Used for: Pressed states on clickable elements
     */
    val Pressed = Color(0x1F000000)

    /**
     * Focus outline - Primary color
     * Used for: Focus indicators, accessibility
     */
    val Focus = Primary

    // ========== Shadow Colors ==========

    /**
     * Shadow - Semi-transparent black
     * Used for: Elevation shadows, depth
     */
    val Shadow = Color(0x1F000000)

    /**
     * Shadow light - Very subtle shadow
     * Used for: Light elevation, subtle depth
     */
    val ShadowLight = Color(0x0D000000)
}
