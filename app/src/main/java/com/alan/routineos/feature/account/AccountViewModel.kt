package com.alan.routineos.feature.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alan.routineos.feature.account.data.FakeAccountRepository
import com.alan.routineos.feature.account.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class AccountViewModel(
    private val repository: FakeAccountRepository = FakeAccountRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountUiState(isLoading = true))
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                repository.getUserProfile(),
                repository.getAccountSections()
            ) { profile, sections ->
                _uiState.value.copy(
                    isLoading = false,
                    profile = profile,
                    sections = sections
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun onEditProfileClick() {
        _uiState.value = _uiState.value.copy(activeDialog = AccountDialogType.EDIT_PROFILE)
    }

    fun onSaveProfile(name: String, email: String, bio: String) {
        repository.updateProfile(name, email, bio)
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
        val updated = _uiState.value.calendarState.copy(projectEventsInPlanning = enabled)
        _uiState.value = _uiState.value.copy(
            calendarState = updated,
            toastMessage = if (enabled) "Eventos de Google Calendar proyectados en Planning" else "Eventos de Google Calendar ocultos de Planning"
        )
    }

    fun onToggleCalendarConflicts(enabled: Boolean) {
        val updated = _uiState.value.calendarState.copy(detectConflicts = enabled)
        _uiState.value = _uiState.value.copy(
            calendarState = updated,
            toastMessage = if (enabled) "Detección de solapamientos activada" else "Detección de solapamientos desactivada"
        )
    }

    fun onSyncCalendarNow() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                calendarState = _uiState.value.calendarState.copy(isSyncing = true)
            )
            delay(1200) // Simular handshake y obtención de eventos OAuth
            _uiState.value = _uiState.value.copy(
                calendarState = _uiState.value.calendarState.copy(
                    isSyncing = false,
                    lastSyncTime = "Hace un momento",
                    syncedEventsCount = 4
                ),
                toastMessage = "Google Calendar sincronizado correctamente (4 eventos obtenidos)"
            )
        }
    }

    fun onDisconnectCalendar() {
        _uiState.value = _uiState.value.copy(
            calendarState = _uiState.value.calendarState.copy(isConnected = false),
            toastMessage = "Google Calendar desconectado"
        )
    }

    fun onConnectCalendar() {
        _uiState.value = _uiState.value.copy(
            calendarState = _uiState.value.calendarState.copy(isConnected = true, lastSyncTime = "Recién conectado"),
            toastMessage = "Google Calendar vinculado exitosamente"
        )
    }

    fun onUpdateAppearance(accentName: String, accentHex: String) {
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
        _uiState.value = _uiState.value.copy(
            activeDialog = null,
            toastMessage = "Sesión cerrada correctamente"
        )
    }

    fun onDismissDialog() {
        _uiState.value = _uiState.value.copy(activeDialog = null)
    }

    fun onClearToast() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }
}
