package com.example.bitacoraelectronica.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF00875A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1F7E5),
    onPrimaryContainer = Color(0xFF002113),
    secondary = Color(0xFF0065FF),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDCE8FF),
    onSecondaryContainer = Color(0xFF001C3B),
    tertiary = Color(0xFFFF991F),
    background = Color(0xFFF4F6F8),
    onBackground = Color(0xFF172B4D),
    surface = Color.White,
    onSurface = Color(0xFF172B4D),
    surfaceVariant = Color(0xFFEBECF0),
    onSurfaceVariant = Color(0xFF5E6C84),
    error = Color(0xFFDE350B)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF57D9A3),
    onPrimary = Color(0xFF003823),
    primaryContainer = Color(0xFF005235),
    onPrimaryContainer = Color(0xFF79F2C0),
    secondary = Color(0xFF4C9AFF),
    onSecondary = Color(0xFF003062),
    secondaryContainer = Color(0xFF00468B),
    onSecondaryContainer = Color(0xFFDCE8FF),
    tertiary = Color(0xFFFFAB00),
    background = Color(0xFF0D1117),
    onBackground = Color(0xFFE6EDF3),
    surface = Color(0xFF161B22),
    onSurface = Color(0xFFE6EDF3),
    surfaceVariant = Color(0xFF21262D),
    onSurfaceVariant = Color(0xFF8B949E),
    error = Color(0xFFFF5630)
)

@Composable
fun BitacoraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    fontSizePreference: String = "Media",
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val fontScaleMultiplier = when (fontSizePreference) {
        "Pequeña" -> 0.90f
        "Grande" -> 1.15f
        else -> 1.0f
    }

    val currentDensity = LocalDensity.current
    val customDensity = Density(
        density = currentDensity.density,
        fontScale = fontScaleMultiplier
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalDensity provides customDensity) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}