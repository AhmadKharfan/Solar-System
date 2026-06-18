package com.solarsystem.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.solarsystem.ui.tokens.PlanetCardDimens

@Immutable
data class PlanetStat(
    @DrawableRes val iconRes: Int,
    val label: String,
    val value: String,
    val hint: String? = null,
)

@Immutable
data class PlanetCardModel(
    val name: String,
    val tagline: String,
    @DrawableRes val imageRes: Int,
    val imageHeight: Dp = PlanetCardDimens.PlanetHeightDefault,
    val glowColor: Color,
    val stats: List<PlanetStat>,
)
