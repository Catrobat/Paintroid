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
import android.graphics.Matrix
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.contract.LayerContracts

class RotateCommand(rotateDirection: RotateDirection) : Command {

    var rotateDirection = rotateDirection; private set

    companion object {
        private const val ANGLE = 90f
    }

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        val rotateMatrix = Matrix().apply {
            when (rotateDirection) {
                RotateDirection.ROTATE_RIGHT -> postRotate(ANGLE)
                RotateDirection.ROTATE_LEFT -> postRotate(-ANGLE)
            }
        }
        val iterator: Iterator<LayerContracts.Layer> = layerModel.listIterator(0)
        while (iterator.hasNext()) {
            val currentLayer = iterator.next()
            val rotatedBitmap = Bitmap.createBitmap(
                currentLayer.bitmap, 0, 0,
                layerModel.width, layerModel.height, rotateMatrix, true
            )
            currentLayer.bitmap = rotatedBitmap
        }
        val tmpWidth = layerModel.width
        layerModel.width = layerModel.height
        layerModel.height = tmpWidth
    }

    override fun freeResources() {
        // No resources to free
    }

    enum class RotateDirection {
        ROTATE_LEFT, ROTATE_RIGHT
    }
}
