package org.catrobat.paintroid.tools.helper

import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import org.catrobat.paintroid.command.implementation.DynamicPathCommand
import kotlin.math.pow
import kotlin.math.sqrt

class Vertex(vertexCenter: PointF?, outgoingPathCommand: DynamicPathCommand?, ingoingPathCommand: DynamicPathCommand?) {
    var vertexCenter: PointF? = vertexCenter
    var outgoingPathCommand: DynamicPathCommand? = outgoingPathCommand
    var ingoingPathCommand: DynamicPathCommand? = ingoingPathCommand

    fun updateVertexCenter(newCenter: PointF) {
        vertexCenter = newCenter
    }

    fun setOutgoingPath(updatedOutgoingPath: DynamicPathCommand?) {
        this.outgoingPathCommand = updatedOutgoingPath
    }

    fun wasClicked(clickedCoordinate: PointF?): Boolean {
        vertexCenter?.let { vc ->
            clickedCoordinate?.let { cc ->
                val distanceFromCenter = calculateDistance(vc.x, vc.y, cc.x, cc.y)
                if (distanceFromCenter <= VERTEX_RADIUS) return true
            }
        }
        return false
    }

    private fun calculateDistance(x0: Float, y0: Float, x1: Float, y1: Float): Double {
        return sqrt((x1.toDouble() - x0.toDouble()).pow(2) +
                       (y1.toDouble() - y0.toDouble()).pow(2))
    }

    companion object {
        private const val PAINT_ALPHA = 128
        private const val VERTEX_PAINT_STROKE_WIDTH = 2f
        private const val EDGE_PAINT_STROKE_WIDTH = 16f
        const val VERTEX_RADIUS = 30f

        fun getVertexPaint(): Paint {
            val paint = Paint()
            paint.run {
                style = Paint.Style.FILL
                color = Color.GRAY
                alpha = PAINT_ALPHA
                strokeWidth = VERTEX_PAINT_STROKE_WIDTH
            }
            return paint
        }
        fun getEdgePaint(newColor: Int): Paint {
            val paint = Paint()
            paint.run {
                style = Paint.Style.FILL
                color = newColor
                alpha = PAINT_ALPHA
                strokeWidth = EDGE_PAINT_STROKE_WIDTH
            }
            return paint
        }
    }
}
