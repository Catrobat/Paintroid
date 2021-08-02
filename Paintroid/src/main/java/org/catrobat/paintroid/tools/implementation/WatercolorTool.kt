package org.catrobat.paintroid.tools.implementation

import android.graphics.BlurMaskFilter
import android.graphics.Color
import android.graphics.Paint
import org.catrobat.paintroid.colorpicker.PresetSelectorSlider
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
    //20f start         niedrigste transparenz
    // 150F ende        höchste transparenz

    // 20 = 255      150 = 0
    override val toolType: ToolType
        get() = ToolType.WATERCOLOR

    init {
        bitmapPaint.maskFilter = BlurMaskFilter(calcRange(bitmapPaint.alpha)
                                    , BlurMaskFilter.Blur.INNER)
        bitmapPaint.alpha = 255
    }

    override fun changePaintColor(color: Int) {
        super.changePaintColor(color)

        bitmapPaint.maskFilter = BlurMaskFilter(calcRange(bitmapPaint.alpha)
                                                , BlurMaskFilter.Blur.INNER)
        // previewPaint.alpha = bitmapPaint.alpha
        bitmapPaint.alpha = 255
    }

    private fun calcRange(value : Int): Float {
        val oldRange = 255
        val newRange = (150 - 20)
        var newValue = (((value) * newRange) / oldRange) + 20

        newValue = 150 - newValue + 20
        return newValue.toFloat()
    }
}