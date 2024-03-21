package org.catrobat.paintroid.test.splitscreen

import android.graphics.Color
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.OffsetLocationProvider
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ShapeToolOptionsViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.drawable.DrawableShape
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape

object ShapeToolTestHelper {
    private lateinit var toolReference: ToolReference

    fun setupEnvironment(mainActivity: MainActivity) {
        toolReference = mainActivity.toolReference
    }

    fun testEllipseIsDrawnOnBitmap() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.SHAPE)
        ShapeToolOptionsViewInteraction.onShapeToolOptionsView()
            .performSelectShape(DrawableShape.OVAL)
        val ellipseTool = toolReference.tool as BaseToolWithRectangleShape
        val rectHeight = ellipseTool.boxHeight
        ToolBarViewInteraction.onToolBarView()
            .performCloseToolOptionsView()
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
            .checkPixelColor(
                Color.BLACK,
                OffsetLocationProvider.withOffset(
                    BitmapLocationProvider.MIDDLE,
                    (rectHeight / 2.5f).toInt(),
                    0
                )
            )
            .checkPixelColor(
                Color.TRANSPARENT,
                OffsetLocationProvider.withOffset(
                    BitmapLocationProvider.MIDDLE,
                    (rectHeight / 2.5f).toInt(),
                    (rectHeight / 2.5f).toInt()
                )
            )
    }
}
