package org.catrobat.paintroid.tools.implementation

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import androidx.annotation.VisibleForTesting
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.common.CommonBrushChangedListener
import org.catrobat.paintroid.tools.common.CommonBrushPreviewListener
import org.catrobat.paintroid.tools.options.BrushToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsVisibilityController

class LineTool(private val brushToolOptionsView: BrushToolOptionsView, contextCallback: ContextCallback,
               toolOptionsViewController: ToolOptionsVisibilityController, toolPaint: ToolPaint,
               workspace: Workspace, commandManager: CommandManager)
    : BaseTool(contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager) {

    @VisibleForTesting
    var initialEventCoordinate: PointF? = null
    @VisibleForTesting
    var currentCoordinate: PointF? = null

    init {
        brushToolOptionsView.setBrushChangedListener(CommonBrushChangedListener(this))
        brushToolOptionsView.setBrushPreviewListener(CommonBrushPreviewListener(toolPaint, toolType))
        brushToolOptionsView.setCurrentPaint(toolPaint.paint)
    }

    override fun draw(canvas: Canvas) {
        initialEventCoordinate?.let { initialCoordinate ->
            currentCoordinate?.let { currentCoordinate ->
                canvas.run {
                    save()
                    clipRect(0, 0, workspace.width, workspace.height)
                    drawLine(initialCoordinate.x,
                             initialCoordinate.y, currentCoordinate.x,
                             currentCoordinate.y, toolPaint.previewPaint)
                    restore()
                }
            }
        }
    }

    override fun getToolType(): ToolType {
        return ToolType.LINE
    }

    override fun handleDown(coordinate: PointF?): Boolean {
        coordinate ?: return false
        initialEventCoordinate = PointF(coordinate.x, coordinate.y)
        previousEventCoordinate = PointF(coordinate.x, coordinate.y)
        return true
    }

    override fun handleMove(coordinate: PointF?): Boolean {
        coordinate ?: return false
        currentCoordinate = PointF(coordinate.x, coordinate.y)
        return true
    }

    override fun handleUp(coordinate: PointF?): Boolean {
        if (initialEventCoordinate == null || previousEventCoordinate == null || coordinate == null) {
            return false
        }
        val bounds = RectF()
        val finalPath = Path().apply {
            moveTo(initialEventCoordinate?.x ?: return false,
                   initialEventCoordinate?.y ?: return false)
            lineTo(coordinate.x, coordinate.y)
            computeBounds(bounds, true)
        }
        bounds.inset(-toolPaint.strokeWidth, -toolPaint.strokeWidth)

        if (workspace.intersectsWith(bounds)) {
            val command = commandFactory.createPathCommand(toolPaint.paint, finalPath)
            commandManager.addCommand(command)
        }
        resetInternalState()
        return true
    }

    override fun resetInternalState() {
        initialEventCoordinate = null
        currentCoordinate = null
    }

    override fun changePaintColor(color: Int) {
        super.changePaintColor(color)
        brushToolOptionsView.invalidate()
    }
}