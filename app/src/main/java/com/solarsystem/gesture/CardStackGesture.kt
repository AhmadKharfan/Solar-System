package com.solarsystem.gesture

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.solarsystem.model.PlanetCardModel
import com.solarsystem.motion.isCardBodyHit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs

data class PlanetCardStackState(
    val maxStep: Int,
    val stepProgress: Animatable<Float, AnimationVector1D>,
    val cardSettlePhase: Animatable<Float, AnimationVector1D>,
    val settledStep: MutableIntState,
    val settleDirection: MutableFloatState,
    val snapAnimationSpec: AnimationSpec<Float>,
    val settleAnimationSpec: AnimationSpec<Float>,
)

@Composable
fun rememberPlanetCardStackState(
    planets: List<PlanetCardModel>,
): PlanetCardStackState {
    val maxStep = (planets.size - 1).coerceAtLeast(0)
    val stepProgress = remember { Animatable(0f) }
    val cardSettlePhase = remember { Animatable(1f) }
    val settledStep = remember { mutableIntStateOf(0) }
    val settleDirection = remember { mutableFloatStateOf(0f) }
    val snapAnimationSpec: AnimationSpec<Float> = remember {
        tween(
            durationMillis = 1020,
            easing = CubicBezierEasing(0.18f, 0.0f, 0.08f, 1.0f),
        )
    }
    val settleAnimationSpec: AnimationSpec<Float> = remember {
        tween(
            durationMillis = 1460,
            easing = LinearEasing,
        )
    }
    return remember(maxStep) {
        PlanetCardStackState(
            maxStep = maxStep,
            stepProgress = stepProgress,
            cardSettlePhase = cardSettlePhase,
            settledStep = settledStep,
            settleDirection = settleDirection,
            snapAnimationSpec = snapAnimationSpec,
            settleAnimationSpec = settleAnimationSpec,
        )
    }
}

fun Modifier.planetCardStackDrag(
    entranceStackProgressProvider: () -> Float,
    planets: List<PlanetCardModel>,
    density: Density,
    motionScope: CoroutineScope,
    stackState: PlanetCardStackState,
): Modifier = pointerInput(stackState.maxStep, density) {
    val gestureContext = CardStackGestureContext(
        entranceStackProgressProvider = entranceStackProgressProvider,
        planets = planets,
        density = density,
        motionScope = motionScope,
        stackState = stackState,
        thresholds = CardStackGestureThresholds(
            snapDistance = 260.dp.toPx(),
            distanceThreshold = 84.dp.toPx(),
            velocityThreshold = 850.dp.toPx(),
        ),
    )

    awaitEachGesture {
        handleCardStackGesture(gestureContext)
    }
}

private data class CardStackGestureThresholds(
    val snapDistance: Float,
    val distanceThreshold: Float,
    val velocityThreshold: Float,
)

private data class CardStackGestureContext(
    val entranceStackProgressProvider: () -> Float,
    val planets: List<PlanetCardModel>,
    val density: Density,
    val motionScope: CoroutineScope,
    val stackState: PlanetCardStackState,
    val thresholds: CardStackGestureThresholds,
)

private suspend fun AwaitPointerEventScope.handleCardStackGesture(
    context: CardStackGestureContext,
) {
    val gestureStart = awaitCardStackGestureStart(
        context = context,
    ) ?: return

    val dragResult = trackCardDrag(
        down = gestureStart.down,
        velocityTracker = gestureStart.velocityTracker,
        hitCardBody = gestureStart.hitCardBody,
        gestureStartStep = gestureStart.step,
        gestureStartProgress = gestureStart.progress,
        maxStep = context.stackState.maxStep,
        snapDistance = context.thresholds.snapDistance,
        motionScope = context.motionScope,
        stepProgress = context.stackState.stepProgress,
        settleDirection = context.stackState.settleDirection,
    ) ?: return

    settleCardStackGesture(
        gestureStartStep = gestureStart.step,
        dragResult = dragResult,
        context = context,
    )
}

private data class CardStackGestureStart(
    val down: PointerInputChange,
    val velocityTracker: VelocityTracker,
    val step: Int,
    val progress: Float,
    val hitCardBody: Boolean,
)

