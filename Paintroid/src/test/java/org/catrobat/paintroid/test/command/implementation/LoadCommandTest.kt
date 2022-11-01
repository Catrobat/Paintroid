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
import android.graphics.Bitmap
import android.graphics.Canvas
import org.mockito.InjectMocks
import org.catrobat.paintroid.command.implementation.LoadCommand
import org.catrobat.paintroid.contract.LayerContracts
import org.junit.Before
import org.catrobat.paintroid.model.LayerModel
import org.mockito.Mockito
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LoadCommandTest {
    @Mock
    var bitmap: Bitmap? = null

    @Mock
    var canvas: Canvas? = null
    var layerModel: LayerContracts.Model? = null

    @InjectMocks
    var command: LoadCommand? = null

    @Before
    fun setUp() { layerModel = LayerModel() }

    @Test
    fun testSetUp() { Mockito.verifyZeroInteractions(bitmap) }

    @Test
    fun testRunCopiesImage() {
        val copy = Mockito.mock(Bitmap::class.java)
        Mockito.`when`(bitmap?.copy(Bitmap.Config.ARGB_8888, true)).thenReturn(copy)

        layerModel?.let { canvas?.let { it1 -> command?.run(it1, it) } }

        val currentLayer = layerModel?.currentLayer
        if (currentLayer != null) { Assert.assertThat(currentLayer.bitmap, CoreMatchers.`is`(copy)) }
    }

    @Test
    fun testRunAddsOneLayer() {
        val clone = Mockito.mock(Bitmap::class.java)
        Mockito.`when`(bitmap?.copy(Bitmap.Config.ARGB_8888, true)).thenReturn(clone)

        canvas?.let { command?.run(it, layerModel!!) }

        Assert.assertThat(layerModel?.layerCount, CoreMatchers.`is`(1))
    }

    @Test
    fun testRunSetsCurrentLayer() {
        val clone = Mockito.mock(Bitmap::class.java)
        Mockito.`when`(bitmap?.copy(Bitmap.Config.ARGB_8888, true)).thenReturn(clone)

        canvas?.let { layerModel?.let { it1 -> command?.run(it, it1) } }

        val currentLayer = layerModel?.currentLayer
        Assert.assertThat(layerModel?.getLayerAt(0), CoreMatchers.`is`(currentLayer))
    }
}
