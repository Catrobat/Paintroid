package org.catrobat.paintroid.tools.drawable

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
class TriangleDrawable :ShapeDrawable{
    private val path = Path()

    override fun draw(canvas: Canvas, shapeRect: RectF, drawPaint: Paint) {
        path.run {
            reset()
            moveTo(shapeRect.left, shapeRect.bottom)
            lineTo(shapeRect.right, shapeRect.bottom)
            lineTo(shapeRect.centerX(), shapeRect.top)
            lineTo(shapeRect.left, shapeRect.bottom)
            close()
        }
        canvas.drawPath(path, drawPaint)
    }

}