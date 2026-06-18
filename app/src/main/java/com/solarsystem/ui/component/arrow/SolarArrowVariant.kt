package com.solarsystem.ui.component.arrow

import androidx.annotation.DrawableRes
import com.solarsystem.R

enum class SolarArrowVariant {
    Default,
    Variant2,
    Variant3,
}

@DrawableRes
internal fun SolarArrowVariant.chevronRes(): Int = when (this) {
    SolarArrowVariant.Default -> R.drawable.ic_arrow_default
    SolarArrowVariant.Variant2 -> R.drawable.ic_arrow_variant2
    SolarArrowVariant.Variant3 -> R.drawable.ic_arrow_variant3
}
