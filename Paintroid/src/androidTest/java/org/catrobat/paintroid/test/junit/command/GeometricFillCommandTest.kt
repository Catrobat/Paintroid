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
package org.catrobat.paintroid.test.junit.command

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.command.implementation.GeometricFillCommand
import org.catrobat.paintroid.model.Layer
import org.catrobat.paintroid.model.LayerModel
import org.catrobat.paintroid.test.utils.PaintroidAsserts
import org.catrobat.paintroid.tools.drawable.RectangleDrawable
import org.junit.Before
import org.junit.Test

class GeometricFillCommandTest {
    private lateinit var commandUnderTest: Command
    private lateinit var paintUnderTest: Paint
    private lateinit var canvasUnderTest: Canvas
    private lateinit var bitmapUnderTest: Bitmap
    private lateinit var canvasBitmapUnderTest: Bitmap

    companion object {
        private const val BITMAP_BASE_COLOR = Color.GREEN
        private const val PAINT_BASE_COLOR = Color.BLUE
        private const val INITIAL_HEIGHT = 80
        private const val INITIAL_WIDTH = 80
        private val RECT = RectF(0f, 0f, 20f, 20f)
    }

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
        paintUnderTest = Paint().apply {
            style = Paint.Style.FILL
            strokeJoin = Paint.Join.MITER
            color = PAINT_BASE_COLOR
            strokeWidth = 0f
        }
        layerModel.addLayerAt(0, layerUnderTest)
        layerModel.currentLayer = layerUnderTest
        commandUnderTest = GeometricFillCommand(RectangleDrawable(), 0, 0, RECT, 0f, paintUnderTest)
    }

    @Test
    fun testGeometricFigureOutOfBounds() {
        commandUnderTest = GeometricFillCommand(RectangleDrawable(), 0, 0, RectF(-20f, -20f, 20f, 20f), 0f, paintUnderTest)
        Canvas(bitmapUnderTest).drawRect(RECT, paintUnderTest)
        commandUnderTest.run(canvasUnderTest, LayerModel())
        PaintroidAsserts.assertBitmapEquals(bitmapUnderTest, canvasBitmapUnderTest)
    }

    @Test
    fun testRun() {
        Canvas(bitmapUnderTest).drawRect(RECT, paintUnderTest)
        commandUnderTest.run(canvasUnderTest, LayerModel())
        PaintroidAsserts.assertBitmapEquals(bitmapUnderTest, canvasBitmapUnderTest)
    }
}
