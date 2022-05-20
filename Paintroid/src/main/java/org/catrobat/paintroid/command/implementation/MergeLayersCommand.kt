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

package org.catrobat.paintroid.command.implementation

import android.graphics.Canvas
import android.util.Log
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.contract.LayerContracts

class MergeLayersCommand(position: Int, mergeWith: Int) : Command {

    var position = position; private set
    var mergeWith = mergeWith; private set

    companion object {
        private val TAG = MergeLayersCommand::class.java.simpleName
    }

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        val sourceLayer = layerModel.getLayerAt(position)
        val destinationLayer = layerModel.getLayerAt(mergeWith)
        var success = false
        if (sourceLayer != null && destinationLayer != null) {
            val destinationBitmap = destinationLayer.bitmap
            destinationBitmap ?: return
            val copyBitmap = destinationBitmap.copy(destinationBitmap.config, true)
            val copyCanvas = Canvas(copyBitmap)
            copyCanvas.drawBitmap(sourceLayer.bitmap ?: return, 0f, 0f, null)
            if (layerModel.removeLayerAt(position)) {
                destinationLayer.bitmap = copyBitmap
                if (sourceLayer == layerModel.currentLayer) {
                    layerModel.currentLayer = destinationLayer
                }
                success = true
            }
        }

        if (!success) {
            Log.e(TAG, "Could not merge layers!")
        }
    }

    override fun freeResources() {
        // No resources to free
    }
}
