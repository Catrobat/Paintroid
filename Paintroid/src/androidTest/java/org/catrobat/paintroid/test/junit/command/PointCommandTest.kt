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
import android.graphics.PointF
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.command.implementation.PointCommand
import org.catrobat.paintroid.model.Layer
import org.catrobat.paintroid.model.LayerModel
import org.catrobat.paintroid.test.utils.PaintroidAsserts
import org.junit.Before
import org.junit.Test

class PointCommandTest {
    private lateinit var commandUnderTest: Command
    private lateinit var paintUnderTest: Paint
    private lateinit var pointUnderTest: PointF
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
        pointUnderTest = PointF(
            (INITIAL_WIDTH / 2).toFloat(), (INITIAL_HEIGHT / 2).toFloat()
        )
        layerModel.addLayerAt(0, layerUnderTest)
        layerModel.currentLayer = layerUnderTest
        commandUnderTest = PointCommand(paintUnderTest, pointUnderTest)
    }

    @Test
    fun testRun() {
        bitmapUnderTest.setPixel(pointUnderTest.x.toInt(), pointUnderTest.y.toInt(), paintUnderTest.color)
        commandUnderTest.run(canvasUnderTest, LayerModel())
        PaintroidAsserts.assertBitmapEquals(bitmapUnderTest, canvasBitmapUnderTest)
    }

    @Test
    fun testRunOutOfBounds() {
        pointUnderTest = PointF(
            (canvasBitmapUnderTest.height + 1).toFloat(), (canvasBitmapUnderTest.width + 1).toFloat()
        )
        commandUnderTest = PointCommand(paintUnderTest, pointUnderTest)
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
