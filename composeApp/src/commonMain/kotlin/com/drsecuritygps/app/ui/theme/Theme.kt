package com.drsecuritygps.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = AppPrimary,
    onPrimary = AppTextOnPrimary,
    primaryContainer = AppPrimaryLight,
    onPrimaryContainer = AppText,
    secondary = AppPrimaryAlt,
    onSecondary = AppTextOnPrimary,
    secondaryContainer = AppPrimarySoft,
    onSecondaryContainer = AppText,
    tertiary = AppPurple,
    onTertiary = AppTextOnPrimary,
    tertiaryContainer = AppPurpleLight,
    onTertiaryContainer = AppText,
    background = AppBg,
    onBackground = AppText,
    surface = AppCard,
    onSurface = AppText,
    surfaceVariant = AppSurfaceSoft,
    onSurfaceVariant = AppMuted,
    outline = AppBorderLight,
    outlineVariant = AppDivider,
    error = AppDanger,
    onError = AppTextOnPrimary,
    errorContainer = AppDangerLight,
    onErrorContainer = AppText,
)

@Composable
fun DrSecurityTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content,
    )
}
