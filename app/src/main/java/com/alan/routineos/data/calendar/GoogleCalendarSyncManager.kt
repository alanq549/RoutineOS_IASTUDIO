package com.alan.routineos.data.calendar

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import com.alan.routineos.core.notification.RoutineAlarmScheduler
import com.alan.routineos.domain.model.ActionProtocol
import com.alan.routineos.domain.model.DailyInstance
import com.alan.routineos.domain.model.DailyInstanceRole
import com.alan.routineos.domain.model.DailyInstanceStatus
import com.alan.routineos.domain.model.TemporalMobility
import com.alan.routineos.domain.repository.ActivityRepository
import com.alan.routineos.feature.account.model.GoogleCalendarState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Collections
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages Google Calendar integration, auto-synchronization in the background,
 * and seamless materialization of real device/Google Calendar events into Room.
 */
@Singleton
class GoogleCalendarSyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val activityRepository: ActivityRepository,
    private val alarmScheduler: RoutineAlarmScheduler
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var autoSyncJob: Job? = null

    private val _calendarState = MutableStateFlow(
        GoogleCalendarState(
            isConnected = true,
            accountEmail = "alanqFF@gmail.com",
            lastSyncTime = "Sincronizando...",
            projectEventsInPlanning = true,
            detectConflicts = true,
            isSyncing = false,
            syncedEventsCount = 0,
            autoSyncActive = true
        )
    )
    val calendarState: StateFlow<GoogleCalendarState> = _calendarState.asStateFlow()

    private val legacyFakeIds = listOf(
        "gcal_today_sprint",
        "gcal_today_ai_tutoring",
        "gcal_tmrw_architecture",
        "gcal_tmrw_academic",
        "gcal_sat_conquista_zonas",
        "gcal_day2_project"
    )

    private val activeSyncedIds = Collections.synchronizedSet(mutableSetOf<String>())

    private val calendarObserver = object : android.database.ContentObserver(
        android.os.Handler(android.os.Looper.getMainLooper())
    ) {
        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            scope.launch {
                if (_calendarState.value.isConnected && _calendarState.value.projectEventsInPlanning) {
                    performSync(isBackground = true)
                }
            }
        }
    }

    init {
        try {
            context.contentResolver.registerContentObserver(
                CalendarContract.Events.CONTENT_URI,
                true,
                calendarObserver
            )
        } catch (_: Exception) {
            // Safe fallback if permission not yet granted
        }
        startAutoSync()
    }

    fun startAutoSync() {
        autoSyncJob?.cancel()
        autoSyncJob = scope.launch {
            if (_calendarState.value.isConnected && _calendarState.value.projectEventsInPlanning) {
                performSync()
            }
            while (isActive) {
                delay(300_000) // Lightweight 5-min fallback; ContentObserver handles instant updates
                if (_calendarState.value.isConnected && _calendarState.value.projectEventsInPlanning) {
                    performSync(isBackground = true)
                }
            }
        }
    }

    suspend fun syncNow() {
        try {
            android.content.ContentResolver.requestSync(
                null,
                CalendarContract.AUTHORITY,
                android.os.Bundle.EMPTY
            )
        } catch (_: Exception) {
            // Best-effort system calendar sync
        }
        performSync(isBackground = false)
    }

    suspend fun setConnected(connected: Boolean) {
        _calendarState.value = _calendarState.value.copy(
            isConnected = connected,
            lastSyncTime = if (connected) "Recién conectado" else _calendarState.value.lastSyncTime
        )
        if (connected) {
            startAutoSync()
        } else {
            autoSyncJob?.cancel()
            clearSyncedEvents()
            _calendarState.value = _calendarState.value.copy(syncedEventsCount = 0)
        }
    }

    suspend fun setProjectEventsInPlanning(enabled: Boolean) {
        _calendarState.value = _calendarState.value.copy(projectEventsInPlanning = enabled)
        if (enabled && _calendarState.value.isConnected) {
            performSync()
        } else {
            clearSyncedEvents()
            _calendarState.value = _calendarState.value.copy(syncedEventsCount = 0)
        }
    }

    fun setDetectConflicts(enabled: Boolean) {
        _calendarState.value = _calendarState.value.copy(detectConflicts = enabled)
    }

    private suspend fun performSync(isBackground: Boolean = false) {
        if (!_calendarState.value.isConnected || !_calendarState.value.projectEventsInPlanning) return

        if (!isBackground) {
            _calendarState.value = _calendarState.value.copy(isSyncing = true)
            delay(250)
        }

        // Remove any legacy hardcoded sample events
        legacyFakeIds.forEach { id ->
            activityRepository.deleteDailyInstance(id)
        }

        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            _calendarState.value = _calendarState.value.copy(
                isSyncing = false,
                lastSyncTime = "Requiere permiso de Calendario",
                syncedEventsCount = 0
            )
            return
        }

        val deviceInstances = queryDeviceCalendarEvents()
        val newIds = deviceInstances.map { it.id }.toSet()

        // Remove events that were deleted or moved in Google Calendar
        val removedIds = activeSyncedIds.filter { it !in newIds }
        removedIds.forEach { id ->
            activityRepository.deleteDailyInstance(id)
            activeSyncedIds.remove(id)
        }

        // Upsert current real calendar events into Room
        deviceInstances.forEach { instance ->
            activeSyncedIds.add(instance.id)
            activityRepository.upsertDailyInstance(instance)
        }

        // Schedule 10-minute advance tray notifications for synced calendar events
        deviceInstances.groupBy { LocalDate.ofEpochDay(it.scheduledDate) }.forEach { (date, instances) ->
            alarmScheduler.syncInstanceReminders(instances, date)
        }

        _calendarState.value = _calendarState.value.copy(
            isSyncing = false,
            lastSyncTime = "Hace un momento",
            syncedEventsCount = deviceInstances.size
        )
    }

    private fun queryDeviceCalendarEvents(): List<DailyInstance> {
        val results = mutableListOf<DailyInstance>()
        try {
            val startMillis = LocalDate.now().minusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endMillis = LocalDate.now().plusDays(30).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
            ContentUris.appendId(builder, startMillis)
            ContentUris.appendId(builder, endMillis)

            val projection = arrayOf(
                CalendarContract.Instances._ID,
                CalendarContract.Instances.EVENT_ID,
                CalendarContract.Instances.TITLE,
                CalendarContract.Instances.BEGIN,
                CalendarContract.Instances.END,
                CalendarContract.Instances.ALL_DAY
            )
            context.contentResolver.query(builder.build(), projection, null, null, null)?.use { cursor ->
                val titleIdx = cursor.getColumnIndex(CalendarContract.Instances.TITLE)
                val beginIdx = cursor.getColumnIndex(CalendarContract.Instances.BEGIN)
                val endIdx = cursor.getColumnIndex(CalendarContract.Instances.END)
                val idIdx = cursor.getColumnIndex(CalendarContract.Instances._ID)
                val allDayIdx = cursor.getColumnIndex(CalendarContract.Instances.ALL_DAY)

                while (cursor.moveToNext()) {
                    val isAllDay = allDayIdx != -1 && cursor.getInt(allDayIdx) == 1
                    if (isAllDay) continue

                    val title = cursor.getString(titleIdx)?.takeIf { it.isNotBlank() } ?: "Evento de Calendario"
                    val begin = cursor.getLong(beginIdx)
                    val end = cursor.getLong(endIdx)
                    val id = cursor.getLong(idIdx)

                    val zdtBegin = Instant.ofEpochMilli(begin).atZone(ZoneId.systemDefault())
                    val zdtEnd = Instant.ofEpochMilli(end).atZone(ZoneId.systemDefault())
                    val epochDay = zdtBegin.toLocalDate().toEpochDay()
                    val startMins = zdtBegin.hour * 60 + zdtBegin.minute
                    val endMins = zdtEnd.hour * 60 + zdtEnd.minute

                    results.add(
                        DailyInstance(
                            id = "device_gcal_$id",
                            target = null,
                            scheduledDate = epochDay,
                            titleSnapshot = title,
                            descriptionSnapshot = "Google Calendar · Ocupado",
                            plannedStartTime = startMins,
                            plannedEndTime = endMins,
                            plannedDurationMinutes = (endMins - startMins).coerceAtLeast(15),
                            status = DailyInstanceStatus.PLANNED,
                            mobility = TemporalMobility.IMMOBILE,
                            isAdHoc = true,
                            role = DailyInstanceRole.ACTIVITY,
                            actionProtocol = ActionProtocol.TIMER
                        )
                    )
                }
            }
        } catch (_: Exception) {
            // Safe fallback
        }
        return results
    }

    private suspend fun clearSyncedEvents() {
        legacyFakeIds.forEach { id ->
            activityRepository.deleteDailyInstance(id)
        }
        activeSyncedIds.toList().forEach { id ->
            activityRepository.deleteDailyInstance(id)
            activeSyncedIds.remove(id)
        }
    }
}
