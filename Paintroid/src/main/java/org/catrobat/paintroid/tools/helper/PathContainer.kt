package org.catrobat.paintroid.tools.helper

import android.graphics.PointF
import org.catrobat.paintroid.command.serialization.SerializablePath
import kotlin.math.sqrt

data class Cubic(val p1: PointF, val p2: PointF, val p3: PointF)

class PathContainer {
    private var cubicsRight = mutableListOf<Cubic>()
    private var cubicsLeft  = mutableListOf<Cubic>()

    private val neededBezierPoints = 3
    private var bezierPoints = mutableListOf<PointF>()
    private var bezierPointsWidths = mutableListOf<Float>()

    private var lastEndPoint = PointF(0f,0f)

    private var lastPath: SerializablePath = SerializablePath()
    fun getClosedPathFromPoints(): SerializablePath {
        val path = SerializablePath()

        if (cubicsLeft.size < neededBezierPoints) return path

        path.incReserve(cubicsLeft.size * 6)

        path.moveTo(cubicsLeft[0].p1.x, cubicsLeft[0].p1.y)
        try {
            cubicsRight.forEach { cubic ->
                path.cubicTo(cubic.p1.x, cubic.p1.y, cubic.p2.x, cubic.p2.y, cubic.p3.x, cubic.p3.y)
            }
            path.lineTo(cubicsLeft.last().p3.x, cubicsLeft.last().p3.y)
            val reversed = cubicsLeft.reversed()
            reversed.forEach { cubic ->
                path.cubicTo(cubic.p3.x, cubic.p3.y, cubic.p2.x, cubic.p2.y, cubic.p1.x, cubic.p1.y)
            }
        } catch(e: Exception) {
            return lastPath
        }

        path.close()
        lastPath = path
        return path
    }

    fun addStartPoint(coordinate: PointF) {
        addNewPoint(coordinate, 0f)
        lastEndPoint = coordinate
    }

    fun addNewPoint(canvasPoint: PointF, shiftBy: Float) {
        if (bezierPoints.size < neededBezierPoints) {
            bezierPoints.add(canvasPoint)
            bezierPointsWidths.add(shiftBy)
            return
        }

        val currentPointsRight = mutableListOf<PointF>()
        val currentPointsLeft = mutableListOf<PointF>()

        val orthogonal = getNormalizedOrthogonalVector(getDirectionalVector(lastEndPoint, bezierPoints.last()))
        for (i in bezierPoints.indices) {
            currentPointsRight.add(getPointShiftedByDistanceRight(bezierPoints[i], orthogonal, bezierPointsWidths[i]))
            currentPointsLeft.add(getPointShiftedByDistanceLeft(bezierPoints[i], orthogonal, bezierPointsWidths[i]))
        }

        cubicsRight.add(Cubic(currentPointsRight[0], currentPointsRight[1], currentPointsRight[2]))
        cubicsLeft.add(Cubic(currentPointsLeft[0], currentPointsLeft[1], currentPointsLeft[2]))

        lastEndPoint = bezierPoints.last()
        bezierPoints.clear()
        bezierPointsWidths.clear()
    }

    fun addEndPoint(coordinate: PointF) {
        addNewPoint(coordinate, 0f)
    }

    private fun getDirectionalVector(vecA: PointF, vecB: PointF): PointF = PointF(vecA.x - vecB.x, vecA.y - vecB.y)

    private fun getNormalizedOrthogonalVector(vector: PointF): PointF {
        val orth = PointF(vector.y, -vector.x)
        val length = sqrt(orth.x * orth.x + orth.y * orth.y)
        return PointF(orth.x / length, orth.y / length)
    }

    private fun getPointShiftedByDistanceRight(point: PointF, orth: PointF, shiftBy: Float): PointF = PointF(point.x + shiftBy * orth.x, point.y + shiftBy * orth.y)

    private fun getPointShiftedByDistanceLeft(point: PointF, orth: PointF, shiftBy: Float): PointF = PointF(point.x - shiftBy * orth.x, point.y - shiftBy * orth.y)
}
