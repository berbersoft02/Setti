package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val id: Int = 1,
    val dailyGoalGlasses: Int = 8,
    val glassSizeMl: Int = 250,
    val userName: String = "Azul A Setti",
    val reminderEnabled: Boolean = true,
    val reminderIntervalMinutes: Int = 120
) {
    val totalGoalMl: Int
        get() = dailyGoalGlasses * glassSizeMl

    val totalGoalLiters: Float
        get() = totalGoalMl / 1000f
}
