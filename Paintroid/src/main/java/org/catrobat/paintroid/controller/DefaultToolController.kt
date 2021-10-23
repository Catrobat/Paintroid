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
package org.catrobat.paintroid.controller

import android.graphics.Bitmap
import android.os.Bundle
import org.catrobat.paintroid.colorpicker.OnColorPickedListener
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.Tool
import org.catrobat.paintroid.tools.Tool.StateChange
import org.catrobat.paintroid.tools.ToolFactory
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape
import org.catrobat.paintroid.tools.implementation.ImportTool
import org.catrobat.paintroid.tools.options.ToolOptionsViewController

class DefaultToolController(
    private val toolReference: ToolReference,
    private val toolOptionsViewController: ToolOptionsViewController,
    private val toolFactory: ToolFactory,
    private val commandManager: CommandManager,
    private val workspace: Workspace,
    private val toolPaint: ToolPaint,
    private val contextCallback: ContextCallback
) : ToolController {
    private lateinit var onColorPickedListener: OnColorPickedListener
    private val toolList =
        hashSetOf(
            ToolType.TEXT,
            ToolType.TRANSFORM,
            ToolType.IMPORTPNG,
            ToolType.SHAPE,
            ToolType.LINE
        )

    override val isDefaultTool: Boolean
        get() = toolReference.tool?.toolType == ToolType.BRUSH

    override val toolColor: Int?
        get() = toolReference.tool?.drawPaint?.color

    override val currentTool: Tool?
        get() = toolReference.tool

    override val toolType: ToolType?
        get() = toolReference.tool?.toolType

    private fun createAndSetupTool(toolType: ToolType): Tool {
        if (toolType != ToolType.HAND) {
            toolOptionsViewController.removeToolViews()
        }
        if (toolList.contains(toolType)) {
            toolOptionsViewController.showCheckmark()
        } else {
            toolOptionsViewController.hideCheckmark()
        }
        val tool: Tool = toolFactory.createTool(
            toolType,
            toolOptionsViewController,
            commandManager,
            workspace,
            toolPaint,
            contextCallback,
            onColorPickedListener
        )
        if (toolType != ToolType.HAND) {
            toolOptionsViewController.resetToOrigin()
            toolOptionsViewController.show()
        }
        return tool
    }

    override fun setOnColorPickedListener(onColorPickedListener: OnColorPickedListener) {
        this.onColorPickedListener = onColorPickedListener
    }

    override fun switchTool(toolType: ToolType, backPressed: Boolean) {
        switchTool(createAndSetupTool(toolType), backPressed)
    }

    override fun hideToolOptionsView() {
        toolOptionsViewController.hide()
    }

    override fun showToolOptionsView() {
        toolOptionsViewController.show()
    }

    override fun toolOptionsViewVisible(): Boolean = toolOptionsViewController.isVisible

    override fun resetToolInternalState() {
        toolReference.tool?.resetInternalState(StateChange.RESET_INTERNAL_STATE)
    }

    override fun resetToolInternalStateOnImageLoaded() {
        toolReference.tool?.resetInternalState(StateChange.NEW_IMAGE_LOADED)
    }

    private fun switchTool(tool: Tool, backPressed: Boolean) {
        val currentTool = toolReference.tool
        val currentToolType = currentTool?.toolType
        if (toolList.contains(currentToolType) && !backPressed) {
            val toolToApply = currentTool as BaseToolWithShape
            toolToApply.onClickOnButton()
        }
        if (currentTool?.toolType == tool.toolType) {
            val toolBundle = Bundle()
            currentTool.onSaveInstanceState(toolBundle)
            tool.onRestoreInstanceState(toolBundle)
        }
        toolReference.tool = tool
        workspace.invalidate()
    }

    override fun disableToolOptionsView() {
        toolOptionsViewController.disable()
    }

    override fun enableToolOptionsView() {
        toolOptionsViewController.enable()
    }

    override fun createTool() {
        val currentTool = toolReference.tool
        if (currentTool == null) {
            toolReference.tool = createAndSetupTool(ToolType.BRUSH)
        } else {
            val bundle = Bundle()
            toolReference.tool?.onSaveInstanceState(bundle)
            toolReference.tool = createAndSetupTool(currentTool.toolType)
            toolReference.tool?.onRestoreInstanceState(bundle)
        }
        workspace.invalidate()
    }

    override fun toggleToolOptionsView() {
        if (toolOptionsViewController.isVisible) {
            toolOptionsViewController.hide()
        } else {
            toolOptionsViewController.show()
        }
    }

    override fun hasToolOptionsView(): Boolean = toolType?.hasOptions() ?: false

    override fun setBitmapFromSource(bitmap: Bitmap?) {
        bitmap ?: return
        val importTool = toolReference.tool as ImportTool?
        importTool?.setBitmapFromSource(bitmap)
    }
}
