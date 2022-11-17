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

object FillToolTestHelper {
    fun testBitmapIsFilled() {
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.BLACK)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
    }

    fun testOnlyFillInnerArea() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.BRUSH)
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.BLACK)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(
                UiInteractions.swipeAccurate(
                    DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE,
                    DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE
                )
            )
            .perform(
                UiInteractions.swipeAccurate(
                    DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE,
                    DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE
                )
            )
            .perform(
                UiInteractions.swipeAccurate(
                    DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE,
                    DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE
                )
            )
            .perform(
                UiInteractions.swipeAccurate(
                    DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE,
                    DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE
                )
            )
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.FILL)
        ToolPropertiesInteraction.onToolProperties()
            .setColorResource(R.color.pocketpaint_color_picker_green1)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColorResource(
                R.color.pocketpaint_color_picker_green1,
                BitmapLocationProvider.MIDDLE
            )
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE_RIGHT)
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
    }
}
