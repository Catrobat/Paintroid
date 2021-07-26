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

import android.graphics.Bitmap
import android.graphics.Canvas
import org.catrobat.paintroid.command.implementation.LoadBitmapListCommand
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.model.LayerModel
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LoadBitmapListCommandTest {

    private var canvas: Canvas = Mockito.mock(Canvas::class.java)
    private var layerModel: LayerContracts.Model = LayerModel()

    private lateinit var commandUnderTest: LoadBitmapListCommand
    private lateinit var bitmapList: MutableList<Bitmap>

    @Before
    fun setUp() {
        bitmapList = mutableListOf()
        for (i in 1..3) {
            bitmapList.add(Bitmap.createBitmap(2 * i, 2, Bitmap.Config.ARGB_8888))
        }
        commandUnderTest = LoadBitmapListCommand(bitmapList)
    }

    @Test
    fun testRunCopiesImage() {
        commandUnderTest.run(canvas, layerModel)
        Assert.assertTrue(layerModel.currentLayer!!.bitmap!!.sameAs(bitmapList[0]))
        Assert.assertTrue(layerModel.getLayerAt(1).bitmap!!.sameAs(bitmapList[1]))
        Assert.assertTrue(layerModel.getLayerAt(2).bitmap!!.sameAs(bitmapList[2]))
    }

    @Test
    fun testCheckLayersCount() {
        commandUnderTest.run(canvas, layerModel)
        Assert.assertThat(layerModel.layerCount, `is`(3))
    }

    @Test
    fun testRunSetsCurrentLayer() {
        commandUnderTest.run(canvas, layerModel)
        val currentLayer = layerModel.currentLayer
        Assert.assertThat(layerModel.getLayerAt(0), `is`(currentLayer))
    }
}
