package com.solarsystem.ui.component.arrow

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.solarsystem.ui.preview.SolarPreviewSurface
import com.solarsystem.ui.tokens.ArrowDimens

@Composable
fun SolarSwipeUp(
    variant: SolarSwipeUpVariant,
    modifier: Modifier = Modifier,
) {
    val layers = variant.layerRes()
    Column(
        modifier = modifier
            .graphicsLayer { clip = false }
            .width(ArrowDimens.SwipeWidth)
            .padding(
                top = variant.topPadding(),
                bottom = variant.bottomPadding(),
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        layers.forEachIndexed { index, layerRes ->
            SwipeArrowLayer(
                res = layerRes,
                modifier = Modifier.offset(y = -ArrowDimens.SwipeOverlap * index),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1223)
@Composable
private fun SolarSwipeUpPreview() {
    SolarPreviewSurface(modifier = Modifier.padding(24.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
            SolarSwipeUp(variant = SolarSwipeUpVariant.Default)
            SolarSwipeUp(variant = SolarSwipeUpVariant.Variant2)
            SolarSwipeUp(variant = SolarSwipeUpVariant.Variant3)
        }
    }
}
