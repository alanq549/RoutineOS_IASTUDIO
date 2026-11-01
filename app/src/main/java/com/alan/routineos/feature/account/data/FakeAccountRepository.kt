package com.alan.routineos.feature.account.data

import com.alan.routineos.feature.account.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeAccountRepository {
    private val _userProfile = MutableStateFlow(
        UserProfile(
            name = "Alan Geovani",
            email = "alanqFF@gmail.com",
            avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCAIb2EHWh1pANMPoLx_8khf4byCRGHYFZYb4JzqkSC-2MIGwsgd0jJjnzz-ylmCmAxOjzxiBuugqK-9x-_6jyEDqxzWfQEPcX4APYJoYhUyb1r3m3lcTMag1ymDX7hBUj5_HolIk9UjgncBBdcV8-I4sV_OvtZmQXETTS5ZmPq7q7eVOimpsWmLFY76rKB8r_J7BVQOgfD8kLTd0yKWPlL_pn2dDSiZCwR9DuEbDdl4t3G7o3KXsft9k7WZFyImyGc--ddfMq3cFDI",
            plan = "Technical Premium · Obsidian",
            bio = "Ingeniería y Alto Rendimiento"
        )
    )

    private val _sections = MutableStateFlow(
        listOf(
            AccountSectionModel(
                title = "PREFERENCIAS",
                items = listOf(
                    SettingItem("appearance", "Apariencia", "Oscuro · Verde Esmeralda", "palette"),
                    SettingItem("notifications", "Notificaciones y Alarmas", "Avisos anticipados (15 min)", "notifications"),
                    SettingItem("accessibility", "Accesibilidad y Telemetría", "Contraste técnico alto", "accessibility")
                )
            ),
            AccountSectionModel(
                title = "PRIVACIDAD Y ALMACENAMIENTO",
                items = listOf(
                    SettingItem("privacy", "Privacidad y Cifrado", "Almacenamiento local SQLite (Zero Cloud Leaks)", "lock"),
                    SettingItem("data", "Gestión de Datos", "3 Rutinas · 12 Instancias · 248 KB", "database")
                )
            ),
            AccountSectionModel(
                title = "APLICACIÓN",
                items = listOf(
                    SettingItem("backup", "Copia de Seguridad", "Exportar respaldo JSON", "cloud_upload"),
                    SettingItem("about", "Acerca de RoutineOS", "v2.4.0 (Build 42) · Protocolo Obsidian", "info")
                )
            )
        )
    )

    fun getUserProfile(): Flow<UserProfile> = _userProfile.asStateFlow()

    fun getAccountSections(): Flow<List<AccountSectionModel>> = _sections.asStateFlow()

    fun updateProfile(name: String, email: String, bio: String) {
        _userProfile.value = _userProfile.value.copy(
            name = name,
            email = email,
            bio = bio
        )
    }

    fun updateAppearanceSubtitle(subtitle: String) {
        updateItemSubtitle("appearance", subtitle)
    }

    fun updateNotificationsSubtitle(subtitle: String) {
        updateItemSubtitle("notifications", subtitle)
    }

    private fun updateItemSubtitle(itemId: String, newSubtitle: String) {
        _sections.value = _sections.value.map { section ->
            section.copy(
                items = section.items.map { item ->
                    if (item.id == itemId) item.copy(subtitle = newSubtitle) else item
                }
            )
        }
    }
}
