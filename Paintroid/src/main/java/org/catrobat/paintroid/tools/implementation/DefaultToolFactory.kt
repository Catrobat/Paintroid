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

import org.catrobat.paintroid.colorpicker.OnColorPickedListener
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.Tool
import org.catrobat.paintroid.tools.ToolFactory
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import org.catrobat.paintroid.ui.tools.DefaultBrushToolOptionsView
import org.catrobat.paintroid.ui.tools.DefaultFillToolOptionsView
import org.catrobat.paintroid.ui.tools.DefaultShapeToolOptionsView
import org.catrobat.paintroid.ui.tools.DefaultSprayToolOptionsView
import org.catrobat.paintroid.ui.tools.DefaultStampToolOptionsView
import org.catrobat.paintroid.ui.tools.DefaultTextToolOptionsView
import org.catrobat.paintroid.ui.tools.DefaultTransformToolOptionsView
import org.catrobat.paintroid.ui.tools.DefaultSmudgeToolOptionsView

private const val DRAW_TIME_INIT: Long = 30_000_000

@SuppressWarnings("LongMethod")
class DefaultToolFactory : ToolFactory {
    override fun createTool(
        toolType: ToolType,
        toolOptionsViewController: ToolOptionsViewController,
        commandManager: CommandManager,
        workspace: Workspace,
        toolPaint: ToolPaint,
        contextCallback: ContextCallback,
        onColorPickedListener: OnColorPickedListener
    ): Tool {
        val toolLayout = toolOptionsViewController.toolSpecificOptionsLayout
        return when (toolType) {
            ToolType.CURSOR -> CursorTool(
                DefaultBrushToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.STAMP -> StampTool(
                DefaultStampToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.IMPORTPNG -> ImportTool(
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.PIPETTE -> PipetteTool(
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                commandManager,
                onColorPickedListener,
            )
            ToolType.FILL -> FillTool(
                DefaultFillToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.TRANSFORM -> TransformTool(
                DefaultTransformToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.SHAPE -> ShapeTool(
                DefaultShapeToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.ERASER -> EraserTool(
                DefaultBrushToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.LINE -> LineTool(
                DefaultBrushToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.TEXT -> TextTool(
                DefaultTextToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.HAND -> HandTool(
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.SPRAY -> SprayTool(
                DefaultSprayToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.WATERCOLOR -> WatercolorTool(
                DefaultBrushToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.SMUDGE -> SmudgeTool(
                DefaultSmudgeToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                commandManager
            )
            else -> BrushTool(
                DefaultBrushToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                commandManager,
                DRAW_TIME_INIT
            )
        }
    }
}
