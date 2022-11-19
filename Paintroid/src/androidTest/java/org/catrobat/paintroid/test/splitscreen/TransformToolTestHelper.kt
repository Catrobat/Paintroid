package org.catrobat.paintroid.test.splitscreen

import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape

object TransformToolTestHelper {

    private var initialWidth = 0
    private var initialHeight = 0

    private lateinit var toolReference: ToolReference

    fun setupEnvironment(mainActivity: MainActivity) {
        toolReference = mainActivity.toolReference
        val workingBitmap = mainActivity.layerModel.currentLayer!!.bitmap
        initialWidth = workingBitmap.width
        initialHeight = workingBitmap.height
    }

    fun testMoveCroppingBordersOnEmptyBitmapAndDoCrop() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
            .performCloseToolOptionsView()
        val width = initialWidth / 2
        val height = initialHeight / 2
        setToolSelectionBoxDimensions(width.toFloat(), height.toFloat())
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkBitmapDimension(width, height)
            .checkLayerDimensions(width, height)
    }

    private fun setToolSelectionBoxDimensions(width: Float, height: Float) {
        val currentTool = toolReference.tool as BaseToolWithRectangleShape
        currentTool.boxWidth = width
        currentTool.boxHeight = height
    }
}