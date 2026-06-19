package com.solarsystem

import android.graphics.BlurMaskFilter
import android.graphics.Color as AndroidColor
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
        )
        setContent {
            SolarSystemScreen(modifier = Modifier.fillMaxSize())
        }
    }
}

/*
 * Theme / Colors / Fonts
 */

private val RubikFontFamily = FontFamily(
    Font(R.font.rubik_regular, FontWeight.Normal),
    Font(R.font.rubik_medium, FontWeight.Medium),
    Font(R.font.rubik_bold, FontWeight.Bold),
)

private val LilyScriptFontFamily = FontFamily(Font(R.font.lily_script_one, FontWeight.Normal))

private object SolarColors {
    val BackgroundBase = Color(0xFF0D0608)
    val CardBackground = Color(0xCC0B1223)
    val CardBackgroundSolid = Color(0xFF0B1223)
    val CardBorder = Color(0xFF2F2E2E)
    val TextPrimary = Color(0xE0FFFFFF)
    val TextSecondary = Color(0xA8FFFFFF)
    val TextShadow = Color(0x29FFFFFF)
    val HeroSubtitle = Color(0xCCFFFFFF)
    val SwipeHintText = Color.White
    val EarthShadow = Color(0xFF4197E7)
    val SwipeHintArrowGlow = Color(0x70FFFFFF)
    val SwipeHintTextShadow = Color(0x70FFFFFF)

    object BackgroundGradient {
        val Transparent = Color(0x00000000)
        val StartDeepSpace = Color(0xFF060816)
        val StartMidnight = Color(0xFF0F172A)
        val StartBlue = Color(0xFF020D3C)
        val EndViolet = Color(0xFF1E1B4B)
        val EndMidnight = Color(0xFF0F172A)
        val EndBlack = Color(0xFF030712)
    }

    object PlanetGlow {
        val Saturn = Color(0x80AB4F20)
        val Mars = Color(0x80FF844E)
        val Mercury = Color(0x80095B91)
        val Venus = Color(0x80C69E4A)
        val Jupiter = Color(0x80FF8332)
        val Uranus = Color(0x8031CFDB)
        val Neptune = Color(0x802CA6DB)
    }
}

private val SolarTextShadow = Shadow(
    color = SolarColors.TextShadow,
    offset = Offset(-4f, 4f),
    blurRadius = 12f,
)

private val FigmaPlatformStyle = PlatformTextStyle(includeFontPadding = false)

private object SolarTypography {
    val CardTitle = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 21.sp,
        letterSpacing = 0.25.sp,
        color = SolarColors.TextPrimary,
        shadow = SolarTextShadow,
        platformStyle = FigmaPlatformStyle,
    )

    val CardSubtitle = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 17.sp,
        letterSpacing = 0.25.sp,
        color = SolarColors.TextSecondary,
        shadow = SolarTextShadow,
        platformStyle = FigmaPlatformStyle,
    )

    val StatLabel = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.25.sp,
        color = SolarColors.TextSecondary,
        platformStyle = FigmaPlatformStyle,
    )

    val StatValue = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.25.sp,
        color = SolarColors.TextPrimary,
        platformStyle = FigmaPlatformStyle,
    )

    val StatHint = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.25.sp,
        color = SolarColors.TextSecondary,
        platformStyle = FigmaPlatformStyle,
    )

    val EarthHeroTitle = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 64.sp,
        lineHeight = 76.sp,
        letterSpacing = 0.25.sp,
        color = SolarColors.TextPrimary,
        shadow = SolarTextShadow,
        platformStyle = FigmaPlatformStyle,
    )

    val SolarHeroTitle = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.25.sp,
        color = SolarColors.TextPrimary,
        shadow = SolarTextShadow,
        platformStyle = FigmaPlatformStyle,
    )

    val HeroSubtitle = TextStyle(
        fontFamily = LilyScriptFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.25.sp,
        color = SolarColors.HeroSubtitle,
        platformStyle = FigmaPlatformStyle,
    )

    val SwipeHint = TextStyle(
        fontFamily = RubikFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 19.sp,
        letterSpacing = 0.25.sp,
        color = SolarColors.SwipeHintText,
        shadow = Shadow(
            color = SolarColors.SwipeHintTextShadow,
            offset = Offset(0f, 4f),
            blurRadius = 16f,
        ),
        platformStyle = FigmaPlatformStyle,
    )
}

/*
 * Models / Constants
 */

private object ArrowDimens {
    val SingleSize = 24.dp
    val SwipeArrowSize = 24.dp
    val SwipeOverlap = 4.dp
    val SwipeShadowOffsetY = 4.dp
    val SwipeShadowBlur = 8.dp
}

private object PlanetCardDimens {
    val Height = 242.dp
    val CornerRadius = 20.dp
    val BorderWidth = 0.5.dp
    val PlanetOffsetX = 15.5.dp
    val PlanetOffsetY = (-16.5).dp
    val PlanetWidth = 112.dp
    val PlanetHeightDefault = 112.dp
    val PlanetHeightSaturn = 104.dp
    val PlanetShadowBlur = 100.dp
    val StackTopPadding = 112.dp
    val SnapDragDistance = 260.dp
    val SnapDistanceThreshold = 84.dp
    val SnapVelocityThreshold = 850.dp
    val HeaderX = 137.dp
    val HeaderWidth = 178.dp
    val TitleY = 13.5.dp
    val SubtitleY = 40.5.dp
    val StatsOffsetX = (-0.5).dp
    val StatsOffsetY = 111.5.dp
    val StatsRowGap = 16.dp
    val StatsRowPaddingX = 16.dp
    val StatsColumnGap = 16.dp
    val IconSize = 20.dp
    val IconTextGap = 8.dp
    val LabelValueGap = 4.dp
    val DividerHeight = 30.dp
    val ListGap = 32.dp
    val StackContainerHeight = Height * 7 + ListGap * 6
}

private object ScreenDimens {
    val FrameWidth = 360.dp
    val FrameHeight = 800.dp
    val BackgroundImageSize = 800.dp
    val BackgroundImageLeft = (-150).dp
    val BackgroundImageTop = 0.dp
    val BackgroundImageViewportCenterX = 250.dp
    val BackgroundImageViewportCenterY = BackgroundImageTop + BackgroundImageSize / 2
    val HeroScrollRange = 770.dp
    val CardsSectionStartTop = 1100.dp
    val CardsSectionEndTop = 330.dp
    val CardsStartTop = CardsSectionStartTop - PlanetCardDimens.StackTopPadding
    val CardsEndTop = CardsSectionEndTop - PlanetCardDimens.StackTopPadding
    val CardsHorizontalPadding = 16.dp
    val EarthBaseSize = 644.dp
    val EarthStartBottomOverflow = 124.dp
    val EarthEndLeft = 80.dp
    val EarthEndTop = 76.dp
    const val EarthEndScale = 200f / 644f
    const val EarthStartAlpha = 1f
    const val EarthEndAlpha = 0.5f
    const val AtmosphereStartAlpha = 0.66f
    const val AtmosphereEndAlpha = 1f
    val EarthHeroHeaderTop = 96.dp
    val EarthHeroHeaderEndTop = (-354).dp
    val SolarHeroHeaderStartTop = (-224).dp
    val SolarHeroHeaderEndTop = 138.dp
    val HeroHeaderWidth = 360.dp
    val HeroHeaderGap = 4.dp
    val SwipeHintBottomPadding = 20.dp
    val SwipeHintHorizontalPadding = 24.dp
    val SwipeHintGap = 10.dp
    val SwipeHintEndTranslationY = 300.dp
}

private const val PeekPlanetAlpha = 0.32f
private val EarthSwipeVelocityThreshold = 850.dp
private val TextLayerBleed = 14.dp
private const val EarthShadowAlpha = 0.25f
private val EarthShadowOffsetY = (-12).dp
private val EarthShadowBlur = 50.dp
private val EarthShadowBleed = 62.dp
private const val CardSnapDurationMillis = 1020
private const val CardSettleDurationMillis = 1460
private const val ActiveCardFloatingScale = 0.004f
private const val BackgroundCardFloatingScale = 0.0017f
private val ActiveCardSettleDistance = 5.dp

@Immutable
private data class PlanetStat(
    @param:DrawableRes val iconRes: Int,
    val label: String,
    val value: String,
    val hint: String? = null,
)

@Immutable
private data class PlanetCardModel(
    val name: String,
    val tagline: String,
    @param:DrawableRes val imageRes: Int,
    val imageHeight: Dp = PlanetCardDimens.PlanetHeightDefault,
    val glowColor: Color,
    val stats: List<PlanetStat>,
)

