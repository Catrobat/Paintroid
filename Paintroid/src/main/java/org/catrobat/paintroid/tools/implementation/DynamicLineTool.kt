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
import org.catrobat.paintroid.tools.helper.Vertex
import org.catrobat.paintroid.tools.options.BrushToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import org.catrobat.paintroid.ui.viewholder.TopBarViewHolder
import java.util.*
import java.util.ArrayDeque

const val MOVING_FRAMES = 3
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
    private var currentStartPoint: PointF? = null
    private var currentEndPoint: PointF? = null
    private var startCoordinateIsSet: Boolean = false
    private var vertexStack: Deque<Vertex> = ArrayDeque()
    private var currentPathCommand: PathCommand? = null
    private var undoRecentlyClicked = false
    private var movingVertex: Vertex? = null
    private var predecessorVertex: Vertex? = null
    private var successorVertex: Vertex? = null
    private var movingFramesCounter: Int = 0

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
        vertexStack.clear()
    }

    fun onClickOnPlus() {
        currentStartPoint = currentEndPoint?.let { copyPointF(it) }
        currentPathCommand = null
    }

    override fun toolPositionCoordinates(coordinate: PointF): PointF = coordinate

    override fun draw(canvas: Canvas) {
        currentStartPoint?.let { start ->
            currentEndPoint?.let { end ->
                canvas.run {
                    Log.e(TAG, "draw")
                    save()
                    clipRect(0, 0, workspace.width, workspace.height)
                    drawLine(start.x, start.y, end.x, end.y, toolPaint.previewPaint)
                    restore()
                }
            }
        }
        drawShape(canvas)
    }
    @Synchronized override fun drawShape(canvas: Canvas) {
        vertexStack.forEach { vertex ->
            vertex?.let { vertex ->
                var cx = vertex.vertexCenter?.x
                var cy = vertex.vertexCenter?.y
                if (cx != null && cy != null) {
                    canvas.drawCircle(cx, cy, Vertex.VERTEX_RADIUS, Vertex.getPaint())
                }
            }
        }
    }

    fun undo() {
        var undoCommand = commandManager.getFirstUndoCommand()
        commandManager.undo()
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
        currentStartPoint = null
        currentEndPoint = null
        startCoordinateIsSet = false
        currentPathCommand = null
    }

    private fun setCurrentPathCommand(currentCommand: PathCommand) {
        currentPathCommand = currentCommand
        this.currentStartPoint = (currentPathCommand as PathCommand).startPoint
        this.currentEndPoint = (currentPathCommand as PathCommand).endPoint
    }

    private fun setToolPaint() {
        super.changePaintColor((currentPathCommand as PathCommand).paint.color)
        super.changePaintStrokeCap((currentPathCommand as PathCommand).paint.strokeCap)
        super.changePaintStrokeWidth((currentPathCommand as PathCommand).paint.strokeWidth.toInt())
    }

    override fun handleDown(coordinate: PointF?): Boolean {
        coordinate ?: return false
        topBarViewHolder?.showPlusButton()
        super.handleDown(coordinate)

        var clicked = vertexWasClicked(coordinate)
        Log.e(TAG, "Vertex clicked = $clicked")
        if (clicked) return false

        currentStartPoint = if (!startCoordinateIsSet) {
            copyPointF(coordinate).also { startCoordinateIsSet = true }
        } else {
            currentStartPoint
        }
        return true
    }

    @Synchronized private fun vertexWasClicked(clickedCoordinate: PointF): Boolean {
        if (vertexStack.isEmpty()) return false
        for (vertex in vertexStack) {
            vertex.let { vertex ->
                if (vertex.wasClicked(clickedCoordinate)) {
                    movingVertex = vertex
                    val index = vertexStack.indexOf(movingVertex)
                    predecessorVertex = vertexStack.elementAtOrNull(index - 1)
                    successorVertex = vertexStack.elementAtOrNull(index + 1)
                    currentStartPoint = null
                    currentEndPoint = null
                    return true
                }
            }
        }
        return false
    }

    override fun handleMove(coordinate: PointF?): Boolean {
        coordinate ?: return false
        hideToolOptions()
        super.handleMove(coordinate)
        currentEndPoint = copyPointF(coordinate)
        if (movingFramesCounter++ % MOVING_FRAMES != 0) return true
        updateMovingVertices(coordinate)
        return true
    }

    private fun updateMovingVertices(coordinate: PointF) {
        if (movingVertex != null) {
            var newCenter = copyPointF(coordinate)
            movingVertex?.updateVertexCenter(newCenter)
            if (movingVertex?.ingoingPathCommand != null) {
                var startPoint = predecessorVertex?.vertexCenter?.let { center -> copyPointF(center) }
                var endPoint = copyPointF(coordinate)
                var updatedPath = createSerializablePath(startPoint, endPoint)
                (movingVertex?.ingoingPathCommand as PathCommand).setPath(updatedPath)
                (movingVertex?.ingoingPathCommand as PathCommand).setStartAndEndPoint(startPoint, endPoint)
                commandManager.executeCommand(movingVertex?.ingoingPathCommand )
            }
            if (movingVertex?.outgoingPathCommand != null) {
                var startPoint = copyPointF(coordinate)
                var endPoint = successorVertex?.vertexCenter?.let { center -> copyPointF(center) }
                var updatedPath = createSerializablePath(startPoint, endPoint)
                (movingVertex?.outgoingPathCommand as PathCommand).setPath(updatedPath)
                (movingVertex?.outgoingPathCommand as PathCommand).setStartAndEndPoint(startPoint, endPoint)
                commandManager.executeCommand(movingVertex?.outgoingPathCommand )
            }
            commandManager.executeAllCommands()
        }
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
        currentEndPoint = copyPointF(coordinate)

        if (resetClickedVertex(coordinate)) return true

        var pathWasCreated = createOrAdjustPathCommand()
        if (pathWasCreated) createVertex() else adjustVertex()
        handleRedo()
        var lenk = vertexStack
        return true
    }

    private fun resetClickedVertex(coordinate: PointF): Boolean {
        // maybe final redraw??
        movingVertex?.let {
            // if the movingVertex is not null at an up event, this means vertices were moved
            // if a middle vertex was moved, the
            movingVertex = null
            predecessorVertex = null
            successorVertex = null
            currentEndPoint = vertexStack.last.vertexCenter?.let { it1 -> copyPointF(it1) }
            return true
        }
        return false
    }

    private fun createOrAdjustPathCommand(): Boolean {
        var currentlyDrawnPath = createSerializablePath(currentStartPoint, currentEndPoint)
        if (currentPathCommand != null) {
            (currentPathCommand as PathCommand).setPath(currentlyDrawnPath)
            (currentPathCommand as PathCommand).
                setStartAndEndPoint(currentStartPoint?.let { start -> copyPointF(start) }, currentEndPoint?.let { end -> copyPointF(end) })
            commandManager.executeAllCommands()
            return false
        } else {
            currentPathCommand = commandFactory.createPathCommand(toolPaint.paint, currentlyDrawnPath) as PathCommand
            (currentPathCommand as PathCommand).
                setStartAndEndPoint(currentStartPoint?.let { start -> copyPointF(start) }, currentEndPoint?.let { end -> copyPointF(end) })
            (currentPathCommand as PathCommand).isDynamicLineToolPathCommand = true
            commandManager.addCommand(currentPathCommand)
            return true
        }
    }

    private fun createVertex() {
        if (vertexStack.isEmpty()) {
            createSourceVertex()
        }
        createDestinationVertex()
    }

    @Synchronized private fun createSourceVertex() {
        var vertexCenter = currentStartPoint?.let { start -> copyPointF(start) }
        var sourceVertex = Vertex(vertexCenter, currentPathCommand, null)
        vertexStack.add(sourceVertex)
    }

    @Synchronized private fun createDestinationVertex() {
        vertexStack.last.setOutgoingPath(currentPathCommand)
        var vertexCenter = currentEndPoint?.let { end -> copyPointF(end) }
        var destinationVertex = Vertex(vertexCenter, null, currentPathCommand)
        vertexStack.add(destinationVertex)

    }

    @Synchronized private fun adjustVertex() {
        var newVertexCenter: PointF? = currentEndPoint?.let { end -> copyPointF(end) }
        if (newVertexCenter != null) {
            vertexStack.last.updateVertexCenter(newVertexCenter)
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
            commandManager.executeAllCommands()
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
