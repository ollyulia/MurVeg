package com.example.murveg.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import com.example.murveg.R

@Composable
private fun murvegLightColorScheme() = lightColorScheme(
    primary = colorResource(R.color.md_theme_light_primary),
    onPrimary = colorResource(R.color.md_theme_light_onPrimary),
    background = colorResource(R.color.md_theme_light_background),
    onBackground = colorResource(R.color.md_theme_light_onBackground),
    surface = colorResource(R.color.md_theme_light_surface),
    onSurface = colorResource(R.color.md_theme_light_onSurface),
    error = colorResource(R.color.md_theme_light_error),
)

@Composable
private fun murvegDarkColorScheme() = darkColorScheme(
    primary = colorResource(R.color.md_theme_dark_primary),
    onPrimary = colorResource(R.color.md_theme_dark_onPrimary),
    background = colorResource(R.color.md_theme_dark_background),
    onBackground = colorResource(R.color.md_theme_dark_onBackground),
    surface = colorResource(R.color.md_theme_dark_surface),
    onSurface = colorResource(R.color.md_theme_dark_onSurface),
    error = colorResource(R.color.md_theme_dark_error),
)

@Composable
fun MurVegTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme = when {
        // динамические цвета Android 12+ (можно выключить, если не нужно)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> murvegDarkColorScheme()
        else -> murvegLightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}