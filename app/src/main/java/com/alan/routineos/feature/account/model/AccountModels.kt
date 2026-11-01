package com.alan.routineos.feature.account.model

data class UserProfile(
    val name: String,
    val email: String,
    val avatarUrl: String?,
    val plan: String,
    val bio: String = "Ingeniería y Alto Rendimiento"
)

data class GoogleCalendarState(
    val isConnected: Boolean = true,
    val accountEmail: String = "alanqFF@gmail.com",
    val lastSyncTime: String = "Hace 2 minutos",
    val projectEventsInPlanning: Boolean = true,
    val detectConflicts: Boolean = true,
    val isSyncing: Boolean = false,
    val syncedEventsCount: Int = 3
)

data class DataStorageStats(
    val routinesCount: Int = 3,
    val plannedEventsCount: Int = 12,
    val backlogCount: Int = 4,
    val databaseVersion: String = "Room v10 (SQLite)",
    val storageSizeKb: String = "248 KB"
)

data class NotificationPreferences(
    val reminderAlarms: Boolean = true,
    val routineAdvanceNotice: Boolean = true,
    val advanceNoticeMinutes: Int = 15,
    val hapticFeedback: Boolean = true,
    val morningBriefing: Boolean = true
)

data class AppearancePreferences(
    val accentName: String = "Verde Esmeralda Neón",
    val accentHex: String = "#5AF0B3",
    val isHighContrast: Boolean = false
)

data class SettingItem(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val iconName: String,
    val type: SettingType = SettingType.NAVIGATION,
    val badge: String? = null
)

enum class SettingType {
    NAVIGATION, TOGGLE
}

data class AccountSectionModel(
    val title: String,
    val items: List<SettingItem>
)

enum class AccountDialogType {
    EDIT_PROFILE,
    CALENDAR_SETTINGS,
    APPEARANCE,
    NOTIFICATIONS,
    ACCESSIBILITY,
    PRIVACY,
    DATA_MANAGEMENT,
    ABOUT,
    LOGOUT_CONFIRMATION
}
