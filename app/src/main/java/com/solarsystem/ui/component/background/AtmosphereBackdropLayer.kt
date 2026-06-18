package com.solarsystem.ui.component.background

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val alpha = lerp(
        ScreenDimens.AtmosphereStartAlpha,
        ScreenDimens.AtmosphereEndAlpha,
        progress.coerceIn(0f, 1f),
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { clip = false },
    ) {
        Image(
            painter = painterResource(R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    clip = false
                    this.alpha = alpha
                },
        )
    }
}
