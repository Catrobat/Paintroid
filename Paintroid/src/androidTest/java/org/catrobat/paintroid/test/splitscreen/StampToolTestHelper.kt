package org.catrobat.paintroid.test.splitscreen

import android.graphics.Color
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.StampToolViewInteraction.Companion.onStampToolViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.implementation.StampTool

object StampToolTestHelper {

    private lateinit var toolReference: ToolReference

    fun setupEnvironment(mainActivity: MainActivity) {
        toolReference = mainActivity.toolReference
    }

    fun testCutAndPastePixel() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.BRUSH)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.STAMP)
        onStampToolViewInteraction()
            .performCut()
        val stampTool = toolReference.tool as StampTool
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, stampTool.toolPosition.x, stampTool.toolPosition.y)
        onStampToolViewInteraction()
            .performPaste()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, stampTool.toolPosition.x, stampTool.toolPosition.y)
    }
}
