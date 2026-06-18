package com.solarsystem.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import com.solarsystem.ui.component.background.AtmosphereBackdropLayer
import com.solarsystem.ui.component.background.ScreenBackground
import com.solarsystem.ui.component.earth.AnimatedEarthLayer
import com.solarsystem.ui.component.hero.HeroHeaderLayer
import com.solarsystem.ui.component.hero.SwipeHintFooter
import com.solarsystem.ui.component.planet.InterpolatedPlanetCardStack
import com.solarsystem.ui.motion.rememberSolarScrollMetrics
import com.solarsystem.ui.theme.SolarSystemTheme
import com.solarsystem.ui.tokens.ScreenDimens
import kotlin.math.ceil

@Composable
fun SolarSystemScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val metrics by rememberSolarScrollMetrics(scrollState)

    val frameHeightPx = with(density) { ScreenDimens.FrameHeight.toPx() }
    val trackHeightPx = metrics.maxScrollPx + frameHeightPx + 128f
    val trackHeight = with(density) { trackHeightPx.toDp() }

    LaunchedEffect(scrollState, metrics.maxScrollPx) {
        snapshotFlow { scrollState.value }.collect { offset ->
            val max = ceil(metrics.maxScrollPx).toInt()
            if (offset > max) {
                scrollState.scrollTo(max)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { clip = false },
    ) {
        ScreenBackground(progress = metrics.heroProgress)

        AtmosphereBackdropLayer(progress = metrics.heroProgress)

        AnimatedEarthLayer(
            progress = metrics.heroProgress,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { clip = false },
        )

        HeroHeaderLayer(
            progress = metrics.heroProgress,
            modifier = Modifier.align(Alignment.TopCenter),
        )

        SwipeHintFooter(
            progress = metrics.heroProgress,
            modifier = Modifier.align(Alignment.BottomCenter),
        )

        InterpolatedPlanetCardStack(
            stackProgress = metrics.stackProgress,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(y = metrics.cardsScreenTop)
                .fillMaxWidth()
                .padding(horizontal = ScreenDimens.CardsHorizontalPadding)
                .graphicsLayer { clip = false },
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
        ) {
            Spacer(modifier = Modifier.height(trackHeight))
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun SolarSystemScreenPreview() {
    SolarSystemTheme {
        SolarSystemScreen()
    }
}
