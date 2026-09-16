package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SuperAceColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color(0xFF451A03),
    secondary = GoldSecondary,
    onSecondary = Color.White,
    tertiary = RubyRed,
    onTertiary = Color.White,
    background = CasinoBackground,
    onBackground = Color(0xFFF8FAFC),
    surface = CasinoSurface,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = CasinoCard,
    onSurfaceVariant = Color(0xFFE2E8F0)
)

@Composable
fun SuperAceTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SuperAceColorScheme,
        typography = Typography,
        content = content
    )
}
