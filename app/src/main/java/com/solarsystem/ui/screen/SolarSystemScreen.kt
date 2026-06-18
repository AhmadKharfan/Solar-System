package com.solarsystem.ui.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import com.solarsystem.ui.component.background.AtmosphereBackdropLayer
import com.solarsystem.ui.component.background.ScreenBackground
import com.solarsystem.ui.component.earth.AnimatedEarthLayer
import com.solarsystem.ui.component.hero.HeroHeaderLayer
import com.solarsystem.ui.component.hero.SwipeHintFooter
import com.solarsystem.ui.component.planet.ScrollableInterpolatedPlanetCardStack
import com.solarsystem.ui.motion.SolarExploreTween
import com.solarsystem.ui.motion.SolarMotionAnchor
import com.solarsystem.ui.motion.SolarMotionProgress
import com.solarsystem.ui.motion.SolarReturnTween
import com.solarsystem.ui.motion.progressValue
import com.solarsystem.ui.motion.solarMotionProgress
import com.solarsystem.ui.theme.SolarSystemTheme
import com.solarsystem.ui.tokens.ScreenDimens
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun SolarSystemScreen(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val anchoredProgress = remember { Animatable(0f) }
    var cardsAtFirstStep by remember { mutableStateOf(true) }
    val screenExitFromCards = remember {
        {
            scope.launch {
                anchoredProgress.animateTo(
                    targetValue = SolarMotionAnchor.Expanded.progressValue(),
                    animationSpec = SolarReturnTween,
                )
            }
            Unit
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { clip = false }
            .pointerInput(cardsAtFirstStep, density) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val touchSlop = viewConfiguration.touchSlop
                    val cardSectionTopPx = with(density) { ScreenDimens.CardsEndTop.toPx() }
                    val startedInCardSection = down.position.y >= cardSectionTopPx
                    var totalDragY = 0f
                    var transitionStarted = false

                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (!change.pressed) break

                        totalDragY += change.positionChange().y
                        if (!transitionStarted && abs(totalDragY) >= touchSlop) {
                            val isExpanded = anchoredProgress.value < 0.5f
                            val canToggleFromCollapsed = !startedInCardSection
                            if (!isExpanded && !canToggleFromCollapsed) continue

                            transitionStarted = true
                            scope.launch {
                                val target = if (isExpanded) {
                                    SolarMotionAnchor.Collapsed
                                } else {
                                    SolarMotionAnchor.Expanded
                                }
                                val spec = if (isExpanded) SolarExploreTween else SolarReturnTween
                                anchoredProgress.animateTo(
                                    targetValue = target.progressValue(),
                                    animationSpec = spec,
                                )
                            }
                        }
                    }
                }
            },
    ) {
        SolarMotionScene(
            progressProvider = { anchoredProgress.value },
            onCardsAtFirstStepChanged = { cardsAtFirstStep = it },
            onRequestScreenExitFromCards = screenExitFromCards,
        )
    }
}

@Composable
private fun BoxScope.SolarMotionScene(
    progressProvider: () -> Float,
    onCardsAtFirstStepChanged: (Boolean) -> Unit,
    onRequestScreenExitFromCards: () -> Unit,
) {
    val motion = solarMotionProgress(progressProvider())

    ScreenBackground(progress = motion.boundedProgress)

    AtmosphereBackdropLayer(progress = motion.boundedProgress)

    Box(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .widthIn(max = ScreenDimens.FrameWidth)
            .fillMaxWidth()
            .fillMaxHeight()
            .graphicsLayer { clip = false },
    ) {
        SolarSystemContent(
            motion = motion,
            onCardsAtFirstStepChanged = onCardsAtFirstStepChanged,
            onRequestScreenExitFromCards = onRequestScreenExitFromCards,
        )
    }
}

@Composable
private fun BoxScope.SolarSystemContent(
    motion: SolarMotionProgress,
    onCardsAtFirstStepChanged: (Boolean) -> Unit,
    onRequestScreenExitFromCards: () -> Unit,
) {
    val density = LocalDensity.current
    val cardsScreenTopPx = with(density) { motion.cardsScreenTop.toPx() }

    AnimatedEarthLayer(
        motion = motion,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { clip = false },
    )

    HeroHeaderLayer(
        progress = motion.cardPositionProgress,
        modifier = Modifier.align(Alignment.TopCenter),
    )

    SwipeHintFooter(
        progress = motion.cardPositionProgress,
        modifier = Modifier.align(Alignment.BottomCenter),
    )

    ScrollableInterpolatedPlanetCardStack(
        entranceStackProgress = motion.cardStackProgress,
        onFirstStepChanged = onCardsAtFirstStepChanged,
        onRequestScreenExit = onRequestScreenExitFromCards,
        modifier = Modifier
            .align(Alignment.TopStart)
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = ScreenDimens.CardsHorizontalPadding)
            .graphicsLayer {
                clip = false
                translationY = cardsScreenTopPx
            },
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun SolarSystemScreenPreview() {
    SolarSystemTheme {
        SolarSystemScreen()
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
private fun SolarSystemScreenWidePreview() {
    SolarSystemTheme {
        SolarSystemScreen()
    }
}
