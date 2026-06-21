package com.solarsystem.util

import android.graphics.BlurMaskFilter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.solarsystem.ui.constants.CardBorderWidth
import com.solarsystem.ui.constants.EarthShadowAlpha
import com.solarsystem.ui.constants.EarthShadowBleed
import com.solarsystem.ui.constants.SwipeArrowShadowBlur
import com.solarsystem.ui.constants.SwipeArrowShadowOffsetY
import com.solarsystem.ui.theme.SolarColors
import kotlin.math.roundToInt

fun Modifier.earthPlacement(
    left: Dp,
    top: Dp,
    width: Dp,
    height: Dp,
): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(
        Constraints.fixed(
            width = width.roundToPx(),
            height = height.roundToPx(),
        ),
    )
    layout(constraints.maxWidth, constraints.maxHeight) {
        placeable.place(left.roundToPx(), top.roundToPx())
    }
}

fun Modifier.earthFigmaDropShadow(
    discDiameterProvider: () -> Dp,
    shadowAlphaProvider: () -> Float = { EarthShadowAlpha },
): Modifier = drawBehind {
    val discDiameterPx = discDiameterProvider().toPx()
    val center = Offset(
        x = EarthShadowBleed.toPx() + discDiameterPx / 2f,
        y = EarthShadowBleed.toPx() + discDiameterPx / 2f,
    )
    drawEarthShadow(
        discDiameterPx = discDiameterPx,
        shadowAlpha = shadowAlphaProvider(),
        blurPx = 44.dp.toPx(),
        center = center,
    )
}

private fun DrawScope.drawEarthShadow(
    discDiameterPx: Float,
    shadowAlpha: Float,
    blurPx: Float,
    center: Offset,
) {
    val discRadius = discDiameterPx / 2f
    val offsetY = (-12).dp.toPx()
    val shadowCenter = Offset(center.x, center.y + offsetY)
    val shadowArgb = SolarColors.EarthShadow.copy(alpha = shadowAlpha).toArgb()

    drawIntoCanvas { canvas ->
        val paint = android.graphics.Paint().apply {
            isAntiAlias = true
            color = shadowArgb
            maskFilter = BlurMaskFilter(blurPx, BlurMaskFilter.Blur.NORMAL)
        }
        canvas.nativeCanvas.drawCircle(shadowCenter.x, shadowCenter.y, discRadius, paint)
    }
}

fun Modifier.planetShadow(
    glowColor: Color,
    planetWidth: Dp,
    planetHeight: Dp,
    alphaProvider: () -> Float = { 1f },
): Modifier = drawBehind {
    val glowAlpha = alphaProvider().coerceIn(0f, 1f)
    val blurPx = 82.dp.toPx()
    val planetRadiusPx = minOf(planetWidth.toPx(), planetHeight.toPx()) / 2f
    val glowRadius = planetRadiusPx + blurPx
    val center = Offset(size.width / 2f, size.height / 2f)
    val edgeStop = planetRadiusPx / glowRadius

    drawCircle(
        brush = Brush.radialGradient(
            colorStops = arrayOf(
                0f to Color.Transparent,
                (edgeStop * 0.72f) to Color.Transparent,
                (edgeStop * 0.92f) to glowColor.copy(alpha = glowColor.alpha * 0.42f * glowAlpha),
                (edgeStop * 1.18f) to glowColor.copy(alpha = glowColor.alpha * 0.20f * glowAlpha),
                (edgeStop * 1.55f) to glowColor.copy(alpha = glowColor.alpha * 0.06f * glowAlpha),
                1f to Color.Transparent,
            ),
            center = center,
            radius = glowRadius,
        ),
        radius = glowRadius,
        center = center,
    )
}

fun Modifier.arrowDropShadow(
    glowColor: Color,
    alphaProvider: () -> Float = { 1f },
): Modifier = drawBehind {
    val glow = glowColor.withMultipliedAlpha(alphaProvider().coerceIn(0f, 1f))
    val blurPx = SwipeArrowShadowBlur.toPx()
    val offsetY = SwipeArrowShadowOffsetY.toPx()
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
                0f to glow.copy(alpha = glow.alpha * 0.55f),
                0.65f to glow.copy(alpha = glow.alpha * 0.2f),
                1f to Color.Transparent,
            ),
            center = Offset(centerX, centerY),
            radius = maxOf(radiusX, radiusY),
        ),
        topLeft = Offset(centerX - radiusX, centerY - radiusY),
        size = Size(radiusX * 2f, radiusY * 2f),
    )
}

fun Modifier.cardBackground(
    frameProvider: () -> com.solarsystem.model.CardFrame,
): Modifier = drawBehind {
    val frame = frameProvider()
    val radius = 20.dp.toPx()
    val strokeWidth = CardBorderWidth.toPx()
    drawRoundRect(
        color = frame.backgroundColor.withMultipliedAlpha(frame.cardAlpha),
        cornerRadius = CornerRadius(radius, radius),
    )
    drawRoundRect(
        color = SolarColors.CardBorder.withMultipliedAlpha(frame.cardAlpha),
        cornerRadius = CornerRadius(radius, radius),
        style = Stroke(width = strokeWidth),
    )
}
