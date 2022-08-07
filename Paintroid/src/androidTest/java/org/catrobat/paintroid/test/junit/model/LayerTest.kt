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

package org.catrobat.paintroid.test.junit.model

import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
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
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class LayerTest {
    private var commandFactory: CommandFactory? = null
    private var commandManager: CommandManager? = null
    private var layerModel: LayerContracts.Model? = null
    private val layerHeight = 200
    private val layerWidth = 200
    @Before
    fun setUp() {
        commandFactory = DefaultCommandFactory()
        layerModel = LayerModel()
        (layerModel as LayerModel).width = layerWidth
        (layerModel as LayerModel).height = layerHeight
        val layer = Layer(Bitmap.createBitmap(layerWidth, layerHeight, Bitmap.Config.ARGB_8888))
        (layerModel as LayerModel).addLayerAt(0, layer)
        (layerModel as LayerModel).currentLayer = layer
        commandManager =
            AsyncCommandManager(
                DefaultCommandManager(
                    CommonFactory(),
                    layerModel as LayerModel
                ),
                layerModel as LayerModel
            )
    }

    @Test
    fun testCreateManyLayers() {
        for (i in 0..9) {
            commandManager!!.addCommand(commandFactory!!.createAddEmptyLayerCommand())
            commandManager!!.addCommand(commandFactory!!.createRemoveLayerCommand(1))
        }
    }

    @Test
    fun testMoveLayer() {
        val listener = Mockito.mock(CommandListener::class.java)
        commandManager?.addCommandListener(listener)
        commandManager?.addCommand(commandFactory?.createAddEmptyLayerCommand())
        Mockito.verify(listener, Mockito.timeout(1000)).commandPostExecute()
        Assert.assertThat(layerModel?.layerCount, Matchers.`is`(2))
        val firstLayer = layerModel?.getLayerAt(0)
        val secondLayer = layerModel?.getLayerAt(1)
        Mockito.reset(listener)
        commandManager?.addCommand(commandFactory?.createReorderLayersCommand(0, 1))
        Mockito.verify(listener, Mockito.timeout(1000)).commandPostExecute()
        Assert.assertThat(layerModel!!.layerCount, Matchers.`is`(2))
        Assert.assertThat(
            layerModel?.getLayerAt(0), Matchers.`is`(secondLayer)
        )
        Assert.assertThat(
            layerModel?.getLayerAt(1), Matchers.`is`(firstLayer)
        )
    }

    @Test
    fun testMergeLayers() {
        val listener = Mockito.mock(CommandListener::class.java)
        val firstLayer = layerModel?.getLayerAt(0)
        firstLayer?.bitmap?.setPixel(1, 1, Color.BLACK)
        firstLayer?.bitmap?.setPixel(1, 2, Color.BLACK)
        commandManager?.addCommandListener(listener)
        commandManager?.addCommand(commandFactory?.createAddEmptyLayerCommand())
        Mockito.verify(listener, Mockito.timeout(1000)).commandPostExecute()
        val secondLayer = layerModel?.getLayerAt(0)
        Assert.assertThat(
            layerModel?.currentLayer, Matchers.`is`(secondLayer)
        )
        secondLayer?.bitmap?.setPixel(1, 1, Color.BLUE)
        secondLayer?.bitmap?.setPixel(2, 1, Color.BLUE)
        Mockito.reset(listener)
        commandManager?.addCommand(commandFactory?.createMergeLayersCommand(0, 1))
        Mockito.verify(listener, Mockito.timeout(1000)).commandPostExecute()
        Assert.assertThat(layerModel?.layerCount, Matchers.`is`(1))
        Assert.assertThat(
            layerModel?.currentLayer, Matchers.`is`(firstLayer)
        )
        Assert.assertThat(
            layerModel!!.getLayerAt(0), Matchers.`is`(firstLayer)
        )
        Assert.assertThat(firstLayer?.bitmap?.getPixel(1, 2), Matchers.`is`(Color.BLACK))
        Assert.assertThat(firstLayer?.bitmap?.getPixel(2, 1), Matchers.`is`(Color.BLUE))
        Assert.assertThat(firstLayer?.bitmap?.getPixel(1, 1), Matchers.`is`(Color.BLUE))
    }

    @Test
    fun testHideThenUnhideLayer() {
        val layerToHide = layerModel?.getLayerAt(0)
        layerToHide?.bitmap?.setPixel(1, 1, Color.BLACK)
        layerToHide?.bitmap?.setPixel(2, 1, Color.BLACK)
        layerToHide?.bitmap?.setPixel(3, 1, Color.BLACK)
        layerToHide?.bitmap?.setPixel(4, 1, Color.BLACK)
        val bitmapCopy = layerToHide?.transparentBitmap
        layerToHide?.switchBitmaps(false)
        layerToHide?.bitmap = bitmapCopy
        Assert.assertThat(layerToHide?.bitmap?.getPixel(1, 1), Matchers.`is`(Color.TRANSPARENT))
        Assert.assertThat(layerToHide?.bitmap?.getPixel(2, 1), Matchers.`is`(Color.TRANSPARENT))
        Assert.assertThat(layerToHide?.bitmap?.getPixel(3, 1), Matchers.`is`(Color.TRANSPARENT))
        Assert.assertThat(layerToHide?.bitmap?.getPixel(4, 1), Matchers.`is`(Color.TRANSPARENT))
        layerToHide?.switchBitmaps(true)
        Assert.assertThat(layerToHide?.bitmap?.getPixel(1, 1), Matchers.`is`(Color.BLACK))
        Assert.assertThat(layerToHide?.bitmap?.getPixel(2, 1), Matchers.`is`(Color.BLACK))
        Assert.assertThat(layerToHide?.bitmap?.getPixel(3, 1), Matchers.`is`(Color.BLACK))
        Assert.assertThat(layerToHide?.bitmap?.getPixel(4, 1), Matchers.`is`(Color.BLACK))
    }

    @Test
    fun testGetLayerAt() {
        for (i in 0 until (layerModel?.layerCount ?: 0)) {
            Assert.assertNotNull(layerModel?.getLayerAt(i))
        }
        Assert.assertNull(layerModel?.getLayerAt(-1))
        Assert.assertNull(layerModel?.layerCount?.let { layerModel?.getLayerAt(it) })
    }

    @Test
    fun testAddLayerAt() {
        val layer = Layer(Bitmap.createBitmap(layerWidth, layerHeight, Bitmap.Config.ARGB_8888))
        layerModel?.addLayerAt(0, layer)?.let { assertTrue(it) }
        assertTrue(layerModel?.layerCount?.let { layerModel?.addLayerAt(it, layer) } == true)
        layerModel?.addLayerAt(-1, layer)?.let { assertFalse(it) }
        layerModel?.addLayerAt(layerModel?.layerCount?.plus(1) ?: 0, layer)?.let { assertFalse(it) }
    }

    @Test
    fun testRemoveLayerAt() {
        assertFalse(layerModel?.removeLayerAt(-1) ?: false)
        assertTrue(layerModel?.removeLayerAt(0) ?: false)
        val layer = Layer(Bitmap.createBitmap(layerWidth, layerHeight, Bitmap.Config.ARGB_8888))
        layerModel?.addLayerAt(0, layer)
        assertFalse(layerModel?.layerCount?.let { layerModel?.removeLayerAt(it) } ?: false)
    }
}
