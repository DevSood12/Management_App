package com.example.login_page.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppColorScheme = lightColorScheme(
    primary = Color(0xFF6366F1),        // Indigo
    secondary = Color(0xFF22C55E),      // Green
    tertiary = Color(0xFFF472B6),       // Pink

    background = Color(0xFFF8FAFC),
    surface = Color(0xFFFFFFFF),

    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF020617),
    onSurface = Color(0xFF020617)
)

@Composable
fun Login_PageTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}