package com.solarsystem.ui.component.hero

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import com.solarsystem.ui.component.arrow.SolarSwipeUpVariant
import com.solarsystem.ui.component.arrow.SwipeArrowLayer
import com.solarsystem.ui.component.arrow.layerRes
import com.solarsystem.ui.theme.SolarColors
import com.solarsystem.ui.theme.SolarTypography
import com.solarsystem.ui.tokens.ArrowDimens
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
            verticalArrangement = Arrangement.spacedBy(-ArrowDimens.SwipeOverlap),
        ) {
            SolarSwipeUpVariant.Default.layerRes().forEach { layerRes ->
                SwipeArrowLayer(
                    res = layerRes,
                    glowColor = SolarColors.SwipeHintArrowGlow,
                    arrowSize = ArrowDimens.SingleSize,
                )
            }
        }
        Text(
            text = "Swipe up to explore",
            style = SolarTypography.SwipeHint,
            textAlign = TextAlign.Center,
        )
    }
}
