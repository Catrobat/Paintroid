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
package org.catrobat.paintroid.tools.drawable

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF

class HeartDrawable : ShapeDrawable {
    private val path = Path()
    private val paint = Paint()
    override fun draw(canvas: Canvas, shapeRect: RectF, drawPaint: Paint) {
        paint.set(drawPaint)
        val midWidth = shapeRect.width() / 2
        val height = shapeRect.height()
        val width = shapeRect.width()
        path.run {
            reset()
            moveTo(midWidth, height)
            cubicTo(
                -0.2f * width, 4.5f * height / 8,
                0.8f * width / 8, -1.5f * height / 8,
                midWidth, 1.5f * height / 8
            )
            cubicTo(
                7.2f * width / 8, -1.5f * height / 8,
                1.2f * width, 4.5f * height / 8,
                midWidth, height
            )
            close()
            offset(shapeRect.left, shapeRect.top)
        }
        canvas.drawPath(path, paint)
    }
}
