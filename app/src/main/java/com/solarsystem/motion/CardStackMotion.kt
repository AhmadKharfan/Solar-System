package com.solarsystem.motion

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.solarsystem.model.CardFrame
import com.solarsystem.ui.constants.CardHeight
import com.solarsystem.ui.constants.CardListGap
import com.solarsystem.ui.constants.MaxStackedCardsBeforeActive
import com.solarsystem.ui.constants.PeekPlanetAlpha
import com.solarsystem.ui.constants.PlanetImageOffsetY
import com.solarsystem.ui.constants.VisibleCardsAfterActive
import com.solarsystem.ui.constants.VisibleStackSlots
import com.solarsystem.ui.theme.SolarColors
import com.solarsystem.util.interpolate
import com.solarsystem.util.interpolateColor
import com.solarsystem.util.smoothStep
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToInt

fun interpolateCardFrame(
    index: Int,
    stepProgress: Float,
    lastIndex: Int,
): CardFrame {
    val clampedStep = stepProgress.coerceIn(0f, lastIndex.coerceAtLeast(0).toFloat())
    val fromStep = floor(clampedStep).toInt()
    val toStep = (fromStep + 1).coerceAtMost(lastIndex.coerceAtLeast(0))
    val segment = clampedStep - fromStep
    val start = cardFrameAtStep(index, fromStep, lastIndex)
    val end = cardFrameAtStep(index, toStep, lastIndex)
    return CardFrame(
        offsetY = interpolate(start.offsetY, end.offsetY, segment),
        cardAlpha = interpolate(start.cardAlpha, end.cardAlpha, segment),
        backgroundColor = interpolateColor(start.backgroundColor, end.backgroundColor, segment),
        planetAlpha = interpolate(start.planetAlpha, end.planetAlpha, segment),
        planetOffsetY = interpolate(start.planetOffsetY, end.planetOffsetY, segment),
        titleAlpha = interpolate(start.titleAlpha, end.titleAlpha, segment),
        taglineAlpha = interpolate(start.taglineAlpha, end.taglineAlpha, segment),
        statsAlpha = interpolate(start.statsAlpha, end.statsAlpha, segment),
        elevatedTitleAlpha = interpolate(start.elevatedTitleAlpha, end.elevatedTitleAlpha, segment),
    )
}

private fun cardFrameAtStep(
    index: Int,
    step: Int,
    lastIndex: Int,
): CardFrame {
    val activeStep = step.coerceIn(0, lastIndex.coerceAtLeast(0))
    val activeSlot = activeStep.coerceAtMost(StackStepFrames.lastIndex)
    val stackStartIndex = stackStartIndex(activeStep)
    val activeRelativeSlot = activeStep - stackStartIndex
    val slot = index - stackStartIndex
    return when {
        activeStep <= StackStepFrames.lastIndex && index in StackStepFrames.indices ->
            StackStepFrames[activeSlot][index]
        slot < 0 -> hiddenStackCardFrame(slot)
        slot < activeRelativeSlot -> stackedOverflowCardFrame(slot)
        slot == activeRelativeSlot -> keyframe(overflowStackOffset(slot), peekSolidFront())
        else -> futureCardFrame(
            slot = slot,
            activeSlot = activeRelativeSlot,
        )
    }
}

private fun stackedOverflowCardFrame(slot: Int): CardFrame =
    keyframe(
        offsetY = overflowStackOffset(slot),
        frame = if (slot < StackStepFrames.lastIndex) {
            StackStepFrames.last()[slot]
        } else {
            CardFrame(
                backgroundColor = SolarColors.CardBackgroundSolid,
                planetAlpha = PeekPlanetAlpha,
            )
        },
    )

private fun hiddenStackCardFrame(slot: Int): CardFrame =
    keyframe(
        offsetY = overflowStackOffset(slot),
        frame = CardFrame(
            cardAlpha = 0f,
            backgroundColor = SolarColors.CardBackgroundSolid,
            planetAlpha = PeekPlanetAlpha,
            titleAlpha = 0f,
            taglineAlpha = 0f,
            statsAlpha = 0f,
            elevatedTitleAlpha = 0f,
        ),
    )

