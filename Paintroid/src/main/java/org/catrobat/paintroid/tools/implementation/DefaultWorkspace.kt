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
package org.catrobat.paintroid.tools.implementation

import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.RectF
import org.catrobat.paintroid.command.serialization.CommandSerializationUtilities
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.model.LayerModel
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.ui.Perspective

class DefaultWorkspace(
    private val layerModel: LayerContracts.Model,
    override var perspective: Perspective,
    private val listener: Listener,
    private val serializationHelper: CommandSerializationUtilities
) : Workspace {
    override val height: Int
        get() = layerModel.height

    override val width: Int
        get() = layerModel.width

    override val surfaceWidth: Int
        get() = perspective.surfaceWidth

    override val surfaceHeight: Int
        get() = perspective.surfaceHeight

    override val bitmapOfAllLayers: Bitmap?
        get() = LayerModel.getBitmapOfAllLayersToSave(layerModel.layers)

    override val bitmapLisOfAllLayers: List<Bitmap?>
        get() = LayerModel.getBitmapListOfAllLayers(layerModel.layers)

    override val bitmapOfCurrentLayer: Bitmap?
        get() = layerModel.currentLayer?.bitmap?.let { Bitmap.createBitmap(it) }

    override val currentLayerIndex: Int
        get() {
            var index = -1
            layerModel.currentLayer?.let {
                index = layerModel.getLayerIndexOf(it)
            }
            return index
        }

    override val scaleForCenterBitmap: Float
        get() = perspective.scaleForCenterBitmap

    override var scale: Float
        get() = perspective.scale
        set(zoomFactor) {
            perspective.scale = zoomFactor
        }

    override fun resetPerspective() {
        perspective.setBitmapDimensions(width, height)
        perspective.resetScaleAndTranslation()
    }

    override fun getCommandSerializationHelper(): CommandSerializationUtilities =
        serializationHelper

    override fun getCanvasPointFromSurfacePoint(surfacePoint: PointF): PointF =
        perspective.getCanvasPointFromSurfacePoint(surfacePoint)

    override fun invalidate() {
        listener.invalidate()
    }

    override fun contains(point: PointF): Boolean =
        point.x < width && point.x >= 0 && point.y < height && point.y >= 0

    override fun intersectsWith(rectF: RectF): Boolean =
        0 < rectF.right && rectF.left < width && 0 < rectF.bottom && rectF.top < height

    override fun getSurfacePointFromCanvasPoint(coordinate: PointF): PointF =
        perspective.getSurfacePointFromCanvasPoint(coordinate)

    fun interface Listener {
        fun invalidate()
    }
}
