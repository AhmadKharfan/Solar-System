package com.solarsystem.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val SolarTextShadow = Shadow(
    color = SolarColors.TextShadow,
    offset = Offset(-4f, 4f),
    blurRadius = 12f,
)

object SolarTypography {
    val CardTitle = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        letterSpacing = 0.25.sp,
        color = SolarColors.TextPrimary,
        shadow = SolarTextShadow,
    )

    val CardSubtitle = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp,
        color = SolarColors.TextSecondary,
        shadow = SolarTextShadow,
    )

    val StatLabel = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.25.sp,
        color = SolarColors.TextSecondary,
    )

    val StatValue = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.25.sp,
        color = SolarColors.TextPrimary,
    )

    val StatHint = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        letterSpacing = 0.25.sp,
        color = SolarColors.TextSecondary,
    )

    val EarthHeroTitle = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 64.sp,
        letterSpacing = 0.25.sp,
        color = SolarColors.TextPrimary,
        shadow = SolarTextShadow,
    )

    val SolarHeroTitle = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        letterSpacing = 0.25.sp,
        color = SolarColors.TextPrimary,
        shadow = SolarTextShadow,
    )

    val HeroSubtitle = TextStyle(
        fontFamily = LilyScriptFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.25.sp,
        color = Color(0xCCFFFFFF),
        lineHeight = 22.sp,
    )

    val SwipeHint = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        letterSpacing = 0.25.sp,
        color = Color.White,
        shadow = Shadow(
            color = Color(0x70FFFFFF),
            offset = Offset(0f, 4f),
            blurRadius = 16f,
        ),
    )
}
