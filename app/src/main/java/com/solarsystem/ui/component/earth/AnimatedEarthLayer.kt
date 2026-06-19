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
import com.solarsystem.ui.motion.solarMotionProgress
import com.solarsystem.ui.tokens.ScreenDimens

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
        val startLeft = maxWidth / 2 - 8.dp - ScreenDimens.EarthBaseSize / 2
        val startTop = maxHeight + ScreenDimens.EarthStartBottomOverflow - ScreenDimens.EarthBaseSize

        val bleed = EarthShadowBleed

        Box(
            modifier = Modifier
                .earthPlacement(
                    left = startLeft - bleed,
                    top = startTop - bleed,
                    width = ScreenDimens.EarthBaseSize + bleed * 2,
                    height = ScreenDimens.EarthBaseSize + bleed * 2,
                )
                .graphicsLayer {
                    clip = false
                    val motion = solarMotionProgress(progressProvider())
                    val alpha = lerp(
                        ScreenDimens.EarthStartAlpha,
                        ScreenDimens.EarthEndAlpha,
                        motion.earthOpacityProgress,
                    )
                    val scale = lerp(1f, ScreenDimens.EarthEndScale, motion.earthScaleProgress)
                    val left = lerp(startLeft, ScreenDimens.EarthEndLeft, motion.earthXProgress)
                    val top = lerp(startTop, ScreenDimens.EarthEndTop, motion.earthYProgress)
                    translationX = (left - startLeft).toPx()
                    translationY = (top - startTop).toPx()
                    transformOrigin = TransformOrigin(0f, 0f)
                    this.alpha = alpha
                }
                .earthFigmaDropShadow(
                    discDiameterProvider = {
                        val motion = solarMotionProgress(progressProvider())
                        val scale = lerp(1f, ScreenDimens.EarthEndScale, motion.earthScaleProgress)
                        ScreenDimens.EarthBaseSize * scale
                    },
                    shadowAlphaProvider = {
                        val motion = solarMotionProgress(progressProvider())
                        val alpha = lerp(
                            ScreenDimens.EarthStartAlpha,
                            ScreenDimens.EarthEndAlpha,
                            motion.earthOpacityProgress,
                        )
                        val firstStateCompensation = lerp(0.52f, 1f, motion.earthScaleProgress)
                        0.25f * alpha * firstStateCompensation
                    },
                ),
        )

        Box(
            modifier = Modifier
                .earthPlacement(startLeft, startTop, ScreenDimens.EarthBaseSize, ScreenDimens.EarthBaseSize)
                .graphicsLayer {
                    clip = false
                    val motion = solarMotionProgress(progressProvider())
                    val alpha = lerp(
                        ScreenDimens.EarthStartAlpha,
                        ScreenDimens.EarthEndAlpha,
                        motion.earthOpacityProgress,
                    )
                    val scale = lerp(1f, ScreenDimens.EarthEndScale, motion.earthScaleProgress)
                    val left = lerp(startLeft, ScreenDimens.EarthEndLeft, motion.earthXProgress)
                    val top = lerp(startTop, ScreenDimens.EarthEndTop, motion.earthYProgress)
                    translationX = (left - startLeft).toPx()
                    translationY = (top - startTop).toPx()
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = TransformOrigin(0f, 0f)
                    this.alpha = alpha
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
}
