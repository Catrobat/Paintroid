/*
 * Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.paintroid.command.implementation

import android.graphics.Bitmap
import android.graphics.Canvas
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.model.Layer

class LoadLayerListCommand(loadedLayers: List<LayerContracts.Layer>) : Command {

    var loadedLayers = loadedLayers; private set

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        loadedLayers.forEachIndexed { index, layer ->
            val currentLayer = Layer(layer.bitmap.copy(Bitmap.Config.ARGB_8888, true))
            currentLayer.opacityPercentage = layer.opacityPercentage
            layerModel.addLayerAt(index, currentLayer)
        }
        layerModel.currentLayer = layerModel.getLayerAt(0)
    }

    override fun freeResources() {
        // No resources to free
    }
}
