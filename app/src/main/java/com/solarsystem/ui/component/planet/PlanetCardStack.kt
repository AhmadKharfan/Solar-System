package com.solarsystem.ui.component.planet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.solarsystem.data.PlanetCatalog
import com.solarsystem.model.PlanetCardModel
import com.solarsystem.ui.preview.SolarPreviewSurface
import com.solarsystem.ui.tokens.PlanetCardDimens

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
    Box(
        modifier = modifier
            .graphicsLayer { clip = false }
            .width(PlanetCardDimens.Width)
            .height(variant.containerHeight()),
    ) {
        planets.forEachIndexed { index, planet ->
            val layer = layers.getOrElse(index) { layers.last() }
            PlanetInfoCard(
                model = planet,
                layerStyle = layer.style,
                modifier = Modifier
                    .zIndex(index.toFloat())
                    .align(Alignment.TopStart)
                    .offset(y = layer.offsetY),
            )
        }
    }
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
