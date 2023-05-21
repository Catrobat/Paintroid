package org.catrobat.paintroid.test.espresso.tools

import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolType
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BrushToolIntegrationTest {
    private val transparentColor = Color.parseColor("#3B000000")

    @get:Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    @Before
    fun setUp() {
        ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.BRUSH)
    }

    @Test
    fun testBrushToolColor() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.BRUSH)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testBrushToolTransparentColor() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.BRUSH)
        ToolPropertiesInteraction.onToolProperties()
            .setColor(transparentColor)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(transparentColor, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testBrushToolWithHandleMoveColor() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.BRUSH)
        ToolPropertiesInteraction.onToolProperties()
            .setColor(Color.BLACK)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.swipe(DrawingSurfaceLocationProvider.MIDDLE, DrawingSurfaceLocationProvider.BOTTOM_MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testBrushToolWithHandleMoveTransparentColor() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.BRUSH)
        ToolPropertiesInteraction.onToolProperties()
            .setColor(transparentColor)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.swipe(DrawingSurfaceLocationProvider.MIDDLE, DrawingSurfaceLocationProvider.BOTTOM_MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(transparentColor, BitmapLocationProvider.MIDDLE)
    }
}
