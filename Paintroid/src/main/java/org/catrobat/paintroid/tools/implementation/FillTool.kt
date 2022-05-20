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
import android.graphics.PointF
import androidx.annotation.VisibleForTesting
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.options.FillToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsViewController

private const val HUNDRED = 100f
const val DEFAULT_TOLERANCE_IN_PERCENT = 12
const val MAX_ABSOLUTE_TOLERANCE = 510

class FillTool(
    fillToolOptionsView: FillToolOptionsView,
    contextCallback: ContextCallback,
    toolOptionsViewController: ToolOptionsViewController,
    toolPaint: ToolPaint,
    workspace: Workspace,
    commandManager: CommandManager,
    override var drawTime: Long
) : BaseTool(
    contextCallback,
    toolOptionsViewController,
    toolPaint,
    workspace,
    commandManager
) {
    @VisibleForTesting
    @JvmField
    var colorTolerance = MAX_ABSOLUTE_TOLERANCE * DEFAULT_TOLERANCE_IN_PERCENT / HUNDRED

    init {
        fillToolOptionsView.setCallback(object : FillToolOptionsView.Callback {
            override fun onColorToleranceChanged(colorTolerance: Int) {
                updateColorTolerance(colorTolerance)
            }
        })
    }

    fun updateColorTolerance(colorToleranceInPercent: Int) {
        colorTolerance = getToleranceAbsoluteValue(colorToleranceInPercent)
    }

    fun getToleranceAbsoluteValue(toleranceInPercent: Int): Float =
        MAX_ABSOLUTE_TOLERANCE * toleranceInPercent / HUNDRED

    override fun handleDown(coordinate: PointF?): Boolean = false

    override fun handleMove(coordinate: PointF?): Boolean = false

    override fun handleUp(coordinate: PointF?): Boolean {
        coordinate ?: return false
        if (!workspace.contains(coordinate)) {
            return false
        }

        val command = commandFactory.createFillCommand(
            coordinate.x.toInt(), coordinate.y.toInt(), toolPaint.paint, colorTolerance
        )
        commandManager.addCommand(command)
        return true
    }

    public override fun resetInternalState() = Unit

    override val toolType: ToolType = ToolType.FILL

    override fun draw(canvas: Canvas) = Unit
}
