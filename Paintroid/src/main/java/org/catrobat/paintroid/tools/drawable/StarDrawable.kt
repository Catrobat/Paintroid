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
package org.catrobat.paintroid.tools.drawable

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF

private const val CONSTANT_1 = 8f
private const val CONSTANT_2 = 1.8f
private const val CONSTANT_3 = 3f
private const val CONSTANT_4 = 2f

class StarDrawable : ShapeDrawable {
    private val path = Path()
    private val paint = Paint()
    override fun draw(canvas: Canvas, shapeRect: RectF, drawPaint: Paint) {
        paint.set(drawPaint)
        val midWidth = shapeRect.width() / 2
        val midHeight = shapeRect.height() / 2
        val height = shapeRect.height()
        val width = shapeRect.width()
        path.run {
            reset()
            moveTo(midWidth, 0f)
            lineTo(midWidth + width / CONSTANT_1, midHeight - height / CONSTANT_1)
            lineTo(width, midHeight - height / CONSTANT_1)
            lineTo(midWidth + CONSTANT_2 * width / CONSTANT_1, midHeight + height / CONSTANT_1)
            lineTo(midWidth + CONSTANT_3 * width / CONSTANT_1, height)
            lineTo(midWidth, midHeight + CONSTANT_4 * height / CONSTANT_1)
            lineTo(midWidth - CONSTANT_3 * width / CONSTANT_1, height)
            lineTo(midWidth - CONSTANT_2 * width / CONSTANT_1, midHeight + height / CONSTANT_1)
            lineTo(0f, midHeight - height / CONSTANT_1)
            lineTo(midWidth - width / CONSTANT_1, midHeight - height / CONSTANT_1)
            lineTo(midWidth, 0f)
            close()
            offset(shapeRect.left, shapeRect.top)
        }
        canvas.drawPath(path, paint)
    }
}
