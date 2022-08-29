/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
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
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Cap
import android.graphics.Path
import android.graphics.RectF
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.command.implementation.PathCommand
import org.catrobat.paintroid.model.Layer
import org.catrobat.paintroid.model.LayerModel
import org.catrobat.paintroid.test.utils.PaintroidAsserts
import org.junit.Before
import org.junit.Test

class PathCommandTest {
    private lateinit var commandUnderTest: Command
    private lateinit var paintUnderTest: Paint
    private lateinit var canvasUnderTest: Canvas
    private lateinit var bitmapUnderTest: Bitmap
    private lateinit var canvasBitmapUnderTest: Bitmap

    @Before
    fun setUp() {
        val layerModel = LayerModel()
        layerModel.width = INITIAL_WIDTH
        layerModel.height = INITIAL_HEIGHT
        canvasBitmapUnderTest = Bitmap.createBitmap(INITIAL_WIDTH, INITIAL_HEIGHT, Bitmap.Config.ARGB_8888)
        canvasBitmapUnderTest.eraseColor(BITMAP_BASE_COLOR)
        bitmapUnderTest = canvasBitmapUnderTest.copy(Bitmap.Config.ARGB_8888, true)

        val layerUnderTest = Layer(bitmapUnderTest)
        canvasUnderTest = Canvas()
        canvasUnderTest.setBitmap(canvasBitmapUnderTest)
        paintUnderTest = Paint()
        paintUnderTest.color = PAINT_BASE_COLOR
        paintUnderTest.strokeWidth = 0f
        paintUnderTest.style = Paint.Style.STROKE
        paintUnderTest.strokeCap = Cap.BUTT

        layerModel.addLayerAt(0, layerUnderTest)
        layerModel.currentLayer = layerUnderTest

        val pathUnderTest = Path()
        pathUnderTest.moveTo(1f, 0f)
        pathUnderTest.lineTo(1f, canvasBitmapUnderTest.height.toFloat())
        commandUnderTest = PathCommand(paintUnderTest, pathUnderTest)
    }

    @Test
    fun testPathOutOfBounds() {
        val path = Path()
        val left = (canvasBitmapUnderTest.width + 50).toFloat()
        val top = (canvasBitmapUnderTest.height + 50).toFloat()
        val right = (canvasBitmapUnderTest.width + 100).toFloat()
        val bottom = (canvasBitmapUnderTest.height + 100).toFloat()

        path.addRect(RectF(left, top, right, bottom), Path.Direction.CW)
        commandUnderTest = PathCommand(paintUnderTest, path)
        commandUnderTest.run(canvasUnderTest, LayerModel())
    }

    @Test
    fun testRun() {
        val color = paintUnderTest.color
        val height = bitmapUnderTest.height

        for (heightIndex in 0 until height) { bitmapUnderTest.setPixel(1, heightIndex, color) }
        commandUnderTest.run(canvasUnderTest, LayerModel())

        PaintroidAsserts.assertBitmapEquals(bitmapUnderTest, canvasBitmapUnderTest)
    }

    companion object {
        private const val BITMAP_BASE_COLOR = Color.GREEN
        private const val PAINT_BASE_COLOR = Color.BLUE
        private const val INITIAL_HEIGHT = 80
        private const val INITIAL_WIDTH = 80
    }
}
