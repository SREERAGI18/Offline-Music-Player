package com.example.offlinemusicplayer.ui.theme

import androidx.compose.ui.graphics.Color

// Define the hex values as private constants to avoid MagicNumber warnings
private const val COLOR_PRIMARY_BLUE = 0xFF4285F4L
private const val COLOR_SECONDARY_BLUE_LIGHT = 0xFF8AB4F8L
private const val COLOR_PRIMARY_BLUE_DARK = 0xFF0D47A1L
private const val COLOR_DARK_SURFACE = 0xFF121212L
private const val COLOR_DARK_ON_SURFACE = 0xFFE0E0E0L
private const val COLOR_DARK_PRIMARY_VARIANT = 0xFF3700B3L
private const val COLOR_LIGHT_GREY = 0xFFF0F0F0L
private const val COLOR_DARK_GREY = 0xFF2E2E2EL

val PrimaryBlue = Color(COLOR_PRIMARY_BLUE)
val SecondaryBlueLight = Color(COLOR_SECONDARY_BLUE_LIGHT)
val PrimaryBlueDark = Color(COLOR_PRIMARY_BLUE_DARK)

val DarkSurface = Color(COLOR_DARK_SURFACE)
val DarkOnSurface = Color(COLOR_DARK_ON_SURFACE)
val DarkPrimaryVariant = Color(COLOR_DARK_PRIMARY_VARIANT)

val Purple40 = PrimaryBlue
val PurpleGrey40 = DarkOnSurface
val Pink40 = DarkPrimaryVariant

val LightGrey = Color(COLOR_LIGHT_GREY)
val DarkGrey = Color(COLOR_DARK_GREY)
