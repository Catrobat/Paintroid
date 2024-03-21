package org.catrobat.paintroid.tools.drawable

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

import android.graphics.Path


class ArrowDrawable :ShapeDrawable {
    private val path = Path()
    override fun draw(canvas: Canvas, shapeRect: RectF, drawPaint: Paint) {
        val midWidth = shapeRect.width() / 2
        val midHeight = shapeRect.height() / 2

        path.run {
            reset()
            moveTo(shapeRect.left, midHeight)
            lineTo(midWidth, shapeRect.top)
            lineTo(shapeRect.right, midHeight)
            lineTo(midWidth, shapeRect.bottom)
            close()
            offset(shapeRect.left, shapeRect.top)
        }
        canvas.drawPath(path, drawPaint)
    }
}
