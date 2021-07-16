package org.catrobat.paintroid.tools.implementation

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.options.BrushToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsVisibilityController

class WatercolorTool(
    brushToolOptionsView: BrushToolOptionsView,
    contextCallback: ContextCallback,
    toolOptionsViewController: ToolOptionsVisibilityController,
    toolPaint: ToolPaint,
    workspace: Workspace,
    commandManager: CommandManager
) : BrushTool(
    brushToolOptionsView,
    contextCallback,
    toolOptionsViewController,
    toolPaint,
    workspace,
    commandManager
) {
    private val blurFilter = BlurMaskFilter(toolPaint.strokeWidth + 100, BlurMaskFilter.Blur.INNER)

    override val previewPaint: Paint
        get() = Paint().apply {
            set(super.bitmapPaint)
            maskFilter = blurFilter
        }

    override val bitmapPaint: Paint
        get() = Paint().apply {
            set(super.bitmapPaint)
            maskFilter = blurFilter
        }

    override val toolType: ToolType
        get() = ToolType.WATERCOLOR
}