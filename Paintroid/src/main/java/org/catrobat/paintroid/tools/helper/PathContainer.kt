package org.catrobat.paintroid.tools.helper

import android.graphics.PointF
import org.catrobat.paintroid.command.serialization.SerializablePath
import kotlin.math.sqrt

data class Move(val p: PointF)

class PathContainer {
    private var moveRight = mutableListOf<Move>()
    private var moveLeft = mutableListOf<Move>()
    private var points = mutableListOf<PointF>()

    private var lastEndPoint = PointF(0f, 0f)

    private var endPoint = PointF(0f, 0f)

    private var lastPath: SerializablePath = SerializablePath()
    fun getClosedPathFromPoints(): SerializablePath {
        val path = SerializablePath()

        if (moveLeft.isEmpty()) return path

        path.moveTo(moveRight[0].p.x, moveRight[0].p.y)
        @Suppress("SwallowedException")
        try {
            moveRight.forEach { move -> path.lineTo(move.p.x, move.p.y) }

            if (!(endPoint.x == 0f && endPoint.y == 0f)) {
                path.lineTo(endPoint.x, endPoint.y)
            }
            val reversed = moveLeft.reversed()

            reversed.forEach { move -> path.lineTo(move.p.x, move.p.y) }
        } catch (ex: ConcurrentModificationException) {
            return lastPath
        }

        path.close()
        lastPath = path
        return path
    }

    private fun smooth() {
        moveRight[0] = moveLeft.first()
        moveRight[moveRight.size - 1] = moveLeft.last()

        moveRight.forEachIndexed { index, move ->
            if (moveRight.size < index + 2 || move == moveRight.last())
                return

            if (index == 0)
                return@forEachIndexed

            val beforeIndex = index - 1
            val afterIndex = index + 1

            val beforeRight = moveRight[beforeIndex]
            val afterRight = moveRight[afterIndex]
            move.p.x = (beforeRight.p.x + afterRight.p.x) / 2
            move.p.y = (beforeRight.p.y + afterRight.p.y) / 2

            val beforeLeft = moveLeft[beforeIndex]
            val afterLeft = moveLeft[afterIndex]
            moveLeft[index].p.x = (beforeLeft.p.x + afterLeft.p.x) / 2
            moveLeft[index].p.y = (beforeLeft.p.y + afterLeft.p.y) / 2
        }
    }

    fun addStartPoint(coordinate: PointF) {
        points = mutableListOf()
        points.add(PointF(coordinate.x, coordinate.y))
        moveRight = mutableListOf()
        moveLeft = mutableListOf()
        addNewPoint(coordinate, 0f)
        lastEndPoint = coordinate
    }

    fun addNewPoint(canvasPoint: PointF, shiftBy: Float) {
        if (points.isNotEmpty() && points.last() == canvasPoint) return

        val orthogonalRight = getNormalizedOrthogonalVector(getDirectionalVector(points.last(), canvasPoint))
        val orthogonalLeft = getNormalizedOrthogonalVector(getDirectionalVector(points.last(), canvasPoint))

        moveRight.add(Move(getPointShiftedByDistanceRight(canvasPoint, orthogonalRight, shiftBy)))
        moveLeft.add(Move(getPointShiftedByDistanceLeft(canvasPoint, orthogonalLeft, shiftBy)))

        points.add(PointF(canvasPoint.x, canvasPoint.y))
    }

    fun addEndPoint(coordinate: PointF) {
        endPoint = PointF(coordinate.x, coordinate.y)
        smooth()
    }

    private fun getDirectionalVector(vecA: PointF, vecB: PointF): PointF = PointF(vecA.x - vecB.x, vecA.y - vecB.y)

    private fun getNormalizedOrthogonalVector(vector: PointF): PointF {
        val orth = PointF(vector.y, -vector.x)
        val length = sqrt(orth.x * orth.x + orth.y * orth.y)
        return if(length == 0f) PointF(0.0f, 0.0f) else PointF(orth.x / length, orth.y / length)
    }

    private fun getPointShiftedByDistanceRight(point: PointF, orth: PointF, shiftBy: Float): PointF = PointF(point.x + shiftBy * orth.x, point.y + shiftBy * orth.y)

    private fun getPointShiftedByDistanceLeft(point: PointF, orth: PointF, shiftBy: Float): PointF = PointF(point.x - shiftBy * orth.x, point.y - shiftBy * orth.y)
}
