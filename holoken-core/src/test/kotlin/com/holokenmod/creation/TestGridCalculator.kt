package com.holokenmod.creation

import com.holokenmod.backtrack.hybrid.MathDokuCage2BackTrack
import com.holokenmod.grid.Grid
import com.holokenmod.grid.GridCell
import com.holokenmod.grid.GridSize
import com.holokenmod.options.GameOptionsVariant.Companion.createClassic
import com.holokenmod.options.GameVariant
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TestGridCalculator : FunSpec({
    test("3x3Grid") {
        val creator = GridCalculator(
            GameVariant(
                GridSize(9, 9),
                createClassic()
            )
        )
        val grid = creator.calculate()
        val backTrack = MathDokuCage2BackTrack(grid, false)

        withClue("There should be exactly one solution of $grid") {
            backTrack.solve() shouldBe 1
        }
    }

    test("bruteForce") {
        val creator = GridCalculator(
            GameVariant(
                GridSize(4, 4),
                createClassic()
            )
        )
        val grid = creator.calculate()
        solveBruteForce(grid, 0)
    }
})

private fun solveBruteForce(grid: Grid, cellNumber: Int) {
    if (cellNumber == grid.gridSize.surfaceArea) {
        if (isValidSolution(grid)) {
            println("Found valid solution.")
            for (cell in grid.cells) {
                withClue("Found differing solution. $grid") {
                    cell.userValue shouldBe cell.value
                }
            }
        }
        return
    }
    val cell = grid.getCell(cellNumber)
    for (value in grid.possibleDigits) {
        if (!grid.isUserValueUsedInSameColumn(cellNumber, value)
            && !grid.isUserValueUsedInSameRow(cellNumber, value)
        ) {
            cell.setUserValueIntern(value)
            solveBruteForce(grid, cellNumber + 1)
        }
    }
    cell.setUserValueIntern(GridCell.NO_VALUE_SET)
}

private fun isValidSolution(grid: Grid): Boolean {
    var validSolution = true
    for (cell in grid.cells) {
        validSolution =
            validSolution and !grid.isUserValueUsedInSameColumn(cell.cellNumber, cell.userValue)
        validSolution =
            validSolution and !grid.isUserValueUsedInSameRow(cell.cellNumber, cell.userValue)
    }
    for (cage in grid.cages) {
        validSolution = validSolution and cage.isMathsCorrect()
    }
    return validSolution
}