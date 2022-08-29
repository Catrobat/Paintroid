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
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.PointF
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.paintroid.PaintroidApplication.Companion.cacheDir
import org.catrobat.paintroid.command.implementation.StampCommand
import org.catrobat.paintroid.model.Layer
import org.catrobat.paintroid.model.LayerModel
import org.catrobat.paintroid.test.utils.PaintroidAsserts
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File
import java.lang.Exception
import kotlin.Throws

class StampCommandTest {
    private lateinit var stampBitmapUnderTest: Bitmap
    private lateinit var commandUnderTest: StampCommand
    private lateinit var pointUnderTest: PointF
    private lateinit var canvasUnderTest: Canvas
    private lateinit var canvasBitmapUnderTest: Bitmap
    private lateinit var layerModel: LayerModel

    @Before
    fun setUp() {
        layerModel = LayerModel()
        layerModel.width = INITIAL_WIDTH
        layerModel.height = INITIAL_HEIGHT
        canvasBitmapUnderTest =
            Bitmap.createBitmap(INITIAL_WIDTH, INITIAL_HEIGHT, Bitmap.Config.ARGB_8888)
        canvasBitmapUnderTest.eraseColor(
            BITMAP_BASE_COLOR
        )

        val bitmapUnderTest = canvasBitmapUnderTest.copy(Bitmap.Config.ARGB_8888, true)
        val layerUnderTest = Layer(bitmapUnderTest)

        canvasUnderTest = Canvas()
        canvasUnderTest.setBitmap(canvasBitmapUnderTest)
        pointUnderTest = PointF((INITIAL_WIDTH / 2).toFloat(), (INITIAL_HEIGHT / 2).toFloat())
        layerModel.addLayerAt(0, layerUnderTest)
        layerModel.currentLayer = layerUnderTest
        cacheDir = InstrumentationRegistry.getInstrumentation().targetContext.cacheDir
        stampBitmapUnderTest = canvasBitmapUnderTest.copy(Bitmap.Config.ARGB_8888, true)
        stampBitmapUnderTest.eraseColor(BITMAP_REPLACE_COLOR)
        commandUnderTest = StampCommand(
            stampBitmapUnderTest, Point(canvasBitmapUnderTest.width / 2, canvasBitmapUnderTest.height / 2),
            canvasBitmapUnderTest.width.toFloat(),
            canvasBitmapUnderTest.height.toFloat(), 0F
        )
    }

    @Test
    fun testRun() {
        val layer = Layer(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))
        val model = LayerModel()

        model.addLayerAt(0, layer)
        model.currentLayer = layer
        commandUnderTest.run(canvasUnderTest, model)
        PaintroidAsserts.assertBitmapEquals(stampBitmapUnderTest, canvasBitmapUnderTest)
        Assert.assertNull("Stamp bitmap not recycled.", commandUnderTest.bitmap)
        Assert.assertNotNull("Bitmap not stored", commandUnderTest.fileToStoredBitmap)

        val secondLayer = Layer(Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888))
        val secondModel = LayerModel()

        secondModel.addLayerAt(0, secondLayer)
        secondModel.currentLayer = secondLayer
        commandUnderTest.run(canvasUnderTest, secondModel)
        PaintroidAsserts.assertBitmapEquals(stampBitmapUnderTest, canvasBitmapUnderTest)
    }

    @Test
    fun testRunRotateStamp() {
        stampBitmapUnderTest.setPixel(0, 0, Color.GREEN)
        commandUnderTest = StampCommand(
            stampBitmapUnderTest, Point(pointUnderTest.x.toInt(), pointUnderTest.y.toInt()),
            canvasBitmapUnderTest.width.toFloat(), canvasBitmapUnderTest.height.toFloat(), 180F
        )
        commandUnderTest.run(canvasUnderTest, LayerModel())
        stampBitmapUnderTest.setPixel(0, 0, Color.CYAN)
        stampBitmapUnderTest.setPixel(
            stampBitmapUnderTest.width - 1, stampBitmapUnderTest.height - 1, Color.GREEN
        )
        PaintroidAsserts.assertBitmapEquals(stampBitmapUnderTest, canvasBitmapUnderTest)
        Assert.assertNull("Stamp bitmap not recycled.", commandUnderTest.bitmap)
        Assert.assertNotNull("Bitmap not stored", commandUnderTest.fileToStoredBitmap)
    }

    @Test
    @Throws(Exception::class)
    fun testFreeResources() {
        val cacheDir = InstrumentationRegistry.getInstrumentation().targetContext.cacheDir
        val storedBitmap = File(cacheDir, "test")

        Assert.assertFalse(storedBitmap.exists())
        commandUnderTest.fileToStoredBitmap = storedBitmap
        commandUnderTest.freeResources()
        Assert.assertNull(commandUnderTest.bitmap)

        val restoredBitmap = commandUnderTest.fileToStoredBitmap
        Assert.assertFalse("bitmap not deleted", restoredBitmap!!.exists())
        if (restoredBitmap.exists()) { Assert.assertTrue(restoredBitmap.delete()) }
        Assert.assertTrue(storedBitmap.createNewFile())
        Assert.assertTrue(storedBitmap.exists())
        commandUnderTest.freeResources()
        Assert.assertFalse(storedBitmap.exists())
        Assert.assertNull(commandUnderTest.bitmap)
    }

    @Test
    fun testStoreBitmap() {
        var storedBitmap: File? = null
        try {
            val bitmapCopy = canvasBitmapUnderTest.copy(canvasBitmapUnderTest.config, canvasBitmapUnderTest.isMutable)
            commandUnderTest.storeBitmap(bitmapCopy, bitmapCopy.width.toFloat(), bitmapCopy.height.toFloat())
            storedBitmap = commandUnderTest.fileToStoredBitmap
            Assert.assertNotNull(storedBitmap)
            Assert.assertNotNull(storedBitmap!!.absolutePath)
            val restoredBitmap = BitmapFactory.decodeFile(storedBitmap.absolutePath)
            PaintroidAsserts.assertBitmapEquals(
                "Loaded file doesn't match saved file.", restoredBitmap, bitmapCopy
            )
        } finally {
            Assert.assertNotNull("Failed to delete the stored bitmap(0)", storedBitmap)
            Assert.assertTrue("Failed to delete the stored bitmap(1)", storedBitmap!!.delete())
        }
    }

    companion object {
        private const val BITMAP_BASE_COLOR = Color.GREEN
        private const val BITMAP_REPLACE_COLOR = Color.CYAN
        private const val INITIAL_HEIGHT = 80
        private const val INITIAL_WIDTH = 80
    }
}
