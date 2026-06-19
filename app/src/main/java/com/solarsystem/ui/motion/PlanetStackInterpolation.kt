package com.solarsystem.ui.motion

import com.solarsystem.model.PlanetCardLayerStyle
import com.solarsystem.model.PlanetCardStackLayer
import com.solarsystem.ui.component.planet.PlanetCardStackVariant
import com.solarsystem.ui.component.planet.defaultStackLayers
import com.solarsystem.ui.component.planet.layers
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

private val StackKeyframes: List<List<PlanetCardStackLayer>> = listOf(
    PlanetCardStackVariant.Variant7.layers(),
    PlanetCardStackVariant.Variant6.layers(),
    PlanetCardStackVariant.Variant5.layers(),
    PlanetCardStackVariant.Variant4.layers(),
    PlanetCardStackVariant.Variant3.layers(),
    PlanetCardStackVariant.Variant2.layers(),
    defaultStackLayers(),
)

data class PlanetCardVisualState(
    val backgroundColor: Color,
    val planetAlpha: Float,
    val planetOffsetY: Dp,
    val titleAlpha: Float,
    val taglineAlpha: Float,
    val statsAlpha: Float,
    val elevatedTitleAlpha: Float,
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

fun interpolatePlanetStackOffsetY(index: Int, progress: Float) = when (val clamped = progress.coerceIn(0f, 1f)) {
    0f -> StackKeyframes.first().layerAt(index).offsetY
    1f -> StackKeyframes.last().layerAt(index).offsetY
    else -> {
        val scaled = clamped * StackKeyframes.lastIndex
        val fromIndex = scaled.toInt().coerceIn(0, StackKeyframes.lastIndex - 1)
        val segment = scaled - fromIndex
        val start = StackKeyframes[fromIndex].layerAt(index)
        val end = StackKeyframes[fromIndex + 1].layerAt(index)
        lerp(start.offsetY, end.offsetY, segment)
    }
}

fun interpolatePlanetStackLayer(index: Int, progress: Float): PlanetCardStackLayer {
    val clamped = progress.coerceIn(0f, 1f)
    if (clamped <= 0f) return StackKeyframes.first().layerAt(index)
    if (clamped >= 1f) return StackKeyframes.last().layerAt(index)

    val scaled = clamped * StackKeyframes.lastIndex
    val fromIndex = scaled.toInt().coerceIn(0, StackKeyframes.lastIndex - 1)
    val segment = scaled - fromIndex
    val start = StackKeyframes[fromIndex].layerAt(index)
    val end = StackKeyframes[fromIndex + 1].layerAt(index)
    return PlanetCardStackLayer(
        offsetY = lerp(start.offsetY, end.offsetY, segment),
        style = lerpLayerStyle(start.style, end.style, segment),
    )
}

fun interpolatePlanetCardVisualState(index: Int, progress: Float): PlanetCardVisualState {
    val clamped = progress.coerceIn(0f, 1f)
    if (clamped <= 0f) return StackKeyframes.first().layerAt(index).style.toVisualState()
    if (clamped >= 1f) return StackKeyframes.last().layerAt(index).style.toVisualState()

    val scaled = clamped * StackKeyframes.lastIndex
    val fromIndex = scaled.toInt().coerceIn(0, StackKeyframes.lastIndex - 1)
    val segment = scaled - fromIndex
    val start = StackKeyframes[fromIndex].layerAt(index).style
    val end = StackKeyframes[fromIndex + 1].layerAt(index).style
    return PlanetCardVisualState(
        backgroundColor = lerpColor(start.backgroundColor, end.backgroundColor, segment),
        planetAlpha = lerp(start.planetAlpha, end.planetAlpha, segment),
        planetOffsetY = lerp(start.planetOffsetY, end.planetOffsetY, segment),
        titleAlpha = lerp(start.titleAlpha(), end.titleAlpha(), segment),
        taglineAlpha = lerp(start.taglineAlpha(), end.taglineAlpha(), segment),
        statsAlpha = lerp(start.statsAlpha(), end.statsAlpha(), segment),
        elevatedTitleAlpha = lerp(start.elevatedTitleAlpha(), end.elevatedTitleAlpha(), segment),
    )
}

fun PlanetCardLayerStyle.toVisualState() = PlanetCardVisualState(
    backgroundColor = backgroundColor,
    planetAlpha = planetAlpha,
    planetOffsetY = planetOffsetY,
    titleAlpha = titleAlpha(),
    taglineAlpha = taglineAlpha(),
    statsAlpha = statsAlpha(),
    elevatedTitleAlpha = elevatedTitleAlpha(),
)

private fun List<PlanetCardStackLayer>.layerAt(index: Int): PlanetCardStackLayer =
    getOrElse(index) { last() }

private fun PlanetCardLayerStyle.titleAlpha() =
    if (showTitle && !taglineOnlyHeader) 1f else 0f

private fun PlanetCardLayerStyle.taglineAlpha() =
    if (showTagline) 1f else 0f

private fun PlanetCardLayerStyle.statsAlpha() =
    if (showStats) 1f else 0f

private fun PlanetCardLayerStyle.elevatedTitleAlpha() =
    if (elevateTitle) 1f else 0f

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
