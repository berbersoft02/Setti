package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterDao {
    @Query("SELECT * FROM water_logs WHERE dateString = :dateString ORDER BY timestamp DESC")
    fun getLogsForDate(dateString: String): Flow<List<WaterLog>>

    @Query("SELECT * FROM water_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<WaterLog>>

    @Query("SELECT * FROM water_logs ORDER BY timestamp DESC")
    suspend fun getAllLogsDirect(): List<WaterLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: WaterLog)

    @Delete
    suspend fun deleteLog(log: WaterLog)

    @Query("DELETE FROM water_logs WHERE id = :id")
    suspend fun deleteLogById(id: Int)

    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    fun getUserSettings(): Flow<UserSettings?>

    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    suspend fun getUserSettingsDirect(): UserSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserSettings(settings: UserSettings)
}
