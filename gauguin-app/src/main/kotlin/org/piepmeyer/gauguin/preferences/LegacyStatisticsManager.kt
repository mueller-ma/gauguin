package org.piepmeyer.gauguin.preferences

import android.content.SharedPreferences
import androidx.core.content.edit
import org.piepmeyer.gauguin.Utils
import org.piepmeyer.gauguin.grid.Grid
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds

class LegacyStatisticsManager(
    private val stats: SharedPreferences,
) {
    fun storeStatisticsAfterFinishedGame(grid: Grid): String? {
        if (grid.countCheated() > 0) {
            return null
        }

        val gridsize = grid.gridSize

        val timestat = stats.getLong("solvedtime$gridsize", 0).milliseconds
        val editor = stats.edit()
        val recordTime =
            if (timestat == ZERO || timestat > grid.playTime) {
                editor.putLong("solvedtime$gridsize", grid.playTime.inWholeMilliseconds)
                Utils.displayableGameDuration(grid.playTime)
            } else {
                null
            }
        editor.apply()
        return recordTime
    }

    fun currentStreak() = stats.getInt("solvedstreak", 0)

    fun longestStreak() = stats.getInt("longeststreak", 0)

    fun totalStarted() = stats.getInt("totalStarted", 0)

    fun totalSolved() = stats.getInt("totalSolved", 0)

    fun totalHinted() = stats.getInt("totalHinted", 0)

    fun clearStatistics() {
        stats.edit { clear() }
    }
}
