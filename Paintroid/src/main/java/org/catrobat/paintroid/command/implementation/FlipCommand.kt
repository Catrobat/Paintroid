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

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.contract.LayerContracts

class FlipCommand(flipDirection: FlipDirection) : Command {

    var flipDirection = flipDirection; private set

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        val flipMatrix = Matrix().apply {
            when (flipDirection) {
                FlipDirection.FLIP_HORIZONTAL -> {
                    setScale(1f, -1f)
                    postTranslate(0f, layerModel.height.toFloat())
                }
                FlipDirection.FLIP_VERTICAL -> {
                    setScale(-1f, 1f)
                    postTranslate(layerModel.width.toFloat(), 0f)
                }
            }
        }
        layerModel.currentLayer?.bitmap?.let { bitmap ->
            val bitmapCopy = bitmap.copy(bitmap.config, bitmap.isMutable)
            val flipCanvas = Canvas(bitmap)
            bitmap.eraseColor(Color.TRANSPARENT)
            flipCanvas.drawBitmap(bitmapCopy, flipMatrix, Paint())
        }
    }

    override fun freeResources() {
        // No resources to free
    }

    enum class FlipDirection {
        FLIP_HORIZONTAL, FLIP_VERTICAL
    }
}
