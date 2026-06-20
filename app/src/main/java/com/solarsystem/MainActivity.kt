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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
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
import kotlin.math.floor
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
 * Constants
 */

private val SwipeArrowShadowOffsetY = 4.dp
private val SwipeArrowShadowBlur = 8.dp

private val EarthBaseSize = 644.dp

private val CardHeight = 242.dp
private val CardBorderWidth = 0.5.dp
private val PlanetImageOffsetY = (-16.5).dp
private val PlanetImageWidth = 112.dp
private val CardStackTopPadding = 112.dp
private val CardHeaderWidth = 178.dp
private val CardTitleY = 13.5.dp
private val CardListGap = 32.dp
private val TextLayerBleed = 14.dp
private val EarthShadowBleed = 62.dp
private const val VisibleStackSlots = 7
private const val MaxStackedCardsBeforeActive = 10
private const val VisibleCardsAfterActive = 20
private const val EarthShadowAlpha = 0.25f

private const val PeekPlanetAlpha = 0.32f


/*
 * Models
 */


@Immutable
private data class PlanetStat(
    @param:DrawableRes val iconRes: Int,
    val label: String,
    val value: String,
    val hint: String? = null,
)

@Immutable
private data class PlanetCardModel(
    val id: Int,
    val name: String,
    val tagline: String,
    @param:DrawableRes val imageRes: Int,
    val imageHeight: Dp = 112.dp,
    val glowColor: Color,
    val stats: List<PlanetStat>,
)

@Immutable
private data class CardFrame(
    val offsetY: Dp = 0.dp,
    val cardAlpha: Float = 1f,
    val backgroundColor: Color = SolarColors.CardBackground,
    val planetAlpha: Float = 1f,
    val planetOffsetY: Dp = PlanetImageOffsetY,
    val titleAlpha: Float = 1f,
    val taglineAlpha: Float = 1f,
    val statsAlpha: Float = 1f,
    val elevatedTitleAlpha: Float = 0f,
)

