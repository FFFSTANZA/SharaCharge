package com.SharaSpot.ui.extensions

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import com.SharaSpot.ui.theme.SharaSpotColors

/**
 * Premium Visual Effects for SharaCharge
 *
 * Provides gradient brushes, glass morphism effects, and other
 * premium visual enhancements.
 */

// ========== Gradient Brushes ==========

/**
 * Primary gradient brush (Electric Green)
 * Used for premium CTAs and highlights
 */
object PremiumGradients {

    /**
     * Primary vertical gradient (top to bottom)
     */
    val primaryVertical = Brush.verticalGradient(
        colors = listOf(
            SharaSpotColors.GradientStart,
            SharaSpotColors.Primary,
            SharaSpotColors.GradientEnd
        )
    )

    /**
     * Primary horizontal gradient (left to right)
     */
    val primaryHorizontal = Brush.horizontalGradient(
        colors = listOf(
            SharaSpotColors.GradientStart,
            SharaSpotColors.Primary,
            SharaSpotColors.GradientEnd
        )
    )

    /**
     * Primary radial gradient (center to edges)
     */
    val primaryRadial = Brush.radialGradient(
        colors = listOf(
            SharaSpotColors.GradientStart,
            SharaSpotColors.Primary,
            SharaSpotColors.GradientEnd
        )
    )

    /**
     * Subtle surface gradient
     * Perfect for cards and surfaces
     */
    val surfaceSubtle = Brush.verticalGradient(
        colors = listOf(
            Color.White,
            SharaSpotColors.Surface
        )
    )

    /**
     * Glass morphism gradient overlay
     */
    val glassMorphism = Brush.verticalGradient(
        colors = listOf(
            SharaSpotColors.GlassBackground,
            Color.White.copy(alpha = 0.8f),
            SharaSpotColors.GlassBackground
        )
    )

    /**
     * Shimmer gradient for loading states
     */
    val shimmer = Brush.linearGradient(
        colors = listOf(
            SharaSpotColors.ShimmerBase,
            SharaSpotColors.ShimmerHighlight,
            SharaSpotColors.ShimmerBase
        ),
        tileMode = TileMode.Mirror
    )

    /**
     * Premium gold accent gradient
     */
    val premiumAccent = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFD700),
            Color(0xFFFFA500),
            Color(0xFFFFD700)
        )
    )

    /**
     * Success gradient
     */
    val success = Brush.horizontalGradient(
        colors = listOf(
            SharaSpotColors.Primary,
            SharaSpotColors.GradientStart
        )
    )

    /**
     * Error gradient
     */
    val error = Brush.horizontalGradient(
        colors = listOf(
            SharaSpotColors.Error,
            Color(0xFFFF5252)
        )
    )

    /**
     * Info gradient
     */
    val info = Brush.horizontalGradient(
        colors = listOf(
            SharaSpotColors.Info,
            Color(0xFF42A5F5).copy(alpha = 0.8f)
        )
    )

    /**
     * Dark scrim for overlays
     */
    val scrim = Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            SharaSpotColors.Scrim
        )
    )
}

// ========== Shadow Effects ==========

/**
 * Premium soft shadow effect
 */
fun Modifier.premiumShadow(): Modifier = this.then(
    Modifier.drawWithContent {
        drawContent()
        // Add custom shadow rendering here if needed
    }
)

// ========== Glass Morphism Effect ==========

/**
 * Creates a frosted glass effect
 * Perfect for floating panels and modals
 */
fun Modifier.glassMorphism(): Modifier = this.then(
    Modifier.drawWithContent {
        drawContent()
        // Draw frosted glass overlay
        drawRect(
            brush = PremiumGradients.glassMorphism,
            alpha = 0.1f
        )
    }
)

// ========== Border Gradients ==========

/**
 * Gradient border effect
 */
fun Modifier.gradientBorder(
    brush: Brush = PremiumGradients.primaryHorizontal,
    width: Float = 2f
): Modifier = this.then(
    Modifier.drawWithContent {
        drawContent()
        // Draw gradient border
        val strokeWidth = width
        drawRect(
            brush = brush,
            alpha = 1f,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
        )
    }
)
