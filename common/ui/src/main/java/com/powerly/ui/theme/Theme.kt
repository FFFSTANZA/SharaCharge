package com.SharaSpot.ui.theme

import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.core.view.WindowCompat
import com.SharaSpot.resources.R
import com.SharaSpot.ui.extensions.isArabic


/**
 * SharaSpot Design System - Material Theme Configuration
 *
 * Applies the premium SharaSpot design system across the app:
 * - Premium color palette with semantic colors
 * - Comprehensive typography system (12 styles)
 * - Consistent spacing via 8dp grid
 * - Elevation and corner radius scales
 */
private val LightColorScheme = lightColorScheme(
    // Primary colors
    primary = SharaSpotColors.Primary,
    onPrimary = Color.White,
    primaryContainer = SharaSpotColors.PrimaryLight,
    onPrimaryContainer = SharaSpotColors.PrimaryDark,

    // Background & Surface
    background = SharaSpotColors.Background,
    onBackground = SharaSpotColors.OnBackground,
    surface = SharaSpotColors.Surface,
    onSurface = SharaSpotColors.OnBackground,
    surfaceVariant = SharaSpotColors.SurfaceVariant,
    onSurfaceVariant = SharaSpotColors.TextSecondary,

    // Borders & Outlines
    outline = SharaSpotColors.Outline,
    outlineVariant = SharaSpotColors.OutlineVariant,

    // Error states
    error = SharaSpotColors.Error,
    onError = Color.White,
    errorContainer = SharaSpotColors.ErrorLight,
    onErrorContainer = SharaSpotColors.Error,

    // Secondary colors (for compatibility)
    secondary = SharaSpotColors.OnBackground,
    onSecondary = Color.White,
    secondaryContainer = SharaSpotColors.Surface,
    onSecondaryContainer = SharaSpotColors.OnBackground,

    // Tertiary colors
    tertiary = SharaSpotColors.TextSecondary,
    onTertiary = Color.White,
    tertiaryContainer = SharaSpotColors.SurfaceVariant,
    onTertiaryContainer = SharaSpotColors.TextSecondary,

    // Interactive states
    surfaceTint = SharaSpotColors.Primary,
    scrim = SharaSpotColors.Shadow
)

@Composable
fun AppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    ConfigureSystemBars(darkTheme)
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = SharaSpotTypography(isArabic()),
        content = content
    )
}


@Suppress("DEPRECATION")
@Composable
private fun ConfigureSystemBars(darkTheme: Boolean) {
    val view = LocalView.current
    val window = LocalActivity.current?.window
    if (view.isInEditMode.not() && window != null) {
        SideEffect {
            val systemBarsColor = if (darkTheme) MyColors.background.toArgb()
            else Color.White.toArgb()
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                window.statusBarColor = systemBarsColor
                window.navigationBarColor = systemBarsColor
            }
            val windowInsetsController = WindowCompat.getInsetsController(window, view)
            windowInsetsController.isAppearanceLightStatusBars = darkTheme.not()
            windowInsetsController.isAppearanceLightNavigationBars = darkTheme.not()
        }
    }
}
