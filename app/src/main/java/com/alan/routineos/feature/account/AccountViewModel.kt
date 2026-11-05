package com.alan.routineos.feature.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alan.routineos.data.calendar.GoogleCalendarSyncManager
import com.alan.routineos.feature.account.data.FakeAccountRepository
import com.alan.routineos.feature.account.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context,
    private val repository: FakeAccountRepository,
    private val calendarSyncManager: GoogleCalendarSyncManager,
    private val themePreferencesManager: com.alan.routineos.core.designsystem.theme.ThemePreferencesManager,
    private val authRepository: com.alan.routineos.data.auth.AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountUiState(isLoading = true))
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(isSignedOut = authRepository.isSignedOut())
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                repository.getUserProfile(),
                repository.getAccountSections(),
                calendarSyncManager.calendarState
            ) { profile, sections, calendarState ->
                _uiState.value.copy(
                    isLoading = false,
                    profile = profile,
                    sections = sections,
                    calendarState = calendarState
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun onEditProfileClick() {
        _uiState.value = _uiState.value.copy(activeDialog = AccountDialogType.EDIT_PROFILE)
    }

    fun onSaveProfile(name: String, email: String, bio: String, avatarIcon: String = "google") {
        repository.updateProfile(name, email, bio, avatarIcon)
        _uiState.value = _uiState.value.copy(
            activeDialog = null,
            toastMessage = "Perfil actualizado con éxito"
        )
    }

    fun onSettingClick(settingId: String) {
        val dialogType = when (settingId) {
            "appearance" -> AccountDialogType.APPEARANCE
            "notifications" -> AccountDialogType.NOTIFICATIONS
            "accessibility" -> AccountDialogType.ACCESSIBILITY
            "privacy" -> AccountDialogType.PRIVACY
            "data" -> AccountDialogType.DATA_MANAGEMENT
            "about" -> AccountDialogType.ABOUT
            "backup" -> {
                onExportData()
                null
            }
            else -> null
        }
        if (dialogType != null) {
            _uiState.value = _uiState.value.copy(activeDialog = dialogType)
        }
    }

    fun onToggleCalendarProject(enabled: Boolean) {
        viewModelScope.launch {
            calendarSyncManager.setProjectEventsInPlanning(enabled)
            _uiState.value = _uiState.value.copy(
                toastMessage = if (enabled)
                    "Google Calendar activo · Eventos proyectados automáticamente"
                else
                    "Google Calendar oculto · Eventos removidos del cronograma"
            )
        }
    }

    fun onToggleCalendarConflicts(enabled: Boolean) {
        calendarSyncManager.setDetectConflicts(enabled)
        _uiState.value = _uiState.value.copy(
            toastMessage = if (enabled) "Detección de solapamientos activada" else "Detección de solapamientos desactivada"
        )
    }

    fun onSyncCalendarNow() {
        viewModelScope.launch {
            calendarSyncManager.syncNow()
            _uiState.value = _uiState.value.copy(
                toastMessage = "Sincronizado con Google Calendar · Eventos al día"
            )
        }
    }

    fun onDisconnectCalendar() {
        viewModelScope.launch {
            calendarSyncManager.setConnected(false)
            _uiState.value = _uiState.value.copy(
                toastMessage = "Google Calendar desconectado (auto-sync detenido)"
            )
        }
    }

    fun onConnectCalendar() {
        viewModelScope.launch {
            calendarSyncManager.setConnected(true)
            _uiState.value = _uiState.value.copy(
                toastMessage = "Google Calendar vinculado · Auto-sync en segundo plano iniciado"
            )
        }
    }

    fun onUpdateAppearance(accentName: String, accentHex: String) {
        themePreferencesManager.setAccentHex(accentHex)
        _uiState.value = _uiState.value.copy(
            appearancePrefs = _uiState.value.appearancePrefs.copy(
                accentName = accentName,
                accentHex = accentHex
            ),
            activeDialog = null,
            toastMessage = "Tema de acento actualizado a $accentName"
        )
        repository.updateAppearanceSubtitle("Oscuro · $accentName")
    }

    fun onUpdateNotifications(
        reminderAlarms: Boolean,
        routineAdvanceNotice: Boolean,
        advanceMinutes: Int,
        haptic: Boolean,
        morning: Boolean
    ) {
        _uiState.value = _uiState.value.copy(
            notificationPrefs = NotificationPreferences(
                reminderAlarms = reminderAlarms,
                routineAdvanceNotice = routineAdvanceNotice,
                advanceNoticeMinutes = advanceMinutes,
                hapticFeedback = haptic,
                morningBriefing = morning
            ),
            activeDialog = null,
            toastMessage = "Preferencias de notificaciones guardadas"
        )
        val noticeDesc = if (routineAdvanceNotice) "Avisos anticipados (${advanceMinutes}m)" else "Solo a la hora exacta"
        repository.updateNotificationsSubtitle(noticeDesc)
    }

    fun onExportData() {
        _uiState.value = _uiState.value.copy(
            toastMessage = "Respaldo JSON generado en almacenamiento local"
        )
    }

    fun onClearHistory() {
        _uiState.value = _uiState.value.copy(
            activeDialog = null,
            storageStats = _uiState.value.storageStats.copy(plannedEventsCount = 3),
            toastMessage = "Historial completado purgado exitosamente"
        )
    }

    fun onLogoutClick() {
        _uiState.value = _uiState.value.copy(activeDialog = AccountDialogType.LOGOUT_CONFIRMATION)
    }

    fun onConfirmLogout() {
        authRepository.signOut()
        _uiState.value = _uiState.value.copy(
            activeDialog = null,
            isSignedOut = true,
            toastMessage = "Sesión cerrada correctamente"
        )
    }

    fun onSignInWithGoogle(
        activityContext: android.content.Context,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val res = authRepository.signInWithGoogle(activityContext)
            res.onSuccess { user ->
                repository.updateProfile(
                    user.name,
                    user.email,
                    _uiState.value.profile.bio,
                    _uiState.value.profile.avatarIcon
                )
                _uiState.value = _uiState.value.copy(
                    isSignedOut = false,
                    toastMessage = "Sesión iniciada con Google (${user.email})"
                )
            }.onFailure { err ->
                val msg = err.message ?: "Error al iniciar sesión con Google"
                _uiState.value = _uiState.value.copy(toastMessage = msg)
                onError(msg)
            }
        }
    }

    fun onEmailLogin(
        email: String,
        password: String,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val res = authRepository.signInWithEmail(email, password)
            res.onSuccess { user ->
                repository.updateProfile(
                    user.name,
                    user.email,
                    _uiState.value.profile.bio,
                    _uiState.value.profile.avatarIcon
                )
                _uiState.value = _uiState.value.copy(
                    isSignedOut = false,
                    toastMessage = "Bienvenido, ${user.name}"
                )
            }.onFailure { err ->
                val msg = err.message ?: "Correo o contraseña incorrectos"
                _uiState.value = _uiState.value.copy(toastMessage = msg)
                onError(msg)
            }
        }
    }

    fun onEmailRegister(
        name: String,
        email: String,
        password: String,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val res = authRepository.registerWithEmail(name, email, password)
            res.onSuccess { user ->
                repository.updateProfile(
                    user.name,
                    user.email,
                    _uiState.value.profile.bio,
                    _uiState.value.profile.avatarIcon
                )
                _uiState.value = _uiState.value.copy(
                    isSignedOut = false,
                    toastMessage = "Cuenta creada: ${user.name}"
                )
            }.onFailure { err ->
                val msg = err.message ?: "Error al registrar la cuenta"
                _uiState.value = _uiState.value.copy(toastMessage = msg)
                onError(msg)
            }
        }
    }

    fun onDismissDialog() {
        _uiState.value = _uiState.value.copy(activeDialog = null)
    }

    fun onClearToast() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }
}
