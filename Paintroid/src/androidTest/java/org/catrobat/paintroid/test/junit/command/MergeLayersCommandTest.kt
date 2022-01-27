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
import org.catrobat.paintroid.command.implementation.MergeLayersCommand
import org.catrobat.paintroid.model.Layer
import org.catrobat.paintroid.model.LayerModel
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MergeLayersCommandTest {
    private lateinit var commandUnderTest: Command
    private lateinit var layerModel: LayerModel
    private lateinit var canvasUnderTest: Canvas
    private lateinit var canvasBitmapUnderTest: Bitmap
    private lateinit var bitmapUnderTest1: Bitmap
    private lateinit var bitmapUnderTest2: Bitmap
    private lateinit var bitmapUnderTest3: Bitmap

    companion object {
        private const val PAINT_BASE_COLOR = Color.BLUE
        private const val INITIAL_HEIGHT = 80
        private const val INITIAL_WIDTH = 80
    }

    @Before
    fun setUp() {
        layerModel = LayerModel()
        layerModel.width = INITIAL_WIDTH
        layerModel.height = INITIAL_HEIGHT
        canvasBitmapUnderTest = Bitmap.createBitmap(INITIAL_WIDTH, INITIAL_HEIGHT, Bitmap.Config.ARGB_8888)
        bitmapUnderTest1 = canvasBitmapUnderTest.copy(Bitmap.Config.ARGB_8888, true)
        bitmapUnderTest2 = canvasBitmapUnderTest.copy(Bitmap.Config.ARGB_8888, true)
        bitmapUnderTest3 = canvasBitmapUnderTest.copy(Bitmap.Config.ARGB_8888, true)

        val layerUnderTest1 = Layer(bitmapUnderTest1)
        val layerUnderTest2 = Layer(bitmapUnderTest2)
        val layerUnderTest3 = Layer(bitmapUnderTest3)

        canvasUnderTest = Canvas()
        canvasUnderTest.setBitmap(canvasBitmapUnderTest)
        bitmapUnderTest1.setPixel(0, 0, PAINT_BASE_COLOR)
        bitmapUnderTest2.setPixel(5, 5, PAINT_BASE_COLOR)
        bitmapUnderTest3.setPixel(8, 8, PAINT_BASE_COLOR)
        layerModel.addLayerAt(0, layerUnderTest1)
        layerModel.addLayerAt(0, layerUnderTest2)
        layerModel.addLayerAt(0, layerUnderTest3)
        layerModel.currentLayer = layerUnderTest1
        commandUnderTest = MergeLayersCommand(0, 1)
    }

    @Test
    fun testRun() {
        commandUnderTest.run(canvasUnderTest, layerModel)
        Assert.assertEquals(layerModel.getLayerAt(0)!!.bitmap!!.getPixel(5, 5), PAINT_BASE_COLOR)
        Assert.assertEquals(layerModel.getLayerAt(0)!!.bitmap!!.getPixel(8, 8), PAINT_BASE_COLOR)
        Assert.assertEquals(layerModel.getLayerAt(0)!!.bitmap!!.getPixel(3, 3), 0)
        Assert.assertEquals(layerModel.getLayerAt(0)!!.bitmap!!.getPixel(0, 0), 0)
    }

    @Test
    fun testCheckLayersCount() {
        commandUnderTest.run(canvasUnderTest, layerModel)
        Assert.assertThat(layerModel.layerCount, CoreMatchers.`is`(2))
        commandUnderTest.run(canvasUnderTest, layerModel)
        Assert.assertThat(layerModel.layerCount, CoreMatchers.`is`(1))
    }

    @Test
    fun testRunMergeSeparatedLayers() {
        commandUnderTest = MergeLayersCommand(2, 0)
        commandUnderTest.run(canvasUnderTest, layerModel)
        Assert.assertEquals(layerModel.currentLayer!!.bitmap!!.getPixel(0, 0), PAINT_BASE_COLOR)
        Assert.assertEquals(layerModel.currentLayer!!.bitmap!!.getPixel(8, 8), PAINT_BASE_COLOR)
        Assert.assertEquals(layerModel.currentLayer!!.bitmap!!.getPixel(3, 3), 0)
        Assert.assertEquals(layerModel.currentLayer!!.bitmap!!.getPixel(5, 5), 0)
        Assert.assertEquals(layerModel.getLayerAt(1)!!.bitmap!!.getPixel(5, 5), PAINT_BASE_COLOR)
    }
}
