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
package org.catrobat.paintroid.test.junit.model

import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.catrobat.paintroid.command.CommandFactory
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.command.CommandManager.CommandListener
import org.catrobat.paintroid.command.implementation.AsyncCommandManager
import org.catrobat.paintroid.command.implementation.DefaultCommandFactory
import org.catrobat.paintroid.command.implementation.DefaultCommandManager
import org.catrobat.paintroid.common.CommonFactory
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.model.Layer
import org.catrobat.paintroid.model.LayerModel
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import org.mockito.Mockito.timeout
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class LayerTest {
    private lateinit var commandFactory: CommandFactory
    private lateinit var commandManager: CommandManager
    private lateinit var layerModel: LayerContracts.Model

    @Before
    fun setUp() {
        commandFactory = DefaultCommandFactory()
        layerModel = LayerModel()
        layerModel.width = 200
        layerModel.height = 200
        val layer = Layer(Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888))
        layerModel.addLayerAt(0, layer)
        layerModel.currentLayer = layer
        commandManager =
            AsyncCommandManager(DefaultCommandManager(CommonFactory(), layerModel), layerModel)
    }

    @Test
    fun testCreateManyLayers() {
        repeat(100) {
            runBlocking {
                commandManager.addCommand(commandFactory.createAddLayerCommand())
                commandManager.addCommand(commandFactory.createRemoveLayerCommand(1))
            }
        }
    }

    @Test
    fun testMoveLayer() {
        val listener = mock(CommandListener::class.java)
        commandManager.addCommandListener(listener)
        runBlocking {
            commandManager.addCommand(commandFactory.createAddLayerCommand())
        }
        verify(listener, timeout(1000)).commandPostExecute()
        assertThat(layerModel.layerCount, `is`(2))
        val firstLayer = layerModel.getLayerAt(0)
        val secondLayer = layerModel.getLayerAt(1)
        reset(listener)
        runBlocking {
            commandManager.addCommand(commandFactory.createReorderLayersCommand(0, 1))
        }
        verify(listener, timeout(1000)).commandPostExecute()
        assertThat(layerModel.layerCount, `is`(2))
        assertThat(layerModel.getLayerAt(0), `is`(secondLayer))
        assertThat(layerModel.getLayerAt(1), `is`(firstLayer))
    }

    @Test
    fun testMergeLayers() {
        val listener = mock(CommandListener::class.java)
        val firstLayer = layerModel.getLayerAt(0)
        firstLayer.bitmap?.setPixel(1, 1, Color.BLACK)
        firstLayer.bitmap?.setPixel(1, 2, Color.BLACK)
        commandManager.addCommandListener(listener)
        runBlocking {
            commandManager.addCommand(commandFactory.createAddLayerCommand())
            delay(200)
        }
        verify(listener, timeout(1000)).commandPostExecute()
        val secondLayer = layerModel.getLayerAt(0)
        layerModel.currentLayer?.let { assertThat(it, `is`(secondLayer)) }
        secondLayer.bitmap?.setPixel(1, 1, Color.BLUE)
        secondLayer.bitmap?.setPixel(2, 1, Color.BLUE)
        reset(listener)
        runBlocking {
            commandManager.addCommand(commandFactory.createMergeLayersCommand(0, 1))
            delay(200)
        }
        verify(listener, timeout(1000)).commandPostExecute()
        assertThat(layerModel.layerCount, `is`(1))
        layerModel.currentLayer?.let { assertThat(it, `is`(firstLayer)) }
        assertThat(layerModel.getLayerAt(0), `is`(firstLayer))
        firstLayer.bitmap?.apply {
            assertThat(getPixel(1, 2), `is`(Color.BLACK))
            assertThat(getPixel(2, 1), `is`(Color.BLUE))
            assertThat(getPixel(1, 1), `is`(Color.BLUE))
        }
    }

    @Test
    fun testHideThenUnHideLayer() {
        val layerToHide = layerModel.getLayerAt(0)
        layerToHide.bitmap?.apply {
            setPixel(1, 1, Color.BLACK)
            setPixel(2, 1, Color.BLACK)
            setPixel(3, 1, Color.BLACK)
            setPixel(4, 1, Color.BLACK)
        }
        val bitmapCopy = layerToHide.transparentBitmap
        layerToHide.switchBitmaps(false)
        layerToHide.bitmap = bitmapCopy
        layerToHide.bitmap?.apply {
            assertThat(getPixel(1, 1), `is`(Color.TRANSPARENT))
            assertThat(getPixel(2, 1), `is`(Color.TRANSPARENT))
            assertThat(getPixel(3, 1), `is`(Color.TRANSPARENT))
            assertThat(getPixel(4, 1), `is`(Color.TRANSPARENT))
        }
        layerToHide.switchBitmaps(true)
        layerToHide.bitmap?.apply {
            assertThat(getPixel(1, 1), `is`(Color.BLACK))
            assertThat(getPixel(2, 1), `is`(Color.BLACK))
            assertThat(getPixel(3, 1), `is`(Color.BLACK))
            assertThat(getPixel(4, 1), `is`(Color.BLACK))
        }
    }
}
