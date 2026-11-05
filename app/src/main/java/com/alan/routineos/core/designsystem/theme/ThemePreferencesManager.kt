package com.alan.routineos.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemePreferencesManager @Inject constructor() {
    private val _accentColor = MutableStateFlow(Color(0xFF5AF0B3))
    val accentColor: StateFlow<Color> = _accentColor.asStateFlow()

    private val _highContrast = MutableStateFlow(false)
    val highContrast: StateFlow<Boolean> = _highContrast.asStateFlow()

    fun setAccentHex(hex: String) {
        val parsed = try {
            Color(android.graphics.Color.parseColor(hex))
        } catch (_: Exception) {
            Color(0xFF5AF0B3)
        }
        _accentColor.value = parsed
    }

    fun setHighContrast(enabled: Boolean) {
        _highContrast.value = enabled
    }
}
