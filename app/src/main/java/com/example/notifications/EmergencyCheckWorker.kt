package com.example.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.AppDatabase
import java.util.Calendar
import java.util.concurrent.TimeUnit

class EmergencyCheckWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val dao = AppDatabase.getDatabase(applicationContext).waterDao()
        val settings = dao.getUserSettingsDirect() ?: return Result.success()

        // Check if emergency contact is configured
        if (settings.emergencyContactPhone.isBlank()) {
            return Result.success()
        }

        // Get last water log timestamp
        val logs = dao.getAllLogsDirect()
        if (logs.isEmpty()) {
            // No logs yet, skip
            return Result.success()
        }

        val lastLogTime = logs.first().timestamp
        val currentTime = System.currentTimeMillis()
        val hoursSinceLastDrink = TimeUnit.MILLISECONDS.toHours(currentTime - lastLogTime)

        // If no water for 6+ hours during waking hours, send alert
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val isAwakeTime = !settings.isInQuietHours(currentHour)

        if (hoursSinceLastDrink >= 6 && isAwakeTime) {
            sendEmergencySMS(settings.emergencyContactPhone, settings.userName, hoursSinceLastDrink)
        }

        return Result.success()
    }

    private fun sendEmergencySMS(phoneNumber: String, userName: String, hours: Long) {
        try {
            val message = "⚠️ ALERTE HYDRATATION: $userName n'a pas bu d'eau depuis $hours heures. Merci de vérifier."

            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(
                phoneNumber,
                null,
                message,
                null,
                null
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
