package com.solarsystem.gesture

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.solarsystem.motion.OvershootSpringSpec
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs

fun Modifier.screenMotionDrag(
    cardsAtFirstStepProvider: () -> Boolean,
    density: Density,
    scope: CoroutineScope,
    anchoredProgress: Animatable<Float, AnimationVector1D>,
): Modifier = pointerInput(density) {
    val dragRangePx = with(density) { 770.dp.toPx() }.coerceAtLeast(1f)
    val swipeVelocityThresholdPx = with(density) { 850.dp.toPx() }
    val cardSectionTopPx = with(density) { 330.dp.toPx() }

    awaitEachGesture {
        handleScreenMotionGesture(
            cardsAtFirstStepProvider = cardsAtFirstStepProvider,
            dragRangePx = dragRangePx,
            swipeVelocityThresholdPx = swipeVelocityThresholdPx,
            cardSectionTopPx = cardSectionTopPx,
            scope = scope,
            anchoredProgress = anchoredProgress,
        )
    }
}

private suspend fun AwaitPointerEventScope.handleScreenMotionGesture(
    cardsAtFirstStepProvider: () -> Boolean,
    dragRangePx: Float,
    swipeVelocityThresholdPx: Float,
    cardSectionTopPx: Float,
    scope: CoroutineScope,
    anchoredProgress: Animatable<Float, AnimationVector1D>,
) {
    val down = awaitFirstDown(requireUnconsumed = false)
    val velocityTracker = VelocityTracker()
    velocityTracker.addPosition(down.uptimeMillis, down.position)

    val dragResult = trackScreenMotionDrag(
        down = down,
        velocityTracker = velocityTracker,
        cardsAtFirstStepProvider = cardsAtFirstStepProvider,
        dragRangePx = dragRangePx,
        cardSectionTopPx = cardSectionTopPx,
        scope = scope,
        anchoredProgress = anchoredProgress,
    ) ?: return

    settleScreenMotion(
        latestProgress = dragResult.latestProgress,
        velocityY = dragResult.velocityY,
        velocityThresholdPx = swipeVelocityThresholdPx,
        scope = scope,
        anchoredProgress = anchoredProgress,
    )
}

private data class ScreenMotionDragResult(
    val latestProgress: Float,
    val velocityY: Float,
)

private suspend fun AwaitPointerEventScope.trackScreenMotionDrag(
    down: PointerInputChange,
    velocityTracker: VelocityTracker,
    cardsAtFirstStepProvider: () -> Boolean,
    dragRangePx: Float,
    cardSectionTopPx: Float,
    scope: CoroutineScope,
    anchoredProgress: Animatable<Float, AnimationVector1D>,
): ScreenMotionDragResult? {
    val dragState = ScreenMotionDragState(
        gestureStartProgress = anchoredProgress.value.coerceIn(0f, 1f),
        startedInCardSection = down.position.y >= cardSectionTopPx,
    )

    while (true) {
        val change = awaitTrackedPointerChange(down) ?: break
        updateScreenMotionDrag(
            state = dragState,
            change = change,
            velocityTracker = velocityTracker,
            cardsAtFirstStep = cardsAtFirstStepProvider(),
            dragRangePx = dragRangePx,
            scope = scope,
            anchoredProgress = anchoredProgress,
        )
    }

    return finishScreenMotionDrag(
        state = dragState,
        velocityTracker = velocityTracker,
    )
}

private class ScreenMotionDragState(
    val gestureStartProgress: Float,
    val startedInCardSection: Boolean,
) {
    var latestProgress: Float = gestureStartProgress
    var totalDragY: Float = 0f
    var isDragging: Boolean = false
    var snapJob: Job? = null
}

private fun AwaitPointerEventScope.updateScreenMotionDrag(
    state: ScreenMotionDragState,
    change: PointerInputChange,
    velocityTracker: VelocityTracker,
    cardsAtFirstStep: Boolean,
    dragRangePx: Float,
    scope: CoroutineScope,
    anchoredProgress: Animatable<Float, AnimationVector1D>,
) {
    state.totalDragY += change.positionChange().y
    velocityTracker.addPosition(change.uptimeMillis, change.position)
    state.startIfAllowed(
        touchSlop = viewConfiguration.touchSlop,
        cardsAtFirstStep = cardsAtFirstStep,
    )
    if (state.isDragging) {
        snapScreenProgress(
            state = state,
            change = change,
            dragRangePx = dragRangePx,
            scope = scope,
            anchoredProgress = anchoredProgress,
        )
    }
}

