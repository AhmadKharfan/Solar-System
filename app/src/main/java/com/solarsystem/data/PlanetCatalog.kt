package com.solarsystem.data

import com.solarsystem.R
import com.solarsystem.model.PlanetCardModel
import com.solarsystem.model.PlanetStat
import com.solarsystem.ui.theme.SolarColors
import com.solarsystem.ui.tokens.PlanetCardDimens

object PlanetCatalog {
    val saturn = PlanetCardModel(
        name = "Saturn",
        tagline = "The Ring Master",
        imageRes = R.drawable.planet_saturn,
        imageHeight = PlanetCardDimens.PlanetHeightSaturn,
        glowColor = SolarColors.PlanetGlow.Saturn,
        stats = listOf(
            PlanetStat(R.drawable.ic_weight_scale, "You Would Weigh", "70kg → 74kg"),
            PlanetStat(R.drawable.ic_sun, "One Day", "10.7 Hours"),
            PlanetStat(R.drawable.ic_temperature, "Temperature", "-178°C,", hint = "Bring a jacket"),
            PlanetStat(R.drawable.ic_alert_circle, "Additional info", "Lighter than water"),
        ),
    )

    val all = listOf(
        saturn,
        PlanetCardModel(
            name = "Mars",
            tagline = "The next colony",
            imageRes = R.drawable.planet_mars,
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
            imageRes = R.drawable.planet_mercury,
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
            imageRes = R.drawable.planet_venus,
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
            imageRes = R.drawable.planet_jupiter,
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
            imageRes = R.drawable.planet_uranus,
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
            imageRes = R.drawable.planet_neptune,
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
