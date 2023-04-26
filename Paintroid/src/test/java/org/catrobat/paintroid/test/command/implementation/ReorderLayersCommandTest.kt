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

import android.graphics.Canvas

import org.junit.runner.RunWith
import org.mockito.Mock
import org.catrobat.paintroid.model.LayerModel
import org.catrobat.paintroid.command.implementation.ReorderLayersCommand
import org.catrobat.paintroid.contract.LayerContracts
import org.junit.Before
import org.mockito.Mockito
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ReorderLayersCommandTest {
    @Mock
    private val firstLayer: LayerContracts.Layer? = null

    @Mock
    private val secondLayer: LayerContracts.Layer? = null

    @Mock
    private val thirdLayer: LayerContracts.Layer? = null
    private var layerModel: LayerModel? = null
    private var command: ReorderLayersCommand? = null

    @Before
    fun setUp() {
        layerModel = LayerModel()

        if (firstLayer != null) { layerModel?.addLayerAt(0, firstLayer) }
        if (secondLayer != null) { layerModel?.addLayerAt(1, secondLayer) }
        if (thirdLayer != null) { layerModel?.addLayerAt(2, thirdLayer) }
    }

    @Test
    fun testRunReorderToBack() {
        command = ReorderLayersCommand(0, 2)

        layerModel?.let { command?.run(Mockito.mock(Canvas::class.java), it) }

        Assert.assertThat(layerModel?.getLayerAt(0), CoreMatchers.`is`(secondLayer))
        Assert.assertThat(layerModel?.getLayerAt(1), CoreMatchers.`is`(thirdLayer))
        Assert.assertThat(layerModel?.getLayerAt(2), CoreMatchers.`is`(firstLayer))
        Assert.assertThat(layerModel?.layerCount, CoreMatchers.`is`(3))
    }

    @Test
    fun testRunReorderToFront() {
        command = ReorderLayersCommand(2, 0)

        layerModel?.let { command?.run(Mockito.mock(Canvas::class.java), it) }

        Assert.assertThat(layerModel?.getLayerAt(0), CoreMatchers.`is`(thirdLayer))
        Assert.assertThat(layerModel?.getLayerAt(1), CoreMatchers.`is`(firstLayer))
        Assert.assertThat(layerModel?.getLayerAt(2), CoreMatchers.`is`(secondLayer))
        Assert.assertThat(layerModel?.layerCount, CoreMatchers.`is`(3))
    }

    @Test
    fun testRunDoesNotAffectCurrentLayer() {
        command = ReorderLayersCommand(2, 0)

        layerModel?.let { command?.run(Mockito.mock(Canvas::class.java), it) }

        Assert.assertNull(layerModel?.currentLayer)
    }
}
