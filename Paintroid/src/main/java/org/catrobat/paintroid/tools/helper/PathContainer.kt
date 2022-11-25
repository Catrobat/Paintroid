package org.catrobat.paintroid.tools.helper

import android.graphics.PointF
import org.catrobat.paintroid.command.serialization.SerializablePath
import kotlin.math.sqrt

class PathContainer {


    private var allBezierPointsRight = mutableListOf<PointF>()
    private var allBezierPointsLeft = mutableListOf<PointF>()

    private var neededBezierPoints = 4
    private var neededPointsLeft = 3
    private var bezierPoints = mutableListOf<PointF>()
    private var bezierPointsWidths = mutableListOf<Float>()

    fun getClosedPathFromPoints(): SerializablePath {
        val path = SerializablePath()

        if (allBezierPointsLeft.size < neededBezierPoints) return path

        path.incReserve(allBezierPointsLeft.size * 2)

        path.moveTo(allBezierPointsRight[0].x, allBezierPointsRight[0].y)
        var i = 0
        while (i < allBezierPointsRight.count() - neededPointsLeft) {
            val startingPointIndex: Int = i + 1
            val middlePointIndex: Int = i + 2
            val endPointIndex: Int = i + 3
            path.cubicTo(allBezierPointsRight[startingPointIndex].x, allBezierPointsRight[startingPointIndex].y,
                         allBezierPointsRight[middlePointIndex].x, allBezierPointsRight[middlePointIndex].y,
                         allBezierPointsRight[endPointIndex].x, allBezierPointsRight[endPointIndex].y)
            i += 3
        }

        i = allBezierPointsLeft.size - 1

        while (i > 3) {
            val startingPointIndex: Int = i - 1
            val middlePointIndex: Int = i - 2
            val endPointIndex: Int = i - 3
            path.cubicTo(allBezierPointsLeft[startingPointIndex].x, allBezierPointsLeft[startingPointIndex].y,
                         allBezierPointsLeft[middlePointIndex].x, allBezierPointsLeft[middlePointIndex].y,
                         allBezierPointsLeft[endPointIndex].x, allBezierPointsLeft[endPointIndex].y)
            i -= 3
        }
        path.close()

        return path
    }

    fun addStartPoint(coordinate: PointF) {
        addNewPoint(coordinate, 0f)
    }

    fun addNewPoint(canvasPoint: PointF, shiftBy: Float) {
        if (bezierPoints.size < neededBezierPoints) {
            bezierPoints.add(canvasPoint)
            bezierPointsWidths.add(shiftBy)
            return
        }

        val dir = getDirectionalVector(bezierPoints.first(), bezierPoints.last())
        val orthogonal = getNormalizedOrthogonalVector(dir)

        for (i in bezierPoints.indices) {
            if (i == 0) continue
            val shifted1 = getPointShiftedByDistanceRight(bezierPoints[i], orthogonal, bezierPointsWidths[i])
            allBezierPointsRight.add(shifted1)

            val shifted2 = getPointShiftedByDistanceLeft(bezierPoints[i], orthogonal, bezierPointsWidths[i])
            allBezierPointsLeft.add(shifted2)
        }

        val bezierPointsTemp = bezierPoints.last()
        val bezierWidthTemp = bezierPointsWidths.last()
        bezierPoints.clear()
        bezierPointsWidths.clear()
        bezierPoints.add(bezierPointsTemp)
        bezierPointsWidths.add(bezierWidthTemp)
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