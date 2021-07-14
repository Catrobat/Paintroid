/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2021 The Catrobat Team
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

class LineTool(
    private val brushToolOptionsView: BrushToolOptionsView,
    contextCallback: ContextCallback,
    toolOptionsViewController: ToolOptionsVisibilityController,
    toolPaint: ToolPaint,
    workspace: Workspace,
    commandManager: CommandManager,
    override var drawTime: Long
) : BaseTool(contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager) {

    @VisibleForTesting
    var initialEventCoordinate: PointF? = null

    @VisibleForTesting
    var currentCoordinate: PointF? = null

    override var toolType: ToolType = ToolType.LINE

    init {
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
            moveTo(
                initialEventCoordinate?.x ?: return false,
                initialEventCoordinate?.y ?: return false
            )
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
