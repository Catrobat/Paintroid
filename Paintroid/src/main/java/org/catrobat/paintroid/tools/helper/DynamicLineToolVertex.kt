package org.catrobat.paintroid.tools.helper

import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import org.catrobat.paintroid.command.Command

const val VERTEX_WIDTH = 30.0f

class DynamicLineToolVertex(vertexCenter: PointF?, outgoingPathCommand: Command?, ingoingPathCommand: Command?) {
    var vertexCenter: PointF? = vertexCenter
    var vertex: RectF? = null
    var outgoingPathCommand: Command? = outgoingPathCommand
    var ingoingPathCommand: Command? = ingoingPathCommand

    init {
        if (vertexCenter != null) {
            vertex = RectF(
                vertexCenter.x - VERTEX_WIDTH,
                vertexCenter.y - VERTEX_WIDTH,
                vertexCenter.x + VERTEX_WIDTH,
                vertexCenter.y + VERTEX_WIDTH
            )
        }
    }

    fun updateVertex(newCenter: PointF) {
        vertexCenter = newCenter
        vertex = RectF(
            newCenter.x - VERTEX_WIDTH,
            newCenter.y - VERTEX_WIDTH,
            newCenter.x + VERTEX_WIDTH,
            newCenter.y + VERTEX_WIDTH
        )
    }

    companion object {
        private const val RECT_PAINT_ALPHA = 128
        private const val RECT_PAINT_STROKE_WIDTH = 2f

        fun getPaint(): Paint {
            val paint = Paint()
            paint.run {
                style = Paint.Style.FILL
                color = Color.GRAY
                alpha = RECT_PAINT_ALPHA
                strokeWidth = RECT_PAINT_STROKE_WIDTH
            }
            return paint
        }

        fun isInsideVertex(clickedCoordinate: PointF, rectF: RectF): Boolean =
            clickedCoordinate.x < rectF.right &&
                rectF.left < clickedCoordinate.x &&
                clickedCoordinate.y < rectF.bottom &&
                rectF.top < clickedCoordinate.y
    }
}
