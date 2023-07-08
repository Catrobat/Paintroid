package org.catrobat.paintroid.test.espresso.tools

/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2023 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.UiInteractions.swipeAccurate
import org.catrobat.paintroid.test.espresso.util.UiMatcher
import org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction.Companion.onColorPickerView
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.hamcrest.Matchers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RequiresApi(api = Build.VERSION_CODES.P)
@RunWith(AndroidJUnit4::class)
class BrushToolIntegrationTest {

    private lateinit var activity: MainActivity
    private var toolReference: ToolReference? = null

    @get:Rule
    var activityScenarioRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    private fun getActivity(): MainActivity {
        lateinit var activity: MainActivity
        activityScenarioRule.scenario.onActivity {
            activity = it
        }
        return activity
    }

    @Before
    fun setUp() {
        activity = getActivity()
        toolReference = activity.toolReference
        ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.BRUSH)
    }

    @Test
    fun drawOnlyOneLineWithSmoothingAlgorithm() {
        val commandManager = activity.commandManager
        val previousCommandCount = commandManager.getUndoCommandCount()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .perform(swipeAccurate(DrawingSurfaceLocationProvider.MIDDLE, DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT))
        val updatedCommandCount = commandManager.getUndoCommandCount()
        assertEquals(previousCommandCount + 1, updatedCommandCount)
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
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.MIDDLE,
                    DrawingSurfaceLocationProvider.BOTTOM_MIDDLE
                )
            )

        val selectedColor = toolReference?.tool?.drawPaint!!.color
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(selectedColor, BitmapLocationProvider.MIDDLE)
    }
}
