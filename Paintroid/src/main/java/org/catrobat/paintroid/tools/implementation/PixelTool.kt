package org.catrobat.paintroid.tools.implementation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.test.espresso.idling.CountingIdlingResource
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.options.PixelationToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsViewController

class PixelTool(
    pixelToolOptionsViewParam : PixelationToolOptionsView,
    contextCallback : ContextCallback,
    toolOptionsViewController: ToolOptionsViewController,
    toolPaint: ToolPaint,
    workspace: Workspace,
    idlingResource: CountingIdlingResource,
    commandManager: CommandManager,
    override var drawTime: Long
) : BaseToolWithRectangleShape(contextCallback, toolOptionsViewController,toolPaint, workspace, idlingResource, commandManager)
{
    private val pixelToolOptionsView: PixelationToolOptionsView
    @VisibleForTesting
    @JvmField
    var numPixelHeight = 0f

    @VisibleForTesting
    @JvmField
    var numPixelWidth = 0f

    @VisibleForTesting
    @JvmField
    var numCollors = 0f

    init {
        boxHeight = workspace.height.toFloat()
        boxWidth = workspace.width.toFloat()
        toolPosition.x = boxWidth / 2f
        toolPosition.y = boxHeight / 2f
        this.pixelToolOptionsView = pixelToolOptionsViewParam
        setBitmap(Bitmap.createBitmap(boxWidth.toInt(), boxHeight.toInt(), Bitmap.Config.ARGB_8888))
        toolOptionsViewController.showDelayed()
        this.pixelToolOptionsView.setPixelPreviewListener(object  : PixelationToolOptionsView.OnPixelationPreviewListener {
            override fun setPixelWidth(widthPixels: Float) {
                    this@PixelTool.numPixelWidth = widthPixels
            }

            override fun setPixelHeight(heightPixels: Float) {
               this@PixelTool.numPixelHeight = heightPixels
            }

            override fun setNumCollor(collorNum: Float) {
                this@PixelTool.numCollors = collorNum
            }

        })
    }


    override val toolType: ToolType
        get() = ToolType.PIXEL

    override fun handleUpAnimations(coordinate: PointF?) {
        super.handleUp(coordinate)
    }

    override fun handleDownAnimations(coordinate: PointF?) {
        super.handleDown(coordinate)
    }

    override fun toolPositionCoordinates(coordinate: PointF): PointF  = coordinate

    // is the checkmark to run the programm
    override fun onClickOnButton() {
        // test if the  ui works good then shoudl be enought for the 30.8
        var i = 10

    }

    override fun resetInternalState() = Unit

}