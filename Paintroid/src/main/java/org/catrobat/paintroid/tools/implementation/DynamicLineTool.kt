package org.catrobat.paintroid.tools.implementation

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.view.View
import androidx.annotation.ColorInt
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

    private var ingoingGhostPathPaint: Paint = Paint()
    private var outgoingGhostPathPaint: Paint = Paint()

    private var outgoingStartCoordinate: PointF? = null
    private var outgoingEndCoordinate: PointF? = null

    override var toolType: ToolType = ToolType.DYNAMICLINE

    var vertexStack: Deque<Vertex> = ArrayDeque()

    var movingVertex: Vertex? = null
    var predecessorVertex: Vertex? = null
    var successorVertex: Vertex? = null

    var addNewPath: Boolean = false
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
        drawGhostPath(ingoingStartCoordinate, ingoingEndCoordinate, canvas, workspace, ingoingGhostPathPaint)
        drawGhostPath(outgoingStartCoordinate, outgoingEndCoordinate, canvas, workspace, outgoingGhostPathPaint)
        drawShape(canvas)
    }

    private fun drawGhostPath(startCoordinate: PointF?, endCoordinate: PointF?, canvas: Canvas, workspace: Workspace, paint: Paint) {
        startCoordinate?.let { start ->
            endCoordinate?.let { end ->
                canvas.run {
                    save()
                    clipRect(0, 0, workspace.width, workspace.height)
                    drawLine(start.x, start.y, end.x, end.y, paint)
                    restore()
                }
            }
        }
    }
    @Synchronized override fun drawShape(canvas: Canvas) {
        vertexStack.forEach { vertex ->
            vertex?.let { vertex ->
                val cx = vertex.vertexCenter?.x
                val cy = vertex.vertexCenter?.y
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
        super.changePaintColor(command.paint.color, false)
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
        val startPoint = redoCommand?.startPoint?.let { copyPointF(it) }
        val endPoint = redoCommand?.endPoint?.let { copyPointF(it) }
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
        val firstRedoCommand = commandManager.getFirstRedoCommand() ?: return
        if (firstRedoCommand is DynamicPathCommand &&
            firstRedoCommand.startPoint != vertexStack.last.vertexCenter) {
            commandManager.clearRedoCommandList()
            undoRecentlyClicked = false
        }
    }

    override fun handleMove(coordinate: PointF?, shouldAnimate: Boolean): Boolean {
        coordinate ?: return false
        super.handleMove(coordinate, shouldAnimate)
        if (shouldAnimate) {
            hideToolOptions()
        }
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
        ingoingGhostPathPaint = Paint()
        outgoingStartCoordinate = null
        outgoingEndCoordinate = null
        outgoingGhostPathPaint = Paint()
    }

    private fun createSourceAndDestinationCommandAndVertices(coordinate: PointF) {
        val startPoint = copyPointF(coordinate)
        val endPoint = copyPointF(coordinate)
        val command = createPathCommand(startPoint, endPoint)
        command?.setAsSourcePath()
        createSourceAndDestinationVertices(startPoint, endPoint, command)
    }

    fun createSourceAndDestinationVertices(startPoint: PointF?, endPoint: PointF?, command: DynamicPathCommand?) {
        val sourceVertex = createAndAddVertex(startPoint, command, null)
        val destinationVertex = createAndAddVertex(endPoint, null, command)
        predecessorVertex = sourceVertex
        movingVertex = destinationVertex
        showPlusButton()
    }

    private fun createDestinationCommandAndVertex() {
        val startPoint = vertexStack.last.vertexCenter?.let { center -> copyPointF(center) }
        val command = createPathCommand(startPoint, startPoint)
        createDestinationVertex(startPoint, command)
    }

    fun createDestinationVertex(endPoint: PointF?, command: DynamicPathCommand?) {
        vertexStack.last.setOutgoingPath(command)
        createAndAddVertex(endPoint, null, command)
        setLastMovingAndPredecessorVertex()
    }

    private fun createAndAddVertex(vertexCenter: PointF?, outgoingCommand: DynamicPathCommand?, ingoingCommand: DynamicPathCommand?): Vertex {
        val vertex = Vertex(vertexCenter, outgoingCommand, ingoingCommand)
        vertexStack.add(vertex)
        return vertex
    }

    private fun createPathCommand(startPoint: PointF?, endPoint: PointF?): DynamicPathCommand? {
        if (startPoint == null || endPoint == null) return null
        val currentlyDrawnPath = createSerializablePath(startPoint, endPoint)
        val command = commandFactory.createDynamicPathCommand(toolPaint.paint, currentlyDrawnPath, startPoint, endPoint) as DynamicPathCommand
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
        var movingVertexCenter: PointF = getMovingVertexCenter(coordinate)
        if (movingVertex != null) {
            movingVertex?.updateVertexCenter(copyPointF(movingVertexCenter))
            if (movingVertex?.ingoingPathCommand != null) {
                ingoingStartCoordinate = predecessorVertex?.vertexCenter?.let { center -> copyPointF(center) }
                ingoingEndCoordinate = copyPointF(movingVertexCenter)
                ingoingGhostPathPaint = createGhostPathPaint(movingVertex?.ingoingPathCommand?.paint)
            }
            if (movingVertex?.outgoingPathCommand != null) {
                outgoingStartCoordinate = copyPointF(movingVertexCenter)
                outgoingEndCoordinate = successorVertex?.vertexCenter?.let { center -> copyPointF(center) }
                outgoingGhostPathPaint = createGhostPathPaint(movingVertex?.outgoingPathCommand?.paint)
            }
        }
    }

    private fun getMovingVertexCenter(coordinate: PointF): PointF {
        var insidePoint: PointF? = getInsidePoint()
        var outsidePoint = copyPointF(coordinate)
        return calculateMovingVertexCenter(insidePoint, outsidePoint)
    }

    private fun calculateMovingVertexCenter(insidePoint: PointF?, outsidePoint: PointF): PointF {
        if (insidePoint == null) return outsidePoint

        var slope = (outsidePoint.y - insidePoint.y) / (outsidePoint.x - insidePoint.x)
        val yIntercept = insidePoint.y - slope * insidePoint.x
        val surfaceHeight = workspace.height.toFloat()
        val surfaceWidth = workspace.width.toFloat()

        if (outsidePoint.y < 0) {
            val x = -yIntercept / slope
            if (x in 0.0f..surfaceWidth) {
                return PointF(x, 0f)
            }
        }

        if (outsidePoint.y > surfaceHeight) {
            val x = (surfaceHeight - yIntercept) / slope
            if (x in 0.0f..surfaceWidth) {
                return PointF(x, surfaceHeight)
            }
        }

        if (outsidePoint.x < 0 && yIntercept in 0.0f..surfaceHeight) {
                return PointF(0f, yIntercept)
        }

        if (outsidePoint.x > surfaceWidth) {
            val y = slope * surfaceWidth + yIntercept
            if (y in 0.0f..surfaceHeight) {
                return PointF(surfaceWidth, y)
            }
        }

        return outsidePoint
    }

    private fun getInsidePoint(): PointF? {
        return if (predecessorVertex != null) {
            predecessorVertex?.vertexCenter?.let { copyPointF(it) }
        } else {
            successorVertex?.vertexCenter?.let { copyPointF(it) }
        }
    }

    private fun updateMovingVertices(coordinate: PointF) {
        var movingVertexCenter = getMovingVertexCenter(coordinate)
        if (movingVertex != null) {
            movingVertex?.updateVertexCenter(copyPointF(movingVertexCenter))
            if (movingVertex?.ingoingPathCommand != null) {
                val startPoint = predecessorVertex?.vertexCenter?.let { center -> copyPointF(center) }
                val endPoint = copyPointF(movingVertexCenter)
                updatePathCommand(startPoint, endPoint, movingVertex?.ingoingPathCommand)
            }
            if (movingVertex?.outgoingPathCommand != null) {
                val startPoint = copyPointF(movingVertexCenter)
                val endPoint = successorVertex?.vertexCenter?.let { center -> copyPointF(center) }
                updatePathCommand(startPoint, endPoint, movingVertex?.outgoingPathCommand)
            }
            commandManager.executeAllCommands()
        }
    }

    override fun changePaintColor(@ColorInt color: Int, invalidate: Boolean) {
        super.changePaintColor(color, false)
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

    private fun createGhostPathPaint(originalPaint: Paint?): Paint {
        val paint = Paint()
        if (originalPaint != null) {
            paint.run {
                style = Paint.Style.FILL
                color = originalPaint.color
                alpha = GHOST_PAINT_ALPHA
                strokeWidth = originalPaint.strokeWidth
                strokeCap = originalPaint.strokeCap
            }
        } else {
            paint.run {
                style = Paint.Style.FILL
                color = Color.GRAY
                alpha = GHOST_PAINT_ALPHA
                strokeWidth = GHOST_STROKE_WIDTH
            }
        }
        return paint
    }

    companion object {
        var topBarViewHolder: TopBarViewHolder? = null
        const val TAG = "DynamicLineTool"
        private const val GHOST_PAINT_ALPHA = 128
        private const val GHOST_STROKE_WIDTH = 16f
    }
}