private fun ScreenMotionDragState.startIfAllowed(
    touchSlop: Float,
    cardsAtFirstStep: Boolean,
) {
    if (!isDragging && canStartScreenMotionDrag(
            totalDragY = totalDragY,
            touchSlop = touchSlop,
            gestureStartProgress = gestureStartProgress,
            startedInCardSection = startedInCardSection,
            cardsAtFirstStep = cardsAtFirstStep,
        )
    ) {
        isDragging = true
    }
}

private fun snapScreenProgress(
    state: ScreenMotionDragState,
    change: PointerInputChange,
    dragRangePx: Float,
    scope: CoroutineScope,
    anchoredProgress: Animatable<Float, AnimationVector1D>,
) {
    change.consume()
    state.latestProgress = draggedScreenProgress(
        gestureStartProgress = state.gestureStartProgress,
        totalDragY = state.totalDragY,
        dragRangePx = dragRangePx,
    )
    state.snapJob?.cancel()
    state.snapJob = scope.launch {
        anchoredProgress.snapTo(state.latestProgress)
    }
}

private fun finishScreenMotionDrag(
    state: ScreenMotionDragState,
    velocityTracker: VelocityTracker,
): ScreenMotionDragResult? {
    state.snapJob?.cancel()
    return if (state.isDragging) {
        ScreenMotionDragResult(
            latestProgress = state.latestProgress,
            velocityY = velocityTracker.calculateVelocity().y,
        )
    } else {
        null
    }
}

private suspend fun AwaitPointerEventScope.awaitTrackedPointerChange(
    down: PointerInputChange,
): PointerInputChange? {
    val event = awaitPointerEvent(PointerEventPass.Initial)
    val change = event.changes.firstOrNull { it.id == down.id } ?: return null
    return change.takeIf { it.pressed }
}

private fun canStartScreenMotionDrag(
    totalDragY: Float,
    touchSlop: Float,
    gestureStartProgress: Float,
    startedInCardSection: Boolean,
    cardsAtFirstStep: Boolean,
): Boolean =
    abs(totalDragY) >= touchSlop && canStartScreenMotion(
        totalDragY = totalDragY,
        gestureStartProgress = gestureStartProgress,
        startedInCardSection = startedInCardSection,
        cardsAtFirstStep = cardsAtFirstStep,
    )

private fun draggedScreenProgress(
    gestureStartProgress: Float,
    totalDragY: Float,
    dragRangePx: Float,
): Float =
    (gestureStartProgress - totalDragY / dragRangePx).coerceIn(0f, 1f)

private fun settleScreenMotion(
    latestProgress: Float,
    velocityY: Float,
    velocityThresholdPx: Float,
    scope: CoroutineScope,
    anchoredProgress: Animatable<Float, AnimationVector1D>,
) {
    val target = targetScreenAnchor(
        velocityY = velocityY,
        velocityThresholdPx = velocityThresholdPx,
        latestProgress = latestProgress,
    )
    scope.launch {
        anchoredProgress.snapTo(latestProgress)
        anchoredProgress.animateTo(
            targetValue = target,
            animationSpec = OvershootSpringSpec,
        )
    }
}

private fun canStartScreenMotion(
    totalDragY: Float,
    gestureStartProgress: Float,
    startedInCardSection: Boolean,
    cardsAtFirstStep: Boolean,
): Boolean {
    val isDraggingTowardCards = totalDragY < 0f
    val isDraggingTowardHero = totalDragY > 0f
    val canMoveProgress = when {
        isDraggingTowardCards -> gestureStartProgress < 1f
        isDraggingTowardHero -> gestureStartProgress > 0f
        else -> false
    }
    val startedInSettledCards = startedInCardSection && gestureStartProgress >= 0.95f
    return if (startedInSettledCards) {
        cardsAtFirstStep && isDraggingTowardHero
    } else {
        canMoveProgress
    }
}

private fun targetScreenAnchor(
    velocityY: Float,
    velocityThresholdPx: Float,
    latestProgress: Float,
) = when {
    velocityY <= -velocityThresholdPx -> 1f
    velocityY >= velocityThresholdPx -> 0f
    latestProgress >= 0.5f -> 1f
    else -> 0f
}