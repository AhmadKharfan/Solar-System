package com.solarsystem.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.solarsystem.data.PlanetCatalog
import com.solarsystem.ui.component.arrow.SolarArrowPreviews
import com.solarsystem.ui.component.planet.PlanetCardStack
import com.solarsystem.ui.component.planet.PlanetCardStackVariant
import com.solarsystem.ui.component.planet.PlanetInfoCard
import com.solarsystem.ui.theme.SolarColors
import com.solarsystem.ui.theme.SolarSystemTheme
import com.solarsystem.ui.theme.SolarTypography
import com.solarsystem.ui.tokens.PlanetCardDimens

@Composable
private fun ComponentSectionLabel(text: String) {
    Text(
        text = text,
        style = SolarTypography.CardSubtitle,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
fun ComponentPreviews(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SolarColors.ScreenBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp + PlanetCardDimens.PlanetOverflowTop, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(48.dp),
    ) {
        ComponentSectionLabel("Arrows")
        SolarArrowPreviews()
        ComponentSectionLabel("Vertical Plants — Default")
        PlanetCardStack(variant = PlanetCardStackVariant.Default)
        ComponentSectionLabel("Vertical Plants — Variant2")
        PlanetCardStack(variant = PlanetCardStackVariant.Variant2)
        ComponentSectionLabel("Vertical Plants — Variant3")
        PlanetCardStack(variant = PlanetCardStackVariant.Variant3)
        ComponentSectionLabel("Vertical Plants — Variant4")
        PlanetCardStack(variant = PlanetCardStackVariant.Variant4)
        ComponentSectionLabel("Vertical Plants — Variant5")
        PlanetCardStack(variant = PlanetCardStackVariant.Variant5)
        ComponentSectionLabel("Vertical Plants — Variant6")
        PlanetCardStack(variant = PlanetCardStackVariant.Variant6)
        ComponentSectionLabel("Vertical Plants — Variant7")
        PlanetCardStack(variant = PlanetCardStackVariant.Variant7)
    }
}

@Composable
fun AllPlanetsPreview(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SolarColors.ScreenBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp + PlanetCardDimens.PlanetOverflowTop, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PlanetCardDimens.ListGap),
    ) {
        PlanetCatalog.all.forEach { planet ->
            PlanetInfoCard(model = planet)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1223, heightDp = 3600)
@Composable
private fun ComponentPreviewsScreenPreview() {
    SolarSystemTheme {
        ComponentPreviews()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1223, heightDp = 2200)
@Composable
private fun AllPlanetsScreenPreview() {
    SolarSystemTheme {
        AllPlanetsPreview()
    }
}
