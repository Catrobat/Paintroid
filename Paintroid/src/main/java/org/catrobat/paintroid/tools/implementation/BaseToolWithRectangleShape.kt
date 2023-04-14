/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
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
import android.graphics.Paint
import android.graphics.Paint.Cap
import android.graphics.Path
import android.graphics.Point
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.DisplayMetrics
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.VisibleForTesting
import androidx.test.espresso.idling.CountingIdlingResource
import org.catrobat.paintroid.R
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.common.INVALID_RESOURCE_ID
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ContextCallback.ScreenOrientation
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import java.lang.Math.toDegrees
import java.lang.Math.toRadians
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
const val MAXIMUM_BORDER_RATIO = 2f

@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
const val MINIMAL_BOX_SIZE = 3

const val DEFAULT_BOX_RESIZE_MARGIN = 20

const val DEFAULT_ANTIALIASING_ON = true
private const val DEFAULT_RECTANGLE_MARGIN = 100f
private const val DEFAULT_TOOL_STROKE_WIDTH = 3f
private const val MINIMAL_TOOL_STROKE_WIDTH = 1f
private const val MAXIMAL_TOOL_STROKE_WIDTH = 8f
private const val DEFAULT_ROTATION_SYMBOL_DISTANCE = 20
private const val DEFAULT_ROTATION_SYMBOL_WIDTH = 30
private const val DEFAULT_MAXIMUM_BOX_RESOLUTION = 0f
private const val DEFAULT_RECTANGLE_SHRINKING = 0
private const val HIGHLIGHT_RECTANGLE_SHRINKING = 5
private const val DEFAULT_ROTATION_ENABLED = false
private const val DEFAULT_RESIZE_POINTS_VISIBLE = true
private const val DEFAULT_RESPECT_MAXIMUM_BORDER_RATIO = true
private const val DEFAULT_RESPECT_MAXIMUM_BOX_RESOLUTION = false
internal const val CLICK_TIMEOUT_MILLIS = 250L
private const val RIGHT_ANGLE = 90f
private const val STRAIGHT_ANGLE = 180f
private const val COMPLETE_ANGLE = 360f
private const val SIDES = 4
private const val CONSTANT_1 = 10
private const val CONSTANT_2 = 8
internal const val CONSTANT_3 = 3
private const val BUNDLE_BOX_WIDTH = "BOX_WIDTH"
private const val BUNDLE_BOX_HEIGHT = "BOX_HEIGHT"
private const val BUNDLE_BOX_ROTATION = "BOX_ROTATION"

