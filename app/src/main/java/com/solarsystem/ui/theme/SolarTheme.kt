package com.solarsystem.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.solarsystem.R

val RubikFontFamily = FontFamily(
    Font(R.font.rubik_regular, FontWeight.Normal),
    Font(R.font.rubik_medium, FontWeight.Medium),
    Font(R.font.rubik_bold, FontWeight.Bold),
)

val LilyScriptFontFamily = FontFamily(Font(R.font.lily_script_one, FontWeight.Normal))

object SolarColors {
    val BackgroundBase = Color(0xFF0D0608)
    val CardBackground = Color(0xCC0B1223)
    val CardBackgroundSolid = Color(0xFF0B1223)
    val CardBorder = Color(0xFF2F2E2E)
    val TextPrimary = Color(0xE0FFFFFF)
    val TextSecondary = Color(0xA8FFFFFF)
    val TextShadow = Color(0x29FFFFFF)
    val HeroSubtitle = Color(0xCCFFFFFF)
    val SwipeHintText = Color.White
    val EarthShadow = Color(0xFF4197E7)
    val SwipeHintArrowGlow = Color(0x70FFFFFF)
    val SwipeHintTextShadow = Color(0x70FFFFFF)

    object BackgroundGradient {
        val Transparent = Color(0x00000000)
        val StartDeepSpace = Color(0xFF060816)
        val StartMidnight = Color(0xFF0F172A)
        val StartBlue = Color(0xFF020D3C)
        val EndViolet = Color(0xFF1E1B4B)
        val EndMidnight = Color(0xFF0F172A)
        val EndBlack = Color(0xFF030712)
    }

    object PlanetGlow {
        val Saturn = Color(0x80AB4F20)
        val Mars = Color(0x80FF844E)
        val Mercury = Color(0x80095B91)
        val Venus = Color(0x80C69E4A)
        val Jupiter = Color(0x80FF8332)
        val Uranus = Color(0x8031CFDB)
        val Neptune = Color(0x802CA6DB)
    }
}

val SolarTextShadow = Shadow(
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
        color = SolarColors.HeroSubtitle,
        platformStyle = FigmaPlatformStyle,
    )

    val SwipeHint = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 19.sp,
        letterSpacing = 0.25.sp,
        color = SolarColors.SwipeHintText,
        shadow = Shadow(
            color = SolarColors.SwipeHintTextShadow,
            offset = Offset(0f, 4f),
            blurRadius = 16f,
        ),
        platformStyle = FigmaPlatformStyle,
    )
}