package org.catrobat.paintroid.tools.drawable


import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF

private const val CONSTANT_1 = 2f
private const val CONSTANT_2 = 1.7f
class SpeechDrawable : ShapeDrawable {
    private val path = Path()
    override fun draw(canvas: Canvas, shapeRect: RectF, drawPaint: Paint) {
        val cornerRadius = shapeRect.height() / CONSTANT_1
        val bubbleHeight = shapeRect.height() / CONSTANT_2
        val bubbleWidth = shapeRect.width() * CONSTANT_1 / CONSTANT_2
        val bubbleTailWidth = shapeRect.width() / CONSTANT_1

        path.run {
            reset()
            // Draw bubble body
            moveTo(shapeRect.left + cornerRadius, shapeRect.top)
            lineTo(shapeRect.right - cornerRadius, shapeRect.top)
            arcTo(RectF(shapeRect.right - 2 * cornerRadius, shapeRect.top, shapeRect.right, shapeRect.top + 2 * cornerRadius), 270f, 90f)
            lineTo(shapeRect.right, shapeRect.bottom - cornerRadius)
            arcTo(RectF(shapeRect.right - 2 * cornerRadius, shapeRect.bottom - 2 * cornerRadius, shapeRect.right, shapeRect.bottom), 0f, 90f)
            lineTo(shapeRect.left + bubbleWidth / 2 + bubbleTailWidth, shapeRect.bottom)
            lineTo(shapeRect.left + bubbleWidth / 2, shapeRect.bottom + bubbleHeight)
            lineTo(shapeRect.left + bubbleWidth / 2 - bubbleTailWidth, shapeRect.bottom)
            lineTo(shapeRect.left + cornerRadius, shapeRect.bottom)
            arcTo(RectF(shapeRect.left, shapeRect.bottom - 2 * cornerRadius, shapeRect.left + 2 * cornerRadius, shapeRect.bottom), 90f, 90f)
            lineTo(shapeRect.left, shapeRect.top + cornerRadius)
            arcTo(RectF(shapeRect.left, shapeRect.top, shapeRect.left + 2 * cornerRadius, shapeRect.top + 2 * cornerRadius), 180f, 90f)
            close()
        }
        canvas.drawPath(path, drawPaint)
    }
}

