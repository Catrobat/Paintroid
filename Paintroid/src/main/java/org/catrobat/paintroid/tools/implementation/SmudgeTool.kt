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
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import androidx.annotation.VisibleForTesting
import androidx.test.espresso.idling.CountingIdlingResource
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.common.CommonBrushChangedListener
import org.catrobat.paintroid.tools.common.CommonBrushPreviewListener
import org.catrobat.paintroid.tools.options.SmudgeToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import kotlin.math.sqrt

const val PERCENT_100 = 100f
const val BITMAP_ROTATION_FACTOR = -0.0f
const val DEFAULT_PRESSURE_IN_PERCENT = 50
const val MAX_PRESSURE = 1f
const val MIN_PRESSURE = 0.85f
const val DEFAULT_DRAG_IN_PERCENT = 50
const val DISTANCE_SMOOTHING = 3f
const val DRAW_THRESHOLD = 0.8f
const val PRESSURE_UPDATE_STEP = 0.004f

class SmudgeTool(
    smudgeToolOptionsView: SmudgeToolOptionsView,
    contextCallback: ContextCallback,
    toolOptionsViewController: ToolOptionsViewController,
    toolPaint: ToolPaint,
    workspace: Workspace,
    idlingResource: CountingIdlingResource,
    commandManager: CommandManager
) : BaseTool(contextCallback, toolOptionsViewController, toolPaint, workspace, idlingResource, commandManager) {

    override var drawTime: Long = 0
    override fun handleUpAnimations(coordinate: PointF?) {
        super.handleUp(coordinate)
    }

    override fun handleDownAnimations(coordinate: PointF?) {
        super.handleDown(coordinate)
    }

    private var currentBitmap: Bitmap? = null
    private var prevPoint: PointF? = null
    private var numOfPointsOnPath = -1

    @VisibleForTesting
    var maxPressure = 0f

    @VisibleForTesting
    var pressure = maxPressure

    @VisibleForTesting
    var maxSmudgeSize = toolPaint.strokeWidth

    @VisibleForTesting
    var minSmudgeSize = 0f

    @VisibleForTesting
    val pointArray = mutableListOf<PointF>()

    override val toolType: ToolType
        get() = ToolType.SMUDGE

    init {
        smudgeToolOptionsView.setBrushChangedListener(CommonBrushChangedListener(this))
        smudgeToolOptionsView.setBrushPreviewListener(
            CommonBrushPreviewListener(
                toolPaint,
                toolType
            )
        )
        smudgeToolOptionsView.setCurrentPaint(toolPaint.paint)
        smudgeToolOptionsView.setStrokeCapButtonChecked(toolPaint.strokeCap)
        smudgeToolOptionsView.setCallback(object : SmudgeToolOptionsView.Callback {
            override fun onPressureChanged(pressure: Int) {
                updatePressure(pressure)
            }

            override fun onDragChanged(drag: Int) {
                updateDrag(drag)
            }
        })

        updatePressure(DEFAULT_PRESSURE_IN_PERCENT)
        updateDrag(DEFAULT_DRAG_IN_PERCENT)
    }

    fun updatePressure(pressureInPercent: Int) {
        val onePercent = (MAX_PRESSURE - MIN_PRESSURE) / PERCENT_100
        maxPressure = MIN_PRESSURE + onePercent * pressureInPercent
        pressure = maxPressure
    }

    fun updateDrag(dragInPercent: Int) {
        val onePercent = maxSmudgeSize / PERCENT_100
        minSmudgeSize = onePercent * dragInPercent
    }

    override fun handleDown(coordinate: PointF?): Boolean {
        coordinate ?: return false

        if (maxSmudgeSize != toolPaint.strokeWidth) {
            val ratio = minSmudgeSize / maxSmudgeSize
            maxSmudgeSize = toolPaint.strokeWidth
            minSmudgeSize = maxSmudgeSize * ratio
        }

        val layerBitmap = workspace.bitmapOfCurrentLayer
        currentBitmap = Bitmap.createBitmap(
            maxSmudgeSize.toInt(),
            maxSmudgeSize.toInt(),
            Bitmap.Config.ARGB_8888
        )
        currentBitmap?.let {
            Canvas(it).apply {
                translate(-coordinate.x + maxSmudgeSize / 2f, -coordinate.y + maxSmudgeSize / 2f)
                rotate(BITMAP_ROTATION_FACTOR, coordinate.x, coordinate.y)
                layerBitmap?.let { bitmap ->
                    drawBitmap(bitmap, 0f, 0f, null)
                }
            }

            if (toolPaint.strokeCap == Paint.Cap.ROUND) {
                currentBitmap = getBitmapClippedCircle(it)
            }
        }

        if (!currentBitmapHasColor()) {
            currentBitmap?.recycle()
            currentBitmap = null
            return false
        }

        prevPoint = PointF(coordinate.x, coordinate.y)
        prevPoint?.apply {
            pointArray.add(PointF(x, y))
        }

        return true
    }

    override fun handleMove(coordinate: PointF?, shouldAnimate: Boolean): Boolean {
        coordinate ?: return false

        if (currentBitmap != null) {
            if (pressure < DRAW_THRESHOLD) { // Needed to stop drawing preview when bitmap becomes too transparent. Has no effect on final drawing.
                return false
            }

            prevPoint?.apply {
                val x1 = coordinate.x - x
                val y1 = coordinate.y - y

                val distance = (sqrt(x1 * x1 + y1 * y1) / DISTANCE_SMOOTHING).toInt()
                val xInterval = x1 / distance
                val yInterval = y1 / distance

                repeat(distance) {
                    x += xInterval
                    y += yInterval

                    pressure -= PRESSURE_UPDATE_STEP

                    pointArray.add(PointF(x, y))
                }
            }
            return true
        } else {
            return false
        }
    }

    private fun getBitmapClippedCircle(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val path = Path()
        path.addCircle(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            kotlin.math.min(width, height / 2).toFloat(),
            Path.Direction.CCW
        )
        val canvas = Canvas(outputBitmap)
        canvas.clipPath(path)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        return outputBitmap
    }

    private fun currentBitmapHasColor(): Boolean {
        currentBitmap?.apply {
            for (x in 0 until width) {
                for (y in 0 until height) {
                    if (getPixel(x, y) != 0) {
                        return true
                    }
                }
            }
        }
        return false
    }

    override fun handleUp(coordinate: PointF?): Boolean {
        coordinate ?: return false

        if (pointArray.isNotEmpty() && currentBitmap != null) {
            currentBitmap?.let {
                val command = commandFactory.createSmudgePathCommand(
                    it,
                    pointArray,
                    maxPressure,
                    maxSmudgeSize,
                    minSmudgeSize
                )
                commandManager.addCommand(command)
            }

            numOfPointsOnPath = if (numOfPointsOnPath < 0) {
                pointArray.size
            } else {
                (numOfPointsOnPath + pointArray.size) / 2
            }

            pressure = maxPressure
            pointArray.clear()
            currentBitmap?.recycle()
            currentBitmap = null
            return true
        } else {
            return false
        }
    }

    override fun toolPositionCoordinates(coordinate: PointF): PointF = coordinate

    override fun draw(canvas: Canvas) {
        if (pointArray.isNotEmpty()) {
            val pointPath = pointArray.toMutableList()

            val step = if (numOfPointsOnPath < 0) {
                (maxSmudgeSize - minSmudgeSize) / pointPath.size
            } else {
                (maxSmudgeSize - minSmudgeSize) / numOfPointsOnPath
            }

            var size = maxSmudgeSize
            var pressure = maxPressure
            val colorMatrix = ColorMatrix()
            val paint = Paint()
            var bitmap = currentBitmap?.copy(Bitmap.Config.ARGB_8888, false)

            pointPath.forEach {
                colorMatrix.setScale(1f, 1f, 1f, pressure)
                paint.colorFilter = ColorMatrixColorFilter(colorMatrix)

                val newBitmap = Bitmap.createBitmap(
                    maxSmudgeSize.toInt(),
                    maxSmudgeSize.toInt(),
                    Bitmap.Config.ARGB_8888
                )

                Canvas(newBitmap).apply {
                    bitmap?.let { currentBitmap ->
                        drawBitmap(currentBitmap, 0f, 0f, paint)
                    }
                }

                bitmap?.recycle()
                bitmap = newBitmap

                val rect = RectF(-size / 2f, -size / 2f, size / 2f, size / 2f)
                with(canvas) {
                    save()
                    clipRect(0, 0, workspace.width, workspace.height)
                    translate(it.x, it.y)
                    bitmap?.let { currentBitmap ->
                        drawBitmap(currentBitmap, null, rect, Paint(Paint.DITHER_FLAG))
                    }
                    restore()
                }
                size -= step
                pressure -= PRESSURE_UPDATE_STEP
            }

            bitmap?.recycle()
        }
    }
}
