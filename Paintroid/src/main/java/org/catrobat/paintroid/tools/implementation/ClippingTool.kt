package org.catrobat.paintroid.tools.implementation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.PointF
import androidx.test.espresso.idling.CountingIdlingResource
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.Tool
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.common.CommonBrushChangedListener
import org.catrobat.paintroid.tools.common.CommonBrushPreviewListener
import org.catrobat.paintroid.tools.options.BrushToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsViewController

const val TWO = 2f
const val TWO_AND_A_HALF = 2.5f
const val CONTOUR_SIZE = 5

class ClippingTool(
    brushToolOptionsView: BrushToolOptionsView,
    contextCallback: ContextCallback,
    toolOptionsViewController: ToolOptionsViewController,
    toolPaint: ToolPaint,
    workspace: Workspace,
    idlingResource: CountingIdlingResource,
    commandManager: CommandManager,
    drawTime: Long,
    var mainActivity: MainActivity
) : BrushTool(
    brushToolOptionsView,
    contextCallback,
    toolOptionsViewController,
    toolPaint,
    workspace,
    idlingResource,
    commandManager,
    false,
    drawTime
) {
    private var newBitmap: Bitmap? = null
    var wasRecentlyApplied: Boolean = false
    var areaClosed = false
    var clippingPaint = Paint()
    private val pathLineLength: Float
        get() = toolPaint.paint.strokeWidth * TWO_AND_A_HALF
    private val pathGapLength: Float
        get() = toolPaint.paint.strokeWidth * TWO
    override val bitmapPaint: Paint
        get() = toolPaint.paint

    override val previewPaint: Paint
        get() = toolPaint.previewPaint

    override val toolType: ToolType
        get() = ToolType.CLIP

    init {
        toolPaint.paint.strokeWidth = STROKE_10
        toolPaint.paint.style = Paint.Style.STROKE
        toolPaint.paint.pathEffect = DashPathEffect(floatArrayOf(pathLineLength, pathGapLength), 0f)
        brushToolOptionsView.setBrushChangedListener(CommonBrushChangedListener(this))
        brushToolOptionsView.setBrushPreviewListener(
            CommonBrushPreviewListener(
                toolPaint,
                toolType
            )
        )
        brushToolOptionsView.setCurrentPaint(toolPaint.paint)
        brushToolOptionsView.invalidate()
        toolOptionsViewController.showCheckmark()
        brushToolOptionsView.hideCaps()
        copyBitmapOfCurrentLayer()
    }

    override fun draw(canvas: Canvas) {
        clippingPaint = toolPaint.previewPaint
        clippingPaint.pathEffect = toolPaint.paint.pathEffect
        clippingPaint.color = if (toolPaint.previewColor == Color.BLACK) Color.WHITE else Color.BLACK
        clippingPaint.strokeWidth = toolPaint.paint.strokeWidth + CONTOUR_SIZE
        idlingResource.increment()
        canvas.run {
            save()
            clipRect(0, 0, workspace.width, workspace.height)
            drawPath(pathToDraw, clippingPaint)
            drawPath(pathToDraw, toolPaint.paint)
            restore()
        }
        idlingResource.decrement()
    }

    fun copyBitmapOfCurrentLayer() {
        if (workspace.bitmapOfCurrentLayer != null) {
            newBitmap = workspace.bitmapOfCurrentLayer?.copy(workspace.bitmapOfCurrentLayer?.config, true)
        }
    }

    override fun handleDown(coordinate: PointF?): Boolean {
        if (areaClosed) {
            super.resetInternalState()
            areaClosed = false
            commandManager.undoInClippingTool()
            changePaintColor(toolPaint.previewPaint.color)
            brushToolOptionsView.setCurrentPaint(toolPaint.paint)
            brushToolOptionsView.invalidate()
            mainActivity.bottomNavigationViewHolder.setColorButtonColor(toolPaint.previewColor)
        }
        return super.handleDown(coordinate)
    }

    override fun handleUp(coordinate: PointF?): Boolean {
        val tempPoint = initialEventCoordinate
        if (previousEventCoordinate == initialEventCoordinate) {
            super.resetInternalState()
            return false
        }
        if (!areaClosed && coordinate != null && tempPoint != null) {
            pathToDraw.incReserve(1)
            pathToDraw.quadTo(coordinate.x, coordinate.y, tempPoint.x, tempPoint.y)
            pointArray.add(PointF(coordinate.x, coordinate.y))
            areaClosed = true
        }
        return super.handleUp(coordinate)
    }

    fun onClickOnButton() {
        idlingResource.increment()
        if (areaClosed) {
            val pathBitmap =
                newBitmap?.config?.let {
                    newBitmap?.width?.let { it1 ->
                        newBitmap?.height?.let { it2 ->
                            Bitmap.createBitmap(
                                it1, it2,
                                it
                            )
                        }
                    }
                }
            val canvas: Canvas?
            val paint = Paint()
            paint.color = Color.BLACK
            paint.style = Paint.Style.FILL
            if (pathBitmap != null) {
                canvas = Canvas(pathBitmap)
                canvas.drawPath(pathToDraw, paint)
            }

            newBitmap?.let {
                val command = pathBitmap?.let { it1 ->
                    commandFactory.createClippingCommand(
                        it,
                        it1
                    )
                }
                commandManager.addCommand(command)
                commandManager.adjustUndoListForClippingTool()
            }
            areaClosed = false
            wasRecentlyApplied = true
            pathToDraw.rewind()
        }
        idlingResource.decrement()
    }

    override fun resetInternalState(stateChange: Tool.StateChange) {
        if (stateChange == Tool.StateChange.NEW_IMAGE_LOADED) {
            areaClosed = false
            super.resetInternalState()
            copyBitmapOfCurrentLayer()
        } else if (stateChange == Tool.StateChange.RESET_INTERNAL_STATE && wasRecentlyApplied) {
            copyBitmapOfCurrentLayer()
            wasRecentlyApplied = false
        }
    }

    override fun changePaintStrokeWidth(strokeWidth: Int) {
        super.changePaintStrokeWidth(strokeWidth)
        toolPaint.paint.pathEffect = DashPathEffect(floatArrayOf(pathLineLength, pathGapLength), 0f)
    }
}