private suspend fun AwaitPointerEventScope.awaitCardStackGestureStart(
    context: CardStackGestureContext,
): CardStackGestureStart? {
    val down = awaitFirstDown(requireUnconsumed = false)
    if (context.entranceStackProgressProvider() < 0.95f) return null

    val velocityTracker = VelocityTracker()
    velocityTracker.addPosition(down.uptimeMillis, down.position)
    return CardStackGestureStart(
        down = down,
        velocityTracker = velocityTracker,
        step = context.stackState.settledStep.intValue,
        progress = context.stackState.stepProgress.value,
        hitCardBody = hitCardBody(
            down = down,
            context = context,
        ),
    )
}

private fun AwaitPointerEventScope.hitCardBody(
    down: PointerInputChange,
    context: CardStackGestureContext,
): Boolean =
    with(context.density) {
        isCardBodyHit(
            position = down.position,
            cardWidthPx = size.width.toFloat(),
            stepProgress = context.stackState.stepProgress.value,
            lastIndex = context.planets.lastIndex,
        )
    }

private fun settleCardStackGesture(
    gestureStartStep: Int,
    dragResult: CardDragResult,
    context: CardStackGestureContext,
) {
    val targetStep = targetCardStep(
        gestureStartStep = gestureStartStep,
        latestDraggedStep = dragResult.latestDraggedStep,
        velocityY = dragResult.velocityY,
        velocityThreshold = context.thresholds.velocityThreshold,
        distanceThreshold = context.thresholds.distanceThreshold,
        snapDistance = context.thresholds.snapDistance,
        maxStep = context.stackState.maxStep,
    )
    animateCardStackToStep(
        targetStep = targetStep,
        latestDraggedStep = dragResult.latestDraggedStep,
        motionScope = context.motionScope,
        stepProgress = context.stackState.stepProgress,
        cardSettlePhase = context.stackState.cardSettlePhase,
        settledStep = context.stackState.settledStep,
        settleDirection = context.stackState.settleDirection,
        snapAnimationSpec = context.stackState.snapAnimationSpec,
        settleAnimationSpec = context.stackState.settleAnimationSpec,
    )
}

private data class CardDragResult(
    val latestDraggedStep: Float,
    val velocityY: Float,
)

private suspend fun AwaitPointerEventScope.trackCardDrag(
    down: PointerInputChange,
    velocityTracker: VelocityTracker,
    hitCardBody: Boolean,
    gestureStartStep: Int,
    gestureStartProgress: Float,
    maxStep: Int,
    snapDistance: Float,
    motionScope: CoroutineScope,
    stepProgress: Animatable<Float, AnimationVector1D>,
    settleDirection: MutableFloatState,
): CardDragResult? {
    val dragState = CardDragState(
        hitCardBody = hitCardBody,
        gestureStartStep = gestureStartStep,
        gestureStartProgress = gestureStartProgress,
    )

    while (true) {
        val change = awaitCardDragChange(down) ?: break
        if (!updateCardDrag(
                state = dragState,
                change = change,
                velocityTracker = velocityTracker,
                maxStep = maxStep,
                snapDistance = snapDistance,
                motionScope = motionScope,
                stepProgress = stepProgress,
                settleDirection = settleDirection,
            )
        ) break
    }

    return finishCardDrag(
        state = dragState,
        velocityTracker = velocityTracker,
    )
}

private class CardDragState(
    val hitCardBody: Boolean,
    val gestureStartStep: Int,
    val gestureStartProgress: Float,
) {
    var totalDragY: Float = 0f
    var isDragging: Boolean = false
    var latestDraggedStep: Float = gestureStartProgress
    var snapJob: Job? = null
}

private suspend fun AwaitPointerEventScope.awaitCardDragChange(
    down: PointerInputChange,
): PointerInputChange? {
    val event = awaitPointerEvent()
    val change = event.changes.firstOrNull { it.id == down.id } ?: return null
    return change.takeIf { it.pressed }
}

