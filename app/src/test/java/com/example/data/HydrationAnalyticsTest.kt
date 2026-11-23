package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class HydrationAnalyticsTest {

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private fun dateOffset(days: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, days)
        return sdf.format(cal.time)
    }

    private fun timestampFor(daysOffset: Int, hour: Int = 12): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, daysOffset)
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    // --- computeDailySummaries Tests ---

    @Test
    fun computeDailySummaries_emptyLogs_returnsEmptyList() {
        val summaries = HydrationAnalytics.computeDailySummaries(emptyList(), 2000)
        assertTrue(summaries.isEmpty())
    }

    @Test
    fun computeDailySummaries_aggregatesMultipleLogsSameDay() {
        val todayStr = dateOffset(0)
        val logs = listOf(
            WaterLog(id = 1, amountMl = 250, timestamp = timestampFor(0, 8), dateString = todayStr),
            WaterLog(id = 2, amountMl = 350, timestamp = timestampFor(0, 11), dateString = todayStr),
            WaterLog(id = 3, amountMl = 500, timestamp = timestampFor(0, 15), dateString = todayStr)
        )

        val summaries = HydrationAnalytics.computeDailySummaries(logs, 2000)
        assertEquals(1, summaries.size)

        val summary = summaries[0]
        assertEquals(todayStr, summary.dateString)
        assertEquals(1100, summary.totalMl)
        assertEquals(2000, summary.goalMl)
        assertEquals(3, summary.logCount)
        assertFalse(summary.isGoalAchieved)
        assertEquals(55, summary.percentage)
    }

    @Test
    fun computeDailySummaries_calculatesGoalAchievementAndPercentages() {
        val todayStr = dateOffset(0)
        val yesterdayStr = dateOffset(-1)

        val logs = listOf(
            // Today: 2200 ml (goal 2000 ml -> achieved)
            WaterLog(id = 1, amountMl = 1200, timestamp = timestampFor(0, 9), dateString = todayStr),
            WaterLog(id = 2, amountMl = 1000, timestamp = timestampFor(0, 14), dateString = todayStr),
            // Yesterday: 1500 ml (goal 2000 ml -> not achieved)
            WaterLog(id = 3, amountMl = 1500, timestamp = timestampFor(-1, 10), dateString = yesterdayStr)
        )

        val summaries = HydrationAnalytics.computeDailySummaries(logs, 2000)
        assertEquals(2, summaries.size)

        val todaySummary = summaries.first { it.dateString == todayStr }
        assertTrue(todaySummary.isGoalAchieved)
        assertEquals(110, todaySummary.percentage)

        val yesterdaySummary = summaries.first { it.dateString == yesterdayStr }
        assertFalse(yesterdaySummary.isGoalAchieved)
        assertEquals(75, yesterdaySummary.percentage)
    }

    @Test
    fun computeDailySummaries_sortsDescendingByDate() {
        val d1 = dateOffset(-2)
        val d2 = dateOffset(-1)
        val d3 = dateOffset(0)

        val logs = listOf(
            WaterLog(id = 1, amountMl = 250, timestamp = timestampFor(-2), dateString = d1),
            WaterLog(id = 2, amountMl = 250, timestamp = timestampFor(0), dateString = d3),
            WaterLog(id = 3, amountMl = 250, timestamp = timestampFor(-1), dateString = d2)
        )

        val summaries = HydrationAnalytics.computeDailySummaries(logs, 1000)
        assertEquals(3, summaries.size)
        assertEquals(d3, summaries[0].dateString)
        assertEquals(d2, summaries[1].dateString)
        assertEquals(d1, summaries[2].dateString)
    }

    // --- calculateHoursSinceLastDrink Tests ---

    @Test
    fun calculateHoursSinceLastDrink_emptyLogs_returnsNull() {
        val hours = HydrationAnalytics.calculateHoursSinceLastDrink(System.currentTimeMillis(), emptyList())
        assertNull(hours)
    }

    @Test
    fun calculateHoursSinceLastDrink_calculatesCorrectElapsedHours() {
        val now = System.currentTimeMillis()
        val threeHoursAgo = now - TimeUnit.HOURS.toMillis(3)

        val logs = listOf(
            WaterLog(id = 1, amountMl = 250, timestamp = threeHoursAgo, dateString = sdf.format(Date(threeHoursAgo)))
        )

        val hours = HydrationAnalytics.calculateHoursSinceLastDrink(now, logs)
        assertNotNull(hours)
        assertEquals(3L, hours)
    }

    @Test
    fun calculateHoursSinceLastDrink_futureTimestamp_returnsZero() {
        val now = System.currentTimeMillis()
        val future = now + TimeUnit.HOURS.toMillis(2)

        val logs = listOf(
            WaterLog(id = 1, amountMl = 250, timestamp = future, dateString = sdf.format(Date(future)))
        )

        val hours = HydrationAnalytics.calculateHoursSinceLastDrink(now, logs)
        assertEquals(0L, hours)
    }

    @Test
    fun calculateHoursSinceLastDrink_multipleLogs_usesMostRecent() {
        val now = System.currentTimeMillis()
        val fiveHoursAgo = now - TimeUnit.HOURS.toMillis(5)
        val oneHourAgo = now - TimeUnit.HOURS.toMillis(1)

        val logs = listOf(
            WaterLog(id = 1, amountMl = 250, timestamp = fiveHoursAgo, dateString = sdf.format(Date(fiveHoursAgo))),
            WaterLog(id = 2, amountMl = 250, timestamp = oneHourAgo, dateString = sdf.format(Date(oneHourAgo)))
        )

        val hours = HydrationAnalytics.calculateHoursSinceLastDrink(now, logs)
        assertEquals(1L, hours)
    }

    // --- computeStats & Streak Tests ---

    @Test
    fun computeStats_emptyLogsOrZeroGoal_returnsZeroDefaults() {
        val statsEmpty = HydrationAnalytics.computeStats(emptyList(), 2000)
        assertEquals(0, statsEmpty.currentStreakDays)
        assertEquals(0, statsEmpty.bestStreakDays)
        assertEquals(0L, statsEmpty.totalVolumeLoggedMl)
        assertEquals(0, statsEmpty.averageDailyMl)
        assertEquals(0, statsEmpty.daysGoalMetCount)
        assertEquals(0, statsEmpty.totalTrackedDays)
        assertEquals(0, statsEmpty.completionRatePercent)

        val statsZeroGoal = HydrationAnalytics.computeStats(
            listOf(WaterLog(id = 1, amountMl = 500, timestamp = System.currentTimeMillis())),
            0
        )
        assertEquals(0, statsZeroGoal.currentStreakDays)
    }

    @Test
    fun computeStats_singleDayMetGoal_returnsOneDayStreak() {
        val todayStr = dateOffset(0)
        val logs = listOf(
            WaterLog(id = 1, amountMl = 2000, timestamp = timestampFor(0), dateString = todayStr)
        )

        val stats = HydrationAnalytics.computeStats(logs, 2000, referenceDate = todayStr)
        assertEquals(1, stats.currentStreakDays)
        assertEquals(1, stats.bestStreakDays)
        assertEquals(2000L, stats.totalVolumeLoggedMl)
        assertEquals(2000, stats.averageDailyMl)
        assertEquals(1, stats.daysGoalMetCount)
        assertEquals(1, stats.totalTrackedDays)
        assertEquals(100, stats.completionRatePercent)
    }

    @Test
    fun computeStats_singleDayUnmetGoal_returnsZeroStreak() {
        val todayStr = dateOffset(0)
        val logs = listOf(
            WaterLog(id = 1, amountMl = 500, timestamp = timestampFor(0), dateString = todayStr)
        )

        val stats = HydrationAnalytics.computeStats(logs, 2000, referenceDate = todayStr)
        assertEquals(0, stats.currentStreakDays)
        assertEquals(0, stats.bestStreakDays)
        assertEquals(500L, stats.totalVolumeLoggedMl)
        assertEquals(500, stats.averageDailyMl)
        assertEquals(0, stats.daysGoalMetCount)
        assertEquals(1, stats.totalTrackedDays)
        assertEquals(0, stats.completionRatePercent)
    }

    @Test
    fun computeStats_yesterdayMetTodayUnmet_preservesStreakFromYesterday() {
        val todayStr = dateOffset(0)
        val yesterdayStr = dateOffset(-1)

        val logs = listOf(
            // Today: in progress, not met yet
            WaterLog(id = 1, amountMl = 500, timestamp = timestampFor(0), dateString = todayStr),
            // Yesterday: met
            WaterLog(id = 2, amountMl = 2000, timestamp = timestampFor(-1), dateString = yesterdayStr)
        )

        val stats = HydrationAnalytics.computeStats(logs, 2000, referenceDate = todayStr)
        // Since yesterday met, current streak is 1 (streak maintained)
        assertEquals(1, stats.currentStreakDays)
        assertEquals(1, stats.bestStreakDays)
    }

    @Test
    fun computeStats_multiDayConsecutiveGoalsMet_accumulatesStreak() {
        val todayStr = dateOffset(0)
        val dMinus1 = dateOffset(-1)
        val dMinus2 = dateOffset(-2)
        val dMinus3 = dateOffset(-3)

        val logs = listOf(
            WaterLog(id = 1, amountMl = 2000, timestamp = timestampFor(0), dateString = todayStr),
            WaterLog(id = 2, amountMl = 2500, timestamp = timestampFor(-1), dateString = dMinus1),
            WaterLog(id = 3, amountMl = 2100, timestamp = timestampFor(-2), dateString = dMinus2),
            WaterLog(id = 4, amountMl = 2000, timestamp = timestampFor(-3), dateString = dMinus3)
        )

        val stats = HydrationAnalytics.computeStats(logs, 2000, referenceDate = todayStr)
        assertEquals(4, stats.currentStreakDays)
        assertEquals(4, stats.bestStreakDays)
        assertEquals(8600L, stats.totalVolumeLoggedMl)
        assertEquals(2150, stats.averageDailyMl)
        assertEquals(4, stats.daysGoalMetCount)
        assertEquals(4, stats.totalTrackedDays)
        assertEquals(100, stats.completionRatePercent)
    }

    @Test
    fun computeStats_brokenStreak_resetsCurrentStreak() {
        val todayStr = dateOffset(0)
        val dMinus1 = dateOffset(-1)
        val dMinus2 = dateOffset(-2)
        val dMinus3 = dateOffset(-3)

        val logs = listOf(
            WaterLog(id = 1, amountMl = 2000, timestamp = timestampFor(0), dateString = todayStr),
            WaterLog(id = 2, amountMl = 2000, timestamp = timestampFor(-1), dateString = dMinus1),
            // dMinus2 missed
            WaterLog(id = 3, amountMl = 500, timestamp = timestampFor(-2), dateString = dMinus2),
            // dMinus3 met
            WaterLog(id = 4, amountMl = 2000, timestamp = timestampFor(-3), dateString = dMinus3)
        )

        val stats = HydrationAnalytics.computeStats(logs, 2000, referenceDate = todayStr)
        // Current streak: today + dMinus1 = 2
        assertEquals(2, stats.currentStreakDays)
        assertEquals(2, stats.bestStreakDays)
        assertEquals(3, stats.daysGoalMetCount)
        assertEquals(4, stats.totalTrackedDays)
        assertEquals(75, stats.completionRatePercent)
    }

    @Test
    fun computeStats_bestStreakExceedsCurrentStreak() {
        val todayStr = dateOffset(0)
        val dMinus1 = dateOffset(-1)
        val dMinus2 = dateOffset(-2)
        val dMinus3 = dateOffset(-3)
        val dMinus4 = dateOffset(-4)
        val dMinus5 = dateOffset(-5)

        val logs = listOf(
            // Today met
            WaterLog(id = 1, amountMl = 2000, timestamp = timestampFor(0), dateString = todayStr),
            // dMinus1 missed
            WaterLog(id = 2, amountMl = 400, timestamp = timestampFor(-1), dateString = dMinus1),
            // dMinus2, dMinus3, dMinus4 met (past 3-day streak)
            WaterLog(id = 3, amountMl = 2000, timestamp = timestampFor(-2), dateString = dMinus2),
            WaterLog(id = 4, amountMl = 2000, timestamp = timestampFor(-3), dateString = dMinus3),
            WaterLog(id = 5, amountMl = 2000, timestamp = timestampFor(-4), dateString = dMinus4),
            // dMinus5 missed
            WaterLog(id = 6, amountMl = 200, timestamp = timestampFor(-5), dateString = dMinus5)
        )

        val stats = HydrationAnalytics.computeStats(logs, 2000, referenceDate = todayStr)
        assertEquals(1, stats.currentStreakDays)
        assertEquals(3, stats.bestStreakDays)
    }

    // --- Safety Alerting & Emergency Check Tests ---

    @Test
    fun shouldTriggerEmergencyAlert_emergencyContactBlank_returnsFalse() {
        val settings = UserSettings(emergencyContactPhone = "")
        val logs = listOf(WaterLog(id = 1, timestamp = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(8)))

        val trigger = HydrationAnalytics.shouldTriggerEmergencyAlert(
            currentTime = System.currentTimeMillis(),
            currentHour = 14,
            settings = settings,
            logs = logs
        )
        assertFalse(trigger)
    }

    @Test
    fun shouldTriggerEmergencyAlert_emptyLogs_returnsFalse() {
        val settings = UserSettings(emergencyContactPhone = "+33612345678")
        val trigger = HydrationAnalytics.shouldTriggerEmergencyAlert(
            currentTime = System.currentTimeMillis(),
            currentHour = 14,
            settings = settings,
            logs = emptyList()
        )
        assertFalse(trigger)
    }

    @Test
    fun shouldTriggerEmergencyAlert_withinQuietHours_returnsFalse() {
        // Quiet hours 21h - 9h
        val settings = UserSettings(emergencyContactPhone = "+33612345678", quietHoursStart = 21, quietHoursEnd = 9)
        val logs = listOf(WaterLog(id = 1, timestamp = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(8)))

        // Hour 23 is in quiet hours
        val trigger = HydrationAnalytics.shouldTriggerEmergencyAlert(
            currentTime = System.currentTimeMillis(),
            currentHour = 23,
            settings = settings,
            logs = logs
        )
        assertFalse(trigger)
    }

    @Test
    fun shouldTriggerEmergencyAlert_drankRecently_returnsFalse() {
        val settings = UserSettings(emergencyContactPhone = "+33612345678", quietHoursStart = 21, quietHoursEnd = 9)
        val now = System.currentTimeMillis()
        val logs = listOf(WaterLog(id = 1, timestamp = now - TimeUnit.HOURS.toMillis(2)))

        val trigger = HydrationAnalytics.shouldTriggerEmergencyAlert(
            currentTime = now,
            currentHour = 14,
            settings = settings,
            logs = logs
        )
        assertFalse(trigger)
    }

    @Test
    fun shouldTriggerEmergencyAlert_exceedsThresholdDuringWakingHours_returnsTrue() {
        val settings = UserSettings(emergencyContactPhone = "+33612345678", quietHoursStart = 21, quietHoursEnd = 9)
        val now = System.currentTimeMillis()
        val logs = listOf(WaterLog(id = 1, timestamp = now - TimeUnit.HOURS.toMillis(7)))

        val trigger = HydrationAnalytics.shouldTriggerEmergencyAlert(
            currentTime = now,
            currentHour = 15,
            settings = settings,
            logs = logs
        )
        assertTrue(trigger)
    }

    // --- Message & Status Tests ---

    @Test
    fun generateEmergencySmsMessage_formatsMessageWithUserName() {
        val msg = HydrationAnalytics.generateEmergencySmsMessage("Grand-Mère Setti", 7)
        assertTrue(msg.contains("Grand-Mère Setti"))
        assertTrue(msg.contains("7 heures"))
        assertTrue(msg.startsWith("⚠️ ALERTE HYDRATATION:"))
    }

    @Test
    fun generateEmergencySmsMessage_blankUserName_usesDefaultFallback() {
        val msg = HydrationAnalytics.generateEmergencySmsMessage("   ", 8)
        assertTrue(msg.contains("L'utilisateur"))
        assertTrue(msg.contains("8 heures"))
    }

    @Test
    fun getHydrationStatus_evaluatesCorrectly() {
        // Optimal when goal met
        assertEquals(
            HydrationStatus.OPTIMAL,
            HydrationAnalytics.getHydrationStatus(2000, 2000, 1L, false)
        )

        // Inactive when > 6h without drinking during daytime
        assertEquals(
            HydrationStatus.INACTIVE,
            HydrationAnalytics.getHydrationStatus(500, 2000, 7L, false)
        )

        // On track when >= 50%
        assertEquals(
            HydrationStatus.ON_TRACK,
            HydrationAnalytics.getHydrationStatus(1200, 2000, 2L, false)
        )

        // Needs hydration when < 50%
        assertEquals(
            HydrationStatus.NEEDS_HYDRATION,
            HydrationAnalytics.getHydrationStatus(400, 2000, 2L, false)
        )
    }
}
