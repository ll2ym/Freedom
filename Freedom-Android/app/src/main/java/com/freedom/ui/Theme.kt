package com.freedom.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF4A90E2),
    onPrimary = Color.White,
    secondary = Color(0xFF50E3C2),
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    onSurface = Color.Black
)

@Composable
fun FreedomTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography(),
        content = content
    )
}
