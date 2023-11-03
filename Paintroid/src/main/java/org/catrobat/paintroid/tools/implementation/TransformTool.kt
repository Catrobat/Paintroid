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

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import androidx.annotation.VisibleForTesting
import androidx.test.espresso.idling.CountingIdlingResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.catrobat.paintroid.R
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.command.implementation.FlipCommand
import org.catrobat.paintroid.command.implementation.RotateCommand
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.helper.CropAlgorithm
import org.catrobat.paintroid.tools.helper.DefaultNumberRangeFilter
import org.catrobat.paintroid.tools.helper.JavaCropAlgorithm
import org.catrobat.paintroid.tools.helper.SetCenterAlgorithm
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import org.catrobat.paintroid.tools.options.ToolOptionsVisibilityController
import org.catrobat.paintroid.tools.options.TransformToolOptionsView
import org.catrobat.paintroid.ui.tools.NumberRangeFilter
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

@VisibleForTesting
const val MAXIMUM_BITMAP_SIZE_FACTOR = 4.0f
private const val START_ZOOM_FACTOR = 0.95f
private const val SIDES = 4
private const val CONSTANT_1 = 10
private const val RIGHT_ANGLE = 90f
private const val HUNDRED = 100f
private const val ROTATION_ENABLED = false
private const val RESIZE_POINTS_VISIBLE = false
private const val RESPECT_MAXIMUM_BORDER_RATIO = false
private const val RESPECT_MAXIMUM_BOX_RESOLUTION = true
private const val DEFAULT_CURSOR_STROKE_WIDTH = 5f
private const val MINIMAL_CURSOR_STROKE_WIDTH = 1f
private const val MAXIMAL_CURSOR_STROKE_WIDTH = 10f
private const val CURSOR_LINES = 4

