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
import android.graphics.Color
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.command.implementation.AddEmptyLayerCommand
import org.catrobat.paintroid.command.implementation.ResizeCommand
import org.catrobat.paintroid.common.CommonFactory
import org.catrobat.paintroid.model.Layer
import org.catrobat.paintroid.model.LayerModel
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ResizeCommandTest {
    private var canvasUnderTest: Canvas? = null
    private var layerModel: LayerModel? = null
    @Before
    fun setUp() {
        layerModel = LayerModel()
        layerModel!!.width = INITIAL_WIDTH
        layerModel!!.height = INITIAL_HEIGHT
        val bitmapUnderTest = Bitmap.createBitmap(INITIAL_WIDTH, INITIAL_HEIGHT, Bitmap.Config.ARGB_8888)
        val layerUnderTest = Layer(bitmapUnderTest)
        canvasUnderTest = Canvas()
        canvasUnderTest!!.setBitmap(bitmapUnderTest)
        layerModel!!.addLayerAt(0, layerUnderTest)
        layerModel!!.currentLayer = layerUnderTest
    }

    @Test
    fun testResizeCommand() {
        val commandUnderTest: Command = ResizeCommand(NEW_WIDTH, NEW_HEIGHT)
        commandUnderTest.run(canvasUnderTest!!, layerModel!!)
        Assert.assertEquals(40, layerModel!!.height.toLong())
        Assert.assertEquals(40, layerModel!!.width.toLong())
    }

    @Test
    fun testBitmapKeepsDrawings() {
        layerModel!!.currentLayer!!.bitmap.setPixel(0, 0, Color.BLACK)
        var currentPixel = layerModel!!.currentLayer!!.bitmap.getPixel(0, 0)
        Assert.assertNotEquals(currentPixel.toLong(), 0)
        val commandUnderTest: Command = ResizeCommand(NEW_WIDTH, NEW_HEIGHT)
        commandUnderTest.run(canvasUnderTest!!, layerModel!!)
        currentPixel = layerModel!!.currentLayer!!.bitmap.getPixel(0, 0)
        Assert.assertNotEquals(currentPixel.toLong(), 0)
    }

    @Test
    fun testLayerStayInSameOrderOnResize() {
        layerModel!!.getLayerAt(0)!!.bitmap.eraseColor(Color.GREEN)
        var addLayerCommand: Command = AddEmptyLayerCommand(CommonFactory())
        addLayerCommand.run(canvasUnderTest!!, layerModel!!)
        layerModel!!.getLayerAt(0)!!.bitmap.eraseColor(Color.YELLOW)
        addLayerCommand = AddEmptyLayerCommand(CommonFactory())
        addLayerCommand.run(canvasUnderTest!!, layerModel!!)
        layerModel!!.getLayerAt(0)!!.bitmap.eraseColor(Color.BLUE)
        val commandUnderTest: Command = ResizeCommand(NEW_WIDTH, NEW_HEIGHT)
        commandUnderTest.run(canvasUnderTest!!, layerModel!!)
        Assert.assertEquals(layerModel!!.getLayerAt(2)!!.bitmap.getPixel(0, 0).toLong(), Color.GREEN.toLong())
        Assert.assertEquals(layerModel!!.getLayerAt(1)!!.bitmap.getPixel(0, 0).toLong(), Color.YELLOW.toLong())
        Assert.assertEquals(layerModel!!.getLayerAt(0)!!.bitmap.getPixel(0, 0).toLong(), Color.BLUE.toLong())
    }

    @Test
    fun testAllLayersAreResized() {
        var addLayerCommand: Command = AddEmptyLayerCommand(CommonFactory())
        addLayerCommand.run(canvasUnderTest!!, layerModel!!)
        addLayerCommand = AddEmptyLayerCommand(CommonFactory())
        addLayerCommand.run(canvasUnderTest!!, layerModel!!)
        val commandUnderTest: Command = ResizeCommand(NEW_WIDTH, NEW_HEIGHT)
        commandUnderTest.run(canvasUnderTest!!, layerModel!!)
        Assert.assertEquals(layerModel!!.getLayerAt(2)!!.bitmap.height.toLong(), NEW_HEIGHT.toLong())
        Assert.assertEquals(layerModel!!.getLayerAt(2)!!.bitmap.width.toLong(), NEW_WIDTH.toLong())
        Assert.assertEquals(layerModel!!.getLayerAt(1)!!.bitmap.height.toLong(), NEW_HEIGHT.toLong())
        Assert.assertEquals(layerModel!!.getLayerAt(1)!!.bitmap.width.toLong(), NEW_WIDTH.toLong())
        Assert.assertEquals(layerModel!!.getLayerAt(0)!!.bitmap.height.toLong(), NEW_HEIGHT.toLong())
        Assert.assertEquals(layerModel!!.getLayerAt(0)!!.bitmap.width.toLong(), NEW_WIDTH.toLong())
    }

    companion object {
        private const val INITIAL_HEIGHT = 80
        private const val INITIAL_WIDTH = 80
        private const val NEW_WIDTH = 40
        private const val NEW_HEIGHT = 40
    }
}
