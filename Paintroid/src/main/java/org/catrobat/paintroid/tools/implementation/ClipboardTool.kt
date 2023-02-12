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

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import androidx.test.espresso.idling.CountingIdlingResource
import org.catrobat.paintroid.R
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.options.ClipboardToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsViewController

private const val BUNDLE_TOOL_READY_FOR_PASTE = "BUNDLE_TOOL_READY_FOR_PASTE"
private const val BUNDLE_TOOL_DRAWING_BITMAP = "BUNDLE_TOOL_DRAWING_BITMAP"

class ClipboardTool(
    clipboardToolOptionsView: ClipboardToolOptionsView,
    contextCallback: ContextCallback,
    toolOptionsViewController: ToolOptionsViewController,
    toolPaint: ToolPaint,
    workspace: Workspace,
    idlingResource: CountingIdlingResource,
    commandManager: CommandManager,
    override var drawTime: Long
) : BaseToolWithRectangleShape(
    contextCallback, toolOptionsViewController, toolPaint, workspace, idlingResource, commandManager
) {
    private val clipboardToolOptionsView: ClipboardToolOptionsView
    private var readyForPaste = false
    private val isDrawingBitmapReusable: Boolean
        get() {
            drawingBitmap?.let {
                return it.width == boxWidth.toInt() && it.height == boxHeight.toInt()
            }
            return false
        }

    override val toolType: ToolType
        get() = ToolType.CLIPBOARD

    override fun toolPositionCoordinates(coordinate: PointF): PointF = coordinate

    init {
        rotationEnabled = true
        this.clipboardToolOptionsView = clipboardToolOptionsView
        setBitmap(Bitmap.createBitmap(boxWidth.toInt(), boxHeight.toInt(), Bitmap.Config.ARGB_8888))
        val callback: ClipboardToolOptionsView.Callback = object : ClipboardToolOptionsView.Callback {
            override fun copyClicked() {
                highlightBox()
                copyBoxContent()
                this@ClipboardTool.clipboardToolOptionsView.enablePaste(true)
            }

            override fun cutClicked() {
                highlightBox()
                copyBoxContent()
                cutBoxContent()
                this@ClipboardTool.clipboardToolOptionsView.enablePaste(true)
            }

            override fun pasteClicked() {
                highlightBox()
                pasteBoxContent()
            }
        }
        clipboardToolOptionsView.setCallback(callback)
        toolOptionsViewController.showDelayed()
    }

    fun copyBoxContent() {
        if (isDrawingBitmapReusable) {
            drawingBitmap?.eraseColor(Color.TRANSPARENT)
        } else {
            drawingBitmap =
                Bitmap.createBitmap(boxWidth.toInt(), boxHeight.toInt(), Bitmap.Config.ARGB_8888)
        }
        val layerBitmap = workspace.bitmapOfCurrentLayer
        if (layerBitmap != null) {
            drawingBitmap?.let {
                Canvas(it).apply {
                    translate(-toolPosition.x + boxWidth / 2, -toolPosition.y + boxHeight / 2)
                    rotate(-boxRotation, toolPosition.x, toolPosition.y)
                    drawBitmap(layerBitmap, 0f, 0f, null)
                }
            }
        }
        readyForPaste = true
    }

    private fun pasteBoxContent() {
        drawingBitmap?.let {
            val command = commandFactory.createClipboardCommand(
                it,
                toolPosition,
                boxWidth,
                boxHeight,
                boxRotation
            )
            commandManager.addCommand(command)
        }
    }

    private fun cutBoxContent() {
        val command =
            commandFactory.createCutCommand(toolPosition, boxWidth, boxHeight, boxRotation)
        commandManager.addCommand(command)
    }

    override fun onClickOnButton() {
        if (!readyForPaste || drawingBitmap == null) {
            contextCallback.showNotification(R.string.clipboard_tool_copy_hint)
        } else if (boxIntersectsWorkspace()) {
            pasteBoxContent()
            highlightBox()
        }
    }

    override fun resetInternalState() = Unit
    override fun onSaveInstanceState(bundle: Bundle?) {
        super.onSaveInstanceState(bundle)
        bundle?.putParcelable(BUNDLE_TOOL_DRAWING_BITMAP, drawingBitmap)
        bundle?.putBoolean(BUNDLE_TOOL_READY_FOR_PASTE, readyForPaste)
    }

    override fun onRestoreInstanceState(bundle: Bundle?) {
        super.onRestoreInstanceState(bundle)
        bundle?.apply {
            readyForPaste = getBoolean(BUNDLE_TOOL_READY_FOR_PASTE, readyForPaste)
            drawingBitmap = getParcelable(BUNDLE_TOOL_DRAWING_BITMAP)
        }
        clipboardToolOptionsView.enablePaste(readyForPaste)
    }
}
