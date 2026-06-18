package com.solarsystem.ui.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.solarsystem.ui.tokens.PlanetCardDimens

fun Modifier.planetShadow(
    glowColor: Color,
    planetWidth: Dp,
    planetHeight: Dp,
): Modifier = drawBehind {
    val blurPx = PlanetCardDimens.PlanetShadowBlur.toPx()
    val planetRadiusPx = minOf(planetWidth.toPx(), planetHeight.toPx()) / 2f
    val glowRadius = planetRadiusPx + blurPx
    val center = Offset(size.width / 2f, size.height / 2f)
    val edgeStop = planetRadiusPx / glowRadius

    drawCircle(
        brush = Brush.radialGradient(
            colorStops = arrayOf(
                0f to Color.Transparent,
                (edgeStop * 0.72f) to Color.Transparent,
                (edgeStop * 0.92f) to glowColor.copy(alpha = glowColor.alpha * 0.55f),
                (edgeStop * 1.18f) to glowColor.copy(alpha = glowColor.alpha * 0.28f),
                (edgeStop * 1.55f) to glowColor.copy(alpha = glowColor.alpha * 0.08f),
                1f to Color.Transparent,
            ),
            center = center,
            radius = glowRadius,
        ),
        radius = glowRadius,
        center = center,
    )
}
