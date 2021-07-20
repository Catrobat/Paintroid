package org.catrobat.paintroid.tools.implementation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.PointF
import android.graphics.PorterDuff
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.common.CommonBrushChangedListener
import org.catrobat.paintroid.tools.common.CommonBrushPreviewListener
import org.catrobat.paintroid.tools.options.BrushToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsVisibilityController

class SmudgeTool (
    private val brushToolOptionsView: BrushToolOptionsView,
    contextCallback: ContextCallback,
    toolOptionsViewController: ToolOptionsVisibilityController,
    toolPaint: ToolPaint,
    workspace: Workspace,
    commandManager: CommandManager
) : BaseTool(contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager) {

    val softness = 0.5
    override var drawTime: Long = 0
    private var currentBitmap : Bitmap? = null
    private var layerBitmap : Bitmap? = null
    private var initialEventCoordinate: PointF? = null


    override val toolType: ToolType
        get() = ToolType.SMUDGE

    init {
        brushToolOptionsView.setBrushChangedListener(CommonBrushChangedListener(this))
        brushToolOptionsView.setBrushPreviewListener(
            CommonBrushPreviewListener(
                toolPaint,
                toolType
            )
        )
        brushToolOptionsView.setCurrentPaint(toolPaint.paint)
    }

    override fun handleDown(coordinate: PointF?): Boolean {
        coordinate ?: return false
        //set bitmap
        initialEventCoordinate = PointF(coordinate.x, coordinate.y)
        layerBitmap = workspace.bitmapOfCurrentLayer
        currentBitmap = Bitmap.createBitmap(toolPaint.strokeWidth.toInt() * 2, toolPaint.strokeWidth.toInt() * 2, Bitmap.Config.ARGB_8888)

        currentBitmap.let {
            if (it != null) {
                Canvas(it).apply {
                    translate(-coordinate.x + toolPaint.strokeWidth / 2, -coordinate.y + toolPaint.strokeWidth / 2)
                    rotate(-0.0f, coordinate.x, coordinate.y)
                    drawBitmap(layerBitmap!!, 0f, 0f, null)
                }
            }
        }

        return true
    }

    override fun handleMove(coordinate: PointF?): Boolean {
        coordinate ?: return false
        layerBitmap = workspace.bitmapOfCurrentLayer
        val colour = 150 and 0xFF shl 24


        val newBitmap = Bitmap.createBitmap(toolPaint.strokeWidth.toInt() * 2, toolPaint.strokeWidth.toInt() * 2, Bitmap.Config.ARGB_8888)
        newBitmap.let {
            if (it != null) {
                Canvas(it).apply {
                    translate(-coordinate.x + toolPaint.strokeWidth / 2, -coordinate.y + toolPaint.strokeWidth / 2)
                    rotate(-0.0f, coordinate.x, coordinate.y)
                    drawBitmap(layerBitmap!!, 0f, 0f, null)
                    drawColor(colour, PorterDuff.Mode.DST_IN)
                }
            }
        }


        val result = Bitmap.createBitmap(newBitmap.width, newBitmap.height, newBitmap.config)
        val canvasResult = Canvas(result)
        canvasResult.drawBitmap(newBitmap, 0.0f, 0.0f, null)
        currentBitmap?.let {
            canvasResult.drawBitmap(it, 0.0f, 0.0f, null)
            canvasResult.drawColor(colour, PorterDuff.Mode.DST_IN)
        }

        val command = commandFactory.createStampCommand(
            // getBitmapClippedCircle(result),
            result,
            coordinate,
            toolPaint.strokeWidth,
            toolPaint.strokeWidth,
            0.0f
        )
        commandManager.addCommand(command)

        currentBitmap?.recycle()
        currentBitmap = null
        currentBitmap = result

        return true
    }

    private fun getBitmapClippedCircle(bitmap: Bitmap): Bitmap? {
        val width = bitmap.width
        val height = bitmap.height
        val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val path = Path()
        path.addCircle(
            (width / 2).toFloat()
            , (height / 2).toFloat()
            , Math.min(width, height / 2).toFloat()
            , Path.Direction.CCW
        )
        val canvas = Canvas(outputBitmap)
        canvas.clipPath(path)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        return outputBitmap
    }

    override fun handleUp(coordinate: PointF?): Boolean {
        coordinate ?: return false

        currentBitmap?.recycle()
        currentBitmap = null
        return true
    }

    override fun draw(canvas: Canvas) {
        canvas.run {
        }
    }

    override fun changePaintColor(color: Int) {
        super.changePaintColor(color)
        brushToolOptionsView.invalidate()
    }
}