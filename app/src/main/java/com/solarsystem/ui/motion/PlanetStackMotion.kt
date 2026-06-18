package com.solarsystem.ui.motion

import com.solarsystem.ui.component.planet.PlanetCardStackVariant

fun planetStackVariantForHeroProgress(progress: Float): PlanetCardStackVariant {
    val fraction = progress.coerceIn(0f, 1f)
    if (fraction >= 1f) return PlanetCardStackVariant.Default

    return when ((fraction * 7f).toInt().coerceIn(0, 6)) {
        0 -> PlanetCardStackVariant.Variant7
        1 -> PlanetCardStackVariant.Variant6
        2 -> PlanetCardStackVariant.Variant5
        3 -> PlanetCardStackVariant.Variant4
        4 -> PlanetCardStackVariant.Variant3
        5 -> PlanetCardStackVariant.Variant2
        else -> PlanetCardStackVariant.Variant2
    }
}
