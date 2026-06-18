package com.solarsystem.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp

@Immutable
data class PlanetCardStackLayer(
    val offsetY: Dp,
    val style: PlanetCardLayerStyle,
)
