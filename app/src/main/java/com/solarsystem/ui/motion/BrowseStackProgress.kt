package com.solarsystem.ui.motion

fun browseProgressToStackProgress(browseProgress: Float): Float {
    val clamped = browseProgress.coerceIn(0f, 1f)
    if (clamped >= 1f) return 0f
    if (clamped <= 0f) return 1f
    return 1f - clamped
}
