package org.catrobat.paintroid.test.splitscreen

import android.graphics.Color
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction
import org.catrobat.paintroid.tools.ToolType

object PipetteToolTestHelper {
    fun testPipetteToolAfterBrushOnSingleLayer() {
        ToolPropertiesInteraction.onToolProperties()
            .setColor(Color.RED)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.RED, BitmapLocationProvider.MIDDLE)
        ToolPropertiesInteraction.onToolProperties()
            .setColorResource(R.color.pocketpaint_color_picker_transparent)
            .checkMatchesColor(Color.TRANSPARENT)
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.PIPETTE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.RED)
    }
}
