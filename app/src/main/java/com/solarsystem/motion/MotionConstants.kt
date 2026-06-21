package com.solarsystem.motion

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.spring
import androidx.compose.ui.unit.dp
import com.solarsystem.R
import com.solarsystem.ui.constants.CardStackTopPadding
import com.solarsystem.ui.theme.SolarColors
import com.solarsystem.util.interpolate
import com.solarsystem.util.smoothStep
import kotlin.math.floor

val OvershootSpringSpec: AnimationSpec<Float> = spring(
    dampingRatio = 0.7f,
    stiffness = 25f,
    visibilityThreshold = 0.001f,
)

val CardStackEasing = CubicBezierEasing(0.24f, 0.0f, 0.04f, 1.0f)
const val CardStackDelay = 0.04f

val StartGradientStops = listOf(
    0f to SolarColors.BackgroundGradient.Transparent,
    0.24545f to SolarColors.BackgroundGradient.StartDeepSpace,
    0.43331f to SolarColors.BackgroundGradient.StartMidnight,
    1f to SolarColors.BackgroundGradient.StartBlue,
)

val EndGradientStops = listOf(
    0f to SolarColors.BackgroundGradient.EndViolet,
    0.5f to SolarColors.BackgroundGradient.EndMidnight,
    1f to SolarColors.BackgroundGradient.EndBlack,
)

val SwipeArrowIcon = R.drawable.ic_arrow1
const val SwipeArrowCount = 3
const val SwipeArrowWaveDurationMillis = 1200
const val SwipeArrowWeakAlpha = 0.25f
const val SwipeArrowMediumAlpha = 0.55f
const val SwipeArrowStrongAlpha = 1f

fun cardStackEntranceProgress(rawProgress: Float): Float {
    val clamped = rawProgress.coerceIn(0f, 1f)
    return CardStackEasing.transform(delayedCardStackProgress(clamped))
}

fun delayedCardStackProgress(fraction: Float): Float =
    ((fraction - CardStackDelay) / (1f - CardStackDelay)).coerceIn(0f, 1f)

fun swipeArrowWaveAlpha(
    index: Int,
    phase: Float,
): Float {
    val reversedPhase = 1f - (phase - floor(phase))
    val scaledPhase = reversedPhase * SwipeArrowCount
    val fromFrame = scaledPhase.toInt().coerceIn(0, SwipeArrowCount - 1)
    val toFrame = (fromFrame + 1) % SwipeArrowCount
    val fraction = smoothStep(scaledPhase - fromFrame)
    return interpolate(
        swipeArrowAlphaAtFrame(index, fromFrame),
        swipeArrowAlphaAtFrame(index, toFrame),
        fraction,
    )
}

private fun swipeArrowAlphaAtFrame(
    index: Int,
    frame: Int,
): Float = when ((index - frame + SwipeArrowCount) % SwipeArrowCount) {
    0 -> SwipeArrowWeakAlpha
    1 -> SwipeArrowMediumAlpha
    else -> SwipeArrowStrongAlpha
}

fun cardsScreenTop(progress: Float) =
    interpolate(
        1100.dp - CardStackTopPadding,
        330.dp - CardStackTopPadding,
        progress,
    )