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

    /**
     * Computes the hydration goal completion percentage (0 - 100).
     */
    fun getProgressPercentage(consumedMl: Int): Int {
        if (totalGoalMl <= 0) return 0
        return ((consumedMl.toFloat() / totalGoalMl) * 100).toInt().coerceIn(0, 100)
    }

    /**
     * Calculates remaining milliliters required to meet the daily goal.
     */
    fun getRemainingMl(consumedMl: Int): Int {
        return (totalGoalMl - consumedMl).coerceAtLeast(0)
    }

    /**
     * Determines whether the given hour of the day (0..23) falls within quiet hours.
     * Handles intervals that span across midnight (e.g. 21h to 9h) or within the same day.
     */
    fun isInQuietHours(hour: Int): Boolean {
        if (quietHoursStart == quietHoursEnd) return false
        return if (quietHoursStart > quietHoursEnd) {
            // Span across midnight (e.g. 21h to 9h)
            hour >= quietHoursStart || hour < quietHoursEnd
        } else {
            // Same day range (e.g. 1h to 6h)
            hour in quietHoursStart until quietHoursEnd
        }
    }
}
