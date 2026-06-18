package com.solarsystem.ui.component.background

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.solarsystem.ui.motion.lerpColor
import com.solarsystem.ui.motion.sampleGradientColor

private val BaseSurface = Color(0xFF0D0608)

private val StartGradientStops = listOf(
    0f to Color(0x00000000),
    0.24545f to Color(0xFF060816),
    0.43331f to Color(0xFF0F172A),
    1f to Color(0xFF020D3C),
)

private val EndGradientStops = listOf(
    0f to Color(0xFF1E1B4B),
    0.5f to Color(0xFF0F172A),
    1f to Color(0xFF030712),
)

private val BlendedGradientPositions = floatArrayOf(
    0f,
    0.24545f,
    0.43331f,
    0.5f,
    1f,
)

@Composable
fun ScreenBackground(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val blend = progress.coerceIn(0f, 1f)
    val gradient = remember(blend) {
        Brush.verticalGradient(
            colorStops = BlendedGradientPositions.map { position ->
                val startColor = sampleGradientColor(StartGradientStops, position)
                val endColor = sampleGradientColor(EndGradientStops, position)
                position to lerpColor(startColor, endColor, blend)
            }.toTypedArray(),
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BaseSurface),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient),
        )
    }
}
