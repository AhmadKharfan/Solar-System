package com.solarsystem.ui.component.planet

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.solarsystem.model.PlanetCardLayerStyle
import com.solarsystem.model.PlanetCardStackLayer
import com.solarsystem.ui.theme.SolarColors
import com.solarsystem.ui.tokens.PeekPlanetAlpha
import com.solarsystem.ui.tokens.PlanetCardDimens

internal fun peekSolidTagline() = PlanetCardLayerStyle(
    backgroundColor = SolarColors.CardBackgroundSolid,
    planetAlpha = PeekPlanetAlpha,
    showTitle = false,
    taglineOnlyHeader = true,
)

internal fun peekSolidStatsOnly() = PlanetCardLayerStyle(
    backgroundColor = SolarColors.CardBackgroundSolid,
    planetAlpha = PeekPlanetAlpha,
    showTitle = false,
    showTagline = false,
)

internal fun peekSolidFront() = PlanetCardLayerStyle(backgroundColor = SolarColors.CardBackgroundSolid)

private val StackVariant2Layers = listOf(
    PlanetCardStackLayer(
        offsetY = 0.dp,
        style = PlanetCardLayerStyle(
            backgroundColor = SolarColors.CardBackgroundSolid,
            planetAlpha = PeekPlanetAlpha,
            elevateTitle = true,
        ),
    ),
    PlanetCardStackLayer(offsetY = 14.dp, style = peekSolidFront()),
    PlanetCardStackLayer(offsetY = 288.dp, style = PlanetCardLayerStyle()),
    PlanetCardStackLayer(offsetY = 562.dp, style = PlanetCardLayerStyle()),
    PlanetCardStackLayer(offsetY = 836.dp, style = PlanetCardLayerStyle()),
    PlanetCardStackLayer(offsetY = 1110.dp, style = PlanetCardLayerStyle()),
    PlanetCardStackLayer(offsetY = 1384.dp, style = PlanetCardLayerStyle()),
)

private val StackVariant3Layers = listOf(
    PlanetCardStackLayer(offsetY = 0.dp, style = peekSolidTagline()),
    PlanetCardStackLayer(
        offsetY = 14.dp,
        style = peekSolidStatsOnly().copy(elevateTitle = true),
    ),
    PlanetCardStackLayer(offsetY = 28.dp, style = peekSolidFront()),
    PlanetCardStackLayer(offsetY = 302.dp, style = PlanetCardLayerStyle()),
    PlanetCardStackLayer(offsetY = 576.dp, style = PlanetCardLayerStyle()),
    PlanetCardStackLayer(offsetY = 850.dp, style = PlanetCardLayerStyle()),
    PlanetCardStackLayer(offsetY = 1124.dp, style = PlanetCardLayerStyle()),
)

private val StackVariant4Layers = listOf(
    PlanetCardStackLayer(offsetY = 0.dp, style = peekSolidTagline()),
    PlanetCardStackLayer(
        offsetY = 14.dp,
        style = peekSolidStatsOnly().copy(elevateTitle = true),
    ),
    PlanetCardStackLayer(
        offsetY = 28.dp,
        style = peekSolidTagline().copy(elevateTitle = true),
    ),
    PlanetCardStackLayer(offsetY = 42.dp, style = peekSolidFront()),
    PlanetCardStackLayer(offsetY = 316.dp, style = PlanetCardLayerStyle()),
    PlanetCardStackLayer(offsetY = 590.dp, style = PlanetCardLayerStyle()),
    PlanetCardStackLayer(offsetY = 864.dp, style = PlanetCardLayerStyle()),
)

private val StackVariant5Layers = listOf(
    PlanetCardStackLayer(offsetY = 0.dp, style = peekSolidTagline()),
    PlanetCardStackLayer(offsetY = 14.dp, style = peekSolidStatsOnly()),
    PlanetCardStackLayer(offsetY = 28.dp, style = peekSolidTagline()),
    PlanetCardStackLayer(
        offsetY = 42.dp,
        style = peekSolidTagline().copy(planetOffsetY = (-15.5).dp),
    ),
    PlanetCardStackLayer(offsetY = 56.dp, style = peekSolidFront()),
    PlanetCardStackLayer(offsetY = 330.dp, style = PlanetCardLayerStyle()),
    PlanetCardStackLayer(offsetY = 604.dp, style = PlanetCardLayerStyle()),
)

private val StackVariant6Layers = listOf(
    PlanetCardStackLayer(offsetY = 0.dp, style = peekSolidTagline()),
    PlanetCardStackLayer(offsetY = 14.dp, style = peekSolidStatsOnly()),
    PlanetCardStackLayer(offsetY = 28.dp, style = peekSolidTagline()),
    PlanetCardStackLayer(
        offsetY = 42.dp,
        style = peekSolidTagline().copy(planetOffsetY = (-15.5).dp),
    ),
    PlanetCardStackLayer(
        offsetY = 56.dp,
        style = PlanetCardLayerStyle(
            backgroundColor = SolarColors.CardBackgroundSolid,
            planetAlpha = PeekPlanetAlpha,
            elevateTitle = true,
        ),
    ),
    PlanetCardStackLayer(offsetY = 70.dp, style = peekSolidFront()),
    PlanetCardStackLayer(offsetY = 344.dp, style = PlanetCardLayerStyle()),
)

private val StackVariant7Layers = listOf(
    PlanetCardStackLayer(offsetY = 0.dp, style = peekSolidTagline()),
    PlanetCardStackLayer(offsetY = 14.dp, style = peekSolidStatsOnly()),
    PlanetCardStackLayer(offsetY = 28.dp, style = peekSolidTagline()),
    PlanetCardStackLayer(
        offsetY = 42.dp,
        style = peekSolidTagline().copy(planetOffsetY = (-15.5).dp),
    ),
    PlanetCardStackLayer(
        offsetY = 56.dp,
        style = PlanetCardLayerStyle(
            backgroundColor = SolarColors.CardBackgroundSolid,
            planetAlpha = PeekPlanetAlpha,
            elevateTitle = true,
        ),
    ),
    PlanetCardStackLayer(
        offsetY = 70.dp,
        style = PlanetCardLayerStyle(
            backgroundColor = SolarColors.CardBackgroundSolid,
            planetAlpha = PeekPlanetAlpha,
        ),
    ),
    PlanetCardStackLayer(offsetY = 84.dp, style = peekSolidFront()),
)

internal fun defaultStackLayers(): List<PlanetCardStackLayer> {
    val pitch = PlanetCardDimens.Height + PlanetCardDimens.ListGap
    return List(7) { index ->
        PlanetCardStackLayer(
            offsetY = pitch * index,
            style = PlanetCardLayerStyle(),
        )
    }
}

internal fun PlanetCardStackVariant.layers(): List<PlanetCardStackLayer> = when (this) {
    PlanetCardStackVariant.Default -> emptyList()
    PlanetCardStackVariant.Variant2 -> StackVariant2Layers
    PlanetCardStackVariant.Variant3 -> StackVariant3Layers
    PlanetCardStackVariant.Variant4 -> StackVariant4Layers
    PlanetCardStackVariant.Variant5 -> StackVariant5Layers
    PlanetCardStackVariant.Variant6 -> StackVariant6Layers
    PlanetCardStackVariant.Variant7 -> StackVariant7Layers
}

internal fun PlanetCardStackVariant.containerHeight(): Dp = PlanetCardDimens.StackContainerHeight
