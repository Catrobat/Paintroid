package org.catrobat.paintroid.tools.drawable

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF

private const val CONSTANT_1 = 2f
private const val CONSTANT_2 = 1.7f

class MoonDrawable : ShapeDrawable {
    private val path = Path()
    override fun draw(canvas: Canvas, shapeRect: RectF, drawPaint: Paint) {
        val midWidth = shapeRect.width() / 2
        val midHeight = shapeRect.height() / 2
        val height = shapeRect.height() / CONSTANT_2
        val width = shapeRect.width() / CONSTANT_1
        path.run {
            reset()

            val leftArcRect = RectF(midWidth - width, midHeight - height, midWidth + width, midHeight + height)
            arcTo(leftArcRect, 90f, 180f)


            val rightArcRect = RectF(midWidth, midHeight - height, midWidth + width * 2, midHeight + height)
            arcTo(rightArcRect, -90f, 180f)

            close()
            offset(shapeRect.left, shapeRect.top)
        }
        canvas.drawPath(path, drawPaint)
    }
}
