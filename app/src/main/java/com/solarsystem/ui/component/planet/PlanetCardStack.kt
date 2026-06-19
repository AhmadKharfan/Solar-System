package com.solarsystem.ui.component.planet

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.solarsystem.data.PlanetCatalog
import com.solarsystem.model.PlanetCardModel
import com.solarsystem.ui.motion.interpolatePlanetCardVisualState
import com.solarsystem.ui.motion.interpolatePlanetStackOffsetY
import com.solarsystem.ui.preview.SolarPreviewSurface
import com.solarsystem.ui.tokens.PlanetCardDimens
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun ScrollableInterpolatedPlanetCardStack(
    entranceStackProgressProvider: () -> Float,
    onFirstStepChanged: (Boolean) -> Unit,
    onRequestScreenExit: () -> Unit,
    modifier: Modifier = Modifier,
    planets: List<PlanetCardModel> = PlanetCatalog.all,
) {
    val density = LocalDensity.current
    val motionScope = rememberCoroutineScope()
    val maxStep = (planets.size - 1).coerceAtLeast(1)
    val stepProgress = remember { Animatable(0f) }
    val settledStep = remember { mutableIntStateOf(0) }
    val styleStep by remember(maxStep) {
        derivedStateOf {
            stepProgress.value.roundToInt().coerceIn(0, maxStep)
        }
    }
    val motionStackProgressProvider = remember(maxStep) {
        {
            1f - (stepProgress.value / maxStep.toFloat()).coerceIn(0f, 1f)
        }
    }
    val snapAnimationSpec = remember {
        tween<Float>(
            durationMillis = 560,
            easing = CubicBezierEasing(0.18f, 0.0f, 0.08f, 1.0f),
        )
    }

    LaunchedEffect(stepProgress) {
        snapshotFlow { stepProgress.value <= 0.01f }
            .distinctUntilChanged()
            .collect { atFirstStep ->
                onFirstStepChanged(atFirstStep)
            }
    }

    Box(
        modifier = modifier
            .graphicsLayer { clip = false }
            .pointerInput(maxStep, density) {
                val snapDistance = PlanetCardDimens.SnapDragDistance.toPx()
                val distanceThreshold = PlanetCardDimens.SnapDistanceThreshold.toPx()
                val velocityThreshold = PlanetCardDimens.SnapVelocityThreshold.toPx()
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val entranceStackProgress = entranceStackProgressProvider()
                    if (entranceStackProgress < 0.95f) return@awaitEachGesture

                    val velocityTracker = VelocityTracker()
                    velocityTracker.addPosition(down.uptimeMillis, down.position)
                    val gestureStartStep = settledStep.intValue
                    val gestureStartProgress = stepProgress.value
                    val currentStackProgress = 1f - (stepProgress.value / maxStep.toFloat()).coerceIn(0f, 1f)
                    val hitCardBody = with(density) {
                        isCardBodyHit(
                            position = down.position,
                            stackProgress = currentStackProgress,
                            planets = planets,
                        )
                    }
                    var totalDragY = 0f
                    var isDraggingCard = false
                    var routedToScreen = false
                    var dragSnapJob: Job? = null
                    var latestDraggedStep = gestureStartProgress

                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (!change.pressed) break

                        val dragY = change.positionChange().y
                        totalDragY += dragY
                        velocityTracker.addPosition(change.uptimeMillis, change.position)

                        if (!isDraggingCard && !routedToScreen && kotlin.math.abs(totalDragY) >= viewConfiguration.touchSlop) {
                            val targetDirection = when {
                                totalDragY < 0f -> 1
                                totalDragY > 0f -> -1
                                else -> 0
                            }
                            val canMoveCard = hitCardBody &&
                                (gestureStartStep + targetDirection).coerceIn(0, maxStep) != gestureStartStep

                            if (canMoveCard) {
                                isDraggingCard = true
                            } else {
                                routedToScreen = true
                                change.consume()
                                onRequestScreenExit()
                                break
                            }
                        }

                        if (isDraggingCard) {
                            change.consume()
                            val draggedStep = (gestureStartProgress - totalDragY / snapDistance)
                                .coerceIn(0f, maxStep.toFloat())
                            latestDraggedStep = draggedStep
                            dragSnapJob?.cancel()
                            dragSnapJob = motionScope.launch {
                                stepProgress.snapTo(draggedStep)
                            }
                        }
                    }

                    if (isDraggingCard) {
                        dragSnapJob?.cancel()
                        val velocityY = velocityTracker.calculateVelocity().y
                        val distanceStep = latestDraggedStep - gestureStartStep.toFloat()
                        val velocityStep = when {
                            velocityY <= -velocityThreshold -> 1
                            velocityY >= velocityThreshold -> -1
                            else -> 0
                        }
                        val thresholdStep = when {
                            distanceStep >= distanceThreshold / snapDistance -> 1
                            distanceStep <= -distanceThreshold / snapDistance -> -1
                            else -> 0
                        }
                        val targetStep = (gestureStartStep + if (velocityStep != 0) velocityStep else thresholdStep)
                            .coerceIn(0, maxStep)

                        settledStep.intValue = targetStep
                        motionScope.launch {
                            stepProgress.snapTo(latestDraggedStep)
                            stepProgress.animateTo(
                                targetValue = targetStep.toFloat(),
                                animationSpec = snapAnimationSpec,
                            )
                        }
                    }
                }
            },
    ) {
        InterpolatedPlanetCardStack(
            motionStackProgressProvider = motionStackProgressProvider,
            styleStackProgress = 1f - (styleStep / maxStep.toFloat()).coerceIn(0f, 1f),
            planets = planets,
            modifier = Modifier
                .padding(top = PlanetCardDimens.StackTopPadding)
                .graphicsLayer { clip = false },
        )
    }
}

