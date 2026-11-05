package com.alan.routineos.core.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.alan.routineos.MainActivity

class RoutineNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            createNotificationChannel(context)
            return
        }

        val title = intent.getStringExtra(EXTRA_TITLE) ?: "RoutineOS"
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "Tienes una actividad programada"
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 1001)
        val roleType = intent.getStringExtra(EXTRA_ROLE_TYPE) ?: "EVENTO"

        if (intent.action == ACTION_SNOOZE_5_MIN) {
            NotificationManagerCompat.from(context).cancel(notificationId)
            scheduleSnooze5Min(context, title, notificationId, roleType)
            return
        }

        createNotificationChannel(context)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = Intent(context, RoutineNotificationReceiver::class.java).apply {
            action = ACTION_SNOOZE_5_MIN
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_MESSAGE, "Recordatorio pospuesto · Comienza en 5 minutos")
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
            putExtra(EXTRA_ROLE_TYPE, roleType)
        }

        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 5000,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val (accentColor, subText) = when (roleType) {
            "RECORDATORIO" -> 0xFFFBBF24.toInt() to "ROUTINEOS // RECORDATORIO"
            "TAREA" -> 0xFF38BDF8.toInt() to "ROUTINEOS // TAREA"
            "CALENDAR" -> 0xFFC0C1FF.toInt() to "ROUTINEOS // GOOGLE CALENDAR"
            else -> 0xFF5AF0B3.toInt() to "ROUTINEOS // EVENTO"
        }

        val customView = android.widget.RemoteViews(
            context.packageName,
            com.alan.routineos.R.layout.notification_routine_obsidian
        ).apply {
            setTextViewText(com.alan.routineos.R.id.notif_badge, subText)
            setTextColor(com.alan.routineos.R.id.notif_badge, accentColor)
            setTextViewText(com.alan.routineos.R.id.notif_title, title)
            setTextViewText(com.alan.routineos.R.id.notif_message, message)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setColor(accentColor)
            .setCustomContentView(customView)
            .setCustomBigContentView(customView)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_menu_recent_history,
                "Recordar en 5 min",
                snoozePendingIntent
            )
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (_: SecurityException) {
            // Handled when notification permission is pending or denied
        }
    }

    private fun scheduleSnooze5Min(context: Context, title: String, notificationId: Int, roleType: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerAt = System.currentTimeMillis() + (5 * 60 * 1000L)

        val intent = Intent(context, RoutineNotificationReceiver::class.java).apply {
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_MESSAGE, "Recordatorio pospuesto · Comienza en breve")
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
            putExtra(EXTRA_ROLE_TYPE, roleType)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            }
        } catch (_: SecurityException) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Avisos y Recordatorios de Rutinas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones personalizadas de eventos, recordatorios y tareas de RoutineOS"
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "routine_reminders_channel"
        const val ACTION_SNOOZE_5_MIN = "com.alan.routineos.ACTION_SNOOZE_5_MIN"
        const val EXTRA_TITLE = "extra_notification_title"
        const val EXTRA_MESSAGE = "extra_notification_message"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
        const val EXTRA_ROLE_TYPE = "extra_notification_role_type"
    }
}
