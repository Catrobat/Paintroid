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
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.common.CommonBrushChangedListener
import org.catrobat.paintroid.tools.common.CommonBrushPreviewListener
import org.catrobat.paintroid.tools.options.SmudgeToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsViewController

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
    commandManager: CommandManager
) : BaseTool(contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager) {

    override var drawTime: Long = 0
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
                drawBitmap(layerBitmap!!, 0f, 0f, null)
            }
        }

        if (toolPaint.strokeCap == Paint.Cap.ROUND) {
            currentBitmap = getBitmapClippedCircle(currentBitmap!!)
        }

        if (!currentBitmapHasColor()) {
            currentBitmap?.recycle()
            currentBitmap = null
            return false
        }

        prevPoint = PointF(coordinate.x, coordinate.y)
        pointArray.add(PointF(prevPoint!!.x, prevPoint!!.y))

        return true
    }

    override fun handleMove(coordinate: PointF?): Boolean {
        coordinate ?: return false
        if (currentBitmap != null) {
            if (pressure < DRAW_THRESHOLD) { // Needed to stop drawing preview when bitmap becomes too transparent. Has no effect on final drawing.
                return false
            }

            val x = coordinate.x - prevPoint!!.x
            val y = coordinate.y - prevPoint!!.y

            val distance = kotlin.math.floor(kotlin.math.sqrt(x * x + y * y) / DISTANCE_SMOOTHING)
            val xInterval = x / distance
            val yInterval = y / distance

            var i = 0
            while (i < distance) {
                prevPoint!!.x += xInterval
                prevPoint!!.y += yInterval

                pressure -= PRESSURE_UPDATE_STEP

                pointArray.add(PointF(prevPoint!!.x, prevPoint!!.y))

                i++
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
        for (x in 0 until currentBitmap!!.width) {
            for (y in 0 until currentBitmap!!.height) {
                if (currentBitmap!!.getPixel(x, y) != 0) {
                    return true
                }
            }
        }
        return false
    }

    override fun handleUp(coordinate: PointF?): Boolean {
        coordinate ?: return false
        if (!pointArray.isEmpty() && currentBitmap != null) {
            val command = commandFactory.createSmudgePathCommand(
                currentBitmap!!,
                pointArray,
                maxPressure,
                maxSmudgeSize,
                minSmudgeSize
            )
            commandManager.addCommand(command)

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
            var bitmap = currentBitmap!!.copy(Bitmap.Config.ARGB_8888, false)

            pointPath.forEach {
                colorMatrix.setScale(1f, 1f, 1f, pressure)
                paint.colorFilter = ColorMatrixColorFilter(colorMatrix)

                val newBitmap = Bitmap.createBitmap(
                    maxSmudgeSize.toInt(),
                    maxSmudgeSize.toInt(),
                    Bitmap.Config.ARGB_8888
                )

                Canvas(newBitmap).apply {
                    drawBitmap(bitmap, 0f, 0f, paint)
                }

                bitmap.recycle()
                bitmap = newBitmap

                val rect = RectF(-size / 2f, -size / 2f, size / 2f, size / 2f)
                with(canvas) {
                    save()
                    clipRect(0, 0, workspace.width, workspace.height)
                    translate(it.x, it.y)
                    drawBitmap(bitmap, null, rect, Paint(Paint.DITHER_FLAG))
                    restore()
                }
                size -= step
                pressure -= PRESSURE_UPDATE_STEP
            }

            bitmap.recycle()
        }
    }

    override fun changePaintColor(color: Int) {
        // Doesn't need to change the color.
    }
}
