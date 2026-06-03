package com.guidovezzoni.sfta.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DashboardColorScheme = darkColorScheme(
    primary = StarkGold,
    background = DashboardBackground,
    surface = DashboardSurface,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

@Composable
fun StarkFutureTechnicalAssessmentTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DashboardColorScheme,
        typography = Typography,
        content = content,
    )
}
