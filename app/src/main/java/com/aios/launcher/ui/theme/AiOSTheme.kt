package com.aios.launcher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AiOSLightColors = lightColorScheme(
    primary = Color(0xFF7779E8),
    onPrimary = Color.White,
    secondary = Color(0xFF9A8DEB),
    background = Color(0xFFF2F1F8),
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF242438),
    onSurface = Color(0xFF242438)
)

private val AiOSDarkColors = darkColorScheme(
    primary = Color(0xFFA5A4FF),
    secondary = Color(0xFFC0B5FF),
    background = Color(0xFF171722),
    surface = Color(0xFF252534),
    onBackground = Color(0xFFF4F3FF),
    onSurface = Color(0xFFF4F3FF)
)

@Composable
fun AiOSTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) AiOSDarkColors else AiOSLightColors,
        content = content
    )
}
