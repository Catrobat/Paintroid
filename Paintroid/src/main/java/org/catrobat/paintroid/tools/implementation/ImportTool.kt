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
import android.graphics.PointF
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.test.espresso.idling.CountingIdlingResource
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.common.ENLARGE_CANVAS_DIALOG_TAG
import org.catrobat.paintroid.dialog.ImportImageCanvasTooLargeDialog
import org.catrobat.paintroid.dialog.ImportImageEnlargeCanvasDialog
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

private const val BUNDLE_TOOL_DRAWING_BITMAP = "BUNDLE_TOOL_DRAWING_BITMAP"

class ImportTool(
    contextCallback: ContextCallback,
    toolOptionsViewController: ToolOptionsViewController,
    toolPaint: ToolPaint,
    workspace: Workspace,
    idlingResource: CountingIdlingResource,
    commandManager: CommandManager,
    override var drawTime: Long,
    private val fragmentManager: FragmentManager
) : BaseToolWithRectangleShape(
    contextCallback, toolOptionsViewController, toolPaint, workspace, idlingResource, commandManager
) {

    override val toolType: ToolType
        get() = ToolType.IMPORTPNG

    override fun handleUpAnimations(coordinate: PointF?) {
        super.handleUp(coordinate)
    }

    override fun handleDownAnimations(coordinate: PointF?) {
        super.handleDown(coordinate)
    }

    override fun toolPositionCoordinates(coordinate: PointF): PointF = coordinate

    init {
        rotationEnabled = true
        maximumBoxResolution =
            metrics.widthPixels * metrics.heightPixels * MAXIMUM_BITMAP_SIZE_FACTOR
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
        if (isImageInsideBitmap()) {
            addImageToCanvas()
        } else {
            val enlargeCanvasDialog = ImportImageEnlargeCanvasDialog()
            enlargeCanvasDialog.show(
                fragmentManager,
                ENLARGE_CANVAS_DIALOG_TAG
            )
        }
    }

    fun onClickOnButtonImplicit(): Boolean {
        if (isImageInsideBitmap()) {
            addImageToCanvas()
            return true
        }
        val enlargeCanvasDialog = ImportImageEnlargeCanvasDialog()
        enlargeCanvasDialog.show(
            fragmentManager,
            ENLARGE_CANVAS_DIALOG_TAG
        )
        return false
    }

    fun setBitmapFromSource(bitmap: Bitmap) {
        super.setBitmap(bitmap)
        val maximumBorderRatioWidth = MAXIMUM_BORDER_RATIO * workspace.width
        val maximumBorderRatioHeight = MAXIMUM_BORDER_RATIO * workspace.height
        val minimumSize = DEFAULT_BOX_RESIZE_MARGIN.toFloat()
        boxWidth = max(minimumSize, min(maximumBorderRatioWidth, bitmap.width.toFloat()))
        boxHeight = max(minimumSize, min(maximumBorderRatioHeight, bitmap.height.toFloat()))
    }

    fun addImageToCanvas() {
        drawingBitmap?.let {
            highlightBox()
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

    fun enlargeCanvas(): Boolean {
        val resizeCoordinateXLeft = min(0, floor(toolPosition.x - boxWidth / 2f).toInt())
        val resizeCoordinateYTop = min(0, floor(toolPosition.y - boxHeight / 2f).toInt())
        val resizeCoordinateXRight = max(workspace.width, ceil(toolPosition.x + boxWidth / 2f - 1f).toInt())
        val resizeCoordinateYBottom = max(workspace.height, ceil(toolPosition.y + boxHeight / 2f - 1f).toInt())
        val enlargeValid = areResizeBordersValid(resizeCoordinateXLeft, resizeCoordinateYTop, resizeCoordinateXRight, resizeCoordinateYBottom)
        if (enlargeValid) {
            val resizeCommand = commandFactory.createCropCommand(
                resizeCoordinateXLeft,
                resizeCoordinateYTop,
                resizeCoordinateXRight,
                resizeCoordinateYBottom,
                maximumBoxResolution.toInt()
            )
            commandManager.addCommand(resizeCommand)
            if (resizeCoordinateXLeft < 0) {
                toolPosition.x = boxWidth / 2f
            }
            if (resizeCoordinateYTop < 0) {
                toolPosition.y = boxHeight / 2f
            }
            addImageToCanvas()
        } else {
            val canvasTooLargeDialog = ImportImageCanvasTooLargeDialog()
            canvasTooLargeDialog.show(
                fragmentManager,
                ENLARGE_CANVAS_DIALOG_TAG
            )
        }
        return enlargeValid
    }

    private fun areResizeBordersValid(resizeBoundWidthXLeft: Int, resizeBoundHeightYTop: Int, resizeBoundWidthXRight: Int, resizeBoundHeightYBottom: Int): Boolean {
        if (resizeBoundWidthXRight < resizeBoundWidthXLeft ||
            resizeBoundHeightYTop > resizeBoundHeightYBottom
        ) {
            return false
        }
        if (resizeBoundWidthXLeft >= workspace.width || min(
                resizeBoundWidthXRight, resizeBoundHeightYBottom
            ) < 0 || resizeBoundHeightYTop >= workspace.height
        ) {
            return false
        }
        if (resizeBoundWidthXLeft == 0 && resizeBoundHeightYTop == 0 && resizeBoundWidthXRight == workspace.width - 1 && resizeBoundHeightYBottom == workspace.height - 1) {
            return false
        }
        val width = resizeBoundWidthXRight - resizeBoundWidthXLeft + 1
        val height = resizeBoundHeightYBottom - resizeBoundHeightYTop + 1
        return width * height <= maximumBoxResolution
    }

    private fun isImageInsideBitmap(): Boolean {
        val canvasHeight = workspace.height.toFloat()
        val canvasWidth = workspace.width.toFloat()
        val imageTopLeft = PointF(toolPosition.x - boxWidth / 2, toolPosition.y - boxHeight / 2)
        val imageBottomRight = PointF(toolPosition.x + boxWidth / 2, toolPosition.y + boxHeight / 2)

        return imageTopLeft.x >= 0 && imageTopLeft.y >= 0 && imageBottomRight.x <= canvasWidth && imageBottomRight.y <= canvasHeight
    }
}
