package com.solarsystem.ui.modifier

import android.graphics.BlurMaskFilter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val EarthShadowColor = Color(0xFF4197E7)
private const val EarthShadowAlpha = 0.25f
private val EarthShadowOffsetY = (-12).dp
private val EarthShadowBlur = 50.dp

fun Modifier.earthFigmaDropShadow(
    discDiameter: Dp,
    shadowAlpha: Float = EarthShadowAlpha,
): Modifier = drawBehind {
    val discRadius = discDiameter.toPx() / 2f
    val blurPx = EarthShadowBlur.toPx()
    val offsetY = EarthShadowOffsetY.toPx()
    val center = Offset(size.width / 2f, size.height / 2f)
    val shadowCenter = Offset(center.x, center.y + offsetY)
    val shadowArgb = EarthShadowColor.copy(alpha = shadowAlpha).toArgb()

    drawIntoCanvas { canvas ->
        val paint = android.graphics.Paint().apply {
            isAntiAlias = true
            color = shadowArgb
            maskFilter = BlurMaskFilter(blurPx, BlurMaskFilter.Blur.NORMAL)
        }
        canvas.nativeCanvas.drawCircle(shadowCenter.x, shadowCenter.y, discRadius, paint)
    }
}

val EarthShadowBleed: Dp = 62.dp
