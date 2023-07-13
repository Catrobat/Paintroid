package org.catrobat.paintroid.tools.helper

import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import org.catrobat.paintroid.command.Command

const val VERTEX_WIDTH = 30.0f

class Vertex(vertexCenter: PointF?, outgoingPathCommand: Command?, ingoingPathCommand: Command?) {
    var vertexCenter: PointF? = vertexCenter
    var vertexShape: RectF? = null
    var outgoingPathCommand: Command? = outgoingPathCommand
    var ingoingPathCommand: Command? = ingoingPathCommand

    init {
        if (vertexCenter != null) {
            vertexShape = RectF(
                vertexCenter.x - VERTEX_WIDTH,
                vertexCenter.y - VERTEX_WIDTH,
                vertexCenter.x + VERTEX_WIDTH,
                vertexCenter.y + VERTEX_WIDTH
            )
        }
    }

    fun updateVertexCenter(newCenter: PointF) {
        vertexCenter = newCenter
        vertexShape = RectF(
            newCenter.x - VERTEX_WIDTH,
            newCenter.y - VERTEX_WIDTH,
            newCenter.x + VERTEX_WIDTH,
            newCenter.y + VERTEX_WIDTH
        )
    }

    fun setOutgoingPath(updatedOutgoingPath: Command?) {
        this.outgoingPathCommand = updatedOutgoingPath
    }

    fun setIngoingPath(updatedIngoingPath: Command?) {
        this.ingoingPathCommand = updatedIngoingPath
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
