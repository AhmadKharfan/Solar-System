package com.solarsystem.ui.component.background

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.solarsystem.R
import com.solarsystem.ui.motion.lerp
import com.solarsystem.ui.tokens.ScreenDimens

@Composable
fun AtmosphereBackdropLayer(
    progressProvider: () -> Float,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { clip = false },
    ) {
        val frameLeft = (maxWidth - ScreenDimens.FrameWidth) / 2
        val frameTop = (maxHeight - ScreenDimens.FrameHeight) / 2
        val viewportLeft = frameLeft + ScreenDimens.BackgroundImageLeft
        val viewportTop = frameTop + ScreenDimens.BackgroundImageTop

        // Extend draw bounds to the full screen so no edge strips are left empty.
        val coverLeft = minOf(viewportLeft, 0.dp)
        val coverTop = minOf(viewportTop, 0.dp)
        val coverWidth = maxOf(maxWidth - coverLeft, ScreenDimens.BackgroundImageSize)
        val coverHeight = maxOf(maxHeight - coverTop, ScreenDimens.BackgroundImageSize)

        // Keep the Figma 800×800 object-cover window anchored at (viewportLeft, viewportTop).
        val viewportCenterX = viewportLeft + ScreenDimens.BackgroundImageViewportCenterX
        val viewportCenterY = viewportTop + ScreenDimens.BackgroundImageViewportCenterY
        val horizontalBias = ((viewportCenterX - coverLeft).value / coverWidth.value - 0.5f) * 2f
        val verticalBias = ((viewportCenterY - coverTop).value / coverHeight.value - 0.5f) * 2f

        Box(
            modifier = Modifier
                .offset(x = coverLeft, y = coverTop)
                .requiredSize(width = coverWidth, height = coverHeight)
                .graphicsLayer { clip = false },
        ) {
            Image(
                painter = painterResource(R.drawable.img_background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alignment = BiasAlignment(
                    horizontalBias = horizontalBias.coerceIn(-1f, 1f),
                    verticalBias = verticalBias.coerceIn(-1f, 1f),
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        clip = false
                        alpha = lerp(
                            ScreenDimens.AtmosphereStartAlpha,
                            ScreenDimens.AtmosphereEndAlpha,
                            progressProvider().coerceIn(0f, 1f),
                        )
                    },
            )
        }
    }
}
