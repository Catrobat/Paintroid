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
import org.catrobat.paintroid.command.implementation.CropCommand
import org.catrobat.paintroid.model.Layer
import org.junit.Assert
import org.junit.Test

class CropCommandTest {
    private var resizeCoordinateXLeft = 0
    private var resizeCoordinateYTop = 0
    private var resizeCoordinateXRight = 0
    private var resizeCoordinateYBottom = 0
    private var maximumBitmapResolution = 0

    private lateinit var commandUnderTest: Command
    private lateinit var canvasUnderTest: Canvas
    private lateinit var bitmapUnderTest: Bitmap
    private lateinit var layerUnderTest: Layer
    private lateinit var layerModel: LayerModel

    @Before
    fun setUp() {
        layerModel = LayerModel()
        layerModel.width = INITIAL_WIDTH
        layerModel.height = INITIAL_HEIGHT

        val canvasBitmapUnderTest = Bitmap.createBitmap(INITIAL_WIDTH, INITIAL_HEIGHT, Bitmap.Config.ARGB_8888)
        canvasBitmapUnderTest.eraseColor(BITMAP_BASE_COLOR)

        bitmapUnderTest = canvasBitmapUnderTest.copy(Bitmap.Config.ARGB_8888, true)
        layerUnderTest = Layer(bitmapUnderTest)
        canvasUnderTest = Canvas()
        canvasUnderTest.setBitmap(canvasBitmapUnderTest)
        layerModel.addLayerAt(0, layerUnderTest)
        layerModel.currentLayer = layerUnderTest
        resizeCoordinateXLeft = 0
        resizeCoordinateYTop = 0
        resizeCoordinateXRight = bitmapUnderTest.width - 1
        resizeCoordinateYBottom = bitmapUnderTest.height - 1
        maximumBitmapResolution = (bitmapUnderTest.width * bitmapUnderTest.height
            * MAXIMUM_BITMAP_RESOLUTION_FACTOR)
        commandUnderTest = CropCommand(
            resizeCoordinateXLeft, resizeCoordinateYTop,
            resizeCoordinateXRight, resizeCoordinateYBottom, maximumBitmapResolution
        )
    }

    @Test
    fun testIfBitmapIsCropped() {
        val widthOriginal = bitmapUnderTest.width
        val heightOriginal = bitmapUnderTest.height

        resizeCoordinateXLeft = 1
        resizeCoordinateYTop = 1
        resizeCoordinateXRight = bitmapUnderTest.width - 2
        resizeCoordinateYBottom = bitmapUnderTest.height - 2
        commandUnderTest = CropCommand(
            resizeCoordinateXLeft, resizeCoordinateYTop,
            resizeCoordinateXRight, resizeCoordinateYBottom, maximumBitmapResolution
        )

        commandUnderTest.run(canvasUnderTest, layerModel)
        val croppedBitmap = layerUnderTest.bitmap
        Assert.assertEquals(
            "Cropping failed, width not correct ",
            (widthOriginal - resizeCoordinateXLeft - (widthOriginal - (resizeCoordinateXRight + 1))).toLong(),
            croppedBitmap?.width?.toLong()
        )
        Assert.assertEquals(
            "Cropping failed, height not correct ",
            (heightOriginal - resizeCoordinateYTop - (widthOriginal - (resizeCoordinateYBottom + 1))).toLong(),
            croppedBitmap?.height?.toLong()
        )
        croppedBitmap?.recycle()
    }

    @Test
    fun testIfBitmapIsEnlarged() {
        val widthOriginal = bitmapUnderTest.width
        val heightOriginal = bitmapUnderTest.height

        resizeCoordinateXLeft = -1
        resizeCoordinateYTop = -1
        resizeCoordinateXRight = bitmapUnderTest.width
        resizeCoordinateYBottom = bitmapUnderTest.height
        commandUnderTest = CropCommand(
            resizeCoordinateXLeft, resizeCoordinateYTop,
            resizeCoordinateXRight, resizeCoordinateYBottom, maximumBitmapResolution)
        commandUnderTest.run(canvasUnderTest, layerModel
        )

        val enlargedBitmap = layerUnderTest.bitmap
        Assert.assertEquals(
            "Enlarging failed, width not correct ",
            (widthOriginal - resizeCoordinateXLeft - (widthOriginal - (resizeCoordinateXRight + 1))).toLong(),
            enlargedBitmap!!.width.toLong()
        )
        Assert.assertEquals(
            "Enlarging failed, height not correct ",
            (heightOriginal - resizeCoordinateYTop
                - (widthOriginal - (resizeCoordinateYBottom + 1))).toLong(), enlargedBitmap.height.toLong()
        )
        enlargedBitmap.recycle()
    }

