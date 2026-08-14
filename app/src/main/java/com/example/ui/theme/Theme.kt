package com.example.ui.theme

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

private val DarkColorScheme =
  darkColorScheme(
    primary = EmeraldPrimaryDarkTheme,
    onPrimary = Color(0xFF003822),
    primaryContainer = EmeraldPrimary,
    onPrimaryContainer = Color(0xFFD1E7DD),
    secondary = GoldAccentDarkTheme,
    onSecondary = Color(0xFF452B00),
    secondaryContainer = Color(0xFF633F00),
    onSecondaryContainer = GoldContainer,
    tertiary = InfoBlueContainer,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceCardDark,
    onBackground = Color(0xFFE2E8F0),
    onSurface = Color(0xFFF1F5F9),
  )

private val LightColorScheme =
  lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = Color(0xFF002113),
    secondary = GoldAccent,
    onSecondary = Color.White,
    secondaryContainer = GoldContainer,
    onSecondaryContainer = OnGoldContainer,
    tertiary = InfoBlue,
    background = Color(0xFFF8FAFC),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF1F5F9),
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF1E293B),
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
