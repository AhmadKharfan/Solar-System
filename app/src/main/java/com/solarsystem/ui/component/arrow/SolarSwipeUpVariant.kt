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

@DrawableRes
internal fun SolarSwipeUpVariant.layerRes(): List<Int> = when (this) {
    SolarSwipeUpVariant.Default -> listOf(
        R.drawable.ic_swipe_up_default_0,
        R.drawable.ic_swipe_up_default_1,
        R.drawable.ic_swipe_up_default_2,
    )
    SolarSwipeUpVariant.Variant2 -> listOf(
        R.drawable.ic_swipe_up_variant2_0,
        R.drawable.ic_swipe_up_variant2_1,
        R.drawable.ic_swipe_up_variant2_2,
    )
    SolarSwipeUpVariant.Variant3 -> listOf(
        R.drawable.ic_swipe_up_variant3_0,
        R.drawable.ic_swipe_up_variant3_1,
        R.drawable.ic_swipe_up_variant3_2,
    )
}

internal fun SolarSwipeUpVariant.topPadding(): Dp = if (this == SolarSwipeUpVariant.Default) 8.dp else 0.dp

internal fun SolarSwipeUpVariant.bottomPadding(): Dp = when (this) {
    SolarSwipeUpVariant.Default -> 20.dp
    SolarSwipeUpVariant.Variant2 -> 12.dp
    SolarSwipeUpVariant.Variant3 -> 8.dp
}