@Immutable
private data class PlanetCardLayerStyle(
    val backgroundColor: Color = SolarColors.CardBackground,
    val planetAlpha: Float = 1f,
    val planetOffsetY: Dp = PlanetCardDimens.PlanetOffsetY,
    val showTitle: Boolean = true,
    val showTagline: Boolean = true,
    val showStats: Boolean = true,
    val taglineOnlyHeader: Boolean = false,
    val elevateTitle: Boolean = false,
)

@Immutable
private data class PlanetCardStackLayer(
    val offsetY: Dp,
    val style: PlanetCardLayerStyle,
)

private data class PlanetCardVisualState(
    val backgroundColor: Color,
    val planetAlpha: Float,
    val planetOffsetY: Dp,
    val titleAlpha: Float,
    val taglineAlpha: Float,
    val statsAlpha: Float,
    val elevatedTitleAlpha: Float,
)

private data class SolarMotionProgress(
    val earthXProgress: Float,
    val earthYProgress: Float,
    val earthScaleProgress: Float,
    val earthOpacityProgress: Float,
    val cardPositionProgress: Float,
    val cardStackProgress: Float,
    val cardsScreenTop: Dp,
)

private enum class SolarMotionAnchor {
    Expanded,
    Collapsed,
}

private enum class PlanetCardStackVariant {
    Variant2,
    Variant3,
    Variant4,
    Variant5,
    Variant6,
    Variant7,
}

private object PlanetCatalog {
    val all = listOf(
        PlanetCardModel(
            name = "Saturn",
            tagline = "The Ring Master",
            imageRes = R.drawable.img_saturn,
            imageHeight = PlanetCardDimens.PlanetHeightSaturn,
            glowColor = SolarColors.PlanetGlow.Saturn,
            stats = listOf(
                PlanetStat(R.drawable.ic_weight_scale, "You Would Weigh", "70kg → 74kg"),
                PlanetStat(R.drawable.ic_sun, "One Day", "10.7 Hours"),
                PlanetStat(R.drawable.ic_temperature, "Temperature", "-178°C,", hint = "Bring a jacket"),
                PlanetStat(R.drawable.ic_alert_circle, "Additional info", "Lighter than water"),
            ),
        ),
        PlanetCardModel(
            name = "Mars",
            tagline = "The next colony",
            imageRes = R.drawable.img_mars,
            glowColor = SolarColors.PlanetGlow.Mars,
            stats = listOf(
                PlanetStat(R.drawable.ic_weight_scale, "You Would Weigh", "70kg → 27kg"),
                PlanetStat(R.drawable.ic_sun, "One Day", "24.6 Hours"),
                PlanetStat(R.drawable.ic_temperature, "Temperature", "-65°C,", hint = "Bring a jacket"),
                PlanetStat(R.drawable.ic_alert_circle, "Additional info", "Red Dust Storms"),
            ),
        ),
        PlanetCardModel(
            name = "Mercury",
            tagline = "The Fastest Planet",
            imageRes = R.drawable.img_mercury,
            glowColor = SolarColors.PlanetGlow.Mercury,
            stats = listOf(
                PlanetStat(R.drawable.ic_weight_scale, "You Would Weigh", "70kg → 26kg"),
                PlanetStat(R.drawable.ic_sun, "One Day", "1,408 Hours"),
                PlanetStat(R.drawable.ic_temperature, "Temperature", "167°C"),
                PlanetStat(R.drawable.ic_alert_circle, "Additional info", "Birthday every 88 days"),
            ),
        ),
        PlanetCardModel(
            name = "Venus",
            tagline = "The Toxic Beauty",
            imageRes = R.drawable.img_venus,
            glowColor = SolarColors.PlanetGlow.Venus,
            stats = listOf(
                PlanetStat(R.drawable.ic_weight_scale, "You Would Weigh", "70kg → 63kg"),
                PlanetStat(R.drawable.ic_sun, "One Day", "243 Days"),
                PlanetStat(R.drawable.ic_temperature, "Temperature", "465°C"),
                PlanetStat(R.drawable.ic_alert_circle, "Additional info", "Sun rises from West"),
            ),
        ),
        PlanetCardModel(
            name = "Jupiter",
            tagline = "The Heavy Giant",
            imageRes = R.drawable.img_jupiter,
            glowColor = SolarColors.PlanetGlow.Jupiter,
            stats = listOf(
                PlanetStat(R.drawable.ic_weight_scale, "You Would Weigh", "70kg → 177kg"),
                PlanetStat(R.drawable.ic_sun, "One Day", "9.9 Hours"),
                PlanetStat(R.drawable.ic_temperature, "Temperature", "-110°C,", hint = "Bring a jacket"),
                PlanetStat(R.drawable.ic_alert_circle, "Additional info", "Has 95 Moons"),
            ),
        ),
        PlanetCardModel(
            name = "Uranus",
            tagline = "The Lazy Iceberg",
            imageRes = R.drawable.img_uranus,
            glowColor = SolarColors.PlanetGlow.Uranus,
            stats = listOf(
                PlanetStat(R.drawable.ic_weight_scale, "You Would Weigh", "70kg → 62kg"),
                PlanetStat(R.drawable.ic_sun, "One Day", "17 Hours"),
                PlanetStat(R.drawable.ic_temperature, "Temperature", "-224°C,", hint = "Bring 3 jacket"),
                PlanetStat(R.drawable.ic_alert_circle, "Additional info", "diamond Shower"),
            ),
        ),
        PlanetCardModel(
            name = "Neptune",
            tagline = "The Windy World",
            imageRes = R.drawable.img_neptune,
            glowColor = SolarColors.PlanetGlow.Neptune,
            stats = listOf(
                PlanetStat(R.drawable.ic_weight_scale, "You Would Weigh", "70kg → 79kg"),
                PlanetStat(R.drawable.ic_sun, "One Day", "16 Hours"),
                PlanetStat(R.drawable.ic_temperature, "Temperature", "-214°C,", hint = "Bring 3 jacket"),
                PlanetStat(R.drawable.ic_alert_circle, "Additional info", "Wind faster than Sound"),
            ),
        ),
    )
}

/*
 * Main Screen
 */

@Composable
private fun SolarSystemScreen(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val anchoredProgress = remember { Animatable(0f) }
    var cardsAtFirstStep by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { clip = false }
            .screenMotionDrag(
                cardsAtFirstStep = cardsAtFirstStep,
                density = density,
                scope = scope,
                anchoredProgress = anchoredProgress,
            ),
    ) {
        SolarMotionScene(
            progressProvider = { anchoredProgress.value },
            onCardsAtFirstStepChanged = { cardsAtFirstStep = it },
        )
    }
}

private fun Modifier.screenMotionDrag(
    cardsAtFirstStep: Boolean,
    density: Density,
    scope: CoroutineScope,
    anchoredProgress: Animatable<Float, AnimationVector1D>,
): Modifier = pointerInput(cardsAtFirstStep, density) {
    val dragRangePx = with(density) { ScreenDimens.HeroScrollRange.toPx() }.coerceAtLeast(1f)
    val swipeVelocityThresholdPx = with(density) { EarthSwipeVelocityThreshold.toPx() }
    val cardSectionTopPx = with(density) { ScreenDimens.CardsSectionEndTop.toPx() }

    awaitEachGesture {
        handleScreenMotionGesture(
            cardsAtFirstStep = cardsAtFirstStep,
            dragRangePx = dragRangePx,
            swipeVelocityThresholdPx = swipeVelocityThresholdPx,
            cardSectionTopPx = cardSectionTopPx,
            scope = scope,
            anchoredProgress = anchoredProgress,
        )
    }
}

