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

package org.catrobat.paintroid.test.model

import org.catrobat.paintroid.contract.LayerContracts
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.catrobat.paintroid.model.LayerModel
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LayerModelTest {
    @InjectMocks
    private val layerModel: LayerModel? = null

    @Test
    fun testSetUp() {
        Assert.assertThat(layerModel?.layerCount, CoreMatchers.`is`(0))
        Assert.assertThat(layerModel?.width, CoreMatchers.`is`(0))
        Assert.assertThat(layerModel?.height, CoreMatchers.`is`(0))
        Assert.assertNotNull(layerModel?.layers)
        Assert.assertNull(layerModel?.currentLayer)

        val iterator: ListIterator<LayerContracts.Layer>? = layerModel?.listIterator(0)

        Assert.assertNotNull(iterator)
        iterator?.hasNext()?.let { Assert.assertFalse(it) }
    }

    @Test
    fun testGetCurrentLayer() {
        val layer = Mockito.mock(LayerContracts.Layer::class.java)

        layerModel?.currentLayer = layer

        Assert.assertThat(layerModel?.currentLayer, CoreMatchers.`is`(layer))
        Mockito.verifyZeroInteractions(layer)
    }

    @Test
    fun testGetWidth() {
        layerModel?.width = 3

        Assert.assertThat(layerModel?.width, CoreMatchers.`is`(3))
    }

    @Test
    fun testGetHeight() {
        layerModel?.height = 5

        Assert.assertThat(layerModel?.height, CoreMatchers.`is`(5))
    }

    @Test
    fun testReset() {
        val layer = Mockito.mock(LayerContracts.Layer::class.java)
        layerModel?.addLayerAt(0, layer)
        layerModel?.reset()

        Assert.assertThat(layerModel?.layerCount, CoreMatchers.`is`(0))
        Mockito.verifyZeroInteractions(layer)
    }

    @Test
    fun testGetLayerCount() {
        val layer = Mockito.mock(LayerContracts.Layer::class.java)

        layerModel?.addLayerAt(0, layer)

        Assert.assertThat(layerModel?.layerCount, CoreMatchers.`is`(1))
        Mockito.verifyZeroInteractions(layer)
    }

    @Test
    fun testGetLayerAt() {
        val layer = Mockito.mock(LayerContracts.Layer::class.java)

        layerModel?.addLayerAt(0, layer)

        Assert.assertThat(layerModel?.getLayerAt(0), CoreMatchers.`is`(layer))
        Mockito.verifyZeroInteractions(layer)
    }

    @Test
    fun testGetLayerIndexOf() {
        val layer = Mockito.mock(LayerContracts.Layer::class.java)

        layerModel?.addLayerAt(0, layer)

        Assert.assertThat(layerModel?.getLayerIndexOf(layer), CoreMatchers.`is`(0))
        Mockito.verifyZeroInteractions(layer)
    }

    @Test
    fun testListIterator() {
        val layer = Mockito.mock(LayerContracts.Layer::class.java)

        layerModel?.addLayerAt(0, layer)

        val iterator: ListIterator<LayerContracts.Layer>? = layerModel?.listIterator(0)

        Assert.assertThat(iterator?.next(), CoreMatchers.`is`(layer))
        Mockito.verifyZeroInteractions(layer)
    }

    @Test
    fun testSetLayerAt() {
        val firstLayer = Mockito.mock(LayerContracts.Layer::class.java)
        val secondLayer = Mockito.mock(LayerContracts.Layer::class.java)

        layerModel?.addLayerAt(0, firstLayer)
        layerModel?.setLayerAt(0, secondLayer)

        Assert.assertThat(layerModel?.layerCount, CoreMatchers.`is`(1))
        Assert.assertThat(layerModel?.getLayerAt(0), CoreMatchers.`is`(secondLayer))
        Mockito.verifyZeroInteractions(firstLayer, secondLayer)
    }

    @Test
    fun testRemoveLayerAt() {
        val firstLayer = Mockito.mock(LayerContracts.Layer::class.java)
        val secondLayer = Mockito.mock(LayerContracts.Layer::class.java)

        layerModel?.addLayerAt(0, firstLayer)
        layerModel?.addLayerAt(0, secondLayer)
        layerModel?.removeLayerAt(0)

        Assert.assertThat(layerModel?.layerCount, CoreMatchers.`is`(1))
        Assert.assertThat(layerModel?.getLayerAt(0), CoreMatchers.`is`(firstLayer))
        Mockito.verifyZeroInteractions(firstLayer, secondLayer)
    }
}
