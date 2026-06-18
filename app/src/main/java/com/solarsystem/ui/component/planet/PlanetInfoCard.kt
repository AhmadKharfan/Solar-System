package com.solarsystem.ui.component.planet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.solarsystem.data.PlanetCatalog
import com.solarsystem.model.PlanetCardLayerStyle
import com.solarsystem.model.PlanetCardModel
import com.solarsystem.ui.preview.SolarPreviewSurface
import com.solarsystem.ui.theme.SolarColors
import com.solarsystem.ui.theme.SolarTypography
import com.solarsystem.ui.tokens.PlanetCardDimens

@Composable
fun PlanetInfoCard(
    model: PlanetCardModel,
    modifier: Modifier = Modifier,
    layerStyle: PlanetCardLayerStyle = PlanetCardLayerStyle(),
) {
    Box(
        modifier = modifier
            .graphicsLayer { clip = false }
            .size(PlanetCardDimens.Width, PlanetCardDimens.Height),
    ) {
        PlanetCardBackground(layerStyle = layerStyle)

        PlanetImage(
            model = model,
            alpha = layerStyle.planetAlpha,
            modifier = Modifier
                .zIndex(1f)
                .align(Alignment.TopStart)
                .offset(x = PlanetCardDimens.PlanetOffsetX, y = layerStyle.planetOffsetY),
        )

        PlanetCardHeader(
            model = model,
            layerStyle = layerStyle,
        )

        if (layerStyle.showStats) {
            PlanetStatsGrid(
                model = model,
                modifier = Modifier.zIndex(2f),
            )
        }

        if (layerStyle.elevateTitle) {
            PlanetCardTitle(
                text = model.name,
                modifier = Modifier.zIndex(3f),
            )
        }
    }
}

@Composable
private fun BoxScope.PlanetCardBackground(layerStyle: PlanetCardLayerStyle) {
    Box(
        modifier = Modifier
            .matchParentSize()
            .clip(RoundedCornerShape(PlanetCardDimens.CornerRadius))
            .background(layerStyle.backgroundColor)
            .border(
                width = PlanetCardDimens.BorderWidth,
                color = SolarColors.CardBorder,
                shape = RoundedCornerShape(PlanetCardDimens.CornerRadius),
            ),
    )
}

@Composable
private fun PlanetCardHeader(
    model: PlanetCardModel,
    layerStyle: PlanetCardLayerStyle,
) {
    if (layerStyle.showTitle && !layerStyle.taglineOnlyHeader) {
        PlanetCardTitle(
            text = model.name,
            modifier = Modifier.zIndex(2f),
        )
    }
    if (layerStyle.showTagline) {
        Text(
            text = model.tagline,
            style = SolarTypography.CardSubtitle,
            modifier = Modifier
                .zIndex(2f)
                .offset(x = PlanetCardDimens.HeaderX, y = PlanetCardDimens.SubtitleY)
                .width(PlanetCardDimens.HeaderWidth),
        )
    }
}

@Composable
private fun PlanetCardTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = SolarTypography.CardTitle,
        modifier = modifier
            .offset(x = PlanetCardDimens.HeaderX, y = PlanetCardDimens.TitleY)
            .width(PlanetCardDimens.HeaderWidth),
    )
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
