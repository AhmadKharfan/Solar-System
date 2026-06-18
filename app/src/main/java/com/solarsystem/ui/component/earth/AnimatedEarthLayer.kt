package com.solarsystem.ui.component.earth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.solarsystem.R
import com.solarsystem.ui.modifier.EarthShadowBleed
import com.solarsystem.ui.modifier.earthFigmaDropShadow
import com.solarsystem.ui.modifier.earthPlacement
import com.solarsystem.ui.motion.lerp
import com.solarsystem.ui.tokens.ScreenDimens

@Composable
fun AnimatedEarthLayer(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val fraction = progress.coerceIn(0f, 1f)
    val alpha = lerp(ScreenDimens.EarthStartAlpha, ScreenDimens.EarthEndAlpha, fraction)
    val scale = lerp(1f, ScreenDimens.EarthEndScale, fraction)
    val visualSize = ScreenDimens.EarthBaseSize * scale

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { clip = false },
    ) {
        val startLeft = maxWidth / 2 - 8.dp - ScreenDimens.EarthBaseSize / 2
        val startTop = maxHeight + ScreenDimens.EarthStartBottomOverflow - ScreenDimens.EarthBaseSize

        val left = lerp(startLeft, ScreenDimens.EarthEndLeft, fraction)
        val top = lerp(startTop, ScreenDimens.EarthEndTop, fraction)

        val bleed = EarthShadowBleed

        Box(
            modifier = Modifier
                .earthPlacement(
                    left = left - bleed,
                    top = top - bleed,
                    width = visualSize + bleed * 2,
                    height = visualSize + bleed * 2,
                )
                .graphicsLayer {
                    clip = false
                    this.alpha = alpha
                }
                .earthFigmaDropShadow(discDiameter = visualSize),
        )

        Box(
            modifier = Modifier
                .earthPlacement(left, top, ScreenDimens.EarthBaseSize, ScreenDimens.EarthBaseSize)
                .graphicsLayer {
                    clip = false
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = TransformOrigin(0f, 0f)
                    this.alpha = alpha
                },
        ) {
            Image(
                painter = painterResource(R.drawable.earth),
                contentDescription = "Earth",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
