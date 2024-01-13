package org.piepmeyer.gauguin.preferences

import android.content.SharedPreferences
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.piepmeyer.gauguin.difficulty.GridDifficultyCalculator
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.statistics.Statistics
import java.io.File
import java.nio.charset.StandardCharsets

private val logger = KotlinLogging.logger {}

class StatisticsManagerImpl(
    directory: File,
    sharedPreferences: SharedPreferences,
) : StatisticsManager {
    private val statisticsFile = File(directory, "statistics.yaml")
    private val legacyManager = LegacyStatisticsManager(sharedPreferences)
    private var statistics: Statistics = loadStatistics()

    override fun puzzleStartedToBePlayed() {
        statistics.overall.gamesStarted++

        saveStatistics()
    }

    override fun puzzleSolved(grid: Grid) {
        statistics.overall.gamesSolvedWithoutHints++

        if (grid.isCheated()) {
            statistics.overall.gamesSolvedWithHints++
        }

        statistics.overall.solvedDifficulty.add(GridDifficultyCalculator(grid).calculate())
        statistics.overall.solvedDuration.add(grid.playTime.inWholeSeconds.toInt())

        saveStatistics()
    }

    override fun storeStatisticsAfterFinishedGame(grid: Grid): String? {
        return legacyManager.storeStatisticsAfterFinishedGame(grid)
    }

    override fun storeStreak(isSolved: Boolean) {
        val solvedStreak = currentStreak()
        val longestStreak = longestStreak()

        if (isSolved) {
            statistics.overall.streak++
            if (solvedStreak == longestStreak) {
                statistics.overall.longestStreak = statistics.overall.streak
            }
        } else {
            statistics.overall.solvedDifficulty.add(0.0)
            statistics.overall.solvedDuration.add(0)
            statistics.overall.streak = 0
        }

        saveStatistics()
    }

    private fun loadStatistics(): Statistics {
        if (!statisticsFile.exists()) {
            return migrateLegacyStatistics()
        }

        val fileData = statisticsFile.readText(StandardCharsets.UTF_8)

        return try {
            Json.decodeFromString<Statistics>(fileData)
        } catch (e: Exception) {
            logger.error(e) { "Error loading statistics: " + e.message }
            Statistics()
        }
    }

    private fun migrateLegacyStatistics(): Statistics {
        if (legacyManager.totalStarted() == 0) {
            return Statistics()
        }

        val stats = Statistics()

        stats.overall.gamesStarted = legacyManager.totalStarted()
        stats.overall.gamesSolvedWithHints = legacyManager.totalHinted()
        stats.overall.gamesSolvedWithoutHints = legacyManager.totalSolved()
        stats.overall.streak = legacyManager.currentStreak()
        stats.overall.longestStreak = legacyManager.longestStreak()

        return stats
    }

    private fun saveStatistics() {
        try {
            val result = Json.encodeToString(statistics)

            statisticsFile.writeText(result)
        } catch (e: Exception) {
            logger.error(e) { "Error saving statistics: " + e.message }
            return
        }
    }

    override fun currentStreak() = statistics.overall.streak

    override fun longestStreak() = statistics.overall.longestStreak

    override fun totalStarted() = statistics.overall.gamesStarted

    override fun totalSolved() = statistics.overall.gamesSolvedWithoutHints

    override fun totalHinted() = statistics.overall.gamesSolvedWithHints

    override fun clearStatistics() {
        statistics = Statistics()
        saveStatistics()

        legacyManager.clearStatistics()
    }

    override fun statistics(): Statistics {
        return statistics
    }
}
