package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UserSettingsTest {

    @Test
    fun defaultSettings_computeCorrectGoalMetrics() {
        val settings = UserSettings(
            dailyGoalGlasses = 8,
            glassSizeMl = 250
        )

        assertEquals(2000, settings.totalGoalMl)
        assertEquals(2.0f, settings.totalGoalLiters, 0.001f)
    }

    @Test
    fun customSettings_computeCorrectGoalMetrics() {
        val settings = UserSettings(
            dailyGoalGlasses = 10,
            glassSizeMl = 300
        )

        assertEquals(3000, settings.totalGoalMl)
        assertEquals(3.0f, settings.totalGoalLiters, 0.001f)
    }

    @Test
    fun getProgressPercentage_calculatesCorrectValues() {
        val settings = UserSettings(dailyGoalGlasses = 8, glassSizeMl = 250) // 2000 ml total

        assertEquals(0, settings.getProgressPercentage(0))
        assertEquals(25, settings.getProgressPercentage(500))
        assertEquals(50, settings.getProgressPercentage(1000))
        assertEquals(100, settings.getProgressPercentage(2000))
        assertEquals(100, settings.getProgressPercentage(3000)) // Capped at 100%
    }

    @Test
    fun getProgressPercentage_handlesZeroGoalGracefully() {
        val settings = UserSettings(dailyGoalGlasses = 0, glassSizeMl = 250)
        assertEquals(0, settings.getProgressPercentage(500))
    }

    @Test
    fun getRemainingMl_calculatesRemainingCorrectly() {
        val settings = UserSettings(dailyGoalGlasses = 8, glassSizeMl = 250) // 2000 ml total

        assertEquals(2000, settings.getRemainingMl(0))
        assertEquals(1250, settings.getRemainingMl(750))
        assertEquals(0, settings.getRemainingMl(2000))
        assertEquals(0, settings.getRemainingMl(2500)) // Does not return negative
    }

    @Test
    fun isInQuietHours_spanningMidnight_identifiesCorrectHours() {
        // Quiet hours: 21 (9 PM) to 9 (9 AM)
        val settings = UserSettings(quietHoursStart = 21, quietHoursEnd = 9)

        // Inside quiet hours
        assertTrue(settings.isInQuietHours(21))
        assertTrue(settings.isInQuietHours(22))
        assertTrue(settings.isInQuietHours(23))
        assertTrue(settings.isInQuietHours(0))
        assertTrue(settings.isInQuietHours(4))
        assertTrue(settings.isInQuietHours(8))

        // Outside quiet hours (active daytime)
        assertFalse(settings.isInQuietHours(9))
        assertFalse(settings.isInQuietHours(10))
        assertFalse(settings.isInQuietHours(14))
        assertFalse(settings.isInQuietHours(20))
    }

    @Test
    fun isInQuietHours_sameDayRange_identifiesCorrectHours() {
        // Quiet hours within same day: 1 AM to 6 AM
        val settings = UserSettings(quietHoursStart = 1, quietHoursEnd = 6)

        assertFalse(settings.isInQuietHours(0))
        assertTrue(settings.isInQuietHours(1))
        assertTrue(settings.isInQuietHours(3))
        assertTrue(settings.isInQuietHours(5))
        assertFalse(settings.isInQuietHours(6))
        assertFalse(settings.isInQuietHours(12))
    }

    @Test
    fun isInQuietHours_equalStartAndEnd_returnsFalse() {
        val settings = UserSettings(quietHoursStart = 8, quietHoursEnd = 8)
        assertFalse(settings.isInQuietHours(8))
        assertFalse(settings.isInQuietHours(12))
    }
}
