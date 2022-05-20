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

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PointF
import org.catrobat.paintroid.colorpicker.OnColorPickedListener
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.options.ToolOptionsViewController

class PipetteTool(
    contextCallback: ContextCallback,
    toolOptionsViewController: ToolOptionsViewController,
    toolPaint: ToolPaint,
    workspace: Workspace,
    commandManager: CommandManager,
    private val listener: OnColorPickedListener
) : BaseTool(contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager) {
    private var surfaceBitmap: Bitmap? = null

    override val toolType: ToolType
        get() = ToolType.PIPETTE

    override var drawTime: Long = 0

    init {
        updateSurfaceBitmap()
    }

    override fun draw(canvas: Canvas) = Unit

    override fun handleDown(coordinate: PointF?): Boolean = setColor(coordinate)

    override fun handleMove(coordinate: PointF?): Boolean = setColor(coordinate)

    override fun handleUp(coordinate: PointF?): Boolean = setColor(coordinate)

    override fun resetInternalState() {
        updateSurfaceBitmap()
    }

    private fun setColor(coordinate: PointF?): Boolean {
        if (coordinate == null || !workspace.contains(coordinate)) {
            return false
        }
        val color =
            surfaceBitmap?.getPixel(coordinate.x.toInt(), coordinate.y.toInt()) ?: return false
        listener.colorChanged(color)
        changePaintColor(color)
        return true
    }

    private fun updateSurfaceBitmap() {
        surfaceBitmap = workspace.bitmapOfAllLayers
    }
}
