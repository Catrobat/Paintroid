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
import android.graphics.Paint
import android.graphics.RectF
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.tools.drawable.ShapeDrawable

class GeometricFillCommand(
    shapeDrawable: ShapeDrawable,
    pointX: Int,
    pointY: Int,
    boxRect: RectF,
    boxRotation: Float,
    paint: Paint
) : Command {

    var shapeDrawable = shapeDrawable; private set
    var pointX = pointX; private set
    var pointY = pointY; private set
    var boxRect = boxRect; private set
    var boxRotation = boxRotation; private set
    var paint = paint; private set

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        with(canvas) {
            save()
            translate(pointX.toFloat(), pointY.toFloat())
            rotate(boxRotation)
            shapeDrawable.draw(this, boxRect, paint)
            restore()
        }
    }

    override fun freeResources() {
        // No resources to free
    }
}
