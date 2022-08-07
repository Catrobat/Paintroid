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
import org.catrobat.paintroid.command.CommandManager.CommandListener
import org.catrobat.paintroid.common.CommonFactory
import org.catrobat.paintroid.model.LayerModel
import org.catrobat.paintroid.command.implementation.DefaultCommandManager
import org.junit.Before
import org.mockito.Mockito
import android.graphics.Bitmap
import android.graphics.Canvas
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.contract.LayerContracts
import org.mockito.ArgumentMatchers
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DefaultCommandManagerTest {
    @Mock
    private val commandListener: CommandListener? = null

    @Mock
    private lateinit var commonFactory: CommonFactory
    private lateinit var layerModel: LayerModel
    private lateinit var commandManager: DefaultCommandManager

    @Before
    fun setUp() {
        layerModel = LayerModel()
        commandManager = DefaultCommandManager(commonFactory, layerModel)
    }

    @Test
    fun testAddCommandListener() {
        commandManager.addCommandListener(commandListener!!)
        Mockito.verifyZeroInteractions(commandListener)
    }

    @Test
    fun testRemoveCommandListener() {
        commandManager.removeCommandListener(commandListener!!)
        Mockito.verifyZeroInteractions(commandListener)
    }

    @Test
    fun testUndoInitiallyNotAvailable() { Assert.assertFalse(commandManager.isUndoAvailable) }

    @Test
    fun testRedoInitiallyNotAvailable() { Assert.assertFalse(commandManager.isRedoAvailable) }

    @Test
    fun testAddCommand() {
        val command = Mockito.mock(Command::class.java)
        val currentLayer = Mockito.mock(LayerContracts.Layer::class.java)
        layerModel.currentLayer = currentLayer
        Mockito.`when`(currentLayer.bitmap).thenReturn(Mockito.mock(Bitmap::class.java))
        Mockito.`when`(commonFactory.createCanvas()).thenReturn(Mockito.mock(Canvas::class.java))
        commandManager.addCommandListener(commandListener!!)
        commandManager.addCommand(command)

        Mockito.verify(command).run(ArgumentMatchers.any(Canvas::class.java), ArgumentMatchers.eq(layerModel))
        Mockito.verify(commandListener).commandPostExecute()

        Assert.assertFalse(commandManager.isRedoAvailable)
        Assert.assertTrue(commandManager.isUndoAvailable)
    }

    @Test
    fun testUndoWhenOnlyOneCommand() {
        val command = Mockito.mock(Command::class.java)
        val currentLayer = Mockito.mock(LayerContracts.Layer::class.java)

        layerModel.currentLayer = currentLayer
        layerModel.addLayerAt(0, currentLayer)
        Mockito.`when`(currentLayer.bitmap).thenReturn(Mockito.mock(Bitmap::class.java))
        Mockito.`when`(commonFactory.createCanvas()).thenReturn(Mockito.mock(Canvas::class.java))
        commandManager.addCommandListener(commandListener!!)
        commandManager.addCommand(command)
        commandManager.undo()

        val inOrder = Mockito.inOrder(command, commandListener)
        inOrder.verify(command).run(ArgumentMatchers.any(Canvas::class.java), ArgumentMatchers.eq(layerModel))
        inOrder.verify(commandListener, Mockito.times(2)).commandPostExecute()

        Assert.assertTrue(commandManager.isRedoAvailable)
        Assert.assertFalse(commandManager.isUndoAvailable)
    }

    @Test
    fun testUndoWithMultipleCommands() {
        val firstCommand = Mockito.mock(Command::class.java)
        val secondCommand = Mockito.mock(Command::class.java)
        val thirdCommand = Mockito.mock(Command::class.java)
        val currentLayer = Mockito.mock(LayerContracts.Layer::class.java)

        layerModel.currentLayer = currentLayer
        layerModel.addLayerAt(0, currentLayer)
        Mockito.`when`(currentLayer.bitmap).thenReturn(Mockito.mock(Bitmap::class.java))
        Mockito.`when`(commonFactory.createCanvas()).thenReturn(Mockito.mock(Canvas::class.java))
        commandManager.addCommand(firstCommand)
        commandManager.addCommand(secondCommand)
        commandManager.addCommand(thirdCommand)
        commandManager.undo()

        val inOrder = Mockito.inOrder(firstCommand, secondCommand, thirdCommand)
        inOrder.verify(firstCommand).run(ArgumentMatchers.any(Canvas::class.java), ArgumentMatchers.eq(layerModel))
        inOrder.verify(secondCommand).run(ArgumentMatchers.any(Canvas::class.java), ArgumentMatchers.eq(layerModel))
        inOrder.verify(thirdCommand).run(ArgumentMatchers.any(Canvas::class.java), ArgumentMatchers.eq(layerModel))
        inOrder.verify(firstCommand).run(ArgumentMatchers.any(Canvas::class.java), ArgumentMatchers.eq(layerModel))
        inOrder.verify(secondCommand).run(ArgumentMatchers.any(Canvas::class.java), ArgumentMatchers.eq(layerModel))
        inOrder.verifyNoMoreInteractions()

        Assert.assertTrue(commandManager.isRedoAvailable)
        Assert.assertTrue(commandManager.isUndoAvailable)
    }

    @Test
    fun testRedo() {
        val firstCommand = Mockito.mock(Command::class.java)
        val secondCommand = Mockito.mock(Command::class.java)
        val thirdCommand = Mockito.mock(Command::class.java)
        val currentLayer = Mockito.mock(LayerContracts.Layer::class.java)

        layerModel.currentLayer = currentLayer
        layerModel.addLayerAt(0, currentLayer)

        Mockito.`when`(currentLayer.bitmap).thenReturn(Mockito.mock(Bitmap::class.java))
        Mockito.`when`(commonFactory.createCanvas()).thenReturn(Mockito.mock(Canvas::class.java))
        commandManager.addCommand(firstCommand)
        commandManager.addCommand(secondCommand)
        commandManager.addCommand(thirdCommand)
        commandManager.undo()
        commandManager.redo()

        val inOrder = Mockito.inOrder(firstCommand, secondCommand, thirdCommand)
        inOrder.verify(firstCommand).run(ArgumentMatchers.any(Canvas::class.java), ArgumentMatchers.eq(layerModel))
        inOrder.verify(secondCommand).run(ArgumentMatchers.any(Canvas::class.java), ArgumentMatchers.eq(layerModel))
        inOrder.verify(thirdCommand).run(ArgumentMatchers.any(Canvas::class.java), ArgumentMatchers.eq(layerModel))
        inOrder.verify(firstCommand).run(ArgumentMatchers.any(Canvas::class.java), ArgumentMatchers.eq(layerModel))
        inOrder.verify(secondCommand).run(ArgumentMatchers.any(Canvas::class.java), ArgumentMatchers.eq(layerModel))
        inOrder.verify(thirdCommand).run(ArgumentMatchers.any(Canvas::class.java), ArgumentMatchers.eq(layerModel))
        inOrder.verifyNoMoreInteractions()

        Assert.assertFalse(commandManager.isRedoAvailable)
        Assert.assertTrue(commandManager.isUndoAvailable)
    }

    @Test
    fun testRedoMultipleCommands() {
        val firstCommand = Mockito.mock(Command::class.java)
        val secondCommand = Mockito.mock(Command::class.java)
        val thirdCommand = Mockito.mock(Command::class.java)
        val currentLayer = Mockito.mock(LayerContracts.Layer::class.java)

        layerModel.currentLayer = currentLayer
        layerModel.addLayerAt(0, currentLayer)
        Mockito.`when`(currentLayer.bitmap).thenReturn(Mockito.mock(Bitmap::class.java))
        Mockito.`when`(commonFactory.createCanvas()).thenReturn(Mockito.mock(Canvas::class.java))

        commandManager.addCommand(firstCommand)
        commandManager.addCommand(secondCommand)
        commandManager.addCommand(thirdCommand)
        commandManager.undo()
        layerModel.addLayerAt(0, currentLayer)
        commandManager.undo()
        layerModel.addLayerAt(0, currentLayer)
        commandManager.undo()
        commandManager.redo()
        commandManager.redo()

        val inOrder = Mockito.inOrder(firstCommand, secondCommand, thirdCommand)
        inOrder.verify(firstCommand).run(ArgumentMatchers.any(Canvas::class.java), ArgumentMatchers.eq(layerModel))
        inOrder.verify(secondCommand).run(ArgumentMatchers.any(Canvas::class.java), ArgumentMatchers.eq(layerModel))
        inOrder.verify(thirdCommand).run(ArgumentMatchers.any(Canvas::class.java), ArgumentMatchers.eq(layerModel))
        inOrder.verify(firstCommand).run(ArgumentMatchers.any(Canvas::class.java), ArgumentMatchers.eq(layerModel))
        inOrder.verify(secondCommand).run(ArgumentMatchers.any(Canvas::class.java), ArgumentMatchers.eq(layerModel))
        inOrder.verify(firstCommand, Mockito.times(2)).run(ArgumentMatchers.any(Canvas::class.java), ArgumentMatchers.eq(layerModel))
        inOrder.verify(secondCommand).run(ArgumentMatchers.any(Canvas::class.java), ArgumentMatchers.eq(layerModel))
        inOrder.verifyNoMoreInteractions()

        Assert.assertTrue(commandManager.isRedoAvailable)
        Assert.assertTrue(commandManager.isUndoAvailable)
        commandManager.redo()
        Assert.assertFalse(commandManager.isRedoAvailable)
        Assert.assertTrue(commandManager.isUndoAvailable)
    }

    @Test
    fun testRedoClearedOnCommand() {
        val firstCommand = Mockito.mock(Command::class.java)
        val secondCommand = Mockito.mock(Command::class.java)
        val thirdCommand = Mockito.mock(Command::class.java)
        val currentLayer = Mockito.mock(LayerContracts.Layer::class.java)

        layerModel.currentLayer = currentLayer
        layerModel.addLayerAt(0, currentLayer)
        Mockito.`when`(currentLayer.bitmap).thenReturn(Mockito.mock(Bitmap::class.java))
        Mockito.`when`(commonFactory.createCanvas()).thenReturn(Mockito.mock(Canvas::class.java))

        commandManager.addCommand(firstCommand)
        commandManager.addCommand(secondCommand)
        commandManager.undo()

        Assert.assertTrue(commandManager.isRedoAvailable)
        Assert.assertTrue(commandManager.isUndoAvailable)
        commandManager.addCommand(thirdCommand)
        Assert.assertFalse(commandManager.isRedoAvailable)
        Assert.assertTrue(commandManager.isUndoAvailable)
    }

    @Test
    fun testInitialStateCommandOnReset() {
        val initialStateCommand = Mockito.mock(Command::class.java)
        val layer = Mockito.mock(LayerContracts.Layer::class.java)

        layerModel.addLayerAt(0, layer)
        Mockito.`when`(commonFactory.createCanvas()).thenReturn(Mockito.mock(Canvas::class.java))
        commandManager.addCommandListener(commandListener!!)
        commandManager.setInitialStateCommand(initialStateCommand)
        commandManager.reset()

        Mockito.verify(commandListener).commandPostExecute()
        Mockito.verify(initialStateCommand).run(ArgumentMatchers.any(Canvas::class.java), ArgumentMatchers.eq(layerModel))
        Assert.assertThat(layerModel.layerCount, CoreMatchers.`is`(0))
    }

    @Test
    fun testInitialStateCommandOnUndo() {
        val initialStateCommand = Mockito.mock(Command::class.java)
        val command = Mockito.mock(Command::class.java)
        val currentLayer = Mockito.mock(LayerContracts.Layer::class.java)

        layerModel.currentLayer = currentLayer
        layerModel.addLayerAt(0, currentLayer)

        Mockito.`when`(currentLayer.bitmap).thenReturn(Mockito.mock(Bitmap::class.java))
        Mockito.`when`(commonFactory.createCanvas()).thenReturn(Mockito.mock(Canvas::class.java))
        commandManager.setInitialStateCommand(initialStateCommand)
        commandManager.addCommand(command)
        commandManager.undo()

        Mockito.verify(initialStateCommand).run(ArgumentMatchers.any(Canvas::class.java), ArgumentMatchers.eq(layerModel))
    }

    @Test
    fun testUndoClearedOnReset() {
        val command = Mockito.mock(Command::class.java)
        val currentLayer = Mockito.mock(LayerContracts.Layer::class.java)

        layerModel.currentLayer = currentLayer
        Mockito.`when`(commonFactory.createCanvas()).thenReturn(Mockito.mock(Canvas::class.java))
        commandManager.addCommand(command)
        commandManager.reset()

        Assert.assertFalse(commandManager.isRedoAvailable)
        Assert.assertFalse(commandManager.isUndoAvailable)
    }

    @Test
    fun testRedoClearedOnReset() {
        val command = Mockito.mock(Command::class.java)
        val currentLayer = Mockito.mock(LayerContracts.Layer::class.java)
        layerModel.currentLayer = currentLayer
        layerModel.addLayerAt(0, currentLayer)
        Mockito.`when`(commonFactory.createCanvas()).thenReturn(Mockito.mock(Canvas::class.java))

        commandManager.addCommand(command)
        commandManager.undo()
        commandManager.reset()
        Assert.assertFalse(commandManager.isRedoAvailable)
        Assert.assertFalse(commandManager.isUndoAvailable)
    }
}
