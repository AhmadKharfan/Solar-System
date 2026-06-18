package com.solarsystem.ui.component.planet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.solarsystem.ui.preview.SolarPreviewSurface
import com.solarsystem.ui.theme.SolarColors
import com.solarsystem.ui.tokens.PlanetCardDimens

@Composable
internal fun CardDividerVertical() {
    Box(
        modifier = Modifier
            .width(PlanetCardDimens.BorderWidth)
            .height(PlanetCardDimens.DividerHeight)
            .background(SolarColors.CardBorder),
    )
}

@Composable
internal fun CardDividerHorizontal() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(PlanetCardDimens.BorderWidth)
            .background(SolarColors.CardBorder),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1223)
@Composable
private fun CardDividersPreview() {
    SolarPreviewSurface(modifier = Modifier.padding(24.dp)) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CardDividerHorizontal()
            CardDividerVertical()
        }
    }
}
