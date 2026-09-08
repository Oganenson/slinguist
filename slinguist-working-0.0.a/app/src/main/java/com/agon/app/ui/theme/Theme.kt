package com.agon.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val InkLight = Color(0xFF101116)
private val ErrorContainerLight = Color(0xFFFFE1E5)
private val OnErrorContainerLight = Color(0xFF6B0A1C)
private val ErrorContainerDark = Color(0xFF7A1226)
private val OnErrorContainerDark = Color(0xFFFFDADE)
private val OnErrorDark = Color(0xFF5A0715)
private val ScrimBlack = Color(0xFF000000)

private val LightColors = lightColorScheme(
    primary = VioletPrimary,
    onPrimary = WhitePure,
    primaryContainer = VioletSoft,
    onPrimaryContainer = VioletDeep,
    inversePrimary = VioletLight,

    secondary = SkyBlue,
    onSecondary = WhitePure,
    secondaryContainer = SkyBlueSoft,
    onSecondaryContainer = SkyDeep,

    tertiary = VioletMid,
    onTertiary = WhitePure,
    tertiaryContainer = VioletSoft,
    onTertiaryContainer = VioletDeep,

    background = GrayBackground,
    onBackground = InkLight,
    surface = WhitePure,
    onSurface = InkLight,
    surfaceVariant = GrayVariant,
    onSurfaceVariant = GrayText,
    surfaceTint = VioletPrimary,

    surfaceContainerLowest = WhitePure,
    surfaceContainerLow = GraySurfaceLow,
    surfaceContainer = GraySurfaceHigh,
    surfaceContainerHigh = WhitePure,
    surfaceContainerHighest = WhitePure,
    surfaceBright = WhitePure,
    surfaceDim = GraySurfaceLow,

    outline = GrayOutline,
    outlineVariant = GrayVariant,

    error = ErrorRed,
    onError = WhitePure,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,

    inverseSurface = BlackSurface,
    inverseOnSurface = OffWhite,
    scrim = ScrimBlack,
)

private val DarkColors = darkColorScheme(
    primary = VioletLight,
    onPrimary = VioletDeep,
    primaryContainer = VioletContainerDark,
    onPrimaryContainer = VioletSoft,
    inversePrimary = VioletPrimary,

    secondary = SkyBlueLight,
    onSecondary = SkyDeep,
    secondaryContainer = SkyContainerDark,
    onSecondaryContainer = SkyBlueSoft,

    tertiary = VioletMid,
    onTertiary = WhitePure,
    tertiaryContainer = VioletContainerDark,
    onTertiaryContainer = VioletSoft,

    background = BlackBackground,
    onBackground = OffWhite,
    surface = BlackSurface,
    onSurface = OffWhite,
    surfaceVariant = DarkGrayVariant,
    onSurfaceVariant = DarkGrayText,
    surfaceTint = VioletLight,

    surfaceContainerLowest = BlackBackground,
    surfaceContainerLow = BlackSurfaceLow,
    surfaceContainer = BlackSurface,
    surfaceContainerHigh = BlackSurfaceHigh,
    surfaceContainerHighest = DarkGrayVariant,
    surfaceBright = BlackSurfaceHigh,
    surfaceDim = BlackBackground,

    outline = DarkGrayOutline,
    outlineVariant = DarkGrayVariant,

    error = ErrorRedDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,

    inverseSurface = OffWhite,
    inverseOnSurface = BlackSurface,
    scrim = ScrimBlack,
)

@Composable
fun SLinguistTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity
            if (activity != null) {
                val controller = WindowCompat.getInsetsController(activity.window, view)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SLinguistTypography,
        shapes = SLinguistShapes,
        content = content,
    )
}
