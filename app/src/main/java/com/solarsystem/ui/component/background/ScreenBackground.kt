package com.solarsystem.ui.component.background

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer

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

@Composable
fun ScreenBackground(
    progressProvider: () -> Float,
    modifier: Modifier = Modifier,
) {
    val startGradient = remember { Brush.verticalGradient(colorStops = StartGradientStops.toTypedArray()) }
    val endGradient = remember { Brush.verticalGradient(colorStops = EndGradientStops.toTypedArray()) }

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BaseSurface),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = 1f - progressProvider().coerceIn(0f, 1f)
                }
                .background(startGradient),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = progressProvider().coerceIn(0f, 1f)
                }
                .background(endGradient),
        )
    }
}
