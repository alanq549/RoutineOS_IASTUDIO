package com.alan.routineos.core.notification

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
            // Channel preparation and boot wake-up acknowledgement
            createNotificationChannel(context)
            return
        }

        val title = intent.getStringExtra(EXTRA_TITLE) ?: "RoutineOS"
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "Tienes una actividad programada"
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 1001)

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

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (_: SecurityException) {
            // Handled when notification permission is pending or denied
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Avisos y Recordatorios de Rutinas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de recordatorios y alarmas de RoutineOS"
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "routine_reminders_channel"
        const val EXTRA_TITLE = "extra_notification_title"
        const val EXTRA_MESSAGE = "extra_notification_message"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    }
}
