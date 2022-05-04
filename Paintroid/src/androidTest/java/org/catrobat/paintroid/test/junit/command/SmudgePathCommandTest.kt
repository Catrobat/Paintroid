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
package org.catrobat.paintroid.test.junit.command

import android.graphics.Canvas
import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.Color
import android.graphics.Paint
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.RectF
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.command.implementation.SmudgePathCommand
import org.catrobat.paintroid.model.Layer
import org.catrobat.paintroid.model.LayerModel
import org.catrobat.paintroid.test.utils.PaintroidAsserts
import org.junit.Before
import org.junit.Test

class SmudgePathCommandTest {
    private lateinit var commandUnderTest: Command
    private lateinit var canvasUnderTest: Canvas
    private lateinit var bitmapUnderTest: Bitmap
    private lateinit var canvasBitmapUnderTest: Bitmap
    private lateinit var commandBitmap: Bitmap
    private lateinit var commandPath: MutableList<PointF>
    private var commandPressure = 1f
    private var commandMaxSize = 25f
    private var commandMinSize = 25f

    companion object {
        private const val BITMAP_BASE_COLOR = Color.WHITE
        private const val COMMAND_BITMAP_BASE_COLOR = Color.GREEN
        private const val INITIAL_HEIGHT = 80
        private const val INITIAL_WIDTH = 80
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
        layerModel.addLayerAt(0, layerUnderTest)
        layerModel.currentLayer = layerUnderTest

        commandBitmap = Bitmap.createBitmap(commandMaxSize.toInt(), commandMaxSize.toInt(), Bitmap.Config.ARGB_8888)
        commandBitmap.eraseColor(COMMAND_BITMAP_BASE_COLOR)
        commandPath = mutableListOf()
        commandPath.add(PointF(40f, 30f))
        commandPath.add(PointF(40f, 34f))
        commandPath.add(PointF(40f, 38f))
        commandPath.add(PointF(40f, 42f))
        commandPath.add(PointF(40f, 46f))
        commandPath.add(PointF(40f, 50f))
        commandPath.add(PointF(40f, 54f))
        commandUnderTest = SmudgePathCommand(commandBitmap, commandPath, commandPressure, commandMaxSize, commandMinSize)
    }

    @Test
    fun testRun() {
        var bitmap = commandBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val colorMatrix = ColorMatrix()
        val paint = Paint()
        var pressure = commandPressure
        commandPath.forEach {
            colorMatrix.setScale(1f, 1f, 1f, pressure)
            paint.colorFilter = ColorMatrixColorFilter(colorMatrix)

            val newBitmap = Bitmap.createBitmap(commandMaxSize.toInt(), commandMaxSize.toInt(), Bitmap.Config.ARGB_8888)

            newBitmap.let {
                Canvas(it).apply {
                    drawBitmap(bitmap, 0f, 0f, paint)
                }
            }

            bitmap.recycle()
            bitmap = newBitmap

            val rect = RectF(-commandMaxSize / 2f, -commandMaxSize / 2f, commandMaxSize / 2f, commandMaxSize / 2f)
            with(Canvas(bitmapUnderTest)) {
                save()
                translate(it.x, it.y)
                drawBitmap(bitmap, null, rect, Paint(Paint.DITHER_FLAG))
                restore()
            }
            pressure -= 0.004f
        }

        commandUnderTest.run(canvasUnderTest, LayerModel())
        PaintroidAsserts.assertBitmapEquals(bitmapUnderTest, canvasBitmapUnderTest)
    }
}
