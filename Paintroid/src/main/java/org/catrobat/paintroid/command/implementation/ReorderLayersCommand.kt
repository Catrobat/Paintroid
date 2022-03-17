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

class ReorderLayersCommand(position: Int, destination: Int) : Command {

    var position = position; private set
    var destination = destination; private set

    companion object {
        private val TAG = ReorderLayersCommand::class.java.simpleName
    }

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        layerModel.run {
            var success = false
            getLayerAt(position)?.let { layer ->
                if (removeLayerAt(position)) {
                    success = addLayerAt(destination, layer)
                }
            }

            if (!success) {
                Log.e(TAG, "Could not retrieve layer to reorder!")
            }
        }
    }

    override fun freeResources() {
        // No resources to free
    }
}
