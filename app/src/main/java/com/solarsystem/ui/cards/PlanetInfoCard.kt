package com.solarsystem.ui.cards

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.solarsystem.model.CardFrame
import com.solarsystem.model.PlanetCardModel
import com.solarsystem.model.PlanetStat
import com.solarsystem.ui.constants.CardBorderWidth
import com.solarsystem.ui.constants.CardHeaderWidth
import com.solarsystem.ui.constants.CardHeight
import com.solarsystem.ui.constants.CardTitleY
import com.solarsystem.ui.constants.PlanetImageOffsetY
import com.solarsystem.ui.constants.PlanetImageWidth
import com.solarsystem.ui.constants.TextLayerBleed
import com.solarsystem.ui.theme.SolarColors
import com.solarsystem.ui.theme.SolarTypography
import com.solarsystem.util.cardBackground
import com.solarsystem.util.planetShadow

@Composable
fun PlanetInfoCard(
    model: PlanetCardModel,
    modifier: Modifier = Modifier,
    frameProvider: () -> CardFrame,
) {
    Box(
        modifier = modifier
            .graphicsLayer { clip = false }
            .fillMaxWidth()
            .height(CardHeight),
    ) {
        PlanetCardBackground(frameProvider = frameProvider)
        PlanetCardPlanetImage(
            model = model,
            frameProvider = frameProvider,
        )
        PlanetCardHeader(
            model = model,
            frameProvider = frameProvider,
        )
        PlanetCardStats(
            model = model,
            frameProvider = frameProvider,
        )
        PlanetCardElevatedTitle(
            text = model.name,
            frameProvider = frameProvider,
        )
    }
}

@Composable
private fun BoxScope.PlanetCardBackground(frameProvider: () -> CardFrame) {
    Box(
        modifier = Modifier
            .matchParentSize()
            .cardBackground(frameProvider = frameProvider),
    )
}

@Composable
private fun BoxScope.PlanetCardPlanetImage(
    model: PlanetCardModel,
    frameProvider: () -> CardFrame,
) {
    val density = LocalDensity.current
    PlanetImage(
        model = model,
        alphaProvider = {
            val frame = frameProvider()
            frame.planetAlpha * frame.cardAlpha
        },
        translationYProvider = {
            with(density) {
                (frameProvider().planetOffsetY - PlanetImageOffsetY).toPx()
            }
        },
        modifier = Modifier
            .zIndex(1f)
            .align(Alignment.TopStart)
            .offset(
                x = 15.5.dp,
                y = PlanetImageOffsetY,
            ),
    )
}

@Composable
private fun PlanetCardStats(
    model: PlanetCardModel,
    frameProvider: () -> CardFrame,
) {
    PlanetStatsGrid(
        model = model,
        modifier = Modifier
            .zIndex(2f)
            .graphicsLayer {
                val frame = frameProvider()
                alpha = frame.statsAlpha * frame.cardAlpha
            },
    )
}

@Composable
private fun PlanetCardElevatedTitle(
    text: String,
    frameProvider: () -> CardFrame,
) {
    PlanetCardTitle(
        text = text,
        alphaProvider = {
            val frame = frameProvider()
            frame.elevatedTitleAlpha * frame.cardAlpha
        },
        modifier = Modifier.zIndex(3f),
    )
}

