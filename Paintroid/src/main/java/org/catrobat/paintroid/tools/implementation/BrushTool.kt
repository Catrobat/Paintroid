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
import android.graphics.Paint
import android.graphics.PointF
import android.view.View
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
        brushToolOptionsView.setStrokeCapButtonChecked(toolPaint.strokeCap)
    }

    override fun draw(canvas: Canvas) {
        canvas.run {
            save()
            clipRect(0, 0, workspace.width, workspace.height)
            drawPath(pathToDraw, previewPaint)
            restore()
        }
    }

    private fun hideBrushSpecificLayoutOnHandleDown() {
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

    private fun showBrushSpecificLayoutOnHandleUp() {
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
        coordinate ?: return false
        super.handleDown(coordinate)
        initialEventCoordinate = PointF(coordinate.x, coordinate.y)
        previousEventCoordinate = PointF(coordinate.x, coordinate.y)
        pathToDraw.moveTo(coordinate.x, coordinate.y)
        drawToolMovedDistance.set(0f, 0f)
        pointArray.add(PointF(coordinate.x, coordinate.y))
        pathInsideBitmap = workspace.contains(coordinate)
        return true
    }

    override fun handleDownAnimations(coordinate: PointF?) {
        hideBrushSpecificLayoutOnHandleDown()
    }

    override fun handleUpAnimations(coordinate: PointF?) {
        showBrushSpecificLayoutOnHandleUp()
        super.handleUp(coordinate)
    }

    override fun handleMove(coordinate: PointF?, shouldAnimate: Boolean): Boolean {
        if (eventCoordinatesAreNull() || coordinate == null) {
            return false
        }
        super.handleMove(coordinate, shouldAnimate)
        if (shouldAnimate) {
            hideBrushSpecificLayoutOnHandleDown()
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

    override fun handleUp(coordinate: PointF?): Boolean {
        if (eventCoordinatesAreNull() || coordinate == null) {
            return false
        }
        showBrushSpecificLayoutOnHandleUp()
        super.handleUp(coordinate)

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

    override fun toolPositionCoordinates(coordinate: PointF): PointF = coordinate

    override fun resetInternalState() {
        pathToDraw.rewind()
        pointArray.clear()
        initialEventCoordinate = null
        previousEventCoordinate = null
    }

    override fun changePaintColor(color: Int, invalidate: Boolean) {
        super.changePaintColor(color, invalidate)
        if (invalidate) brushToolOptionsView.invalidate()
    }

    private fun eventCoordinatesAreNull(): Boolean =
        initialEventCoordinate == null || previousEventCoordinate == null

    private fun addPathCommand(coordinate: PointF): Boolean {
        pathToDraw.lineTo(coordinate.x, coordinate.y)

        if (!pathInsideBitmap) {
            resetInternalState(StateChange.RESET_INTERNAL_STATE)
            return false
        }

        if (toolType == ToolType.ERASER) {
            val command = commandFactory.createPathCommand(bitmapPaint, pathToDraw)
            commandManager.addCommand(command)
        } else {
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
}
