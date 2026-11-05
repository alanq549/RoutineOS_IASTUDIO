package com.alan.routineos.feature.account

import com.alan.routineos.feature.account.model.*

data class AccountUiState(
    val isLoading: Boolean = false,
    val profile: UserProfile = UserProfile(
        name = "Alan Geovani",
        email = "alanqFF@gmail.com",
        avatarUrl = null,
        plan = "Technical Premium · Obsidian",
        bio = "Ingeniería y Alto Rendimiento"
    ),
    val calendarState: GoogleCalendarState = GoogleCalendarState(),
    val storageStats: DataStorageStats = DataStorageStats(),
    val notificationPrefs: NotificationPreferences = NotificationPreferences(),
    val appearancePrefs: AppearancePreferences = AppearancePreferences(),
    val sections: List<AccountSectionModel> = emptyList(),
    val activeDialog: AccountDialogType? = null,
    val toastMessage: String? = null,
    val isSignedOut: Boolean = false,
    val version: String = "2.4.0 (Build 42)"
)