private fun futureCardFrame(
    slot: Int,
    activeSlot: Int,
): CardFrame {
    val activeOffset = overflowStackOffset(activeSlot)
    val distanceFromActive = slot - activeSlot
    return keyframe(activeOffset + (CardHeight + CardListGap) * distanceFromActive)
}

private fun overflowStackOffset(slot: Int): Dp =
    14.dp * slot

private fun stackStartIndex(activeStep: Int): Int =
    (activeStep - MaxStackedCardsBeforeActive).coerceAtLeast(0)

fun visibleCardRange(
    stepProgress: Float,
    lastIndex: Int,
): IntRange {
    if (lastIndex < 0) return IntRange.EMPTY

    val activeIndex = floor(stepProgress.coerceAtLeast(0f)).toInt().coerceAtMost(lastIndex)
    val startIndex = maxOf(0, activeIndex - MaxStackedCardsBeforeActive)
    val endIndex = minOf(lastIndex, activeIndex + VisibleCardsAfterActive)
    return startIndex..endIndex
}

private fun defaultStackLayers(): List<CardFrame> {
    val pitch = CardHeight + CardListGap
    return List(VisibleStackSlots) { index -> keyframe(pitch * index) }
}

private fun keyframe(offsetY: Dp, frame: CardFrame = CardFrame()) =
    frame.copy(offsetY = offsetY)

private fun stackStep(vararg stackedFrames: CardFrame): List<CardFrame> {
    val normalPitch = CardHeight + CardListGap
    val frontIndex = stackedFrames.lastIndex
    val frontOffset = stackedFrames.last().offsetY
    return stackedFrames.toList() + List(VisibleStackSlots - stackedFrames.size) { tailIndex ->
        val index = stackedFrames.size + tailIndex
        keyframe(frontOffset + normalPitch * (index - frontIndex))
    }
}

private val StackStepFrames: List<List<CardFrame>> = listOf(
    defaultStackLayers(),
    stackStep(
        keyframe(0.dp, peekSolidElevatedTitle()),
        keyframe(14.dp, peekSolidFront()),
    ),
    stackStep(
        keyframe(0.dp, peekSolidTagline()),
        keyframe(14.dp, peekSolidStatsOnly().copy(elevatedTitleAlpha = 1f)),
        keyframe(28.dp, peekSolidFront()),
    ),
    stackStep(
        keyframe(0.dp, peekSolidTagline()),
        keyframe(14.dp, peekSolidStatsOnly().copy(elevatedTitleAlpha = 1f)),
        keyframe(28.dp, peekSolidTagline().copy(elevatedTitleAlpha = 1f)),
        keyframe(42.dp, peekSolidFront()),
    ),
    stackStep(
        keyframe(0.dp, peekSolidTagline()),
        keyframe(14.dp, peekSolidStatsOnly()),
        keyframe(28.dp, peekSolidTagline()),
        keyframe(42.dp, peekSolidTagline().copy(planetOffsetY = (-15.5).dp)),
        keyframe(56.dp, peekSolidFront()),
    ),
    stackStep(
        keyframe(0.dp, peekSolidTagline()),
        keyframe(14.dp, peekSolidStatsOnly()),
        keyframe(28.dp, peekSolidTagline()),
        keyframe(42.dp, peekSolidTagline().copy(planetOffsetY = (-15.5).dp)),
        keyframe(56.dp, peekSolidElevatedTitle()),
        keyframe(70.dp, peekSolidFront()),
    ),
    stackStep(
        keyframe(0.dp, peekSolidTagline()),
        keyframe(14.dp, peekSolidStatsOnly()),
        keyframe(28.dp, peekSolidTagline()),
        keyframe(42.dp, peekSolidTagline().copy(planetOffsetY = (-15.5).dp)),
        keyframe(56.dp, peekSolidElevatedTitle()),
        keyframe(70.dp, peekSolidPlanetOnly()),
        keyframe(84.dp, peekSolidFront()),
    ),
)

