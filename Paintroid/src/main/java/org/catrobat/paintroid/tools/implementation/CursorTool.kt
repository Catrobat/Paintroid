/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
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
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Cap
import android.graphics.PointF
import android.graphics.RectF
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.test.espresso.idling.CountingIdlingResource
import org.catrobat.paintroid.R
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
import org.catrobat.paintroid.tools.helper.AdvancedSettingsAlgorithms.smoothing
import org.catrobat.paintroid.tools.helper.AdvancedSettingsAlgorithms.smoothingAlgorithm
import org.catrobat.paintroid.tools.helper.AdvancedSettingsAlgorithms.threshold
import org.catrobat.paintroid.tools.options.BrushToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

private const val DEFAULT_TOOL_STROKE_WIDTH = 5f
private const val MINIMAL_TOOL_STROKE_WIDTH = 1f
private const val MAXIMAL_TOOL_STROKE_WIDTH = 10f
private const val CURSOR_LINES = 4

open class CursorTool(
    private val brushToolOptionsView: BrushToolOptionsView,
    contextCallback: ContextCallback,
    toolOptionsViewController: ToolOptionsViewController,
    toolPaint: ToolPaint,
    workspace: Workspace,
    idlingResource: CountingIdlingResource,
    commandManager: CommandManager,
    override var drawTime: Long
) : BaseToolWithShape(
    contextCallback,
    toolOptionsViewController,
    toolPaint,
    workspace,
    idlingResource,
    commandManager
) {
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    @JvmField
    var pathToDraw: SerializablePath = SerializablePath()

    @VisibleForTesting
    @JvmField
    var cursorToolSecondaryShapeColor: Int

    @VisibleForTesting
    @JvmField
    var toolInDrawMode = false

    private var pointInsideBitmap: Boolean
    private val cursorToolPrimaryShapeColor: Int

    private val initialEventCoordinate = PointF(0f, 0f)
    private val pointArray = mutableListOf<PointF>()

    override val toolType: ToolType
        get() = ToolType.CURSOR

    init {
        pathToDraw.incReserve(1)
        cursorToolPrimaryShapeColor =
            contextCallback.getColor(R.color.pocketpaint_main_cursor_tool_inactive_primary_color)
        cursorToolSecondaryShapeColor = Color.LTGRAY
        pointInsideBitmap = false
        brushToolOptionsView.run {
            setBrushChangedListener(CommonBrushChangedListener(this@CursorTool))
            setBrushPreviewListener(CommonBrushPreviewListener(toolPaint, toolType))
            setCurrentPaint(toolPaint.paint)
        }
        brushToolOptionsView.setStrokeCapButtonChecked(toolPaint.strokeCap)
    }

    override fun changePaintColor(color: Int) {
        super.changePaintColor(color)
        if (toolInDrawMode) {
            cursorToolSecondaryShapeColor = toolPaint.color
        }
        brushToolOptionsView.invalidate()
    }

    private fun hideToolOptions() {
        if (toolOptionsViewController.isVisible) {
            if (brushToolOptionsView.getTopToolOptions().visibility == View.VISIBLE) {
                toolOptionsViewController.slideUp(
                    brushToolOptionsView.getTopToolOptions(),
                    willHide = true,
                    showOptionsView = false
                )
            }

            if (brushToolOptionsView.getBottomToolOptions().visibility == View.VISIBLE) {
                toolOptionsViewController.slideDown(
                    brushToolOptionsView.getBottomToolOptions(),
                    willHide = true,
                    showOptionsView = false
                )
            }
        }
    }

    private fun showToolOptions() {
        if (!toolOptionsViewController.isVisible) {
            if (brushToolOptionsView.getBottomToolOptions().visibility == View.INVISIBLE) {
                toolOptionsViewController.slideDown(
                    brushToolOptionsView.getTopToolOptions(),
                    willHide = false,
                    showOptionsView = true
                )
            }

            if (brushToolOptionsView.getBottomToolOptions().visibility == View.INVISIBLE) {
                toolOptionsViewController.slideUp(
                    brushToolOptionsView.getBottomToolOptions(),
                    willHide = false,
                    showOptionsView = true
                )
            }
        }
    }

    override fun handleDown(coordinate: PointF?): Boolean {
        super.handleDown(coordinate)
        pathToDraw.moveTo(toolPosition.x, toolPosition.y)
        coordinate?.let {
            previousEventCoordinate?.set(it)
            initialEventCoordinate.set(coordinate)
        }
        pointArray.add(PointF(toolPosition.x, toolPosition.y))
        movedDistance.set(0f, 0f)
        pointInsideBitmap = workspace.contains(toolPosition)
        return true
    }

    override fun handleMove(coordinate: PointF?): Boolean {
        if (coordinate != null) {
            hideToolOptions()
            super.handleMove(coordinate)
            previousEventCoordinate?.let {
                val deltaX = coordinate.x - it.x
                val deltaY = coordinate.y - it.y
                it.set(coordinate.x, coordinate.y)
                pointInsideBitmap = pointInsideBitmap || workspace.contains(toolPosition)
                val newToolPosition = calculateNewClampedToolPosition(deltaX, deltaY)
                if (toolInDrawMode) {
                    val dx = (toolPosition.x + newToolPosition.x) / 2f
                    val dy = (toolPosition.y + newToolPosition.y) / 2f
                    pathToDraw.quadTo(toolPosition.x, toolPosition.y, dx, dy)
                    pathToDraw.incReserve(1)
                }
                pointArray.add(PointF(toolPosition.x, toolPosition.y))
                toolPosition.set(newToolPosition)
                movedDistance.offset(abs(deltaX), abs(deltaY))
            }
        }
        return true
    }

    private fun calculateNewClampedToolPosition(deltaX: Float, deltaY: Float): PointF {
        val newToolPosition = PointF(toolPosition.x + deltaX, toolPosition.y + deltaY)
        val toolSurfacePosition = workspace.getSurfacePointFromCanvasPoint(newToolPosition)
        val surfaceWidth = workspace.surfaceWidth
        val surfaceHeight = workspace.surfaceHeight
        val positionOutsideBounds = !contains(toolSurfacePosition, surfaceWidth, surfaceHeight)
        if (positionOutsideBounds) {
            toolSurfacePosition.x = clamp(toolSurfacePosition.x, 0f, surfaceWidth.toFloat())
            toolSurfacePosition.y = clamp(toolSurfacePosition.y, 0f, surfaceHeight.toFloat())
            newToolPosition.set(workspace.getCanvasPointFromSurfacePoint(toolSurfacePosition))
        }
        return newToolPosition
    }

    private fun clamp(value: Float, min: Float, max: Float) = min(max, max(value, min))

    private fun contains(point: PointF, width: Int, height: Int): Boolean =
        point.x >= 0 && point.y >= 0 && point.x < width && point.y < height

    override fun handleUp(coordinate: PointF?): Boolean {
        showToolOptions()
        super.handleUp(coordinate)

        if (!pointInsideBitmap && workspace.contains(toolPosition)) {
            pointInsideBitmap = true
        }
        if (coordinate != null) {
            previousEventCoordinate?.let {
                movedDistance.set(
                    movedDistance.x + abs(coordinate.x - it.x),
                    movedDistance.y + abs(coordinate.y - it.y)
                )
            }
        }
        handleDrawMode()
        pointArray.clear()
        return true
    }

    public override fun resetInternalState() {
        pointArray.clear()
        pathToDraw.rewind()
    }

    private fun drawCircle(
        canvas: Canvas,
        strokeWidth: Float,
        outerCircleRadius: Float,
        innerCircleRadius: Float
    ) {
        canvas.drawCircle(toolPosition.x, toolPosition.y, outerCircleRadius, linePaint)
        linePaint.color = Color.LTGRAY
        canvas.drawCircle(toolPosition.x, toolPosition.y, innerCircleRadius, linePaint)
        linePaint.color = Color.TRANSPARENT
        linePaint.style = Paint.Style.FILL
        canvas.drawCircle(
            toolPosition.x,
            toolPosition.y,
            innerCircleRadius - strokeWidth / 2f,
            linePaint
        )
    }

    private fun drawRect(
        canvas: Canvas,
        strokeWidth: Float,
        outerCircleRadius: Float,
        innerCircleRadius: Float
    ) {
        val strokeRect = RectF(
            toolPosition.x - outerCircleRadius,
            toolPosition.y - outerCircleRadius,
            toolPosition.x + outerCircleRadius,
            toolPosition.y + outerCircleRadius
        )
        canvas.drawRect(strokeRect, linePaint)
        strokeRect.set(
            toolPosition.x - innerCircleRadius,
            toolPosition.y - innerCircleRadius,
            toolPosition.x + innerCircleRadius,
            toolPosition.y + innerCircleRadius
        )
        linePaint.color = Color.LTGRAY
        canvas.drawRect(strokeRect, linePaint)
        linePaint.color = Color.TRANSPARENT
        linePaint.style = Paint.Style.FILL
        strokeRect.set(
            toolPosition.x - innerCircleRadius + strokeWidth / 2f,
            toolPosition.y - innerCircleRadius + strokeWidth / 2f,
            toolPosition.x + innerCircleRadius - strokeWidth / 2f,
            toolPosition.y + innerCircleRadius - strokeWidth / 2f
        )
        canvas.drawRect(strokeRect, linePaint)
    }

    override fun drawShape(canvas: Canvas) {
        val brushStrokeWidth = max(toolPaint.strokeWidth / 2f, 1f)
        val strokeWidth = getStrokeWidthForZoom(
            DEFAULT_TOOL_STROKE_WIDTH,
            MINIMAL_TOOL_STROKE_WIDTH, MAXIMAL_TOOL_STROKE_WIDTH
        )
        val cursorPartLength = strokeWidth * 2
        val innerCircleRadius = brushStrokeWidth + strokeWidth / 2f
        val outerCircleRadius = innerCircleRadius + strokeWidth
        linePaint.apply {
            color = cursorToolPrimaryShapeColor
            style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
        }
        val strokeCap = toolPaint.strokeCap
        if (strokeCap == Cap.ROUND) {
            drawCircle(canvas, strokeWidth, outerCircleRadius, innerCircleRadius)
        } else {
            drawRect(canvas, strokeWidth, outerCircleRadius, innerCircleRadius)
        }

        linePaint.style = Paint.Style.FILL
        var startLineLengthAddition = strokeWidth / 2f
        var endLineLengthAddition = cursorPartLength + strokeWidth
        var lineNr = 0
        while (lineNr < CURSOR_LINES) {
            if (lineNr % 2 == 0) {
                linePaint.color = cursorToolSecondaryShapeColor
            } else {
                linePaint.color = cursorToolPrimaryShapeColor
            }

            canvas.drawLine(
                toolPosition.x - outerCircleRadius - startLineLengthAddition,
                toolPosition.y,
                toolPosition.x - outerCircleRadius - endLineLengthAddition,
                toolPosition.y,
                linePaint
            )
            canvas.drawLine(
                toolPosition.x + outerCircleRadius + startLineLengthAddition,
                toolPosition.y,
                toolPosition.x + outerCircleRadius + endLineLengthAddition,
                toolPosition.y,
                linePaint
            )
            canvas.drawLine(
                toolPosition.x,
                toolPosition.y + outerCircleRadius + startLineLengthAddition,
                toolPosition.x,
                toolPosition.y + outerCircleRadius + endLineLengthAddition,
                linePaint
            )
            canvas.drawLine(
                toolPosition.x,
                toolPosition.y - outerCircleRadius - startLineLengthAddition,
                toolPosition.x,
                toolPosition.y - outerCircleRadius - endLineLengthAddition,
                linePaint
            )
            lineNr++
            startLineLengthAddition = strokeWidth / 2f + cursorPartLength * lineNr
            endLineLengthAddition = strokeWidth + cursorPartLength * (lineNr + 1f)
        }
    }

    override fun onClickOnButton() = Unit

    override fun draw(canvas: Canvas) {
        if (toolInDrawMode) {
            canvas.run {
                save()
                clipRect(0, 0, workspace.width, workspace.height)
                drawPath(pathToDraw, toolPaint.previewPaint)
                restore()
            }
        }
        drawShape(canvas)
    }

    private fun addPathCommand(coordinate: PointF): Boolean {
        pathToDraw.lineTo(coordinate.x, coordinate.y)
        val bounds = RectF()
        pathToDraw.computeBounds(bounds, true)
        bounds.inset(-toolPaint.strokeWidth, -toolPaint.strokeWidth)
        if (workspace.intersectsWith(bounds)) {

            val distance = sqrt(
                (
                    (coordinate.x - initialEventCoordinate.x) *
                        (coordinate.x - initialEventCoordinate.x) +
                        (coordinate.y - initialEventCoordinate.y)
                    ).toDouble()
            )
            val speed = distance / drawTime

            if (!smoothing || speed < threshold) {
                val command = commandFactory.createPathCommand(toolPaint.paint, pathToDraw)
                commandManager.addCommand(command)
            } else {
                val pathNew =
                    smoothingAlgorithm(pointArray)
                pathNew.computeBounds(bounds, true)
                val command = commandFactory.createPathCommand(toolPaint.paint, pathNew)
                commandManager.addCommand(command)
            }

            return true
        }
        resetInternalState(StateChange.RESET_INTERNAL_STATE)
        return false
    }

    private fun addPointCommand(coordinate: PointF): Boolean {
        if (!pointInsideBitmap) {
            resetInternalState(StateChange.RESET_INTERNAL_STATE)
            return false
        }
        val command = commandFactory.createPointCommand(toolPaint.paint, coordinate)
        commandManager.addCommand(command)
        return true
    }

    private fun handleDrawMode() {
        if (toolInDrawMode) {
            if (MOVE_TOLERANCE < movedDistance.x || MOVE_TOLERANCE < movedDistance.y) {
                addPathCommand(toolPosition)
                cursorToolSecondaryShapeColor = toolPaint.color
            } else {
                contextCallback.showNotification(R.string.cursor_draw_inactive)
                toolInDrawMode = false
                cursorToolSecondaryShapeColor = Color.LTGRAY
            }
        } else {
            if (MOVE_TOLERANCE >= movedDistance.x && MOVE_TOLERANCE >= movedDistance.y) {
                contextCallback.showNotification(R.string.cursor_draw_active)
                toolInDrawMode = true
                cursorToolSecondaryShapeColor = toolPaint.color
                addPointCommand(toolPosition)
            }
        }

        pointArray.clear()
    }

    override fun toolPositionCoordinates(coordinate: PointF): PointF {
        var finalCoordinates: PointF = PointF(0f, 0f)
        previousEventCoordinate?.let {
            val deltaX = coordinate.x - it.x
            val deltaY = coordinate.y - it.y
            finalCoordinates = calculateNewClampedToolPosition(deltaX, deltaY)
        }
        return finalCoordinates
    }
}
