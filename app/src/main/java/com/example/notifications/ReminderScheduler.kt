package com.example.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.data.UserSettings
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    const val CHANNEL_ID = "eau_rappels"
    const val WATER_REMINDER_WORK = "water_reminder_periodic"
    const val SNOOZE_WORK = "water_reminder_snooze"

    private const val MIN_PERIOD_MINUTES = 15L
    private const val SNOOZE_DELAY_MINUTES = 30L

    fun schedule(context: Context, settings: UserSettings) {
        ensureChannel(context)
        val workManager = WorkManager.getInstance(context)

        if (!settings.reminderEnabled) {
            workManager.cancelUniqueWork(WATER_REMINDER_WORK)
            workManager.cancelUniqueWork("emergency_check_work")
            return
        }

        val intervalMinutes = settings.reminderIntervalMinutes.coerceAtLeast(MIN_PERIOD_MINUTES.toInt())
        val request = PeriodicWorkRequestBuilder<WaterReminderWorker>(
            intervalMinutes.toLong(),
            TimeUnit.MINUTES
        )
            .setInitialDelay(intervalMinutes.toLong(), TimeUnit.MINUTES)
            .build()

        workManager.enqueueUniquePeriodicWork(
            WATER_REMINDER_WORK,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request
        )

        // Schedule emergency check worker (every 2 hours)
        if (settings.emergencyContactPhone.isNotBlank()) {
            val emergencyRequest = PeriodicWorkRequestBuilder<com.example.notifications.EmergencyCheckWorker>(
                2,
                TimeUnit.HOURS
            ).build()

            workManager.enqueueUniquePeriodicWork(
                "emergency_check_work",
                ExistingPeriodicWorkPolicy.KEEP,
                emergencyRequest
            )
        }
    }

    fun snooze(context: Context) {
        ensureChannel(context)
        val request = OneTimeWorkRequestBuilder<WaterReminderWorker>()
            .setInitialDelay(SNOOZE_DELAY_MINUTES, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            SNOOZE_WORK,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    fun ensureChannel(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(CHANNEL_ID) == null) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Rappels d'eau",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Rappels périodiques pour boire de l'eau"
            channel.enableVibration(true)
            channel.setBypassDnd(true)
            channel.lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            manager.createNotificationChannel(channel)
        }
    }
}
