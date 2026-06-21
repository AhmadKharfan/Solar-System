package com.solarsystem.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.solarsystem.ui.background.AtmosphereBackdropLayer
import com.solarsystem.ui.background.ScreenBackground
import com.solarsystem.ui.cards.PlanetCardsLayer
import com.solarsystem.ui.earth.AnimatedEarthLayer
import com.solarsystem.ui.hero.HeroHeaderLayer
import com.solarsystem.ui.swipe.SwipeHintFooter

@Composable
fun BoxScope.SolarMotionScene(
    progressProvider: () -> Float,
    onCardsAtFirstStepChanged: (Boolean) -> Unit,
) {
    ScreenBackground(progressProvider = progressProvider)
    AtmosphereBackdropLayer(progressProvider = progressProvider)

    Box(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .fillMaxWidth()
            .fillMaxHeight()
            .graphicsLayer { clip = false },
    ) {
        SolarSystemContent(
            progressProvider = progressProvider,
        )
    }

    PlanetCardsLayer(
        progressProvider = progressProvider,
        onCardsAtFirstStepChanged = onCardsAtFirstStepChanged,
    )
}

@Composable
private fun BoxScope.SolarSystemContent(
    progressProvider: () -> Float,
) {
    AnimatedEarthLayer(
        progressProvider = progressProvider,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { clip = false },
    )

    HeroHeaderLayer(
        progressProvider = progressProvider,
        modifier = Modifier.align(Alignment.TopCenter),
    )

    SwipeHintFooter(
        progressProvider = progressProvider,
        modifier = Modifier.align(Alignment.BottomCenter),
    )
}