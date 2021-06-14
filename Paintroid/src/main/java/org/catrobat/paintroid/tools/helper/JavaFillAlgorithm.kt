/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2021 The Catrobat Team
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
    lateinit var ranges: Queue<Range>

    @VisibleForTesting
    @JvmField
    var targetColor = 0

    @VisibleForTesting
    @JvmField
    var replacementColor = 0

    @VisibleForTesting
    @JvmField
    var colorToleranceThresholdSquared = 0

    private lateinit var filledPixels: Array<BooleanArray>
    private var considerTolerance = false
    private lateinit var bitmap: Bitmap
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
        width = bitmap.width
        height = bitmap.height
        pixels = Array(bitmap.height) { IntArray(bitmap.width) }
        for (i in 0 until height) {
            this.bitmap.getPixels(pixels[i], 0, width, 0, i, width, 1)
        }
        filledPixels = Array(bitmap.height) { BooleanArray(bitmap.width) }
        this.clickedPixel = clickedPixel
        this.targetColor = targetColor
        this.replacementColor = replacementColor
        ranges = LinkedList()
        colorToleranceThresholdSquared = (colorToleranceThreshold * colorToleranceThreshold).toInt()
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
            ranges.poll()?.let {
                range = it
            }
            if (range.direction == UP) {
                row = range.line - 1
                if (row >= 0) {
                    checkRangeAndGenerateNewRanges(range, row, UP)
                }
            } else {
                row = range.line + 1
                if (row < height) {
                    checkRangeAndGenerateNewRanges(range, row, DOWN)
                }
            }
        }
    }

    private fun checkCell(row: Int, col: Int): Boolean =
        !filledPixels[row][col] && (
            pixels[row][col] == replacementColor ||
                considerTolerance && isPixelWithinColorTolerance(
                pixels[row][col],
                replacementColor
            )
            )

    private fun validateAndAssign(row: Int, col: Int): Boolean = if (checkCell(row, col)) {
        pixels[row][col] = targetColor
        filledPixels[row][col] = true
        true
    } else false

    private fun getStartIndex(row: Int, col: Int): Int {
        var start = col
        for (i in col downTo 0) {
            if (!validateAndAssign(row, i)) {
                break
            }
            start = i - 1
        }
        return start + 1
    }

    private fun getEndIndex(row: Int, col: Int): Int {
        var end = col
        for (i in col until width) {
            if (!validateAndAssign(row, i)) {
                break
            }
            end = i + 1
        }
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
            if (checkCell(row, col)) {
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
        val redDiff = (pixel shr 16 and 0xFF) - (referenceColor shr 16 and 0xFF)
        val greenDiff = (pixel shr 8 and 0xFF) - (referenceColor shr 8 and 0xFF)
        val blueDiff = (pixel and 0xFF) - (referenceColor and 0xFF)
        val alphaDiff = (pixel ushr 24) - (referenceColor ushr 24)
        return (
            redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff + alphaDiff * alphaDiff
                <= colorToleranceThresholdSquared
            )
    }

    inner class Range {
        var line: Int
        var start: Int
        var end: Int
        var direction: Boolean

        constructor(line: Int, start: Int, end: Int, directionUp: Boolean) {
            this.line = line
            this.start = start
            this.end = end
            direction = directionUp
        }

        constructor() {
            line = 0
            start = 0
            end = 0
            direction = false
        }
    }
}
