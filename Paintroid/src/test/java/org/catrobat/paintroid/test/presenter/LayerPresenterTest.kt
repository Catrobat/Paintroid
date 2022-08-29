/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.paintroid.test.presenter

import android.graphics.Bitmap
import android.view.View
import android.widget.Toast
import org.catrobat.paintroid.R
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.command.CommandFactory
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.model.Layer
import org.catrobat.paintroid.model.LayerModel
import org.catrobat.paintroid.presenter.LayerPresenter
import org.catrobat.paintroid.ui.dragndrop.ListItemDragHandler
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LayerPresenterTest {
    @Mock
    private val listItemDragHandler: ListItemDragHandler? = null

    @Mock
    private val layerMenuViewHolder: LayerContracts.LayerMenuViewHolder? = null

    @Mock
    private val layerAdapter: LayerContracts.Adapter? = null

    @Mock
    private val navigator: LayerContracts.Navigator? = null

    @Mock
    private val commandManager: CommandManager? = null

    @Mock
    private val commandFactory: CommandFactory? = null
    private var layerModel: LayerModel? = null
    private var layerPresenter: LayerPresenter? = null
    @Before
    fun setUp() { layerModel = LayerModel() }

    @Suppress("NestedBlockDepth")
    private fun createPresenter() {
        layerPresenter = layerModel?.let {
            layerMenuViewHolder?.let { it1 ->
                commandManager?.let { it2 ->
                    commandFactory?.let { it3 ->
                        navigator?.let { it4 ->
                            listItemDragHandler?.let { it5 ->
                                LayerPresenter(
                                    it, it5,
                                    it1, it2, it3, it4
                                )
                            }
                        }
                    }
                }
            }
        }
        if (layerAdapter != null) {
            layerPresenter?.setAdapter(layerAdapter)
        }
    }

    @Test
    fun testSetUp() {
        createPresenter()
        Mockito.verifyZeroInteractions(
            layerAdapter, layerMenuViewHolder, listItemDragHandler,
            commandManager, commandFactory, navigator
        )
    }

    @Test
    fun testRefreshLayerMenuViewHolder() {
        layerModel?.addLayerAt(0, Mockito.mock(Layer::class.java))
        layerModel?.addLayerAt(1, Mockito.mock(Layer::class.java))
        createPresenter()
        layerPresenter?.refreshLayerMenuViewHolder()
        Mockito.verify(layerMenuViewHolder)?.enableAddLayerButton()
        Mockito.verify(layerMenuViewHolder)?.enableRemoveLayerButton()
        Mockito.verifyNoMoreInteractions(layerMenuViewHolder)
        Mockito.verifyZeroInteractions(
            commandManager,
            commandFactory,
            layerAdapter,
            listItemDragHandler
        )
    }

    @Test
    fun testRefreshLayerMenuViewHolderOnAddDisabled() {
        for (i in 0..99) {
            layerModel?.addLayerAt(i, Mockito.mock(Layer::class.java))
        }
        createPresenter()
        layerPresenter?.refreshLayerMenuViewHolder()
        Mockito.verify(layerMenuViewHolder)?.disableAddLayerButton()
        Mockito.verify(layerMenuViewHolder)?.enableRemoveLayerButton()
        Mockito.verifyNoMoreInteractions(layerMenuViewHolder)
        Mockito.verifyZeroInteractions(
            commandManager,
            commandFactory,
            layerAdapter,
            listItemDragHandler
        )
    }

    @Test
    fun testRefreshLayerMenuViewHolderOnRemoveDisabled() {
        layerModel?.addLayerAt(0, Mockito.mock(Layer::class.java))
        createPresenter()
        layerPresenter?.refreshLayerMenuViewHolder()
        Mockito.verify(layerMenuViewHolder)?.enableAddLayerButton()
        Mockito.verify(layerMenuViewHolder)?.disableRemoveLayerButton()
        Mockito.verifyNoMoreInteractions(layerMenuViewHolder)
        Mockito.verifyZeroInteractions(
            commandManager,
            commandFactory,
            layerAdapter,
            listItemDragHandler
        )
    }

    @Test
    fun testGetLayerCount() {
        layerModel?.addLayerAt(0, Mockito.mock(Layer::class.java))
        layerModel?.addLayerAt(1, Mockito.mock(Layer::class.java))
        layerModel?.addLayerAt(2, Mockito.mock(Layer::class.java))
        layerModel?.addLayerAt(3, Mockito.mock(Layer::class.java))
        createPresenter()
        val result = layerPresenter?.layerCount
        Assert.assertEquals(4, result)
    }

    @Test
    fun testGetLayerItem() {
        layerModel?.addLayerAt(0, Mockito.mock(Layer::class.java))
        layerModel?.addLayerAt(1, Mockito.mock(Layer::class.java))
        layerModel?.addLayerAt(2, Mockito.mock(Layer::class.java))
        layerModel?.addLayerAt(3, Mockito.mock(Layer::class.java))
        createPresenter()
        val result = layerPresenter?.getLayerItem(3)
        Assert.assertEquals(layerModel?.getLayerAt(3), result)
    }

    @Test
    fun testGetLayerItemId() {
        createPresenter()
        Assert.assertEquals(3, layerPresenter?.getLayerItemId(3)?.toInt())
        Assert.assertEquals(0, layerPresenter?.getLayerItemId(0)?.toInt())
    }

    @Test
    fun testAddLayer() {
        layerModel?.addLayerAt(0, Mockito.mock(Layer::class.java))
        layerModel?.addLayerAt(1, Mockito.mock(Layer::class.java))
        val command = Mockito.mock(Command::class.java)
        Mockito.`when`(commandFactory?.createAddEmptyLayerCommand()).thenReturn(command)
        createPresenter()
        layerPresenter?.addLayer()
        Mockito.verify(commandManager)?.addCommand(command)
        Mockito.verifyZeroInteractions(layerMenuViewHolder, layerAdapter, listItemDragHandler)
    }

    @Test
    fun testAddLayerWhenTooManyLayers() {
        for (i in 0..99) {
            layerModel?.addLayerAt(i, Mockito.mock(Layer::class.java))
        }
        createPresenter()
        layerPresenter?.addLayer()
        Mockito.verify(navigator)?.showToast(R.string.layer_too_many_layers, Toast.LENGTH_SHORT)
        Mockito.verifyZeroInteractions(
            commandManager,
            layerMenuViewHolder,
            layerAdapter,
            listItemDragHandler
        )
    }

    @Test
    fun testRemoveLayer() {
        val selectedLayer = Mockito.mock(Layer::class.java)
        layerModel?.addLayerAt(0, Mockito.mock(Layer::class.java))
        layerModel?.addLayerAt(1, Mockito.mock(Layer::class.java))
        layerModel?.currentLayer = layerModel?.getLayerAt(1)
        val command = Mockito.mock(Command::class.java)
        Mockito.`when`(commandFactory?.createRemoveLayerCommand(1)).thenReturn(command)
        createPresenter()
        layerPresenter?.removeLayer()
        Mockito.verify(commandManager)?.addCommand(command)
        Mockito.verifyZeroInteractions(
            selectedLayer,
            layerMenuViewHolder,
            layerAdapter,
            listItemDragHandler
        )
    }

    @Test
    fun testRemoveLayerWhenOnlyOneLayer() {
        layerModel?.addLayerAt(0, Mockito.mock(Layer::class.java))
        createPresenter()
        layerPresenter?.removeLayer()
        Mockito.verifyZeroInteractions(
            commandManager,
            commandFactory,
            layerMenuViewHolder,
            layerAdapter,
            listItemDragHandler
        )
    }

    @Test
    fun testOnDragLayerAtPosition() {
        val view = Mockito.mock(View::class.java)
        val firstLayer = Layer(Mockito.mock(Bitmap::class.java))
        val secondLayer = Layer(Mockito.mock(Bitmap::class.java))
        Assert.assertTrue(firstLayer.isVisible)
        Assert.assertTrue(secondLayer.isVisible)
        layerModel?.addLayerAt(0, firstLayer)
        layerModel?.addLayerAt(1, secondLayer)
        createPresenter()
        layerPresenter?.onStartDragging(0, view)
        Mockito.verify(listItemDragHandler)?.startDragging(0, view)
        Mockito.verifyZeroInteractions(
            commandManager,
            commandFactory,
            layerMenuViewHolder,
            layerAdapter
        )
    }

    @Test
    fun testOnDragLayerAtPositionWhenOnlyOneLayer() {
        val view = Mockito.mock(View::class.java)
        layerModel?.addLayerAt(0, Mockito.mock(Layer::class.java))
        createPresenter()
        layerPresenter?.onStartDragging(0, view)
        Mockito.verifyZeroInteractions(
            listItemDragHandler,
            commandManager,
            commandFactory,
            layerMenuViewHolder,
            layerAdapter
        )
    }

    @Test
    fun testOnDragLayerAtPositionWhenPositionOutOfBounds() {
        val view = Mockito.mock(View::class.java)
        layerModel?.addLayerAt(0, Mockito.mock(Layer::class.java))
        layerModel?.addLayerAt(1, Mockito.mock(Layer::class.java))
        createPresenter()
        layerPresenter?.onStartDragging(2, view)
        Mockito.verifyZeroInteractions(
            listItemDragHandler,
            commandManager,
            commandFactory,
            layerMenuViewHolder,
            layerAdapter
        )
    }

    @Test
    fun testOnClickLayerAtPositionWhenDeselected() {
        layerModel?.addLayerAt(0, Mockito.mock(Layer::class.java))
        layerModel?.addLayerAt(1, Mockito.mock(Layer::class.java))
        layerModel?.currentLayer = layerModel?.getLayerAt(1)
        val command = Mockito.mock(Command::class.java)
        Mockito.`when`(commandFactory?.createSelectLayerCommand(0)).thenReturn(command)
        createPresenter()
        layerPresenter?.setLayerSelected(0)
        Mockito.verify(commandManager)?.addCommand(command)
    }

    @Test
    fun testOnClickLayerAtPositionWhenAlreadySelected() {
        val layer = Mockito.mock(Layer::class.java)
        layerModel?.addLayerAt(0, layer)
        layerModel?.currentLayer = layer
        createPresenter()
        layerPresenter?.setLayerSelected(0)
        Mockito.verifyZeroInteractions(
            commandManager,
            commandFactory,
            layerMenuViewHolder,
            layerAdapter
        )
    }

    @Test
    fun testOnClickLayerAtPositionWhenPositionOutOfBounds() {
        val firstLayer = Mockito.mock(Layer::class.java)
        val secondLayer = Mockito.mock(Layer::class.java)
        layerModel?.addLayerAt(0, firstLayer)
        layerModel?.addLayerAt(1, secondLayer)
        layerModel?.currentLayer = firstLayer
        createPresenter()
        layerPresenter?.setLayerSelected(2)
        Mockito.verifyZeroInteractions(
            commandManager,
            commandFactory,
            layerMenuViewHolder,
            layerAdapter
        )
    }

    @Test
    fun testSwapItemsVisuallyDoesNotModifyModel() {
        val firstLayer = Mockito.mock(Layer::class.java)
        val secondLayer = Mockito.mock(Layer::class.java)
        layerModel?.addLayerAt(0, firstLayer)
        layerModel?.addLayerAt(1, secondLayer)
        createPresenter()
        layerPresenter?.swapItemsVisually(0, 1)
        Assert.assertEquals(2, layerModel?.layerCount)
        Assert.assertEquals(0, layerModel?.getLayerIndexOf(firstLayer))
        Assert.assertEquals(1, layerModel?.getLayerIndexOf(secondLayer))
        Mockito.verifyZeroInteractions(commandManager, layerMenuViewHolder, firstLayer, secondLayer)
    }

    @Test
    fun testSwapItemsVisually() {
        val firstLayer = Mockito.mock(Layer::class.java)
        val secondLayer = Mockito.mock(Layer::class.java)
        layerModel?.addLayerAt(0, firstLayer)
        layerModel?.addLayerAt(1, secondLayer)
        createPresenter()
        layerPresenter?.swapItemsVisually(0, 1)
        Assert.assertEquals(2, layerPresenter?.layerCount)
        Assert.assertEquals(firstLayer, layerPresenter?.getLayerItem(1))
        Assert.assertEquals(secondLayer, layerPresenter?.getLayerItem(0))
        Mockito.verifyZeroInteractions(commandManager, layerMenuViewHolder, firstLayer, secondLayer)
    }

    @Test
    fun testMergeItems() {
        val firstLayer = Mockito.mock(Layer::class.java)
        val secondLayer = Mockito.mock(Layer::class.java)
        layerModel?.addLayerAt(0, firstLayer)
        layerModel?.addLayerAt(1, secondLayer)
        val command = Mockito.mock(Command::class.java)
        Mockito.`when`(commandFactory?.createMergeLayersCommand(0, 1)).thenReturn(command)
        createPresenter()
        layerPresenter?.mergeItems(0, 1)
        Mockito.verify(commandManager)?.addCommand(command)
        Mockito.verify(navigator)?.showToast(R.string.layer_merged, Toast.LENGTH_SHORT)
        Mockito.verifyNoMoreInteractions(commandManager)
        Mockito.verifyZeroInteractions(layerMenuViewHolder, firstLayer, secondLayer, command)
    }

    @Test
    fun testMergeItemsWhenAtSamePosition() {
        val firstLayer = Mockito.mock(Layer::class.java)
        val secondLayer = Mockito.mock(Layer::class.java)
        layerModel?.addLayerAt(0, firstLayer)
        layerModel?.addLayerAt(1, secondLayer)
        createPresenter()
        layerPresenter?.mergeItems(0, 0)
        Mockito.verifyZeroInteractions(
            commandManager,
            layerMenuViewHolder,
            firstLayer,
            secondLayer,
            navigator
        )
    }

    @Test
    fun testMergeItemsAfterVisualSwap() {
        val firstLayer = Mockito.mock(Layer::class.java)
        val secondLayer = Mockito.mock(Layer::class.java)
        val thirdLayer = Mockito.mock(Layer::class.java)
        layerModel?.addLayerAt(0, firstLayer)
        layerModel?.addLayerAt(1, secondLayer)
        layerModel?.addLayerAt(2, thirdLayer)
        val command = Mockito.mock(Command::class.java)
        Mockito.`when`(commandFactory?.createMergeLayersCommand(0, 1)).thenReturn(command)
        createPresenter()
        layerPresenter?.swapItemsVisually(0, 1)
        layerPresenter?.mergeItems(0, 0)
        Mockito.verify(commandManager)?.addCommand(command)
        Mockito.verify(navigator)?.showToast(R.string.layer_merged, Toast.LENGTH_SHORT)
        Mockito.verifyNoMoreInteractions(commandManager, firstLayer, secondLayer, thirdLayer)
        Mockito.verifyZeroInteractions(layerMenuViewHolder, command)
    }

    @Test
    fun testReorderItems() {
        val firstLayer = Mockito.mock(Layer::class.java)
        val secondLayer = Mockito.mock(Layer::class.java)
        layerModel?.addLayerAt(0, firstLayer)
        layerModel?.addLayerAt(1, secondLayer)
        val command = Mockito.mock(Command::class.java)
        Mockito.`when`(commandFactory?.createReorderLayersCommand(1, 0)).thenReturn(command)
        createPresenter()
        layerPresenter?.reorderItems(1, 0)
        Mockito.verify(commandManager)?.addCommand(command)
        Mockito.verifyNoMoreInteractions(commandManager)
        Mockito.verifyZeroInteractions(layerMenuViewHolder, firstLayer, secondLayer, command)
    }

    @Test
    fun testReorderItemsAfterVisualSwap() {
        val firstLayer = Mockito.mock(Layer::class.java)
        val secondLayer = Mockito.mock(Layer::class.java)
        val thirdLayer = Mockito.mock(Layer::class.java)
        layerModel?.addLayerAt(0, firstLayer)
        layerModel?.addLayerAt(1, secondLayer)
        layerModel?.addLayerAt(2, thirdLayer)
        val command = Mockito.mock(Command::class.java)
        Mockito.`when`(commandFactory?.createReorderLayersCommand(2, 0)).thenReturn(command)
        createPresenter()
        layerPresenter?.swapItemsVisually(2, 1)
        layerPresenter?.swapItemsVisually(1, 0)
        layerPresenter?.reorderItems(2, 0)
        Mockito.verify(commandManager)?.addCommand(command)
        Mockito.verifyNoMoreInteractions(commandManager)
        Mockito.verifyZeroInteractions(layerMenuViewHolder, firstLayer, secondLayer, command)
    }

    @Test
    fun testReorderItemsWhenAlreadyAtThisPosition() {
        val firstLayer = Mockito.mock(Layer::class.java)
        val secondLayer = Mockito.mock(Layer::class.java)
        layerModel?.addLayerAt(0, firstLayer)
        layerModel?.addLayerAt(1, secondLayer)
        createPresenter()
        layerPresenter?.reorderItems(1, 1)
        Mockito.verifyZeroInteractions(commandManager, layerMenuViewHolder, firstLayer, secondLayer)
    }

    @Test
    fun testMarkMergeable() {
        val firstLayer = Mockito.mock(Layer::class.java)
        val secondLayer = Mockito.mock(Layer::class.java)
        layerModel?.addLayerAt(0, firstLayer)
        layerModel?.addLayerAt(1, secondLayer)
        val layerViewHolder = Mockito.mock(LayerContracts.LayerViewHolder::class.java)
        Mockito.`when`(layerAdapter?.getViewHolderAt(1)).thenReturn(layerViewHolder)
        createPresenter()
        layerPresenter?.markMergeable(0, 1)
        Mockito.verify(layerViewHolder).setMergable()
        Mockito.verifyZeroInteractions(commandManager, layerMenuViewHolder, listItemDragHandler)
    }

    @Test
    fun testMarkMergeableWhenPositionInvalidDoesNothing() {
        createPresenter()
        layerPresenter?.markMergeable(0, 1)
        Mockito.verifyZeroInteractions(commandManager, layerMenuViewHolder, listItemDragHandler)
    }

    @Test
    fun testCommandPostExecuteResetsInternalModel() {
        val firstLayer = Mockito.mock(Layer::class.java)
        val secondLayer = Mockito.mock(Layer::class.java)
        layerModel?.addLayerAt(0, firstLayer)
        layerModel?.addLayerAt(1, secondLayer)
        createPresenter()
        layerPresenter?.swapItemsVisually(0, 1)
        Assert.assertEquals(secondLayer, layerPresenter?.getLayerItem(0))
        Assert.assertEquals(firstLayer, layerPresenter?.getLayerItem(1))
        layerPresenter?.invalidate()
        Assert.assertEquals(firstLayer, layerPresenter?.getLayerItem(0))
        Assert.assertEquals(secondLayer, layerPresenter?.getLayerItem(1))
    }

    @Test
    fun testCommandPostExecute() {
        layerModel?.addLayerAt(0, Mockito.mock(Layer::class.java))
        layerModel?.addLayerAt(1, Mockito.mock(Layer::class.java))
        createPresenter()
        layerPresenter?.invalidate()
        Mockito.verify(layerAdapter)?.notifyDataSetChanged()
        Mockito.verify(layerMenuViewHolder)?.enableAddLayerButton()
        Mockito.verify(layerMenuViewHolder)?.enableRemoveLayerButton()
    }
}