class TransformTool(
    private val transformToolOptionsView: TransformToolOptionsView,
    contextCallback: ContextCallback,
    toolOptionsViewController: ToolOptionsViewController,
    toolPaint: ToolPaint,
    workspace: Workspace,
    idlingResource: CountingIdlingResource,
    commandManager: CommandManager,
    override var drawTime: Long
) : BaseToolWithRectangleShape(
    contextCallback,
    toolOptionsViewController,
    toolPaint,
    workspace,
    idlingResource,
    commandManager
) {
    @VisibleForTesting
    @JvmField
    var resizeBoundWidthXLeft = 0f

    @VisibleForTesting
    @JvmField
    var resizeBoundWidthXRight = 0f

    @VisibleForTesting
    @JvmField
    var resizeBoundHeightYTop = 0f

    @VisibleForTesting
    @JvmField
    var resizeBoundHeightYBottom = 0f

    var checkMarkClicked = false

    private var cropRunFinished = false
    private var maxImageResolutionInformationAlreadyShown = false
    private var zeroSizeBitmap = false
    private var isSetCenter = false
    private val rangeFilterHeight: NumberRangeFilter
    private val rangeFilterWidth: NumberRangeFilter
    private val cropAlgorithm: CropAlgorithm
    private val setCenterCropAlgorithm: SetCenterAlgorithm

    override val toolType: ToolType
        get() = ToolType.TRANSFORM

    override fun handleUpAnimations(coordinate: PointF?) {
        super.handleUp(coordinate)
    }

    override fun handleDownAnimations(coordinate: PointF?) {
        super.handleDown(coordinate)
    }

    override fun toolPositionCoordinates(coordinate: PointF): PointF = coordinate

    init {
        rotationEnabled = ROTATION_ENABLED
        resizePointsVisible = RESIZE_POINTS_VISIBLE
        respectMaximumBorderRatio = RESPECT_MAXIMUM_BORDER_RATIO
        boxHeight = workspace.height.toFloat()
        boxWidth = workspace.width.toFloat()
        toolPosition.x = boxWidth / 2f
        toolPosition.y = boxHeight / 2f
        cropAlgorithm = JavaCropAlgorithm()
        setCenterCropAlgorithm = SetCenterAlgorithm(cropAlgorithm)
        cropRunFinished = true
        maximumBoxResolution =
            metrics.widthPixels * metrics.heightPixels * MAXIMUM_BITMAP_SIZE_FACTOR
        respectMaximumBoxResolution = RESPECT_MAXIMUM_BOX_RESOLUTION
        initResizeBounds()
        toolOptionsViewController.setCallback(object : ToolOptionsVisibilityController.Callback {
            override fun onHide() {
                if (isSetCenter) {
                    contextCallback.showNotificationWithDuration(
                        R.string.set_center_info_text,
                        ContextCallback.NotificationDuration.LONG
                    )
                } else if (!zeroSizeBitmap) {
                    contextCallback.showNotificationWithDuration(
                        R.string.transform_info_text,
                        ContextCallback.NotificationDuration.LONG
                    )
                } else {
                    zeroSizeBitmap = false
                }
            }

            override fun onShow() {
                updateToolOptions()
            }
        })
        transformToolOptionsView.setCallback(object : TransformToolOptionsView.Callback {
            override fun autoCropClicked() {
                autoCrop()
            }

            override fun setCenterClicked() {
                setCenter()
            }

            override fun rotateCounterClockwiseClicked() {
                rotateCounterClockWise()
            }

            override fun rotateClockwiseClicked() {
                rotateClockWise()
            }

            override fun flipHorizontalClicked() {
                flipHorizontal()
            }

            override fun flipVerticalClicked() {
                flipVertical()
            }

            override fun setBoxWidth(boxWidth: Float) {
                this@TransformTool.boxWidth = boxWidth
            }

            override fun setBoxHeight(boxHeight: Float) {
                this@TransformTool.boxHeight = boxHeight
            }

            override fun hideToolOptions() {
                this@TransformTool.toolOptionsViewController.hide()
            }

            override fun applyResizeClicked(resizePercentage: Int) {
                onApplyResizeClicked(resizePercentage)
            }
        })
        rangeFilterHeight = DefaultNumberRangeFilter(1, (maximumBoxResolution / boxWidth).toInt())
        rangeFilterWidth = DefaultNumberRangeFilter(1, (maximumBoxResolution / boxHeight).toInt())
        transformToolOptionsView.setHeightFilter(rangeFilterHeight)
        transformToolOptionsView.setWidthFilter(rangeFilterWidth)
        updateToolOptions()
        toolOptionsViewController.showDelayed()
    }

    override fun resetInternalState() {
        initialiseResizingState()
    }

    override fun handleDown(coordinate: PointF?): Boolean {
        coordinate ?: return false
        if (!isSetCenter) {
            return super.handleDown(coordinate)
        }

        CoroutineScope(Dispatchers.Default).launch {
            toolPosition.x = coordinate.x
            toolPosition.y = coordinate.y
            withContext(Dispatchers.Main) {
                workspace.invalidate()
            }
        }
        return true
    }

    override fun handleMove(coordinate: PointF?, shouldAnimate: Boolean): Boolean {
        if (isSetCenter) {
            return false
        }
        return super.handleMove(coordinate, true)
    }

    override fun drawToolSpecifics(canvas: Canvas, boxWidth: Float, boxHeight: Float) {

        if (isSetCenter) {
            drawCursor(canvas)
            return
        }

        var width = boxWidth
        var height = boxHeight

        if (cropRunFinished) {
            linePaint.color = primaryShapeColor
            linePaint.strokeWidth = toolStrokeWidth * 2

            val rightTopPoint = PointF(-width / 2, -height / 2)
            repeat(SIDES) {
                val resizeLineLengthHeight = height / CONSTANT_1
                val resizeLineLengthWidth = width / CONSTANT_1
                canvas.drawLine(
                    rightTopPoint.x - toolStrokeWidth / 2,
                    rightTopPoint.y,
                    rightTopPoint.x + resizeLineLengthWidth,
                    rightTopPoint.y,
                    linePaint
                )
                canvas.drawLine(
                    rightTopPoint.x,
                    rightTopPoint.y - toolStrokeWidth / 2,
                    rightTopPoint.x,
                    rightTopPoint.y + resizeLineLengthHeight,
                    linePaint
                )
                canvas.drawLine(
                    rightTopPoint.x + width / 2 - resizeLineLengthWidth,
                    rightTopPoint.y,
                    rightTopPoint.x + width / 2 + resizeLineLengthWidth,
                    rightTopPoint.y,
                    linePaint
                )
                canvas.rotate(RIGHT_ANGLE)
                val tempX = rightTopPoint.x
                rightTopPoint.x = rightTopPoint.y
                rightTopPoint.y = tempX
                val tempHeight = height
                height = width
                width = tempHeight
            }
        }
    }

    private fun resetScaleAndTranslation() {
        workspace.resetPerspective()
        val zoomFactor = workspace.scaleForCenterBitmap * START_ZOOM_FACTOR
        workspace.scale = zoomFactor
    }

    private fun initialiseResizingState() {
        cropRunFinished = false
        resizeBoundWidthXRight = 0f
        resizeBoundHeightYBottom = 0f
        resizeBoundWidthXLeft = workspace.width.toFloat()
        resizeBoundHeightYTop = workspace.height.toFloat()
        if (!checkMarkClicked) {
            resetScaleAndTranslation()
            resizeBoundWidthXRight = workspace.width - 1f
            resizeBoundHeightYBottom = workspace.height - 1f
            resizeBoundWidthXLeft = 0f
            resizeBoundHeightYTop = 0f
        } else {
            resizeBoundWidthXRight = toolPosition.x + workspace.width / 2 + 1
            resizeBoundWidthXLeft = toolPosition.x - workspace.width / 2 + 1
            resizeBoundHeightYBottom = toolPosition.y + workspace.height / 2 + 1
            resizeBoundHeightYTop = toolPosition.y - workspace.height / 2 + 1
        }

        setRectangle(
            RectF(
                resizeBoundWidthXLeft,
                resizeBoundHeightYTop,
                resizeBoundWidthXRight,
                resizeBoundHeightYBottom
            )
        )
        if (checkMarkClicked) {
            workspace.perspective.surfaceTranslationX += resizeBoundWidthXLeft
            workspace.perspective.surfaceTranslationY += resizeBoundHeightYTop
            workspace.perspective.setBitmapDimensions(boxWidth.toInt(), boxHeight.toInt())
            toolPosition.x -= resizeBoundWidthXLeft
            toolPosition.y -= resizeBoundHeightYTop
            checkMarkClicked = false
        }
        cropRunFinished = true
        updateToolOptions()
    }

    private fun executeSetCenterCommand() {
        CoroutineScope(Dispatchers.Default).launch {
            val shapeBounds = setCenterCropAlgorithm.crop(workspace.bitmapOfAllLayers, toolPosition)
            if (shapeBounds != null) {
                boxWidth = shapeBounds.width() + 1f
                boxHeight = shapeBounds.height() + 1f
                toolPosition.x = shapeBounds.left + (shapeBounds.width() + 1) / 2.0f
                toolPosition.y = shapeBounds.top + (shapeBounds.height() + 1) / 2.0f
            } else {
                toolPosition.x = boxWidth / 2.0f
                toolPosition.y = boxHeight / 2.0f
            }

            withContext(Dispatchers.Main) {
                isSetCenter = false
                shouldDrawRectangle = true
                workspace.invalidate()
                toolOptionsViewController.hide()
            }
        }
    }

    private fun executeResizeCommand() {
        if (cropRunFinished) {
            cropRunFinished = false
            initResizeBounds()
            if (areResizeBordersValid()) {
                val resizeCommand = commandFactory.createCropCommand(
                    floor(resizeBoundWidthXLeft).toInt(),
                    floor(resizeBoundHeightYTop).toInt(),
                    floor(resizeBoundWidthXRight).toInt(),
                    floor(resizeBoundHeightYBottom).toInt(),
                    maximumBoxResolution.toInt()
                )
                commandManager.addCommand(resizeCommand)
            } else {
                cropRunFinished = true
                contextCallback.showNotification(R.string.resize_nothing_to_resize)
            }
        }
    }

    private fun onApplyResizeClicked(resizePercentage: Int) {
        val newWidth = (workspace.width / HUNDRED * resizePercentage).toInt()
        val newHeight = (workspace.height / HUNDRED * resizePercentage).toInt()
        if (newWidth == 0 || newHeight == 0) {
            zeroSizeBitmap = true
            contextCallback.showNotificationWithDuration(
                R.string.resize_cannot_resize_to_this_size,
                ContextCallback.NotificationDuration.LONG
            )
        } else {
            val command = commandFactory.createResizeCommand(newWidth, newHeight)
            commandManager.addCommand(command)
        }
    }

    private fun flipHorizontal() {
        val command = commandFactory.createFlipCommand(FlipCommand.FlipDirection.FLIP_HORIZONTAL)
        commandManager.addCommand(command)
    }

    private fun flipVertical() {
        val command = commandFactory.createFlipCommand(FlipCommand.FlipDirection.FLIP_VERTICAL)
        commandManager.addCommand(command)
    }

    private fun rotateCounterClockWise() {
        val command = commandFactory.createRotateCommand(RotateCommand.RotateDirection.ROTATE_LEFT)
        commandManager.addCommand(command)
        swapWidthAndHeight()
    }

    private fun rotateClockWise() {
        val command = commandFactory.createRotateCommand(RotateCommand.RotateDirection.ROTATE_RIGHT)
        commandManager.addCommand(command)
        swapWidthAndHeight()
    }

    private fun swapWidthAndHeight() {
        val tempBoxWidth = boxWidth
        boxWidth = boxHeight
        boxHeight = tempBoxWidth
    }

    private fun autoCrop() {
        CoroutineScope(Dispatchers.Default).launch {
            idlingResource.increment()
            val shapeBounds = cropAlgorithm.crop(workspace.bitmapOfAllLayers)
            if (shapeBounds != null) {
                boxWidth = shapeBounds.width() + 1f
                boxHeight = shapeBounds.height() + 1f
                toolPosition.x = shapeBounds.left + (shapeBounds.width() + 1) / 2.0f
                toolPosition.y = shapeBounds.top + (shapeBounds.height() + 1) / 2.0f
            }

            withContext(Dispatchers.Main) {
                workspace.invalidate()
                toolOptionsViewController.hide()
            }
            idlingResource.decrement()
        }
    }

    private fun setCenter() {
        CoroutineScope(Dispatchers.Default).launch {
            isSetCenter = true
            shouldDrawRectangle = false
            withContext(Dispatchers.Main) {
                toolOptionsViewController.hide()
            }
            workspace.invalidate()
        }
    }

    private fun drawCursor(canvas: Canvas) {
        val positionX = 0.0f
        val positionY = 0.0f
        val brushStrokeWidth = max(toolPaint.strokeWidth / 2f, 1f)
        val strokeWidth = getStrokeWidthForZoom(
            DEFAULT_CURSOR_STROKE_WIDTH,
            MINIMAL_CURSOR_STROKE_WIDTH, MAXIMAL_CURSOR_STROKE_WIDTH
        )
        val cursorPartLength = strokeWidth * 2
        val innerCircleRadius = brushStrokeWidth + strokeWidth / 2f
        val outerCircleRadius = innerCircleRadius + strokeWidth
        linePaint.apply {
            color =
                contextCallback.getColor(R.color.pocketpaint_main_cursor_tool_inactive_primary_color)
            style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
        }
        drawCursorCircle(
            canvas,
            strokeWidth,
            outerCircleRadius,
            innerCircleRadius,
            positionX,
            positionY
        )

        linePaint.style = Paint.Style.FILL
        var startLineLengthAddition = strokeWidth / 2f
        var endLineLengthAddition = cursorPartLength + strokeWidth
        var lineNr = 0
        while (lineNr < CURSOR_LINES) {
            if (lineNr % 2 == 0) {
                linePaint.color = Color.LTGRAY
            } else {
                linePaint.color =
                    contextCallback.getColor(R.color.pocketpaint_main_cursor_tool_inactive_primary_color)
            }

            canvas.drawLine(
                positionX - outerCircleRadius - startLineLengthAddition,
                positionY,
                positionX - outerCircleRadius - endLineLengthAddition,
                positionY,
                linePaint
            )
            canvas.drawLine(
                positionX + outerCircleRadius + startLineLengthAddition,
                positionY,
                positionX + outerCircleRadius + endLineLengthAddition,
                positionY,
                linePaint
            )
            canvas.drawLine(
                positionX,
                positionY + outerCircleRadius + startLineLengthAddition,
                positionX,
                positionY + outerCircleRadius + endLineLengthAddition,
                linePaint
            )
            canvas.drawLine(
                positionX,
                positionY - outerCircleRadius - startLineLengthAddition,
                positionX,
                positionY - outerCircleRadius - endLineLengthAddition,
                linePaint
            )
            lineNr++
            startLineLengthAddition = strokeWidth / 2f + cursorPartLength * lineNr
            endLineLengthAddition = strokeWidth + cursorPartLength * (lineNr + 1f)
        }
        linePaint.style = Paint.Style.STROKE
    }

    private fun drawCursorCircle(
        canvas: Canvas,
        strokeWidth: Float,
        outerCircleRadius: Float,
        innerCircleRadius: Float,
        positionX: Float,
        positionY: Float,
    ) {
        canvas.drawCircle(positionX, positionY, outerCircleRadius, linePaint)
        linePaint.color = Color.LTGRAY
        canvas.drawCircle(positionX, positionY, innerCircleRadius, linePaint)
        linePaint.color = Color.TRANSPARENT
        linePaint.style = Paint.Style.FILL
        canvas.drawCircle(
            positionX,
            positionY,
            innerCircleRadius - strokeWidth / 2f,
            linePaint
        )
    }

    private fun areResizeBordersValid(): Boolean {
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
        if (resizeBoundWidthXLeft == 0f && resizeBoundHeightYTop == 0f && resizeBoundWidthXRight == workspace.width - 1f && resizeBoundHeightYBottom == workspace.height - 1f) {
            return false
        }
        val width = resizeBoundWidthXRight - resizeBoundWidthXLeft + 1
        val height = resizeBoundHeightYBottom - resizeBoundHeightYTop + 1
        return width * height <= maximumBoxResolution
    }

    private fun setRectangle(rectangle: RectF) {
        if (!checkMarkClicked) {
            boxWidth = rectangle.right - rectangle.left + 1f
            boxHeight = rectangle.bottom - rectangle.top + 1f
            toolPosition.x = rectangle.left + boxWidth / 2f
            toolPosition.y = rectangle.top + boxHeight / 2f
        } else {
            boxWidth = rectangle.right - rectangle.left
            boxHeight = rectangle.bottom - rectangle.top
        }
    }

    private fun initResizeBounds() {
        resizeBoundWidthXLeft = toolPosition.x - boxWidth / 2f
        resizeBoundWidthXRight = toolPosition.x + boxWidth / 2f - 1f
        resizeBoundHeightYTop = toolPosition.y - boxHeight / 2f
        resizeBoundHeightYBottom = toolPosition.y + boxHeight / 2f - 1f
    }

    override fun onClickOnButton() {
        if (isSetCenter) {
            executeSetCenterCommand()
        } else {
            executeResizeCommand()
        }
    }

    override fun preventThatBoxGetsTooLarge(
        oldWidth: Float,
        oldHeight: Float,
        oldPosX: Float,
        oldPosY: Float
    ) {
        super.preventThatBoxGetsTooLarge(oldWidth, oldHeight, oldPosX, oldPosY)
        if (!maxImageResolutionInformationAlreadyShown) {
            contextCallback.showNotification(R.string.resize_max_image_resolution_reached)
            maxImageResolutionInformationAlreadyShown = true
        }
    }

    private fun updateToolOptions() {
        rangeFilterHeight.max = (maximumBoxResolution / boxWidth).toInt()
        rangeFilterWidth.max = (maximumBoxResolution / boxHeight).toInt()
        transformToolOptionsView.setWidth(boxWidth.toInt())
        transformToolOptionsView.setHeight(boxHeight.toInt())
    }
}
