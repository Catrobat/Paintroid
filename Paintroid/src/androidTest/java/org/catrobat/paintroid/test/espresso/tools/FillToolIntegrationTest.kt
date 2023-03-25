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
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry.getInstance
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.UiInteractions.swipeAccurate
import org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt
import org.catrobat.paintroid.test.espresso.util.UiMatcher.withProgress
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.Companion.onDrawingSurfaceView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction.Companion.onToolProperties
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.implementation.DEFAULT_TOLERANCE_IN_PERCENT
import org.catrobat.paintroid.tools.implementation.FillTool
import org.catrobat.paintroid.ui.Perspective
import org.junit.*
import org.junit.Assert.assertEquals
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
        getInstance().register(idlingResource)
        onToolBarView()
            .performSelectTool(ToolType.FILL)
    }

    @After
    fun tearDown() {
        getInstance().unregister(idlingResource)
    }

    @Test
    fun testFloodFillIfImageLoaded() {
        mainActivity!!.model.savedPictureUri = Uri.fromFile(File("dummy"))
        onToolProperties()
            .checkMatchesColor(Color.BLACK)
        onDrawingSurfaceView()
            .perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        mainActivity!!.model.savedPictureUri = null
    }

    @Test
    fun testBitmapIsFilled() {
        onToolProperties()
            .checkMatchesColor(Color.BLACK)
        onDrawingSurfaceView()
            .perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testNothingHappensWhenClickedOutsideDrawingArea() {
        perspective!!.multiplyScale(.5f)
        onToolProperties()
            .checkMatchesColor(Color.BLACK)
        onDrawingSurfaceView()
            .perform(touchAt(DrawingSurfaceLocationProvider.OUTSIDE_MIDDLE_RIGHT))
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testOnlyFillInnerArea() {
        onToolBarView()
            .performSelectTool(ToolType.BRUSH)
        onToolProperties()
            .checkMatchesColor(Color.BLACK)
        onDrawingSurfaceView()
            .perform(
                swipeAccurate(
                    DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE,
                    DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE
                )
            )
            .perform(
                swipeAccurate(
                    DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE,
                    DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE
                )
            )
            .perform(
                swipeAccurate(
                    DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE,
                    DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE
                )
            )
            .perform(
                swipeAccurate(
                    DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE,
                    DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE
                )
            )
        onToolBarView()
            .performSelectTool(ToolType.FILL)
        onToolProperties()
            .setColorResource(R.color.pocketpaint_color_picker_green1)
        onDrawingSurfaceView()
            .perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView()
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
        assertEquals(
            "Wrong fill tool member value for color tolerance",
            fillTool!!.getToleranceAbsoluteValue(DEFAULT_TOLERANCE_IN_PERCENT).toDouble(),
            fillTool.colorTolerance.toDouble(),
            TOLERANCE_DELTA
        )
        onToolBarView()
            .performClickSelectedToolButton()
        val colorToleranceInput =
            onView(withId(R.id.pocketpaint_fill_tool_dialog_color_tolerance_input))
        val colorToleranceSeekBar =
            onView(withId(R.id.pocketpaint_color_tolerance_seek_bar))
        val testToleranceText = "100"
        colorToleranceInput.check(
            matches(
                withText(
                    Integer.toString(
                        DEFAULT_TOLERANCE_IN_PERCENT
                    )
                )
            )
        )
        colorToleranceInput.perform(
            replaceText(testToleranceText),
            closeSoftKeyboard()
        )
        colorToleranceInput.check(matches(withText(testToleranceText)))
        colorToleranceSeekBar.check(matches(withProgress(testToleranceText.toInt())))
        val expectedAbsoluteTolerance = fillTool.getToleranceAbsoluteValue(100)
        assertEquals(
            "Wrong fill tool member value for color tolerance",
            expectedAbsoluteTolerance.toDouble(),
            fillTool.colorTolerance.toDouble(),
            TOLERANCE_DELTA
        )

        // Close tool options
        onToolBarView()
            .performClickSelectedToolButton()
    }

    @Test
    fun testFillToolDialogAfterToolSwitch() {
        val fillTool = toolReference!!.tool as FillTool?
        onToolBarView()
            .performClickSelectedToolButton()
        val colorToleranceInput =
            onView(withId(R.id.pocketpaint_fill_tool_dialog_color_tolerance_input))
        val colorToleranceSeekBar =
            onView(withId(R.id.pocketpaint_color_tolerance_seek_bar))
        val toleranceInPercent = 50
        colorToleranceInput.perform(replaceText(toleranceInPercent.toString()))
        val expectedAbsoluteTolerance = fillTool!!.getToleranceAbsoluteValue(toleranceInPercent)
        assertEquals(
            "Wrong fill tool member value for color tolerance",
            expectedAbsoluteTolerance.toDouble(),
            fillTool.colorTolerance.toDouble(),
            TOLERANCE_DELTA
        )

        // Close tool options
        onToolBarView()
            .performClickSelectedToolButton()
        onToolBarView()
            .performSelectTool(ToolType.BRUSH)
        onToolBarView()
            .performSelectTool(ToolType.FILL)
        onToolBarView()
            .performClickSelectedToolButton()
        colorToleranceInput.check(
            matches(
                withText(
                    Integer.toString(
                        DEFAULT_TOLERANCE_IN_PERCENT
                    )
                )
            )
        )
        colorToleranceSeekBar.check(
            matches(
                withProgress(
                    DEFAULT_TOLERANCE_IN_PERCENT
                )
            )
        )
    }

    @Ignore("Fails on Jenkins, trying out if everything works without this test or if error is due to a bug on Jenkins")
    @Test
    fun testFillToolUndoRedoWithTolerance() {
        onToolBarView()
            .performSelectTool(ToolType.BRUSH)
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
            .perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        onToolProperties()
            .setColorResource(R.color.pocketpaint_color_picker_brown2)
            .checkMatchesColorResource(R.color.pocketpaint_color_picker_brown2)
        onToolBarView()
            .performSelectTool(ToolType.FILL)
            .performOpenToolOptionsView()
        onView(withId(R.id.pocketpaint_fill_tool_dialog_color_tolerance_input))
            .perform(replaceText(100.toString()))
        onToolBarView()
            .performCloseToolOptionsView()
        onDrawingSurfaceView()
            .perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColorResource(
                R.color.pocketpaint_color_picker_brown2,
                BitmapLocationProvider.MIDDLE
            )
            .checkPixelColorResource(
                R.color.pocketpaint_color_picker_brown2,
                BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE
            )
        onTopBarView()
            .performUndo()
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        onTopBarView()
            .performRedo()
        onDrawingSurfaceView()
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
