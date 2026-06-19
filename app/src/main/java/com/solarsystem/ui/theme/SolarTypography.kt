package com.solarsystem.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val SolarTextShadow = Shadow(
    color = SolarColors.TextShadow,
    offset = Offset(-4f, 4f),
    blurRadius = 12f,
)

private val FigmaPlatformStyle = PlatformTextStyle(includeFontPadding = false)

object SolarTypography {
    val CardTitle = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 21.sp,
        letterSpacing = 0.25.sp,
        color = SolarColors.TextPrimary,
        shadow = SolarTextShadow,
        platformStyle = FigmaPlatformStyle,
    )

    val CardSubtitle = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 17.sp,
        letterSpacing = 0.25.sp,
        color = SolarColors.TextSecondary,
        shadow = SolarTextShadow,
        platformStyle = FigmaPlatformStyle,
    )

    val StatLabel = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.25.sp,
        color = SolarColors.TextSecondary,
        platformStyle = FigmaPlatformStyle,
    )

    val StatValue = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.25.sp,
        color = SolarColors.TextPrimary,
        platformStyle = FigmaPlatformStyle,
    )

    val StatHint = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.25.sp,
        color = SolarColors.TextSecondary,
        platformStyle = FigmaPlatformStyle,
    )

    val EarthHeroTitle = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 64.sp,
        lineHeight = 76.sp,
        letterSpacing = 0.25.sp,
        color = SolarColors.TextPrimary,
        shadow = SolarTextShadow,
        platformStyle = FigmaPlatformStyle,
    )

    val SolarHeroTitle = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.25.sp,
        color = SolarColors.TextPrimary,
        shadow = SolarTextShadow,
        platformStyle = FigmaPlatformStyle,
    )

    val HeroSubtitle = TextStyle(
        fontFamily = LilyScriptFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.25.sp,
        color = Color(0xCCFFFFFF),
        platformStyle = FigmaPlatformStyle,
    )

    val SwipeHint = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 19.sp,
        letterSpacing = 0.25.sp,
        color = Color.White,
        shadow = Shadow(
            color = Color(0x70FFFFFF),
            offset = Offset(0f, 4f),
            blurRadius = 16f,
        ),
        platformStyle = FigmaPlatformStyle,
    )
}
