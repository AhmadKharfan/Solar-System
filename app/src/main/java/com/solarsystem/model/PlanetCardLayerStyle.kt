package com.solarsystem.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.solarsystem.ui.theme.SolarColors
import com.solarsystem.ui.tokens.PlanetCardDimens

@Immutable
data class PlanetCardLayerStyle(
    val backgroundColor: Color = SolarColors.CardBackground,
    val planetAlpha: Float = 1f,
    val planetOffsetY: Dp = PlanetCardDimens.PlanetOffsetY,
    val showTitle: Boolean = true,
    val showTagline: Boolean = true,
    val showStats: Boolean = true,
    val taglineOnlyHeader: Boolean = false,
    val elevateTitle: Boolean = false,
)
