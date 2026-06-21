# Solar System

An interactive Android app that explores our solar system starting on Earth and swiping through beautifully animated planet cards.

## Demo



https://github.com/user-attachments/assets/dcac92ea-2894-4e77-bf5a-449716326d94



## Tech Stack

- **Kotlin** + **Jetpack Compose**
- **Compose Animation** & **Foundation** (gestures, motion)

## Animation & Performance

Motion is driven by **`Animatable`** and **`graphicsLayer`** not by recomposing the UI on every frame.

- Progress values are passed as **lambda providers** (`() -> Float`) so composables read motion at draw time without triggering recomposition
- Gestures update **`Animatable`** directly; the composition tree stays stable while frames animate
- Card stack uses **interpolated keyframes** and **derived state** only where needed (e.g. visible card range)

The result: smooth screen transitions, Earth parallax, stacked planet cards, and swipe hints with **zero recomposition during animation**.

## Project Structure

```
app/src/main/java/com/solarsystem/
├── MainActivity.kt          # Entry point
├── ui/                      # Screens & composables
├── model/                   # Planet data
├── motion/                  # Keyframes & interpolation
├── gesture/                 # Touch handling
└── util/                    # Draw helpers
```

## Getting Started

1. Open the project in **Android Studio**
2. Run on a device or emulator (min SDK 24)
3. Swipe up from the Earth hero to explore the planets