abstract class BaseToolWithRectangleShape(
    contextCallback: ContextCallback,
    toolOptionsViewController: ToolOptionsViewController,
    toolPaint: ToolPaint,
    workspace: Workspace,
    idlingResource: CountingIdlingResource,
    commandManager: CommandManager
) : BaseToolWithShape(
    contextCallback,
    toolOptionsViewController,
    toolPaint,
    workspace,
    idlingResource,
    commandManager
) {
    private val rotationArrowArcStrokeWidth: Int
    private val rotationArrowArcRadius: Int
    private val rotationArrowHeadSize: Int
    private val rotationArrowOffset: Int
    private val arcPaint: Paint
    private val arrowPaint: Paint
    private val arcPath: Path
    private val arrowPath: Path
    private val tempDrawingRectangle: RectF
    private val tempToolPosition: PointF

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    @JvmField
    var boxWidth: Float

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    @JvmField
    var boxHeight: Float

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    @JvmField
    var boxRotation = 0f // in degree

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    @JvmField
    var rotationEnabled: Boolean

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    @JvmField
    var drawingBitmap: Bitmap? = null

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    @JvmField
    var rotationSymbolDistance = 0f

    @JvmField
    var toolStrokeWidth = 0f

    @JvmField
    var maximumBoxResolution: Float

    @JvmField
    var resizePointsVisible: Boolean

    @JvmField
    var respectMaximumBorderRatio: Boolean

    @JvmField
    var respectMaximumBoxResolution: Boolean

    @JvmField
    var rectangleShrinkingOnHighlight: Int

    @JvmField
    var shouldDrawRectangle = true

    private var boxResizeMargin: Float? = 0f
    private var rotationSymbolWidth: Float? = 0f
    private var currentAction: FloatingBoxAction? = null
    private var overlayDrawable: Drawable? = null
    private var downTimer: CountDownTimer? = null
    private var resizeAction: ResizeAction
    private var touchDownPositionX = 0f
    private var touchDownPositionY = 0f

    init {
        val orientation = contextCallback.orientation
        val boxSize =
            if (orientation == ScreenOrientation.PORTRAIT) metrics.widthPixels.toFloat() else metrics.heightPixels.toFloat()
        boxWidth = boxSize / workspace.scale - 2 * getInverselyProportionalSizeForZoom(
            DEFAULT_RECTANGLE_MARGIN
        )
        boxHeight = boxWidth
        if (DEFAULT_RESPECT_MAXIMUM_BORDER_RATIO && (
            boxHeight > workspace.height * MAXIMUM_BORDER_RATIO ||
                boxWidth > workspace.width * MAXIMUM_BORDER_RATIO
            )
        ) {
            boxHeight = workspace.height * MAXIMUM_BORDER_RATIO
            boxWidth = workspace.width * MAXIMUM_BORDER_RATIO
        }
        rectangleShrinkingOnHighlight = DEFAULT_RECTANGLE_SHRINKING
        rotationArrowArcStrokeWidth = getDensitySpecificValue(2)
        rotationArrowArcRadius = getDensitySpecificValue(CONSTANT_2)
        rotationArrowHeadSize = getDensitySpecificValue(CONSTANT_3)
        rotationArrowOffset = getDensitySpecificValue(CONSTANT_3)
        resizeAction = ResizeAction.NONE
        rotationEnabled = DEFAULT_ROTATION_ENABLED
        resizePointsVisible = DEFAULT_RESIZE_POINTS_VISIBLE
        respectMaximumBorderRatio = DEFAULT_RESPECT_MAXIMUM_BORDER_RATIO
        respectMaximumBoxResolution = DEFAULT_RESPECT_MAXIMUM_BOX_RESOLUTION
        maximumBoxResolution = DEFAULT_MAXIMUM_BOX_RESOLUTION
        initScaleDependedValues()
        createOverlayDrawable()
        linePaint.apply {
            reset()
            isDither = true
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
        }
        arcPaint = Paint()
        arcPaint.apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeCap = Cap.BUTT
        }
        arrowPaint = Paint()
        arrowPaint.color = Color.WHITE
        arrowPaint.style = Paint.Style.FILL
        arcPath = Path()
        arrowPath = Path()
        tempDrawingRectangle = RectF()
        tempToolPosition = PointF()
    }

    private fun getDensitySpecificValue(value: Int): Int {
        val baseDensity = DisplayMetrics.DENSITY_MEDIUM
        val density = max(DisplayMetrics.DENSITY_MEDIUM, metrics.densityDpi)
        return value * density / baseDensity
    }

    private fun initScaleDependedValues() {
        toolStrokeWidth = getStrokeWidthForZoom(
            DEFAULT_TOOL_STROKE_WIDTH,
            MINIMAL_TOOL_STROKE_WIDTH, MAXIMAL_TOOL_STROKE_WIDTH
        )
        boxResizeMargin = getInverselyProportionalSizeForZoom(DEFAULT_BOX_RESIZE_MARGIN.toFloat())
        rotationSymbolDistance = getInverselyProportionalSizeForZoom(
            DEFAULT_ROTATION_SYMBOL_DISTANCE.toFloat()
        ) * 2
        rotationSymbolWidth =
            getInverselyProportionalSizeForZoom(DEFAULT_ROTATION_SYMBOL_WIDTH.toFloat())
    }

    private fun <T : Any, R : Any> ifNotNull(vararg options: T?, block: (List<T>) -> R) {
        if (options.all { it != null }) {
            block(options.filterNotNull())
        }
    }

    fun setBitmap(bitmap: Bitmap?) {
        if (bitmap != null) {
            drawingBitmap = bitmap
        }
        workspace.invalidate()
    }

    private fun hideToolSpecificLayout() {
        if (this !is TextTool &&
            toolOptionsViewController.isVisible &&
            toolOptionsViewController.toolSpecificOptionsLayout.visibility == View.VISIBLE) {
            toolOptionsViewController.slideDown(
                toolOptionsViewController.toolSpecificOptionsLayout,
                willHide = true,
                showOptionsView = false
            )
        }
        toolOptionsViewController.animateBottomAndTopNavigation(true)
    }

     private fun showToolSpecificLayout() {
        if (this !is TextTool &&
            !toolOptionsViewController.isVisible &&
            toolOptionsViewController.toolSpecificOptionsLayout.visibility == View.INVISIBLE) {
            toolOptionsViewController.slideUp(
                toolOptionsViewController.toolSpecificOptionsLayout,
                willHide = false,
                showOptionsView = true
            )
        }
        toolOptionsViewController.animateBottomAndTopNavigation(false)
    }

    override fun handleDown(coordinate: PointF?): Boolean {
        movedDistance.set(0f, 0f)

        coordinate?.apply {
            previousEventCoordinate = PointF(x, y)
            currentAction = getAction(x, y)
        }
        touchDownPositionX = toolPosition.x
        touchDownPositionY = toolPosition.y
        return true
    }

    override fun handleMove(coordinate: PointF?): Boolean {
        if (previousEventCoordinate == null || currentAction == null) {
            return false
        }
        hideToolSpecificLayout()
        ifNotNull(coordinate, previousEventCoordinate) { (coordinate, previousEventCoordinate) ->
            val delta = PointF(
                coordinate.x - previousEventCoordinate.x,
                coordinate.y - previousEventCoordinate.y
            )
            movedDistance.set(movedDistance.x + abs(delta.x), movedDistance.y + abs(delta.y))
            previousEventCoordinate.set(coordinate.x, coordinate.y)
            when (currentAction) {
                FloatingBoxAction.MOVE -> move(delta.x, delta.y)
                FloatingBoxAction.RESIZE -> resize(delta.x, delta.y)
                FloatingBoxAction.ROTATE -> rotate(delta.x, delta.y)
                else -> Unit
            }
        }
        return true
    }

    override fun handleUp(coordinate: PointF?): Boolean {
        if (previousEventCoordinate == null) {
            return false
        }
        showToolSpecificLayout()
        ifNotNull(coordinate, previousEventCoordinate) { (coordinate, previousEventCoordinate) ->
            movedDistance.x += abs(coordinate.x - previousEventCoordinate.x)
            movedDistance.y += abs(coordinate.y - previousEventCoordinate.y)
        }
        return true
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    fun boxContainsPoint(coordinate: PointF): Boolean {
        val relativeToOriginX = coordinate.x - toolPosition.x
        val relativeToOriginY = coordinate.y - toolPosition.y
        val radians = -(boxRotation * PI.toFloat() / STRAIGHT_ANGLE)
        val rotatedX =
            relativeToOriginX * cos(radians) - relativeToOriginY * sin(radians) + toolPosition.x
        val rotatedY =
            relativeToOriginX * sin(radians) + relativeToOriginY * cos(radians) + toolPosition.y
        return rotatedX > toolPosition.x - boxWidth / 2 && rotatedX < toolPosition.x + boxWidth / 2 && rotatedY > toolPosition.y - boxHeight / 2 && rotatedY < toolPosition.y + boxHeight / 2
    }

    protected fun boxIntersectsWorkspace(): Boolean =
        toolPosition.x - boxWidth / 2 < workspace.width && toolPosition.y - boxHeight / 2 < workspace.height && toolPosition.x + boxWidth / 2 >= 0 && toolPosition.y + boxHeight / 2 >= 0

    override fun draw(canvas: Canvas) {
        drawShape(canvas)
    }

    override fun drawShape(canvas: Canvas) {
        initScaleDependedValues()
        val boxWidth = boxWidth
        val boxHeight = boxHeight
        val boxRotation = boxRotation
        tempToolPosition.set(toolPosition.x, toolPosition.y)
        canvas.run {
            save()
            translate(tempToolPosition.x, tempToolPosition.y)
            rotate(boxRotation)
        }
        if (resizePointsVisible) {
            drawToolSpecifics(canvas, boxWidth, boxHeight)
        }
        if (rotationEnabled) {
            drawRotationArrows(canvas, boxWidth, boxHeight)
        }
        drawBitmap(canvas, boxWidth, boxHeight)
        if (overlayDrawable != null) {
            drawOverlayDrawable(canvas, boxWidth, boxHeight, boxRotation)
        }
        if (shouldDrawRectangle) {
            drawRectangle(canvas, boxWidth, boxHeight)
        }
        drawToolSpecifics(canvas, boxWidth, boxHeight)
        canvas.restore()
    }

    private fun drawRotationArrows(canvas: Canvas, boxWidth: Float, boxHeight: Float) {
        var width = boxWidth
        var height = boxHeight
        val arcStrokeWidth =
            getInverselyProportionalSizeForZoom(rotationArrowArcStrokeWidth.toFloat())
        val arcRadius = getInverselyProportionalSizeForZoom(rotationArrowArcRadius.toFloat())
        val arrowSize = getInverselyProportionalSizeForZoom(rotationArrowHeadSize.toFloat())
        val offset = getInverselyProportionalSizeForZoom(rotationArrowOffset.toFloat())
        arcPaint.strokeWidth = arcStrokeWidth
        repeat(SIDES) {
            val xBase = -width / 2 - offset
            val yBase = -height / 2 - offset
            arcPath.reset()
            tempDrawingRectangle.set(
                xBase - arcRadius,
                yBase - arcRadius,
                xBase + arcRadius,
                yBase + arcRadius
            )
            arcPath.addArc(tempDrawingRectangle, STRAIGHT_ANGLE, RIGHT_ANGLE)
            canvas.drawPath(arcPath, arcPaint)
            arrowPath.run {
                reset()
                moveTo(xBase - arcRadius - arrowSize, yBase)
                lineTo(xBase - arcRadius + arrowSize, yBase)
                lineTo(xBase - arcRadius, yBase + arrowSize)
                close()
                moveTo(xBase, yBase - arcRadius - arrowSize)
                lineTo(xBase, yBase - arcRadius + arrowSize)
                lineTo(xBase + arrowSize, yBase - arcRadius)
                close()
            }
            canvas.drawPath(arrowPath, arrowPaint)
            val tempLength = width
            width = height
            height = tempLength
            canvas.rotate(RIGHT_ANGLE)
        }
    }

    protected open fun drawBitmap(canvas: Canvas, boxWidth: Float, boxHeight: Float) {
        drawingBitmap?.let {
            tempDrawingRectangle.set(-boxWidth / 2, -boxHeight / 2, boxWidth / 2, boxHeight / 2)
            canvas.clipRect(tempDrawingRectangle)

            val alphaPaint = Paint().apply {
                workspace.layerModel.currentLayer?.let { layer ->
                    alpha = layer.getValueForOpacityPercentage()
                }
            }

            canvas.drawBitmap(it, null, tempDrawingRectangle, alphaPaint)
        }
    }

    private fun drawOverlayDrawable(
        canvas: Canvas,
        boxWidth: Float,
        boxHeight: Float,
        boxRotation: Float
    ) {
        val size = (min(boxWidth, boxHeight) / CONSTANT_2).toInt()
        canvas.save()
        canvas.rotate(-boxRotation)
        overlayDrawable?.run {
            setBounds(-size, -size, size, size)
            draw(canvas)
        }
        canvas.restore()
    }

    private fun drawRectangle(canvas: Canvas, boxWidth: Float, boxHeight: Float) {
        linePaint.strokeWidth = toolStrokeWidth
        linePaint.color = secondaryShapeColor
        tempDrawingRectangle.set(
            -boxWidth / 2 + rectangleShrinkingOnHighlight,
            -boxHeight / 2 + rectangleShrinkingOnHighlight,
            boxWidth / 2 - rectangleShrinkingOnHighlight,
            boxHeight / 2 - rectangleShrinkingOnHighlight
        )
        canvas.drawRect(tempDrawingRectangle, linePaint)
    }

    private fun move(deltaX: Float, deltaY: Float) {
        toolPosition.x += deltaX
        toolPosition.y += deltaY
    }

    private fun rotate(deltaX: Float, deltaY: Float) {
        previousEventCoordinate?.let {
            val currentPoint = PointF(it.x, it.y)
            val previousXLength = (it.x - deltaX - toolPosition.x).toDouble()
            val previousYLength = (it.y - deltaY - toolPosition.y).toDouble()
            val currentXLength = (currentPoint.x - toolPosition.x).toDouble()
            val currentYLength = (currentPoint.y - toolPosition.y).toDouble()
            val rotationAnglePrevious = atan2(previousYLength, previousXLength)
            val rotationAngleCurrent = atan2(currentYLength, currentXLength)
            val deltaAngle = -(rotationAnglePrevious - rotationAngleCurrent)
            boxRotation += toDegrees(deltaAngle).toFloat() + COMPLETE_ANGLE
            boxRotation %= COMPLETE_ANGLE
            if (boxRotation > STRAIGHT_ANGLE) {
                boxRotation -= COMPLETE_ANGLE
            }
        }
    }

    private fun resizeOnFrame(
        clickCoordinatesRotatedX: Float,
        clickCoordinatesRotatedY: Float,
        margin: Float
    ) {
        if (clickCoordinatesRotatedX < toolPosition.x - boxWidth / 2 + margin) {
            resizeAction = ResizeAction.LEFT
        } else if (clickCoordinatesRotatedX > toolPosition.x + boxWidth / 2 - margin) {
            resizeAction = ResizeAction.RIGHT
        }
        if (clickCoordinatesRotatedY < toolPosition.y - boxHeight / 2 + margin) {
            resizeAction = when (resizeAction) {
                ResizeAction.LEFT -> ResizeAction.TOPLEFT
                ResizeAction.RIGHT -> ResizeAction.TOPRIGHT
                else -> ResizeAction.TOP
            }
        } else if (clickCoordinatesRotatedY > toolPosition.y + boxHeight / 2 - margin) {
            resizeAction = when (resizeAction) {
                ResizeAction.LEFT -> ResizeAction.BOTTOMLEFT
                ResizeAction.RIGHT -> ResizeAction.BOTTOMRIGHT
                else -> ResizeAction.BOTTOM
            }
        }
    }

    private fun getRotationPoints(): Array<PointF> {
        val newX1 = toolPosition.x - boxWidth / 2 - rotationSymbolDistance / 2
        val newX2 = toolPosition.x + boxWidth / 2 + rotationSymbolDistance / 2
        val newY1 = toolPosition.y - boxHeight / 2 - rotationSymbolDistance / 2
        val newY2 = toolPosition.y + boxHeight / 2 + rotationSymbolDistance / 2

        val topLeftRotationPoint = PointF(newX1, newY1)
        val topRightRotationPoint = PointF(newX2, newY1)
        val bottomLeftRotationPoint = PointF(newX1, newY2)
        val bottomRightRotationPoint = PointF(newX2, newY2)

        return arrayOf(
            topLeftRotationPoint,
            topRightRotationPoint,
            bottomLeftRotationPoint,
            bottomRightRotationPoint
        )
    }

    private fun getAction(
        clickCoordinatesX: Float,
        clickCoordinatesY: Float
    ): FloatingBoxAction {
        resizeAction = ResizeAction.NONE
        val rotationRadiant = boxRotation * PI.toFloat() / STRAIGHT_ANGLE
        val clickCoordinatesRotatedX =
            toolPosition.x + cos(-rotationRadiant) * (clickCoordinatesX - toolPosition.x) - sin(-rotationRadiant) * (clickCoordinatesY - toolPosition.y)
        val clickCoordinatesRotatedY =
            toolPosition.y + sin(-rotationRadiant) * (clickCoordinatesX - toolPosition.x) + cos(-rotationRadiant) * (clickCoordinatesY - toolPosition.y)

        val margin = boxResizeMargin ?: 0f
        // Move (within box)
        if (clickCoordinatesRotatedX < toolPosition.x + boxWidth / 2 - margin && clickCoordinatesRotatedX > toolPosition.x - boxWidth / 2 + margin && clickCoordinatesRotatedY < toolPosition.y + boxHeight / 2 - margin && clickCoordinatesRotatedY > toolPosition.y - boxHeight / 2 + margin) {
            return FloatingBoxAction.MOVE
        }
        // Resize (on frame)
        if (clickCoordinatesRotatedX < toolPosition.x + boxWidth / 2 + margin && clickCoordinatesRotatedX > toolPosition.x - boxWidth / 2 - margin && clickCoordinatesRotatedY < toolPosition.y + boxHeight / 2 + margin && clickCoordinatesRotatedY > toolPosition.y - boxHeight / 2 - margin) {
            resizeOnFrame(clickCoordinatesRotatedX, clickCoordinatesRotatedY, margin)
            return FloatingBoxAction.RESIZE
        }
        if (rotationEnabled && checkRotationPoints(
                clickCoordinatesRotatedX,
                clickCoordinatesRotatedY,
                getRotationPoints()
            )
        ) {
            return FloatingBoxAction.ROTATE
        }
        return FloatingBoxAction.MOVE
    }

    private fun checkRotationPoints(
        clickCoordinatesRotatedX: Float,
        clickCoordinatesRotatedY: Float,
        rotationPoints: Array<PointF>
    ): Boolean {
        rotationPoints.forEach { point ->
            if (clickCoordinatesRotatedX > point.x - rotationSymbolDistance / 2 && clickCoordinatesRotatedX < point.x + rotationSymbolDistance / 2 && clickCoordinatesRotatedY > point.y - rotationSymbolDistance / 2 && clickCoordinatesRotatedY < point.y + rotationSymbolDistance / 2) {
                return true
            }
        }
        return false
    }

    private fun resizeHeight(deltaYCorrected: Float, rotationRadian: Double) {
        val maximumBorderRatioHeight = workspace.height * MAXIMUM_BORDER_RATIO
        val resizeYMoveCenterX = (deltaYCorrected / 2 * sin(rotationRadian)).toFloat()
        val resizeYMoveCenterY = (deltaYCorrected / 2 * cos(rotationRadian)).toFloat()
        val height: Float
        val posX: Float
        val posY: Float

        when (resizeAction) {
            ResizeAction.TOP, ResizeAction.TOPRIGHT, ResizeAction.TOPLEFT -> {
                height = boxHeight - deltaYCorrected
                posX = toolPosition.x - resizeYMoveCenterX
                posY = toolPosition.y + resizeYMoveCenterY
                if (respectMaximumBorderRatio && height > maximumBorderRatioHeight) {
                    boxHeight = maximumBorderRatioHeight
                } else {
                    boxHeight = height
                    toolPosition.x = posX
                    toolPosition.y = posY
                }
            }
            ResizeAction.BOTTOM, ResizeAction.BOTTOMLEFT, ResizeAction.BOTTOMRIGHT -> {
                height = boxHeight + deltaYCorrected
                posX = toolPosition.x - resizeYMoveCenterX
                posY = toolPosition.y + resizeYMoveCenterY
                if (respectMaximumBorderRatio && height > maximumBorderRatioHeight) {
                    boxHeight = maximumBorderRatioHeight
                } else {
                    boxHeight = height
                    toolPosition.x = posX
                    toolPosition.y = posY
                }
            }
            else -> Unit
        }
    }

    private fun resizeWidth(deltaXCorrected: Float, rotationRadian: Double) {
        val maximumBorderRatioWidth = workspace.width * MAXIMUM_BORDER_RATIO
        val resizeXMoveCenterX = (deltaXCorrected / 2 * cos(rotationRadian)).toFloat()
        val resizeXMoveCenterY = (deltaXCorrected / 2 * sin(rotationRadian)).toFloat()
        val width: Float
        val posX: Float
        val posY: Float

        when (resizeAction) {
            ResizeAction.LEFT, ResizeAction.TOPLEFT, ResizeAction.BOTTOMLEFT -> {
                width = boxWidth - deltaXCorrected
                posX = toolPosition.x + resizeXMoveCenterX
                posY = toolPosition.y + resizeXMoveCenterY
                if (respectMaximumBorderRatio && width > maximumBorderRatioWidth) {
                    boxWidth = maximumBorderRatioWidth
                } else {
                    boxWidth = width
                    toolPosition.x = posX
                    toolPosition.y = posY
                }
            }
            ResizeAction.RIGHT, ResizeAction.TOPRIGHT, ResizeAction.BOTTOMRIGHT -> {
                width = boxWidth + deltaXCorrected
                posX = toolPosition.x + resizeXMoveCenterX
                posY = toolPosition.y + resizeXMoveCenterY
                if (respectMaximumBorderRatio && width > maximumBorderRatioWidth) {
                    boxWidth = maximumBorderRatioWidth
                } else {
                    boxWidth = width
                    toolPosition.x = posX
                    toolPosition.y = posY
                }
            }
            else -> Unit
        }
    }

    private fun resize(deltaX: Float, deltaY: Float) {
        val rotationRadian = toRadians(boxRotation.toDouble())
        var deltaXCorrected = cos(-rotationRadian) * deltaX - sin(-rotationRadian) * deltaY
        var deltaYCorrected = sin(-rotationRadian) * deltaX + cos(-rotationRadian) * deltaY
        when (resizeAction) {
            ResizeAction.TOPLEFT, ResizeAction.BOTTOMRIGHT ->
                if (abs(deltaXCorrected) > abs(
                        deltaYCorrected
                    )
                ) {
                    deltaYCorrected =
                        (boxWidth + deltaXCorrected) * boxHeight / boxWidth - boxHeight
                } else {
                    deltaXCorrected =
                        boxWidth * (boxHeight + deltaYCorrected) / boxHeight - boxWidth
                }
            ResizeAction.TOPRIGHT, ResizeAction.BOTTOMLEFT ->
                if (abs(deltaXCorrected) > abs(
                        deltaYCorrected
                    )
                ) {
                    deltaYCorrected =
                        (boxWidth - deltaXCorrected) * boxHeight / boxWidth - boxHeight
                } else {
                    deltaXCorrected =
                        boxWidth * (boxHeight - deltaYCorrected) / boxHeight - boxWidth
                }
            else -> Unit
        }
        val oldPosX = toolPosition.x
        val oldPosY = toolPosition.y
        val oldHeight = boxHeight
        val oldWidth = boxWidth

        resizeHeight(deltaYCorrected.toFloat(), rotationRadian)
        resizeWidth(deltaXCorrected.toFloat(), rotationRadian)

        // prevent that box gets too small
        if (boxWidth < MINIMAL_BOX_SIZE) {
            boxWidth = MINIMAL_BOX_SIZE.toFloat()
            toolPosition.x = oldPosX
        }
        if (boxHeight < MINIMAL_BOX_SIZE) {
            boxHeight = MINIMAL_BOX_SIZE.toFloat()
            toolPosition.y = oldPosY
        }
        if (respectMaximumBoxResolution && maximumBoxResolution > 0 && boxWidth * boxHeight > maximumBoxResolution) {
            preventThatBoxGetsTooLarge(oldWidth, oldHeight, oldPosX, oldPosY)
        }
    }

    protected open fun preventThatBoxGetsTooLarge(
        oldWidth: Float,
        oldHeight: Float,
        oldPosX: Float,
        oldPosY: Float
    ) {
        boxWidth = oldWidth
        boxHeight = oldHeight
        toolPosition.x = oldPosX
        toolPosition.y = oldPosY
    }

    private fun createOverlayDrawable() {
        val overlayDrawableResource = toolType.overlayDrawableResource
        if (overlayDrawableResource != INVALID_RESOURCE_ID) {
            overlayDrawable = contextCallback.getDrawable(overlayDrawableResource)
            overlayDrawable?.isFilterBitmap = false
        }
    }

    fun highlightBox() {
        downTimer = object :
            CountDownTimer(
                CLICK_TIMEOUT_MILLIS,
                CLICK_TIMEOUT_MILLIS / CONSTANT_3
            ) {
            override fun onTick(millisUntilFinished: Long) {
                highlightBoxWhenClickInBox(true)
                workspace.invalidate()
            }

            override fun onFinish() {
                highlightBoxWhenClickInBox(false)
                workspace.invalidate()
                downTimer?.cancel()
            }
        }.start()
    }

    fun highlightBoxWhenClickInBox(highlight: Boolean) {
        @ColorRes val colorId =
            if (highlight) R.color.pocketpaint_main_rectangle_tool_highlight_color else R.color.pocketpaint_main_rectangle_tool_accent_color
        secondaryShapeColor = contextCallback.getColor(colorId)
        rectangleShrinkingOnHighlight =
            if (highlight) HIGHLIGHT_RECTANGLE_SHRINKING else DEFAULT_RECTANGLE_SHRINKING
    }

    override fun getAutoScrollDirection(
        pointX: Float,
        pointY: Float,
        screenWidth: Int,
        screenHeight: Int
    ): Point {
        return if (currentAction == FloatingBoxAction.MOVE || currentAction == FloatingBoxAction.RESIZE) {
            super.getAutoScrollDirection(pointX, pointY, screenWidth, screenHeight)
        } else Point(0, 0)
    }

    override fun onSaveInstanceState(bundle: Bundle?) {
        super.onSaveInstanceState(bundle)
        bundle?.apply {
            putFloat(BUNDLE_BOX_WIDTH, boxWidth)
            putFloat(BUNDLE_BOX_HEIGHT, boxHeight)
            putFloat(BUNDLE_BOX_ROTATION, boxRotation)
        }
    }

    override fun onRestoreInstanceState(bundle: Bundle?) {
        super.onRestoreInstanceState(bundle)
        bundle?.apply {
            boxWidth = getFloat(BUNDLE_BOX_WIDTH, boxWidth)
            boxHeight = getFloat(BUNDLE_BOX_HEIGHT, boxHeight)
            boxRotation = getFloat(BUNDLE_BOX_ROTATION, boxRotation)
        }
    }

    override fun drawToolSpecifics(canvas: Canvas, boxWidth: Float, boxHeight: Float) {
        var width = boxWidth
        var height = boxHeight
        linePaint.color = primaryShapeColor
        linePaint.strokeWidth = toolStrokeWidth * 2
        val rightTopPoint = PointF(
            -width / 2 + rectangleShrinkingOnHighlight,
            -height / 2 + rectangleShrinkingOnHighlight
        )
        repeat(SIDES) {
            val resizeLineLengthHeight = height / CONSTANT_1
            val resizeLineLengthWidth = width / CONSTANT_1
            canvas.run {
                drawLine(
                    rightTopPoint.x - toolStrokeWidth / 2,
                    rightTopPoint.y, rightTopPoint.x + resizeLineLengthWidth,
                    rightTopPoint.y,
                    linePaint
                )
                drawLine(
                    rightTopPoint.x,
                    rightTopPoint.y - toolStrokeWidth / 2,
                    rightTopPoint.x,
                    rightTopPoint.y + resizeLineLengthHeight,
                    linePaint
                )
                drawLine(
                    rightTopPoint.x + width / 2 - resizeLineLengthWidth,
                    rightTopPoint.y,
                    rightTopPoint.x + width / 2 + resizeLineLengthWidth,
                    rightTopPoint.y,
                    linePaint
                )
                rotate(RIGHT_ANGLE)
            }
            val tempX = rightTopPoint.x
            rightTopPoint.x = rightTopPoint.y
            rightTopPoint.y = tempX
            val tempHeight = height
            height = width
            width = tempHeight
        }
    }

    private enum class FloatingBoxAction {
        NONE, MOVE, RESIZE, ROTATE
    }

    private enum class ResizeAction {
        NONE, TOP, RIGHT, BOTTOM, LEFT, TOPLEFT, TOPRIGHT, BOTTOMLEFT, BOTTOMRIGHT
    }
}
