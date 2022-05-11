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
import android.graphics.PointF
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.Paint
import android.graphics.ColorMatrixColorFilter
import android.graphics.RectF
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.tools.implementation.PRESSURE_UPDATE_STEP

class SmudgePathCommand(bitmap: Bitmap, pointPath: MutableList<PointF>, maxPressure: Float, maxSize: Float, minSize: Float) : Command {

    var originalBitmap = bitmap; private set
    var pointPath = pointPath; private set
    var maxPressure = maxPressure; private set
    var maxSize = maxSize; private set
    var minSize = minSize; private set

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        val step = (maxSize - minSize) / pointPath.size
        var size = maxSize
        var pressure = maxPressure
        val colorMatrix = ColorMatrix()
        val paint = Paint()
        var bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, false)

        pointPath.forEach {
            colorMatrix.setScale(1f, 1f, 1f, pressure)
            paint.colorFilter = ColorMatrixColorFilter(colorMatrix)

            val newBitmap = Bitmap.createBitmap(maxSize.toInt(), maxSize.toInt(), Bitmap.Config.ARGB_8888)

            newBitmap.let {
                Canvas(it).apply {
                    drawBitmap(bitmap, 0f, 0f, paint)
                }
            }

            bitmap.recycle()
            bitmap = newBitmap

            val rect = RectF(-size / 2f, -size / 2f, size / 2f, size / 2f)
            with(canvas) {
                save()
                translate(it.x, it.y)
                drawBitmap(bitmap, null, rect, Paint(Paint.DITHER_FLAG))
                restore()
            }
            size -= step
            pressure -= PRESSURE_UPDATE_STEP
        }
    }

    override fun freeResources() {
        originalBitmap.recycle()
    }
}
