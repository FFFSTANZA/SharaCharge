package com.SharaSpot.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.SharaSpot.resources.R


val myBorder = BorderStroke(1.dp, color = MyColors.borderColor)

/**
 * SharaSpot Design System - Typography Scale
 *
 * A minimalist 4-style typography system for clear hierarchy and readability.
 * All text in the app should use these predefined styles via MaterialTheme.typography.
 *
 * Hierarchy:
 * - displayLarge: Hero titles, screen headers (32sp, Bold)
 * - headlineMedium: Section headers, card titles (24sp, SemiBold)
 * - bodyLarge: Primary body text, button labels (16sp, Normal)
 * - bodyMedium: Secondary text, captions, metadata (14sp, Normal)
 */
fun SharaSpotTypography(isArabic: Boolean): Typography {
    val fontFamily = Fonts.appFont(isArabic)

    return Typography(
        // Hero titles - 32sp Bold
        displayLarge = TextStyle(
            fontFamily = fontFamily,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 40.sp,
            letterSpacing = (-0.5).sp
        ),

        // Section headers - 24sp SemiBold
        headlineMedium = TextStyle(
            fontFamily = fontFamily,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 32.sp,
            letterSpacing = 0.sp
        ),

        // Primary body text - 16sp Normal
        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),

        // Secondary text - 14sp Normal
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        )
    )
}

// Legacy function for backward compatibility
@Deprecated("Use SharaSpotTypography instead", ReplaceWith("SharaSpotTypography(isArabic)"))
fun myTypography(isArabic: Boolean): Typography = SharaSpotTypography(isArabic)

object Fonts {

    private val arabicFont = FontFamily(
        Font(R.font.font_arabic_light, FontWeight.Light),
        Font(R.font.font_arabic_regular, FontWeight.Normal),
        Font(R.font.font_arabic_regular, FontWeight.Normal, FontStyle.Italic),
        Font(R.font.font_arabic_medium, FontWeight.Medium),
        Font(R.font.font_arabic_bold, FontWeight.Bold)
    )


    private val latinFont = FontFamily(
        Font(R.font.font_latin_thin, FontWeight.Thin),
        Font(R.font.font_latin_light, FontWeight.Light),
        Font(R.font.font_latin_regular, FontWeight.Normal),
        Font(R.font.font_latin_italic, FontWeight.Normal, FontStyle.Italic),
        Font(R.font.font_latin_medium, FontWeight.Medium),
        Font(R.font.font_latin_bold, FontWeight.Bold),
        Font(R.font.font_latin_black, FontWeight.Black),
    )

    fun appFont(isArabic: Boolean): FontFamily {
        return if (isArabic) arabicFont else latinFont
    }
}