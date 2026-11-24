package com.example.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Represents the hydration status of a user at a given moment.
 */
enum class HydrationStatus {
    OPTIMAL,          // Met or exceeded daily goal
    ON_TRACK,         // Good progress, drank recently
    NEEDS_HYDRATION,  // Behind schedule, needs a drink
    INACTIVE          // Extended period without drinking during active hours
}

/**
 * Aggregated hydration summary for a specific calendar date (yyyy-MM-dd).
 */
data class DailyHydrationSummary(
    val dateString: String,
    val totalMl: Int,
    val goalMl: Int,
    val logCount: Int,
    val isGoalAchieved: Boolean,
    val percentage: Int
)

/**
 * Multi-day hydration performance metrics and streak tracking.
 */
data class HydrationStats(
    val currentStreakDays: Int,
    val bestStreakDays: Int,
    val totalVolumeLoggedMl: Long,
    val averageDailyMl: Int,
    val daysGoalMetCount: Int,
    val totalTrackedDays: Int,
    val completionRatePercent: Int
)

/**
 * Pure domain logic for hydration statistics, streak calculations,
 * and safety monitoring rules.
 */
object HydrationAnalytics {

    fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(timestamp))
    }

    /**
     * Groups water logs by date string (sorted in reverse chronological order)
     * and computes daily totals and goal achievement.
     */
    fun computeDailySummaries(
        logs: List<WaterLog>,
        goalMl: Int
    ): List<DailyHydrationSummary> {
        if (logs.isEmpty()) return emptyList()

        return logs.groupBy { it.dateString }
            .entries
            .map { (dateStr, dayLogs) ->
                val totalMl = dayLogs.sumOf { it.amountMl }
                val percentage = if (goalMl > 0) {
                    ((totalMl.toFloat() / goalMl) * 100).toInt()
                } else 0

                DailyHydrationSummary(
                    dateString = dateStr,
                    totalMl = totalMl,
                    goalMl = goalMl,
                    logCount = dayLogs.size,
                    isGoalAchieved = goalMl > 0 && totalMl >= goalMl,
                    percentage = percentage
                )
            }
            .sortedByDescending { it.dateString }
    }

    /**
     * Calculates the elapsed time in full hours since the most recent drink log.
     * Returns null if no logs exist or if the most recent log is in the future.
     */
    fun calculateHoursSinceLastDrink(
        currentTime: Long,
        logs: List<WaterLog>
    ): Long? {
        if (logs.isEmpty()) return null
        val latestLog = logs.maxByOrNull { it.timestamp } ?: return null
        if (latestLog.timestamp > currentTime) return 0L
        return TimeUnit.MILLISECONDS.toHours(currentTime - latestLog.timestamp)
    }

    /**
     * Calculates streak and aggregate hydration statistics.
     *
     * Streak logic:
     * - A day counts towards the streak if totalMl >= goalMl.
     * - The streak is evaluated starting from [referenceDate] (defaults to today).
     * - If [referenceDate] goal is already met, it is included in the streak.
     * - If [referenceDate] goal is NOT yet met, but yesterday's goal was met, the streak
     *   from yesterday is preserved as active (since today is still in progress).
     * - If yesterday's goal was also missed, current streak is 0.
     */
    fun computeStats(
        logs: List<WaterLog>,
        goalMl: Int,
        referenceDate: String = formatDate(System.currentTimeMillis())
    ): HydrationStats {
        if (logs.isEmpty() || goalMl <= 0) {
            return HydrationStats(
                currentStreakDays = 0,
                bestStreakDays = 0,
                totalVolumeLoggedMl = 0L,
                averageDailyMl = 0,
                daysGoalMetCount = 0,
                totalTrackedDays = 0,
                completionRatePercent = 0
            )
        }

        val summaries = computeDailySummaries(logs, goalMl)
        val totalVolume = logs.sumOf { it.amountMl.toLong() }
        val trackedDays = summaries.size
        val daysGoalMet = summaries.count { it.isGoalAchieved }
        val avgDailyMl = if (trackedDays > 0) (totalVolume / trackedDays).toInt() else 0
        val completionRate = if (trackedDays > 0) {
            ((daysGoalMet.toFloat() / trackedDays) * 100).toInt().coerceIn(0, 100)
        } else 0

        // Map for fast lookup by date string: "yyyy-MM-dd"
        val summaryByDate = summaries.associateBy { it.dateString }

        // Calculate current streak
        val currentStreak = calculateCurrentStreak(summaryByDate, referenceDate)

        // Calculate best all-time streak
        val bestStreak = calculateBestStreak(summaries)

        return HydrationStats(
            currentStreakDays = currentStreak,
            bestStreakDays = maxOf(currentStreak, bestStreak),
            totalVolumeLoggedMl = totalVolume,
            averageDailyMl = avgDailyMl,
            daysGoalMetCount = daysGoalMet,
            totalTrackedDays = trackedDays,
            completionRatePercent = completionRate
        )
    }

    private fun calculateCurrentStreak(
        summaryByDate: Map<String, DailyHydrationSummary>,
        referenceDate: String
    ): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val refDate = try {
            sdf.parse(referenceDate) ?: return 0
        } catch (_: Exception) {
            return 0
        }

        val todaySummary = summaryByDate[referenceDate]
        val todayAchieved = todaySummary?.isGoalAchieved == true

        var streak = 0
        val cal = Calendar.getInstance()
        cal.time = refDate

        if (todayAchieved) {
            streak++
            cal.add(Calendar.DAY_OF_YEAR, -1)
        } else {
            // Check yesterday
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayStr = sdf.format(cal.time)
            val yesterdaySummary = summaryByDate[yesterdayStr]
            if (yesterdaySummary?.isGoalAchieved != true) {
                return 0
            }
            // Yesterday achieved, so start streak from yesterday
            streak++
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }

        // Continue going backwards day by day
        while (true) {
            val dateStr = sdf.format(cal.time)
            val summary = summaryByDate[dateStr]
            if (summary?.isGoalAchieved == true) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }

        return streak
    }

    private fun calculateBestStreak(
        sortedSummaries: List<DailyHydrationSummary>
    ): Int {
        if (sortedSummaries.isEmpty()) return 0

        // Parse unique dates sorted chronologically (ascending)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateAchievement = sortedSummaries
            .mapNotNull { summary ->
                try {
                    val date = sdf.parse(summary.dateString)
                    if (date != null) Pair(date, summary.isGoalAchieved) else null
                } catch (_: Exception) {
                    null
                }
            }
            .sortedBy { it.first }

        var maxStreak = 0
        var currentStreak = 0
        var previousDate: Date? = null

        val calPrev = Calendar.getInstance()
        val calCurr = Calendar.getInstance()

        for ((date, isAchieved) in dateAchievement) {
            if (!isAchieved) {
                currentStreak = 0
                previousDate = date
                continue
            }

            if (previousDate == null) {
                currentStreak = 1
            } else {
                calPrev.time = previousDate
                calCurr.time = date
                calPrev.add(Calendar.DAY_OF_YEAR, 1)

                val isConsecutive = calPrev.get(Calendar.YEAR) == calCurr.get(Calendar.YEAR) &&
                        calPrev.get(Calendar.DAY_OF_YEAR) == calCurr.get(Calendar.DAY_OF_YEAR)

                if (isConsecutive) {
                    currentStreak++
                } else {
                    currentStreak = 1
                }
            }

            previousDate = date
            if (currentStreak > maxStreak) {
                maxStreak = currentStreak
            }
        }

        return maxStreak
    }

    /**
     * Evaluates whether an emergency safety alert should be triggered.
     * Criteria:
     * 1. Emergency contact phone must be configured.
     * 2. At least one water log must exist in history.
     * 3. Elapsed hours since last drink must meet or exceed [thresholdHours] (default: 6).
     * 4. Current hour must not be within quiet hours (active awake period).
     */
    fun shouldTriggerEmergencyAlert(
        currentTime: Long,
        currentHour: Int,
        settings: UserSettings,
        logs: List<WaterLog>,
        thresholdHours: Long = 6L
    ): Boolean {
        if (settings.emergencyContactPhone.isBlank()) return false
        if (settings.isInQuietHours(currentHour)) return false

        val hoursSince = calculateHoursSinceLastDrink(currentTime, logs) ?: return false
        return hoursSince >= thresholdHours
    }

    /**
     * Builds a standardized safety notification/SMS text for caregivers.
     */
    fun generateEmergencySmsMessage(userName: String, hoursSinceLastDrink: Long): String {
        val name = if (userName.isBlank()) "L'utilisateur" else userName.trim()
        return "⚠️ ALERTE HYDRATATION: $name n'a pas bu d'eau depuis $hoursSinceLastDrink heures. Merci de vérifier."
    }

    /**
     * Determines high-level hydration status given today's progress and activity.
     */
    fun getHydrationStatus(
        consumedTodayMl: Int,
        goalMl: Int,
        hoursSinceLastDrink: Long?,
        isInQuietHours: Boolean
    ): HydrationStatus {
        if (goalMl > 0 && consumedTodayMl >= goalMl) {
            return HydrationStatus.OPTIMAL
        }

        if (!isInQuietHours && hoursSinceLastDrink != null && hoursSinceLastDrink >= 6) {
            return HydrationStatus.INACTIVE
        }

        val percentage = if (goalMl > 0) ((consumedTodayMl.toFloat() / goalMl) * 100).toInt() else 0
        return if (percentage >= 50) {
            HydrationStatus.ON_TRACK
        } else {
            HydrationStatus.NEEDS_HYDRATION
        }
    }
}
