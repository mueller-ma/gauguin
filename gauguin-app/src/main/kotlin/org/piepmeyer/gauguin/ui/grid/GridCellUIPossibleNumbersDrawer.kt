package org.piepmeyer.gauguin.ui.grid

import android.graphics.Canvas
import android.graphics.Paint
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.options.NumeralSystem
import org.piepmeyer.gauguin.preferences.ApplicationPreferences

class GridCellUIPossibleNumbersDrawer(
    private val cellUI: GridCellUI,
    private val paintHolder: GridPaintHolder,
) : KoinComponent {
    private val applicationPreferences: ApplicationPreferences by inject()
    private val cell = cellUI.cell

    fun drawPossibleNumbers(
        canvas: Canvas,
        possibleDigits: Set<Int>,
        cellSize: Float,
        layoutDetails: GridLayoutDetails,
        fastFinishMode: Boolean,
        numeralSystem: NumeralSystem,
    ) {
        if (cell.possibles.isEmpty()) return

        val possiblesPaint = paintHolder.possiblesPaint(cell, fastFinishMode)

        if (possibleDigits.size <= 9 && possibleDigits.max() <= 9 && applicationPreferences.show3x3Pencils()) {
            drawPossibleNumbersWithFixedGrid(canvas, possibleDigits, cellSize, possiblesPaint, layoutDetails, numeralSystem)
        } else {
            drawPossibleNumbersDynamically(canvas, cellSize, possiblesPaint, layoutDetails, numeralSystem)
        }
    }

    private fun drawPossibleNumbersDynamically(
        canvas: Canvas,
        cellSize: Float,
        paint: Paint,
        layoutDetails: GridLayoutDetails,
        numeralSystem: NumeralSystem,
    ) {
        val possiblesLines = adaptTextSize(paint, cellSize, layoutDetails, numeralSystem)

        var index = 0
        val metrics = paint.fontMetricsInt
        val lineHeigth = -metrics.ascent + metrics.leading + metrics.descent

        possiblesLines.forEach {
            canvas.drawText(
                it,
                cellUI.westPixel + layoutDetails.possibleNumbersMarginX(),
                cellUI.northPixel + cellSize - layoutDetails.possibleNumbersMarginY() - lineHeigth * index,
                paint,
            )
            index++
        }
    }

    private fun adaptTextSize(
        paint: Paint,
        cellSize: Float,
        layoutDetails: GridLayoutDetails,
        numeralSystem: NumeralSystem,
    ): List<String> {
        for (textDivider in listOf(4f, 4.25f, 4.5f, 4.75f)) {
            paint.textSize = (cellSize / textDivider).toInt().toFloat()
            val possiblesLines = calculatePossibleLines(paint, cellSize, layoutDetails, numeralSystem)

            if (possiblesLines.size <= 2) {
                return possiblesLines
            }
        }

        paint.textSize = (cellSize / 5.0f).toInt().toFloat()
        return calculatePossibleLines(paint, cellSize, layoutDetails, numeralSystem)
    }

    private fun calculatePossibleLines(
        paint: Paint,
        cellSize: Float,
        layoutDetails: GridLayoutDetails,
        numeralSystem: NumeralSystem,
    ): List<String> {
        if (cellSize < 35) {
            return listOf("...")
        }

        val possiblesLines = mutableListOf<MutableSet<String>>()

        // adds all possible to one line
        var currentLine =
            cell.possibles.sorted()
                .map { numeralSystem.displayableString(it) }
                .toMutableSet()

        possiblesLines += currentLine
        var currentLineText = getPossiblesLineText(currentLine)

        while (paint.measureText(currentLineText) > cellSize - 2 * layoutDetails.possibleNumbersMarginX()) {
            val newLine = mutableSetOf<String>()
            possiblesLines += newLine
            while (paint.measureText(currentLineText) > cellSize - 2 * layoutDetails.possibleNumbersMarginX()) {
                val firstDigitOfCurrentLine = currentLine.first()
                newLine.add(firstDigitOfCurrentLine)
                currentLine.remove(firstDigitOfCurrentLine)
                currentLineText = getPossiblesLineText(currentLine)
            }
            currentLine = newLine
            currentLineText = getPossiblesLineText(currentLine)
        }

        return possiblesLines.map { getPossiblesLineText(it) }
    }

    private fun drawPossibleNumbersWithFixedGrid(
        canvas: Canvas,
        possibleDigits: Set<Int>,
        cellSize: Float,
        paint: Paint,
        layoutDetails: GridLayoutDetails,
        numeralSystem: NumeralSystem,
    ) {
        paint.textSize = (cellSize / 4.75).toInt().toFloat()
        val xOffset = layoutDetails.possibleNumbersMarginX() * 2
        val yOffset = (cellSize / 1.9).toInt() + 1

        for (possible in cell.possibles) {
            val index = possibleDigits.indexOf(possible)

            canvas.drawText(
                numeralSystem.displayableString(possible),
                cellUI.westPixel + xOffset + index % 3 * layoutDetails.possiblesFixedGridDistanceX(),
                cellUI.northPixel + yOffset + index / 3 * layoutDetails.possiblesFixedGridDistanceY(),
                paint,
            )
        }
    }

    private fun getPossiblesLineText(possibles: Set<String>): String {
        return possibles.joinToString("|")
    }
}
