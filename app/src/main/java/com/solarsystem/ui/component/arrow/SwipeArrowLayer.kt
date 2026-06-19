package com.solarsystem.ui.component.arrow

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.solarsystem.ui.modifier.arrowDropShadow
import com.solarsystem.ui.theme.SolarColors
import com.solarsystem.ui.tokens.ArrowDimens

@Composable
internal fun SwipeArrowLayer(
    @DrawableRes res: Int,
    modifier: Modifier = Modifier,
    glowColor: Color = SolarColors.ArrowBlueGlow,
    arrowSize: Dp = ArrowDimens.SwipeArrowSize,
) {
    val usesBlurShadow = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    Box(
        modifier = modifier
            .graphicsLayer { clip = false }
            .size(arrowSize)
            .then(
                if (usesBlurShadow) {
                    Modifier
                } else {
                    Modifier.arrowDropShadow(glowColor)
                },
            ),
    ) {
        if (usesBlurShadow) {
            Image(
                painter = painterResource(res),
                contentDescription = null,
                colorFilter = ColorFilter.tint(glowColor.copy(alpha = 1f)),
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = ArrowDimens.SwipeShadowOffsetY)
                    .blur(ArrowDimens.SwipeShadowBlur)
                    .alpha(glowColor.alpha),
                contentScale = ContentScale.Fit,
            )
        }
        Image(
            painter = painterResource(res),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
        )
    }
}
