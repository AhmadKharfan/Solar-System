package com.solarsystem.ui.component.hero

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.solarsystem.ui.motion.lerp
import com.solarsystem.ui.theme.SolarTypography
import com.solarsystem.ui.tokens.ScreenDimens

@Composable
fun HeroHeaderLayer(
    progressProvider: () -> Float,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current

    Box(modifier = modifier.fillMaxWidth()) {
        HeroHeaderBlock(
            title = "Earth",
            subtitle = "A tiny blue world drifting\nthrough the endless dark.",
            titleStyle = SolarTypography.EarthHeroTitle,
            subtitleStyle = SolarTypography.HeroSubtitle,
            modifier = Modifier
                .width(ScreenDimens.HeroHeaderWidth)
                .align(Alignment.TopCenter)
                .graphicsLayer {
                    val fraction = progressProvider().coerceIn(0f, 1f)
                    translationY = with(density) {
                        lerp(
                            ScreenDimens.EarthHeroHeaderTop,
                            ScreenDimens.EarthHeroHeaderEndTop,
                            fraction,
                        ).toPx()
                    }
                },
        )
        HeroHeaderBlock(
            title = "Our Solar System",
            subtitle = "Earth is only one small part of a much larger story.",
            titleStyle = SolarTypography.SolarHeroTitle,
            subtitleStyle = SolarTypography.HeroSubtitle,
            modifier = Modifier
                .width(ScreenDimens.HeroHeaderWidth)
                .align(Alignment.TopCenter)
                .graphicsLayer {
                    val fraction = progressProvider().coerceIn(0f, 1f)
                    translationY = with(density) {
                        lerp(
                            ScreenDimens.SolarHeroHeaderStartTop,
                            ScreenDimens.SolarHeroHeaderEndTop,
                            fraction,
                        ).toPx()
                    }
                },
        )
    }
}

@Composable
private fun HeroHeaderBlock(
    title: String,
    subtitle: String,
    titleStyle: TextStyle,
    subtitleStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            style = titleStyle,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = subtitle,
            style = subtitleStyle,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = ScreenDimens.HeroHeaderGap),
        )
    }
}
