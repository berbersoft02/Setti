package com.example.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import com.example.data.AppDatabase
import com.example.data.WaterLog

class ReminderActionReceiver : BroadcastReceiver() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_DRINK -> {
                val pendingResult = goAsync()
                scope.launch {
                    try {
                        val dao = AppDatabase.getDatabase(context.applicationContext).waterDao()
                        val settings = dao.getUserSettingsDirect()
                        dao.insertLog(WaterLog(amountMl = settings?.glassSizeMl ?: DEFAULT_GLASS_ML))
                    } finally {
                        cancelNotification(context)
                        pendingResult.finish()
                    }
                }
            }

            ACTION_SNOOZE -> {
                cancelNotification(context)
                ReminderScheduler.snooze(context)
            }
        }
    }

    private fun cancelNotification(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(WaterReminderWorker.NOTIFICATION_ID)
    }

    companion object {
        const val ACTION_DRINK = "com.example.settiflow.action.DRINK"
        const val ACTION_SNOOZE = "com.example.settiflow.action.SNOOZE"
        private const val DEFAULT_GLASS_ML = 250
    }
}
