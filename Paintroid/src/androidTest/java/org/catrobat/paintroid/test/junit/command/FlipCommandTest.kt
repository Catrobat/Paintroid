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
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.model.LayerModel
import org.junit.Before
import org.catrobat.paintroid.command.implementation.FlipCommand
import org.catrobat.paintroid.command.implementation.FlipCommand.FlipDirection
import org.catrobat.paintroid.model.Layer
import org.junit.Assert
import org.junit.Test

class FlipCommandTest {
    private lateinit var commandUnderTest: Command
    private lateinit var canvasUnderTest: Canvas
    private lateinit var bitmapUnderTest: Bitmap
    private lateinit var layerModel: LayerModel

    @Before
    fun setUp() {
        layerModel = LayerModel()
        layerModel.width = INITIAL_WIDTH
        layerModel.height = INITIAL_HEIGHT

        val canvasBitmapUnderTest =
            Bitmap.createBitmap(INITIAL_WIDTH, INITIAL_HEIGHT, Bitmap.Config.ARGB_8888)
        canvasBitmapUnderTest.eraseColor(BITMAP_BASE_COLOR)
        bitmapUnderTest = canvasBitmapUnderTest.copy(Bitmap.Config.ARGB_8888, true)

        val layerUnderTest = Layer(bitmapUnderTest)
        canvasUnderTest = Canvas()
        canvasUnderTest.setBitmap(canvasBitmapUnderTest)
        layerModel.addLayerAt(0, layerUnderTest)
        layerModel.currentLayer = layerUnderTest
    }

    @Test
    fun testVerticalFlip() {
        commandUnderTest = FlipCommand(FlipDirection.FLIP_VERTICAL)
        bitmapUnderTest.setPixel(0, INITIAL_HEIGHT / 2, PAINT_BASE_COLOR)
        commandUnderTest.run(canvasUnderTest, layerModel)

        val pixel = bitmapUnderTest.getPixel(INITIAL_WIDTH - 1, INITIAL_WIDTH / 2)
        Assert.assertEquals(PAINT_BASE_COLOR.toLong(), pixel.toLong())
    }

    @Test
    fun testHorizontalFlip() {
        commandUnderTest = FlipCommand(FlipDirection.FLIP_HORIZONTAL)
        bitmapUnderTest.setPixel(INITIAL_WIDTH / 2, 0, PAINT_BASE_COLOR)
        commandUnderTest.run(canvasUnderTest, layerModel)

        val pixel = bitmapUnderTest.getPixel(INITIAL_WIDTH / 2, INITIAL_WIDTH - 1)
        Assert.assertEquals(PAINT_BASE_COLOR.toLong(), pixel.toLong())
    }

    companion object {
        private const val BITMAP_BASE_COLOR = Color.GREEN
        private const val PAINT_BASE_COLOR = Color.BLUE
        private const val INITIAL_HEIGHT = 80
        private const val INITIAL_WIDTH = 80
    }
}
