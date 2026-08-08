package com.example.data

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WaterRepository(private val waterDao: WaterDao) {

    fun getTodayLogs(): Flow<List<WaterLog>> {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return waterDao.getLogsForDate(todayStr)
    }

    fun getAllLogs(): Flow<List<WaterLog>> = waterDao.getAllLogs()

    fun getUserSettings(): Flow<UserSettings?> = waterDao.getUserSettings()

    suspend fun addWaterLog(amountMl: Int) {
        val log = WaterLog(amountMl = amountMl)
        waterDao.insertLog(log)
    }

    suspend fun removeWaterLog(id: Int) {
        waterDao.deleteLogById(id)
    }

    suspend fun updateSettings(settings: UserSettings) {
        waterDao.insertUserSettings(settings)
    }

    suspend fun ensureDefaultSettings() {
        if (waterDao.getUserSettingsDirect() == null) {
            waterDao.insertUserSettings(UserSettings())
        }
    }
}