private suspend fun AwaitPointerEventScope.handleScreenMotionGesture(
    cardsAtFirstStep: Boolean,
    dragRangePx: Float,
    swipeVelocityThresholdPx: Float,
    cardSectionTopPx: Float,
    scope: CoroutineScope,
    anchoredProgress: Animatable<Float, AnimationVector1D>,
) {
    val down = awaitFirstDown(requireUnconsumed = false)
    val velocityTracker = VelocityTracker()
    velocityTracker.addPosition(down.uptimeMillis, down.position)

    val gestureStartProgress = anchoredProgress.value.coerceIn(0f, 1f)
    val startedInCardSection = down.position.y >= cardSectionTopPx
    var latestProgress = gestureStartProgress
    var totalDragY = 0f
    var draggingScreenMotion = false
    var progressSnapJob: Job? = null

    while (true) {
        val event = awaitPointerEvent(PointerEventPass.Initial)
        val change = event.changes.firstOrNull { it.id == down.id } ?: break
        if (!change.pressed) break

        totalDragY += change.positionChange().y
        velocityTracker.addPosition(change.uptimeMillis, change.position)
        if (!draggingScreenMotion && abs(totalDragY) >= viewConfiguration.touchSlop) {
            if (!canStartScreenMotion(
                    totalDragY = totalDragY,
                    gestureStartProgress = gestureStartProgress,
                    startedInCardSection = startedInCardSection,
                    cardsAtFirstStep = cardsAtFirstStep,
                )
            ) {
                continue
            }
            draggingScreenMotion = true
        }

        if (draggingScreenMotion) {
            change.consume()
            latestProgress = (gestureStartProgress - totalDragY / dragRangePx).coerceIn(0f, 1f)
            progressSnapJob?.cancel()
            progressSnapJob = scope.launch {
                anchoredProgress.snapTo(latestProgress)
            }
        }
    }

    if (!draggingScreenMotion) return

    progressSnapJob?.cancel()
    val target = targetScreenAnchor(
        velocityY = velocityTracker.calculateVelocity().y,
        velocityThresholdPx = swipeVelocityThresholdPx,
        latestProgress = latestProgress,
    )
    scope.launch {
        anchoredProgress.snapTo(latestProgress)
        anchoredProgress.animateTo(
            targetValue = target.progressValue(),
            animationSpec = OvershootSpringSpec,
        )
    }
}

private fun canStartScreenMotion(
    totalDragY: Float,
    gestureStartProgress: Float,
    startedInCardSection: Boolean,
    cardsAtFirstStep: Boolean,
): Boolean {
    val isDraggingTowardCards = totalDragY < 0f
    val isDraggingTowardHero = totalDragY > 0f
    val canMoveProgress = when {
        isDraggingTowardCards -> gestureStartProgress < 1f
        isDraggingTowardHero -> gestureStartProgress > 0f
        else -> false
    }
    val startedInSettledCards = startedInCardSection && gestureStartProgress >= 0.95f
    return if (startedInSettledCards) {
        cardsAtFirstStep && isDraggingTowardHero
    } else {
        canMoveProgress
    }
}

private fun targetScreenAnchor(
    velocityY: Float,
    velocityThresholdPx: Float,
    latestProgress: Float,
) = when {
    velocityY <= -velocityThresholdPx -> SolarMotionAnchor.Collapsed
    velocityY >= velocityThresholdPx -> SolarMotionAnchor.Expanded
    latestProgress >= 0.5f -> SolarMotionAnchor.Collapsed
    else -> SolarMotionAnchor.Expanded
}

@Composable
private fun BoxScope.SolarMotionScene(
    progressProvider: () -> Float,
    onCardsAtFirstStepChanged: (Boolean) -> Unit,
) {
    ScreenBackground(progressProvider = progressProvider)
    AtmosphereBackdropLayer(progressProvider = progressProvider)

    Box(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .widthIn(max = ScreenDimens.FrameWidth)
            .fillMaxWidth()
            .fillMaxHeight()
            .graphicsLayer { clip = false },
    ) {
        SolarSystemContent(
            progressProvider = progressProvider,
        )
    }

    PlanetCardsLayer(
        progressProvider = progressProvider,
        onCardsAtFirstStepChanged = onCardsAtFirstStepChanged,
    )
}

@Composable
private fun BoxScope.SolarSystemContent(
    progressProvider: () -> Float,
) {
    AnimatedEarthLayer(
        progressProvider = progressProvider,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { clip = false },
    )

    HeroHeaderLayer(
        progressProvider = {
            solarMotionProgress(progressProvider()).cardPositionProgress
        },
        modifier = Modifier.align(Alignment.TopCenter),
    )

    SwipeHintFooter(
        progressProvider = {
            solarMotionProgress(progressProvider()).cardPositionProgress
        },
        modifier = Modifier.align(Alignment.BottomCenter),
    )
}

@Composable
private fun BoxScope.PlanetCardsLayer(
    progressProvider: () -> Float,
    onCardsAtFirstStepChanged: (Boolean) -> Unit,
) {
    val density = LocalDensity.current

    ScrollableInterpolatedPlanetCardStack(
        entranceStackProgressProvider = {
            solarMotionProgress(progressProvider()).cardStackProgress
        },
        onFirstStepChanged = onCardsAtFirstStepChanged,
        modifier = Modifier
            .align(Alignment.TopStart)
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = ScreenDimens.CardsHorizontalPadding)
            .graphicsLayer {
                clip = false
                val motion = solarMotionProgress(progressProvider())
                val cardsScreenTopPx = with(density) { motion.cardsScreenTop.toPx() }
                translationY = cardsScreenTopPx
            },
    )
}

/*
 * Components
 */

@Composable
private fun ScreenBackground(
    progressProvider: () -> Float,
    modifier: Modifier = Modifier,
) {
    val startGradient = remember { Brush.verticalGradient(colorStops = StartGradientStops.toTypedArray()) }
    val endGradient = remember { Brush.verticalGradient(colorStops = EndGradientStops.toTypedArray()) }

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SolarColors.BackgroundBase),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = 1f - progressProvider().coerceIn(0f, 1f)
                }
                .background(startGradient),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = progressProvider().coerceIn(0f, 1f)
                }
                .background(endGradient),
        )
    }
}

@Composable
private fun AtmosphereBackdropLayer(
    progressProvider: () -> Float,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { clip = false },
    ) {
        val frameLeft = (maxWidth - ScreenDimens.FrameWidth) / 2
        val frameTop = (maxHeight - ScreenDimens.FrameHeight) / 2
        val viewportLeft = frameLeft + ScreenDimens.BackgroundImageLeft
        val viewportTop = frameTop + ScreenDimens.BackgroundImageTop
        val coverLeft = minOf(viewportLeft, 0.dp)
        val coverTop = minOf(viewportTop, 0.dp)
        val coverWidth = maxOf(maxWidth - coverLeft, ScreenDimens.BackgroundImageSize)
        val coverHeight = maxOf(maxHeight - coverTop, ScreenDimens.BackgroundImageSize)
        val viewportCenterX = viewportLeft + ScreenDimens.BackgroundImageViewportCenterX
        val viewportCenterY = viewportTop + ScreenDimens.BackgroundImageViewportCenterY
        val horizontalBias = ((viewportCenterX - coverLeft).value / coverWidth.value - 0.5f) * 2f
        val verticalBias = ((viewportCenterY - coverTop).value / coverHeight.value - 0.5f) * 2f

        Box(
            modifier = Modifier
                .offset(x = coverLeft, y = coverTop)
                .requiredSize(width = coverWidth, height = coverHeight)
                .graphicsLayer { clip = false },
        ) {
            Image(
                painter = painterResource(R.drawable.img_background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alignment = BiasAlignment(
                    horizontalBias = horizontalBias.coerceIn(-1f, 1f),
                    verticalBias = verticalBias.coerceIn(-1f, 1f),
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        clip = false
                        alpha = interpolate(
                            ScreenDimens.AtmosphereStartAlpha,
                            ScreenDimens.AtmosphereEndAlpha,
                            progressProvider().coerceIn(0f, 1f),
                        )
                    },
            )
        }
    }
}

@Composable
private fun AnimatedEarthLayer(
    progressProvider: () -> Float,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { clip = false },
    ) {
        val startBounds = earthStartBounds(maxWidth = maxWidth, maxHeight = maxHeight)
        EarthShadowLayer(
            startBounds = startBounds,
            progressProvider = progressProvider,
        )
        EarthImageLayer(
            startBounds = startBounds,
            progressProvider = progressProvider,
        )
    }
}

@Composable
private fun EarthShadowLayer(
    startBounds: EarthStartBounds,
    progressProvider: () -> Float,
) {
    Box(
        modifier = Modifier
            .earthPlacement(
                left = startBounds.left - EarthShadowBleed,
                top = startBounds.top - EarthShadowBleed,
                width = ScreenDimens.EarthBaseSize + EarthShadowBleed * 2,
                height = ScreenDimens.EarthBaseSize + EarthShadowBleed * 2,
            )
            .graphicsLayer {
                applyEarthMotion(
                    progressProvider = progressProvider,
                    startBounds = startBounds,
                    scaleImage = false,
                )
            }
            .earthFigmaDropShadow(
                discDiameterProvider = { earthDiscDiameter(progressProvider()) },
                shadowAlphaProvider = { earthShadowAlpha(progressProvider()) },
            ),
    )
}