private fun peekSolidTagline() = CardFrame(
    backgroundColor = SolarColors.CardBackgroundSolid,
    planetAlpha = PeekPlanetAlpha,
    titleAlpha = 0f,
)

private fun peekSolidStatsOnly() = CardFrame(
    backgroundColor = SolarColors.CardBackgroundSolid,
    planetAlpha = PeekPlanetAlpha,
    titleAlpha = 0f,
    taglineAlpha = 0f,
)

private fun peekSolidElevatedTitle() = CardFrame(
    backgroundColor = SolarColors.CardBackgroundSolid,
    planetAlpha = PeekPlanetAlpha,
    elevatedTitleAlpha = 1f,
)

private fun peekSolidPlanetOnly() = CardFrame(
    backgroundColor = SolarColors.CardBackgroundSolid,
    planetAlpha = PeekPlanetAlpha,
)

private fun peekSolidFront() = CardFrame(backgroundColor = SolarColors.CardBackgroundSolid)

fun Density.isCardBodyHit(
    position: androidx.compose.ui.geometry.Offset,
    cardWidthPx: Float,
    stepProgress: Float,
    lastIndex: Int,
): Boolean {
    if (position.x !in 0f..cardWidthPx) return false

    val topPadding = com.solarsystem.ui.constants.CardStackTopPadding.toPx()
    val cardHeight = CardHeight.toPx()
    return visibleCardRange(
        stepProgress = stepProgress,
        lastIndex = lastIndex,
    ).any { index ->
        val top = topPadding + interpolateCardFrame(
            index = index,
            stepProgress = stepProgress,
            lastIndex = lastIndex,
        ).offsetY.toPx()
        position.y in top..(top + cardHeight)
    }
}

fun stackZIndex(
    index: Int,
    boostedIndex: Int,
): Float {
    val lastCardBoost = if (index == boostedIndex) 100f else 0f
    return index.toFloat() + lastCardBoost
}

fun cardSettleTranslationY(
    index: Int,
    activeIndex: Int,
    phase: Float,
    direction: Float,
): Dp {
    if (direction == 0f) return 0.dp

    val indexDistance = abs(index - activeIndex)
    val weight = when (indexDistance) {
        0 -> 1f
        1 -> 0.42f
        2 -> 0.18f
        else -> 0f
    }
    if (weight == 0f) return 0.dp

    val delayedPhase = delayedCardSettlePhase(
        phase = phase,
        indexDistance = indexDistance,
    )
    val followThrough = settleFollowThrough(delayedPhase)
    return 5.dp * direction * weight * followThrough
}

fun cardFloatingScale(
    index: Int,
    activeIndex: Int,
    stepProgress: Float,
    cardCount: Int,
): Float {
    if (cardCount <= 1) return 1f

    val clampedStep = stepProgress.coerceIn(0f, (cardCount - 1).toFloat())
    val travelAmount = 1f - (abs(clampedStep - clampedStep.roundToInt()) * 2f).coerceIn(0f, 1f)
    if (travelAmount == 0f) return 1f

    val activeDistance = abs(index - activeIndex)
    val scaleAmount = when (activeDistance) {
        0 -> 0.004f
        1 -> 0.0017f
        else -> 0f
    }
    return 1f + scaleAmount * travelAmount
}

private fun delayedCardSettlePhase(
    phase: Float,
    indexDistance: Int,
): Float {
    val delay = indexDistance * 0.045f
    return ((phase.coerceIn(0f, 1f) - delay) / (1f - delay)).coerceIn(0f, 1f)
}

private fun settleFollowThrough(phase: Float): Float = when {
    phase < 0.48f -> 0f
    phase < 0.76f -> smoothStep((phase - 0.48f) / 0.28f)
    else -> 1f - smoothStep((phase - 0.76f) / 0.24f)
}

fun stackedCardFrame(
    index: Int,
    stepProgressProvider: () -> Float,
    lastIndex: Int,
): CardFrame =
    interpolateCardFrame(
        index = index,
        stepProgress = stepProgressProvider(),
        lastIndex = lastIndex,
    )