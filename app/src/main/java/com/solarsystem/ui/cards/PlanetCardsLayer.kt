package com.solarsystem.ui.cards

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.solarsystem.motion.cardStackEntranceProgress
import com.solarsystem.motion.cardsScreenTop

@Composable
fun BoxScope.PlanetCardsLayer(
    progressProvider: () -> Float,
    onCardsAtFirstStepChanged: (Boolean) -> Unit,
) {
    val density = LocalDensity.current

    ScrollableInterpolatedPlanetCardStack(
        entranceStackProgressProvider = {
            cardStackEntranceProgress(progressProvider())
        },
        onFirstStepChanged = onCardsAtFirstStepChanged,
        modifier = Modifier
            .align(Alignment.TopStart)
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 16.dp)
            .graphicsLayer {
                clip = false
                translationY = with(density) { cardsScreenTop(progressProvider()).toPx() }
            },
    )
}