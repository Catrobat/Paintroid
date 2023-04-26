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
import org.catrobat.paintroid.model.LayerModel
import org.mockito.Mockito
import org.catrobat.paintroid.command.implementation.SelectLayerCommand
import org.catrobat.paintroid.contract.LayerContracts
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SelectLayerCommandTest {
    @Test
    fun testRunWhenNoLayerSelected() {
        val model = LayerModel()
        val layer = Mockito.mock(LayerContracts.Layer::class.java)

        model.addLayerAt(0, layer)

        val command = SelectLayerCommand(0)
        command.run(Mockito.mock(Canvas::class.java), model)

        Assert.assertThat(model.currentLayer, CoreMatchers.`is`(layer))
    }

    @Test
    fun testRunWhenOtherLayerSelected() {
        val model = LayerModel()
        val firstLayer = Mockito.mock(LayerContracts.Layer::class.java)
        val secondLayer = Mockito.mock(LayerContracts.Layer::class.java)

        model.addLayerAt(0, firstLayer)
        model.addLayerAt(1, secondLayer)
        model.currentLayer = firstLayer

        val command = SelectLayerCommand(1)
        command.run(Mockito.mock(Canvas::class.java), model)

        Assert.assertThat(model.currentLayer, CoreMatchers.`is`(secondLayer))
    }
}
