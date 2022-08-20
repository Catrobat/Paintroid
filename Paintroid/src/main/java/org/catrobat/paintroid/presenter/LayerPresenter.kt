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
package org.catrobat.paintroid.presenter

import android.graphics.PointF
import android.util.Log
import android.view.View
import android.widget.Toast
import org.catrobat.paintroid.R
import org.catrobat.paintroid.command.CommandFactory
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.common.MAX_LAYERS
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.controller.DefaultToolController
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.implementation.ClippingTool
import org.catrobat.paintroid.tools.implementation.LineTool
import org.catrobat.paintroid.ui.DrawingSurface
import org.catrobat.paintroid.ui.dragndrop.DragAndDropPresenter
import org.catrobat.paintroid.ui.dragndrop.ListItemDragHandler
import org.catrobat.paintroid.ui.viewholder.BottomNavigationViewHolder
import java.util.Collections.swap

class LayerPresenter(
    private val model: LayerContracts.Model,
    private val listItemDragHandler: ListItemDragHandler,
    private val layerMenuViewHolder: LayerContracts.LayerMenuViewHolder,
    private val commandManager: CommandManager,
    private val commandFactory: CommandFactory,
    private val navigator: LayerContracts.Navigator
) : LayerContracts.Presenter, DragAndDropPresenter {
    private var adapter: LayerContracts.Adapter? = null
    private var drawingSurface: DrawingSurface? = null
    private var defaultToolController: DefaultToolController? = null
    private var bottomNavigationViewHolder: BottomNavigationViewHolder? = null
    private val layers: MutableList<LayerContracts.Layer>

    override val presenter: LayerPresenter
        get() = this

    override val layerCount: Int
        get() = layers.size

    companion object {
        private val TAG = LayerPresenter::class.java.simpleName
    }

    init {
        layers = ArrayList(model.layers)
    }

    private fun isPositionValid(position: Int): Boolean = position >= 0 && position < layers.size

    private fun checkIfLineToolInUse() {
        defaultToolController?.apply {
            if (toolType == ToolType.LINE) {
                val lineTool = currentTool as LineTool
                if (!lineTool.lineFinalized && lineTool.startpointSet && !lineTool.endpointSet) {
                    if (commandManager.isUndoAvailable) {
                        commandManager.undoIgnoringColorChanges()
                    }
                    lineTool.startPointToDraw = null
                    lineTool.startpointSet = false
                } else if (!lineTool.lineFinalized && lineTool.startpointSet && lineTool.endpointSet) {
                    lineTool.toolSwitched = true
                    lineTool.onClickOnButton()
                }
            }
        }
    }

    private fun checkIfClippingToolInUse(): Boolean {
        if (defaultToolController?.currentTool?.toolType == ToolType.CLIP) {
            val clippingTool = defaultToolController?.currentTool as ClippingTool
            clippingTool.wasRecentlyApplied = true
            if (clippingTool.areaClosed) {
                clippingTool.handleDown(PointF(0f, 0f))
                clippingTool.initialEventCoordinate = null
                clippingTool.previousEventCoordinate = null
                clippingTool.pathToDraw.rewind()
                clippingTool.pointArray.clear()
            }
            return true
        } else {
            return false
        }
    }

    override fun setAdapter(layerAdapter: LayerContracts.Adapter) {
        this.adapter = layerAdapter
    }

    override fun setDrawingSurface(drawingSurface: DrawingSurface) {
        this.drawingSurface = drawingSurface
    }

    override fun setDefaultToolController(defaultToolController: DefaultToolController) {
        this.defaultToolController = defaultToolController
    }

    override fun setBottomNavigationViewHolder(bottomNavigationViewHolder: BottomNavigationViewHolder) {
        this.bottomNavigationViewHolder = bottomNavigationViewHolder
    }

    override fun onBindLayerViewHolderAtPosition(
        position: Int,
        viewHolder: LayerContracts.LayerViewHolder,
        isOpen: Boolean
    ) {
        val layer = getLayerItem(position)
        if (layer === model.currentLayer) {
            viewHolder.setSelected(position, bottomNavigationViewHolder, defaultToolController)
        } else {
            viewHolder.setDeselected()
        }
        if (!layers[position].isVisible) {
            viewHolder.updateImageView(layer.transparentBitmap)
            viewHolder.setLayerVisibilityCheckbox(false)
        } else {
            viewHolder.updateImageView(layer.bitmap)
            viewHolder.setLayerVisibilityCheckbox(true)
        }
    }

    override fun refreshLayerMenuViewHolder() {
        if (layerCount < MAX_LAYERS) {
            layerMenuViewHolder.enableAddLayerButton()
        } else {
            layerMenuViewHolder.disableAddLayerButton()
        }
        if (layerCount > 1) {
            layerMenuViewHolder.enableRemoveLayerButton()
        } else {
            layerMenuViewHolder.disableRemoveLayerButton()
        }
    }

    override fun isShown(): Boolean = layerMenuViewHolder.isShown()

    override fun getLayerItem(position: Int): LayerContracts.Layer = layers[position]

    override fun getLayerItemId(position: Int): Long = position.toLong()

    override fun addLayer() {
        if (layerCount < MAX_LAYERS) {
            checkIfLineToolInUse()
            val clippingToolInUse = checkIfClippingToolInUse()
            commandManager.addCommand(commandFactory.createAddEmptyLayerCommand())
            if (clippingToolInUse) (defaultToolController?.currentTool as ClippingTool).copyBitmapOfCurrentLayer()
        } else {
            navigator.showToast(R.string.layer_too_many_layers, Toast.LENGTH_SHORT)
        }
    }

    override fun removeLayer() {
        if (layerCount > 1) {
            checkIfLineToolInUse()
            val clippingToolInUse = checkIfClippingToolInUse()
            val layerToDelete = model.currentLayer ?: return
            val index = model.getLayerIndexOf(layerToDelete)
            commandManager.addCommand(commandFactory.createRemoveLayerCommand(index))
            if (clippingToolInUse) (defaultToolController?.currentTool as ClippingTool).copyBitmapOfCurrentLayer()
        }
    }

    private fun getDestinationLayer(
        position: Int,
        isUnhide: Boolean
    ): LayerContracts.Layer? = model.getLayerAt(position)?.apply {
        if (isUnhide) switchBitmaps(isUnhide)
        val newBitmap = if (!isUnhide) transparentBitmap else bitmap
        if (!isUnhide) switchBitmaps(isUnhide)
        bitmap = newBitmap
        isVisible = isUnhide
    }

    override fun hideLayer(position: Int) {
        drawingSurface?.refreshDrawingSurface()
        getDestinationLayer(position, false)?.let { layer ->
            if (model.currentLayer == layer) {
                defaultToolController?.switchTool(ToolType.HAND)
                bottomNavigationViewHolder?.showCurrentTool(ToolType.HAND)
            }
        }
    }

    override fun unhideLayer(position: Int, viewHolder: LayerContracts.LayerViewHolder) {
        drawingSurface?.refreshDrawingSurface()
        getDestinationLayer(position, true)?.let { layer ->
            viewHolder.updateImageView(layer.bitmap)
            if (model.currentLayer == layer) {
                defaultToolController?.switchTool(ToolType.BRUSH)
                bottomNavigationViewHolder?.showCurrentTool(ToolType.BRUSH)
            }
        }
    }

    override fun swapItemsVisually(position: Int, swapWith: Int): Int {
        swap(layers, position, swapWith)
        return swapWith
    }

    override fun mergeItems(position: Int, mergeWith: Int) {
        checkIfLineToolInUse()
        layers.getOrNull(mergeWith)?.let { actualLayer ->
            val actualPosition = model.getLayerIndexOf(actualLayer)
            val clippingToolInUse = checkIfClippingToolInUse()
            if (position != actualPosition && actualPosition > -1) {
                commandManager.addCommand(
                    commandFactory.createMergeLayersCommand(position, actualPosition)
                )
                navigator.showToast(R.string.layer_merged, Toast.LENGTH_SHORT)
                if (clippingToolInUse) (defaultToolController?.currentTool as ClippingTool).copyBitmapOfCurrentLayer()
            }
        }
    }

    override fun reorderItems(position: Int, swapWith: Int) {
        if (position != swapWith) {
            checkIfLineToolInUse()
            commandManager.addCommand(commandFactory.createReorderLayersCommand(position, swapWith))
            checkIfClippingToolInUse()
        }
    }

    override fun markMergeable(position: Int, mergeWith: Int) {
        if (!isPositionValid(position) || !isPositionValid(mergeWith)) {
            Log.e(TAG, "onLongClickLayerAtPosition at invalid position")
            return
        }
        adapter?.getViewHolderAt(mergeWith)?.setMergable()
    }

    override fun onStopDragging() {
        listItemDragHandler.stopDragging()
    }

    override fun onStartDragging(position: Int, view: View) {
        if (!isPositionValid(position)) {
            Log.e(TAG, "onLongClickLayerAtPosition at invalid position")
            return
        }
        var isAllowedToLongclick = true
        for (i in layers.indices) {
            if (!layers[i].isVisible) {
                isAllowedToLongclick = false
            }
        }
        if (isAllowedToLongclick) {
            if (layerCount > 1) {
                listItemDragHandler.startDragging(position, view)
            }
        } else {
            navigator.showToast(R.string.no_longclick_on_hidden_layer, Toast.LENGTH_SHORT)
        }
    }

    override fun onClickLayerAtPosition(position: Int, view: View) {
        if (!isPositionValid(position)) {
            Log.e(TAG, "onClickLayerAtPosition at invalid position")
            return
        }
        if (position != model.currentLayer?.let { model.getLayerIndexOf(it) }) {
            checkIfLineToolInUse()
            commandManager.addCommand(commandFactory.createSelectLayerCommand(position))
            checkIfClippingToolInUse()
        }
    }

    override fun invalidate() {
        synchronized(model) {
            layers.clear()
            layers.addAll(model.layers)
        }
        refreshLayerMenuViewHolder()
        adapter?.notifyDataSetChanged()
        listItemDragHandler.stopDragging()
    }

    fun resetMergeColor(layerPosition: Int) {
        if (adapter != null && adapter?.getViewHolderAt(layerPosition) != null) {
            if (adapter?.getViewHolderAt(layerPosition)?.isSelected() == true) {
                adapter?.getViewHolderAt(layerPosition)?.setSelected()
            } else {
                adapter?.getViewHolderAt(layerPosition)?.setDeselected()
            }
        }
    }
}
