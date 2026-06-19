package com.solarsystem.ui.component.arrow

import androidx.annotation.DrawableRes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.solarsystem.R

enum class SolarSwipeUpVariant {
    Default,
    Variant2,
    Variant3,
}

private val swipeUpLayers = listOf(
    R.drawable.ic_arrow1,
    R.drawable.ic_arrow2,
    R.drawable.ic_arrow3,
)

@DrawableRes
internal fun SolarSwipeUpVariant.layerRes(): List<Int> = when (this) {
    SolarSwipeUpVariant.Default,
    SolarSwipeUpVariant.Variant2,
    SolarSwipeUpVariant.Variant3,
    -> swipeUpLayers
}

internal fun SolarSwipeUpVariant.topPadding(): Dp = if (this == SolarSwipeUpVariant.Default) 8.dp else 0.dp

internal fun SolarSwipeUpVariant.bottomPadding(): Dp = when (this) {
    SolarSwipeUpVariant.Default -> 20.dp
    SolarSwipeUpVariant.Variant2 -> 12.dp
    SolarSwipeUpVariant.Variant3 -> 8.dp
}
