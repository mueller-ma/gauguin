package org.piepmeyer.gauguin.creation

import io.github.oshai.kotlinlogging.KotlinLogging
import org.piepmeyer.gauguin.RandomSingleton
import org.piepmeyer.gauguin.Randomizer
import org.piepmeyer.gauguin.creation.dlx.DLX
import org.piepmeyer.gauguin.creation.dlx.MathDokuDLX
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.options.GameVariant

private val logger = KotlinLogging.logger {}

class GridCalculator(
    private val randomizer: Randomizer,
    private val shuffler: PossibleDigitsShuffler,
    private val variant: GameVariant,
) {
    constructor(variant: GameVariant) : this(
        RandomSingleton.instance,
        RandomPossibleDigitsShuffler(),
        variant,
    )

    suspend fun calculate(): Grid {
        var dlxNumber: Int
        var numAttempts = 0
        var sumDLXDuration: Long = 0

        var grid: Grid

        do {
            grid = GridCreator(variant, randomizer, shuffler).createRandomizedGridWithCages()
            numAttempts++
            val dlxMillis = System.currentTimeMillis()
            val mdd = MathDokuDLX(grid)

            logger.debug { grid }
            // Stop solving as soon as we find multiple solutions
            dlxNumber = mdd.solve(DLX.SolveType.MULTIPLE)
            val dlxDuration = System.currentTimeMillis() - dlxMillis
            sumDLXDuration += dlxDuration
            logger.info { "DLX Num Solns = $dlxNumber in $dlxDuration ms" }

            if (dlxNumber == 0) {
                logger.debug { grid.toString() }
            }
        } while (dlxNumber != 1)

        val averageDLX = sumDLXDuration / numAttempts
        logger.info { "DLX Num Attempts = $numAttempts in $sumDLXDuration ms (average $averageDLX ms)" }

        grid.clearUserValues()

        return grid
    }
}
