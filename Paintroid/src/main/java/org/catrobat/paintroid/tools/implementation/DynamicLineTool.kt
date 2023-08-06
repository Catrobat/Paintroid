package org.catrobat.paintroid.tools.implementation

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.Log
import android.view.View
import androidx.test.espresso.idling.CountingIdlingResource
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.command.implementation.DynamicPathCommand
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
import java.util.ArrayDeque
import java.util.Deque

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
    private var ingoingStartCoordinate: PointF? = null
    private var ingoingEndCoordinate: PointF? = null
    private var ingoingGhostPathColor: Int = Color.GRAY
    private var outgoingGhostPathColor: Int = Color.GRAY
    private var outgoingStartCoordinate: PointF? = null
    private var outgoingEndCoordinate: PointF? = null
    override var toolType: ToolType = ToolType.DYNAMICLINE
    var vertexStack: Deque<Vertex> = ArrayDeque()
    var movingVertex: Vertex? = null
    var predecessorVertex: Vertex? = null
    var successorVertex: Vertex? = null
    var undoRecentlyClicked = false
    var addNewPath: Boolean = false

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
        if (vertexStack.isEmpty()) return
        hidePlusButton()
        clear()
        commandManager.clearRedoCommandList()
        commandManager.executeAllCommands()
    }

    fun clear() {
        vertexStack.clear()
        movingVertex = null
        predecessorVertex = null
        successorVertex = null
    }

    fun onClickOnPlus() {
        addNewPath = true
    }

    override fun toolPositionCoordinates(coordinate: PointF): PointF = coordinate

    override fun draw(canvas: Canvas) {
        Log.e(TAG, "draw")
        drawGhostPath(ingoingStartCoordinate, ingoingEndCoordinate, canvas, workspace, ingoingGhostPathColor)
        drawGhostPath(outgoingStartCoordinate, outgoingEndCoordinate, canvas, workspace, outgoingGhostPathColor)
        drawShape(canvas)
    }

    private fun drawGhostPath(startCoordinate: PointF?, endCoordinate: PointF?, canvas: Canvas, workspace: Workspace, color: Int) {
        startCoordinate?.let { start ->
            endCoordinate?.let { end ->
                canvas.run {
                    save()
                    clipRect(0, 0, workspace.width, workspace.height)
                    drawLine(start.x, start.y, end.x, end.y, Vertex.getEdgePaint(color))
                    restore()
                }
            }
        }
    }
    @Synchronized override fun drawShape(canvas: Canvas) {
        vertexStack.forEach { vertex ->
            vertex?.let { vertex ->
                var cx = vertex.vertexCenter?.x
                var cy = vertex.vertexCenter?.y
                if (cx != null && cy != null) {
                    canvas.drawCircle(cx, cy, Vertex.VERTEX_RADIUS, Vertex.getVertexPaint())
                }
            }
        }
    }

    fun updateVertexStackAfterUndo() {
        undoRecentlyClicked = true
        if (vertexStack.size == 2) {
            vertexStack.clear()
            return
        }
        vertexStack.pollLast()
        setLastMovingAndPredecessorVertex()
    }

    fun setToolPaint(command: DynamicPathCommand) {
        super.changePaintColor(command.paint.color)
        super.changePaintStrokeCap(command.paint.strokeCap)
        super.changePaintStrokeWidth(command.paint.strokeWidth.toInt())
    }

    private fun setLastMovingAndPredecessorVertex() {
        if (vertexStack.isEmpty()) return
        val index = vertexStack.indexOf(vertexStack.last)
        predecessorVertex = vertexStack.elementAtOrNull(index - 1)
        movingVertex = vertexStack.last
    }

    fun updateVertexStackAfterRedo(redoCommand: DynamicPathCommand?) {
        var startPoint = redoCommand?.startPoint?.let { copyPointF(it) }
        var endPoint = redoCommand?.endPoint?.let { copyPointF(it) }
        if (vertexStack.isEmpty()) {
            createSourceAndDestinationVertices(startPoint, endPoint, redoCommand)
        } else {
            createDestinationVertex(endPoint, redoCommand)
        }
    }

    private fun updatePathCommand(start: PointF?, end: PointF?, pathCommand: DynamicPathCommand?) {
        start?.let { startPoint ->
            end?.let { endPoint ->
                pathCommand?.let { command ->
                    command.updatePath(createSerializablePath(startPoint, endPoint))
                    command.setStartAndEndPoint(startPoint, endPoint)
                }
            }
        }
    }

    override fun handleDown(coordinate: PointF?): Boolean {
        coordinate ?: return false
        super.handleDown(coordinate)

        if (vertexWasClicked(coordinate)) {
            return true
        }
        if (vertexStack.isEmpty()) {
            createSourceAndDestinationCommandAndVertices(coordinate)
            return true
        }
        if (addNewPath) {
            createDestinationCommandAndVertex()
            addNewPath = false
            return true
        }
        return true
    }

    private fun clearRedoIfPathWasAdjusted() {
        if (!undoRecentlyClicked) return
        var firstRedoCommand = commandManager.getFirstRedoCommand() ?: return
        if (firstRedoCommand is DynamicPathCommand &&
            firstRedoCommand.startPoint != vertexStack.last.vertexCenter) {
            // a previous command was moved so redo has to be deactivated
            commandManager.clearRedoCommandList()
            undoRecentlyClicked = false
        }
    }

    override fun handleMove(coordinate: PointF?): Boolean {
        coordinate ?: return false
        hideToolOptions()
        super.handleMove(coordinate)
        updateMovingGhostVertices(coordinate)
        clearRedoIfPathWasAdjusted()
        return true
    }

    override fun handleUp(coordinate: PointF?): Boolean {
        coordinate ?: return false
        showToolOptions()
        super.handleUp(coordinate)
        updateMovingVertices(coordinate)
        clearRedoIfPathWasAdjusted()
        resetGhostPathCoordinates()
        showPlusButton()
        return true
    }

    private fun resetGhostPathCoordinates() {
        ingoingStartCoordinate = null
        ingoingEndCoordinate = null
        ingoingGhostPathColor = Color.GRAY
        outgoingStartCoordinate = null
        outgoingEndCoordinate = null
        outgoingGhostPathColor = Color.GRAY
    }

    private fun createSourceAndDestinationCommandAndVertices(coordinate: PointF) {
        var startPoint = copyPointF(coordinate)
        var endPoint = copyPointF(coordinate)
        var command = createPathCommand(startPoint, endPoint)
        command?.setAsSourcePath()
        createSourceAndDestinationVertices(startPoint, endPoint, command)
    }

    fun createSourceAndDestinationVertices(startPoint: PointF?, endPoint: PointF?, command: DynamicPathCommand?) {
        var sourceVertex = createAndAddVertex(startPoint, command, null)
        var destinationVertex = createAndAddVertex(endPoint, null, command)
        predecessorVertex = sourceVertex
        movingVertex = destinationVertex
        showPlusButton()
    }

    private fun createDestinationCommandAndVertex() {
        var startPoint = vertexStack.last.vertexCenter?.let { center -> copyPointF(center) }
        var command = createPathCommand(startPoint, startPoint)
        createDestinationVertex(startPoint, command)
    }

    fun createDestinationVertex(endPoint: PointF?, command: DynamicPathCommand?) {
        vertexStack.last.setOutgoingPath(command)
        createAndAddVertex(endPoint, null, command)
        setLastMovingAndPredecessorVertex()
    }

    private fun createAndAddVertex(vertexCenter: PointF?, outgoingCommand: DynamicPathCommand?, ingoingCommand: DynamicPathCommand?): Vertex {
        var vertex = Vertex(vertexCenter, outgoingCommand, ingoingCommand)
        vertexStack.add(vertex)
        return vertex
    }

    private fun createPathCommand(startPoint: PointF?, endPoint: PointF?): DynamicPathCommand? {
        if (startPoint == null || endPoint == null) return null
        var currentlyDrawnPath = createSerializablePath(startPoint, endPoint)
        var command = commandFactory.createDynamicPathCommand(toolPaint.paint, currentlyDrawnPath, startPoint, endPoint) as DynamicPathCommand
        commandManager.addCommand(command)
        return command
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
                    return true
                }
            }
        }
        return false
    }

    private fun updateMovingGhostVertices(coordinate: PointF) {
        if (movingVertex != null) {
            movingVertex?.updateVertexCenter(copyPointF(coordinate))
            if (movingVertex?.ingoingPathCommand != null) {
                ingoingStartCoordinate = predecessorVertex?.vertexCenter?.let { center -> copyPointF(center) }
                ingoingEndCoordinate = copyPointF(coordinate)
                ingoingGhostPathColor = movingVertex?.ingoingPathCommand?.paint?.color ?: Color.GRAY
            }
            if (movingVertex?.outgoingPathCommand != null) {
                outgoingStartCoordinate = copyPointF(coordinate)
                outgoingEndCoordinate = successorVertex?.vertexCenter?.let { center -> copyPointF(center) }
                outgoingGhostPathColor = movingVertex?.outgoingPathCommand?.paint?.color ?: Color.GRAY
            }
        }
    }

    private fun updateMovingVertices(coordinate: PointF) {
        if (movingVertex != null) {
            movingVertex?.updateVertexCenter(copyPointF(coordinate))
            if (movingVertex?.ingoingPathCommand != null) {
                var startPoint = predecessorVertex?.vertexCenter?.let { center -> copyPointF(center) }
                var endPoint = copyPointF(coordinate)
                updatePathCommand(startPoint, endPoint, movingVertex?.ingoingPathCommand)
            }
            if (movingVertex?.outgoingPathCommand != null) {
                var startPoint = copyPointF(coordinate)
                var endPoint = successorVertex?.vertexCenter?.let { center -> copyPointF(center) }
                updatePathCommand(startPoint, endPoint, movingVertex?.outgoingPathCommand)
            }
            commandManager.executeAllCommands()
        }
    }

    override fun changePaintColor(color: Int) {
        super.changePaintColor(color)
        if (vertexStack.isEmpty()) return
        vertexStack.last.ingoingPathCommand?.setPaintColor(toolPaint.color)
        commandManager.executeAllCommands()
        brushToolOptionsView.invalidate()
    }

    override fun changePaintStrokeWidth(strokeWidth: Int) {
        super.changePaintStrokeWidth(strokeWidth)
        if (vertexStack.isEmpty()) return
        vertexStack.last.ingoingPathCommand?.setPaintStrokeWidth(toolPaint.strokeWidth)
        commandManager.executeAllCommands()
        brushToolOptionsView.invalidate()
    }

    override fun changePaintStrokeCap(cap: Paint.Cap) {
        super.changePaintStrokeCap(cap)
        if (vertexStack.isEmpty()) return
        vertexStack.last.ingoingPathCommand?.setPaintStrokeCap(toolPaint.strokeCap)
        commandManager.executeAllCommands()
        brushToolOptionsView.invalidate()
    }

    private fun showPlusButton() {
        if (topBarViewHolder != null && topBarViewHolder?.plusButton?.visibility != View.VISIBLE) {
            topBarViewHolder?.showPlusButton()
        }
    }

    private fun hidePlusButton() {
        if (topBarViewHolder != null && topBarViewHolder?.plusButton?.visibility == View.VISIBLE) {
            topBarViewHolder?.hidePlusButton()
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
