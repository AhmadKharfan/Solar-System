package com.solarsystem.ui.component.planet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.solarsystem.R
import com.solarsystem.model.PlanetStat
import com.solarsystem.ui.preview.SolarPreviewSurface
import com.solarsystem.ui.theme.SolarTypography
import com.solarsystem.ui.tokens.PlanetCardDimens

@Composable
internal fun PlanetStatCell(
    stat: PlanetStat,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(PlanetCardDimens.IconTextGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlanetStatIcon(iconRes = stat.iconRes)
        Column(verticalArrangement = Arrangement.spacedBy(PlanetCardDimens.LabelValueGap)) {
            Text(text = stat.label, style = SolarTypography.StatLabel)
            if (stat.hint != null) {
                Row {
                    Text(text = stat.value, style = SolarTypography.StatValue)
                    Text(text = " ${stat.hint}", style = SolarTypography.StatHint)
                }
            } else {
                Text(text = stat.value, style = SolarTypography.StatValue)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1223)
@Composable
private fun PlanetStatCellPreview() {
    SolarPreviewSurface(modifier = Modifier.padding(24.dp)) {
        PlanetStatCell(
            stat = PlanetStat(
                iconRes = R.drawable.ic_temperature,
                label = "Temperature",
                value = "-178°C,",
                hint = "Bring a jacket",
            ),
        )
    }
}
