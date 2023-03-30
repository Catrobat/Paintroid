package org.catrobat.paintroid.tools.drawable

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF

private const val CONSTANT_1 = 0.95f
private const val CONSTANT_2 = 0.31f
private const val CONSTANT_3 = 0.59f

class PentagonDrawable: ShapeDrawable {
    private val path = Path()

    override fun draw(canvas: Canvas, shapeRect: RectF, drawPaint: Paint) {
        val midWidth = shapeRect.width() / 2
        val midHeight = shapeRect.height() / 2
        val height = shapeRect.height()
        val width = shapeRect.width()
        path.run {
            reset()
            moveTo(midWidth, 0f)
            lineTo(midWidth + CONSTANT_1 * width / 2, CONSTANT_2 * height)
            lineTo(midWidth + CONSTANT_3 * width / 2, height)
            lineTo(midWidth - CONSTANT_3 * width / 2, height)
            lineTo(midWidth - CONSTANT_1 * width / 2, CONSTANT_2 * height)
            lineTo(midWidth, 0f)
            close()
            offset(shapeRect.left, shapeRect.top)
        }
        canvas.drawPath(path, drawPaint)
    }
}