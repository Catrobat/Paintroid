package org.catrobat.paintroid.tools.drawable

import android.graphics.Canvas
import android.graphics.RectF
import io.reactivex.plugins.RxJavaPlugins.reset


import android.graphics.Paint
import android.graphics.Path

private const val CONSTANT_1 = 2f
private const val CONSTANT_2 = 1.7f

class OctagonDrawable : ShapeDrawable {
    private val path = Path()
    override fun draw(canvas: Canvas, shapeRect: RectF, drawPaint: Paint) {
        val midWidth = shapeRect.width() / 2
        val midHeight = shapeRect.height() / 2
        val width = shapeRect.width() / CONSTANT_1

        path.run {
            reset()
            moveTo(midWidth - width, shapeRect.top)
            lineTo(midWidth + width, shapeRect.top)
            lineTo(shapeRect.right, midHeight - width)
            lineTo(shapeRect.right, midHeight + width)
            lineTo(midWidth + width, shapeRect.bottom)
            lineTo(midWidth - width, shapeRect.bottom)
            lineTo(shapeRect.left, midHeight + width)
            lineTo(shapeRect.left, midHeight - width)
            lineTo(midWidth - width, shapeRect.top)
            close()
            offset(shapeRect.left, shapeRect.top)
        }
        canvas.drawPath(path, drawPaint)
    }
}

