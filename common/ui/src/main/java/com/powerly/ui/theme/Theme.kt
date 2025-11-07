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
 * Applies the minimalist SharaSpot design system across the app:
 * - 3-color palette (Electric Green, White, Dark Gray)
 * - 4-style typography system
 * - Consistent spacing via 8dp grid
 */
private val LightColorScheme = lightColorScheme(
    primary = SharaSpotColors.Primary,
    onPrimary = Color.White,
    background = SharaSpotColors.Background,
    onBackground = SharaSpotColors.OnBackground,
    surface = SharaSpotColors.Surface,
    onSurface = SharaSpotColors.OnBackground,
    outline = SharaSpotColors.Outline,
    error = SharaSpotColors.Error,
    onError = Color.White,
    // Secondary colors mapped to maintain compatibility
    secondary = SharaSpotColors.OnBackground,
    onSecondary = Color.White,
    tertiary = SharaSpotColors.Primary,
    onTertiary = Color.White
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