private fun Density.isCardBodyHit(
    position: Offset,
    stackProgress: Float,
    planets: List<PlanetCardModel>,
): Boolean {
    val cardWidth = PlanetCardDimens.Width.toPx()
    if (position.x !in 0f..cardWidth) return false

    val topPadding = PlanetCardDimens.StackTopPadding.toPx()
    val cardHeight = PlanetCardDimens.Height.toPx()
    return planets.indices.any { index ->
        val top = topPadding + interpolatePlanetStackOffsetY(index, stackProgress).toPx()
        position.y in top..(top + cardHeight)
    }
}

@Composable
fun InterpolatedPlanetCardStack(
    motionStackProgressProvider: () -> Float,
    styleStackProgress: Float,
    modifier: Modifier = Modifier,
    planets: List<PlanetCardModel> = PlanetCatalog.all,
) {
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .graphicsLayer { clip = false }
            .width(PlanetCardDimens.Width)
            .height(PlanetCardDimens.StackContainerHeight),
    ) {
        planets.forEachIndexed { index, planet ->
            val zIndex = stackZIndex(
                index = index,
                lastIndex = planets.lastIndex,
                stackProgress = styleStackProgress,
            )
            PlanetInfoCard(
                model = planet,
                visualStateProvider = {
                    interpolatePlanetCardVisualState(
                        index = index,
                        progress = motionStackProgressProvider(),
                    )
                },
                modifier = Modifier
                    .zIndex(zIndex)
                    .align(Alignment.TopStart)
                    .graphicsLayer {
                        clip = false
                        translationY = with(density) {
                            interpolatePlanetStackOffsetY(
                                index = index,
                                progress = motionStackProgressProvider(),
                            ).toPx()
                        }
                    },
            )
        }
    }
}

@Composable
fun PlanetCardStack(
    variant: PlanetCardStackVariant,
    modifier: Modifier = Modifier,
    planets: List<PlanetCardModel> = PlanetCatalog.all,
) {
    if (variant == PlanetCardStackVariant.Default) {
        DefaultPlanetStack(
            planets = planets,
            modifier = modifier,
        )
        return
    }

    LayeredPlanetStack(
        variant = variant,
        planets = planets,
        modifier = modifier,
    )
}

@Composable
private fun DefaultPlanetStack(
    planets: List<PlanetCardModel>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(PlanetCardDimens.Width)
            .height(PlanetCardStackVariant.Default.containerHeight()),
        verticalArrangement = Arrangement.spacedBy(PlanetCardDimens.ListGap),
    ) {
        planets.forEach { planet ->
            PlanetInfoCard(model = planet)
        }
    }
}

@Composable
private fun LayeredPlanetStack(
    variant: PlanetCardStackVariant,
    planets: List<PlanetCardModel>,
    modifier: Modifier = Modifier,
) {
    val layers = variant.layers()
    val density = LocalDensity.current
    Box(
        modifier = modifier
            .graphicsLayer { clip = false }
            .width(PlanetCardDimens.Width)
            .height(variant.containerHeight()),
    ) {
        planets.forEachIndexed { index, planet ->
            val layer = layers.getOrElse(index) { layers.last() }
            val offsetYPx = with(density) { layer.offsetY.toPx() }
            PlanetInfoCard(
                model = planet,
                layerStyle = layer.style,
                modifier = Modifier
                    .zIndex(stackZIndex(index, planets.lastIndex, stackProgress = 0f))
                    .align(Alignment.TopStart)
                    .graphicsLayer {
                        clip = false
                        translationY = offsetYPx
                    },
            )
        }
    }
}

private fun stackZIndex(
    index: Int,
    lastIndex: Int,
    stackProgress: Float,
): Float {
    val stackedAmount = 1f - stackProgress.coerceIn(0f, 1f)
    val lastCardBoost = if (index == lastIndex && stackedAmount > 0.82f) 100f else 0f
    return index.toFloat() + lastCardBoost
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1223, widthDp = 360, heightDp = 400)
@Composable
private fun PlanetCardStackVariant7Preview() {
    SolarPreviewSurface(
        modifier = Modifier.padding(
            top = 24.dp + PlanetCardDimens.PlanetOverflowTop,
            start = 16.dp,
            end = 16.dp,
        ),
    ) {
        PlanetCardStack(variant = PlanetCardStackVariant.Variant7)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1223, widthDp = 360, heightDp = 1700)
@Composable
private fun PlanetCardStackVariant2Preview() {
    SolarPreviewSurface(modifier = Modifier.padding(16.dp)) {
        PlanetCardStack(variant = PlanetCardStackVariant.Variant2)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1223, widthDp = 360, heightDp = 2200)
@Composable
private fun PlanetCardStackDefaultPreview() {
    SolarPreviewSurface(
        modifier = Modifier.padding(
            top = 24.dp + PlanetCardDimens.PlanetOverflowTop,
            start = 16.dp,
            end = 16.dp,
            bottom = 16.dp,
        ),
    ) {
        PlanetCardStack(variant = PlanetCardStackVariant.Default)
    }
}
