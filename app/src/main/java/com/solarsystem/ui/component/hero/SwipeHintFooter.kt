package com.solarsystem.ui.component.hero

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.solarsystem.ui.theme.SolarTypography
import com.solarsystem.ui.tokens.ScreenDimens

@Composable
fun SwipeHintFooter(
    progressProvider: () -> Float,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                translationY = with(density) {
                    (ScreenDimens.SwipeHintEndTranslationY * progressProvider()).toPx()
                }
            }
            .padding(
                horizontal = ScreenDimens.SwipeHintHorizontalPadding,
                vertical = ScreenDimens.SwipeHintBottomPadding,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ScreenDimens.SwipeHintGap),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy((-4).dp),
        ) {
            repeat(3) {
                SwipeChevron(modifier = Modifier.size(24.dp))
            }
        }
        Text(
            text = "Swipe up to explore",
            style = SolarTypography.SwipeHint,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SwipeChevron(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = 1.5.dp.toPx()
        val path = Path().apply {
            moveTo(size.width * 0.23f, size.height * 0.62f)
            lineTo(size.width * 0.5f, size.height * 0.35f)
            lineTo(size.width * 0.77f, size.height * 0.62f)
        }
        drawPath(
            path = path,
            color = androidx.compose.ui.graphics.Color.White,
            style = Stroke(width = strokeWidth),
        )
    }
}
