package com.solarsystem.ui.cards

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.zIndex
import com.solarsystem.gesture.PlanetCardStackState
import com.solarsystem.gesture.planetCardStackDrag
import com.solarsystem.gesture.rememberPlanetCardStackState
import com.solarsystem.model.PlanetCardModel
import com.solarsystem.model.PlanetCatalog
import com.solarsystem.motion.cardFloatingScale
import com.solarsystem.motion.cardSettleTranslationY
import com.solarsystem.motion.stackZIndex
import com.solarsystem.motion.stackedCardFrame
import com.solarsystem.motion.visibleCardRange
import com.solarsystem.ui.constants.CardHeight
import com.solarsystem.ui.constants.CardListGap
import com.solarsystem.ui.constants.CardStackTopPadding
import com.solarsystem.ui.constants.VisibleStackSlots
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.roundToInt

@Composable
fun ScrollableInterpolatedPlanetCardStack(
    entranceStackProgressProvider: () -> Float,
    onFirstStepChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    planets: List<PlanetCardModel> = PlanetCatalog.all,
) {
    if (planetCardStackEmptyState(planets, onFirstStepChanged, modifier)) {
        return
    }

    val density = LocalDensity.current
    val motionScope = rememberCoroutineScope()
    val stackState = rememberPlanetCardStackState(planets)
    val visibleCardRange by rememberVisibleCardRange(stackState, planets)

    ReportFirstStackStep(
        stepProgress = stackState.stepProgress,
        onFirstStepChanged = onFirstStepChanged,
    )

    PlanetCardStackContainer(
        entranceStackProgressProvider = entranceStackProgressProvider,
        planets = planets,
        visibleCardRange = visibleCardRange,
        density = density,
        motionScope = motionScope,
        stackState = stackState,
        modifier = modifier,
    )
}

@Composable
private fun planetCardStackEmptyState(
    planets: List<PlanetCardModel>,
    onFirstStepChanged: (Boolean) -> Unit,
    modifier: Modifier,
): Boolean {
    if (planets.isNotEmpty()) return false

    LaunchedEffect(onFirstStepChanged) {
        onFirstStepChanged(true)
    }
    Box(modifier = modifier)
    return true
}

@Composable
private fun rememberVisibleCardRange(
    stackState: PlanetCardStackState,
    planets: List<PlanetCardModel>,
): State<IntRange> =
    remember(stackState.maxStep) {
        derivedStateOf {
            visibleCardRange(
                stepProgress = stackState.stepProgress.value,
                lastIndex = planets.lastIndex,
            )
        }
    }

@Composable
private fun ReportFirstStackStep(
    stepProgress: Animatable<Float, AnimationVector1D>,
    onFirstStepChanged: (Boolean) -> Unit,
) {
    LaunchedEffect(stepProgress) {
        snapshotFlow { stepProgress.value <= 0.01f }
            .distinctUntilChanged()
            .collect { atFirstStep ->
                onFirstStepChanged(atFirstStep)
            }
    }
}

@Composable
private fun PlanetCardStackContainer(
    entranceStackProgressProvider: () -> Float,
    planets: List<PlanetCardModel>,
    visibleCardRange: IntRange,
    density: Density,
    motionScope: CoroutineScope,
    stackState: PlanetCardStackState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .graphicsLayer { clip = false }
            .planetCardStackDrag(
                entranceStackProgressProvider = entranceStackProgressProvider,
                planets = planets,
                density = density,
                motionScope = motionScope,
                stackState = stackState,
            ),
    ) {
        InterpolatedPlanetCardStack(
            stepProgressProvider = { stackState.stepProgress.value },
            activeCardIndexProvider = {
                stackState.stepProgress.value.roundToInt().coerceIn(0, planets.lastIndex)
            },
            settlePhaseProvider = { stackState.cardSettlePhase.value },
            settleDirectionProvider = { stackState.settleDirection.floatValue },
            visibleCardRange = visibleCardRange,
            planets = planets,
            modifier = Modifier
                .padding(top = CardStackTopPadding)
                .graphicsLayer { clip = false },
        )
    }
}

@Composable
private fun InterpolatedPlanetCardStack(
    stepProgressProvider: () -> Float,
    visibleCardRange: IntRange,
    modifier: Modifier = Modifier,
    planets: List<PlanetCardModel> = PlanetCatalog.all,
    activeCardIndexProvider: () -> Int = { 0 },
    settlePhaseProvider: () -> Float = { 1f },
    settleDirectionProvider: () -> Float = { 0f },
) {
    val renderContext = PlanetStackRenderContext(
        lastIndex = planets.lastIndex,
        count = planets.size,
        boostedIndex = visibleCardRange.last.coerceAtMost(planets.lastIndex),
        stepProgressProvider = stepProgressProvider,
        activeCardIndexProvider = activeCardIndexProvider,
        settlePhaseProvider = settlePhaseProvider,
        settleDirectionProvider = settleDirectionProvider,
    )

    Box(
        modifier = modifier
            .graphicsLayer { clip = false }
            .fillMaxWidth()
            .height(CardHeight * VisibleStackSlots + CardListGap * (VisibleStackSlots - 1)),
    ) {
        visibleCardRange.forEach { index ->
            val planet = planets.getOrNull(index) ?: return@forEach
            StackedPlanetCard(
                index = index,
                planet = planet,
                context = renderContext,
            )
        }
    }
}

private class PlanetStackRenderContext(
    val lastIndex: Int,
    val count: Int,
    val boostedIndex: Int,
    val stepProgressProvider: () -> Float,
    val activeCardIndexProvider: () -> Int,
    val settlePhaseProvider: () -> Float,
    val settleDirectionProvider: () -> Float,
)

@Composable
private fun BoxScope.StackedPlanetCard(
    index: Int,
    planet: PlanetCardModel,
    context: PlanetStackRenderContext,
) {
    val density = LocalDensity.current
    key(planet.id) {
        PlanetInfoCard(
            model = planet,
            frameProvider = {
                stackedCardFrame(
                    index = index,
                    stepProgressProvider = context.stepProgressProvider,
                    lastIndex = context.lastIndex,
                )
            },
            modifier = Modifier
                .zIndex(
                    stackZIndex(
                        index = index,
                        boostedIndex = context.boostedIndex,
                    ),
                )
                .align(Alignment.TopStart)
                .graphicsLayer {
                    applyStackedCardMotion(
                        index = index,
                        context = context,
                        density = density,
                    )
                },
        )
    }
}

private fun androidx.compose.ui.graphics.GraphicsLayerScope.applyStackedCardMotion(
    index: Int,
    context: PlanetStackRenderContext,
    density: Density,
) {
    clip = false
    val stepProgress = context.stepProgressProvider()
    val activeIndex = context.activeCardIndexProvider()
    val settleTranslationY = cardSettleTranslationY(
        index = index,
        activeIndex = activeIndex,
        phase = context.settlePhaseProvider(),
        direction = context.settleDirectionProvider(),
    )
    translationY = with(density) {
        stackedCardFrame(
            index = index,
            stepProgressProvider = context.stepProgressProvider,
            lastIndex = context.lastIndex,
        ).offsetY.toPx() + settleTranslationY.toPx()
    }
    val scale = cardFloatingScale(
        index = index,
        activeIndex = activeIndex,
        stepProgress = stepProgress,
        cardCount = context.count,
    )
    scaleX = scale
    scaleY = scale
}