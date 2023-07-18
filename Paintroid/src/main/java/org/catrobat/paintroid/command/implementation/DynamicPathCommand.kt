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
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.Log
import androidx.annotation.ColorInt
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.tools.implementation.DynamicLineTool

class DynamicPathCommand(var paint: Paint, path: Path, startPoint: PointF, endPoint: PointF) : Command {

    var path = path
    var startPoint: PointF? = startPoint
    var endPoint: PointF? = endPoint

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        canvas.drawPath(path, paint)
        Log.e(DynamicLineTool.TAG, "run PathCommand")
    }

    override fun freeResources() {
        // No resources to free
    }

    fun updatePath(newPath: Path) {
        this.path = newPath
    }

    fun setPaintColor(@ColorInt newColor: Int) {
        this.paint.color = newColor
    }

    fun setPaintStrokeWidth(newStrokeWidth: Float) {
        this.paint.strokeWidth = newStrokeWidth
    }

    fun setPaintStrokeCap(newCap: Paint.Cap) {
        this.paint.strokeCap = newCap
    }

    fun setStartAndEndPoint(startPoint: PointF?, endPoint: PointF?) {
        this.startPoint = startPoint
        this.endPoint = endPoint
    }
}
