/*
 * Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
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

@file:Suppress("DEPRECATION")

package org.catrobat.paintroid.test.espresso.dialog

import android.graphics.Paint
import android.graphics.Paint.Cap
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.EspressoUtils.DEFAULT_STROKE_WIDTH
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.UiMatcher
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolType
import org.hamcrest.core.IsNot
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BrushPickerIntegrationTest {
    @get:Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    @Before
    fun setUp() { onToolBarView().performSelectTool(ToolType.BRUSH) }

    private fun getCurrentToolBitmapPaint(): Paint = launchActivityRule.activity.toolPaint.paint

    private fun getCurrentToolCanvasPaint(): Paint = launchActivityRule.activity.toolPaint.previewPaint

    private fun assertStrokePaint(strokePaint: Paint, expectedStrokeWidth: Int, expectedCap: Cap) {
        val paintStrokeWidth = strokePaint.strokeWidth.toInt()
        val paintCap = strokePaint.strokeCap

        Assert.assertEquals("Stroke did not change", expectedStrokeWidth.toLong(), paintStrokeWidth.toLong())
        Assert.assertEquals("Stroke cap not $expectedCap", expectedCap, paintCap)
    }

    private fun setStrokeWidth(strokeWidth: Int, expectedStrokeWidth: Int) {
        onView(withId(R.id.pocketpaint_stroke_width_seek_bar))
            .perform(UiInteractions.setProgress(strokeWidth))
            .check(ViewAssertions.matches(UiMatcher.withProgress(expectedStrokeWidth)))
    }

    private fun setStrokeWidth(strokeWidth: Int) = setStrokeWidth(strokeWidth, strokeWidth)

    @Test
    fun brushPickerDialogDefaultLayoutAndToolChanges() {
        onView(withId(R.id.pocketpaint_brush_tool_preview))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.pocketpaint_stroke_width_seek_bar))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(UiMatcher.withProgress(DEFAULT_STROKE_WIDTH)))
        onView(withId(R.id.pocketpaint_stroke_width_width_text))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(
                ViewAssertions.matches(
                    withText(
                        DEFAULT_STROKE_WIDTH.toString()
                    )
                )
            )
        onView(withId(R.id.pocketpaint_stroke_ibtn_rect))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(IsNot.not(ViewMatchers.isSelected())))
        onView(withId(R.id.pocketpaint_stroke_ibtn_circle))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isSelected()))
        setStrokeWidth(MIN_STROKE_WIDTH)
        setStrokeWidth(MIDDLE_STROKE_WIDTH)
        setStrokeWidth(MAX_STROKE_WIDTH)
        assertStrokePaint(getCurrentToolCanvasPaint(), MAX_STROKE_WIDTH, Cap.ROUND)
        onView(withId(R.id.pocketpaint_stroke_ibtn_rect))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isSelected()))
        onView(withId(R.id.pocketpaint_stroke_ibtn_circle))
            .check(ViewAssertions.matches(IsNot.not(ViewMatchers.isSelected())))
        assertStrokePaint(getCurrentToolCanvasPaint(), MAX_STROKE_WIDTH, Cap.SQUARE)
        onToolBarView().performCloseToolOptionsView()
        assertStrokePaint(getCurrentToolCanvasPaint(), MAX_STROKE_WIDTH, Cap.SQUARE)
    }

    @Test
    fun brushPickerDialogKeepStrokeOnToolChange() {
        val newStrokeWidth = 80
        setStrokeWidth(newStrokeWidth)
        onView(withId(R.id.pocketpaint_stroke_ibtn_rect)).perform(ViewActions.click())
        assertStrokePaint(getCurrentToolCanvasPaint(), newStrokeWidth, Cap.SQUARE)
        onToolBarView()
            .performCloseToolOptionsView()
            .performSelectTool(ToolType.CURSOR)
        onView(withId(R.id.pocketpaint_stroke_width_seek_bar))
            .check(ViewAssertions.matches(UiMatcher.withProgress(newStrokeWidth)))
        assertStrokePaint(getCurrentToolCanvasPaint(), newStrokeWidth, Cap.SQUARE)
        onToolBarView().performCloseToolOptionsView()
    }

    @Test
    fun brushPickerDialogMinimumBrushWidth() {
        setStrokeWidth(0, MIN_STROKE_WIDTH)
        setStrokeWidth(MIN_STROKE_WIDTH)
        onToolBarView().performCloseToolOptionsView()
    }

    @Test
    fun brushPickerAntiAliasingOffAtMinimumBrushSize() {
        onView(withId(R.id.pocketpaint_stroke_width_seek_bar)).perform(UiInteractions.touchCenterLeft())
        onToolBarView().performCloseToolOptionsView()

        val bitmapPaint = getCurrentToolBitmapPaint()
        val canvasPaint = getCurrentToolCanvasPaint()

        Assert.assertFalse("BITMAP_PAINT antialiasing should be off", bitmapPaint.isAntiAlias)
        Assert.assertFalse("CANVAS_PAINT antialiasing should be off", canvasPaint.isAntiAlias)
    }

    @Test
    fun setAntiAliasingNotOnWhenCancelPressed() {
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_advanced)).perform(ViewActions.click())
        onView(withId(R.id.pocketpaint_antialiasing)).perform(ViewActions.click())
        onView(withText(R.string.cancel_button_text)).perform(ViewActions.click())

        val bitmapPaint = getCurrentToolBitmapPaint()
        val canvasPaint = getCurrentToolCanvasPaint()

        Assert.assertTrue("BITMAP_PAINT antialiasing should be on", bitmapPaint.isAntiAlias)
        Assert.assertTrue("CANVAS_PAINT antialiasing should be on", canvasPaint.isAntiAlias)
    }

    @Test
    fun setAntiAliasingOffWhenAdvancedSettingsTurnOffAndOn() {
        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_advanced)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withText(R.string.menu_advanced)).perform(ViewActions.click())
        onView(withId(R.id.pocketpaint_antialiasing)).perform(ViewActions.click())
        onView(withText(R.string.pocketpaint_ok)).perform(ViewActions.click())

        var bitmapPaint = getCurrentToolBitmapPaint()
        var canvasPaint = getCurrentToolCanvasPaint()

        Assert.assertFalse("BITMAP_PAINT antialiasing should be off", bitmapPaint.isAntiAlias)
        Assert.assertFalse("CANVAS_PAINT antialiasing should be off", canvasPaint.isAntiAlias)

        onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_advanced)).perform(ViewActions.click())
        onView(withId(R.id.pocketpaint_antialiasing)).perform(ViewActions.click())
        onView(withText(R.string.pocketpaint_ok)).perform(ViewActions.click())

        bitmapPaint = getCurrentToolBitmapPaint()
        canvasPaint = getCurrentToolCanvasPaint()

        Assert.assertTrue("BITMAP_PAINT antialiasing should be on", bitmapPaint.isAntiAlias)
        Assert.assertTrue("CANVAS_PAINT antialiasing should be on", canvasPaint.isAntiAlias)
    }

    @Test
    fun brushPickerDialogRadioButtonsBehaviour() {
        onView(withId(R.id.pocketpaint_stroke_ibtn_rect))
            .check(ViewAssertions.matches(IsNot.not(ViewMatchers.isSelected())))
        onView(withId(R.id.pocketpaint_stroke_ibtn_circle))
            .check(ViewAssertions.matches(ViewMatchers.isSelected()))
        onView(withId(R.id.pocketpaint_stroke_ibtn_rect))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isSelected()))
        onView(withId(R.id.pocketpaint_stroke_ibtn_circle))
            .check(ViewAssertions.matches(IsNot.not(ViewMatchers.isSelected())))
        onToolBarView().performCloseToolOptionsView()
        assertStrokePaint(getCurrentToolCanvasPaint(), DEFAULT_STROKE_WIDTH, Cap.SQUARE)
        onToolBarView().performOpenToolOptionsView()
        onView(withId(R.id.pocketpaint_stroke_ibtn_circle))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isSelected()))
        onView(withId(R.id.pocketpaint_stroke_ibtn_rect))
            .check(ViewAssertions.matches(IsNot.not(ViewMatchers.isSelected())))
        assertStrokePaint(getCurrentToolCanvasPaint(), DEFAULT_STROKE_WIDTH, Cap.ROUND)
        onToolBarView().performCloseToolOptionsView()
        assertStrokePaint(getCurrentToolCanvasPaint(), DEFAULT_STROKE_WIDTH, Cap.ROUND)
    }

    @Test
    fun brushPickerDialogEditTextBehaviour() {
        onView(withId(R.id.pocketpaint_stroke_width_width_text))
            .perform(ViewActions.replaceText(MIDDLE_STROKE_WIDTH.toString()))
        closeSoftKeyboard()
        onView(withId(R.id.pocketpaint_stroke_width_width_text))
            .check(ViewAssertions.matches(withText(MIDDLE_STROKE_WIDTH.toString())))
        onView(withId(R.id.pocketpaint_stroke_width_seek_bar))
            .check(ViewAssertions.matches(UiMatcher.withProgress(MIDDLE_STROKE_WIDTH)))
    }

    companion object {
        private const val MIN_STROKE_WIDTH = 1
        private const val MIDDLE_STROKE_WIDTH = 50
        private const val MAX_STROKE_WIDTH = 100
    }
}
