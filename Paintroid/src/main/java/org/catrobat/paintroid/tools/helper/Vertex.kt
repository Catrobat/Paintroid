package org.catrobat.paintroid.tools.helper

import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import org.catrobat.paintroid.command.Command
import kotlin.math.pow
import kotlin.math.sqrt

class Vertex(vertexCenter: PointF?, outgoingPathCommand: Command?, ingoingPathCommand: Command?) {
    var vertexCenter: PointF? = vertexCenter
    var outgoingPathCommand: Command? = outgoingPathCommand
    var ingoingPathCommand: Command? = ingoingPathCommand

    fun updateVertexCenter(newCenter: PointF) {
        vertexCenter = newCenter
    }

    fun setOutgoingPath(updatedOutgoingPath: Command?) {
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
        private const val PAINT_STROKE_WIDTH = 2f
        const val VERTEX_RADIUS = 50F

        fun getPaint(): Paint {
            val paint = Paint()
            paint.run {
                style = Paint.Style.FILL
                color = Color.GRAY
                alpha = PAINT_ALPHA
                strokeWidth = PAINT_STROKE_WIDTH
            }
            return paint
        }
    }
}
