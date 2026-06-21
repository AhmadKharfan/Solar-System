package com.solarsystem.ui.earth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.solarsystem.R
import com.solarsystem.motion.EarthStartBounds
import com.solarsystem.motion.earthDiscDiameter
import com.solarsystem.motion.earthScale
import com.solarsystem.motion.earthShadowAlpha
import com.solarsystem.motion.earthStartBounds
import com.solarsystem.ui.constants.EarthBaseSize
import com.solarsystem.ui.constants.EarthShadowBleed
import com.solarsystem.util.earthFigmaDropShadow
import com.solarsystem.util.earthPlacement
import com.solarsystem.util.interpolate
import com.solarsystem.motion.earthAlpha

@Composable
fun AnimatedEarthLayer(
    progressProvider: () -> Float,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { clip = false },
    ) {
        val startBounds = earthStartBounds(maxWidth = maxWidth, maxHeight = maxHeight)
        EarthShadowLayer(
            startBounds = startBounds,
            progressProvider = progressProvider,
        )
        EarthImageLayer(
            startBounds = startBounds,
            progressProvider = progressProvider,
        )
    }
}

@Composable
private fun EarthShadowLayer(
    startBounds: EarthStartBounds,
    progressProvider: () -> Float,
) {
    Box(
        modifier = Modifier
            .earthPlacement(
                left = startBounds.left - EarthShadowBleed,
                top = startBounds.top - EarthShadowBleed,
                width = EarthBaseSize + EarthShadowBleed * 2,
                height = EarthBaseSize + EarthShadowBleed * 2,
            )
            .graphicsLayer {
                applyEarthMotion(
                    progressProvider = progressProvider,
                    startBounds = startBounds,
                    scaleImage = false,
                )
            }
            .earthFigmaDropShadow(
                discDiameterProvider = { earthDiscDiameter(progressProvider()) },
                shadowAlphaProvider = { earthShadowAlpha(progressProvider()) },
            ),
    )
}

@Composable
private fun EarthImageLayer(
    startBounds: EarthStartBounds,
    progressProvider: () -> Float,
) {
    Box(
        modifier = Modifier
            .earthPlacement(
                left = startBounds.left,
                top = startBounds.top,
                width = EarthBaseSize,
                height = EarthBaseSize,
            )
            .graphicsLayer {
                applyEarthMotion(
                    progressProvider = progressProvider,
                    startBounds = startBounds,
                    scaleImage = true,
                )
            },
    ) {
        Image(
            painter = painterResource(R.drawable.img_earth),
            contentDescription = "Earth",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

private fun GraphicsLayerScope.applyEarthMotion(
    progressProvider: () -> Float,
    startBounds: EarthStartBounds,
    scaleImage: Boolean,
) {
    clip = false
    val progress = progressProvider()
    val alpha = earthAlpha(progress)
    val scale = earthScale(progress)
    val left = interpolate(startBounds.left, startBounds.endLeft, progress)
    val top = interpolate(startBounds.top, 76.dp, progress)

    translationX = (left - startBounds.left).toPx()
    translationY = (top - startBounds.top).toPx()
    transformOrigin = TransformOrigin(0f, 0f)
    this.alpha = alpha
    if (scaleImage) {
        scaleX = scale
        scaleY = scale
    }
}