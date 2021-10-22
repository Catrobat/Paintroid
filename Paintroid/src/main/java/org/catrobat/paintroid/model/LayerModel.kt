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
package org.catrobat.paintroid.model

import android.graphics.Bitmap
import android.graphics.Canvas
import org.catrobat.paintroid.contract.LayerContracts
import java.util.ArrayList

class LayerModel : LayerContracts.Model {
    override var currentLayer: LayerContracts.Layer? = null
    override var width = 0
    override var height = 0
    override val layers: ArrayList<LayerContracts.Layer> = ArrayList()
    override val layerCount: Int
        get() = layers.size

    override fun reset() {
        layers.clear()
    }

    override fun getLayerAt(index: Int): LayerContracts.Layer = layers[index]

    override fun getLayerIndexOf(layer: LayerContracts.Layer): Int = layers.indexOf(layer)

    override fun addLayerAt(index: Int, layer: LayerContracts.Layer) {
        layers.add(index, layer)
    }

    override fun listIterator(index: Int): ListIterator<LayerContracts.Layer> =
        layers.listIterator(index)

    override fun setLayerAt(position: Int, layer: LayerContracts.Layer) {
        layers[position] = layer
    }

    override fun removeLayerAt(position: Int) {
        layers.removeAt(position)
    }

    companion object {
        @JvmStatic
        fun getBitmapOfAllLayersToSave(layers: List<LayerContracts.Layer>): Bitmap? {
            if (layers.isEmpty()) {
                return null
            }
            val referenceBitmap = layers[0].bitmap
            val bitmap = referenceBitmap?.let {
                Bitmap.createBitmap(it.width, it.height, Bitmap.Config.ARGB_8888)
            }
            val canvas = bitmap?.let { Canvas(it) }
            val layerListIterator = layers.listIterator(layers.size)
            while (layerListIterator.hasPrevious()) {
                val layer = layerListIterator.previous()
                layer.bitmap?.let { canvas?.drawBitmap(it, 0f, 0f, null) }
            }
            return bitmap
        }

        @JvmStatic
        fun getBitmapListOfAllLayers(layers: List<LayerContracts.Layer>): List<Bitmap?> {
            val bitmapList: MutableList<Bitmap?> = ArrayList()
            for (layer in layers) {
                bitmapList.add(layer.bitmap)
            }
            return bitmapList
        }
    }
}
