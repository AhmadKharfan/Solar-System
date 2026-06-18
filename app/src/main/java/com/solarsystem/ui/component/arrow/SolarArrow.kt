package com.solarsystem.ui.component.arrow

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.solarsystem.ui.preview.SolarPreviewSurface
import com.solarsystem.ui.tokens.ArrowDimens

@Composable
fun SolarArrow(
    variant: SolarArrowVariant,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(variant.chevronRes()),
        contentDescription = null,
        modifier = modifier.size(ArrowDimens.SingleSize),
        contentScale = ContentScale.Fit,
    )
}

@Composable
fun SolarArrowPreviews(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            SolarArrow(variant = SolarArrowVariant.Default)
            SolarArrow(variant = SolarArrowVariant.Variant2)
            SolarArrow(variant = SolarArrowVariant.Variant3)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
            SolarSwipeUp(variant = SolarSwipeUpVariant.Default)
            SolarSwipeUp(variant = SolarSwipeUpVariant.Variant2)
            SolarSwipeUp(variant = SolarSwipeUpVariant.Variant3)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1223)
@Composable
private fun SolarArrowPreview() {
    SolarPreviewSurface(modifier = Modifier.padding(24.dp)) {
        SolarArrowPreviews()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1223)
@Composable
private fun SolarArrowSinglePreview() {
    SolarPreviewSurface(modifier = Modifier.padding(24.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            SolarArrow(variant = SolarArrowVariant.Default)
            SolarArrow(variant = SolarArrowVariant.Variant2)
            SolarArrow(variant = SolarArrowVariant.Variant3)
        }
    }
}
