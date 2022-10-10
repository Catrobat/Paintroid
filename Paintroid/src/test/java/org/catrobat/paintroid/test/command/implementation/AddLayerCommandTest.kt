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

package org.catrobat.paintroid.test.command.implementation

import org.junit.runner.RunWith
import org.mockito.Mock
import org.catrobat.paintroid.common.CommonFactory
import org.mockito.InjectMocks
import org.catrobat.paintroid.command.implementation.AddEmptyLayerCommand
import org.mockito.Mockito
import org.catrobat.paintroid.model.LayerModel
import android.graphics.Bitmap
import android.graphics.Canvas
import org.junit.Assert
import org.junit.Test
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AddLayerCommandTest {
    @Mock
    private val commonFactory: CommonFactory? = null

    @Mock
    private val canvas: Canvas? = null

    @InjectMocks
    private val command: AddEmptyLayerCommand? = null

    @Test
    fun testSetUp() { Mockito.verifyZeroInteractions(commonFactory) }

    @Test
    fun testAddOneLayer() {
        val layerModel = LayerModel()
        layerModel.width = 3
        layerModel.height = 5

        canvas?.let { command?.run(it, layerModel) }

        Mockito.verify(commonFactory)?.createBitmap(3, 5, Bitmap.Config.ARGB_8888)
        Assert.assertEquals(1, layerModel.layerCount.toLong())

        val currentLayer = layerModel.currentLayer
        Assert.assertEquals(currentLayer, layerModel.getLayerAt(0))
    }

    @Test
    fun testAddTwoLayersAddsToFront() {
        val layerModel = LayerModel()

        if (canvas != null) { command?.run(canvas, layerModel) }

        val firstLayer = layerModel.getLayerAt(0)

        if (canvas != null) { command?.run(canvas, layerModel) }

        Assert.assertEquals(2, layerModel.layerCount.toLong())

        val currentLayer = layerModel.currentLayer
        Assert.assertEquals(currentLayer, layerModel.getLayerAt(0))
        Assert.assertEquals(firstLayer, layerModel.getLayerAt(1))
    }

    @Test
    fun testAddMultipleLayersWillUseSameArguments() {
        val layerModel = LayerModel()

        layerModel.width = 7
        layerModel.height = 11

        if (canvas != null) { command?.run(canvas, layerModel) }
        if (canvas != null) { command?.run(canvas, layerModel) }
        if (canvas != null) { command?.run(canvas, layerModel) }
        if (canvas != null) { command?.run(canvas, layerModel) }

        Mockito.verify(
            commonFactory,
            Mockito.times(4)
        )?.createBitmap(7, 11, Bitmap.Config.ARGB_8888)
    }
}
