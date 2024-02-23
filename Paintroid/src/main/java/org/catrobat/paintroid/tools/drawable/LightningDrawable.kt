package org.catrobat.paintroid.tools.drawable
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF

private const val CONSTANT_1 = 2f
private const val CONSTANT_2 = 1.7f
class LightningDrawable:ShapeDrawable {
    private val path = Path()
    override fun draw(canvas: Canvas, shapeRect: RectF, drawPaint: Paint) {
        val midWidth = shapeRect.width() / 2
        val midHeight = shapeRect.height() / 2
        val height = shapeRect.height() / CONSTANT_2
        val width = shapeRect.width() / CONSTANT_1
        path.run {
            reset()

            moveTo(midWidth - width, midHeight - height)

            lineTo(midWidth, midHeight + height)
            lineTo(midWidth + width, midHeight - height)
            lineTo(midWidth + width / 2, midHeight - height)
            lineTo(midWidth + width / 2, midHeight + height)
            lineTo(midWidth + width * 2, midHeight - height)

            lineTo(midWidth + width * 2, midHeight + height)
            close()
            offset(shapeRect.left, shapeRect.top)
        }
        canvas.drawPath(path, drawPaint)
    }
}