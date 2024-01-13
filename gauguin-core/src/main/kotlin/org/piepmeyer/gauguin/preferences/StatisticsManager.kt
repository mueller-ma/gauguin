package org.piepmeyer.gauguin.preferences

import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.statistics.Statistics

interface StatisticsManager {
    fun puzzleStartedToBePlayed()

    fun puzzleSolved(grid: Grid)

    fun storeStatisticsAfterFinishedGame(grid: Grid): String?

    fun storeStreak(isSolved: Boolean)

    fun currentStreak(): Int

    fun longestStreak(): Int

    fun totalStarted(): Int

    fun totalSolved(): Int

    fun totalHinted(): Int

    fun clearStatistics()

    fun statistics(): Statistics
}
