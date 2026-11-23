package com.example.notifications

import android.content.Context
import android.telephony.SmsManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.AppDatabase
import com.example.data.HydrationAnalytics
import java.util.Calendar

class EmergencyCheckWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val dao = AppDatabase.getDatabase(applicationContext).waterDao()
        val settings = dao.getUserSettingsDirect() ?: return Result.success()

        val logs = dao.getAllLogsDirect()
        val currentTime = System.currentTimeMillis()
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        if (HydrationAnalytics.shouldTriggerEmergencyAlert(currentTime, currentHour, settings, logs)) {
            val hours = HydrationAnalytics.calculateHoursSinceLastDrink(currentTime, logs) ?: 6L
            val message = HydrationAnalytics.generateEmergencySmsMessage(settings.userName, hours)
            sendEmergencySMS(settings.emergencyContactPhone, message)
        }

        return Result.success()
    }

    private fun sendEmergencySMS(phoneNumber: String, message: String) {
        try {
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
