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
    private var isFirstPath: Boolean = true
    private var createNewVertex: Boolean = true
    private var undoWasClicked = false

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
        Log.e(TAG, " ✓ clicked")
        hidePlusButton()
        startCoordinateIsSet = false
        currentPathCommand = null
        createNewVertex = true
        isFirstPath = true
    }

    fun onClickOnPlus() {
        startPoint = endPoint?.let { copyPointF(it) }
        currentPathCommand = null
        createNewVertex = true
        Log.e(TAG, "+ clicked")
    }

    override fun toolPositionCoordinates(coordinate: PointF): PointF = coordinate

    override fun draw(canvas: Canvas) {
        startPoint?.let { start ->
            endPoint?.let { end ->
                Log.e(TAG, "drawing")
                canvas.run {
                    save()
                    clipRect(0, 0, workspace.width, workspace.height)
                    drawLine(start.x, start.y, end.x, end.y, toolPaint.previewPaint)
                    restore()
                }
            }
        }
        drawShape(canvas)
    }

    fun undo() {
        startPoint = null
        endPoint = null
        commandManager.undo()
//        currentPathCommand = commandManager.undoInDynamicLineTool() as PathCommand
//        if (currentPathCommand != null) {
//            this.startPoint = (currentPathCommand as PathCommand).startPoint
//            this.endPoint = (currentPathCommand as PathCommand).endPoint
//        }
        // remove from vertex stack
        var d = 3

    }

    fun redo() {

        // what to do with redo if a line was moved ? empty the redo??
//        currentPathCommand = commandManager.redoInDynamicLineTool() as PathCommand
//        if (currentPathCommand != null) {
//            this.startPoint = (currentPathCommand as PathCommand).startPoint
//            this.endPoint = (currentPathCommand as PathCommand).endPoint
//        }
        commandManager.redo()
        var d = 3
    }

    override fun drawShape(canvas: Canvas) {
        vertexStack.forEach {
            it.vertex?.let { vertex -> canvas.drawRect(vertex, DynamicLineToolVertex.getPaint())
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
//        undoAdjustingPath()
//        updateLastVertexPosition(copyPointF(coordinate))

        endPoint = copyPointF(coordinate)
        Log.e(TAG, "Startcoordinate x: " + startPoint!!.x.toString() + " y: " + startPoint!!.y.toString())
        Log.e(TAG, "Endcoordinate x: " + endPoint!!.x.toString() + " y: " + endPoint!!.y.toString())
        return true
    }

    private fun updateLastVertexPosition(newCenter: PointF) {
        if (vertexStack.isNotEmpty()) {
            vertexStack.last.updateVertex(newCenter)
        }
    }

    private fun undoAdjustingPath() {
        if (currentPathCommand != null) {
            commandManager.undoIgnoringColorChanges()
            currentPathCommand = null
        }
    }

    override fun handleUp(coordinate: PointF?): Boolean {
        coordinate ?: return false
        showToolOptions()
        super.handleUp(coordinate)

        var currentlyDrawnPath = createSerializeablePath(startPoint, coordinate)
        // This would mean we are updating an existing path
        if (currentPathCommand != null) {
            // either update an existing command
            (currentPathCommand as PathCommand).updatePath(currentlyDrawnPath)
            updateStartAndEndPointsOfCurrentPath(coordinate)

            commandManager.executeAllCommands()
        } else {
            // or create a new one
            currentPathCommand = commandFactory.createPathCommand(toolPaint.paint, currentlyDrawnPath) as PathCommand
            updateStartAndEndPointsOfCurrentPath(coordinate)
            commandManager.addCommand(currentPathCommand)
        }


        return true
    }

    private fun updateStartAndEndPointsOfCurrentPath(coordinate: PointF) {
        var pathStartPoint = startPoint?.let { copyPointF(it) }
        var pathEndPoint = copyPointF(coordinate)
        (currentPathCommand as PathCommand).updateStartAndEndPoint(pathStartPoint, pathEndPoint)
    }

    override fun changePaintColor(color: Int) {
        super.changePaintColor(color)
        updatePaintColor()
        brushToolOptionsView.invalidate()
    }

    override fun changePaintStrokeWidth(strokeWidth: Int) {
        super.changePaintStrokeWidth(strokeWidth)
        updatePaintStroke()
        brushToolOptionsView.invalidate()
    }

    override fun changePaintStrokeCap(cap: Paint.Cap) {
        super.changePaintStrokeCap(cap)
        updatePaintStroke()
        brushToolOptionsView.invalidate()
    }

    private fun updatePaintColor() {
        if (currentPathCommand != null) {
            (currentPathCommand as PathCommand).updatePaint(toolPaint.paint)
        }
    }

    private fun updatePaintStroke() {
        if (currentPathCommand != null) {
            (currentPathCommand as PathCommand).updatePaint(toolPaint.paint)
            commandManager.executeAllCommands()
        }
    }
    private fun hidePlusButton() {
        if (LineTool.topBarViewHolder != null && LineTool.topBarViewHolder?.plusButton?.visibility == View.VISIBLE) {
            LineTool.topBarViewHolder?.hidePlusButton()
        }
    }

    private fun copyPointF(coordinate: PointF): PointF = PointF(coordinate.x, coordinate.y)

    private fun createSerializeablePath(startCoordinate: PointF?, endCoordinate: PointF): SerializablePath {
        return SerializablePath().apply {
            if (startCoordinate != null && endCoordinate != null) {
                moveTo(startCoordinate.x, startCoordinate.y)
                lineTo(endCoordinate.x, endCoordinate.y)
            }
        }
    }

    companion object {
        var topBarViewHolder: TopBarViewHolder? = null
        const val TAG = "DynamicLineTool"
    }
}
