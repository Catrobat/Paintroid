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
import android.os.Bundle
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import kotlin.math.max
import kotlin.math.min

private const val BUNDLE_TOOL_DRAWING_BITMAP = "BUNDLE_TOOL_DRAWING_BITMAP"

class ImportTool(
    contextCallback: ContextCallback,
    toolOptionsViewController: ToolOptionsViewController,
    toolPaint: ToolPaint,
    workspace: Workspace,
    commandManager: CommandManager,
    override var drawTime: Long
) : BaseToolWithRectangleShape(
    contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager
) {

    override val toolType: ToolType
        get() = ToolType.IMPORTPNG

    init {
        rotationEnabled = true
    }

    override fun drawShape(canvas: Canvas) {
        if (drawingBitmap != null) {
            super.drawShape(canvas)
        }
    }

    override fun onSaveInstanceState(bundle: Bundle?) {
        super.onSaveInstanceState(bundle)
        bundle?.putParcelable(BUNDLE_TOOL_DRAWING_BITMAP, drawingBitmap)
    }

    override fun onRestoreInstanceState(bundle: Bundle?) {
        super.onRestoreInstanceState(bundle)
        val bitmap = bundle?.getParcelable<Bitmap>(BUNDLE_TOOL_DRAWING_BITMAP)
        drawingBitmap = bitmap
    }

    override fun onClickOnButton() {
        drawingBitmap?.let {
            highlightBox()
            val command = commandFactory.createStampCommand(
                it,
                toolPosition,
                boxWidth,
                boxHeight,
                boxRotation
            )
            commandManager.addCommand(command)
        }
    }

    fun setBitmapFromSource(bitmap: Bitmap) {
        super.setBitmap(bitmap)
        val maximumBorderRatioWidth = MAXIMUM_BORDER_RATIO * workspace.width
        val maximumBorderRatioHeight = MAXIMUM_BORDER_RATIO * workspace.height
        val minimumSize = DEFAULT_BOX_RESIZE_MARGIN.toFloat()
        boxWidth = max(minimumSize, min(maximumBorderRatioWidth, bitmap.width.toFloat()))
        boxHeight = max(minimumSize, min(maximumBorderRatioHeight, bitmap.height.toFloat()))
    }
}
