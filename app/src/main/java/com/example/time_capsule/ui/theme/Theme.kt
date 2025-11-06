package com.example.timecapsule.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val darkScheme = darkColorScheme(
    primary = Main,
    secondary = Accent,
    background = Bg,
    surface = Main,
    onPrimary = Color.White,
    onSecondary = Color(0xFF312E2D),
    onBackground = Color.White,
    onSurface = Color.White,
)

@Composable
fun TimeCapsuleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkScheme,
        typography = Typography(),
        content = content
    )
}