private fun AwaitPointerEventScope.updateCardDrag(
    state: CardDragState,
    change: PointerInputChange,
    velocityTracker: VelocityTracker,
    maxStep: Int,
    snapDistance: Float,
    motionScope: CoroutineScope,
    stepProgress: Animatable<Float, AnimationVector1D>,
    settleDirection: MutableFloatState,
): Boolean {
    state.totalDragY += change.positionChange().y
    velocityTracker.addPosition(change.uptimeMillis, change.position)
    if (!state.isDragging && abs(state.totalDragY) >= viewConfiguration.touchSlop) {
        if (!state.canStart(maxStep)) {
            change.consume()
            return false
        }
        state.isDragging = true
    }

    if (state.isDragging) {
        snapCardDragProgress(
            state = state,
            change = change,
            maxStep = maxStep,
            snapDistance = snapDistance,
            motionScope = motionScope,
            stepProgress = stepProgress,
            settleDirection = settleDirection,
        )
    }
    return true
}

private fun CardDragState.canStart(maxStep: Int): Boolean =
    canStartCardDrag(
        totalDragY = totalDragY,
        hitCardBody = hitCardBody,
        gestureStartStep = gestureStartStep,
        maxStep = maxStep,
    )

private fun snapCardDragProgress(
    state: CardDragState,
    change: PointerInputChange,
    maxStep: Int,
    snapDistance: Float,
    motionScope: CoroutineScope,
    stepProgress: Animatable<Float, AnimationVector1D>,
    settleDirection: MutableFloatState,
) {
    change.consume()
    settleDirection.floatValue = 0f
    state.latestDraggedStep = draggedStep(
        gestureStartProgress = state.gestureStartProgress,
        totalDragY = state.totalDragY,
        snapDistance = snapDistance,
        maxStep = maxStep,
    )
    state.snapJob?.cancel()
    state.snapJob = motionScope.launch {
        stepProgress.snapTo(state.latestDraggedStep)
    }
}

private fun finishCardDrag(
    state: CardDragState,
    velocityTracker: VelocityTracker,
): CardDragResult? {
    state.snapJob?.cancel()
    return if (state.isDragging) {
        CardDragResult(
            latestDraggedStep = state.latestDraggedStep,
            velocityY = velocityTracker.calculateVelocity().y,
        )
    } else {
        null
    }
}

private fun canStartCardDrag(
    totalDragY: Float,
    hitCardBody: Boolean,
    gestureStartStep: Int,
    maxStep: Int,
): Boolean {
    val direction = dragStepDirection(totalDragY)
    return hitCardBody && (gestureStartStep + direction).coerceIn(0, maxStep) != gestureStartStep
}

private fun draggedStep(
    gestureStartProgress: Float,
    totalDragY: Float,
    snapDistance: Float,
    maxStep: Int,
): Float = (gestureStartProgress - totalDragY / snapDistance).coerceIn(0f, maxStep.toFloat())

private fun targetCardStep(
    gestureStartStep: Int,
    latestDraggedStep: Float,
    velocityY: Float,
    velocityThreshold: Float,
    distanceThreshold: Float,
    snapDistance: Float,
    maxStep: Int,
): Int {
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
    return (gestureStartStep + if (velocityStep != 0) velocityStep else thresholdStep)
        .coerceIn(0, maxStep)
}

private fun animateCardStackToStep(
    targetStep: Int,
    latestDraggedStep: Float,
    motionScope: CoroutineScope,
    stepProgress: Animatable<Float, AnimationVector1D>,
    cardSettlePhase: Animatable<Float, AnimationVector1D>,
    settledStep: MutableIntState,
    settleDirection: MutableFloatState,
    snapAnimationSpec: AnimationSpec<Float>,
    settleAnimationSpec: AnimationSpec<Float>,
) {
    settledStep.intValue = targetStep
    val targetStepFloat = targetStep.toFloat()
    settleDirection.floatValue = when {
        targetStepFloat > latestDraggedStep -> -1f
        targetStepFloat < latestDraggedStep -> 1f
        else -> 0f
    }
    motionScope.launch {
        stepProgress.snapTo(latestDraggedStep)
        cardSettlePhase.snapTo(0f)
        launch {
            stepProgress.animateTo(
                targetValue = targetStepFloat,
                animationSpec = snapAnimationSpec,
            )
        }
        launch {
            cardSettlePhase.animateTo(
                targetValue = 1f,
                animationSpec = settleAnimationSpec,
            )
        }
    }
}

private fun dragStepDirection(totalDragY: Float) = when {
    totalDragY < 0f -> 1
    totalDragY > 0f -> -1
    else -> 0
}