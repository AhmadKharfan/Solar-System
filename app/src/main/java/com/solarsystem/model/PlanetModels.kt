package com.solarsystem.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class PlanetStat(
    @param:DrawableRes val iconRes: Int,
    val label: String,
    val value: String,
    val hint: String? = null,
)

@Immutable
data class PlanetCardModel(
    val id: Int,
    val name: String,
    val tagline: String,
    @param:DrawableRes val imageRes: Int,
    val imageHeight: Dp = 112.dp,
    val glowColor: Color,
    val stats: List<PlanetStat>,
)

@Immutable
data class CardFrame(
    val offsetY: Dp = 0.dp,
    val cardAlpha: Float = 1f,
    val backgroundColor: Color = com.solarsystem.ui.theme.SolarColors.CardBackground,
    val planetAlpha: Float = 1f,
    val planetOffsetY: Dp = com.solarsystem.ui.constants.PlanetImageOffsetY,
    val titleAlpha: Float = 1f,
    val taglineAlpha: Float = 1f,
    val statsAlpha: Float = 1f,
    val elevatedTitleAlpha: Float = 0f,
)