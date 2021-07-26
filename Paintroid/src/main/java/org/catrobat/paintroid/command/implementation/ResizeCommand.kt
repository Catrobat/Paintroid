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
package org.catrobat.paintroid.command.implementation

import android.graphics.Bitmap
import android.graphics.Canvas
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.contract.LayerContracts

class ResizeCommand(newWidth: Int, newHeight: Int) : Command {

    var newWidth = newWidth; private set
    var newHeight = newHeight; private set

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        val iterator = layerModel.listIterator(0)
        while (iterator.hasNext()) {
            val currentLayer = iterator.next()
            currentLayer.bitmap?.let { currentBitmap ->
                val resizedBitmap = Bitmap.createScaledBitmap(currentBitmap, newWidth, newHeight, true)
                currentLayer.bitmap = resizedBitmap
            }
        }
        layerModel.height = newHeight
        layerModel.width = newWidth
    }

    override fun freeResources() {
        // No resources to free
    }
}
