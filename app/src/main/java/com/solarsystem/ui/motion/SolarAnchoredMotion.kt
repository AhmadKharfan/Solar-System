package com.solarsystem.ui.motion

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.spring
import androidx.compose.ui.unit.Dp
import com.solarsystem.ui.tokens.ScreenDimens

data class SolarMotionProgress(
    val rawProgress: Float,
    val boundedProgress: Float,
    val earthXProgress: Float,
    val earthYProgress: Float,
    val earthScaleProgress: Float,
    val earthOpacityProgress: Float,
    val cardPositionProgress: Float,
    val cardStackProgress: Float,
    val cardsScreenTop: Dp,
)

enum class SolarMotionAnchor {
    Expanded,
    Collapsed,
}

val OvershootSpringSpec = spring<Float>(
    dampingRatio = 0.7f,
    stiffness = 25f,
)

private val CardStackEasing = CubicBezierEasing(0.24f, 0.0f, 0.04f, 1.0f)

fun solarMotionProgress(rawProgress: Float): SolarMotionProgress {
    val clamped = rawProgress.coerceIn(0f, 1f)
    val cardStackProgress = eased(CardStackEasing, delayed(clamped, 0.04f))

    return SolarMotionProgress(
        rawProgress = rawProgress,
        boundedProgress = clamped,
        earthXProgress = rawProgress,
        earthYProgress = rawProgress,
        earthScaleProgress = rawProgress,
        earthOpacityProgress = rawProgress,
        cardPositionProgress = rawProgress,
        cardStackProgress = cardStackProgress,
        cardsScreenTop = lerp(
            ScreenDimens.CardsStartTop,
            ScreenDimens.CardsEndTop,
            rawProgress,
        ),
    )
}

fun SolarMotionAnchor.progressValue(): Float = when (this) {
    SolarMotionAnchor.Expanded -> 0f
    SolarMotionAnchor.Collapsed -> 1f
}

private fun eased(easing: Easing, fraction: Float): Float =
    easing.transform(fraction.coerceIn(0f, 1f))

private fun delayed(fraction: Float, delay: Float): Float =
    ((fraction - delay) / (1f - delay)).coerceIn(0f, 1f)