private object PlanetCatalog {
    val all = listOf(
        PlanetCardModel(
            id = 1,
            name = "Saturn",
            tagline = "The Ring Master",
            imageRes = R.drawable.img_saturn,
            imageHeight = 104.dp,
            glowColor = SolarColors.PlanetGlow.Saturn,
            stats = listOf(
                PlanetStat(R.drawable.ic_weight_scale, "You Would Weigh", "70kg → 74kg"),
                PlanetStat(R.drawable.ic_sun, "One Day", "10.7 Hours"),
                PlanetStat(R.drawable.ic_temperature, "Temperature", "-178°C,", hint = "Bring a jacket"),
                PlanetStat(R.drawable.ic_alert_circle, "Additional info", "Lighter than water"),
            ),
        ),
        PlanetCardModel(
            id = 2,
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
            id = 3,
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
            id = 4,
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
            id = 5,
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
            id = 6,
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
            id = 7,
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
    val dragRangePx = with(density) { 770.dp.toPx() }.coerceAtLeast(1f)
    val swipeVelocityThresholdPx = with(density) { 850.dp.toPx() }
    val cardSectionTopPx = with(density) { 330.dp.toPx() }

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

    val dragResult = trackScreenMotionDrag(
        down = down,
        velocityTracker = velocityTracker,
        cardsAtFirstStep = cardsAtFirstStep,
        dragRangePx = dragRangePx,
        cardSectionTopPx = cardSectionTopPx,
        scope = scope,
        anchoredProgress = anchoredProgress,
    ) ?: return

    settleScreenMotion(
        latestProgress = dragResult.latestProgress,
        velocityY = dragResult.velocityY,
        velocityThresholdPx = swipeVelocityThresholdPx,
        scope = scope,
        anchoredProgress = anchoredProgress,
    )
}

private data class ScreenMotionDragResult(
    val latestProgress: Float,
    val velocityY: Float,
)

private suspend fun AwaitPointerEventScope.trackScreenMotionDrag(
    down: PointerInputChange,
    velocityTracker: VelocityTracker,
    cardsAtFirstStep: Boolean,
    dragRangePx: Float,
    cardSectionTopPx: Float,
    scope: CoroutineScope,
    anchoredProgress: Animatable<Float, AnimationVector1D>,
): ScreenMotionDragResult? {
    val dragState = ScreenMotionDragState(
        gestureStartProgress = anchoredProgress.value.coerceIn(0f, 1f),
        startedInCardSection = down.position.y >= cardSectionTopPx,
    )

    while (true) {
        val change = awaitTrackedPointerChange(down) ?: break
        updateScreenMotionDrag(
            state = dragState,
            change = change,
            velocityTracker = velocityTracker,
            cardsAtFirstStep = cardsAtFirstStep,
            dragRangePx = dragRangePx,
            scope = scope,
            anchoredProgress = anchoredProgress,
        )
    }

    return finishScreenMotionDrag(
        state = dragState,
        velocityTracker = velocityTracker,
    )
}

private class ScreenMotionDragState(
    val gestureStartProgress: Float,
    val startedInCardSection: Boolean,
) {
    var latestProgress: Float = gestureStartProgress
    var totalDragY: Float = 0f
    var isDragging: Boolean = false
    var snapJob: Job? = null
}

private fun AwaitPointerEventScope.updateScreenMotionDrag(
    state: ScreenMotionDragState,
    change: PointerInputChange,
    velocityTracker: VelocityTracker,
    cardsAtFirstStep: Boolean,
    dragRangePx: Float,
    scope: CoroutineScope,
    anchoredProgress: Animatable<Float, AnimationVector1D>,
) {
    state.totalDragY += change.positionChange().y
    velocityTracker.addPosition(change.uptimeMillis, change.position)
    state.startIfAllowed(
        touchSlop = viewConfiguration.touchSlop,
        cardsAtFirstStep = cardsAtFirstStep,
    )
    if (state.isDragging) {
        snapScreenProgress(
            state = state,
            change = change,
            dragRangePx = dragRangePx,
            scope = scope,
            anchoredProgress = anchoredProgress,
        )
    }
}

private fun ScreenMotionDragState.startIfAllowed(
    touchSlop: Float,
    cardsAtFirstStep: Boolean,
) {
    if (!isDragging && canStartScreenMotionDrag(
            totalDragY = totalDragY,
            touchSlop = touchSlop,
            gestureStartProgress = gestureStartProgress,
            startedInCardSection = startedInCardSection,
            cardsAtFirstStep = cardsAtFirstStep,
        )
    ) {
        isDragging = true
    }
}

private fun snapScreenProgress(
    state: ScreenMotionDragState,
    change: PointerInputChange,
    dragRangePx: Float,
    scope: CoroutineScope,
    anchoredProgress: Animatable<Float, AnimationVector1D>,
) {
    change.consume()
    state.latestProgress = draggedScreenProgress(
        gestureStartProgress = state.gestureStartProgress,
        totalDragY = state.totalDragY,
        dragRangePx = dragRangePx,
    )
    state.snapJob?.cancel()
    state.snapJob = scope.launch {
        anchoredProgress.snapTo(state.latestProgress)
    }
}

private fun finishScreenMotionDrag(
    state: ScreenMotionDragState,
    velocityTracker: VelocityTracker,
): ScreenMotionDragResult? {
    state.snapJob?.cancel()
    return if (state.isDragging) {
        ScreenMotionDragResult(
            latestProgress = state.latestProgress,
            velocityY = velocityTracker.calculateVelocity().y,
        )
    } else {
        null
    }
}

private suspend fun AwaitPointerEventScope.awaitTrackedPointerChange(
    down: PointerInputChange,
): PointerInputChange? {
    val event = awaitPointerEvent(PointerEventPass.Initial)
    val change = event.changes.firstOrNull { it.id == down.id } ?: return null
    return change.takeIf { it.pressed }
}

private fun canStartScreenMotionDrag(
    totalDragY: Float,
    touchSlop: Float,
    gestureStartProgress: Float,
    startedInCardSection: Boolean,
    cardsAtFirstStep: Boolean,
): Boolean =
    abs(totalDragY) >= touchSlop && canStartScreenMotion(
        totalDragY = totalDragY,
        gestureStartProgress = gestureStartProgress,
        startedInCardSection = startedInCardSection,
        cardsAtFirstStep = cardsAtFirstStep,
    )

private fun draggedScreenProgress(
    gestureStartProgress: Float,
    totalDragY: Float,
    dragRangePx: Float,
): Float =
    (gestureStartProgress - totalDragY / dragRangePx).coerceIn(0f, 1f)

private fun settleScreenMotion(
    latestProgress: Float,
    velocityY: Float,
    velocityThresholdPx: Float,
    scope: CoroutineScope,
    anchoredProgress: Animatable<Float, AnimationVector1D>,
) {
    val target = targetScreenAnchor(
        velocityY = velocityY,
        velocityThresholdPx = velocityThresholdPx,
        latestProgress = latestProgress,
    )
    scope.launch {
        anchoredProgress.snapTo(latestProgress)
        anchoredProgress.animateTo(
            targetValue = target,
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
    velocityY <= -velocityThresholdPx -> 1f
    velocityY >= velocityThresholdPx -> 0f
    latestProgress >= 0.5f -> 1f
    else -> 0f
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
        progressProvider = progressProvider,
        modifier = Modifier.align(Alignment.TopCenter),
    )

    SwipeHintFooter(
        progressProvider = progressProvider,
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
            cardStackEntranceProgress(progressProvider())
        },
        onFirstStepChanged = onCardsAtFirstStepChanged,
        modifier = Modifier
            .align(Alignment.TopStart)
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 16.dp)
            .graphicsLayer {
                clip = false
                translationY = with(density) { cardsScreenTop(progressProvider()).toPx() }
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
    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { clip = false },
    ) {
        Image(
            painter = painterResource(R.drawable.img_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    clip = false
                    alpha = interpolate(
                        0.66f,
                        1f,
                        progressProvider().coerceIn(0f, 1f),
                    )
                },
        )
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
                width = EarthBaseSize + EarthShadowBleed * 2,
                height = EarthBaseSize + EarthShadowBleed * 2,
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
                width = EarthBaseSize,
                height = EarthBaseSize,
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
    val endLeft: Dp,
)

private fun earthStartBounds(maxWidth: Dp, maxHeight: Dp) = EarthStartBounds(
    left = maxWidth / 2 - 8.dp - EarthBaseSize / 2,
    top = maxHeight + 124.dp - EarthBaseSize,
    endLeft = maxWidth / 2 - 100.dp,
)

private fun GraphicsLayerScope.applyEarthMotion(
    progressProvider: () -> Float,
    startBounds: EarthStartBounds,
    scaleImage: Boolean,
) {
    clip = false
    val progress = progressProvider()
    val alpha = earthAlpha(progress)
    val scale = earthScale(progress)
    val left = interpolate(startBounds.left, startBounds.endLeft, progress)
    val top = interpolate(startBounds.top, 76.dp, progress)

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
    EarthBaseSize * earthScale(progress)

private fun earthShadowAlpha(progress: Float): Float {
    val firstStateCompensation = interpolate(0.52f, 1f, progress)
    return EarthShadowAlpha * earthAlpha(progress) * firstStateCompensation
}

private fun earthAlpha(progress: Float): Float =
    interpolate(
        1f,
        0.5f,
        progress,
    )

private fun earthScale(progress: Float): Float =
    interpolate(1f, 200f / 644f, progress)

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
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .graphicsLayer {
                    translationY = with(density) {
                        interpolate(
                            96.dp,
                            (-354).dp,
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
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .graphicsLayer {
                    translationY = with(density) {
                        interpolate(
                            (-224).dp,
                            138.dp,
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
                .padding(top = 4.dp),
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
                    (300.dp * progressProvider()).toPx()
                }
            }
            .padding(
                horizontal = 24.dp,
                vertical = 20.dp,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SwipeHintContent()
    }
}

@Composable
private fun SwipeHintContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy((-4).dp),
    ) {
        SwipeHintArrowLayers.forEach { layerRes ->
            SwipeArrowLayer(
                res = layerRes,
                glowColor = SolarColors.SwipeHintArrowGlow,
            )
        }
    }
    Text(
        text = "Swipe up to explore",
        style = SolarTypography.SwipeHint,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun SwipeArrowLayer(
    @DrawableRes res: Int,
    modifier: Modifier = Modifier,
    glowColor: Color,
) {
    val usesBlurShadow = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    Box(
        modifier = modifier
            .graphicsLayer { clip = false }
            .size(24.dp)
            .then(
                if (usesBlurShadow) {
                    Modifier
                } else {
                    Modifier.arrowDropShadow(glowColor)
                },
            ),
    ) {
        if (usesBlurShadow) {
            SwipeArrowBlurShadow(
                res = res,
                glowColor = glowColor,
            )
        }
        SwipeArrowImage(res = res)
    }
}

@Composable
private fun SwipeArrowBlurShadow(
    @DrawableRes res: Int,
    glowColor: Color,
) {
    Image(
        painter = painterResource(res),
        contentDescription = null,
        colorFilter = ColorFilter.tint(glowColor.copy(alpha = 1f)),
        modifier = Modifier
            .fillMaxSize()
            .offset(y = SwipeArrowShadowOffsetY)
            .blur(SwipeArrowShadowBlur)
            .alpha(glowColor.alpha),
        contentScale = ContentScale.Fit,
    )
}

@Composable
private fun SwipeArrowImage(@DrawableRes res: Int) {
    Image(
        painter = painterResource(res),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit,
    )
}

@Composable
private fun ScrollableInterpolatedPlanetCardStack(
    entranceStackProgressProvider: () -> Float,
    onFirstStepChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    planets: List<PlanetCardModel> = PlanetCatalog.all,
) {
    if (planetCardStackEmptyState(planets, onFirstStepChanged, modifier)) {
        return
    }

    val density = LocalDensity.current
    val motionScope = rememberCoroutineScope()
    val stackState = rememberPlanetCardStackState(planets)
    val activeStep by rememberActiveStackStep(stackState)
    val visibleCardRange by rememberVisibleCardRange(stackState, planets)

    ReportFirstStackStep(
        stepProgress = stackState.stepProgress,
        onFirstStepChanged = onFirstStepChanged,
    )

    PlanetCardStackContainer(
        entranceStackProgressProvider = entranceStackProgressProvider,
        planets = planets,
        activeStep = activeStep,
        visibleCardRange = visibleCardRange,
        density = density,
        motionScope = motionScope,
        stackState = stackState,
        modifier = modifier,
    )
}

@Composable
private fun planetCardStackEmptyState(
    planets: List<PlanetCardModel>,
    onFirstStepChanged: (Boolean) -> Unit,
    modifier: Modifier,
): Boolean {
    if (planets.isNotEmpty()) return false

    LaunchedEffect(onFirstStepChanged) {
        onFirstStepChanged(true)
    }
    Box(modifier = modifier)
    return true
}

private data class PlanetCardStackState(
    val maxStep: Int,
    val stepProgress: Animatable<Float, AnimationVector1D>,
    val cardSettlePhase: Animatable<Float, AnimationVector1D>,
    val settledStep: MutableIntState,
    val settleDirection: MutableFloatState,
    val snapAnimationSpec: AnimationSpec<Float>,
    val settleAnimationSpec: AnimationSpec<Float>,
)

@Composable
private fun rememberPlanetCardStackState(
    planets: List<PlanetCardModel>,
): PlanetCardStackState {
    val maxStep = (planets.size - 1).coerceAtLeast(0)
    val stepProgress = remember { Animatable(0f) }
    val cardSettlePhase = remember { Animatable(1f) }
    val settledStep = remember { mutableIntStateOf(0) }
    val settleDirection = remember { mutableFloatStateOf(0f) }
    val snapAnimationSpec: AnimationSpec<Float> = remember {
        tween(
            durationMillis = 1020,
            easing = CubicBezierEasing(0.18f, 0.0f, 0.08f, 1.0f),
        )
    }
    val settleAnimationSpec: AnimationSpec<Float> = remember {
        tween(
            durationMillis = 1460,
            easing = LinearEasing,
        )
    }
    return remember(maxStep) {
        PlanetCardStackState(
            maxStep = maxStep,
            stepProgress = stepProgress,
            cardSettlePhase = cardSettlePhase,
            settledStep = settledStep,
            settleDirection = settleDirection,
            snapAnimationSpec = snapAnimationSpec,
            settleAnimationSpec = settleAnimationSpec,
        )
    }
}

@Composable
private fun rememberActiveStackStep(
    stackState: PlanetCardStackState,
): State<Int> =
    remember(stackState.maxStep) {
        derivedStateOf {
            stackState.stepProgress.value.roundToInt().coerceIn(0, stackState.maxStep)
        }
    }

@Composable
private fun rememberVisibleCardRange(
    stackState: PlanetCardStackState,
    planets: List<PlanetCardModel>,
): State<IntRange> =
    remember(stackState.maxStep) {
        derivedStateOf {
            visibleCardRange(
                stepProgress = stackState.stepProgress.value,
                lastIndex = planets.lastIndex,
            )
        }
    }

@Composable
private fun ReportFirstStackStep(
    stepProgress: Animatable<Float, AnimationVector1D>,
    onFirstStepChanged: (Boolean) -> Unit,
) {
    LaunchedEffect(stepProgress) {
        snapshotFlow { stepProgress.value <= 0.01f }
            .distinctUntilChanged()
            .collect { atFirstStep ->
                onFirstStepChanged(atFirstStep)
            }
    }
}

@Composable
private fun PlanetCardStackContainer(
    entranceStackProgressProvider: () -> Float,
    planets: List<PlanetCardModel>,
    activeStep: Int,
    visibleCardRange: IntRange,
    density: Density,
    motionScope: CoroutineScope,
    stackState: PlanetCardStackState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .graphicsLayer { clip = false }
            .planetCardStackDrag(
                entranceStackProgressProvider = entranceStackProgressProvider,
                planets = planets,
                density = density,
                motionScope = motionScope,
                stackState = stackState,
            ),
    ) {
        InterpolatedPlanetCardStack(
            stepProgressProvider = { stackState.stepProgress.value },
            activeCardIndexProvider = {
                activeStep.coerceIn(0, planets.lastIndex)
            },
            settlePhaseProvider = { stackState.cardSettlePhase.value },
            settleDirectionProvider = { stackState.settleDirection.floatValue },
            styleStackProgress = stackStyleProgress(activeStep),
            visibleCardRange = visibleCardRange,
            planets = planets,
            modifier = Modifier
                .padding(top = CardStackTopPadding)
                .graphicsLayer { clip = false },
        )
    }
}

private fun Modifier.planetCardStackDrag(
    entranceStackProgressProvider: () -> Float,
    planets: List<PlanetCardModel>,
    density: Density,
    motionScope: CoroutineScope,
    stackState: PlanetCardStackState,
): Modifier = pointerInput(stackState.maxStep, density) {
    val gestureContext = CardStackGestureContext(
        entranceStackProgressProvider = entranceStackProgressProvider,
        planets = planets,
        density = density,
        motionScope = motionScope,
        stackState = stackState,
        thresholds = CardStackGestureThresholds(
            snapDistance = 260.dp.toPx(),
            distanceThreshold = 84.dp.toPx(),
            velocityThreshold = 850.dp.toPx(),
        ),
    )

    awaitEachGesture {
        handleCardStackGesture(gestureContext)
    }
}

private data class CardStackGestureThresholds(
    val snapDistance: Float,
    val distanceThreshold: Float,
    val velocityThreshold: Float,
)

private data class CardStackGestureContext(
    val entranceStackProgressProvider: () -> Float,
    val planets: List<PlanetCardModel>,
    val density: Density,
    val motionScope: CoroutineScope,
    val stackState: PlanetCardStackState,
    val thresholds: CardStackGestureThresholds,
)

private suspend fun AwaitPointerEventScope.handleCardStackGesture(
    context: CardStackGestureContext,
) {
    val gestureStart = awaitCardStackGestureStart(
        context = context,
    ) ?: return

    val dragResult = trackCardDrag(
        down = gestureStart.down,
        velocityTracker = gestureStart.velocityTracker,
        hitCardBody = gestureStart.hitCardBody,
        gestureStartStep = gestureStart.step,
        gestureStartProgress = gestureStart.progress,
        maxStep = context.stackState.maxStep,
        snapDistance = context.thresholds.snapDistance,
        motionScope = context.motionScope,
        stepProgress = context.stackState.stepProgress,
        settleDirection = context.stackState.settleDirection,
    ) ?: return

    settleCardStackGesture(
        gestureStartStep = gestureStart.step,
        dragResult = dragResult,
        context = context,
    )
}

private data class CardStackGestureStart(
    val down: PointerInputChange,
    val velocityTracker: VelocityTracker,
    val step: Int,
    val progress: Float,
    val hitCardBody: Boolean,
)

private suspend fun AwaitPointerEventScope.awaitCardStackGestureStart(
    context: CardStackGestureContext,
): CardStackGestureStart? {
    val down = awaitFirstDown(requireUnconsumed = false)
    if (context.entranceStackProgressProvider() < 0.95f) return null

    val velocityTracker = VelocityTracker()
    velocityTracker.addPosition(down.uptimeMillis, down.position)
    return CardStackGestureStart(
        down = down,
        velocityTracker = velocityTracker,
        step = context.stackState.settledStep.intValue,
        progress = context.stackState.stepProgress.value,
        hitCardBody = hitCardBody(
            down = down,
            context = context,
        ),
    )
}

private fun AwaitPointerEventScope.hitCardBody(
    down: PointerInputChange,
    context: CardStackGestureContext,
): Boolean =
    with(context.density) {
        isCardBodyHit(
            position = down.position,
            cardWidthPx = size.width.toFloat(),
            stepProgress = context.stackState.stepProgress.value,
            lastIndex = context.planets.lastIndex,
        )
    }

private fun settleCardStackGesture(
    gestureStartStep: Int,
    dragResult: CardDragResult,
    context: CardStackGestureContext,
) {
    val targetStep = targetCardStep(
        gestureStartStep = gestureStartStep,
        latestDraggedStep = dragResult.latestDraggedStep,
        velocityY = dragResult.velocityY,
        velocityThreshold = context.thresholds.velocityThreshold,
        distanceThreshold = context.thresholds.distanceThreshold,
        snapDistance = context.thresholds.snapDistance,
        maxStep = context.stackState.maxStep,
    )
    animateCardStackToStep(
        targetStep = targetStep,
        latestDraggedStep = dragResult.latestDraggedStep,
        motionScope = context.motionScope,
        stepProgress = context.stackState.stepProgress,
        cardSettlePhase = context.stackState.cardSettlePhase,
        settledStep = context.stackState.settledStep,
        settleDirection = context.stackState.settleDirection,
        snapAnimationSpec = context.stackState.snapAnimationSpec,
        settleAnimationSpec = context.stackState.settleAnimationSpec,
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
    val dragState = CardDragState(
        hitCardBody = hitCardBody,
        gestureStartStep = gestureStartStep,
        gestureStartProgress = gestureStartProgress,
    )

    while (true) {
        val change = awaitCardDragChange(down) ?: break
        if (!updateCardDrag(
                state = dragState,
                change = change,
                velocityTracker = velocityTracker,
                maxStep = maxStep,
                snapDistance = snapDistance,
                motionScope = motionScope,
                stepProgress = stepProgress,
                settleDirection = settleDirection,
            )
        ) break
    }

    return finishCardDrag(
        state = dragState,
        velocityTracker = velocityTracker,
    )
}

private class CardDragState(
    val hitCardBody: Boolean,
    val gestureStartStep: Int,
    val gestureStartProgress: Float,
) {
    var totalDragY: Float = 0f
    var isDragging: Boolean = false
    var latestDraggedStep: Float = gestureStartProgress
    var snapJob: Job? = null
}

private suspend fun AwaitPointerEventScope.awaitCardDragChange(
    down: PointerInputChange,
): PointerInputChange? {
    val event = awaitPointerEvent()
    val change = event.changes.firstOrNull { it.id == down.id } ?: return null
    return change.takeIf { it.pressed }
}

private fun AwaitPointerEventScope.updateCardDrag(
    state: CardDragState,
    change: PointerInputChange,
    velocityTracker: VelocityTracker,
    maxStep: Int,
    snapDistance: Float,
    motionScope: CoroutineScope,
    stepProgress: Animatable<Float, AnimationVector1D>,
    settleDirection: MutableFloatState,
): Boolean {
    state.totalDragY += change.positionChange().y
    velocityTracker.addPosition(change.uptimeMillis, change.position)
    if (!state.isDragging && abs(state.totalDragY) >= viewConfiguration.touchSlop) {
        if (!state.canStart(maxStep)) {
            change.consume()
            return false
        }
        state.isDragging = true
    }

    if (state.isDragging) {
        snapCardDragProgress(
            state = state,
            change = change,
            maxStep = maxStep,
            snapDistance = snapDistance,
            motionScope = motionScope,
            stepProgress = stepProgress,
            settleDirection = settleDirection,
        )
    }
    return true
}

private fun CardDragState.canStart(maxStep: Int): Boolean =
    canStartCardDrag(
        totalDragY = totalDragY,
        hitCardBody = hitCardBody,
        gestureStartStep = gestureStartStep,
        maxStep = maxStep,
    )

private fun snapCardDragProgress(
    state: CardDragState,
    change: PointerInputChange,
    maxStep: Int,
    snapDistance: Float,
    motionScope: CoroutineScope,
    stepProgress: Animatable<Float, AnimationVector1D>,
    settleDirection: MutableFloatState,
) {
    change.consume()
    settleDirection.floatValue = 0f
    state.latestDraggedStep = draggedStep(
        gestureStartProgress = state.gestureStartProgress,
        totalDragY = state.totalDragY,
        snapDistance = snapDistance,
        maxStep = maxStep,
    )
    state.snapJob?.cancel()
    state.snapJob = motionScope.launch {
        stepProgress.snapTo(state.latestDraggedStep)
    }
}

private fun finishCardDrag(
    state: CardDragState,
    velocityTracker: VelocityTracker,
): CardDragResult? {
    state.snapJob?.cancel()
    return if (state.isDragging) {
        CardDragResult(
            latestDraggedStep = state.latestDraggedStep,
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

@Composable
private fun InterpolatedPlanetCardStack(
    stepProgressProvider: () -> Float,
    styleStackProgress: Float,
    visibleCardRange: IntRange,
    modifier: Modifier = Modifier,
    planets: List<PlanetCardModel> = PlanetCatalog.all,
    activeCardIndexProvider: () -> Int = { 0 },
    settlePhaseProvider: () -> Float = { 1f },
    settleDirectionProvider: () -> Float = { 0f },
) {
    val renderContext = PlanetStackRenderContext(
        lastIndex = planets.lastIndex,
        count = planets.size,
        boostedIndex = visibleCardRange.last.coerceAtMost(planets.lastIndex),
        styleProgress = styleStackProgress,
        stepProgressProvider = stepProgressProvider,
        activeCardIndexProvider = activeCardIndexProvider,
        settlePhaseProvider = settlePhaseProvider,
        settleDirectionProvider = settleDirectionProvider,
    )

    Box(
        modifier = modifier
            .graphicsLayer { clip = false }
            .fillMaxWidth()
            .height(CardHeight * VisibleStackSlots + CardListGap * (VisibleStackSlots - 1)),
    ) {
        visibleCardRange.forEach { index ->
            val planet = planets.getOrNull(index) ?: return@forEach
            StackedPlanetCard(
                index = index,
                planet = planet,
                context = renderContext,
            )
        }
    }
}

private class PlanetStackRenderContext(
    val lastIndex: Int,
    val count: Int,
    val boostedIndex: Int,
    val styleProgress: Float,
    val stepProgressProvider: () -> Float,
    val activeCardIndexProvider: () -> Int,
    val settlePhaseProvider: () -> Float,
    val settleDirectionProvider: () -> Float,
)

@Composable
private fun BoxScope.StackedPlanetCard(
    index: Int,
    planet: PlanetCardModel,
    context: PlanetStackRenderContext,
) {
    val density = LocalDensity.current
    key(planet.id) {
        PlanetInfoCard(
            model = planet,
            frameProvider = { stackedCardFrame(index, context) },
            modifier = Modifier
                .zIndex(
                    stackZIndex(
                        index = index,
                        boostedIndex = context.boostedIndex,
                        stackProgress = context.styleProgress,
                    ),
                )
                .align(Alignment.TopStart)
                .graphicsLayer {
                    applyStackedCardMotion(
                        index = index,
                        context = context,
                        density = density,
                    )
                },
        )
    }
}

private fun GraphicsLayerScope.applyStackedCardMotion(
    index: Int,
    context: PlanetStackRenderContext,
    density: Density,
) {
    clip = false
    val stepProgress = context.stepProgressProvider()
    val activeIndex = context.activeCardIndexProvider()
    val settleTranslationY = cardSettleTranslationY(
        index = index,
        activeIndex = activeIndex,
        phase = context.settlePhaseProvider(),
        direction = context.settleDirectionProvider(),
    )
    translationY = with(density) {
        stackedCardFrame(index, context).offsetY.toPx() + settleTranslationY.toPx()
    }
    val scale = cardFloatingScale(
        index = index,
        activeIndex = activeIndex,
        stepProgress = stepProgress,
        cardCount = context.count,
    )
    scaleX = scale
    scaleY = scale
}

private fun stackedCardFrame(
    index: Int,
    context: PlanetStackRenderContext,
): CardFrame =
    interpolateCardFrame(
        index = index,
        stepProgress = context.stepProgressProvider(),
        lastIndex = context.lastIndex,
    )

@Composable
private fun PlanetInfoCard(
    model: PlanetCardModel,
    modifier: Modifier = Modifier,
    frameProvider: () -> CardFrame,
) {
    Box(
        modifier = modifier
            .graphicsLayer { clip = false }
            .fillMaxWidth()
            .height(CardHeight),
    ) {
        PlanetCardBackground(frameProvider = frameProvider)
        PlanetCardPlanetImage(
            model = model,
            frameProvider = frameProvider,
        )
        PlanetCardHeader(
            model = model,
            frameProvider = frameProvider,
        )
        PlanetCardStats(
            model = model,
            frameProvider = frameProvider,
        )
        PlanetCardElevatedTitle(
            text = model.name,
            frameProvider = frameProvider,
        )
    }
}

@Composable
private fun BoxScope.PlanetCardPlanetImage(
    model: PlanetCardModel,
    frameProvider: () -> CardFrame,
) {
    val density = LocalDensity.current
    PlanetImage(
        model = model,
        alphaProvider = {
            val frame = frameProvider()
            frame.planetAlpha * frame.cardAlpha
        },
        translationYProvider = {
            with(density) {
                (frameProvider().planetOffsetY - PlanetImageOffsetY).toPx()
            }
        },
        modifier = Modifier
            .zIndex(1f)
            .align(Alignment.TopStart)
            .offset(
                x = 15.5.dp,
                y = PlanetImageOffsetY,
            ),
    )
}

@Composable
private fun PlanetCardStats(
    model: PlanetCardModel,
    frameProvider: () -> CardFrame,
) {
    PlanetStatsGrid(
        model = model,
        modifier = Modifier
            .zIndex(2f)
            .graphicsLayer {
                val frame = frameProvider()
                alpha = frame.statsAlpha * frame.cardAlpha
            },
    )
}

@Composable
private fun PlanetCardElevatedTitle(
    text: String,
    frameProvider: () -> CardFrame,
) {
    PlanetCardTitle(
        text = text,
        alphaProvider = {
            val frame = frameProvider()
            frame.elevatedTitleAlpha * frame.cardAlpha
        },
        modifier = Modifier.zIndex(3f),
    )
}

@Composable
private fun BoxScope.PlanetCardBackground(frameProvider: () -> CardFrame) {
    Box(
        modifier = Modifier
            .matchParentSize()
            .drawBehind {
                val frame = frameProvider()
                val radius = 20.dp.toPx()
                val strokeWidth = CardBorderWidth.toPx()
                drawRoundRect(
                    color = frame.backgroundColor.withMultipliedAlpha(frame.cardAlpha),
                    cornerRadius = CornerRadius(radius, radius),
                )
                drawRoundRect(
                    color = SolarColors.CardBorder.withMultipliedAlpha(frame.cardAlpha),
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
            .size(PlanetImageWidth, model.imageHeight)
            .graphicsLayer {
                clip = false
                translationYProvider?.let { translationY = it() }
            }
            .planetShadow(
                glowColor = model.glowColor,
                planetWidth = PlanetImageWidth,
                planetHeight = model.imageHeight,
                alphaProvider = alphaProvider ?: { 1f },
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
                .size(PlanetImageWidth, model.imageHeight),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun PlanetCardHeader(
    model: PlanetCardModel,
    frameProvider: () -> CardFrame,
) {
    FadingTextLayer(
        text = model.name,
        style = SolarTypography.CardTitle,
        alphaProvider = {
            val frame = frameProvider()
            frame.titleAlpha * frame.cardAlpha
        },
        y = CardTitleY,
        modifier = Modifier.zIndex(2f),
    )
    FadingTextLayer(
        text = model.tagline,
        style = SolarTypography.CardSubtitle,
        alphaProvider = {
            val frame = frameProvider()
            frame.taglineAlpha * frame.cardAlpha
        },
        y = 40.5.dp,
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
        y = CardTitleY,
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
                x = 137.dp - TextLayerBleed,
                y = y - TextLayerBleed,
            )
            .width(CardHeaderWidth + TextLayerBleed * 2)
            .graphicsLayer {
                clip = false
                alpha = alphaProvider()
            }
            .padding(TextLayerBleed),
    ) {
        Text(
            text = text,
            style = style,
            modifier = Modifier.width(CardHeaderWidth),
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
            .offset(x = (-0.5).dp, y = 111.5.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
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
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
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
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlanetStatIcon(iconRes = stat.iconRes)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
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
        modifier = modifier.size(20.dp),
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
            .width(CardBorderWidth)
            .height(30.dp)
            .background(SolarColors.CardBorder),
    )
}

@Composable
private fun CardDividerHorizontal() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(CardBorderWidth)
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

private fun cardStackEntranceProgress(rawProgress: Float): Float {
    val clamped = rawProgress.coerceIn(0f, 1f)
    return CardStackEasing.transform(delayedCardStackProgress(clamped))
}

private fun cardsScreenTop(progress: Float): Dp =
    interpolate(
        1100.dp - CardStackTopPadding,
        330.dp - CardStackTopPadding,
        progress,
    )

private fun interpolateCardFrame(
    index: Int,
    stepProgress: Float,
    lastIndex: Int,
): CardFrame {
    val clampedStep = stepProgress.coerceIn(0f, lastIndex.coerceAtLeast(0).toFloat())
    val fromStep = floor(clampedStep).toInt()
    val toStep = (fromStep + 1).coerceAtMost(lastIndex.coerceAtLeast(0))
    val segment = clampedStep - fromStep
    val start = cardFrameAtStep(index, fromStep, lastIndex)
    val end = cardFrameAtStep(index, toStep, lastIndex)
    return CardFrame(
        offsetY = interpolate(start.offsetY, end.offsetY, segment),
        cardAlpha = interpolate(start.cardAlpha, end.cardAlpha, segment),
        backgroundColor = interpolateColor(start.backgroundColor, end.backgroundColor, segment),
        planetAlpha = interpolate(start.planetAlpha, end.planetAlpha, segment),
        planetOffsetY = interpolate(start.planetOffsetY, end.planetOffsetY, segment),
        titleAlpha = interpolate(start.titleAlpha, end.titleAlpha, segment),
        taglineAlpha = interpolate(start.taglineAlpha, end.taglineAlpha, segment),
        statsAlpha = interpolate(start.statsAlpha, end.statsAlpha, segment),
        elevatedTitleAlpha = interpolate(start.elevatedTitleAlpha, end.elevatedTitleAlpha, segment),
    )
}

private fun cardFrameAtStep(
    index: Int,
    step: Int,
    lastIndex: Int,
): CardFrame {
    val activeStep = step.coerceIn(0, lastIndex.coerceAtLeast(0))
    val activeSlot = activeStep.coerceAtMost(StackStepFrames.lastIndex)
    val stackStartIndex = stackStartIndex(activeStep)
    val activeRelativeSlot = activeStep - stackStartIndex
    val slot = index - stackStartIndex
    return when {
        activeStep <= StackStepFrames.lastIndex && index in StackStepFrames.indices ->
            StackStepFrames[activeSlot][index]
        slot < 0 -> hiddenStackCardFrame(slot)
        slot < activeRelativeSlot -> stackedOverflowCardFrame(slot)
        slot == activeRelativeSlot -> keyframe(overflowStackOffset(slot), peekSolidFront())
        else -> futureCardFrame(
            slot = slot,
            activeSlot = activeRelativeSlot,
        )
    }
}

private fun stackedOverflowCardFrame(slot: Int): CardFrame =
    keyframe(
        offsetY = overflowStackOffset(slot),
        frame = if (slot < StackStepFrames.lastIndex) {
            StackStepFrames.last()[slot]
        } else {
            CardFrame(
                backgroundColor = SolarColors.CardBackgroundSolid,
                planetAlpha = PeekPlanetAlpha,
            )
        },
    )

private fun hiddenStackCardFrame(slot: Int): CardFrame =
    keyframe(
        offsetY = overflowStackOffset(slot),
        frame = CardFrame(
            cardAlpha = 0f,
            backgroundColor = SolarColors.CardBackgroundSolid,
            planetAlpha = PeekPlanetAlpha,
            titleAlpha = 0f,
            taglineAlpha = 0f,
            statsAlpha = 0f,
            elevatedTitleAlpha = 0f,
        ),
    )

private fun futureCardFrame(
    slot: Int,
    activeSlot: Int,
): CardFrame {
    val activeOffset = overflowStackOffset(activeSlot)
    val distanceFromActive = slot - activeSlot
    return keyframe(activeOffset + (CardHeight + CardListGap) * distanceFromActive)
}

private fun overflowStackOffset(slot: Int): Dp =
    14.dp * slot

private fun stackStartIndex(activeStep: Int): Int =
    (activeStep - MaxStackedCardsBeforeActive).coerceAtLeast(0)

private fun visibleCardRange(
    stepProgress: Float,
    lastIndex: Int,
): IntRange {
    if (lastIndex < 0) return IntRange.EMPTY

    val activeIndex = floor(stepProgress.coerceAtLeast(0f)).toInt().coerceAtMost(lastIndex)
    val startIndex = maxOf(0, activeIndex - MaxStackedCardsBeforeActive)
    val endIndex = minOf(lastIndex, activeIndex + VisibleCardsAfterActive)
    return startIndex..endIndex
}

private fun stackStyleProgress(activeStep: Int): Float =
    1f - (activeStep.coerceIn(0, StackStepFrames.lastIndex) / StackStepFrames.lastIndex.toFloat())

private fun defaultStackLayers(): List<CardFrame> {
    val pitch = CardHeight + CardListGap
    return List(VisibleStackSlots) { index -> keyframe(pitch * index) }
}

private fun keyframe(offsetY: Dp, frame: CardFrame = CardFrame()) =
    frame.copy(offsetY = offsetY)

private fun stackStep(vararg stackedFrames: CardFrame): List<CardFrame> {
    val normalPitch = CardHeight + CardListGap
    val frontIndex = stackedFrames.lastIndex
    val frontOffset = stackedFrames.last().offsetY
    return stackedFrames.toList() + List(VisibleStackSlots - stackedFrames.size) { tailIndex ->
        val index = stackedFrames.size + tailIndex
        keyframe(frontOffset + normalPitch * (index - frontIndex))
    }
}

private val StackStepFrames: List<List<CardFrame>> = listOf(
    defaultStackLayers(),
    stackStep(
        keyframe(0.dp, peekSolidElevatedTitle()),
        keyframe(14.dp, peekSolidFront()),
    ),
    stackStep(
        keyframe(0.dp, peekSolidTagline()),
        keyframe(14.dp, peekSolidStatsOnly().copy(elevatedTitleAlpha = 1f)),
        keyframe(28.dp, peekSolidFront()),
    ),
    stackStep(
        keyframe(0.dp, peekSolidTagline()),
        keyframe(14.dp, peekSolidStatsOnly().copy(elevatedTitleAlpha = 1f)),
        keyframe(28.dp, peekSolidTagline().copy(elevatedTitleAlpha = 1f)),
        keyframe(42.dp, peekSolidFront()),
    ),
    stackStep(
        keyframe(0.dp, peekSolidTagline()),
        keyframe(14.dp, peekSolidStatsOnly()),
        keyframe(28.dp, peekSolidTagline()),
        keyframe(42.dp, peekSolidTagline().copy(planetOffsetY = (-15.5).dp)),
        keyframe(56.dp, peekSolidFront()),
    ),
    stackStep(
        keyframe(0.dp, peekSolidTagline()),
        keyframe(14.dp, peekSolidStatsOnly()),
        keyframe(28.dp, peekSolidTagline()),
        keyframe(42.dp, peekSolidTagline().copy(planetOffsetY = (-15.5).dp)),
        keyframe(56.dp, peekSolidElevatedTitle()),
        keyframe(70.dp, peekSolidFront()),
    ),
    stackStep(
        keyframe(0.dp, peekSolidTagline()),
        keyframe(14.dp, peekSolidStatsOnly()),
        keyframe(28.dp, peekSolidTagline()),
        keyframe(42.dp, peekSolidTagline().copy(planetOffsetY = (-15.5).dp)),
        keyframe(56.dp, peekSolidElevatedTitle()),
        keyframe(70.dp, peekSolidPlanetOnly()),
        keyframe(84.dp, peekSolidFront()),
    ),
)

private fun peekSolidTagline() = CardFrame(
    backgroundColor = SolarColors.CardBackgroundSolid,
    planetAlpha = PeekPlanetAlpha,
    titleAlpha = 0f,
)

private fun peekSolidStatsOnly() = CardFrame(
    backgroundColor = SolarColors.CardBackgroundSolid,
    planetAlpha = PeekPlanetAlpha,
    titleAlpha = 0f,
    taglineAlpha = 0f,
)

private fun peekSolidElevatedTitle() = CardFrame(
    backgroundColor = SolarColors.CardBackgroundSolid,
    planetAlpha = PeekPlanetAlpha,
    elevatedTitleAlpha = 1f,
)

private fun peekSolidPlanetOnly() = CardFrame(
    backgroundColor = SolarColors.CardBackgroundSolid,
    planetAlpha = PeekPlanetAlpha,
)

private fun peekSolidFront() = CardFrame(backgroundColor = SolarColors.CardBackgroundSolid)

private fun Density.isCardBodyHit(
    position: Offset,
    cardWidthPx: Float,
    stepProgress: Float,
    lastIndex: Int,
): Boolean {
    if (position.x !in 0f..cardWidthPx) return false

    val topPadding = CardStackTopPadding.toPx()
    val cardHeight = CardHeight.toPx()
    return visibleCardRange(
        stepProgress = stepProgress,
        lastIndex = lastIndex,
    ).any { index ->
        val top = topPadding + interpolateCardFrame(
            index = index,
            stepProgress = stepProgress,
            lastIndex = lastIndex,
        ).offsetY.toPx()
        position.y in top..(top + cardHeight)
    }
}

private fun stackZIndex(
    index: Int,
    boostedIndex: Int,
    stackProgress: Float,
): Float {
    val stackedAmount = 1f - stackProgress.coerceIn(0f, 1f)
    val lastCardBoost = if (index == boostedIndex && stackedAmount > 0.82f) 100f else 0f
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
    return 5.dp * direction * weight * followThrough
}

private fun cardFloatingScale(
    index: Int,
    activeIndex: Int,
    stepProgress: Float,
    cardCount: Int,
): Float {
    if (cardCount <= 1) return 1f

    val clampedStep = stepProgress.coerceIn(0f, (cardCount - 1).toFloat())
    val travelAmount = 1f - (abs(clampedStep - clampedStep.roundToInt()) * 2f).coerceIn(0f, 1f)
    if (travelAmount == 0f) return 1f

    val activeDistance = abs(index - activeIndex)
    val scaleAmount = when (activeDistance) {
        0 -> 0.004f
        1 -> 0.0017f
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
): Modifier = drawBehind {
    val discDiameterPx = discDiameterProvider().toPx()
    val center = Offset(
        x = EarthShadowBleed.toPx() + discDiameterPx / 2f,
        y = EarthShadowBleed.toPx() + discDiameterPx / 2f,
    )
    drawEarthShadow(
        discDiameterPx = discDiameterPx,
        shadowAlpha = shadowAlphaProvider(),
        blurPx = 50.dp.toPx(),
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
    val offsetY = (-12).dp.toPx()
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
    alphaProvider: () -> Float = { 1f },
): Modifier = drawBehind {
    val glowAlpha = alphaProvider().coerceIn(0f, 1f)
    val blurPx = 100.dp.toPx()
    val planetRadiusPx = minOf(planetWidth.toPx(), planetHeight.toPx()) / 2f
    val glowRadius = planetRadiusPx + blurPx
    val center = Offset(size.width / 2f, size.height / 2f)
    val edgeStop = planetRadiusPx / glowRadius

    drawCircle(
        brush = Brush.radialGradient(
            colorStops = arrayOf(
                0f to Color.Transparent,
                (edgeStop * 0.72f) to Color.Transparent,
                (edgeStop * 0.92f) to glowColor.copy(alpha = glowColor.alpha * 0.55f * glowAlpha),
                (edgeStop * 1.18f) to glowColor.copy(alpha = glowColor.alpha * 0.28f * glowAlpha),
                (edgeStop * 1.55f) to glowColor.copy(alpha = glowColor.alpha * 0.08f * glowAlpha),
                1f to Color.Transparent,
            ),
            center = center,
            radius = glowRadius,
        ),
        radius = glowRadius,
        center = center,
    )
}

private fun Color.withMultipliedAlpha(alphaMultiplier: Float): Color =
    copy(alpha = alpha * alphaMultiplier.coerceIn(0f, 1f))

private fun Modifier.arrowDropShadow(glowColor: Color): Modifier = drawBehind {
    val blurPx = SwipeArrowShadowBlur.toPx()
    val offsetY = SwipeArrowShadowOffsetY.toPx()
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
