package org.piepmeyer.gauguin.statistics

import kotlinx.serialization.Serializable

@Serializable
data class Statistics(
    val overall: OverallStatistics = OverallStatistics(),
)

@Serializable
data class OverallStatistics(
    val solvedDifficulty: MutableList<Double> = mutableListOf(),
    val solvedDuration: MutableList<Int> = mutableListOf(),
    var gamesStarted: Int = 0,
    var gamesSolvedWithHints: Int = 0,
    var gamesSolvedWithoutHints: Int = 0,
    var streak: Int = 0,
    var longestStreak: Int = 0,
)
