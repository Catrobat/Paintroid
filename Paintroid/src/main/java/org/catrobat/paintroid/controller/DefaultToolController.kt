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
package org.catrobat.paintroid.controller

import android.graphics.Bitmap
import android.graphics.PointF
import android.os.Bundle
import android.view.View
import androidx.test.espresso.idling.CountingIdlingResource
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
import org.catrobat.paintroid.tools.implementation.ClipboardTool
import org.catrobat.paintroid.tools.implementation.ClippingTool
import org.catrobat.paintroid.tools.implementation.EraserTool
import org.catrobat.paintroid.tools.implementation.ImportTool
import org.catrobat.paintroid.tools.implementation.LineTool
import org.catrobat.paintroid.tools.implementation.ShapeTool
import org.catrobat.paintroid.tools.implementation.SprayTool
import org.catrobat.paintroid.tools.implementation.TextTool
import org.catrobat.paintroid.tools.options.ToolOptionsViewController

class DefaultToolController(
    private val toolReference: ToolReference,
    private val toolOptionsViewController: ToolOptionsViewController,
    private val toolFactory: ToolFactory,
    private val commandManager: CommandManager,
    private val workspace: Workspace,
    private val idlingResource: CountingIdlingResource,
    private val toolPaint: ToolPaint,
    private val contextCallback: ContextCallback
) : ToolController {
    private lateinit var onColorPickedListener: OnColorPickedListener

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
        } else if (toolType != ToolType.CLIP) {
            toolOptionsViewController.removeToolViews()
        }

        if (toolsThatUseCheckmark.contains(toolType)) {
            toolOptionsViewController.showCheckmark()
        } else {
            toolOptionsViewController.hideCheckmark()
        }

        if (toolReference.tool?.toolType == ToolType.SPRAY) {
            (currentTool as SprayTool).resetRadiusToStrokeWidth()
        }

        val tool: Tool = toolFactory.createTool(
            toolType,
            toolOptionsViewController,
            commandManager,
            workspace,
            idlingResource,
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

    override fun switchTool(toolType: ToolType) {
        switchTool(createAndSetupTool(toolType))
    }

    override fun hideToolOptionsView() {
        when (toolReference.tool?.toolType) {
            ToolType.TEXT -> (toolReference.tool as TextTool).hideTextToolLayout()
            ToolType.SHAPE -> (toolReference.tool as ShapeTool).changeShapeToolLayoutVisibility(true, true)
            ToolType.CLIPBOARD -> (toolReference.tool as ClipboardTool).changeClipboardToolLayoutVisibility(true, true)
            else -> toolOptionsViewController.hide()
        }
    }

    override fun hideToolOptionsViewForShapeTools() {
        when (toolReference.tool?.toolType) {
            ToolType.TEXT -> (toolReference.tool as TextTool).hideTextToolLayout()
            ToolType.SHAPE -> (toolReference.tool as ShapeTool).changeShapeToolLayoutVisibility(true, true)
            ToolType.CLIPBOARD -> (toolReference.tool as ClipboardTool).changeClipboardToolLayoutVisibility(true, true)
            else -> {}
        }
    }

    override fun showToolOptionsView() {
        toolOptionsViewController.show()
        when (toolReference.tool?.toolType) {
            ToolType.TEXT -> (toolReference.tool as TextTool).showTextToolLayout()
            ToolType.SHAPE -> (toolReference.tool as ShapeTool).changeShapeToolLayoutVisibility(false, true)
            ToolType.CLIPBOARD -> (toolReference.tool as ClipboardTool).changeClipboardToolLayoutVisibility(false, true)
            else -> {}
        }
    }

    override fun toolOptionsViewVisible(): Boolean = toolOptionsViewController.isVisible

    override fun resetToolInternalState() {
        toolReference.tool?.resetInternalState(StateChange.RESET_INTERNAL_STATE)
    }

    override fun resetToolInternalStateOnImageLoaded() {
        toolReference.tool?.resetInternalState(StateChange.NEW_IMAGE_LOADED)
    }

    private fun switchTool(tool: Tool) {
        val currentTool = toolReference.tool
        val currentToolType = currentTool?.toolType
        if (currentToolType == ToolType.ERASER) {
            (currentTool as EraserTool).setSavedColor()
        }
        currentToolType?.let { hidePlusIfShown(it) }

        if (currentToolType == tool.toolType) {
            val toolBundle = Bundle()
            currentTool.onSaveInstanceState(toolBundle)
            tool.onRestoreInstanceState(toolBundle)
        }
        toolReference.tool = tool
        workspace.invalidate()
    }

    override fun adjustClippingToolOnBackPressed(backPressed: Boolean) {
        val clippingTool = currentTool as ClippingTool
        if (backPressed) {
            if (clippingTool.areaClosed) {
                clippingTool.handleDown(PointF(0f, 0f))
                clippingTool.wasRecentlyApplied = true
                clippingTool.resetInternalState(StateChange.NEW_IMAGE_LOADED)
            }
        } else {
            clippingTool.onClickOnButton()
        }
    }

    private fun hidePlusIfShown(currentToolType: ToolType) {
        if (currentToolType == ToolType.LINE && null != LineTool.topBarViewHolder) {
            val visibility = LineTool.topBarViewHolder?.plusButton?.visibility == View.VISIBLE
            if (visibility) {
                LineTool.topBarViewHolder?.plusButton?.visibility = View.GONE
            }
        }
    }

    override fun disableToolOptionsView() {
        if (toolType != ToolType.IMPORTPNG) {
            toolOptionsViewController.disable()
        }
    }

    override fun enableToolOptionsView() {
        toolOptionsViewController.enable()
    }

    override fun adaptLayoutForFullScreen() {
        if (toolReference.tool?.toolType != ToolType.HAND) {
            toolOptionsViewController.show(true)
        }
    }

    override fun enableHideOption() {
        toolOptionsViewController.enableHide()
    }

    override fun disableHideOption() {
        if (toolType != ToolType.IMPORTPNG) {
            toolOptionsViewController.disableHide()
        }
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
