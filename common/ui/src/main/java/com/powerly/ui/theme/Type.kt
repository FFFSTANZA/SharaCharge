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


val myBorder = BorderStroke(1.dp, color = SharaSpotColors.Outline)

/**
 * SharaSpot Design System - Premium Typography Scale
 *
 * A comprehensive typography system with 12 styles for clear hierarchy and premium feel.
 * All text in the app should use these predefined styles via MaterialTheme.typography.
 *
 * Hierarchy:
 * Display: Hero elements, major titles
 * Headline: Screen headers, important titles
 * Title: Section headers, prominent labels
 * Body: Main content, descriptions
 * Label: Buttons, chips, small labels
 */
fun SharaSpotTypography(isArabic: Boolean): Typography {
    val fontFamily = Fonts.appFont(isArabic)

    return Typography(
        // ========== Display Styles (Hero elements) ==========

        // Extra large hero - 40sp ExtraBold
        displayLarge = TextStyle(
            fontFamily = fontFamily,
            fontSize = 40.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 48.sp,
            letterSpacing = (-0.5).sp
        ),

        // Large hero - 32sp Bold
        displayMedium = TextStyle(
            fontFamily = fontFamily,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 40.sp,
            letterSpacing = (-0.25).sp
        ),

        // Medium hero - 28sp Bold
        displaySmall = TextStyle(
            fontFamily = fontFamily,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 36.sp,
            letterSpacing = 0.sp
        ),

        // ========== Headline Styles (Screen headers) ==========

        // Large headline - 24sp SemiBold
        headlineLarge = TextStyle(
            fontFamily = fontFamily,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 32.sp,
            letterSpacing = 0.sp
        ),

        // Medium headline - 20sp SemiBold
        headlineMedium = TextStyle(
            fontFamily = fontFamily,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),

        // Small headline - 18sp Medium
        headlineSmall = TextStyle(
            fontFamily = fontFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 24.sp,
            letterSpacing = 0.sp
        ),

        // ========== Title Styles (Section headers) ==========

        // Large title - 16sp SemiBold
        titleLarge = TextStyle(
            fontFamily = fontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),

        // Medium title - 14sp SemiBold
        titleMedium = TextStyle(
            fontFamily = fontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),

        // Small title - 12sp Medium
        titleSmall = TextStyle(
            fontFamily = fontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 16.sp,
            letterSpacing = 0.1.sp
        ),

        // ========== Body Styles (Main content) ==========

        // Large body - 16sp Normal (PRIMARY BODY)
        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),

        // Medium body - 14sp Normal
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        ),

        // Small body - 12sp Normal
        bodySmall = TextStyle(
            fontFamily = fontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp
        ),

        // ========== Label Styles (Buttons, chips) ==========

        // Large label - 14sp Medium
        labelLarge = TextStyle(
            fontFamily = fontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),

        // Medium label - 12sp Medium
        labelMedium = TextStyle(
            fontFamily = fontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        ),

        // Small label - 10sp Medium
        labelSmall = TextStyle(
            fontFamily = fontFamily,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 14.sp,
            letterSpacing = 0.5.sp
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