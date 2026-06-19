package com.solarsystem.ui.component.background

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
        Image(
            painter = painterResource(R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .offset(
                    x = frameLeft + ScreenDimens.BackgroundImageLeft,
                    y = ScreenDimens.BackgroundImageTop,
                )
                .size(ScreenDimens.BackgroundImageSize)
                .graphicsLayer {
                    clip = false
                    val alpha = lerp(
                        ScreenDimens.AtmosphereStartAlpha,
                        ScreenDimens.AtmosphereEndAlpha,
                        progressProvider().coerceIn(0f, 1f),
                    )
                    this.alpha = alpha
                },
        )
    }
}
