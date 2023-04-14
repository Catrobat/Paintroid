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
import android.graphics.RectF
import android.view.View
import androidx.test.espresso.idling.CountingIdlingResource
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.command.serialization.SerializablePath
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.common.CommonBrushChangedListener
import org.catrobat.paintroid.tools.common.CommonBrushPreviewListener
import org.catrobat.paintroid.tools.options.BrushToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import org.catrobat.paintroid.ui.viewholder.TopBarViewHolder

class LineTool(
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
    override var toolType: ToolType = ToolType.LINE

    var lineFinalized: Boolean = false
    var endpointSet: Boolean = false
    var startpointSet: Boolean = false
    var initialEventCoordinate: PointF? = null
    var startPointToDraw: PointF? = null
    var endPointToDraw: PointF? = null
    var currentCoordinate: PointF? = null
    var toolSwitched: Boolean = false
    var lastSetStrokeWidth: Int = 0
    var connectedLines = false
    var undoRecentlyClicked = false
    var undoPreviousLineForConnectedLines = true
    var changeInitialCoordinateForHandleNormalLine = false

    companion object {
        var topBarViewHolder: TopBarViewHolder? = null
    }

    init {
        brushToolOptionsView.setBrushChangedListener(CommonBrushChangedListener(this))
        brushToolOptionsView.setBrushPreviewListener(
            CommonBrushPreviewListener(
                toolPaint,
                toolType
            )
        )
        brushToolOptionsView.setCurrentPaint(toolPaint.paint)
        brushToolOptionsView.setStrokeCapButtonChecked(toolPaint.strokeCap)
        if (topBarViewHolder != null && topBarViewHolder?.plusButton?.visibility == View.VISIBLE) {
            topBarViewHolder?.hidePlusButton()
        }
    }

    override fun draw(canvas: Canvas) {
        initialEventCoordinate?.let { initialCoordinate ->
            currentCoordinate?.let { currentCoordinate ->
                canvas.run {
                    save()
                    clipRect(0, 0, workspace.width, workspace.height)
                    drawLine(
                        initialCoordinate.x,
                        initialCoordinate.y, currentCoordinate.x,
                        currentCoordinate.y, toolPaint.previewPaint
                    )
                    restore()
                }
            }
        }
    }

    fun handleStateBeforeUndo() {
        if (!lineFinalized && startpointSet && !connectedLines) {
            startpointSet = false
            startPointToDraw = null
        } else {
            if (!undoRecentlyClicked) {
                endpointSet = false
                endPointToDraw = null
            } else {
                undoPreviousLineForConnectedLines = true
                changeInitialCoordinateForHandleNormalLine = false
                lineFinalized = true
                resetInternalState()
            }
            undoRecentlyClicked = true
        }
        val isPlusVisible = topBarViewHolder!!.plusButton.visibility == View.VISIBLE
        if (isPlusVisible && !connectedLines) {
            topBarViewHolder!!.plusButton.visibility = View.GONE
        }
    }

    override fun drawShape(canvas: Canvas) {
        // This should never be invoked
    }

    fun onClickOnPlus() {
        if (startpointSet && endpointSet) {
            val newStartCoordinate = endPointToDraw?.let { PointF(it.x, it.y) }
            initialEventCoordinate = endPointToDraw?.let { PointF(it.x, it.y) }
            previousEventCoordinate = endPointToDraw?.let { PointF(it.x, it.y) }
            startPointToDraw = null
            endPointToDraw = null
            startpointSet = false
            endpointSet = false
            lineFinalized = false
            connectedLines = true
            undoRecentlyClicked = false
            handleAddedLine(newStartCoordinate)
        }
    }

    override fun onClickOnButton() {
        if (topBarViewHolder != null && topBarViewHolder?.plusButton?.visibility == View.VISIBLE) {
            topBarViewHolder?.hidePlusButton()
        }
        undoRecentlyClicked = false
        if (startpointSet && endpointSet) {
            if (toolSwitched) {
                val startX = startPointToDraw?.x
                val startY = startPointToDraw?.y
                val endX = endPointToDraw?.x
                val endY = endPointToDraw?.y
                val finalPath = SerializablePath().apply {
                    if (startX != null && startY != null && endX != null && endY != null) {
                        moveTo(startX, startY)
                        lineTo(endX, endY)
                    }
                }
                lineFinalized = true
                toolSwitched = false
                val command = commandFactory.createPathCommand(toolPaint.paint, finalPath)
                commandManager.addCommand(command)
            }
            lineFinalized = true
            resetInternalState()
        } else if (startpointSet && !endpointSet) {
            if (commandManager.isUndoAvailable) {
                commandManager.undoIgnoringColorChanges()
            }
            lineFinalized = true
            resetInternalState()
        } else {
            resetInternalState()
        }
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
        coordinate ?: return false
        super.handleDown(coordinate)
        initialEventCoordinate = PointF(coordinate.x, coordinate.y)
        previousEventCoordinate = PointF(coordinate.x, coordinate.y)
        return true
    }

    override fun handleMove(coordinate: PointF?): Boolean {
        coordinate ?: return false
        hideToolOptions()
        super.handleMove(coordinate)
        changeInitialCoordinateForHandleNormalLine = true
        if (startpointSet) {
            initialEventCoordinate = startPointToDraw?.let { PointF(it.x, it.y) }
            previousEventCoordinate = startPointToDraw?.let { PointF(it.x, it.y) }
            if (undoPreviousLineForConnectedLines && commandManager.isUndoAvailable && !undoRecentlyClicked) {
                undoRecentlyClicked = false
                commandManager.undoIgnoringColorChanges()
            }
            undoPreviousLineForConnectedLines = false
            undoRecentlyClicked = false
        }
        currentCoordinate = PointF(coordinate.x, coordinate.y)
        return true
    }

    fun handleStartPoint(xDistance: Float, yDistance: Float): Boolean {
        startPointToDraw = previousEventCoordinate?.let { PointF(it.x, it.y) }
        startPointToDraw?.x = startPointToDraw?.x?.minus(xDistance)
        startPointToDraw?.y = startPointToDraw?.y?.minus(yDistance)

        if (startPointToDraw?.let { workspace.contains(it) } == true) {
            startpointSet = true
            undoRecentlyClicked = false
            resetInternalState()
            startPointToDraw?.let {
                return addPointCommand(it)
            }
        } else {
            lineFinalized = true
            resetInternalState()
        }
        return true
    }

    fun handleEndPoint(xDistance: Float, yDistance: Float, fromHandleLine: Boolean = false): Boolean {
        if (previousEventCoordinate?.let { workspace.contains(it) } == false) {
            return false
        }
        endPointToDraw = previousEventCoordinate?.let { PointF(it.x, it.y) }
        endPointToDraw?.x = endPointToDraw?.x?.minus(xDistance)
        endPointToDraw?.y = endPointToDraw?.y?.minus(yDistance)
        endpointSet = true
        val startX = startPointToDraw?.x
        val startY = startPointToDraw?.y
        val endX = endPointToDraw?.x
        val endY = endPointToDraw?.y

        val finalPath = SerializablePath().apply {
            if (startX != null && startY != null && endX != null && endY != null) {
                moveTo(startX, startY)
                lineTo(endX, endY)
            }
        }
        val command = commandFactory.createPathCommand(toolPaint.paint, finalPath)

        if (!fromHandleLine && !undoRecentlyClicked) {
            if (commandManager.isUndoAvailable && !undoRecentlyClicked) {
                commandManager.undoIgnoringColorChangesAndAddCommand(command)
            }
        } else {
            commandManager.addCommand(command)
        }
        undoRecentlyClicked = false
        resetInternalState()
        if (topBarViewHolder != null && topBarViewHolder?.plusButton?.visibility != View.VISIBLE) {
            topBarViewHolder?.showPlusButton()
        }
        return true
    }

    fun handleNormalLine(coordinate: PointF, xDistance: Float, yDistance: Float): Boolean {
        val bounds = RectF()
        if (startpointSet) {
            return handleEndPoint(xDistance, yDistance, true)
        }
        val finalPath = SerializablePath().apply {
            moveTo(
                initialEventCoordinate?.x ?: return false,
                initialEventCoordinate?.y ?: return false
            )
            lineTo(coordinate.x, coordinate.y)
            computeBounds(bounds, true)
        }
        bounds.inset(-toolPaint.strokeWidth, -toolPaint.strokeWidth)

        previousEventCoordinate?.x = previousEventCoordinate?.x?.minus(xDistance)
        previousEventCoordinate?.y = previousEventCoordinate?.y?.minus(yDistance)
        startPointToDraw = initialEventCoordinate?.let { PointF(it.x, it.y) }
        endPointToDraw = previousEventCoordinate?.let { PointF(it.x, it.y) }

        endpointSet = true
        startpointSet = true
        undoRecentlyClicked = false

        if (topBarViewHolder != null && topBarViewHolder?.plusButton?.visibility != View.VISIBLE) {
            topBarViewHolder?.showPlusButton()
        }

        if (workspace.intersectsWith(bounds)) {
            val command = commandFactory.createPathCommand(toolPaint.paint, finalPath)
            commandManager.addCommand(command)
        }
        resetInternalState()
        return true
    }

    override fun handleUp(coordinate: PointF?): Boolean {
        showToolOptions()
        super.handleUp(coordinate)
        return handleAddedLine(coordinate)
    }

    fun handleAddedLine(coordinate: PointF?): Boolean {
        undoPreviousLineForConnectedLines = true
        if (changeInitialCoordinateForHandleNormalLine && initialEventCoordinate == null) {
            initialEventCoordinate = startPointToDraw?.let { PointF(it.x, it.y) }
        }
        if (initialEventCoordinate == null || previousEventCoordinate == null || coordinate == null) {
            changeInitialCoordinateForHandleNormalLine = false
            return false
        }
        val xDistance = initialEventCoordinate?.x?.minus(coordinate.x)
        val yDistance = initialEventCoordinate?.y?.minus(coordinate.y)
        if (xDistance != null && yDistance != null) {
            if (changeInitialCoordinateForHandleNormalLine) {
                changeInitialCoordinateForHandleNormalLine = false
                return handleNormalLine(coordinate, xDistance, yDistance)
            } else if (!startpointSet) {
                return handleStartPoint(xDistance, yDistance)
            } else {
                return handleEndPoint(xDistance, yDistance)
            }
        }
        changeInitialCoordinateForHandleNormalLine = false
        return true
    }

    override fun toolPositionCoordinates(coordinate: PointF): PointF = coordinate

    override fun resetInternalState() {
        initialEventCoordinate = null
        currentCoordinate = null
        if (lineFinalized) {
            connectedLines = false
            startPointToDraw = null
            endPointToDraw = null
            startpointSet = false
            endpointSet = false
            lineFinalized = false
        }
    }

    override fun changePaintColor(color: Int) {
        super.changePaintColor(color)
        if (startpointSet && endpointSet) {
            val startX = startPointToDraw?.x
            val startY = startPointToDraw?.y
            val endX = endPointToDraw?.x
            val endY = endPointToDraw?.y
            if (commandManager.isUndoAvailable) {
                val finalPath = SerializablePath().apply {
                    if (startX != null && startY != null && endX != null && endY != null) {
                        moveTo(startX, startY)
                        lineTo(endX, endY)
                    }
                }
                val command = commandFactory.createPathCommand(toolPaint.paint, finalPath)
                commandManager.undoIgnoringColorChangesAndAddCommand(command)
            }
        } else if (startpointSet && !endpointSet && !lineFinalized) {
            if (commandManager.isUndoAvailable && !undoRecentlyClicked) {
                startPointToDraw?.let {
                    val command = commandFactory.createPointCommand(this.drawPaint, it)
                    commandManager.undoIgnoringColorChangesAndAddCommand(command)
                }
            }
        }
        brushToolOptionsView.invalidate()
    }

    fun undoChangePaintColor(color: Int) {
        handleStateBeforeUndo()
        super.changePaintColor(color)
        brushToolOptionsView.invalidate()
        if (connectedLines) {
            commandManager.undoInConnectedLinesMode()
        } else {
            commandManager.undo()
        }
    }

    fun redoLineTool() {
        undoRecentlyClicked = false
        if (connectedLines) {
            commandManager.redoInConnectedLinesMode()
        } else {
            commandManager.redo()
        }
    }

    fun undoColorChangedCommand(color: Int) {
        super.changePaintColor(color)
        brushToolOptionsView.invalidate()
    }

    override fun changePaintStrokeWidth(strokeWidth: Int) {
        super.changePaintStrokeWidth(strokeWidth)
        val noNewLine = lastSetStrokeWidth == strokeWidth
        if (startpointSet && endpointSet && !noNewLine) {
            val startX = startPointToDraw?.x
            val startY = startPointToDraw?.y
            val endX = endPointToDraw?.x
            val endY = endPointToDraw?.y
            if (commandManager.isUndoAvailable) {
                val finalPath = SerializablePath().apply {
                    if (startX != null && startY != null && endX != null && endY != null) {
                        moveTo(startX, startY)
                        lineTo(endX, endY)
                    }
                }
                val command = commandFactory.createPathCommand(toolPaint.paint, finalPath)
                commandManager.undoIgnoringColorChangesAndAddCommand(command)
            }
        } else if (startpointSet && !endpointSet && !lineFinalized && !noNewLine) {
            if (commandManager.isUndoAvailable && !undoRecentlyClicked) {
                startPointToDraw?.let {
                    val command = commandFactory.createPointCommand(this.drawPaint, it)
                    commandManager.undoIgnoringColorChangesAndAddCommand(command)
                }
            }
        }
        lastSetStrokeWidth = strokeWidth
        brushToolOptionsView.invalidate()
    }

    override fun changePaintStrokeCap(cap: Paint.Cap) {
        super.changePaintStrokeCap(cap)
        if (startpointSet && endpointSet) {
            val startX = startPointToDraw?.x
            val startY = startPointToDraw?.y
            val endX = endPointToDraw?.x
            val endY = endPointToDraw?.y
            if (commandManager.isUndoAvailable) {
                val finalPath = SerializablePath().apply {
                    if (startX != null && startY != null && endX != null && endY != null) {
                        moveTo(startX, startY)
                        lineTo(endX, endY)
                    }
                }
                val command = commandFactory.createPathCommand(toolPaint.paint, finalPath)
                commandManager.undoIgnoringColorChangesAndAddCommand(command)
            }
        } else if (startpointSet && !endpointSet && !lineFinalized) {
            if (commandManager.isUndoAvailable && !undoRecentlyClicked) {
                startPointToDraw?.let {
                    val command = commandFactory.createPointCommand(this.drawPaint, it)
                    commandManager.undoIgnoringColorChangesAndAddCommand(command)
                }
            }
        }
        brushToolOptionsView.invalidate()
    }

    private fun addPointCommand(coordinate: PointF): Boolean {
        val command = commandFactory.createPointCommand(this.drawPaint, coordinate)
        commandManager.addCommand(command)
        return true
    }
}
