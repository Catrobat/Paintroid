/*
 * Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.paintroid.tools.implementation

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.view.MotionEvent
import androidx.test.espresso.idling.CountingIdlingResource
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.command.serialization.SerializablePath
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.Tool.StateChange
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.common.CommonBrushChangedListener
import org.catrobat.paintroid.tools.common.CommonBrushPreviewListener
import org.catrobat.paintroid.tools.common.MOVE_TOLERANCE
import org.catrobat.paintroid.tools.helper.AdvancedSettingsAlgorithms
import org.catrobat.paintroid.tools.helper.AdvancedSettingsAlgorithms.smoothing
import org.catrobat.paintroid.tools.helper.AdvancedSettingsAlgorithms.useEventSize
import org.catrobat.paintroid.tools.helper.AdvancedSettingsAlgorithms.threshold
import org.catrobat.paintroid.tools.options.BrushToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

open class BrushTool(
    val brushToolOptionsView: BrushToolOptionsView,
    contextCallback: ContextCallback,
    toolOptionsViewController: ToolOptionsViewController,
    toolPaint: ToolPaint,
    workspace: Workspace,
    idlingResource: CountingIdlingResource,
    commandManager: CommandManager,
    var useEventDependentStrokeWidth: Boolean,
    override var drawTime: Long
) : BaseTool(contextCallback, toolOptionsViewController, toolPaint, workspace, idlingResource, commandManager) {
    protected open val previewPaint: Paint
        get() = toolPaint.previewPaint

    protected open val bitmapPaint: Paint
        get() = toolPaint.paint

    override val toolType: ToolType
        get() = ToolType.BRUSH

    @JvmField
    var pathToDraw: SerializablePath = SerializablePath()
    var initialEventCoordinate: PointF? = null
    private var pathInsideBitmap = false
    private val drawToolMovedDistance = PointF(0f, 0f)

    private var initWidth = 0f
    private var bezierPoints = mutableListOf<PointF>()
    private var bezierPointsWidths = mutableListOf<Float>()

    private var allBezierPointsRight = mutableListOf<PointF>()
    private var allBezierPointsLeft = mutableListOf<PointF>()

    val pointArray = mutableListOf<PointF>()

    init {
        toolOptionsViewController.enable()
        pathToDraw.incReserve(1)
        brushToolOptionsView.setBrushChangedListener(CommonBrushChangedListener(this))
        brushToolOptionsView.setBrushPreviewListener(
            CommonBrushPreviewListener(
                toolPaint,
                toolType
            )
        )
        brushToolOptionsView.setCurrentPaint(toolPaint.paint)
    }

    override fun draw(canvas: Canvas) {
        canvas.run {
            save()
            clipRect(0, 0, workspace.width, workspace.height)
            if (useEventDependentStrokeWidth) {
                previewPaint.style = Paint.Style.FILL
                bitmapPaint.style = Paint.Style.FILL
                drawPath(getClosedPathFromPoints(), previewPaint)
            }
            else
                drawPath(pathToDraw, previewPaint)

            restore()
        }
    }

    override fun handleDown(coordinate: PointF?): Boolean {
        coordinate ?: return false

        if (useEventDependentStrokeWidth) {
            bezierPoints.add(coordinate)
            bezierPointsWidths.add(0f)
        } else {
            initialEventCoordinate = PointF(coordinate.x, coordinate.y)
            previousEventCoordinate = PointF(coordinate.x, coordinate.y)
            pathToDraw.moveTo(coordinate.x, coordinate.y)
            drawToolMovedDistance.set(0f, 0f)
            pointArray.add(PointF(coordinate.x, coordinate.y))
            pathInsideBitmap = workspace.contains(coordinate)
        }

        return true
    }

    override fun handleMove(coordinate: PointF?): Boolean {
        if (eventCoordinatesAreNull() || coordinate == null) {
            return false
        }
        previousEventCoordinate?.let {
            pathToDraw.quadTo(it.x, it.y, coordinate.x, coordinate.y)
            pathToDraw.incReserve(1)
            drawToolMovedDistance.set(
                drawToolMovedDistance.x + abs(coordinate.x - it.x),
                drawToolMovedDistance.y + abs(coordinate.y - it.y)
            )
            pointArray.add(PointF(coordinate.x, coordinate.y))
            it.set(coordinate.x, coordinate.y)
        }
        if (!pathInsideBitmap && workspace.contains(coordinate)) {
            pathInsideBitmap = true
        }
        return true
    }

    fun handleMoveEvent(canvasTouchPoint: PointF, event: MotionEvent): Boolean {
        val end = PointF(canvasTouchPoint.x, canvasTouchPoint.y)

        if (bezierPoints.isEmpty()) return true

        val shiftBy = getNextStrokeWidth(event)

        if (bezierPoints.size < 4) {
            bezierPoints.add(end)
            bezierPointsWidths.add(shiftBy)
            return true
        }

        val dir = getDirectionalVector(bezierPoints[0], bezierPoints[3])
        val orthogonal = getNormalizedOrthogonalVector(dir)

        for (i in bezierPoints.indices) {
            if (i == 0) continue
            val shifted1 = getPointShiftedByDistanceRight(bezierPoints[i], orthogonal, bezierPointsWidths[i])
            allBezierPointsRight.add(shifted1)

            val shifted2 = getPointShiftedByDistanceLeft(bezierPoints[i], orthogonal, bezierPointsWidths[i])
            allBezierPointsLeft.add(shifted2)
        }

        val bezierPointsTemp = bezierPoints[3]
        val bezierWidthTemp = bezierPointsWidths[3]
        bezierPoints.clear()
        bezierPointsWidths.clear()
        bezierPoints.add(bezierPointsTemp)
        bezierPointsWidths.add(bezierWidthTemp)

        return true
    }

    override fun handleUp(coordinate: PointF?): Boolean {
        if (useEventDependentStrokeWidth) {
            if (coordinate == null) return false
            if (allBezierPointsLeft.size < 3) return false

            bezierPoints.clear()
            bezierPointsWidths.clear()

            val path = getClosedPathFromPoints()

            allBezierPointsRight.clear()
            allBezierPointsLeft.clear()

            bitmapPaint.style = Paint.Style.FILL
            val command = commandFactory.createPathCommand(bitmapPaint, path)
            commandManager.addCommand(command)

            return true
        } else {
            if (eventCoordinatesAreNull() || coordinate == null) {
                return false
            }

            if (!pathInsideBitmap && workspace.contains(coordinate)) {
                pathInsideBitmap = true
            }

            previousEventCoordinate?.let {
                drawToolMovedDistance.set(
                    drawToolMovedDistance.x + abs(coordinate.x - it.x),
                    drawToolMovedDistance.y + abs(coordinate.y - it.y)
                )
            }

            return if (MOVE_TOLERANCE < max(drawToolMovedDistance.x, drawToolMovedDistance.y)) {
                addPathCommand(coordinate)
            } else {
                initialEventCoordinate?.let {
                    return addPointCommand(it)
                }
                false
            }
        }
    }

    override fun resetInternalState() {
        pathToDraw.rewind()
        pointArray.clear()
        initialEventCoordinate = null
        previousEventCoordinate = null
    }

    override fun changePaintColor(color: Int) {
        super.changePaintColor(color)
        brushToolOptionsView.invalidate()
    }

    private fun eventCoordinatesAreNull(): Boolean =
        initialEventCoordinate == null || previousEventCoordinate == null

    private fun addPathCommand(coordinate: PointF): Boolean {
        pathToDraw.lineTo(coordinate.x, coordinate.y)

        if (!pathInsideBitmap) {
            resetInternalState(StateChange.RESET_INTERNAL_STATE)
            return false
        }

        var distance: Double? = null
        initialEventCoordinate?.apply {
            distance =
                sqrt(((coordinate.x - x) * (coordinate.x - x) + (coordinate.y - y) * (coordinate.y - y)).toDouble())
        }
        val speed = distance?.div(drawTime)

        if (!smoothing || speed != null && speed < threshold) {
            val command = commandFactory.createPathCommand(bitmapPaint, pathToDraw)
            commandManager.addCommand(command)
        } else {
            val pathNew = AdvancedSettingsAlgorithms.smoothingAlgorithm(pointArray)
            val command = commandFactory.createPathCommand(bitmapPaint, pathNew)
            commandManager.addCommand(command)
        }

        pointArray.clear()
        return true
    }

    private fun addPointCommand(coordinate: PointF): Boolean {
        if (!pathInsideBitmap) {
            resetInternalState(StateChange.RESET_INTERNAL_STATE)
            return false
        }

        pointArray.clear()
        val command = commandFactory.createPointCommand(bitmapPaint, coordinate)
        commandManager.addCommand(command)
        return true
    }

    private fun getDirectionalVector(A: PointF, B: PointF): PointF {
        return PointF(A.x - B.x, A.y - B.y)
    }

    private fun getNormalizedOrthogonalVector(vector: PointF): PointF {
        val orth = PointF(vector.y, -vector.x)
        val length = sqrt(orth.x * orth.x + orth.y * orth.y)
        return PointF(orth.x/length, orth.y/length)
    }

    private fun getPointShiftedByDistanceRight(point: PointF, orth: PointF, shiftBy: Float): PointF {
        return PointF(point.x + shiftBy*orth.x, point.y + shiftBy*orth.y)
    }

    private fun getPointShiftedByDistanceLeft(point: PointF, orth: PointF, shiftBy: Float): PointF {
        return PointF(point.x - shiftBy*orth.x, point.y - shiftBy*orth.y)
    }

    private fun getClosedPathFromPoints() : SerializablePath
    {
        val path = SerializablePath()

        if (allBezierPointsLeft.size < 4) return path

        path.incReserve(allBezierPointsLeft.size * 2)

        path.moveTo(allBezierPointsRight[0].x, allBezierPointsRight[0].y)
        var i = 0
        while (i < allBezierPointsRight.count() - 3) {
            path.cubicTo(allBezierPointsRight[i+1].x, allBezierPointsRight[i+1].y,
                         allBezierPointsRight[i+2].x, allBezierPointsRight[i+2].y,
                         allBezierPointsRight[i+3].x, allBezierPointsRight[i+3].y)
            i += 3
        }

        i = allBezierPointsLeft.size - 1

        while (i > 3) {
            path.cubicTo(allBezierPointsLeft[i-1].x, allBezierPointsLeft[i-1].y,
                         allBezierPointsLeft[i-2].x, allBezierPointsLeft[i-2].y,
                         allBezierPointsLeft[i-3].x, allBezierPointsLeft[i-3].y)
            i -= 3
        }
        path.close()

        return path
    }

    private fun getNextStrokeWidth(event : MotionEvent) : Float {
        val newWidth = if (useEventSize) {
            event.size * 80 * bitmapPaint.strokeWidth / 100
        } else {
            event.pressure * 80 * bitmapPaint.strokeWidth / 100
        }
        initWidth = newWidth

        return newWidth
    }
}
