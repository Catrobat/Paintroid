/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.paintroid.test.espresso.tools

import android.graphics.Color
import android.net.Uri
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.UiMatcher
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.implementation.DEFAULT_TOLERANCE_IN_PERCENT
import org.catrobat.paintroid.tools.implementation.FillTool
import org.catrobat.paintroid.ui.Perspective
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class FillToolIntegrationTest {
    @get:Rule
    var launchActivityRule = ActivityTestRule(
        MainActivity::class.java
    )

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    private var perspective: Perspective? = null
    private var toolReference: ToolReference? = null
    private var mainActivity: MainActivity? = null
    private var idlingResource: CountingIdlingResource? = null

    @Before
    fun setUp() {
        mainActivity = launchActivityRule.activity
        perspective = mainActivity?.perspective
        toolReference = mainActivity?.toolReference
        idlingResource = mainActivity?.idlingResource
        IdlingRegistry.getInstance().register(idlingResource)
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.FILL)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun testFloodFillIfImageLoaded() {
        mainActivity!!.model.savedPictureUri = Uri.fromFile(File("dummy"))
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.BLACK)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        mainActivity!!.model.savedPictureUri = null
    }

    @Test
    fun testBitmapIsFilled() {
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.BLACK)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testNothingHappensWhenClickedOutsideDrawingArea() {
        perspective!!.multiplyScale(.5f)
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.BLACK)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.OUTSIDE_MIDDLE_RIGHT))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
    }

    @Test
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

    @Test
    fun testFillToolOptionsDialog() {
        val fillTool = toolReference!!.tool as FillTool?
        Assert.assertEquals(
            "Wrong fill tool member value for color tolerance",
            fillTool!!.getToleranceAbsoluteValue(DEFAULT_TOLERANCE_IN_PERCENT).toDouble(),
            fillTool.colorTolerance.toDouble(),
            TOLERANCE_DELTA
        )
        ToolBarViewInteraction.onToolBarView()
            .performClickSelectedToolButton()
        val colorToleranceInput =
            Espresso.onView(withId(R.id.pocketpaint_fill_tool_dialog_color_tolerance_input))
        val colorToleranceSeekBar =
            Espresso.onView(withId(R.id.pocketpaint_color_tolerance_seek_bar))
        val testToleranceText = "100"
        colorToleranceInput.check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    DEFAULT_TOLERANCE_IN_PERCENT.toString()
                )
            )
        )
        colorToleranceInput.perform(
            ViewActions.replaceText(testToleranceText),
            ViewActions.closeSoftKeyboard()
        )
        colorToleranceInput.check(ViewAssertions.matches(ViewMatchers.withText(testToleranceText)))
        colorToleranceSeekBar.check(ViewAssertions.matches(UiMatcher.withProgress(testToleranceText.toInt())))
        val expectedAbsoluteTolerance = fillTool.getToleranceAbsoluteValue(100)
        Assert.assertEquals(
            "Wrong fill tool member value for color tolerance",
            expectedAbsoluteTolerance.toDouble(),
            fillTool.colorTolerance.toDouble(),
            TOLERANCE_DELTA
        )

        // Close tool options
        ToolBarViewInteraction.onToolBarView()
            .performClickSelectedToolButton()
    }

    @Test
    fun testFillToolDialogAfterToolSwitch() {
        val fillTool = toolReference!!.tool as FillTool?
        ToolBarViewInteraction.onToolBarView()
            .performClickSelectedToolButton()
        val colorToleranceInput =
            Espresso.onView(withId(R.id.pocketpaint_fill_tool_dialog_color_tolerance_input))
        val colorToleranceSeekBar =
            Espresso.onView(withId(R.id.pocketpaint_color_tolerance_seek_bar))
        val toleranceInPercent = 50
        colorToleranceInput.perform(ViewActions.replaceText(toleranceInPercent.toString()))
        val expectedAbsoluteTolerance = fillTool!!.getToleranceAbsoluteValue(toleranceInPercent)
        Assert.assertEquals(
            "Wrong fill tool member value for color tolerance",
            expectedAbsoluteTolerance.toDouble(),
            fillTool.colorTolerance.toDouble(),
            TOLERANCE_DELTA
        )

        // Close tool options
        ToolBarViewInteraction.onToolBarView()
            .performClickSelectedToolButton()
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.BRUSH)
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.FILL)
        ToolBarViewInteraction.onToolBarView()
            .performClickSelectedToolButton()
        colorToleranceInput.check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    DEFAULT_TOLERANCE_IN_PERCENT.toString()
                )
            )
        )
        colorToleranceSeekBar.check(
            ViewAssertions.matches(
                UiMatcher.withProgress(
                    DEFAULT_TOLERANCE_IN_PERCENT
                )
            )
        )
    }

    @Test
    fun testFillToolUndoRedoWithTolerance() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.BRUSH)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        ToolPropertiesInteraction.onToolProperties()
            .setColorResource(R.color.pocketpaint_color_picker_brown2)
            .checkMatchesColorResource(R.color.pocketpaint_color_picker_brown2)
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.FILL)
        Espresso.onView(withId(R.id.pocketpaint_fill_tool_dialog_color_tolerance_input))
            .perform(ViewActions.replaceText(100.toString()))
        ToolBarViewInteraction.onToolBarView()
            .performCloseToolOptionsView()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColorResource(
                R.color.pocketpaint_color_picker_brown2,
                BitmapLocationProvider.MIDDLE
            )
            .checkPixelColorResource(
                R.color.pocketpaint_color_picker_brown2,
                BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE
            )
        TopBarViewInteraction.onTopBarView()
            .performUndo()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        TopBarViewInteraction.onTopBarView()
            .performRedo()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColorResource(
                R.color.pocketpaint_color_picker_brown2,
                BitmapLocationProvider.MIDDLE
            )
            .checkPixelColorResource(
                R.color.pocketpaint_color_picker_brown2,
                BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE
            )
    }

    companion object {
        private const val TOLERANCE_DELTA = 0.05
    }
}