@Composable
private fun PlanetImage(
    model: PlanetCardModel,
    modifier: Modifier = Modifier,
    alphaProvider: (() -> Float)? = null,
    translationYProvider: (() -> Float)? = null,
) {
    Box(
        modifier = modifier
            .size(PlanetImageWidth, model.imageHeight)
            .graphicsLayer {
                clip = false
                translationYProvider?.let { translationY = it() }
            }
            .planetShadow(
                glowColor = model.glowColor,
                planetWidth = PlanetImageWidth,
                planetHeight = model.imageHeight,
                alphaProvider = alphaProvider ?: { 1f },
            ),
    ) {
        Image(
            painter = painterResource(model.imageRes),
            contentDescription = model.name,
            modifier = Modifier
                .graphicsLayer {
                    clip = false
                    alpha = alphaProvider?.invoke() ?: 1f
                }
                .size(PlanetImageWidth, model.imageHeight),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun PlanetCardHeader(
    model: PlanetCardModel,
    frameProvider: () -> CardFrame,
) {
    FadingTextLayer(
        text = model.name,
        style = SolarTypography.CardTitle,
        alphaProvider = {
            val frame = frameProvider()
            frame.titleAlpha * frame.cardAlpha
        },
        y = CardTitleY,
        modifier = Modifier.zIndex(2f),
    )
    FadingTextLayer(
        text = model.tagline,
        style = SolarTypography.CardSubtitle,
        alphaProvider = {
            val frame = frameProvider()
            frame.taglineAlpha * frame.cardAlpha
        },
        y = 40.5.dp,
        modifier = Modifier.zIndex(2f),
    )
}

@Composable
private fun PlanetCardTitle(
    text: String,
    modifier: Modifier = Modifier,
    alphaProvider: () -> Float = { 1f },
) {
    FadingTextLayer(
        text = text,
        style = SolarTypography.CardTitle,
        alphaProvider = alphaProvider,
        y = CardTitleY,
        modifier = modifier,
    )
}

@Composable
private fun FadingTextLayer(
    text: String,
    style: androidx.compose.ui.text.TextStyle,
    alphaProvider: () -> Float,
    y: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .offset(
                x = 137.dp - TextLayerBleed,
                y = y - TextLayerBleed,
            )
            .width(CardHeaderWidth + TextLayerBleed * 2)
            .graphicsLayer {
                clip = false
                alpha = alphaProvider()
            }
            .padding(TextLayerBleed),
    ) {
        Text(
            text = text,
            style = style,
            modifier = Modifier.width(CardHeaderWidth),
        )
    }
}

@Composable
private fun PlanetStatsGrid(
    model: PlanetCardModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .offset(x = (-0.5).dp, y = 111.5.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        PlanetStatsRow(
            left = model.stats[0],
            right = model.stats[1],
        )
        CardDividerHorizontal()
        PlanetStatsRow(
            left = model.stats[2],
            right = model.stats[3],
        )
    }
}

@Composable
private fun PlanetStatsRow(
    left: PlanetStat,
    right: PlanetStat,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlanetStatCell(
            stat = left,
            modifier = Modifier.weight(1f),
        )
        CardDividerVertical()
        PlanetStatCell(
            stat = right,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun PlanetStatCell(
    stat: PlanetStat,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlanetStatIcon(iconRes = stat.iconRes)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(text = stat.label, style = SolarTypography.StatLabel)
            Text(
                text = stat.valueWithHint(),
                style = SolarTypography.StatValue,
            )
        }
    }
}

@Composable
private fun PlanetStatIcon(
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.size(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun CardDividerVertical() {
    Box(
        modifier = Modifier
            .width(CardBorderWidth)
            .height(30.dp)
            .background(SolarColors.CardBorder),
    )
}

@Composable
private fun CardDividerHorizontal() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(CardBorderWidth)
            .background(SolarColors.CardBorder),
    )
}

private fun PlanetStat.valueWithHint() = buildAnnotatedString {
    if (hint == null) {
        append(value)
        return@buildAnnotatedString
    }

    withStyle(SolarTypography.StatValue.asStatSpanStyle()) {
        append(value)
    }
    append(" ")
    withStyle(SolarTypography.StatHint.asStatSpanStyle()) {
        append(hint)
    }
}

private fun androidx.compose.ui.text.TextStyle.asStatSpanStyle() = SpanStyle(
    color = color,
    fontSize = fontSize,
    fontFamily = fontFamily,
    fontWeight = fontWeight,
    letterSpacing = letterSpacing,
)