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
import org.catrobat.paintroid.command.implementation.LoadLayerListCommand
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.model.Layer
import org.catrobat.paintroid.model.LayerModel
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LoadLayerListCommandTest {

    private var canvas: Canvas = Mockito.mock(Canvas::class.java)
    private var layerModel: LayerContracts.Model = LayerModel()

    private lateinit var commandUnderTest: LoadLayerListCommand
    private lateinit var layerList: MutableList<LayerContracts.Layer>

    @Before
    fun setUp() {
        layerList = mutableListOf()
        for (i in 1..3) {
            val layer = Layer(Bitmap.createBitmap(2 * i, 2, Bitmap.Config.ARGB_8888))
            layerList.add(layer)
        }
        commandUnderTest = LoadLayerListCommand(layerList)
    }

    @Test
    fun testRunCopiesImage() {
        commandUnderTest.run(canvas, layerModel)
        Assert.assertTrue(layerModel.currentLayer!!.bitmap.sameAs(layerList[0].bitmap))
        Assert.assertTrue(layerModel.getLayerAt(1)!!.bitmap.sameAs(layerList[1].bitmap))
        Assert.assertTrue(layerModel.getLayerAt(2)!!.bitmap.sameAs(layerList[2].bitmap))
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
