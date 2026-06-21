package com.solarsystem.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

fun interpolate(start: Float, end: Float, fraction: Float): Float =
    start + (end - start) * fraction

fun interpolate(start: Dp, end: Dp, fraction: Float): Dp =
    start + (end - start) * fraction

fun interpolateColor(start: Color, end: Color, fraction: Float): Color {
    val f = fraction.coerceIn(0f, 1f)
    return Color(
        alpha = interpolate(start.alpha, end.alpha, f),
        red = interpolate(start.red, end.red, f),
        green = interpolate(start.green, end.green, f),
        blue = interpolate(start.blue, end.blue, f),
    )
}

fun smoothStep(fraction: Float): Float {
    val t = fraction.coerceIn(0f, 1f)
    return t * t * (3f - 2f * t)
}

fun Color.withMultipliedAlpha(alphaMultiplier: Float): Color =
    copy(alpha = alpha * alphaMultiplier.coerceIn(0f, 1f))
