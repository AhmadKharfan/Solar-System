package com.solarsystem.ui.motion

import androidx.compose.ui.graphics.Color

fun sampleGradientColor(stops: List<Pair<Float, Color>>, position: Float): Color {
    if (stops.isEmpty()) return Color.Transparent
    val t = position.coerceIn(0f, 1f)
    if (t <= stops.first().first) return stops.first().second
    if (t >= stops.last().first) return stops.last().second

    val upperIndex = stops.indexOfFirst { it.first >= t }.coerceAtLeast(1)
    val (startPos, startColor) = stops[upperIndex - 1]
    val (endPos, endColor) = stops[upperIndex]
    val segment = ((t - startPos) / (endPos - startPos)).coerceIn(0f, 1f)
    return lerpColor(startColor, endColor, segment)
}
