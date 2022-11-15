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

class CropCommand(
    resizeCoordinateXLeft: Int,
    resizeCoordinateYTop: Int,
    resizeCoordinateXRight: Int,
    resizeCoordinateYBottom: Int,
    maximumBitmapResolution: Int
) : Command {

    var resizeCoordinateXLeft = resizeCoordinateXLeft; private set
    var resizeCoordinateYTop = resizeCoordinateYTop; private set
    var resizeCoordinateXRight = resizeCoordinateXRight; private set
    var resizeCoordinateYBottom = resizeCoordinateYBottom; private set
    var maximumBitmapResolution = maximumBitmapResolution; private set

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        if (resizeCoordinateXRight < resizeCoordinateXLeft || resizeCoordinateYBottom < resizeCoordinateYTop) {
            return
        }
        if (resizeCoordinateXLeft >= layerModel.width || resizeCoordinateXRight < 0 || resizeCoordinateYTop >= layerModel.height || resizeCoordinateYBottom < 0) {
            return
        }
        if (resizeCoordinateXLeft == 0 && resizeCoordinateXRight == layerModel.width - 1 && resizeCoordinateYBottom == layerModel.height - 1 && resizeCoordinateYTop == 0) {
            return
        }
        if ((resizeCoordinateXRight + 1 - resizeCoordinateXLeft) * (resizeCoordinateYBottom + 1 - resizeCoordinateYTop) > maximumBitmapResolution) {
            return
        }
        val width = resizeCoordinateXRight + 1 - resizeCoordinateXLeft
        val height = resizeCoordinateYBottom + 1 - resizeCoordinateYTop
        val iterator = layerModel.listIterator(0)
        while (iterator.hasNext()) {
            val currentLayer = iterator.next()
            val currentBitmap = currentLayer.bitmap ?: Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val resizedBitmap = Bitmap.createBitmap(width, height, currentBitmap.config)
            val resizedCanvas = Canvas(resizedBitmap)
            resizedCanvas.drawBitmap(currentBitmap, -resizeCoordinateXLeft.toFloat(), -resizeCoordinateYTop.toFloat(), null)
            currentLayer.bitmap = resizedBitmap
        }
        layerModel.height = height
        layerModel.width = width
    }

    override fun freeResources() {
        // No resources to free
    }
}
