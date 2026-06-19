package com.solarsystem.ui.component.planet

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.solarsystem.data.PlanetCatalog
import com.solarsystem.model.PlanetCardModel
import com.solarsystem.ui.modifier.planetShadow
import com.solarsystem.ui.preview.SolarPreviewSurface
import com.solarsystem.ui.tokens.PlanetCardDimens

@Composable
internal fun PlanetImage(
    model: PlanetCardModel,
    modifier: Modifier = Modifier,
    alphaProvider: (() -> Float)? = null,
    translationYProvider: (() -> Float)? = null,
) {
    Box(
        modifier = modifier
            .size(PlanetCardDimens.PlanetWidth, model.imageHeight)
            .graphicsLayer {
                clip = false
                translationYProvider?.let { translationY = it() }
            }
            .planetShadow(
                glowColor = model.glowColor,
                planetWidth = PlanetCardDimens.PlanetWidth,
                planetHeight = model.imageHeight,
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
                .size(PlanetCardDimens.PlanetWidth, model.imageHeight),
            contentScale = ContentScale.Crop,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1223)
@Composable
private fun PlanetImagePreview() {
    SolarPreviewSurface(modifier = Modifier.padding(120.dp)) {
        PlanetImage(model = PlanetCatalog.saturn)
    }
}
