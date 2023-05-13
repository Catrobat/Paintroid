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
package org.catrobat.paintroid.test.espresso.dialog

import android.graphics.Paint
import android.graphics.Paint.Cap
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import junit.framework.TestCase.assertEquals
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.EspressoUtils.DEFAULT_STROKE_WIDTH
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.UiInteractions.setProgress
import org.catrobat.paintroid.test.espresso.util.UiMatcher
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
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
    var launchActivityRule = ActivityTestRule(
        MainActivity::class.java
    )

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    @Before
    fun setUp() {
        onToolBarView()
            .performSelectTool(ToolType.BRUSH)
    }
    private fun getCurrentToolBitmapPaint(): Paint {
        return launchActivityRule.activity.toolPaint.paint
    }

    private fun getCurrentToolCanvasPaint(): Paint {
        return launchActivityRule.activity.toolPaint.previewPaint
    }


    private fun assertStrokePaint(strokePaint: Paint, expectedStrokeWidth: Int, expectedCap: Cap) {
        val paintStrokeWidth = strokePaint.strokeWidth.toInt()
        val paintCap = strokePaint.strokeCap
        assertEquals(
            "Stroke did not change",
            expectedStrokeWidth.toLong(),
            paintStrokeWidth.toLong()
        )
        assertEquals("Stroke cap not $expectedCap", expectedCap, paintCap)
    }

    private fun setStrokeWidth(strokeWidth: Int, expectedStrokeWidth: Int) {
        onView(withId(R.id.pocketpaint_stroke_width_seek_bar))
            .perform(setProgress(strokeWidth))
            .check(matches(UiMatcher.withProgress(expectedStrokeWidth)))
    }

    private fun setStrokeWidth(strokeWidth: Int) {
        setStrokeWidth(strokeWidth, strokeWidth)
    }

    @Test
    fun brushPickerDialogDefaultLayoutAndToolChanges() {
        onView(withId(R.id.pocketpaint_brush_tool_preview))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.pocketpaint_stroke_width_seek_bar))
            .check(matches(ViewMatchers.isDisplayed()))
            .check(matches(UiMatcher.withProgress(DEFAULT_STROKE_WIDTH)))
        onView(withId(R.id.pocketpaint_stroke_width_width_text))
            .check(matches(ViewMatchers.isDisplayed()))
            .check(
                matches(
                    ViewMatchers.withText(
                        Integer.toString(
                            DEFAULT_STROKE_WIDTH
                        )
                    )
                )
            )
        onView(withId(R.id.pocketpaint_stroke_ibtn_rect))
            .check(matches(ViewMatchers.isDisplayed()))
            .check(matches(IsNot.not(ViewMatchers.isChecked())))
        onView(withId(R.id.pocketpaint_stroke_ibtn_circle))
            .check(matches(ViewMatchers.isDisplayed()))
            .check(matches(ViewMatchers.isChecked()))
        setStrokeWidth(MIN_STROKE_WIDTH)
        setStrokeWidth(MIDDLE_STROKE_WIDTH)
        setStrokeWidth(MAX_STROKE_WIDTH)
        assertStrokePaint(this.getCurrentToolCanvasPaint(), MAX_STROKE_WIDTH, Cap.ROUND)
        onView(withId(R.id.pocketpaint_stroke_ibtn_rect))
            .perform(ViewActions.click())
            .check(matches(ViewMatchers.isChecked()))
        onView(withId(R.id.pocketpaint_stroke_ibtn_circle))
            .check(matches(IsNot.not(ViewMatchers.isChecked())))
        assertStrokePaint(this.getCurrentToolCanvasPaint(), MAX_STROKE_WIDTH, Cap.SQUARE)
        onToolBarView()
            .performCloseToolOptionsView()
        assertStrokePaint(this.getCurrentToolCanvasPaint(), MAX_STROKE_WIDTH, Cap.SQUARE)
    }

    @Test
    fun brushPickerDialogKeepStrokeOnToolChange() {
        val newStrokeWidth = 80
        setStrokeWidth(newStrokeWidth)
        onView(withId(R.id.pocketpaint_stroke_ibtn_rect))
            .perform(ViewActions.click())
        assertStrokePaint(this.getCurrentToolCanvasPaint(), newStrokeWidth, Cap.SQUARE)
        onToolBarView()
            .performCloseToolOptionsView()
            .performSelectTool(ToolType.CURSOR)
        onView(withId(R.id.pocketpaint_stroke_width_seek_bar))
            .check(matches(UiMatcher.withProgress(newStrokeWidth)))
        assertStrokePaint(this.getCurrentToolCanvasPaint(), newStrokeWidth, Cap.SQUARE)
        onToolBarView()
            .performCloseToolOptionsView()
    }

    @Test
    fun brushPickerDialogMinimumBrushWidth() {
        setStrokeWidth(0, MIN_STROKE_WIDTH)
        setStrokeWidth(MIN_STROKE_WIDTH)
        onToolBarView()
            .performCloseToolOptionsView()
    }

    @Test
    fun brushPickerAntiAliasingOffAtMinimumBrushSize() {
        onView(withId(R.id.pocketpaint_stroke_width_seek_bar))
            .perform(UiInteractions.touchCenterLeft())
        onToolBarView()
            .performCloseToolOptionsView()
        val bitmapPaint: Paint = this.getCurrentToolBitmapPaint()
        val canvasPaint: Paint = this.getCurrentToolCanvasPaint()
        Assert.assertFalse("BITMAP_PAINT antialiasing should be off", bitmapPaint.isAntiAlias)
        Assert.assertFalse("CANVAS_PAINT antialiasing should be off", canvasPaint.isAntiAlias)
    }

    @Test
    fun setAntiAliasingNotOnWhenCancelPressed() {
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        onView(ViewMatchers.withText(R.string.menu_advanced))
            .perform(ViewActions.click())
        onView(withId(R.id.pocketpaint_antialiasing))
            .perform(ViewActions.click())
        onView(ViewMatchers.withText(R.string.cancel_button_text))
            .perform(ViewActions.click())
        val bitmapPaint: Paint = this.getCurrentToolBitmapPaint()
        val canvasPaint: Paint = this.getCurrentToolCanvasPaint()
        Assert.assertTrue("BITMAP_PAINT antialiasing should be on", bitmapPaint.isAntiAlias)
        Assert.assertTrue("CANVAS_PAINT antialiasing should be on", canvasPaint.isAntiAlias)
    }

    @Test
    fun setAntiAliasingOffWhenAdvancedSettingsTurnOffAndOn() {
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        onView(ViewMatchers.withText(R.string.menu_advanced))
            .check(matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText(R.string.menu_advanced))
            .perform(ViewActions.click())
        onView(withId(R.id.pocketpaint_antialiasing))
            .perform(ViewActions.click())
        onView(ViewMatchers.withText(R.string.pocketpaint_ok))
            .perform(ViewActions.click())
        var bitmapPaint: Paint = this.getCurrentToolBitmapPaint()
        var canvasPaint: Paint = this.getCurrentToolCanvasPaint()
        Assert.assertFalse("BITMAP_PAINT antialiasing should be off", bitmapPaint.isAntiAlias)
        Assert.assertFalse("CANVAS_PAINT antialiasing should be off", canvasPaint.isAntiAlias)
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        onView(ViewMatchers.withText(R.string.menu_advanced))
            .perform(ViewActions.click())
        onView(withId(R.id.pocketpaint_antialiasing))
            .perform(ViewActions.click())
        onView(ViewMatchers.withText(R.string.pocketpaint_ok))
            .perform(ViewActions.click())
        bitmapPaint = this.getCurrentToolBitmapPaint()
        canvasPaint = this.getCurrentToolCanvasPaint()
        Assert.assertTrue("BITMAP_PAINT antialiasing should be on", bitmapPaint.isAntiAlias)
        Assert.assertTrue("CANVAS_PAINT antialiasing should be on", canvasPaint.isAntiAlias)
    }

    @Test
    fun brushPickerDialogRadioButtonsBehaviour() {
        onView(withId(R.id.pocketpaint_stroke_ibtn_rect))
            .check(matches(IsNot.not(ViewMatchers.isChecked())))
        onView(withId(R.id.pocketpaint_stroke_ibtn_circle))
            .check(matches(ViewMatchers.isChecked()))
        onView(withId(R.id.pocketpaint_stroke_ibtn_rect))
            .perform(ViewActions.click())
            .check(matches(ViewMatchers.isChecked()))
        onView(withId(R.id.pocketpaint_stroke_ibtn_circle))
            .check(matches(IsNot.not(ViewMatchers.isChecked())))
        onToolBarView()
            .performCloseToolOptionsView()
        assertStrokePaint(this.getCurrentToolCanvasPaint(), DEFAULT_STROKE_WIDTH, Cap.SQUARE)
        onToolBarView()
            .performOpenToolOptionsView()
        onView(withId(R.id.pocketpaint_stroke_ibtn_circle))
            .perform(ViewActions.click())
            .check(matches(ViewMatchers.isChecked()))
        onView(withId(R.id.pocketpaint_stroke_ibtn_rect))
            .check(matches(IsNot.not(ViewMatchers.isChecked())))
        assertStrokePaint(this.getCurrentToolCanvasPaint(), DEFAULT_STROKE_WIDTH, Cap.ROUND)
        onToolBarView()
            .performCloseToolOptionsView()
        assertStrokePaint(this.getCurrentToolCanvasPaint(), DEFAULT_STROKE_WIDTH, Cap.ROUND)
    }

    @Test
    fun brushPickerDialogEditTextBehaviour() {
        onView(withId(R.id.pocketpaint_stroke_width_width_text))
            .perform(ViewActions.replaceText(MIDDLE_STROKE_WIDTH.toString()))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.pocketpaint_stroke_width_width_text))
            .check(matches(ViewMatchers.withText(MIDDLE_STROKE_WIDTH.toString())))
        onView(withId(R.id.pocketpaint_stroke_width_seek_bar))
            .check(matches(UiMatcher.withProgress(MIDDLE_STROKE_WIDTH)))
    }

    companion object {
        private const val MIN_STROKE_WIDTH = 1
        private const val MIDDLE_STROKE_WIDTH = 50
        private const val MAX_STROKE_WIDTH = 100
    }
}