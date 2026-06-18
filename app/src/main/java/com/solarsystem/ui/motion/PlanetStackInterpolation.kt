package com.solarsystem.ui.motion

import com.solarsystem.model.PlanetCardLayerStyle
import com.solarsystem.model.PlanetCardStackLayer
import com.solarsystem.ui.component.planet.PlanetCardStackVariant
import com.solarsystem.ui.component.planet.defaultStackLayers
import com.solarsystem.ui.component.planet.layers

private val StackKeyframes: List<List<PlanetCardStackLayer>> = listOf(
    PlanetCardStackVariant.Variant7.layers(),
    PlanetCardStackVariant.Variant6.layers(),
    PlanetCardStackVariant.Variant5.layers(),
    PlanetCardStackVariant.Variant4.layers(),
    PlanetCardStackVariant.Variant3.layers(),
    PlanetCardStackVariant.Variant2.layers(),
    defaultStackLayers(),
)

fun interpolatePlanetStackLayers(progress: Float): List<PlanetCardStackLayer> {
    val clamped = progress.coerceIn(0f, 1f)
    if (clamped <= 0f) return StackKeyframes.first()
    if (clamped >= 1f) return StackKeyframes.last()

    val scaled = clamped * StackKeyframes.lastIndex
    val fromIndex = scaled.toInt().coerceIn(0, StackKeyframes.lastIndex - 1)
    val segment = scaled - fromIndex
    val from = StackKeyframes[fromIndex]
    val to = StackKeyframes[fromIndex + 1]
    val cardCount = maxOf(from.size, to.size)

    return List(cardCount) { index ->
        val start = from.getOrElse(index) { from.last() }
        val end = to.getOrElse(index) { to.last() }
        PlanetCardStackLayer(
            offsetY = lerp(start.offsetY, end.offsetY, segment),
            style = lerpLayerStyle(start.style, end.style, segment),
        )
    }
}

private fun lerpLayerStyle(start: PlanetCardLayerStyle, end: PlanetCardLayerStyle, fraction: Float): PlanetCardLayerStyle {
    val f = fraction.coerceIn(0f, 1f)
    return PlanetCardLayerStyle(
        backgroundColor = lerpColor(start.backgroundColor, end.backgroundColor, f),
        planetAlpha = lerp(start.planetAlpha, end.planetAlpha, f),
        planetOffsetY = lerp(start.planetOffsetY, end.planetOffsetY, f),
        showTitle = if (f < 0.5f) start.showTitle else end.showTitle,
        showTagline = if (f < 0.5f) start.showTagline else end.showTagline,
        showStats = if (f < 0.5f) start.showStats else end.showStats,
        taglineOnlyHeader = if (f < 0.5f) start.taglineOnlyHeader else end.taglineOnlyHeader,
        elevateTitle = if (f < 0.5f) start.elevateTitle else end.elevateTitle,
    )
}
