package com.solarsystem.ui.swipe

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.solarsystem.motion.SwipeArrowCount
import com.solarsystem.motion.SwipeArrowIcon
import com.solarsystem.motion.SwipeArrowWaveDurationMillis
import com.solarsystem.motion.swipeArrowWaveAlpha
import com.solarsystem.ui.constants.SwipeArrowShadowBlur
import com.solarsystem.ui.constants.SwipeArrowShadowOffsetY
import com.solarsystem.ui.theme.SolarColors
import com.solarsystem.ui.theme.SolarTypography
import com.solarsystem.util.arrowDropShadow

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
                    (300.dp * progressProvider()).toPx()
                }
            }
            .padding(
                horizontal = 24.dp,
                vertical = 20.dp,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        AnimatedSwipeArrows()
        Text(
            text = "Swipe up to explore",
            style = SolarTypography.SwipeHint,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun AnimatedSwipeArrows() {
    val phase = rememberInfiniteTransition(label = "SwipeArrowWave").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = SwipeArrowWaveDurationMillis,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "SwipeArrowWavePhase",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy((-4).dp),
    ) {
        repeat(SwipeArrowCount) { index ->
            val arrowAlphaProvider = remember(index, phase) {
                {
                    swipeArrowWaveAlpha(
                        index = index,
                        phase = phase.value,
                    )
                }
            }
            SwipeArrowLayer(
                res = SwipeArrowIcon,
                glowColor = SolarColors.SwipeHintArrowGlow,
                alphaProvider = arrowAlphaProvider,
            )
        }
    }
}

@Composable
private fun SwipeArrowLayer(
    @DrawableRes res: Int,
    modifier: Modifier = Modifier,
    glowColor: Color,
    alphaProvider: () -> Float = { 1f },
) {
    val usesBlurShadow = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    Box(
        modifier = modifier
            .graphicsLayer { clip = false }
            .size(24.dp)
            .then(
                if (usesBlurShadow) {
                    Modifier
                } else {
                    Modifier.arrowDropShadow(
                        glowColor = glowColor,
                        alphaProvider = alphaProvider,
                    )
                },
            ),
    ) {
        if (usesBlurShadow) {
            SwipeArrowBlurShadow(
                res = res,
                glowColor = glowColor,
                alphaProvider = alphaProvider,
            )
        }
        SwipeArrowImage(
            res = res,
            alphaProvider = alphaProvider,
        )
    }
}

@Composable
private fun SwipeArrowBlurShadow(
    @DrawableRes res: Int,
    glowColor: Color,
    alphaProvider: () -> Float,
) {
    Image(
        painter = painterResource(res),
        contentDescription = null,
        colorFilter = ColorFilter.tint(glowColor.copy(alpha = 1f)),
        modifier = Modifier
            .fillMaxSize()
            .offset(y = SwipeArrowShadowOffsetY)
            .blur(SwipeArrowShadowBlur)
            .graphicsLayer {
                alpha = glowColor.alpha * alphaProvider().coerceIn(0f, 1f)
            },
        contentScale = ContentScale.Fit,
    )
}

@Composable
private fun SwipeArrowImage(
    @DrawableRes res: Int,
    alphaProvider: () -> Float,
) {
    Image(
        painter = painterResource(res),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                alpha = alphaProvider().coerceIn(0f, 1f)
            },
        contentScale = ContentScale.Fit,
    )
}