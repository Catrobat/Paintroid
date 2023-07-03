package org.catrobat.paintroid.tools.implementation

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.Log
import android.view.View
import androidx.test.espresso.idling.CountingIdlingResource
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.common.CommonBrushChangedListener
import org.catrobat.paintroid.tools.common.CommonBrushPreviewListener
import org.catrobat.paintroid.tools.options.BrushToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import org.catrobat.paintroid.ui.viewholder.TopBarViewHolder

class DynamicLineTool (
    private val brushToolOptionsView: BrushToolOptionsView,
    contextCallback: ContextCallback,
    toolOptionsViewController: ToolOptionsViewController,
    toolPaint: ToolPaint,
    workspace: Workspace,
    idlingResource: CountingIdlingResource,
    commandManager: CommandManager,
    override var drawTime: Long
) : BaseToolWithShape(
    contextCallback,
    toolOptionsViewController,
    toolPaint,
    workspace,
    idlingResource,
    commandManager
) {
    override var toolType: ToolType = ToolType.DYNAMICLINE
    var startCoordinate: PointF? = null
    var endCoordinate: PointF? = null
    var startCoordinateIsSet: Boolean = false

    init {
        brushToolOptionsView.setBrushChangedListener(CommonBrushChangedListener(this))
        brushToolOptionsView.setBrushPreviewListener(
                CommonBrushPreviewListener(
                        toolPaint,
                        toolType
                )
        )
        brushToolOptionsView.setCurrentPaint(toolPaint.paint)
        brushToolOptionsView.setStrokeCapButtonChecked(toolPaint.strokeCap)
        topBarViewHolder?.hidePlusButton()

    }

    override fun handleUpAnimations(coordinate: PointF?) {
        super.handleUp(coordinate)
    }

    override fun handleDownAnimations(coordinate: PointF?) {
        super.handleDown(coordinate)
    }

    override fun toolPositionCoordinates(coordinate: PointF): PointF = coordinate

    override fun draw(canvas: Canvas) {
        Log.e(TAG, "drawing")
        startCoordinate?.let { start ->
            endCoordinate?.let { end ->
                canvas.run {
                    save()
                    clipRect(0, 0, workspace.width, workspace.height)
                    drawLine(start.x, start.y, end.x, end.y, toolPaint.previewPaint)
                    restore()
                }
            }
        }
    }

    override fun drawShape(canvas: Canvas) {
        TODO("Not yet implemented")
    }

    override fun onClickOnButton() {
        Log.e(TAG, " ✓ clicked")
        startCoordinateIsSet = false
    }

    fun onClickOnPlus() {
        Log.e(TAG, "+ clicked")

    }

    private fun hideToolOptions() {
        if (toolOptionsViewController.isVisible) {
            if (brushToolOptionsView.getTopToolOptions().visibility == View.VISIBLE) {
                toolOptionsViewController.slideUp(
                    brushToolOptionsView.getTopToolOptions(),
                    willHide = true,
                    showOptionsView = false
                )
            }

            if (brushToolOptionsView.getBottomToolOptions().visibility == View.VISIBLE) {
                toolOptionsViewController.slideDown(
                    brushToolOptionsView.getBottomToolOptions(),
                    willHide = true,
                    showOptionsView = false
                )
            }
        }
    }

    private fun showToolOptions() {
        if (!toolOptionsViewController.isVisible) {
            if (brushToolOptionsView.getBottomToolOptions().visibility == View.INVISIBLE) {
                toolOptionsViewController.slideDown(
                    brushToolOptionsView.getTopToolOptions(),
                    willHide = false,
                    showOptionsView = true
                )
            }

            if (brushToolOptionsView.getBottomToolOptions().visibility == View.INVISIBLE) {
                toolOptionsViewController.slideUp(
                    brushToolOptionsView.getBottomToolOptions(),
                    willHide = false,
                    showOptionsView = true
                )
            }
        }
    }

    override fun handleDown(coordinate: PointF?): Boolean {
        coordinate ?: return false
        topBarViewHolder?.showPlusButton()
        startCoordinate = if (!startCoordinateIsSet) {
            copyPointF(coordinate).also { startCoordinateIsSet = true }
        } else {
            startCoordinate
        }
        super.handleDown(coordinate)
        return true
    }

    override fun handleMove(coordinate: PointF?): Boolean {
        coordinate ?: return false
        hideToolOptions()
        super.handleMove(coordinate)

        endCoordinate = copyPointF(coordinate)
        Log.e(TAG, endCoordinate!!.x.toString() + " " + endCoordinate!!.y.toString())
        return true
    }

    override fun handleUp(coordinate: PointF?): Boolean {
        showToolOptions()
        super.handleUp(coordinate)
        return true
    }

    override fun changePaintColor(color: Int) {
        super.changePaintColor(color)
        brushToolOptionsView.invalidate()
    }

    override fun changePaintStrokeWidth(strokeWidth: Int) {
        super.changePaintStrokeWidth(strokeWidth)
        brushToolOptionsView.invalidate()
    }

    override fun changePaintStrokeCap(cap: Paint.Cap) {
        super.changePaintStrokeCap(cap)
        brushToolOptionsView.invalidate()
    }

    private fun copyPointF(coordinate: PointF): PointF{
        return PointF(coordinate.x, coordinate.y)
    }

    companion object {
        var topBarViewHolder: TopBarViewHolder? = null
        const val TAG = "DynamicLineTool"
    }
}