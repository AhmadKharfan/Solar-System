package com.solarsystem.ui.motion

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.solarsystem.data.PlanetCatalog
import com.solarsystem.ui.tokens.PlanetCardDimens
import com.solarsystem.ui.tokens.ScreenDimens

data class SolarScrollMetrics(
    val heroProgress: Float,
    val cardsScreenTop: Dp,
    val stackProgress: Float,
    val maxScrollPx: Float,
)

@Composable
fun rememberSolarScrollMetrics(scrollState: ScrollState): State<SolarScrollMetrics> {
    val density = LocalDensity.current
    val heroRangePx = remember(density) {
        with(density) { ScreenDimens.HeroScrollRange.toPx() }
    }
    val cardPitchPx = remember(density) {
        with(density) { (PlanetCardDimens.Height + PlanetCardDimens.ListGap).toPx() }
    }
    val stackSegmentCount = PlanetCatalog.all.size - 1
    val targetBrowsePx = remember(cardPitchPx, stackSegmentCount) {
        cardPitchPx * stackSegmentCount
    }
    val targetMaxScrollPx = remember(heroRangePx, targetBrowsePx) {
        heroRangePx + targetBrowsePx
    }

    return remember(scrollState, density, heroRangePx, targetBrowsePx, targetMaxScrollPx) {
        derivedStateOf {
            val scrollPx = scrollState.value.toFloat()
            val scrollMaxPx = scrollState.maxValue.toFloat()
            val reachableBrowsePx = if (scrollMaxPx > heroRangePx) {
                scrollMaxPx - heroRangePx
            } else {
                targetBrowsePx
            }

            val totalPx = scrollPx.coerceIn(0f, maxOf(targetMaxScrollPx, scrollMaxPx))
            val heroScrollPx = totalPx.coerceAtMost(heroRangePx)
            val cardsBrowsePx = (totalPx - heroRangePx).coerceIn(0f, reachableBrowsePx)
            val heroProgress = (heroScrollPx / heroRangePx).coerceIn(0f, 1f)
            val browseProgress = if (reachableBrowsePx > 0f) {
                (cardsBrowsePx / reachableBrowsePx).coerceIn(0f, 1f)
            } else {
                0f
            }
            val stackProgress = if (heroProgress < 1f) {
                heroProgress
            } else {
                browseProgressToStackProgress(browseProgress)
            }
            val cardsTop = lerp(
                ScreenDimens.CardsStartTop,
                ScreenDimens.CardsEndTop,
                heroProgress,
            )
            SolarScrollMetrics(
                heroProgress = heroProgress,
                cardsScreenTop = cardsTop,
                stackProgress = stackProgress,
                maxScrollPx = maxOf(targetMaxScrollPx, scrollMaxPx),
            )
        }
    }
}

fun lerp(start: Float, end: Float, fraction: Float): Float =
    start + (end - start) * fraction

fun lerp(start: Dp, end: Dp, fraction: Float): Dp =
    start + (end - start) * fraction

fun lerpColor(start: androidx.compose.ui.graphics.Color, end: androidx.compose.ui.graphics.Color, fraction: Float): androidx.compose.ui.graphics.Color {
    val f = fraction.coerceIn(0f, 1f)
    return androidx.compose.ui.graphics.Color(
        alpha = lerp(start.alpha, end.alpha, f),
        red = lerp(start.red, end.red, f),
        green = lerp(start.green, end.green, f),
        blue = lerp(start.blue, end.blue, f),
    )
}
