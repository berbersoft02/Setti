package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val id: Int = 1,
    val dailyGoalGlasses: Int = 8,
    val glassSizeMl: Int = 250,
    val userName: String = "Setti",
    val reminderEnabled: Boolean = true,
    val reminderIntervalMinutes: Int = 120,
    val onboardingCompleted: Boolean = false,
    val quietHoursStart: Int = 21, // 9 PM
    val quietHoursEnd: Int = 9, // 9 AM
    val emergencyContactPhone: String = "" // Pour notifications SMS
) {
    val totalGoalMl: Int
        get() = dailyGoalGlasses * glassSizeMl

    val totalGoalLiters: Float
        get() = totalGoalMl / 1000f
}
