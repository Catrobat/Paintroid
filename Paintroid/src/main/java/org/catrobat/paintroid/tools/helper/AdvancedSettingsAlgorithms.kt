package org.catrobat.paintroid.tools.helper

import android.graphics.PointF
import org.catrobat.paintroid.command.serialization.SerializablePath

object AdvancedSettingsAlgorithms {
    @kotlin.jvm.JvmField
    var smoothing = true

    var useEventSize = false

    const val threshold = 0.2
    const val divider = 3

    @JvmStatic
    fun smoothingAlgorithm(pointArray: List<PointF>): SerializablePath {

        val diffPointArray = mutableListOf<PointF>()
        if (pointArray.size > 1) {
            for (i in pointArray.indices) {
                if (i >= 0) {
                    val point: PointF = pointArray[i]
                    when (i) {
                        0 -> {
                            val next: PointF = pointArray[i + 1]
                            val differenceX = next.x - point.x
                            val differenceY = next.y - point.y
                            val newPoint = PointF(differenceX / divider, differenceY / divider)
                            diffPointArray.add(newPoint)
                        }
                        pointArray.size - 1 -> {
                            val prev: PointF = pointArray[i - 1]
                            val differenceX = point.x - prev.x
                            val differenceY = point.y - prev.y
                            val newPoint = PointF(differenceX / divider, differenceY / divider)
                            diffPointArray.add(newPoint)
                        }
                        else -> {
                            val next: PointF = pointArray[i + 1]
                            val prev: PointF = pointArray[i - 1]
                            val differenceX = next.x - prev.x
                            val differenceY = next.y - prev.y
                            val newPoint = PointF(differenceX / divider, differenceY / divider)
                            diffPointArray.add(newPoint)
                        }
                    }
                }
            }
        }

        val trueList = mutableListOf<PointF>()
        trueList.add(PointF(pointArray[0].x, pointArray[0].y))
        for (i in 1 until pointArray.size) {

            val point: PointF = pointArray[i]
            val diff: PointF = diffPointArray[i]

            val erg1 = point.x + diff.x
            val erg2 = point.y + diff.y

            trueList.add(PointF(erg1, erg2))
        }

        val pathNew = SerializablePath()
        pathNew.incReserve(1)
        pathNew.moveTo(trueList[0].x, trueList[0].y)

        for (i in 1 until trueList.size - 1 step 2) {
            val point: PointF = trueList[i]

            val prev: PointF = trueList[i - 1]
            val next: PointF = trueList[i + 1]

            pathNew.cubicTo(prev.x, prev.y, point.x, point.y, next.x, next.y)
            pathNew.incReserve(1)
        }

        trueList.clear()
        diffPointArray.clear()

        return pathNew
    }
}
