package org.catrobat.paintroid.test.splitscreen

import android.graphics.Color
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ClipboardToolViewInteraction.Companion.onClipboardToolViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.implementation.ClipboardTool

object ClipboardToolTestHelper {

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
            .performSelectTool(ToolType.CLIPBOARD)
        onClipboardToolViewInteraction()
            .performCut()
        val clipboardTool = toolReference.tool as ClipboardTool
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, clipboardTool.toolPosition.x, clipboardTool.toolPosition.y)
        onClipboardToolViewInteraction()
            .performPaste()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, clipboardTool.toolPosition.x, clipboardTool.toolPosition.y)
    }
}
