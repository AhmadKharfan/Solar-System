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
import com.solarsystem.ui.motion.SolarMotionProgress
import com.solarsystem.ui.motion.lerp
import com.solarsystem.ui.tokens.ScreenDimens

@Composable
fun AnimatedEarthLayer(
    motion: SolarMotionProgress,
    modifier: Modifier = Modifier,
) {
    val alpha = lerp(
        ScreenDimens.EarthStartAlpha,
        ScreenDimens.EarthEndAlpha,
        motion.earthOpacityProgress,
    )
    val scale = lerp(1f, ScreenDimens.EarthEndScale, motion.earthScaleProgress)
    val visualSize = ScreenDimens.EarthBaseSize * scale

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { clip = false },
    ) {
        val startLeft = maxWidth / 2 - 8.dp - ScreenDimens.EarthBaseSize / 2
        val startTop = maxHeight + ScreenDimens.EarthStartBottomOverflow - ScreenDimens.EarthBaseSize

        val left = lerp(startLeft, ScreenDimens.EarthEndLeft, motion.earthXProgress)
        val top = lerp(startTop, ScreenDimens.EarthEndTop, motion.earthYProgress)
        val translationX = left - startLeft
        val translationY = top - startTop

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
                .earthPlacement(startLeft, startTop, ScreenDimens.EarthBaseSize, ScreenDimens.EarthBaseSize)
                .graphicsLayer {
                    clip = false
                    this.translationX = translationX.toPx()
                    this.translationY = translationY.toPx()
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