    @Test
    fun testIfBitmapIsShifted() {
        val widthOriginal = bitmapUnderTest.width
        val heightOriginal = bitmapUnderTest.height

        resizeCoordinateXLeft = bitmapUnderTest.width / 2 - 1
        resizeCoordinateYTop = bitmapUnderTest.height / 2 - 1
        resizeCoordinateXRight = resizeCoordinateXLeft + bitmapUnderTest.width - 1
        resizeCoordinateYBottom = resizeCoordinateYTop + bitmapUnderTest.height - 1
        commandUnderTest = CropCommand(
            resizeCoordinateXLeft, resizeCoordinateYTop,
            resizeCoordinateXRight, resizeCoordinateYBottom, maximumBitmapResolution)
        commandUnderTest.run(canvasUnderTest, layerModel
        )

        val enlargedBitmap = layerUnderTest.bitmap
        Assert.assertEquals(
            "Enlarging failed, width not correct ", widthOriginal.toLong(), enlargedBitmap?.width?.toLong()
        )
        Assert.assertEquals(
            "Enlarging failed, height not correct ", heightOriginal.toLong(), enlargedBitmap?.height?.toLong()
        )
        enlargedBitmap?.recycle()
    }

    @Test
    fun testIfMaximumResolutionIsRespected() {
        val widthOriginal = bitmapUnderTest.width
        val heightOriginal = bitmapUnderTest.height

        commandUnderTest =
            CropCommand(0, 0, widthOriginal * 2, heightOriginal * 2, maximumBitmapResolution)
        commandUnderTest.run(canvasUnderTest, layerModel)
        Assert.assertEquals(
            "Width should not have changed", widthOriginal.toLong(), layerUnderTest.bitmap?.width?.toLong()
        )
        Assert.assertEquals(
            "Height should not have changed", heightOriginal.toLong(), layerUnderTest.bitmap?.height?.toLong()
        )
    }

    @Test
    fun testIfBitmapIsNotResizedWithInvalidBounds() {
        val originalBitmap = layerUnderTest.bitmap

        commandUnderTest = CropCommand(
            bitmapUnderTest.width, 0, bitmapUnderTest.width,
            0, maximumBitmapResolution)
        commandUnderTest.run(canvasUnderTest, layerModel)

        if (originalBitmap != null) {
            Assert.assertTrue(
                "bitmap must not change if X left is larger than bitmap scope", originalBitmap.sameAs(
                    layerUnderTest.bitmap)
            )
        }

        commandUnderTest = CropCommand(-1, 0, -1, 0, maximumBitmapResolution)
        commandUnderTest.run(canvasUnderTest, layerModel)
        if (originalBitmap != null) {
            Assert.assertTrue(
                "bitmap must not change if X right is smaller than bitmap scope", originalBitmap.sameAs(
                    layerUnderTest.bitmap))
        }

        commandUnderTest = CropCommand(
            0, bitmapUnderTest.height, 0,
            bitmapUnderTest.height, maximumBitmapResolution)
        commandUnderTest.run(canvasUnderTest, layerModel)

        if (originalBitmap != null) {
            Assert.assertTrue(
                "bitmap must not change if Y top is larger than bitmap scope", originalBitmap.sameAs(
                    layerUnderTest.bitmap))
        }

        commandUnderTest = CropCommand(0, -1, 0, -1, maximumBitmapResolution)
        commandUnderTest.run(canvasUnderTest, layerModel)

        if (originalBitmap != null) {
            Assert.assertTrue(
                "bitmap must not change if Y bottom is smaller than bitmap scope",
                originalBitmap.sameAs(layerUnderTest.bitmap))
        }

        commandUnderTest = CropCommand(1, 0, 0, 0, maximumBitmapResolution)
        commandUnderTest.run(canvasUnderTest, layerModel)

        if (originalBitmap != null) {
            Assert.assertTrue(
                "bitmap must not change with widthXRight < widthXLeft bound", originalBitmap.sameAs(
                    layerUnderTest.bitmap)
            )
        }

        commandUnderTest = CropCommand(0, 1, 0, 0, maximumBitmapResolution)
        commandUnderTest.run(canvasUnderTest, layerModel)

        if (originalBitmap != null) {
            Assert.assertTrue(
                "bitmap must not change with widthYBottom < widthYTop bound", originalBitmap.sameAs(
                    layerUnderTest.bitmap)
            )
        }

        commandUnderTest = CropCommand(
            0, 0, bitmapUnderTest.width - 1,
            bitmapUnderTest.height - 1, maximumBitmapResolution)
        commandUnderTest.run(canvasUnderTest, layerModel
        )

        if (originalBitmap != null) {
            Assert.assertTrue(
                "bitmap must not change because bounds are the same as original bitmap",
                originalBitmap.sameAs(layerUnderTest.bitmap))
        }
    }

    companion object {
        private const val MAXIMUM_BITMAP_RESOLUTION_FACTOR = 4
        private const val BITMAP_BASE_COLOR = Color.GREEN
        private const val INITIAL_HEIGHT = 80
        private const val INITIAL_WIDTH = 80
    }
}
