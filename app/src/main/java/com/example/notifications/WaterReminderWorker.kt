package com.example.notifications

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.R
import com.example.MainActivity
import com.example.data.AppDatabase
import com.example.data.UserSettings

class WaterReminderWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val settings = AppDatabase.getDatabase(applicationContext)
            .waterDao()
            .getUserSettingsDirect() ?: return Result.success()

        if (!settings.reminderEnabled) return Result.success()

        // Check if we're in quiet hours
        if (isQuietHours(settings)) return Result.success()

        ReminderScheduler.ensureChannel(applicationContext)
        if (hasNotificationPermission()) {
            showReminderNotification(settings)
        }
        return Result.success()
    }

    private fun isQuietHours(settings: UserSettings): Boolean {
        val calendar = java.util.Calendar.getInstance()
        val currentHour = calendar.get(java.util.Calendar.HOUR_OF_DAY)

        val start = settings.quietHoursStart // 21h
        val end = settings.quietHoursEnd // 9h

        // Quiet hours span across midnight (21h to 9h)
        return if (start > end) {
            // If current time is >= 21h OR < 9h, it's quiet hours
            currentHour >= start || currentHour < end
        } else {
            // Normal range (e.g., 22h to 7h)
            currentHour >= start && currentHour < end
        }
    }

    private fun hasNotificationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showReminderNotification(settings: UserSettings) {
        val context = applicationContext

        // Launch fullscreen alarm activity
        val alarmIntent = Intent(context, com.example.AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val drinkPendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_DRINK,
            Intent(context, ReminderActionReceiver::class.java)
                .setAction(ReminderActionReceiver.ACTION_DRINK),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_SNOOZE,
            Intent(context, ReminderActionReceiver::class.java)
                .setAction(ReminderActionReceiver.ACTION_SNOOZE),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, ReminderScheduler.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("C'est l'heure de boire de l'eau ! 💧")
            .setContentText("Prenez un verre de ${settings.glassSizeMl} ml pour atteindre votre objectif du jour.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(false)
            .setOngoing(true)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setContentIntent(fullScreenPendingIntent)
            .addAction(0, "J'ai bu", drinkPendingIntent)
            .addAction(0, "Plus tard", snoozePendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)

        // Also start the alarm activity directly
        context.startActivity(alarmIntent)
    }

    companion object {
        const val NOTIFICATION_ID = 1001
        private const val REQUEST_DRINK = 1
        private const val REQUEST_SNOOZE = 2
    }
}
