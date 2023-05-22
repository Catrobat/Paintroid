package org.catrobat.paintroid.test.espresso.tools

import android.graphics.Color
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.UiMatcher
import org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction.onColorPickerView
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BrushToolIntegrationTest {
    private var toolReference: ToolReference? = null

    @get:Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    @Before
    fun setUp() {
        toolReference = launchActivityRule.activity.toolReference
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
        onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(
            Matchers.allOf(
                withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_preset)
            )
        ).perform(ViewActions.click())
        Espresso.onView(withId(R.id.color_alpha_slider)).perform(
            ViewActions.scrollTo(),
            UiInteractions.touchCenterMiddle()
        )
        Espresso.onView(
            Matchers.allOf(
                withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        onColorPickerView()
            .onPositiveButton()
            .perform(ViewActions.click())
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))

        val selectedColor = toolReference?.tool?.drawPaint!!.color
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(selectedColor, BitmapLocationProvider.MIDDLE)
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
        onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(
            Matchers.allOf(
                withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_preset)
            )
        ).perform(ViewActions.click())
        Espresso.onView(withId(R.id.color_alpha_slider)).perform(
            ViewActions.scrollTo(),
            UiInteractions.touchCenterMiddle()
        )
        Espresso.onView(
            Matchers.allOf(
                withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        onColorPickerView()
            .onPositiveButton()
            .perform(ViewActions.click())
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.swipe(DrawingSurfaceLocationProvider.MIDDLE, DrawingSurfaceLocationProvider.BOTTOM_MIDDLE))

        val selectedColor = toolReference?.tool?.drawPaint!!.color
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(selectedColor, BitmapLocationProvider.MIDDLE)
    }
}
