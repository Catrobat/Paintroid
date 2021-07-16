package org.catrobat.paintroid.tools.implementation

import android.graphics.BlurMaskFilter
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.options.BrushToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsVisibilityController

private const val MAX_ALPHA_VALUE = 255
private const val MAX_NEW_RANGE = 150
private const val MIN_NEW_RANGE = 20

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
    override val toolType: ToolType
        get() = ToolType.WATERCOLOR

    init {
        bitmapPaint.maskFilter = BlurMaskFilter(calcRange(bitmapPaint.alpha), BlurMaskFilter.Blur.INNER)
        bitmapPaint.alpha = MAX_ALPHA_VALUE
    }

    override fun changePaintColor(color: Int) {
        super.changePaintColor(color)

        bitmapPaint.maskFilter = BlurMaskFilter(calcRange(bitmapPaint.alpha), BlurMaskFilter.Blur.INNER)
        bitmapPaint.alpha = MAX_ALPHA_VALUE
    }

    private fun calcRange(value: Int): Float {
        val oldRange = MAX_ALPHA_VALUE
        val newRange = MAX_NEW_RANGE - MIN_NEW_RANGE
        var newValue = value * newRange / oldRange + MIN_NEW_RANGE

        newValue = MAX_NEW_RANGE - newValue + MIN_NEW_RANGE
        return newValue.toFloat()
    }
}
