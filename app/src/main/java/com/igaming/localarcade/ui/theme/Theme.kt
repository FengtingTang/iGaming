package com.igaming.localarcade.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = Color(0xFF006D43),
    secondary = Color(0xFF51635A),
    tertiary = Color(0xFF3D6374),
    background = Color(0xFFFBFCF7),
    surface = Color(0xFFFBFCF7),
    surfaceVariant = Color(0xFFDCE5DD)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF65DCA3),
    secondary = Color(0xFFB8CCC0),
    tertiary = Color(0xFFA5CFE0),
    background = Color(0xFF101411),
    surface = Color(0xFF101411),
    surfaceVariant = Color(0xFF3F4942)
)

@Composable
fun LocalArcadeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ArcadeTypography,
        content = content
    )
}
