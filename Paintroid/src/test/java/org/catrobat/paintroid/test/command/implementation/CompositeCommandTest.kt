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

import org.mockito.Mockito
import org.catrobat.paintroid.model.LayerModel
import org.catrobat.paintroid.command.implementation.CompositeCommand
import org.hamcrest.CoreMatchers
import android.graphics.Bitmap
import android.graphics.Canvas
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.model.Layer
import org.junit.Assert
import org.junit.Test

class CompositeCommandTest {
    @Test
    fun testRunEmpty() {
        val canvas = Mockito.mock(Canvas::class.java)
        val layerModel = LayerModel()
        val command = CompositeCommand()

        command.run(canvas, layerModel)

        Mockito.verifyZeroInteractions(canvas)
        Assert.assertThat(layerModel.layerCount, CoreMatchers.`is`(0))
        Assert.assertNull(layerModel.currentLayer)
    }

    @Test
    fun testRunAfterAddWithoutCurrentLayer() {
        val canvas = Mockito.mock(Canvas::class.java)
        val layerModel = LayerModel()
        val firstCommand = Mockito.mock(Command::class.java)
        val secondCommand = Mockito.mock(Command::class.java)
        val command = CompositeCommand()

        command.addCommand(firstCommand)
        command.addCommand(secondCommand)
        command.run(canvas, layerModel)

        Mockito.verifyZeroInteractions(canvas)

        val inOrder = Mockito.inOrder(firstCommand, secondCommand)
        inOrder.verify(firstCommand).run(canvas, layerModel)
        inOrder.verify(secondCommand).run(canvas, layerModel)
        inOrder.verifyNoMoreInteractions()
    }

    @Test
    fun testRunAfterAddWithCurrentLayerSet() {
        val canvas = Mockito.mock(Canvas::class.java)
        val layerModel = LayerModel()
        val firstCommand = Mockito.mock(Command::class.java)
        val secondCommand = Mockito.mock(Command::class.java)
        val currentLayer = Mockito.mock(Layer::class.java)
        val currentBitmap = Mockito.mock(Bitmap::class.java)

        Mockito.`when`(currentLayer.bitmap).thenReturn(currentBitmap)
        layerModel.currentLayer = currentLayer

        val command = CompositeCommand()
        command.addCommand(firstCommand)
        command.addCommand(secondCommand)
        command.run(canvas, layerModel)

        val inOrder = Mockito.inOrder(firstCommand, secondCommand, canvas)
        inOrder.verify(canvas).setBitmap(currentBitmap)
        inOrder.verify(firstCommand).run(canvas, layerModel)
        inOrder.verify(canvas).setBitmap(currentBitmap)
        inOrder.verify(secondCommand).run(canvas, layerModel)
        inOrder.verifyNoMoreInteractions()
    }

    @Test
    fun testFreeResourcesEmpty() {
        val command = CompositeCommand()
        command.freeResources()
    }

    @Test
    fun testFreeResourcesAfterAdd() {
        val firstCommand = Mockito.mock(Command::class.java)
        val secondCommand = Mockito.mock(Command::class.java)
        val command = CompositeCommand()

        command.addCommand(firstCommand)
        command.addCommand(secondCommand)
        command.freeResources()

        val inOrder = Mockito.inOrder(firstCommand, secondCommand)
        inOrder.verify(firstCommand).freeResources()
        inOrder.verify(secondCommand).freeResources()
        inOrder.verifyNoMoreInteractions()
    }
}
