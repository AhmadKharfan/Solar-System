package com.solarsystem.ui.motion

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
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

val SolarExploreTween: TweenSpec<Float> = tween(
    durationMillis = 2400,
    easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f),
)

val SolarReturnTween: TweenSpec<Float> = tween(
    durationMillis = 1200,
    easing = CubicBezierEasing(0.16f, 0.0f, 0.08f, 1.0f),
)

private val EarthXEasing = CubicBezierEasing(0.18f, 0.0f, 0.12f, 1.0f)
private val EarthYEasing = CubicBezierEasing(0.12f, 0.0f, 0.0f, 1.0f)
private val EarthScaleEasing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
private val OpacityEasing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
private val CardPositionEasing = CubicBezierEasing(0.18f, 0.0f, 0.0f, 1.0f)
private val CardStackEasing = CubicBezierEasing(0.24f, 0.0f, 0.04f, 1.0f)

fun solarMotionProgress(rawProgress: Float): SolarMotionProgress {
    val clamped = rawProgress.coerceIn(0f, 1f)

    val earthXProgress = eased(EarthXEasing, clamped)
    val earthYProgress = eased(EarthYEasing, clamped)
    val earthScaleProgress = eased(EarthScaleEasing, clamped)
    val cardPositionProgress = eased(CardPositionEasing, delayed(clamped, 0.06f))
    val cardStackProgress = eased(CardStackEasing, delayed(clamped, 0.04f))

    return SolarMotionProgress(
        rawProgress = rawProgress,
        boundedProgress = clamped,
        earthXProgress = earthXProgress,
        earthYProgress = earthYProgress,
        earthScaleProgress = earthScaleProgress,
        earthOpacityProgress = eased(OpacityEasing, clamped),
        cardPositionProgress = cardPositionProgress,
        cardStackProgress = cardStackProgress,
        cardsScreenTop = lerp(
            ScreenDimens.CardsStartTop,
            ScreenDimens.CardsEndTop,
            cardPositionProgress,
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
