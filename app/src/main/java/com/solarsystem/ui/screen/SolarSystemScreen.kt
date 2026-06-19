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
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.solarsystem.ui.component.background.AtmosphereBackdropLayer
import com.solarsystem.ui.component.background.ScreenBackground
import com.solarsystem.ui.component.earth.AnimatedEarthLayer
import com.solarsystem.ui.component.hero.HeroHeaderLayer
import com.solarsystem.ui.component.hero.SwipeHintFooter
import com.solarsystem.ui.component.planet.ScrollableInterpolatedPlanetCardStack
import com.solarsystem.ui.motion.OvershootSpringSpec
import com.solarsystem.ui.motion.SolarMotionAnchor
import com.solarsystem.ui.motion.progressValue
import com.solarsystem.ui.motion.solarMotionProgress
import com.solarsystem.ui.theme.SolarSystemTheme
import com.solarsystem.ui.tokens.ScreenDimens
import kotlinx.coroutines.Job
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
            Unit
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { clip = false }
            .pointerInput(cardsAtFirstStep, density) {
                val dragRangePx = with(density) { ScreenDimens.HeroScrollRange.toPx() }
                    .coerceAtLeast(1f)
                val swipeVelocityThresholdPx = with(density) { EarthSwipeVelocityThreshold.toPx() }
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val touchSlop = viewConfiguration.touchSlop
                    val cardSectionTopPx = with(density) { ScreenDimens.CardsSectionEndTop.toPx() }
                    val startedInCardSection = down.position.y >= cardSectionTopPx
                    val velocityTracker = VelocityTracker()
                    velocityTracker.addPosition(down.uptimeMillis, down.position)
                    val gestureStartProgress = anchoredProgress.value.coerceIn(0f, 1f)
                    var latestProgress = gestureStartProgress
                    var totalDragY = 0f
                    var draggingScreenMotion = false
                    var progressSnapJob: Job? = null

                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (!change.pressed) break

                        totalDragY += change.positionChange().y
                        velocityTracker.addPosition(change.uptimeMillis, change.position)
                        if (!draggingScreenMotion && abs(totalDragY) >= touchSlop) {
                            val isDraggingTowardCards = totalDragY < 0f
                            val isDraggingTowardHero = totalDragY > 0f
                            val canMoveProgress = when {
                                isDraggingTowardCards -> gestureStartProgress < 1f
                                isDraggingTowardHero -> gestureStartProgress > 0f
                                else -> false
                            }
                            val startedInSettledCards = startedInCardSection && gestureStartProgress >= 0.95f
                            val canDriveScreenMotion = if (startedInSettledCards) {
                                cardsAtFirstStep && isDraggingTowardHero
                            } else {
                                canMoveProgress
                            }

                            if (!canDriveScreenMotion) continue

                            draggingScreenMotion = true
                            progressSnapJob?.cancel()
                        }

                        if (draggingScreenMotion) {
                            change.consume()
                            latestProgress = (gestureStartProgress - totalDragY / dragRangePx)
                                .coerceIn(0f, 1f)
                            progressSnapJob?.cancel()
                            progressSnapJob = scope.launch {
                                anchoredProgress.snapTo(latestProgress)
                            }
                        }
                    }

                    if (draggingScreenMotion) {
                        progressSnapJob?.cancel()
                        val velocityY = velocityTracker.calculateVelocity().y
                        val target = when {
                            velocityY <= -swipeVelocityThresholdPx -> SolarMotionAnchor.Collapsed
                            velocityY >= swipeVelocityThresholdPx -> SolarMotionAnchor.Expanded
                            latestProgress >= 0.5f -> SolarMotionAnchor.Collapsed
                            else -> SolarMotionAnchor.Expanded
                        }
                        scope.launch {
                            anchoredProgress.snapTo(latestProgress)
                            anchoredProgress.animateTo(
                                targetValue = target.progressValue(),
                                animationSpec = OvershootSpringSpec,
                            )
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
    ScreenBackground(progressProvider = progressProvider)

    AtmosphereBackdropLayer(progressProvider = progressProvider)

    Box(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .widthIn(max = ScreenDimens.FrameWidth)
            .fillMaxWidth()
            .fillMaxHeight()
            .graphicsLayer { clip = false },
    ) {
        SolarSystemContent(
            progressProvider = progressProvider,
            onCardsAtFirstStepChanged = onCardsAtFirstStepChanged,
            onRequestScreenExitFromCards = onRequestScreenExitFromCards,
        )
    }
}

@Composable
private fun BoxScope.SolarSystemContent(
    progressProvider: () -> Float,
    onCardsAtFirstStepChanged: (Boolean) -> Unit,
    onRequestScreenExitFromCards: () -> Unit,
) {
    val density = LocalDensity.current

    AnimatedEarthLayer(
        progressProvider = progressProvider,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { clip = false },
    )

    HeroHeaderLayer(
        progressProvider = {
            solarMotionProgress(progressProvider()).cardPositionProgress
        },
        modifier = Modifier.align(Alignment.TopCenter),
    )

    SwipeHintFooter(
        progressProvider = {
            solarMotionProgress(progressProvider()).cardPositionProgress
        },
        modifier = Modifier.align(Alignment.BottomCenter),
    )

    ScrollableInterpolatedPlanetCardStack(
        entranceStackProgressProvider = {
            solarMotionProgress(progressProvider()).cardStackProgress
        },
        onFirstStepChanged = onCardsAtFirstStepChanged,
        onRequestScreenExit = onRequestScreenExitFromCards,
        modifier = Modifier
            .align(Alignment.TopStart)
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = ScreenDimens.CardsHorizontalPadding)
            .graphicsLayer {
                clip = false
                val motion = solarMotionProgress(progressProvider())
                val cardsScreenTopPx = with(density) { motion.cardsScreenTop.toPx() }
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

private val EarthSwipeVelocityThreshold = 850.dp