@Composable
private fun EarthImageLayer(
    startBounds: EarthStartBounds,
    progressProvider: () -> Float,
) {
    Box(
        modifier = Modifier
            .earthPlacement(
                left = startBounds.left,
                top = startBounds.top,
                width = ScreenDimens.EarthBaseSize,
                height = ScreenDimens.EarthBaseSize,
            )
            .graphicsLayer {
                applyEarthMotion(
                    progressProvider = progressProvider,
                    startBounds = startBounds,
                    scaleImage = true,
                )
            },
    ) {
        Image(
            painter = painterResource(R.drawable.img_earth),
            contentDescription = "Earth",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

private data class EarthStartBounds(
    val left: Dp,
    val top: Dp,
)

private fun earthStartBounds(maxWidth: Dp, maxHeight: Dp) = EarthStartBounds(
    left = maxWidth / 2 - 8.dp - ScreenDimens.EarthBaseSize / 2,
    top = maxHeight + ScreenDimens.EarthStartBottomOverflow - ScreenDimens.EarthBaseSize,
)

private fun GraphicsLayerScope.applyEarthMotion(
    progressProvider: () -> Float,
    startBounds: EarthStartBounds,
    scaleImage: Boolean,
) {
    clip = false
    val motion = solarMotionProgress(progressProvider())
    val alpha = earthAlpha(motion)
    val scale = earthScale(motion)
    val left = interpolate(startBounds.left, ScreenDimens.EarthEndLeft, motion.earthXProgress)
    val top = interpolate(startBounds.top, ScreenDimens.EarthEndTop, motion.earthYProgress)

    translationX = (left - startBounds.left).toPx()
    translationY = (top - startBounds.top).toPx()
    transformOrigin = TransformOrigin(0f, 0f)
    this.alpha = alpha
    if (scaleImage) {
        scaleX = scale
        scaleY = scale
    }
}

private fun earthDiscDiameter(progress: Float): Dp =
    ScreenDimens.EarthBaseSize * earthScale(solarMotionProgress(progress))

private fun earthShadowAlpha(progress: Float): Float {
    val motion = solarMotionProgress(progress)
    val firstStateCompensation = interpolate(0.52f, 1f, motion.earthScaleProgress)
    return EarthShadowAlpha * earthAlpha(motion) * firstStateCompensation
}

private fun earthAlpha(motion: SolarMotionProgress): Float =
    interpolate(
        ScreenDimens.EarthStartAlpha,
        ScreenDimens.EarthEndAlpha,
        motion.earthOpacityProgress,
    )

private fun earthScale(motion: SolarMotionProgress): Float =
    interpolate(1f, ScreenDimens.EarthEndScale, motion.earthScaleProgress)

@Composable
private fun HeroHeaderLayer(
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
                    translationY = with(density) {
                        interpolate(
                            ScreenDimens.EarthHeroHeaderTop,
                            ScreenDimens.EarthHeroHeaderEndTop,
                            progressProvider(),
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
                    translationY = with(density) {
                        interpolate(
                            ScreenDimens.SolarHeroHeaderStartTop,
                            ScreenDimens.SolarHeroHeaderEndTop,
                            progressProvider(),
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

@Composable
private fun SwipeHintFooter(
    progressProvider: () -> Float,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                translationY = with(density) {
                    (ScreenDimens.SwipeHintEndTranslationY * progressProvider()).toPx()
                }
            }
            .padding(
                horizontal = ScreenDimens.SwipeHintHorizontalPadding,
                vertical = ScreenDimens.SwipeHintBottomPadding,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ScreenDimens.SwipeHintGap),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(-ArrowDimens.SwipeOverlap),
        ) {
            SwipeHintArrowLayers.forEach { layerRes ->
                SwipeArrowLayer(
                    res = layerRes,
                    glowColor = SolarColors.SwipeHintArrowGlow,
                    arrowSize = ArrowDimens.SingleSize,
                )
            }
        }
        Text(
            text = "Swipe up to explore",
            style = SolarTypography.SwipeHint,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SwipeArrowLayer(
    @DrawableRes res: Int,
    modifier: Modifier = Modifier,
    glowColor: Color,
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

@Composable
private fun ScrollableInterpolatedPlanetCardStack(
    entranceStackProgressProvider: () -> Float,
    onFirstStepChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    planets: List<PlanetCardModel> = PlanetCatalog.all,
) {
    val density = LocalDensity.current
    val motionScope = rememberCoroutineScope()
    val maxStep = (planets.size - 1).coerceAtLeast(1)
    val stepProgress = remember { Animatable(0f) }
    val cardSettlePhase = remember { Animatable(1f) }
    val settledStep = remember { mutableIntStateOf(0) }
    val settleDirection = remember { mutableFloatStateOf(0f) }
    val styleStep by remember(maxStep) {
        derivedStateOf {
            stepProgress.value.roundToInt().coerceIn(0, maxStep)
        }
    }
    val motionStackProgressProvider = remember(maxStep) {
        {
            currentStackProgress(stepProgress.value, maxStep)
        }
    }
    val snapAnimationSpec: AnimationSpec<Float> = remember {
        tween(
            durationMillis = CardSnapDurationMillis,
            easing = CubicBezierEasing(0.18f, 0.0f, 0.08f, 1.0f),
        )
    }
    val settleAnimationSpec: AnimationSpec<Float> = remember {
        tween(
            durationMillis = CardSettleDurationMillis,
            easing = LinearEasing,
        )
    }

    LaunchedEffect(stepProgress) {
        snapshotFlow { stepProgress.value <= 0.01f }
            .distinctUntilChanged()
            .collect { atFirstStep ->
                onFirstStepChanged(atFirstStep)
            }
    }

    Box(
        modifier = modifier
            .graphicsLayer { clip = false }
            .planetCardStackDrag(
                entranceStackProgressProvider = entranceStackProgressProvider,
                planets = planets,
                maxStep = maxStep,
                density = density,
                motionScope = motionScope,
                stepProgress = stepProgress,
                cardSettlePhase = cardSettlePhase,
                settledStep = settledStep,
                settleDirection = settleDirection,
                snapAnimationSpec = snapAnimationSpec,
                settleAnimationSpec = settleAnimationSpec,
            ),
    ) {
        InterpolatedPlanetCardStack(
            motionStackProgressProvider = motionStackProgressProvider,
            activeCardIndexProvider = {
                stepProgress.value.roundToInt().coerceIn(0, planets.lastIndex)
            },
            settlePhaseProvider = { cardSettlePhase.value },
            settleDirectionProvider = { settleDirection.floatValue },
            styleStackProgress = 1f - (styleStep / maxStep.toFloat()).coerceIn(0f, 1f),
            planets = planets,
            modifier = Modifier
                .padding(top = PlanetCardDimens.StackTopPadding)
                .graphicsLayer { clip = false },
        )
    }
}

private fun Modifier.planetCardStackDrag(
    entranceStackProgressProvider: () -> Float,
    planets: List<PlanetCardModel>,
    maxStep: Int,
    density: Density,
    motionScope: CoroutineScope,
    stepProgress: Animatable<Float, AnimationVector1D>,
    cardSettlePhase: Animatable<Float, AnimationVector1D>,
    settledStep: MutableIntState,
    settleDirection: MutableFloatState,
    snapAnimationSpec: AnimationSpec<Float>,
    settleAnimationSpec: AnimationSpec<Float>,
): Modifier = pointerInput(maxStep, density) {
    val snapDistance = PlanetCardDimens.SnapDragDistance.toPx()
    val distanceThreshold = PlanetCardDimens.SnapDistanceThreshold.toPx()
    val velocityThreshold = PlanetCardDimens.SnapVelocityThreshold.toPx()

    awaitEachGesture {
        handleCardStackGesture(
            entranceStackProgressProvider = entranceStackProgressProvider,
            planets = planets,
            maxStep = maxStep,
            density = density,
            motionScope = motionScope,
            stepProgress = stepProgress,
            cardSettlePhase = cardSettlePhase,
            settledStep = settledStep,
            settleDirection = settleDirection,
            snapAnimationSpec = snapAnimationSpec,
            settleAnimationSpec = settleAnimationSpec,
            snapDistance = snapDistance,
            distanceThreshold = distanceThreshold,
            velocityThreshold = velocityThreshold,
        )
    }
}

private suspend fun AwaitPointerEventScope.handleCardStackGesture(
    entranceStackProgressProvider: () -> Float,
    planets: List<PlanetCardModel>,
    maxStep: Int,
    density: Density,
    motionScope: CoroutineScope,
    stepProgress: Animatable<Float, AnimationVector1D>,
    cardSettlePhase: Animatable<Float, AnimationVector1D>,
    settledStep: MutableIntState,
    settleDirection: MutableFloatState,
    snapAnimationSpec: AnimationSpec<Float>,
    settleAnimationSpec: AnimationSpec<Float>,
    snapDistance: Float,
    distanceThreshold: Float,
    velocityThreshold: Float,
) {
    val down = awaitFirstDown(requireUnconsumed = false)
    if (entranceStackProgressProvider() < 0.95f) return

    val velocityTracker = VelocityTracker()
    velocityTracker.addPosition(down.uptimeMillis, down.position)
    val gestureStartStep = settledStep.intValue
    val gestureStartProgress = stepProgress.value
    val cardStackWidthPx = size.width.toFloat()
    val hitCardBody = with(density) {
        isCardBodyHit(
            position = down.position,
            cardWidthPx = cardStackWidthPx,
            stackProgress = currentStackProgress(stepProgress.value, maxStep),
            planets = planets,
        )
    }
    val dragResult = trackCardDrag(
        down = down,
        velocityTracker = velocityTracker,
        hitCardBody = hitCardBody,
        gestureStartStep = gestureStartStep,
        gestureStartProgress = gestureStartProgress,
        maxStep = maxStep,
        snapDistance = snapDistance,
        motionScope = motionScope,
        stepProgress = stepProgress,
        settleDirection = settleDirection,
    ) ?: return

    val targetStep = targetCardStep(
        gestureStartStep = gestureStartStep,
        latestDraggedStep = dragResult.latestDraggedStep,
        velocityY = dragResult.velocityY,
        velocityThreshold = velocityThreshold,
        distanceThreshold = distanceThreshold,
        snapDistance = snapDistance,
        maxStep = maxStep,
    )
    animateCardStackToStep(
        targetStep = targetStep,
        latestDraggedStep = dragResult.latestDraggedStep,
        motionScope = motionScope,
        stepProgress = stepProgress,
        cardSettlePhase = cardSettlePhase,
        settledStep = settledStep,
        settleDirection = settleDirection,
        snapAnimationSpec = snapAnimationSpec,
        settleAnimationSpec = settleAnimationSpec,
    )
}

private data class CardDragResult(
    val latestDraggedStep: Float,
    val velocityY: Float,
)

private suspend fun AwaitPointerEventScope.trackCardDrag(
    down: PointerInputChange,
    velocityTracker: VelocityTracker,
    hitCardBody: Boolean,
    gestureStartStep: Int,
    gestureStartProgress: Float,
    maxStep: Int,
    snapDistance: Float,
    motionScope: CoroutineScope,
    stepProgress: Animatable<Float, AnimationVector1D>,
    settleDirection: MutableFloatState,
): CardDragResult? {
    var totalDragY = 0f
    var isDraggingCard = false
    var dragSnapJob: Job? = null
    var latestDraggedStep = gestureStartProgress

    while (true) {
        val event = awaitPointerEvent()
        val change = event.changes.firstOrNull { it.id == down.id } ?: break
        if (!change.pressed) break

        totalDragY += change.positionChange().y
        velocityTracker.addPosition(change.uptimeMillis, change.position)
        if (!isDraggingCard && abs(totalDragY) >= viewConfiguration.touchSlop) {
            if (!canStartCardDrag(
                    totalDragY = totalDragY,
                    hitCardBody = hitCardBody,
                    gestureStartStep = gestureStartStep,
                    maxStep = maxStep,
                )
            ) {
                change.consume()
                break
            }
            isDraggingCard = true
        }

        if (isDraggingCard) {
            change.consume()
            settleDirection.floatValue = 0f
            latestDraggedStep = draggedStep(
                gestureStartProgress = gestureStartProgress,
                totalDragY = totalDragY,
                snapDistance = snapDistance,
                maxStep = maxStep,
            )
            dragSnapJob?.cancel()
            dragSnapJob = motionScope.launch {
                stepProgress.snapTo(latestDraggedStep)
            }
        }
    }

    dragSnapJob?.cancel()
    return if (isDraggingCard) {
        CardDragResult(
            latestDraggedStep = latestDraggedStep,
            velocityY = velocityTracker.calculateVelocity().y,
        )
    } else {
        null
    }
}

private fun canStartCardDrag(
    totalDragY: Float,
    hitCardBody: Boolean,
    gestureStartStep: Int,
    maxStep: Int,
): Boolean {
    val direction = dragStepDirection(totalDragY)
    return hitCardBody && (gestureStartStep + direction).coerceIn(0, maxStep) != gestureStartStep
}

private fun draggedStep(
    gestureStartProgress: Float,
    totalDragY: Float,
    snapDistance: Float,
    maxStep: Int,
): Float = (gestureStartProgress - totalDragY / snapDistance).coerceIn(0f, maxStep.toFloat())

private fun targetCardStep(
    gestureStartStep: Int,
    latestDraggedStep: Float,
    velocityY: Float,
    velocityThreshold: Float,
    distanceThreshold: Float,
    snapDistance: Float,
    maxStep: Int,
): Int {
    val distanceStep = latestDraggedStep - gestureStartStep.toFloat()
    val velocityStep = when {
        velocityY <= -velocityThreshold -> 1
        velocityY >= velocityThreshold -> -1
        else -> 0
    }
    val thresholdStep = when {
        distanceStep >= distanceThreshold / snapDistance -> 1
        distanceStep <= -distanceThreshold / snapDistance -> -1
        else -> 0
    }
    return (gestureStartStep + if (velocityStep != 0) velocityStep else thresholdStep)
        .coerceIn(0, maxStep)
}

private fun animateCardStackToStep(
    targetStep: Int,
    latestDraggedStep: Float,
    motionScope: CoroutineScope,
    stepProgress: Animatable<Float, AnimationVector1D>,
    cardSettlePhase: Animatable<Float, AnimationVector1D>,
    settledStep: MutableIntState,
    settleDirection: MutableFloatState,
    snapAnimationSpec: AnimationSpec<Float>,
    settleAnimationSpec: AnimationSpec<Float>,
) {
    settledStep.intValue = targetStep
    val targetStepFloat = targetStep.toFloat()
    settleDirection.floatValue = when {
        targetStepFloat > latestDraggedStep -> -1f
        targetStepFloat < latestDraggedStep -> 1f
        else -> 0f
    }
    motionScope.launch {
        stepProgress.snapTo(latestDraggedStep)
        cardSettlePhase.snapTo(0f)
        launch {
            stepProgress.animateTo(
                targetValue = targetStepFloat,
                animationSpec = snapAnimationSpec,
            )
        }
        launch {
            cardSettlePhase.animateTo(
                targetValue = 1f,
                animationSpec = settleAnimationSpec,
            )
        }
    }
}

private fun dragStepDirection(totalDragY: Float) = when {
    totalDragY < 0f -> 1
    totalDragY > 0f -> -1
    else -> 0
}

private fun currentStackProgress(stepProgress: Float, maxStep: Int): Float =
    1f - (stepProgress / maxStep.toFloat()).coerceIn(0f, 1f)

@Composable
private fun InterpolatedPlanetCardStack(
    motionStackProgressProvider: () -> Float,
    styleStackProgress: Float,
    modifier: Modifier = Modifier,
    planets: List<PlanetCardModel> = PlanetCatalog.all,
    activeCardIndexProvider: () -> Int = { 0 },
    settlePhaseProvider: () -> Float = { 1f },
    settleDirectionProvider: () -> Float = { 0f },
) {
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .graphicsLayer { clip = false }
            .fillMaxWidth()
            .height(PlanetCardDimens.StackContainerHeight),
    ) {
        planets.forEachIndexed { index, planet ->
            val zIndex = stackZIndex(
                index = index,
                lastIndex = planets.lastIndex,
                stackProgress = styleStackProgress,
            )
            PlanetInfoCard(
                model = planet,
                visualStateProvider = {
                    interpolatePlanetCardVisualState(
                        index = index,
                        progress = motionStackProgressProvider(),
                    )
                },
                modifier = Modifier
                    .zIndex(zIndex)
                    .align(Alignment.TopStart)
                    .graphicsLayer {
                        clip = false
                        val stackProgress = motionStackProgressProvider()
                        val activeIndex = activeCardIndexProvider()
                        val settleTranslationY = cardSettleTranslationY(
                            index = index,
                            activeIndex = activeIndex,
                            phase = settlePhaseProvider(),
                            direction = settleDirectionProvider(),
                        )
                        val floatingScale = cardFloatingScale(
                            index = index,
                            activeIndex = activeIndex,
                            stackProgress = stackProgress,
                            cardCount = planets.size,
                        )
                        translationY = with(density) {
                            interpolatePlanetStackOffsetY(
                                index = index,
                                progress = stackProgress,
                            ).toPx()
                        } + with(density) { settleTranslationY.toPx() }
                        scaleX = floatingScale
                        scaleY = floatingScale
                    },
            )
        }
    }
}

@Composable
private fun PlanetInfoCard(
    model: PlanetCardModel,
    modifier: Modifier = Modifier,
    layerStyle: PlanetCardLayerStyle = PlanetCardLayerStyle(),
    visualStateProvider: (() -> PlanetCardVisualState)? = null,
) {
    val staticVisualState = remember(layerStyle) { layerStyle.toVisualState() }
    val currentVisualState = visualStateProvider ?: { staticVisualState }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .graphicsLayer { clip = false }
            .fillMaxWidth()
            .height(PlanetCardDimens.Height),
    ) {
        PlanetCardBackground(visualStateProvider = currentVisualState)

        PlanetImage(
            model = model,
            alphaProvider = { currentVisualState().planetAlpha },
            translationYProvider = {
                with(density) {
                    (currentVisualState().planetOffsetY - PlanetCardDimens.PlanetOffsetY).toPx()
                }
            },
            modifier = Modifier
                .zIndex(1f)
                .align(Alignment.TopStart)
                .offset(
                    x = PlanetCardDimens.PlanetOffsetX,
                    y = PlanetCardDimens.PlanetOffsetY,
                ),
        )

        PlanetCardHeader(
            model = model,
            visualStateProvider = currentVisualState,
        )

        PlanetStatsGrid(
            model = model,
            modifier = Modifier
                .zIndex(2f)
                .graphicsLayer {
                    alpha = currentVisualState().statsAlpha
                },
        )

        PlanetCardTitle(
            text = model.name,
            alphaProvider = { currentVisualState().elevatedTitleAlpha },
            modifier = Modifier.zIndex(3f),
        )
    }
}

@Composable
private fun BoxScope.PlanetCardBackground(visualStateProvider: () -> PlanetCardVisualState) {
    Box(
        modifier = Modifier
            .matchParentSize()
            .drawBehind {
                val radius = PlanetCardDimens.CornerRadius.toPx()
                val strokeWidth = PlanetCardDimens.BorderWidth.toPx()
                drawRoundRect(
                    color = visualStateProvider().backgroundColor,
                    cornerRadius = CornerRadius(radius, radius),
                )
                drawRoundRect(
                    color = SolarColors.CardBorder,
                    cornerRadius = CornerRadius(radius, radius),
                    style = Stroke(width = strokeWidth),
                )
            },
    )
}

@Composable
private fun PlanetImage(
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

@Composable
private fun PlanetCardHeader(
    model: PlanetCardModel,
    visualStateProvider: () -> PlanetCardVisualState,
) {
    FadingTextLayer(
        text = model.name,
        style = SolarTypography.CardTitle,
        alphaProvider = { visualStateProvider().titleAlpha },
        y = PlanetCardDimens.TitleY,
        modifier = Modifier.zIndex(2f),
    )
    FadingTextLayer(
        text = model.tagline,
        style = SolarTypography.CardSubtitle,
        alphaProvider = { visualStateProvider().taglineAlpha },
        y = PlanetCardDimens.SubtitleY,
        modifier = Modifier.zIndex(2f),
    )
}

@Composable
private fun PlanetCardTitle(
    text: String,
    modifier: Modifier = Modifier,
    alphaProvider: () -> Float = { 1f },
) {
    FadingTextLayer(
        text = text,
        style = SolarTypography.CardTitle,
        alphaProvider = alphaProvider,
        y = PlanetCardDimens.TitleY,
        modifier = modifier,
    )
}

@Composable
private fun FadingTextLayer(
    text: String,
    style: TextStyle,
    alphaProvider: () -> Float,
    y: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .offset(
                x = PlanetCardDimens.HeaderX - TextLayerBleed,
                y = y - TextLayerBleed,
            )
            .width(PlanetCardDimens.HeaderWidth + TextLayerBleed * 2)
            .graphicsLayer {
                clip = false
                alpha = alphaProvider()
            }
            .padding(TextLayerBleed),
    ) {
        Text(
            text = text,
            style = style,
            modifier = Modifier.width(PlanetCardDimens.HeaderWidth),
        )
    }
}

@Composable
private fun PlanetStatsGrid(
    model: PlanetCardModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .offset(x = PlanetCardDimens.StatsOffsetX, y = PlanetCardDimens.StatsOffsetY)
            .fillMaxWidth(),
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
            modifier = Modifier.weight(1f),
        )
        CardDividerVertical()
        PlanetStatCell(
            stat = right,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun PlanetStatCell(
    stat: PlanetStat,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(PlanetCardDimens.IconTextGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlanetStatIcon(iconRes = stat.iconRes)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(PlanetCardDimens.LabelValueGap),
        ) {
            Text(text = stat.label, style = SolarTypography.StatLabel)
            Text(
                text = stat.valueWithHint(),
                style = SolarTypography.StatValue,
            )
        }
    }
}

@Composable
private fun PlanetStatIcon(
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.size(PlanetCardDimens.IconSize),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun CardDividerVertical() {
    Box(
        modifier = Modifier
            .width(PlanetCardDimens.BorderWidth)
            .height(PlanetCardDimens.DividerHeight)
            .background(SolarColors.CardBorder),
    )
}

@Composable
private fun CardDividerHorizontal() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(PlanetCardDimens.BorderWidth)
            .background(SolarColors.CardBorder),
    )
}

/*
 * Animations / Motion Helpers
 */

private val OvershootSpringSpec: AnimationSpec<Float> = spring(
    dampingRatio = 0.7f,
    stiffness = 25f,
    visibilityThreshold = 0.001f,
)

private val CardStackEasing = CubicBezierEasing(0.24f, 0.0f, 0.04f, 1.0f)
private const val CardStackDelay = 0.04f

private val StartGradientStops = listOf(
    0f to SolarColors.BackgroundGradient.Transparent,
    0.24545f to SolarColors.BackgroundGradient.StartDeepSpace,
    0.43331f to SolarColors.BackgroundGradient.StartMidnight,
    1f to SolarColors.BackgroundGradient.StartBlue,
)

private val EndGradientStops = listOf(
    0f to SolarColors.BackgroundGradient.EndViolet,
    0.5f to SolarColors.BackgroundGradient.EndMidnight,
    1f to SolarColors.BackgroundGradient.EndBlack,
)

private val SwipeHintArrowLayers = listOf(
    R.drawable.ic_arrow1,
    R.drawable.ic_arrow2,
    R.drawable.ic_arrow3,
)

private fun solarMotionProgress(rawProgress: Float): SolarMotionProgress {
    val clamped = rawProgress.coerceIn(0f, 1f)
    val cardStackProgress = CardStackEasing.transform(delayedCardStackProgress(clamped))

    return SolarMotionProgress(
        earthXProgress = rawProgress,
        earthYProgress = rawProgress,
        earthScaleProgress = rawProgress,
        earthOpacityProgress = rawProgress,
        cardPositionProgress = rawProgress,
        cardStackProgress = cardStackProgress,
        cardsScreenTop = interpolate(
            ScreenDimens.CardsStartTop,
            ScreenDimens.CardsEndTop,
            rawProgress,
        ),
    )
}

private fun SolarMotionAnchor.progressValue(): Float = when (this) {
    SolarMotionAnchor.Expanded -> 0f
    SolarMotionAnchor.Collapsed -> 1f
}

private fun interpolatePlanetStackOffsetY(index: Int, progress: Float): Dp {
    val clamped = progress.coerceIn(0f, 1f)
    if (clamped <= 0f) return StackKeyframes.first().layerAt(index).offsetY
    if (clamped >= 1f) return StackKeyframes.last().layerAt(index).offsetY

    val scaled = clamped * StackKeyframes.lastIndex
    val fromIndex = scaled.toInt().coerceIn(0, StackKeyframes.lastIndex - 1)
    val segment = scaled - fromIndex
    val start = StackKeyframes[fromIndex].layerAt(index)
    val end = StackKeyframes[fromIndex + 1].layerAt(index)
    return interpolate(start.offsetY, end.offsetY, segment)
}

private fun interpolatePlanetCardVisualState(index: Int, progress: Float): PlanetCardVisualState {
    val clamped = progress.coerceIn(0f, 1f)
    if (clamped <= 0f) return StackKeyframes.first().layerAt(index).style.toVisualState()
    if (clamped >= 1f) return StackKeyframes.last().layerAt(index).style.toVisualState()

    val scaled = clamped * StackKeyframes.lastIndex
    val fromIndex = scaled.toInt().coerceIn(0, StackKeyframes.lastIndex - 1)
    val segment = scaled - fromIndex
    val start = StackKeyframes[fromIndex].layerAt(index).style
    val end = StackKeyframes[fromIndex + 1].layerAt(index).style
    return PlanetCardVisualState(
        backgroundColor = interpolateColor(start.backgroundColor, end.backgroundColor, segment),
        planetAlpha = interpolate(start.planetAlpha, end.planetAlpha, segment),
        planetOffsetY = interpolate(start.planetOffsetY, end.planetOffsetY, segment),
        titleAlpha = interpolate(start.titleAlpha(), end.titleAlpha(), segment),
        taglineAlpha = interpolate(start.taglineAlpha(), end.taglineAlpha(), segment),
        statsAlpha = interpolate(start.statsAlpha(), end.statsAlpha(), segment),
        elevatedTitleAlpha = interpolate(start.elevatedTitleAlpha(), end.elevatedTitleAlpha(), segment),
    )
}

private fun defaultStackLayers(): List<PlanetCardStackLayer> {
    val pitch = PlanetCardDimens.Height + PlanetCardDimens.ListGap
    return List(7) { index ->
        PlanetCardStackLayer(
            offsetY = pitch * index,
            style = PlanetCardLayerStyle(),
        )
    }
}

private fun PlanetCardStackVariant.layers(): List<PlanetCardStackLayer> = when (this) {
    PlanetCardStackVariant.Variant2 -> StackVariant2Layers
    PlanetCardStackVariant.Variant3 -> StackVariant3Layers
    PlanetCardStackVariant.Variant4 -> StackVariant4Layers
    PlanetCardStackVariant.Variant5 -> StackVariant5Layers
    PlanetCardStackVariant.Variant6 -> StackVariant6Layers
    PlanetCardStackVariant.Variant7 -> StackVariant7Layers
}

private val StackVariant2Layers = listOf(
    PlanetCardStackLayer(
        offsetY = 0.dp,
        style = PlanetCardLayerStyle(
            backgroundColor = SolarColors.CardBackgroundSolid,
            planetAlpha = PeekPlanetAlpha,
            elevateTitle = true,
        ),
    ),
    PlanetCardStackLayer(offsetY = 14.dp, style = peekSolidFront()),
    PlanetCardStackLayer(offsetY = 288.dp, style = PlanetCardLayerStyle()),
    PlanetCardStackLayer(offsetY = 562.dp, style = PlanetCardLayerStyle()),
    PlanetCardStackLayer(offsetY = 836.dp, style = PlanetCardLayerStyle()),
    PlanetCardStackLayer(offsetY = 1110.dp, style = PlanetCardLayerStyle()),
    PlanetCardStackLayer(offsetY = 1384.dp, style = PlanetCardLayerStyle()),
)

private val StackVariant3Layers = listOf(
    PlanetCardStackLayer(offsetY = 0.dp, style = peekSolidTagline()),
    PlanetCardStackLayer(offsetY = 14.dp, style = peekSolidStatsOnly().copy(elevateTitle = true)),
    PlanetCardStackLayer(offsetY = 28.dp, style = peekSolidFront()),
    PlanetCardStackLayer(offsetY = 302.dp, style = PlanetCardLayerStyle()),
    PlanetCardStackLayer(offsetY = 576.dp, style = PlanetCardLayerStyle()),
    PlanetCardStackLayer(offsetY = 850.dp, style = PlanetCardLayerStyle()),
    PlanetCardStackLayer(offsetY = 1124.dp, style = PlanetCardLayerStyle()),
)

private val StackVariant4Layers = listOf(
    PlanetCardStackLayer(offsetY = 0.dp, style = peekSolidTagline()),
    PlanetCardStackLayer(offsetY = 14.dp, style = peekSolidStatsOnly().copy(elevateTitle = true)),
    PlanetCardStackLayer(offsetY = 28.dp, style = peekSolidTagline().copy(elevateTitle = true)),
    PlanetCardStackLayer(offsetY = 42.dp, style = peekSolidFront()),
    PlanetCardStackLayer(offsetY = 316.dp, style = PlanetCardLayerStyle()),
    PlanetCardStackLayer(offsetY = 590.dp, style = PlanetCardLayerStyle()),
    PlanetCardStackLayer(offsetY = 864.dp, style = PlanetCardLayerStyle()),
)

private val StackVariant5Layers = listOf(
    PlanetCardStackLayer(offsetY = 0.dp, style = peekSolidTagline()),
    PlanetCardStackLayer(offsetY = 14.dp, style = peekSolidStatsOnly()),
    PlanetCardStackLayer(offsetY = 28.dp, style = peekSolidTagline()),
    PlanetCardStackLayer(offsetY = 42.dp, style = peekSolidTagline().copy(planetOffsetY = (-15.5).dp)),
    PlanetCardStackLayer(offsetY = 56.dp, style = peekSolidFront()),
    PlanetCardStackLayer(offsetY = 330.dp, style = PlanetCardLayerStyle()),
    PlanetCardStackLayer(offsetY = 604.dp, style = PlanetCardLayerStyle()),
)

private val StackVariant6Layers = listOf(
    PlanetCardStackLayer(offsetY = 0.dp, style = peekSolidTagline()),
    PlanetCardStackLayer(offsetY = 14.dp, style = peekSolidStatsOnly()),
    PlanetCardStackLayer(offsetY = 28.dp, style = peekSolidTagline()),
    PlanetCardStackLayer(offsetY = 42.dp, style = peekSolidTagline().copy(planetOffsetY = (-15.5).dp)),
    PlanetCardStackLayer(
        offsetY = 56.dp,
        style = PlanetCardLayerStyle(
            backgroundColor = SolarColors.CardBackgroundSolid,
            planetAlpha = PeekPlanetAlpha,
            elevateTitle = true,
        ),
    ),
    PlanetCardStackLayer(offsetY = 70.dp, style = peekSolidFront()),
    PlanetCardStackLayer(offsetY = 344.dp, style = PlanetCardLayerStyle()),
)

private val StackVariant7Layers = listOf(
    PlanetCardStackLayer(offsetY = 0.dp, style = peekSolidTagline()),
    PlanetCardStackLayer(offsetY = 14.dp, style = peekSolidStatsOnly()),
    PlanetCardStackLayer(offsetY = 28.dp, style = peekSolidTagline()),
    PlanetCardStackLayer(offsetY = 42.dp, style = peekSolidTagline().copy(planetOffsetY = (-15.5).dp)),
    PlanetCardStackLayer(
        offsetY = 56.dp,
        style = PlanetCardLayerStyle(
            backgroundColor = SolarColors.CardBackgroundSolid,
            planetAlpha = PeekPlanetAlpha,
            elevateTitle = true,
        ),
    ),
    PlanetCardStackLayer(
        offsetY = 70.dp,
        style = PlanetCardLayerStyle(
            backgroundColor = SolarColors.CardBackgroundSolid,
            planetAlpha = PeekPlanetAlpha,
        ),
    ),
    PlanetCardStackLayer(offsetY = 84.dp, style = peekSolidFront()),
)

private val StackKeyframes: List<List<PlanetCardStackLayer>> = listOf(
    PlanetCardStackVariant.Variant7.layers(),
    PlanetCardStackVariant.Variant6.layers(),
    PlanetCardStackVariant.Variant5.layers(),
    PlanetCardStackVariant.Variant4.layers(),
    PlanetCardStackVariant.Variant3.layers(),
    PlanetCardStackVariant.Variant2.layers(),
    defaultStackLayers(),
)

private fun peekSolidTagline() = PlanetCardLayerStyle(
    backgroundColor = SolarColors.CardBackgroundSolid,
    planetAlpha = PeekPlanetAlpha,
    showTitle = false,
    taglineOnlyHeader = true,
)

private fun peekSolidStatsOnly() = PlanetCardLayerStyle(
    backgroundColor = SolarColors.CardBackgroundSolid,
    planetAlpha = PeekPlanetAlpha,
    showTitle = false,
    showTagline = false,
)

private fun peekSolidFront() = PlanetCardLayerStyle(backgroundColor = SolarColors.CardBackgroundSolid)

private fun PlanetCardLayerStyle.toVisualState() = PlanetCardVisualState(
    backgroundColor = backgroundColor,
    planetAlpha = planetAlpha,
    planetOffsetY = planetOffsetY,
    titleAlpha = titleAlpha(),
    taglineAlpha = taglineAlpha(),
    statsAlpha = statsAlpha(),
    elevatedTitleAlpha = elevatedTitleAlpha(),
)

private fun List<PlanetCardStackLayer>.layerAt(index: Int): PlanetCardStackLayer =
    getOrElse(index) { last() }

private fun PlanetCardLayerStyle.titleAlpha() =
    if (showTitle && !taglineOnlyHeader) 1f else 0f

private fun PlanetCardLayerStyle.taglineAlpha() =
    if (showTagline) 1f else 0f

private fun PlanetCardLayerStyle.statsAlpha() =
    if (showStats) 1f else 0f

private fun PlanetCardLayerStyle.elevatedTitleAlpha() =
    if (elevateTitle) 1f else 0f

private fun Density.isCardBodyHit(
    position: Offset,
    cardWidthPx: Float,
    stackProgress: Float,
    planets: List<PlanetCardModel>,
): Boolean {
    if (position.x !in 0f..cardWidthPx) return false

    val topPadding = PlanetCardDimens.StackTopPadding.toPx()
    val cardHeight = PlanetCardDimens.Height.toPx()
    return planets.indices.any { index ->
        val top = topPadding + interpolatePlanetStackOffsetY(index, stackProgress).toPx()
        position.y in top..(top + cardHeight)
    }
}

private fun stackZIndex(
    index: Int,
    lastIndex: Int,
    stackProgress: Float,
): Float {
    val stackedAmount = 1f - stackProgress.coerceIn(0f, 1f)
    val lastCardBoost = if (index == lastIndex && stackedAmount > 0.82f) 100f else 0f
    return index.toFloat() + lastCardBoost
}

private fun cardSettleTranslationY(
    index: Int,
    activeIndex: Int,
    phase: Float,
    direction: Float,
): Dp {
    if (direction == 0f) return 0.dp

    val indexDistance = abs(index - activeIndex)
    val weight = when (indexDistance) {
        0 -> 1f
        1 -> 0.42f
        2 -> 0.18f
        else -> 0f
    }
    if (weight == 0f) return 0.dp

    val delayedPhase = delayedCardSettlePhase(
        phase = phase,
        indexDistance = indexDistance,
    )
    val followThrough = settleFollowThrough(delayedPhase)
    return ActiveCardSettleDistance * direction * weight * followThrough
}

private fun cardFloatingScale(
    index: Int,
    activeIndex: Int,
    stackProgress: Float,
    cardCount: Int,
): Float {
    if (cardCount <= 1) return 1f

    val fractionalStep = (1f - stackProgress.coerceIn(0f, 1f)) * (cardCount - 1)
    val travelAmount = 1f - (abs(fractionalStep - fractionalStep.roundToInt()) * 2f).coerceIn(0f, 1f)
    if (travelAmount == 0f) return 1f

    val activeDistance = abs(index - activeIndex)
    val scaleAmount = when (activeDistance) {
        0 -> ActiveCardFloatingScale
        1 -> BackgroundCardFloatingScale
        else -> 0f
    }
    return 1f + scaleAmount * travelAmount
}

private fun delayedCardSettlePhase(
    phase: Float,
    indexDistance: Int,
): Float {
    val delay = indexDistance * 0.045f
    return ((phase.coerceIn(0f, 1f) - delay) / (1f - delay)).coerceIn(0f, 1f)
}

private fun settleFollowThrough(phase: Float): Float = when {
    phase < 0.48f -> 0f
    phase < 0.76f -> smoothStep((phase - 0.48f) / 0.28f)
    else -> 1f - smoothStep((phase - 0.76f) / 0.24f)
}

/*
 * Modifiers / Utilities
 */

private fun Modifier.earthPlacement(
    left: Dp,
    top: Dp,
    width: Dp,
    height: Dp,
): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(
        Constraints.fixed(
            width = width.roundToPx(),
            height = height.roundToPx(),
        ),
    )
    layout(constraints.maxWidth, constraints.maxHeight) {
        placeable.place(left.roundToPx(), top.roundToPx())
    }
}

private fun Modifier.earthFigmaDropShadow(
    discDiameterProvider: () -> Dp,
    shadowAlphaProvider: () -> Float = { EarthShadowAlpha },
    shadowBlurProvider: () -> Dp = { EarthShadowBlur },
): Modifier = drawBehind {
    val discDiameterPx = discDiameterProvider().toPx()
    val center = Offset(
        x = EarthShadowBleed.toPx() + discDiameterPx / 2f,
        y = EarthShadowBleed.toPx() + discDiameterPx / 2f,
    )
    drawEarthShadow(
        discDiameterPx = discDiameterPx,
        shadowAlpha = shadowAlphaProvider(),
        blurPx = shadowBlurProvider().toPx(),
        center = center,
    )
}

private fun DrawScope.drawEarthShadow(
    discDiameterPx: Float,
    shadowAlpha: Float,
    blurPx: Float,
    center: Offset,
) {
    val discRadius = discDiameterPx / 2f
    val offsetY = EarthShadowOffsetY.toPx()
    val shadowCenter = Offset(center.x, center.y + offsetY)
    val shadowArgb = SolarColors.EarthShadow.copy(alpha = shadowAlpha).toArgb()

    drawIntoCanvas { canvas ->
        val paint = android.graphics.Paint().apply {
            isAntiAlias = true
            color = shadowArgb
            maskFilter = BlurMaskFilter(blurPx, BlurMaskFilter.Blur.NORMAL)
        }
        canvas.nativeCanvas.drawCircle(shadowCenter.x, shadowCenter.y, discRadius, paint)
    }
}

private fun Modifier.planetShadow(
    glowColor: Color,
    planetWidth: Dp,
    planetHeight: Dp,
): Modifier = drawBehind {
    val blurPx = PlanetCardDimens.PlanetShadowBlur.toPx()
    val planetRadiusPx = minOf(planetWidth.toPx(), planetHeight.toPx()) / 2f
    val glowRadius = planetRadiusPx + blurPx
    val center = Offset(size.width / 2f, size.height / 2f)
    val edgeStop = planetRadiusPx / glowRadius

    drawCircle(
        brush = Brush.radialGradient(
            colorStops = arrayOf(
                0f to Color.Transparent,
                (edgeStop * 0.72f) to Color.Transparent,
                (edgeStop * 0.92f) to glowColor.copy(alpha = glowColor.alpha * 0.55f),
                (edgeStop * 1.18f) to glowColor.copy(alpha = glowColor.alpha * 0.28f),
                (edgeStop * 1.55f) to glowColor.copy(alpha = glowColor.alpha * 0.08f),
                1f to Color.Transparent,
            ),
            center = center,
            radius = glowRadius,
        ),
        radius = glowRadius,
        center = center,
    )
}

private fun Modifier.arrowDropShadow(glowColor: Color): Modifier = drawBehind {
    val blurPx = ArrowDimens.SwipeShadowBlur.toPx()
    val offsetY = ArrowDimens.SwipeShadowOffsetY.toPx()
    val insetTop = size.height * 0.3308f
    val insetHorizontal = size.width * 0.2279f
    val insetBottom = size.height * 0.3529f
    val chevronWidth = size.width - insetHorizontal * 2f
    val chevronHeight = size.height - insetTop - insetBottom
    val centerX = size.width / 2f
    val centerY = insetTop + chevronHeight / 2f + offsetY
    val radiusX = chevronWidth / 2f + blurPx * 0.35f
    val radiusY = chevronHeight / 2f + blurPx * 0.35f

    drawOval(
        brush = Brush.radialGradient(
            colorStops = arrayOf(
                0f to glowColor.copy(alpha = glowColor.alpha * 0.55f),
                0.65f to glowColor.copy(alpha = glowColor.alpha * 0.2f),
                1f to Color.Transparent,
            ),
            center = Offset(centerX, centerY),
            radius = maxOf(radiusX, radiusY),
        ),
        topLeft = Offset(centerX - radiusX, centerY - radiusY),
        size = Size(radiusX * 2f, radiusY * 2f),
    )
}

private fun PlanetStat.valueWithHint() = buildAnnotatedString {
    if (hint == null) {
        append(value)
        return@buildAnnotatedString
    }

    withStyle(SolarTypography.StatValue.asStatSpanStyle()) {
        append(value)
    }
    append(" ")
    withStyle(SolarTypography.StatHint.asStatSpanStyle()) {
        append(hint)
    }
}

private fun TextStyle.asStatSpanStyle() = SpanStyle(
    color = color,
    fontSize = fontSize,
    fontFamily = fontFamily,
    fontWeight = fontWeight,
    letterSpacing = letterSpacing,
)

private fun delayedCardStackProgress(fraction: Float): Float =
    ((fraction - CardStackDelay) / (1f - CardStackDelay)).coerceIn(0f, 1f)

private fun interpolate(start: Float, end: Float, fraction: Float): Float =
    start + (end - start) * fraction

private fun interpolate(start: Dp, end: Dp, fraction: Float): Dp =
    start + (end - start) * fraction

private fun interpolateColor(start: Color, end: Color, fraction: Float): Color {
    val f = fraction.coerceIn(0f, 1f)
    return Color(
        alpha = interpolate(start.alpha, end.alpha, f),
        red = interpolate(start.red, end.red, f),
        green = interpolate(start.green, end.green, f),
        blue = interpolate(start.blue, end.blue, f),
    )
}

private fun smoothStep(fraction: Float): Float {
    val t = fraction.coerceIn(0f, 1f)
    return t * t * (3f - 2f * t)
}
