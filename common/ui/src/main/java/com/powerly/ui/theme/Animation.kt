package com.SharaSpot.ui.theme

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

/**
 * SharaSpot Design System - Animation System
 *
 * Consistent animation timing and springs for smooth, premium interactions.
 * All animations should use these constants for consistency.
 */
object AnimationConstants {
    // ========== Duration Constants ==========

    /**
     * Extra fast - 100ms
     * Used for: Micro-interactions, ripples
     */
    const val ExtraFast = 100

    /**
     * Fast - 150ms
     * Used for: Quick state changes, hover effects
     */
    const val Fast = 150

    /**
     * Normal - 250ms (PRIMARY DURATION)
     * Used for: Standard transitions, fades, slides
     */
    const val Normal = 250

    /**
     * Slow - 350ms
     * Used for: Emphasized transitions, important state changes
     */
    const val Slow = 350

    /**
     * Extra slow - 500ms
     * Used for: Complex animations, page transitions
     */
    const val ExtraSlow = 500

    // ========== Easing Functions ==========

    /**
     * Standard easing - Fast out, slow in
     * Most common for general animations
     */
    val StandardEasing: Easing = FastOutSlowInEasing

    /**
     * Enter easing - Linear out, slow in
     * Used for elements entering the screen
     */
    val EnterEasing: Easing = LinearOutSlowInEasing

    /**
     * Exit easing - Linear
     * Used for elements leaving the screen
     */
    val ExitEasing: Easing = LinearEasing

    // ========== Spring Configurations ==========

    /**
     * Gentle spring - Low stiffness, high damping
     * Creates smooth, gentle motion
     */
    val GentleSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )

    /**
     * Standard spring - Medium stiffness, medium damping
     * Default spring for most animations
     */
    val StandardSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )

    /**
     * Bouncy spring - Medium stiffness, low damping
     * Creates playful, bouncy motion
     */
    val BouncySpring = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    /**
     * Snappy spring - High stiffness, no bounce
     * Creates quick, crisp motion
     */
    val SnappySpring = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessHigh
    )
}

/**
 * Convenience functions for creating tween animations
 */
object AnimationSpecs {
    /**
     * Fast fade animation
     */
    fun fastFade() = tween<Float>(
        durationMillis = AnimationConstants.Fast,
        easing = AnimationConstants.StandardEasing
    )

    /**
     * Standard transition animation
     */
    fun standard() = tween<Float>(
        durationMillis = AnimationConstants.Normal,
        easing = AnimationConstants.StandardEasing
    )

    /**
     * Enter animation (for elements appearing)
     */
    fun enter() = tween<Float>(
        durationMillis = AnimationConstants.Normal,
        easing = AnimationConstants.EnterEasing
    )

    /**
     * Exit animation (for elements disappearing)
     */
    fun exit() = tween<Float>(
        durationMillis = AnimationConstants.Fast,
        easing = AnimationConstants.ExitEasing
    )

    /**
     * Emphasized animation (for important transitions)
     */
    fun emphasized() = tween<Float>(
        durationMillis = AnimationConstants.Slow,
        easing = AnimationConstants.StandardEasing
    )
}
