package com.lyrisync.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val ColorScheme.shadow: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) Color.White else Color.Gray

private val DarkColorScheme = darkColorScheme(
    primary = SecondaryBlueLight,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color.Black,
    onBackground = Color.White,
    onPrimary = Color.White,
    surfaceVariant = DarkGrey
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = SecondaryBlueLight,
    tertiary = Pink40,
    background = Color.White,
    onBackground = Color.Black,
    onPrimary = Color.White,
    surfaceVariant = LightGrey
)

@Composable
fun OfflineMusicPlayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}