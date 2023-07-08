/*
 * Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.paintroid.tools.helper

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import androidx.annotation.VisibleForTesting
import java.util.LinkedList
import java.util.Queue

private const val UP = true
private const val DOWN = false

class JavaFillAlgorithm : FillAlgorithm {
    @VisibleForTesting
    lateinit var pixels: Array<IntArray>

    @VisibleForTesting
    lateinit var clickedPixel: Point

    @VisibleForTesting
    val ranges: Queue<Range> = LinkedList()

    @VisibleForTesting
    var targetColor = 0

    @VisibleForTesting
    var colorToBeReplaced = 0

    @VisibleForTesting
    var colorToleranceThresholdSquared = 0

    private lateinit var filledPixels: Array<BooleanArray>
    private lateinit var bitmap: Bitmap
    private var considerTolerance = false
    private var width = 0
    private var height = 0

    override fun setParameters(
        bitmap: Bitmap,
        clickedPixel: Point,
        targetColor: Int,
        replacementColor: Int,
        colorToleranceThreshold: Float
    ) {
        this.bitmap = bitmap
        this.width = bitmap.width
        this.height = bitmap.height
        pixels = Array(bitmap.height) { IntArray(bitmap.width) }
        for (i in 0 until height) {
            this.bitmap.getPixels(pixels[i], 0, width, 0, i, width, 1)
        }
        filledPixels = Array(bitmap.height) { BooleanArray(bitmap.width) }
        this.clickedPixel = clickedPixel
        this.targetColor = targetColor
        this.colorToBeReplaced = replacementColor
        colorToleranceThresholdSquared = square(colorToleranceThreshold.toInt())
        considerTolerance = colorToleranceThreshold > 0
    }

    override fun performFilling() {
        var range = generateRangeAndReplaceColor(
            clickedPixel.y, clickedPixel.x, UP
        )
        ranges.add(range)
        ranges.add(Range(range.line, range.start, range.end, DOWN))
        var row: Int
        while (!ranges.isEmpty()) {
            range = ranges.poll() ?: break
            val direction = range.direction
            val diff = if (direction == UP) -1 else 1
            row = range.line + diff
            if (row in 0 until height) {
                checkRangeAndGenerateNewRanges(range, row, direction)
            }
        }
    }

    private fun square(x: Int) = x * x

    private fun shouldCellBeFilled(row: Int, col: Int): Boolean =
        !filledPixels[row][col] && (
            pixels[row][col] == colorToBeReplaced || considerTolerance && isPixelWithinColorTolerance(
                pixels[row][col],
                    colorToBeReplaced
            )
            )

    private fun validateAndAssign(row: Int, col: Int): Boolean = if (shouldCellBeFilled(row, col)) {
        pixels[row][col] = targetColor
        filledPixels[row][col] = true
        true
    } else false

    private fun getStartIndex(row: Int, col: Int): Int {
        val start = (col downTo 0).find { !validateAndAssign(row, it) } ?: -1
        return start + 1
    }

    private fun getEndIndex(row: Int, col: Int): Int {
        val end = (col until width).find { !validateAndAssign(row, it) } ?: width
        return end - 1
    }

    private fun generateRangeAndReplaceColor(row: Int, col: Int, direction: Boolean): Range {
        val range = Range()
        pixels[row][col] = targetColor
        filledPixels[row][col] = true

        val start = getStartIndex(row, col - 1)
        val end = getEndIndex(row, col + 1)
        range.apply {
            line = row
            this.end = end
            this.start = start
            this.direction = direction
        }
        bitmap.setPixels(pixels[row], start, width, start, row, end - start + 1, 1)
        return range
    }

    private fun checkRangeAndGenerateNewRanges(range: Range, row: Int, directionUp: Boolean) {
        var newRange: Range
        var col = range.start
        while (col <= range.end) {
            if (shouldCellBeFilled(row, col)) {
                newRange = generateRangeAndReplaceColor(row, col, directionUp)
                ranges.add(newRange)
                if (newRange.start <= range.start - 2) {
                    ranges.add(Range(row, newRange.start, range.start - 2, !directionUp))
                }
                if (newRange.end >= range.end + 2) {
                    ranges.add(Range(row, range.end + 2, newRange.end, !directionUp))
                }
                col = if (newRange.end >= range.end - 1) {
                    break
                } else {
                    newRange.end + 1
                }
            }
            col++
        }
    }

    private fun isPixelWithinColorTolerance(pixel: Int, referenceColor: Int): Boolean {
        val redDiff = Color.red(pixel) - Color.red(referenceColor)
        val greenDiff = Color.green(pixel) - Color.green(referenceColor)
        val blueDiff = Color.blue(pixel) - Color.blue(referenceColor)
        val alphaDiff = Color.alpha(pixel) - Color.alpha(referenceColor)
        return (
            square(redDiff) + square(greenDiff) + square(blueDiff) + square(alphaDiff)
                <= colorToleranceThresholdSquared
            )
    }

    data class Range(
        var line: Int = 0,
        var start: Int = 0,
        var end: Int = 0,
        var direction: Boolean = false
    )
}
