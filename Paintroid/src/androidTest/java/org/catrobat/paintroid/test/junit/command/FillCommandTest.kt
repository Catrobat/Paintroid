/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.paintroid.test.junit.command

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.graphics.Point

import java.util.LinkedList
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.command.implementation.FillCommand
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.model.Layer
import org.catrobat.paintroid.model.LayerModel
import org.catrobat.paintroid.tools.helper.JavaFillAlgorithmFactory
import org.catrobat.paintroid.tools.implementation.MAX_ABSOLUTE_TOLERANCE
import org.hamcrest.Matchers.lessThan
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FillCommandTest {
    private var layerModel: LayerContracts.Model? = null
    private var bitmapUnderTest: Bitmap? = null

    @Before
    fun setUp() {
        bitmapUnderTest =
            Bitmap.createBitmap(INITIAL_WIDTH, INITIAL_HEIGHT, Bitmap.Config.ARGB_8888)
        layerModel = LayerModel()
        val layer = bitmapUnderTest?.let { Layer(it) }
        if (layer != null) {
            (layerModel as LayerModel).addLayerAt(0, layer)
        }
        (layerModel as LayerModel).currentLayer = layer
    }

    @Test
    fun testFillingOnEmptyBitmap() {
        val width = 10
        val height = 20
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.WHITE)
        }
        layerModel?.currentLayer?.bitmap = bitmap
        val clickedPixel = Point(width / 2, height / 2)
        val targetColor = Color.BLACK
        val paint = Paint().apply {
            color = targetColor
        }
        val fillCommand = FillCommand(JavaFillAlgorithmFactory(), clickedPixel, paint, NO_TOLERANCE)
        layerModel?.let { fillCommand.run(Canvas(), it) }
        val pixels = getPixelsFromBitmap(bitmap)
        assertEquals("Wrong array size", height, pixels.size)
        assertEquals("Wrong array size", width, pixels[0].size)
        for (row in 0 until height) {
            for (col in 0 until width) {
                assertEquals("Color should have been replaced", targetColor, pixels[row][col])
            }
        }
    }

    @Test
    fun testFillingOnNotEmptyBitmap() {
        val width = 6
        val height = 8
        val clickedPixel = Point(width / 2, height / 2)
        val targetColor = Color.GREEN
        val boundaryColor = Color.RED
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        layerModel?.currentLayer?.bitmap = bitmap
        val paint = Paint().apply {
            color = targetColor
        }
        val pixels = getPixelsFromBitmap(bitmap)
        pixels[0][1] = boundaryColor
        pixels[1][0] = boundaryColor
        putPixelsToBitmap(bitmap, pixels)
        val fillCommand = FillCommand(JavaFillAlgorithmFactory(), clickedPixel, paint, NO_TOLERANCE)
        layerModel?.let { fillCommand.run(Canvas(), it) }
        val updatedPixels = getPixelsFromBitmap(bitmap)
        assertEquals("Color of upper left pixel should not have been replaced", 0, updatedPixels[0][0])
        assertEquals("Boundary color should not have been replaced", boundaryColor, updatedPixels[0][1])
        assertEquals("Boundary color should not have been replaced", boundaryColor, updatedPixels[1][0])
        assertEquals("Pixel color should have been replaced", targetColor, updatedPixels[1][1])
        for (row in 0 until height) {
            for (col in 0 until width) {
                if (row > 1 || col > 1) {
                    assertEquals("Pixel color should have been replaced", targetColor, updatedPixels[row][col])
                }
            }
        }
    }

    @Test
    fun testFillingWithMaxColorTolerance() {
        val width = 6
        val height = 8
        val clickedPixel = Point(width / 2, height / 2)
        val targetColor = Color.argb(0xFF, 0xFF, 0xFF, 0xFF)
        val replacementColor = Color.TRANSPARENT
        val maxTolerancePerChannel = 0xFF
        val boundaryColor = Color.argb(maxTolerancePerChannel, maxTolerancePerChannel, maxTolerancePerChannel, maxTolerancePerChannel)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        layerModel?.currentLayer?.bitmap = bitmap
        bitmap.eraseColor(replacementColor)
        val paint = Paint().apply {
            color = targetColor
        }
        val pixels = getPixelsFromBitmap(bitmap)
        pixels[0][1] = boundaryColor
        pixels[1][0] = boundaryColor
        putPixelsToBitmap(bitmap, pixels)
        val fillCommand = FillCommand(JavaFillAlgorithmFactory(), clickedPixel, paint, MAX_TOLERANCE)
        layerModel?.let { fillCommand.run(Canvas(), it) }
        val updatedPixels = getPixelsFromBitmap(bitmap)
        for (row in 0 until height) {
            for (col in 0 until width) {
                assertEquals("Pixel color should have been replaced", targetColor, updatedPixels[row][col])
            }
        }
    }

    @Test
    fun testFillingWhenOutOfTolerance() {
        val width = 6
        val height = 8
        val clickedPixel = Point(width / 2, height / 2)
        val targetColor = Color.argb(0xFF, 0xFF, 0xFF, 0xFF)
        val replacementColor = Color.TRANSPARENT
        val maxTolerancePerChannel = 0xFF
        val boundaryColor = Color.argb(maxTolerancePerChannel, maxTolerancePerChannel, maxTolerancePerChannel, maxTolerancePerChannel)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        layerModel?.currentLayer?.bitmap = bitmap
        bitmap.eraseColor(replacementColor)
        val paint = Paint().apply {
            color = targetColor
        }
        var pixels = getPixelsFromBitmap(bitmap)
        pixels[0][1] = boundaryColor
        pixels[1][0] = boundaryColor
        putPixelsToBitmap(bitmap, pixels)
        val fillCommand = FillCommand(JavaFillAlgorithmFactory(), clickedPixel, paint, MAX_TOLERANCE - 1)
        layerModel?.let { fillCommand.run(Canvas(), it) }
        val updatedPixels = getPixelsFromBitmap(bitmap)
        for (row in 0 until height) {
            for (col in 0 until width) {
                if (row == 0 && col == 0) {
                    assertNotEquals("Pixel color should not have been replaced", targetColor, updatedPixels[row][col])
                } else {
                    assertEquals("Pixel color should have been replaced", targetColor, updatedPixels[row][col])
                }
            }
        }
    }

    @Test
    fun testEqualTargetAndReplacementColorWithTolerance() {
        val width = 8
        val height = 8
        val clickedPixel = Point(width / 2, height / 2)
        val boundaryPixel = Point(width / 4, height / 4)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        layerModel?.currentLayer?.bitmap = bitmap
        val targetColor = 0
        val boundaryColor = Color.argb(0xFF, 0xFF, 0xFF, 0xFF)
        bitmap.eraseColor(targetColor)
        val paint = Paint().apply {
            color = targetColor
        }
        val pixels = getPixelsFromBitmap(bitmap)
        pixels[boundaryPixel.x][boundaryPixel.y] = boundaryColor
        putPixelsToBitmap(bitmap, pixels)
        val fillCommand = FillCommand(JavaFillAlgorithmFactory(), clickedPixel, paint, HALF_TOLERANCE)
        layerModel?.let { fillCommand.run(Canvas(), it) }
        val updatedPixels = getPixelsFromBitmap(bitmap)
        for (row in 0 until height) {
            for (col in 0 until width) {
                if (row == boundaryPixel.y && col == boundaryPixel.x) {
                    assertEquals("Pixel color should not have been replaced", boundaryColor, updatedPixels[row][col])
                } else {
                    assertEquals("Pixel color should have been replaced", targetColor, updatedPixels[row][col])
                }
            }
        }
    }

    @Test
    fun testFillingWhenTargetColorIsWithinTolerance() {
        val targetColor = -0x551156
        val boundaryColor = -0x10000
        val replacementColor = -0x1
        val height = 8
        val width = 8
        val topLeftQuarterPixel = Point(width / 4, height / 4)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        layerModel?.currentLayer?.bitmap = bitmap
        bitmap.eraseColor(replacementColor)
        val paint = Paint().apply {
            color = targetColor
        }
        val pixels = getPixelsFromBitmap(bitmap)
        for (col in 0 until width) {
            pixels[height / 2][col] = targetColor
        }
        val boundaryPixel = Point(width / 2, height / 4)
        pixels[boundaryPixel.y][boundaryPixel.x] = boundaryColor
        putPixelsToBitmap(bitmap, pixels)
        val fillCommand = FillCommand(JavaFillAlgorithmFactory(), topLeftQuarterPixel, paint, HALF_TOLERANCE)
        layerModel?.let { fillCommand.run(Canvas(), it) }
        val actualPixels = getPixelsFromBitmap(bitmap)
        for (row in 0 until height) {
            for (col in 0 until width) {
                if (row == boundaryPixel.y && col == boundaryPixel.x) {
                    assertEquals("Wrong pixel color for boundary pixel", boundaryColor, actualPixels[row][col])
                } else {
                    assertEquals("Wrong pixel color for pixel[$row][$col]", targetColor, actualPixels[row][col])
                }
            }
        }
    }

    @Test
    fun testFillingWithSpiral() {
        val targetColor = -0x551156
        val boundaryColor = -0x10000
        val replacementColor = -0x1
        val pixels = createPixelArrayAndDrawSpiral(replacementColor, boundaryColor)
        val height = pixels.size
        val width = pixels[0].size
        val clickedPixel = Point(1, 1)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        layerModel?.currentLayer?.bitmap = bitmap
        bitmap.eraseColor(replacementColor)
        val paint = Paint().apply {
            color = targetColor
        }
        putPixelsToBitmap(bitmap, pixels)
        val fillCommand = FillCommand(JavaFillAlgorithmFactory(), clickedPixel, paint, HALF_TOLERANCE)
        layerModel?.let { fillCommand.run(Canvas(), it) }
        val actualPixels = getPixelsFromBitmap(bitmap)
        val expectedPixels = createPixelArrayAndDrawSpiral(targetColor, boundaryColor)
        for (row in 0 until height) {
            for (col in 0 until width) {
                assertEquals("Wrong pixel color for pixels[$row][$col]", expectedPixels[row][col], actualPixels[row][col])
            }
        }
    }

    @Test
    fun testComplexDrawing() {
        val targetColor = -0x551156
        val boundaryColor = -0x10000
        val replacementColor = -0x1
        val paint = Paint().apply {
            color = targetColor
        }
        val height = createPixelArrayForComplexTest(replacementColor, boundaryColor).size
        val width = createPixelArrayForComplexTest(replacementColor, boundaryColor)[0].size
        val clickedPixels = listOf(Point(0, 0), Point(width - 1, 0), Point(width - 1, height - 1), Point(0, height - 1))
        for (clickedPixel in clickedPixels) {
            val pixels = createPixelArrayForComplexTest(replacementColor, boundaryColor)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            layerModel?.currentLayer?.bitmap = bitmap
            bitmap.eraseColor(replacementColor)
            putPixelsToBitmap(bitmap, pixels)
            val fillCommand = FillCommand(JavaFillAlgorithmFactory(), clickedPixel, paint, HALF_TOLERANCE)
            layerModel?.let { fillCommand.run(Canvas(), it) }
            val actualPixels = getPixelsFromBitmap(bitmap)
            val expectedPixels = createPixelArrayForComplexTest(targetColor, boundaryColor)
            for (row in pixels.indices) {
                for (col in 0 until pixels[0].size) {
                    assertEquals("Wrong pixel color, clicked ${clickedPixel.x}/${clickedPixel.y}", expectedPixels[row][col], actualPixels[row][col])
                }
            }
        }
    }

    @Test
    fun testSkipPixelsInCheckRangesFunction() {
        val targetColor = -0x551156
        val boundaryColor = -0x10000
        val replacementColor = -0x1
        val paint = Paint().apply {
            color = targetColor
        }
        val clickedPixel = Point(0, 0)
        val pixels = createPixelArrayForSkipPixelTest(replacementColor, boundaryColor)
        val height = pixels.size
        val width = pixels[0].size
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        layerModel?.currentLayer?.bitmap = bitmap
        bitmap.eraseColor(replacementColor)
        putPixelsToBitmap(bitmap, pixels)
        val fillCommand = FillCommand(JavaFillAlgorithmFactory(), clickedPixel, paint, HALF_TOLERANCE)
        layerModel?.let { fillCommand.run(Canvas(), it) }
        val actualPixels = getPixelsFromBitmap(bitmap)
        val expectedPixels = createPixelArrayForSkipPixelTest(targetColor, boundaryColor)
        for (row in 0 until height) {
            for (col in 0 until width) {
                assertEquals("Wrong pixel color", expectedPixels[row][col], actualPixels[row][col])
            }
        }
    }

    @Ignore("Flaky test, sometimes fails on Jenkins. runtime.gc() is not an assurance that memory will be freed might.")
    @Test
    fun testCommandsDoNotLeakMemory() {
        val commands: MutableList<Command> = LinkedList()
        val testBitmap = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888)
        val clickedPixel = Point(10, 10)
        val paint = Paint()
        val canvas = Canvas()
        val layerModel = LayerModel()
        val testLayer = Layer(testBitmap)
        layerModel.addLayerAt(0, testLayer)
        layerModel.currentLayer = testLayer
        val runtime = Runtime.getRuntime()
        runtime.gc()
        val memoryBefore = runtime.totalMemory() - runtime.freeMemory()
        for (i in 0..4) {
            val fillCommand = FillCommand(JavaFillAlgorithmFactory(), clickedPixel, paint, 0.5f)
            fillCommand.run(canvas, layerModel)
            commands.add(fillCommand)
        }
        runtime.gc()
        val memoryAfter = runtime.totalMemory() - runtime.freeMemory()
        assertThat(memoryAfter / 1024, lessThan(memoryBefore / 1024 + 10))
    }

    private fun createPixelArrayForComplexTest(backgroundColor: Int, boundaryColor: Int): Array<IntArray> {
        val w = boundaryColor
        val i = backgroundColor

        return arrayOf(
            intArrayOf(i, i, i, i, i, i, i, i, i, i, i, i, i, i, i, i),
            intArrayOf(i, i, i, i, i, w, w, w, i, i, i, w, w, w, i, i),
            intArrayOf(i, i, i, i, i, i, w, i, i, i, w, i, i, i, w, i),
            intArrayOf(i, i, i, w, i, i, w, i, i, i, w, i, i, i, w, i),
            intArrayOf(i, i, w, i, i, w, i, w, i, i, i, i, i, i, w, i),
            intArrayOf(i, i, w, i, i, i, i, w, i, i, i, i, i, w, i, i),
            intArrayOf(i, i, w, w, w, i, w, i, i, i, w, i, i, i, w, i),
            intArrayOf(i, i, w, i, i, i, w, i, i, i, w, w, w, w, w, i),
            intArrayOf(w, i, i, w, w, w, i, i, i, i, i, i, i, i, i, i),
            intArrayOf(i, w, i, i, i, i, i, i, i, i, i, w, w, w, i, i),
            intArrayOf(i, i, i, i, i, i, i, i, i, i, i, i, w, i, i, i)
        )
    }

    private fun createPixelArrayForSkipPixelTest(backgroundColor: Int, boundaryColor: Int): Array<IntArray> {
        val w = boundaryColor
        val i = backgroundColor

        return arrayOf(
            intArrayOf(i, i, i, i, w),
            intArrayOf(i, i, w, i, w),
            intArrayOf(i, w, i, i, w),
            intArrayOf(i, i, w, w, i),
            intArrayOf(i, i, i, i, i)
        )
    }

    private fun createPixelArrayAndDrawSpiral(backgroundColor: Int, boundaryColor: Int): Array<IntArray> {
        val size = 10
        val pixels = Array(size) { IntArray(size) { backgroundColor } }
        val spiralPoints = listOf(
            Pair(4, 4), Pair(5, 4), Pair(5, 5), Pair(4, 6), Pair(3, 6), Pair(2, 5),
            Pair(2, 4), Pair(2, 3), Pair(3, 2), Pair(4, 2), Pair(5, 2), Pair(6, 2),
            Pair(7, 3), Pair(7, 4)
        )
        for ((x, y) in spiralPoints) {
            pixels[y][x] = boundaryColor
        }
        return pixels
    }

    private fun getPixelsFromBitmap(bitmap: Bitmap): Array<IntArray> {
        val pixels = Array(bitmap.height) {
            IntArray(bitmap.width)
        }
        for (i in 0 until bitmap.height) {
            bitmap.getPixels(pixels[i], 0, bitmap.width, 0, i, bitmap.width, 1)
        }
        return pixels
    }

    private fun putPixelsToBitmap(bitmap: Bitmap, pixels: Array<IntArray>) {
        val height = bitmap.height
        val width = bitmap.width
        assertEquals("Height is inconsistent", height, pixels.size)
        assertEquals("Width is inconsistent", width, pixels[0].size)
        for (i in 0 until height) {
            bitmap.setPixels(pixels[i], 0, width, 0, i, width, 1)
        }
    }

    companion object {
        private const val NO_TOLERANCE = 0.0f
        private const val HALF_TOLERANCE: Float = MAX_ABSOLUTE_TOLERANCE / 2.0f
        private const val MAX_TOLERANCE: Float = MAX_ABSOLUTE_TOLERANCE.toFloat()
        private const val INITIAL_HEIGHT = 80
        private const val INITIAL_WIDTH = 80
    }
}
