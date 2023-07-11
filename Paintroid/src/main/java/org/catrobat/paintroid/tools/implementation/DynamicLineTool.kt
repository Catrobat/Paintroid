package org.catrobat.paintroid.tools.implementation

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.Log
import android.view.View
import androidx.test.espresso.idling.CountingIdlingResource
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.command.implementation.PathCommand
import org.catrobat.paintroid.command.serialization.SerializablePath
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.common.CommonBrushChangedListener
import org.catrobat.paintroid.tools.common.CommonBrushPreviewListener
import org.catrobat.paintroid.tools.helper.DynamicLineToolVertex
import org.catrobat.paintroid.tools.options.BrushToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import org.catrobat.paintroid.ui.viewholder.TopBarViewHolder
import java.util.*
import java.util.ArrayDeque

class DynamicLineTool(
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
    override var toolType: ToolType = ToolType.DYNAMICLINE
    private var startPoint: PointF? = null
    private var endPoint: PointF? = null
    private var startCoordinateIsSet: Boolean = false
    private var vertexStack: Deque<DynamicLineToolVertex> = ArrayDeque()
    private var currentPathCommand: PathCommand? = null
    private var undoRecentlyClicked = false

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
        topBarViewHolder?.hidePlusButton()
    }

    override fun handleUpAnimations(coordinate: PointF?) {
        super.handleUp(coordinate)
    }

    override fun handleDownAnimations(coordinate: PointF?) {
        super.handleDown(coordinate)
    }

    override fun onClickOnButton() {
        hidePlusButton()
        startCoordinateIsSet = false
        currentPathCommand = null
    }

    fun onClickOnPlus() {
        startPoint = endPoint?.let { copyPointF(it) }
        currentPathCommand = null
    }

    override fun toolPositionCoordinates(coordinate: PointF): PointF = coordinate

    override fun draw(canvas: Canvas) {
        startPoint?.let { start ->
            endPoint?.let { end ->
                canvas.run {
                    Log.e(TAG, "draw")
                    save()
                    clipRect(0, 0, workspace.width, workspace.height)
                    drawLine(start.x, start.y, end.x, end.y, toolPaint.previewPaint)
                    restore()
                }
            }
        }
    }

    fun undo() {
        var undoCommand = commandManager.getFirstUndoCommand()
//        commandManager.undo()
        commandManager.undoIgnoringColorChanges()
        undoRecentlyClicked = true
        if (undoCommand != null && undoCommand is PathCommand && undoCommand.isDynamicLineToolPathCommand) {
            setCurrentPathCommand(undoCommand)
            setToolPaint()
        } else {
            reset()
        }
    }

    fun redo() {
        var redoCommand = commandManager.getFirstRedoCommand()
        commandManager.redo()
        if (redoCommand != null && redoCommand is PathCommand && redoCommand.isDynamicLineToolPathCommand) {
            setCurrentPathCommand(redoCommand)
            setToolPaint()
        } else {
            reset()
        }
    }

    private fun reset() {
        startPoint = null
        endPoint = null
        startCoordinateIsSet = false
        currentPathCommand = null
    }

    private fun setCurrentPathCommand(currentCommand: PathCommand) {
        currentPathCommand = currentCommand
        this.startPoint = (currentPathCommand as PathCommand).startPoint
        this.endPoint = (currentPathCommand as PathCommand).endPoint
    }

    private fun setToolPaint() {
        super.changePaintColor((currentPathCommand as PathCommand).paint.color)
        super.changePaintStrokeCap((currentPathCommand as PathCommand).paint.strokeCap)
        super.changePaintStrokeWidth((currentPathCommand as PathCommand).paint.strokeWidth.toInt())
    }

    override fun drawShape(canvas: Canvas) {
        vertexStack.forEach {
            it.vertex?.let { vertex -> canvas.drawRect(vertex, DynamicLineToolVertex.getPaint())
            }
        }
    }

    override fun handleDown(coordinate: PointF?): Boolean {
        coordinate ?: return false
        topBarViewHolder?.showPlusButton()
        super.handleDown(coordinate)
        startPoint = if (!startCoordinateIsSet) {
            copyPointF(coordinate).also { startCoordinateIsSet = true }
        } else {
            startPoint
        }
        return true
    }

    override fun handleMove(coordinate: PointF?): Boolean {
        coordinate ?: return false
        hideToolOptions()
        super.handleMove(coordinate)
        endPoint = copyPointF(coordinate)
        return true
    }

    private fun handleRedo() {
        if (undoRecentlyClicked) {
            var firstRedoCommand = commandManager.getFirstRedoCommand()
            if (firstRedoCommand != null &&
                currentPathCommand != null &&
                firstRedoCommand is PathCommand &&
                firstRedoCommand.isDynamicLineToolPathCommand &&
                firstRedoCommand.startPoint != currentPathCommand?.endPoint) {
                // a previous command was moved so redo has to be deactivated
                commandManager.clearRedoCommandList()
                undoRecentlyClicked = false
            }
        }
    }

    override fun handleUp(coordinate: PointF?): Boolean {
        coordinate ?: return false
        showToolOptions()
        super.handleUp(coordinate)
        endPoint = copyPointF(coordinate)
        createOrAdjustPathCommand()
        handleRedo()
        return true
    }

    private fun createOrAdjustPathCommand() {
        var currentlyDrawnPath = createSerializablePath(startPoint, endPoint)
        if (currentPathCommand != null) {
            (currentPathCommand as PathCommand).setPath(currentlyDrawnPath)
            (currentPathCommand as PathCommand).
                setStartAndEndPoint(startPoint?.let { start -> copyPointF(start) }, endPoint?.let { end -> copyPointF(end) })
            commandManager.executeAllCommands()
        } else {
            currentPathCommand = commandFactory.createPathCommand(toolPaint.paint, currentlyDrawnPath) as PathCommand
            (currentPathCommand as PathCommand).
                setStartAndEndPoint(startPoint?.let { start -> copyPointF(start) }, endPoint?.let { end -> copyPointF(end) })
            (currentPathCommand as PathCommand).isDynamicLineToolPathCommand = true
            commandManager.addCommand(currentPathCommand)
        }
    }

    override fun changePaintColor(color: Int) {
        super.changePaintColor(color)
        updatePaintColor()
    }

    override fun changePaintStrokeWidth(strokeWidth: Int) {
        super.changePaintStrokeWidth(strokeWidth)
        updatePaintStrokeWidth()
    }

    override fun changePaintStrokeCap(cap: Paint.Cap) {
        super.changePaintStrokeCap(cap)
        updatePaintStrokeCap()
    }

    private fun updatePaintColor() {
        if (currentPathCommand != null) {
            (currentPathCommand as PathCommand).setPaintColor(toolPaint.color)
            commandManager.executeCommand(currentPathCommand)
            brushToolOptionsView.invalidate()
        }
    }

    private fun updatePaintStrokeCap() {
        if (currentPathCommand != null) {
            (currentPathCommand as PathCommand).setPaintStrokeCap(toolPaint.strokeCap)
            commandManager.executeAllCommands()
            brushToolOptionsView.invalidate()
        }
    }

    private fun updatePaintStrokeWidth() {
        if (currentPathCommand != null) {
            (currentPathCommand as PathCommand).setPaintStrokeWidth(toolPaint.strokeWidth)
            commandManager.executeAllCommands()
            brushToolOptionsView.invalidate()
        }
    }

    private fun hidePlusButton() {
        if (LineTool.topBarViewHolder != null && LineTool.topBarViewHolder?.plusButton?.visibility == View.VISIBLE) {
            LineTool.topBarViewHolder?.hidePlusButton()
        }
    }

    private fun copyPointF(coordinate: PointF): PointF = PointF(coordinate.x, coordinate.y)

    private fun createSerializablePath(startCoordinate: PointF?, endCoordinate: PointF?): SerializablePath {
        return SerializablePath().apply {
            if (startCoordinate != null && endCoordinate != null) {
                moveTo(startCoordinate.x, startCoordinate.y)
                lineTo(endCoordinate.x, endCoordinate.y)
            }
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

    companion object {
        var topBarViewHolder: TopBarViewHolder? = null
        const val TAG = "DynamicLineTool"
    }
}
