package com.solarsystem.motion

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.solarsystem.ui.constants.EarthBaseSize
import com.solarsystem.ui.constants.EarthShadowAlpha
import com.solarsystem.util.interpolate

data class EarthStartBounds(
    val left: Dp,
    val top: Dp,
    val endLeft: Dp,
)

fun earthStartBounds(maxWidth: Dp, maxHeight: Dp) = EarthStartBounds(
    left = maxWidth / 2 - 8.dp - EarthBaseSize / 2,
    top = maxHeight + 124.dp - EarthBaseSize,
    endLeft = maxWidth / 2 - 100.dp,
)

fun earthDiscDiameter(progress: Float): Dp =
    EarthBaseSize * earthScale(progress)

fun earthShadowAlpha(progress: Float): Float {
    val firstStateCompensation = interpolate(0.52f, 1f, progress)
    return EarthShadowAlpha * earthAlpha(progress) * firstStateCompensation
}

fun earthAlpha(progress: Float): Float =
    interpolate(
        1f,
        0.5f,
        progress,
    )

fun earthScale(progress: Float): Float =
    interpolate(1f, 200f / 644f, progress)