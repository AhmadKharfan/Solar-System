package com.solarsystem.ui.component.planet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.solarsystem.data.PlanetCatalog
import com.solarsystem.model.PlanetCardLayerStyle
import com.solarsystem.model.PlanetCardModel
import com.solarsystem.ui.motion.PlanetCardVisualState
import com.solarsystem.ui.motion.toVisualState
import com.solarsystem.ui.preview.SolarPreviewSurface
import com.solarsystem.ui.theme.SolarColors
import com.solarsystem.ui.theme.SolarTypography
import com.solarsystem.ui.tokens.PlanetCardDimens

private val TextLayerBleed = 14.dp

@Composable
fun PlanetInfoCard(
    model: PlanetCardModel,
    modifier: Modifier = Modifier,
    layerStyle: PlanetCardLayerStyle = PlanetCardLayerStyle(),
    visualStateProvider: (() -> PlanetCardVisualState)? = null,
) {
    val staticVisualState = remember(layerStyle) { layerStyle.toVisualState() }
    val currentVisualState = visualStateProvider ?: { staticVisualState }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .graphicsLayer { clip = false }
            .size(PlanetCardDimens.Width, PlanetCardDimens.Height),
    ) {
        PlanetCardBackground(visualStateProvider = currentVisualState)

        PlanetImage(
            model = model,
            alphaProvider = { currentVisualState().planetAlpha },
            translationYProvider = {
                with(density) {
                    (
                        currentVisualState().planetOffsetY - PlanetCardDimens.PlanetOffsetY
                    ).toPx()
                }
            },
            modifier = Modifier
                .zIndex(1f)
                .align(Alignment.TopStart)
                .offset(
                    x = PlanetCardDimens.PlanetOffsetX,
                    y = PlanetCardDimens.PlanetOffsetY,
                ),
        )

        PlanetCardHeader(
            model = model,
            visualStateProvider = currentVisualState,
        )

        PlanetStatsGrid(
            model = model,
            modifier = Modifier
                .zIndex(2f)
                .graphicsLayer {
                    alpha = currentVisualState().statsAlpha
                },
        )

        PlanetCardTitle(
            text = model.name,
            alphaProvider = { currentVisualState().elevatedTitleAlpha },
            modifier = Modifier.zIndex(3f),
        )
    }
}

@Composable
private fun BoxScope.PlanetCardBackground(visualStateProvider: () -> PlanetCardVisualState) {
    Box(
        modifier = Modifier
            .matchParentSize()
            .drawBehind {
                val radius = PlanetCardDimens.CornerRadius.toPx()
                val strokeWidth = PlanetCardDimens.BorderWidth.toPx()
                drawRoundRect(
                    color = visualStateProvider().backgroundColor,
                    cornerRadius = CornerRadius(radius, radius),
                )
                drawRoundRect(
                    color = SolarColors.CardBorder,
                    cornerRadius = CornerRadius(radius, radius),
                    style = Stroke(width = strokeWidth),
                )
            },
    )
}

@Composable
private fun PlanetCardHeader(
    model: PlanetCardModel,
    visualStateProvider: () -> PlanetCardVisualState,
) {
    FadingTextLayer(
        text = model.name,
        style = SolarTypography.CardTitle,
        alphaProvider = { visualStateProvider().titleAlpha },
        y = PlanetCardDimens.TitleY,
        modifier = Modifier
            .zIndex(2f),
    )
    FadingTextLayer(
        text = model.tagline,
        style = SolarTypography.CardSubtitle,
        alphaProvider = { visualStateProvider().taglineAlpha },
        y = PlanetCardDimens.SubtitleY,
        modifier = Modifier
            .zIndex(2f),
    )
}

@Composable
private fun PlanetCardTitle(
    text: String,
    alphaProvider: () -> Float = { 1f },
    modifier: Modifier = Modifier,
) {
    FadingTextLayer(
        text = text,
        style = SolarTypography.CardTitle,
        alphaProvider = alphaProvider,
        y = PlanetCardDimens.TitleY,
        modifier = modifier,
    )
}

@Composable
private fun FadingTextLayer(
    text: String,
    style: TextStyle,
    alphaProvider: () -> Float,
    y: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .offset(
                x = PlanetCardDimens.HeaderX - TextLayerBleed,
                y = y - TextLayerBleed,
            )
            .width(PlanetCardDimens.HeaderWidth + TextLayerBleed * 2)
            .graphicsLayer {
                clip = false
                alpha = alphaProvider()
            }
            .padding(TextLayerBleed),
    ) {
        Text(
            text = text,
            style = style,
            modifier = Modifier.width(PlanetCardDimens.HeaderWidth),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1223, widthDp = 360)
@Composable
private fun PlanetInfoCardPreview() {
    SolarPreviewSurface(
        modifier = Modifier.padding(
            top = 24.dp + PlanetCardDimens.PlanetOverflowTop,
            start = 16.dp,
            end = 16.dp,
            bottom = 16.dp,
        ),
    ) {
        PlanetInfoCard(model = PlanetCatalog.saturn)
    }
}
