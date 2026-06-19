package com.solarsystem.ui.component.planet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.solarsystem.data.PlanetCatalog
import com.solarsystem.model.PlanetCardModel
import com.solarsystem.model.PlanetStat
import com.solarsystem.ui.preview.SolarPreviewSurface
import com.solarsystem.ui.tokens.PlanetCardDimens

@Composable
internal fun PlanetStatsGrid(
    model: PlanetCardModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .offset(x = PlanetCardDimens.StatsOffsetX, y = PlanetCardDimens.StatsOffsetY)
            .width(PlanetCardDimens.Width),
        verticalArrangement = Arrangement.spacedBy(PlanetCardDimens.StatsRowGap),
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
            .padding(horizontal = PlanetCardDimens.StatsRowPaddingX),
        horizontalArrangement = Arrangement.spacedBy(PlanetCardDimens.StatsColumnGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlanetStatCell(
            stat = left,
            textWidth = PlanetCardDimens.StatsLeftTextWidth,
            modifier = Modifier.weight(1f),
        )
        CardDividerVertical()
        PlanetStatCell(
            stat = right,
            textWidth = PlanetCardDimens.StatsRightTextWidth,
            modifier = Modifier.weight(1f),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1223, widthDp = 360)
@Composable
private fun PlanetStatsGridPreview() {
    SolarPreviewSurface(modifier = Modifier.padding(16.dp)) {
        PlanetStatsGrid(model = PlanetCatalog.saturn)
    }
}
