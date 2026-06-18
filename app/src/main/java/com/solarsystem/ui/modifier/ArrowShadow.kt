package com.solarsystem.ui.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.solarsystem.ui.tokens.ArrowDimens

fun Modifier.arrowDropShadow(glowColor: Color): Modifier = drawBehind {
    val blurPx = ArrowDimens.SwipeShadowBlur.toPx()
    val offsetY = ArrowDimens.SwipeShadowOffsetY.toPx()
    val insetTop = size.height * 0.3308f
    val insetHorizontal = size.width * 0.2279f
    val insetBottom = size.height * 0.3529f
    val chevronWidth = size.width - insetHorizontal * 2f
    val chevronHeight = size.height - insetTop - insetBottom
    val centerX = size.width / 2f
    val centerY = insetTop + chevronHeight / 2f + offsetY
    val radiusX = chevronWidth / 2f + blurPx * 0.35f
    val radiusY = chevronHeight / 2f + blurPx * 0.35f

    drawOval(
        brush = Brush.radialGradient(
            colorStops = arrayOf(
                0f to glowColor.copy(alpha = glowColor.alpha * 0.55f),
                0.65f to glowColor.copy(alpha = glowColor.alpha * 0.2f),
                1f to Color.Transparent,
            ),
            center = Offset(centerX, centerY),
            radius = maxOf(radiusX, radiusY),
        ),
        topLeft = Offset(centerX - radiusX, centerY - radiusY),
        size = Size(radiusX * 2f, radiusY * 2f),
    )
}
