package com.example.time_capsule.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkScheme = darkColorScheme(
    primary = SurfaceMain,
    secondary = Accent,
    background = Bg,
    surface = SurfaceMain,
    onPrimary = TextPrimary,
    onSecondary = Color(0xFF312E2D),
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun TimeCapsuleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkScheme,
        typography = Typography(),
        content = content
    )
}
