package com.alan.routineos.core.designsystem.color

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class RoutineColorScheme(
    val background: Color,
    val surface1: Color,
    val surface2: Color,
    val surface3: Color,
    val border: Color,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val error: Color,
    val onPrimary: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val surfaceContainerLowest: Color = Color(0xFF0A0E15),
    val surfaceContainerHighest: Color = Color(0xFF31353D),
    val outlineVariant: Color = Color(0xFF3C4A42),
    val outline: Color = Color(0xFF85948B),
    val roleEvent: Color = Color(0xFF5AF0B3),
    val roleTask: Color = Color(0xFFC0C1FF),
    val roleReminder: Color = Color(0xFFFFCF96),
    val warning: Color = Color(0xFFFFA929)
)

val DarkRoutineColorScheme = RoutineColorScheme(
    background = Color(0xFF0F131B), // Stitch Canvas #0f131b
    surface1 = Color(0xFF181C23), // Stitch surface-container-low #181c23
    surface2 = Color(0xFF1C2027), // Stitch surface-container #1c2027
    surface3 = Color(0xFF262A32), // Stitch surface-container-high #262a32
    border = Color(0xFF3C4A42), // Stitch outline-variant #3c4a42
    primary = Color(0xFF5AF0B3), // Stitch primary #5af0b3
    secondary = Color(0xFFC0C1FF), // Stitch secondary #c0c1ff
    tertiary = Color(0xFFFFCF96), // Stitch tertiary #ffcf96
    error = Color(0xFFFFB4AB),
    onPrimary = Color(0xFF003825),
    onSurface = Color(0xFFDFE2ED), // Stitch on-surface #dfe2ed
    onSurfaceVariant = Color(0xFFBBCAC0), // Stitch on-surface-variant #bbcac0
    surfaceContainerLowest = Color(0xFF0A0E15), // Stitch surface-container-lowest #0a0e15
    surfaceContainerHighest = Color(0xFF31353D), // Stitch surface-container-highest #31353d
    outlineVariant = Color(0xFF3C4A42), // Stitch outline-variant #3c4a42
    outline = Color(0xFF85948B), // Stitch outline #85948b
    roleEvent = Color(0xFF5AF0B3),
    roleTask = Color(0xFFC0C1FF),
    roleReminder = Color(0xFFFFCF96),
    warning = Color(0xFFFFA929)
)

val LocalRoutineColors = staticCompositionLocalOf {
    DarkRoutineColorScheme
}
