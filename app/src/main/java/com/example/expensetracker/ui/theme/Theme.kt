package com.example.expensetracker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = Primary, onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer, onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary, onSecondary = OnSecondary, secondaryContainer = SecondaryContainer,
    tertiary = Tertiary, onTertiary = OnTertiary, tertiaryContainer = TertiaryContainer,
    surface = Surface, onSurface = OnSurface,
    surfaceVariant = SurfaceVariant, onSurfaceVariant = OnSurfaceVariant,
    outline = Outline
)

// For dark: either define a dark palette or rely on dynamicColor on Android 12+.
// Temporarily you can reuse LightColors or create proper dark values.

@Composable
fun ExpenseTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme =
        if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            if (darkTheme) dynamicDarkColorScheme(LocalContext.current) else dynamicLightColorScheme(LocalContext.current)
        else
            if (darkTheme) /* DarkColors */ LightColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Use surface for bars to match app background
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
            window.navigationBarColor = Color.White.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightNavigationBars = !darkTheme
        }
    }
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

