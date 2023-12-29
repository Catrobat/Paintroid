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
package org.catrobat.paintroid.model

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.tools.Tool

open class LayerModel : LayerContracts.Model {
    override var currentLayer: LayerContracts.Layer? = null
    override var width = 0
    override var height = 0
    override val layers: ArrayList<LayerContracts.Layer> = arrayListOf()
    override val layerCount: Int
        get() = layers.size

    override fun reset() {
        layers.clear()
    }

    override fun getLayerAt(index: Int): LayerContracts.Layer? = layers.getOrNull(index)

    override fun getLayerIndexOf(layer: LayerContracts.Layer): Int = layers.indexOf(layer)

    @SuppressWarnings("SwallowedException", "TooGenericExceptionCaught")
    override fun addLayerAt(index: Int, layer: LayerContracts.Layer): Boolean = try {
        layers.add(index, layer)
        true
    } catch (e: IndexOutOfBoundsException) {
        false
    }

    override fun listIterator(index: Int): ListIterator<LayerContracts.Layer> =
        layers.listIterator(index)

    override fun setLayerAt(position: Int, layer: LayerContracts.Layer) {
        layers[position] = layer
    }

    @SuppressWarnings("SwallowedException", "TooGenericExceptionCaught")
    override fun removeLayerAt(position: Int): Boolean = try {
        layers.removeAt(position)
        true
    } catch (e: IndexOutOfBoundsException) {
        false
    }

    @Synchronized
    override fun getBitmapOfAllLayers(): Bitmap? {
        synchronized(this) {
            if (layers.isEmpty()) {
                return null
            }
            val referenceBitmap = layers[0].bitmap
            val bitmap = Bitmap.createBitmap(referenceBitmap.width, referenceBitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = bitmap?.let { Canvas(it) }

            drawLayersOntoCanvas(canvas)

            return bitmap
        }
    }

    override fun getBitmapListOfAllLayers(): List<Bitmap?> = layers.map { it.bitmap }

    fun drawLayersOntoCanvas(canvas: Canvas?) {
        layers.asReversed().forEach { layer ->
            if (layer.isVisible) {
                val alphaPaint = Paint().apply {
                    isFilterBitmap = false
                    alpha = layer.getValueForOpacityPercentage()
                }
                canvas?.drawBitmap(layer.bitmap, 0f, 0f, alphaPaint)
            }
        }
    }

    fun drawLayersOntoCanvasCorrectOrder(
        surfaceViewCanvas: Canvas?,
        currentLayerIndex: Int?,
        drawingBoardCanvas: Canvas?,
        tool: Tool?
    ) {
        layers.asReversed().forEach { layer ->
            val layerIndex = getLayerIndexOf(layer)
            if (layer.isVisible) {
                val alphaPaint = Paint().apply {
                    isFilterBitmap = false
                    alpha = layer.getValueForOpacityPercentage()
                }
                surfaceViewCanvas?.drawBitmap(layer.bitmap, 0f, 0f, alphaPaint)
                drawingBoardCanvas?.drawBitmap(layer.bitmap, 0f, 0f, alphaPaint)
                if (surfaceViewCanvas != null && layerIndex == currentLayerIndex) {
                    tool?.draw(surfaceViewCanvas)
                }
                if (drawingBoardCanvas != null && layerIndex == currentLayerIndex) {
                    tool?.draw(drawingBoardCanvas)
                }
            }
        }
    }

    fun drawLayersOntoCanvasCorrectOrderEraser(
        surfaceViewCanvas: Canvas?,
        currentLayerIndex: Int?,
        tool: Tool?
    ) {
        var bitmapWithEraseApplied: Bitmap?
        var eraseAppliedCanvas: Canvas?
        layers.asReversed().forEach { layer ->
            val layerIndex = getLayerIndexOf(layer)
            if (layer.isVisible) {
                val alphaPaint = Paint().apply {
                    isFilterBitmap = false
                    alpha = layer.getValueForOpacityPercentage()
                }
                surfaceViewCanvas?.drawBitmap(layer.bitmap, 0f, 0f, alphaPaint)
                if (surfaceViewCanvas != null && layerIndex == currentLayerIndex) {
                    bitmapWithEraseApplied = currentLayer?.bitmap
                    eraseAppliedCanvas = bitmapWithEraseApplied?.let { Canvas(it) }
                    eraseAppliedCanvas?.let { tool?.draw(it) }
                    bitmapWithEraseApplied?.let { surfaceViewCanvas.drawBitmap(it, 0f, 0f, alphaPaint) }
                }
            }
        }
    }
}
