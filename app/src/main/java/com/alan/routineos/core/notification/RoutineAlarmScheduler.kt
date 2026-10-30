package com.alan.routineos.core.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.alan.routineos.domain.model.DailyInstance
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoutineAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleReminder(
        instanceId: String,
        title: String,
        triggerAtMillis: Long,
        advanceMinutes: Int? = null
    ) {
        val actualTrigger = if (advanceMinutes != null && advanceMinutes > 0) {
            triggerAtMillis - (advanceMinutes * 60 * 1000L)
        } else {
            triggerAtMillis
        }

        if (actualTrigger <= System.currentTimeMillis()) return

        val intent = Intent(context, RoutineNotificationReceiver::class.java).apply {
            putExtra(RoutineNotificationReceiver.EXTRA_TITLE, title)
            val msg = if (advanceMinutes != null && advanceMinutes > 0) {
                "Comienza en $advanceMinutes minutos"
            } else {
                "Es hora de tu actividad: $title"
            }
            putExtra(RoutineNotificationReceiver.EXTRA_MESSAGE, msg)
            putExtra(RoutineNotificationReceiver.EXTRA_NOTIFICATION_ID, instanceId.hashCode())
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            instanceId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    actualTrigger,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    actualTrigger,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            // Handled when SCHEDULE_EXACT_ALARM is not granted; fallback to inexact
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                actualTrigger,
                pendingIntent
            )
        }
    }

    fun cancelReminder(instanceId: String) {
        val intent = Intent(context, RoutineNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            instanceId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun syncInstanceReminders(instances: List<DailyInstance>, date: LocalDate) {
        val zone = ZoneId.systemDefault()
        instances.forEach { instance ->
            val startMinutes = instance.plannedStartTime
            val reminderAbs = instance.reminderAbs
            val reminderRel = instance.reminderRel

            if (reminderAbs != null) {
                val trigger = date.atStartOfDay(zone)
                    .plusMinutes(reminderAbs.toLong())
                    .toInstant()
                    .toEpochMilli()
                scheduleReminder(instance.id, instance.titleSnapshot, trigger)
            } else if (startMinutes != null && reminderRel != null) {
                val trigger = date.atStartOfDay(zone)
                    .plusMinutes(startMinutes.toLong())
                    .toInstant()
                    .toEpochMilli()
                scheduleReminder(instance.id, instance.titleSnapshot, trigger, reminderRel)
            } else if (startMinutes != null) {
                // Default subtle prompt at start
                val trigger = date.atStartOfDay(zone)
                    .plusMinutes(startMinutes.toLong())
                    .toInstant()
                    .toEpochMilli()
                scheduleReminder(instance.id, instance.titleSnapshot, trigger)
            }
        }
    }
}
