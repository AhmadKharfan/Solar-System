package com.solarsystem.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.solarsystem.ui.theme.SolarColors
import com.solarsystem.ui.theme.SolarSystemTheme

@Composable
fun SolarPreviewSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    SolarSystemTheme {
        Box(
            modifier = modifier.background(SolarColors.ScreenBackground),
            content = { content() },
        )
    }
}
