package com.holokenmod.backtrack.hybrid

import com.holokenmod.GridSolver
import com.holokenmod.creation.GridBuilder
import com.holokenmod.creation.cage.GridCageType
import com.holokenmod.grid.GridCageAction
import com.holokenmod.options.DigitSetting
import com.srlee.dlx.MathDokuDLXSolver
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.IsStableType
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class TestCageBacktrack : FunSpec({
    val solverFactories = listOf(DlxFactory(), Cage2BackTrackFactory())

    context("3x3 grid 1") {
        withData(solverFactories) { solverFactory ->
            /*  |     1-  0 |     3x  1 |         1 |
                |         0 |     4x  2 |         2 |
                |     3/  3 |         3 |         2 | */
            val builder = GridBuilder(3)
            builder.addCage(1, GridCageAction.ACTION_SUBTRACT, GridCageType.DOUBLE_VERTICAL, 0)
                .addCage(3, GridCageAction.ACTION_MULTIPLY, GridCageType.DOUBLE_HORIZONTAL, 1)
                .addCage(4, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_LEFT_BOTTOM, 4)
                .addCage(3, GridCageAction.ACTION_DIVIDE, GridCageType.DOUBLE_HORIZONTAL, 6)
            val grid = builder.createGrid()
            println(grid.toString())
            grid.clearUserValues()

            solverFactory.createSolver().solve(grid, false) shouldBe 1
        }
    }

    context("3x3 grid 2") {
        withData(solverFactories) { solverFactory ->
            /*  |     3x  0 |         0 |    12x  1 |
                |     5+  2 |         0 |         1 |
                |         2 |         1 |         1 | */
            val builder = GridBuilder(3)
            builder.addCage(3, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_LEFT_BOTTOM, 0)
                .addCage(
                    12,
                    GridCageAction.ACTION_MULTIPLY,
                    GridCageType.L_VERTICAL_SHORT_LEFT_BOTTOM,
                    2
                )
                .addCage(5, GridCageAction.ACTION_ADD, GridCageType.DOUBLE_VERTICAL, 3)
            val grid = builder.createGrid()
            println(grid.toString())
            grid.clearUserValues()

            solverFactory.createSolver().solve(grid, false) shouldBe 1
        }
    }

    context("3x3 grid 3") {
        withData(solverFactories) { solverFactory ->
            /*  |     6+  0 |     7+  1 |         1 |
                |         0 |         1 |         1 |
                |         0 |     6x  2 |         2 | */
            val builder = GridBuilder(3)
            builder.addCage(6, GridCageAction.ACTION_ADD, GridCageType.TRIPLE_VERTICAL, 0)
                .addCage(7, GridCageAction.ACTION_ADD, GridCageType.SQUARE, 1)
                .addCage(6, GridCageAction.ACTION_MULTIPLY, GridCageType.DOUBLE_HORIZONTAL, 7)
            val grid = builder.createGrid()
            println(grid.toString())
            grid.clearUserValues()

            solverFactory.createSolver().solve(grid, false) shouldBe 2
        }
    }

    context("4x4 grid 1") {
        withData(solverFactories) { solverFactory ->
            /*  |     2/  0 |         0 |     3+  1 |         1 |
    			|     0x  2 |     6+  3 |         3 |         3 |
   			 	|         2 |         2 |     6+  4 |         3 |
    			|     3-  5 |         5 |         4 |         4 |*/
            val builder = GridBuilder(4, DigitSetting.FIRST_DIGIT_ZERO)
            builder.addCage(2, GridCageAction.ACTION_DIVIDE, GridCageType.DOUBLE_HORIZONTAL, 0)
                .addCage(3, GridCageAction.ACTION_ADD, GridCageType.DOUBLE_HORIZONTAL, 2)
                .addCage(0, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_RIGHT_TOP, 4)
                .addCage(
                    6,
                    GridCageAction.ACTION_ADD,
                    GridCageType.L_HORIZONTAL_SHORT_RIGHT_BOTTOM,
                    5
                )
                .addCage(6, GridCageAction.ACTION_ADD, GridCageType.ANGLE_RIGHT_TOP, 10)
                .addCage(3, GridCageAction.ACTION_SUBTRACT, GridCageType.DOUBLE_HORIZONTAL, 12)
            val grid = builder.createGrid()
            println(grid.toString())
            grid.clearUserValues()

            solverFactory.createSolver().solve(grid, false) shouldBe 2
        }
    }

    context("4x4 grid 2") {
        withData(solverFactories) { solverFactory ->
            /*  |     6x  0 |     4+  1 |     2/  2 |     0x  3 |
                |         0 |         1 |         2 |         3 |
                |     0x  4 |         1 |         3 |         3 |
                |         4 |         4 |     3x  5 |         5 | */
            val builder = GridBuilder(4, DigitSetting.FIRST_DIGIT_ZERO)

            builder.addCage(6, GridCageAction.ACTION_MULTIPLY, GridCageType.DOUBLE_VERTICAL, 0)
                .addCage(4, GridCageAction.ACTION_ADD, GridCageType.TRIPLE_VERTICAL, 1)
                .addCage(2, GridCageAction.ACTION_DIVIDE, GridCageType.DOUBLE_VERTICAL, 2)
                .addCage(
                    0,
                    GridCageAction.ACTION_MULTIPLY,
                    GridCageType.L_VERTICAL_SHORT_LEFT_BOTTOM,
                    3
                )
                .addCage(0, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_RIGHT_TOP, 8)
                .addCage(3, GridCageAction.ACTION_MULTIPLY, GridCageType.DOUBLE_HORIZONTAL, 14)

            val grid = builder.createGrid()
            println(grid.toString())

            solverFactory.createSolver().solve(grid, false) shouldBe 2
        }
    }

    context("4x4 grid 3") {
        withData(solverFactories) { solverFactory ->
            /*  |     1-  0 |     0x  1 |         1 |     6x  2 |
                |         0 |         1 |         2 |         2 |
                |     4+  3 |         3 |         3 |     3-  4 |
                |     5+  5 |         5 |         3 |         4 |*/
            val builder = GridBuilder(4, DigitSetting.FIRST_DIGIT_ZERO)
            builder.addCage(1, GridCageAction.ACTION_SUBTRACT, GridCageType.DOUBLE_VERTICAL, 0)
                .addCage(0, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_RIGHT_BOTTOM, 1)
                .addCage(6, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_LEFT_TOP, 3)
                .addCage(
                    4,
                    GridCageAction.ACTION_ADD,
                    GridCageType.L_HORIZONTAL_SHORT_RIGHT_BOTTOM,
                    8
                )
                .addCage(3, GridCageAction.ACTION_SUBTRACT, GridCageType.DOUBLE_VERTICAL, 11)
                .addCage(5, GridCageAction.ACTION_ADD, GridCageType.DOUBLE_HORIZONTAL, 12)
            val grid = builder.createGrid()
            println(grid.toString())

            solverFactory.createSolver().solve(grid, false) shouldBe 2
        }
    }

    context("4x4 grid 4") {
        withData(solverFactories) { solverFactory ->
            /*  |    12x  0 |         0 |     1-  1 |     0x  2 |
                |         0 |     4+  3 |         1 |         2 |
                |         0 |         3 |    12x  4 |         2 |
                |         3 |         3 |         4 |         4 |*/
            val builder = GridBuilder(4, DigitSetting.FIRST_DIGIT_ZERO)
            builder.addCage(12, GridCageAction.ACTION_MULTIPLY, GridCageType.L_VERTICAL_SHORT_RIGHT_TOP, 0)
                .addCage(1, GridCageAction.ACTION_SUBTRACT, GridCageType.DOUBLE_VERTICAL, 2)
                .addCage(0, GridCageAction.ACTION_MULTIPLY, GridCageType.TRIPLE_VERTICAL, 3)
                .addCage(4, GridCageAction.ACTION_ADD, GridCageType.L_VERTICAL_SHORT_LEFT_BOTTOM, 5)
                .addCage(12, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_RIGHT_TOP, 10)

            val grid = builder.createGrid()
            println(grid.toString())

            solverFactory.createSolver().solve(grid, false) shouldBe 2
        }
    }
})

@IsStableType
class DlxFactory: SolverFactory {
    override fun createSolver(): GridSolver {
        return MathDokuDLXSolver()
    }

    override fun toString(): String {
        return "DLX"
    }
}

@IsStableType
class Cage2BackTrackFactory: SolverFactory {
    override fun createSolver(): GridSolver {
        return MathDokuCage2BackTrackSolver()
    }

    override fun toString(): String {
        return "cage2BackTrack"
    }
}
