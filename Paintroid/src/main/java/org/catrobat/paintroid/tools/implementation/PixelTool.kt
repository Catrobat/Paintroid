package org.catrobat.paintroid.tools.implementation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.widget.Toast
import androidx.test.espresso.idling.CountingIdlingResource
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.options.PixelationToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsViewController

class PixelTool(
    pixelToolOptionsView : PixelationToolOptionsView,
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


    init {
        rotationEnabled = true
        this.pixelToolOptionsView = pixelToolOptionsView
        setBitmap(Bitmap.createBitmap(boxWidth.toInt(), boxHeight.toInt(), Bitmap.Config.ARGB_8888))
        toolOptionsViewController.showDelayed()
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
    }

    override fun resetInternalState() = Unit

}