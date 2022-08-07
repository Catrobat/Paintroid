/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.paintroid.test.espresso.rtl

import org.junit.runner.RunWith
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.test.espresso.rtl.util.RtlActivityTestRule
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.tools.ToolType
import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers
import org.catrobat.paintroid.R
import androidx.test.espresso.assertion.ViewAssertions
import org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction
import org.catrobat.paintroid.test.espresso.util.UiMatcher
import androidx.test.espresso.action.ViewActions
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class HindiNumberFormatTest {
    @Rule
    var launchActivityRule: ActivityTestRule<MainActivity> = RtlActivityTestRule(MainActivity::class.java, "ar")

    @Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    @Test
    fun testHindiNumberAtTool() {
        ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.BRUSH)
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_stroke_width_width_text))
            .check(ViewAssertions.matches(ViewMatchers.withText(
                        Matchers.containsString(EXPECTED_STROKE_WIDTH_VALUE))))
        ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.LINE)
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_stroke_width_width_text))
            .check(ViewAssertions.matches(ViewMatchers.withText(
                Matchers.containsString(EXPECTED_STROKE_WIDTH_VALUE))))
        ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.CURSOR)
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_stroke_width_width_text))
            .check(ViewAssertions.matches(ViewMatchers.withText(
                        Matchers.containsString(EXPECTED_STROKE_WIDTH_VALUE))))
        ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.FILL)
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_fill_tool_dialog_color_tolerance_input))
            .check(ViewAssertions.matches(ViewMatchers.withText(
                        Matchers.containsString(EXPECTED_COLOR_TOLERANCE_VALUE))))
        ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.ERASER)
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_stroke_width_width_text))
            .check(ViewAssertions.matches(ViewMatchers.withText(
                        Matchers.containsString(EXPECTED_STROKE_WIDTH_VALUE))))
    }

    @Test
    fun testHindiNumberAtColorDialog() {
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
            .performClickColorPickerPresetSelectorButton(7)
        Espresso.onView(Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_red_value))
            .check(ViewAssertions.matches(ViewMatchers.withText(
                        Matchers.containsString(EXPECTED_RED_VALUE))))
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_green_value))
            .check(ViewAssertions.matches(ViewMatchers.withText(
                        Matchers.containsString(EXPECTED_GREEN_VALUE))))
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_blue_value))
            .check(ViewAssertions.matches(ViewMatchers.withText(
                        Matchers.containsString(EXPECTED_BLAU_VALUE))))
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_alpha_value))
            .check(ViewAssertions.matches(ViewMatchers.withText(
                        Matchers.containsString(EXPECTED_ALFA_VALUE))))
    }

    companion object {
        private const val EXPECTED_RED_VALUE = "٢٤٠"
        private const val EXPECTED_GREEN_VALUE = "٢٢٨"
        private const val EXPECTED_BLAU_VALUE = "١٦٨"
        private const val EXPECTED_ALFA_VALUE = "١٠٠"
        private const val EXPECTED_STROKE_WIDTH_VALUE = "٢٥"
        private const val EXPECTED_COLOR_TOLERANCE_VALUE = "١٢"
    }
}
