/*
 * Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
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

import android.graphics.BlurMaskFilter
import androidx.test.espresso.idling.CountingIdlingResource
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.options.BrushToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsViewController

const val MAX_ALPHA_VALUE = 255
const val MAX_NEW_RANGE = 150
const val MIN_NEW_RANGE = 20

class WatercolorTool(
    brushToolOptionsView: BrushToolOptionsView,
    contextCallback: ContextCallback,
    toolOptionsViewController: ToolOptionsViewController,
    toolPaint: ToolPaint,
    workspace: Workspace,
    idlingResource: CountingIdlingResource,
    commandManager: CommandManager,
    drawTime: Long
) : BrushTool(
    brushToolOptionsView,
    contextCallback,
    toolOptionsViewController,
    toolPaint,
    workspace,
    idlingResource,
    commandManager,
    drawTime
) {
    override val toolType: ToolType
        get() = ToolType.WATERCOLOR

    init {
        bitmapPaint.maskFilter = BlurMaskFilter(calcRange(bitmapPaint.alpha), BlurMaskFilter.Blur.INNER)
        previewPaint.maskFilter = BlurMaskFilter(calcRange(previewPaint.alpha), BlurMaskFilter.Blur.INNER)
    }

    override fun changePaintColor(color: Int, invalidate: Boolean) {
        super.changePaintColor(color, invalidate)

        if (invalidate) brushToolOptionsView.invalidate()
        bitmapPaint.maskFilter = BlurMaskFilter(calcRange(bitmapPaint.alpha), BlurMaskFilter.Blur.INNER)
        previewPaint.maskFilter = BlurMaskFilter(calcRange(previewPaint.alpha), BlurMaskFilter.Blur.INNER)
    }
    companion object {
        fun calcRange(value: Int): Float {
            val oldRange = MAX_ALPHA_VALUE
            val newRange = MAX_NEW_RANGE - MIN_NEW_RANGE
            var newValue = value * newRange / oldRange + MIN_NEW_RANGE

            newValue = MAX_NEW_RANGE - newValue + MIN_NEW_RANGE
            return newValue.toFloat()
        }
    }
}
