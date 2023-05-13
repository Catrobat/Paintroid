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
package org.catrobat.paintroid.test.espresso.dialog

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.colorpicker.HSVColorPickerView
import org.catrobat.paintroid.colorpicker.PresetSelectorView
import org.catrobat.paintroid.colorpicker.RgbSelectorView
import org.catrobat.paintroid.common.CATROBAT_IMAGE_ENDING
import org.catrobat.paintroid.common.PAINTROID_PICTURE_NAME
import org.catrobat.paintroid.common.PAINTROID_PICTURE_PATH
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.UiInteractions.touchCenterLeft
import org.catrobat.paintroid.test.espresso.util.UiInteractions.touchCenterRight
import org.catrobat.paintroid.test.espresso.util.UiMatcher
import org.catrobat.paintroid.test.espresso.util.UiMatcher.*
import org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction.onColorPickerView
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolReference
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers
import org.hamcrest.core.AllOf
import org.junit.Assert
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.*

@RunWith(AndroidJUnit4::class)
class ColorDialogIntegrationTest {
    @get:Rule
    var launchActivityRule = ActivityTestRule(
        MainActivity::class.java
    )

    @get:Rule
    var launchActivityRuleWithIntent = IntentsTestRule(
        MainActivity::class.java, false, false
    )

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    private var toolReference: ToolReference? = null
    private val deletionFileList: List<File?> = ArrayList()
    @Before
    fun setUp() {
        toolReference = launchActivityRule.activity.toolReference
    }

    private fun getColorById(colorId: Int): Int {
        return launchActivityRule.activity.resources.getColor(colorId)
    }

    @Test
    fun testStandardTabSelected() {
        onColorPickerView()
            .performOpenColorPicker()
        onView(
            withClassName(
                containsString(
                    TAB_VIEW_PRESET_SELECTOR_CLASS
                )
            )
        ).check(matches(isDisplayed()))
    }

    @Test
    fun testCorrectColorAfterApplyWithoutNewColorSelected() {
        val initialPaint = toolReference!!.tool!!.drawPaint
        onColorPickerView()
            .performOpenColorPicker()
            .onPositiveButton()
            .perform(click())
        assertEquals(
            initialPaint.color.toLong(),
            toolReference!!.tool!!.drawPaint.color.toLong()
        )
    }

    @Test
    fun testTabsAreSelectable() {
        onColorPickerView()
            .performOpenColorPicker()
        onView(
            withClassName(
                containsString(
                    TAB_VIEW_PRESET_SELECTOR_CLASS
                )
            )
        ).check(matches(isDisplayed()))
        onView(
            allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_hsv)
            )
        ).perform(click())
        onView(
            withClassName(
                containsString(
                    TAB_VIEW_HSV_SELECTOR_CLASS
                )
            )
        ).check(matches(isDisplayed()))
        onView(
            allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(click())
        onView(
            withClassName(
                containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        ).check(matches(isDisplayed()))
        onView(
            allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_preset)
            )
        ).perform(scrollTo(), click())
        onView(
            withClassName(
                containsString(
                    TAB_VIEW_PRESET_SELECTOR_CLASS
                )
            )
        ).check(matches(isDisplayed()))
    }

    @Test
    fun dontShowAlphaRelatedStuffFromCatroidFormulaEditor() {
        launchActivityRule.activity.model.isOpenedFromCatroid = true
        launchActivityRule.activity.model.isOpenedFromFormulaEditorInCatroid = true
        onColorPickerView()
            .performOpenColorPicker()
        onView(withId(R.id.color_picker_base_layout))
            .perform(swipeUp())
        onView(withId(R.id.color_alpha_slider))
            .check(matches(not(isDisplayed())))
        onView(
            Matchers.allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_hsv)
            )
        ).perform(click())
        onView(withId(R.id.color_picker_base_layout))
            .perform(swipeUp())
        onView(withId(R.id.color_alpha_slider))
            .check(matches(Matchers.not(isDisplayed())))
        onColorPickerView()
            .onPositiveButton()
            .perform(click())
        val currentSelectColor = toolReference!!.tool!!.drawPaint.color
        onColorPickerView()
            .performOpenColorPicker()
        onView(
            Matchers.allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(click())
        onView(withId(R.id.color_picker_base_layout))
            .perform(swipeUp())
        onView(withId(R.id.color_picker_alpha_row))
            .check(matches(Matchers.not(isDisplayed())))
        onView(withId(R.id.color_picker_color_rgb_hex))
            .check(
                matches(
                    withText(
                        String.format(
                            "#%02X%02X%02X",
                            Color.red(currentSelectColor),
                            Color.green(currentSelectColor),
                            Color.blue(currentSelectColor)
                        )
                    )
                )
            )
    }

    @Test
    fun showAlphaSliderFromCatroid() {
        launchActivityRule.activity.model.isOpenedFromCatroid = true
        onColorPickerView()
            .performOpenColorPicker()
        onView(withId(R.id.color_picker_base_layout))
            .perform(swipeUp())
        onView(withId(R.id.color_alpha_slider))
            .check(matches(isDisplayed()))
        onView(
            allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_hsv)
            )
        ).perform(click())
        onView(withId(R.id.color_picker_base_layout))
            .perform(swipeUp())
        onView(withId(R.id.color_alpha_slider))
            .check(matches(isDisplayed()))
    }

    @Test
    fun showAlphaSliderIfNotCatroidFlagSet() {
        onColorPickerView()
            .performOpenColorPicker()
        onView(withId(R.id.color_picker_base_layout))
            .perform(swipeUp())
        onView(withId(R.id.color_alpha_slider))
            .check(matches(isDisplayed()))
        onView(
            allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_hsv)
            )
        ).perform(click())
        onView(withId(R.id.color_picker_base_layout))
            .perform(swipeUp())
        onView(withId(R.id.color_alpha_slider))
            .check(matches(isDisplayed()))
    }

    @Test
    fun dontShowAlphaSliderInRgb() {
        onColorPickerView()
            .performOpenColorPicker()
        onView(
            allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(click())
        onView(withId(R.id.color_picker_base_layout))
            .perform(swipeUp())
        onView(withId(R.id.color_alpha_slider))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun testColorSelectionChangesNewColorViewColor() {
        onColorPickerView()
            .performOpenColorPicker()
        val resources = launchActivityRule.activity.resources
        val presetColors =
            resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors)
        for (counterColors in 0 until presetColors.length()) {
            onColorPickerView()
                .performClickColorPickerPresetSelectorButton(counterColors)
            val arrayColor = presetColors.getColor(counterColors, Color.BLACK)
            onView(
                allOf(
                    withId(R.id.color_picker_new_color_view), instanceOf(
                        View::class.java
                    )
                )
            )
                .check(matches(withBackgroundColor(arrayColor)))
        }
        presetColors.recycle()
    }

    @Test
    fun testColorNewColorViewChangesStandard() {
        val resources = launchActivityRule.activity.resources
        val presetColors =
            resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors)
        for (counterColors in 0 until presetColors.length()) {
            onColorPickerView()
                .performOpenColorPicker()
            onColorPickerView()
                .performClickColorPickerPresetSelectorButton(counterColors)
            onColorPickerView()
                .onPositiveButton()
                .perform(click())
            val arrayColor = presetColors.getColor(counterColors, Color.BLACK)
            val selectedColor = toolReference!!.tool!!.drawPaint.color
            Assert.assertEquals(
                "Color in array and selected color not the same",
                arrayColor.toLong(),
                selectedColor.toLong()
            )
        }
        presetColors.recycle()
    }

    @Test
    fun testCurrentColorViewHasInitialColor() {
        val selectedColor = toolReference!!.tool!!.drawPaint.color
        onColorPickerView()
            .performOpenColorPicker()
        onColorPickerView()
            .checkCurrentViewColor(selectedColor)
    }

    @Test
    fun testCurrentColorViewDoesNotChangeColor() {
        onColorPickerView()
            .performOpenColorPicker()
        val initialColor = toolReference!!.tool!!.drawPaint.color
        val resources = launchActivityRule.activity.resources
        val presetColors =
            resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors)
        for (counterColors in 0 until presetColors.length()) {
            onColorPickerView()
                .performClickColorPickerPresetSelectorButton(counterColors)
            onView(
                allOf(
                    withId(R.id.color_picker_current_color_view), instanceOf(
                        View::class.java
                    )
                )
            )
                .check(matches(UiMatcher.withBackgroundColor(initialColor)))
        }
        presetColors.recycle()
    }

    @Test
    fun testColorPickerDialogOnBackPressedSelectedColorShouldChange() {
        val initialColor = toolReference!!.tool!!.drawPaint.color
        onColorPickerView()
            .performOpenColorPicker()
        onView(
            allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_preset)
            )
        ).perform(click())
        onView(
            withClassName(
                containsString(
                    TAB_VIEW_PRESET_SELECTOR_CLASS
                )
            )
        ).check(matches(isDisplayed()))
        val presetColors =
            launchActivityRule.activity.resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors)
        val colorToSelectIndex = presetColors.length() / 2
        val colorToSelect = presetColors.getColor(colorToSelectIndex, Color.WHITE)
        presetColors.recycle()
        assertNotEquals(
            "Selected color should not be the same as the initial color",
            colorToSelect.toLong(),
            initialColor.toLong()
        )
        onColorPickerView()
            .performClickColorPickerPresetSelectorButton(colorToSelectIndex)

        // Close color picker dialog
        onView(isRoot()).perform(ViewActions.pressBack())
        val currentSelectedColor = toolReference!!.tool!!.drawPaint.color
        assertEquals(
            "Selected color has not changed",
            colorToSelect.toLong(),
            currentSelectedColor.toLong()
        )
    }

    @Test
    fun testIfRGBSeekBarsDoChangeColor() {
        val resources = launchActivityRule.activity.resources
        val presetColors =
            resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors)
        onColorPickerView()
            .performOpenColorPicker()
        onView(
            allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_preset)
            )
        ).perform(click())
        onView(
            withClassName(
                containsString(
                    TAB_VIEW_PRESET_SELECTOR_CLASS
                )
            )
        ).check(matches(isDisplayed()))
        onColorPickerView()
            .performClickColorPickerPresetSelectorButton(0)
        onView(
            allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(click())
        onView(
            withClassName(
                containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        ).check(matches(isDisplayed()))
        onView(withId(R.id.color_picker_color_rgb_textview_red))
            .perform(scrollTo())
        onView(withId(R.id.color_picker_color_rgb_textview_red)).check(
            matches(
                allOf(
                    isDisplayed(),
                    withText(R.string.color_red),
                    withTextColor(getColorById(R.color.pocketpaint_color_picker_rgb_red))
                )
            )
        )
        onView(withId(R.id.color_picker_color_rgb_textview_green)).check(
            matches(
                allOf(
                    isDisplayed(),
                    withText(R.string.color_green),
                    withTextColor(getColorById(R.color.pocketpaint_color_picker_rgb_green))
                )
            )
        )
        onView(withId(R.id.color_picker_color_rgb_textview_blue)).check(
            matches(
                allOf(
                    isDisplayed(),
                    withText(R.string.color_blue),
                    withTextColor(getColorById(R.color.pocketpaint_color_picker_rgb_blue))
                )
            )
        )
        onView(withId(R.id.color_picker_color_rgb_textview_alpha))
            .perform(scrollTo())
        onView(withId(R.id.color_picker_color_rgb_textview_alpha)).check(
            matches(
                allOf(
                    isDisplayed(),
                    withText(R.string.color_alpha),
                    withTextColor(getColorById(R.color.pocketpaint_color_picker_rgb_alpha))
                )
            )
        )
        onView(withId(R.id.color_picker_color_rgb_textview_red))
            .perform(scrollTo())
        onView(withId(R.id.color_picker_color_rgb_seekbar_red))
            .check(matches(isDisplayed()))
        onView(withId(R.id.color_picker_color_rgb_seekbar_green))
            .check(matches(isDisplayed()))
        onView(withId(R.id.color_picker_color_rgb_seekbar_blue))
            .check(matches(isDisplayed()))
        onView(withId(R.id.color_picker_color_rgb_seekbar_alpha))
            .perform(scrollTo())
        onView(withId(R.id.color_picker_color_rgb_seekbar_alpha))
            .check(matches(isDisplayed()))
        onView(withId(R.id.color_picker_color_rgb_hex))
            .perform(scrollTo())
        onView(withId(R.id.color_picker_color_rgb_hex))
            .check(matches(isDisplayed()))
        onView(withId(R.id.color_picker_rgb_red_value))
            .perform(scrollTo())
        onView(withId(R.id.color_picker_rgb_red_value))
            .check(matches(isDisplayed()))
        onView(withId(R.id.color_picker_rgb_green_value))
            .check(matches(isDisplayed()))
        onView(withId(R.id.color_picker_rgb_blue_value))
            .check(matches(isDisplayed()))
        onView(withId(R.id.color_picker_rgb_alpha_value))
            .perform(scrollTo())
        onView(withId(R.id.color_picker_rgb_alpha_value))
            .check(matches(isDisplayed()))
        onView(
            allOf(
                withText(TEXT_PERCENT_SIGN),
                hasSibling(withId(R.id.color_picker_rgb_alpha_value))
            )
        ).check(matches(isDisplayed()))
        val currentSelectedColor = presetColors.getColor(0, Color.BLACK)
        onView(withId(R.id.color_picker_rgb_red_value))
            .perform(scrollTo())
        onView(withId(R.id.color_picker_rgb_red_value)).check(
            matches(
                withText(
                    Integer.toString(Color.red(currentSelectedColor))
                )
            )
        )
        onView(withId(R.id.color_picker_rgb_green_value)).check(
            matches(
                withText(
                    Integer.toString(Color.green(currentSelectedColor))
                )
            )
        )
        onView(withId(R.id.color_picker_rgb_blue_value)).check(
            matches(
                withText(
                    Integer.toString(Color.blue(currentSelectedColor))
                )
            )
        )
        onView(withId(R.id.color_picker_rgb_alpha_value))
            .perform(scrollTo())
        onView(withId(R.id.color_picker_rgb_alpha_value)).check(
            matches(
                withText(
                    Integer.toString((Color.alpha(currentSelectedColor) / 2.55f).toInt())
                )
            )
        )
        onView(withId(R.id.color_picker_color_rgb_seekbar_red))
            .perform(scrollTo(), touchCenterLeft())
        onView(withId(R.id.color_picker_rgb_red_value)).check(
            matches(
                withText(
                    TEXT_RGB_MIN
                )
            )
        )
        onView(withId(R.id.color_picker_color_rgb_seekbar_red))
            .perform(scrollTo(), UiInteractions.touchCenterRight())
        onView(withId(R.id.color_picker_rgb_red_value)).check(
            matches(
                withText(
                    TEXT_RGB_MAX
                )
            )
        )
        onView(withId(R.id.color_picker_color_rgb_seekbar_green))
            .perform(scrollTo(), touchCenterLeft())
        onView(withId(R.id.color_picker_rgb_green_value)).check(
            matches(
                withText(
                    TEXT_RGB_MIN
                )
            )
        )
        onView(withId(R.id.color_picker_color_rgb_seekbar_green))
            .perform(scrollTo(), UiInteractions.touchCenterRight())
        onView(withId(R.id.color_picker_rgb_green_value)).check(
            matches(
                withText(
                    TEXT_RGB_MAX
                )
            )
        )
        onView(withId(R.id.color_picker_color_rgb_seekbar_blue))
            .perform(scrollTo(), touchCenterLeft())
        onView(withId(R.id.color_picker_rgb_blue_value)).check(
            matches(
                withText(
                    TEXT_RGB_MIN
                )
            )
        )
        onView(withId(R.id.color_picker_color_rgb_seekbar_blue))
            .perform(scrollTo(), UiInteractions.touchCenterRight())
        onView(withId(R.id.color_picker_rgb_blue_value)).check(
            matches(
                withText(
                    TEXT_RGB_MAX
                )
            )
        )
        onView(withId(R.id.color_picker_color_rgb_seekbar_alpha))
            .perform(scrollTo(), touchCenterLeft())
        onView(withId(R.id.color_picker_rgb_alpha_value)).check(
            matches(
                withText(
                    TEXT_ALPHA_MIN
                )
            )
        )
        onView(withId(R.id.color_picker_color_rgb_seekbar_alpha))
            .perform(scrollTo(), UiInteractions.touchCenterRight())
        onView(withId(R.id.color_picker_rgb_alpha_value)).check(
            matches(
                withText(
                    TEXT_ALPHA_MAX
                )
            )
        )

        // Select color red #FFFF0000 by using hex input
        onView(withId(R.id.color_picker_color_rgb_hex))
            .perform(replaceText("#FFFF0000"))
        onColorPickerView()
            .checkNewColorViewColor(Color.RED)
        onView(
            allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(scrollTo(), click())

        // Select color blue #FF0000FF by using seekbars
        onView(withId(R.id.color_picker_color_rgb_seekbar_red))
            .perform(scrollTo(), touchCenterLeft())
        onView(withId(R.id.color_picker_color_rgb_seekbar_green))
            .perform(scrollTo(), touchCenterLeft())
        onView(withId(R.id.color_picker_color_rgb_seekbar_blue))
            .perform(scrollTo(), touchCenterRight())
        onView(withId(R.id.color_picker_color_rgb_seekbar_alpha))
            .perform(scrollTo(), touchCenterRight())
        onColorPickerView()
            .onPositiveButton()
            .perform(scrollTo(), click())
        assertNotEquals(
            "Selected color changed to blue from black",
            toolReference!!.tool!!.drawPaint.color.toLong(),
            Color.BLACK.toLong()
        )
        assertEquals(
            "Selected color is not blue",
            toolReference!!.tool!!.drawPaint.color.toLong(),
            Color.BLUE.toLong()
        )
    }

    @Test
    fun testHEXEditTextMaxInputLength() {
        onColorPickerView()
            .performOpenColorPicker()
        onView(
            allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(click())
        onView(
            withClassName(
                containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        ).check(matches(isDisplayed()))
        onView(withId(R.id.color_picker_color_rgb_hex))
            .perform(replaceText("#0123456789ABCDEF01234"))
        onView(withId(R.id.color_picker_color_rgb_hex)).check(
            matches(
                withText(String.format("#0123456789ABCDEF0"))
            )
        )
    }

    @Test
    fun testHEXUpdatingOnColorChange() {
        onColorPickerView()
            .performOpenColorPicker()
        onView(
            allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_preset)
            )
        ).perform(click())
        onView(
            withClassName(
                Matchers.containsString(
                    TAB_VIEW_PRESET_SELECTOR_CLASS
                )
            )
        ).check(matches(isDisplayed()))
        onColorPickerView()
            .performClickColorPickerPresetSelectorButton(10)
        onColorPickerView()
            .onPositiveButton()
            .perform(click())
        var currentSelectColor = toolReference!!.tool!!.drawPaint.color
        onColorPickerView()
            .performOpenColorPicker()
        onView(
            Matchers.allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(click())
        onView(
            withClassName(
                Matchers.containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        ).check(matches(isDisplayed()))
        onView(withId(R.id.color_picker_color_rgb_hex)).check(
            matches(
                withText(String.format("#FF%06X", 0xFFFFFF and currentSelectColor))
            )
        )
        onView(
            Matchers.allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_hsv)
            )
        ).perform(scrollTo(), click())
        onView(
            withClassName(
                Matchers.containsString(
                    TAB_VIEW_HSV_SELECTOR_CLASS
                )
            )
        ).check(matches(isDisplayed()))
        ColorPickerViewInteraction.onColorPickerView()
            .perform(scrollTo(), UiInteractions.touchCenterMiddle())
        onColorPickerView()
            .onPositiveButton()
            .perform(click())
        currentSelectColor = toolReference!!.tool!!.drawPaint.color
        onColorPickerView()
            .performOpenColorPicker()
        onView(
            Matchers.allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(click())
        onView(
            withClassName(
                Matchers.containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        ).check(matches(isDisplayed()))
        onView(withId(R.id.color_picker_color_rgb_hex)).check(
            matches(
                withText(String.format("#FF%06X", 0xFFFFFF and currentSelectColor))
            )
        )
        onView(withId(R.id.color_picker_color_rgb_seekbar_red))
            .perform(scrollTo(), touchCenterLeft())
        onView(withId(R.id.color_picker_color_rgb_seekbar_blue))
            .perform(scrollTo(), touchCenterLeft())
        onView(withId(R.id.color_picker_color_rgb_seekbar_green))
            .perform(scrollTo(), touchCenterLeft())
        onView(withId(R.id.color_picker_color_rgb_seekbar_alpha))
            .perform(scrollTo(), UiInteractions.touchCenterRight())
        onColorPickerView()
            .onPositiveButton()
            .perform(click())
        currentSelectColor = toolReference!!.tool!!.drawPaint.color
        onColorPickerView()
            .performOpenColorPicker()
        onView(
            Matchers.allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(click())
        onView(
            withClassName(
                Matchers.containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        ).check(matches(isDisplayed()))
        onView(withId(R.id.color_picker_color_rgb_hex)).check(
            matches(
                withText(String.format("#FF%06X", 0xFFFFFF and currentSelectColor))
            )
        )
    }

    @Test
    fun testHEXEditTextMarkingWrongInput() {
        onColorPickerView()
            .performOpenColorPicker()
        onView(
            Matchers.allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(click())
        onView(
            withClassName(
                Matchers.containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        ).check(matches(isDisplayed()))

        //set to invalid length of 6 (alpha missing)
        onView(withId(R.id.color_picker_color_rgb_hex))
            .perform(replaceText("#FF0000"))
        onView(withId(R.id.color_picker_color_rgb_hex))
            .check(matches(ViewMatchers.hasTextColor(R.color.pocketpaint_color_picker_hex_wrong_value_red)))

        //set to invalid value
        onView(withId(R.id.color_picker_color_rgb_hex))
            .perform(replaceText("#FFXXYYZZ"))
        onView(withId(R.id.color_picker_color_rgb_hex))
            .check(matches(ViewMatchers.hasTextColor(R.color.pocketpaint_color_picker_hex_wrong_value_red)))

        //set to invalid value (# missing)
        onView(withId(R.id.color_picker_color_rgb_hex))
            .perform(replaceText("FF000000"))
        onView(withId(R.id.color_picker_color_rgb_hex))
            .check(matches(ViewMatchers.hasTextColor(R.color.pocketpaint_color_picker_hex_wrong_value_red)))
    }

    @Test
    fun testHEXEditTextMarkingCorrectInputAfterWrongInput() {
        onColorPickerView()
            .performOpenColorPicker()
        onView(
            Matchers.allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(click())
        onView(
            withClassName(
                Matchers.containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        ).check(matches(isDisplayed()))

        //set to invalid length of 6 (alpha missing)
        onView(withId(R.id.color_picker_color_rgb_hex))
            .perform(replaceText("#FF0000"))

        //set correct HEX value
        onView(withId(R.id.color_picker_color_rgb_hex))
            .perform(replaceText("#FF000000"))
        onView(withId(R.id.color_picker_color_rgb_hex))
            .check(matches(ViewMatchers.hasTextColor(R.color.pocketpaint_color_picker_hex_correct_black)))
    }

    @Test
    fun testHEXEditTextInitialColorIsSetCorrectly() {
        onColorPickerView()
            .performOpenColorPicker()
        onView(
            Matchers.allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(click())
        onView(
            withClassName(
                Matchers.containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        ).check(matches(isDisplayed()))

        //Inital text color should be black
        onView(withId(R.id.color_picker_color_rgb_hex))
            .check(matches(ViewMatchers.hasTextColor(R.color.pocketpaint_color_picker_hex_correct_black)))
    }

    @Test
    fun testOpenColorPickerOnClickOnColorButton() {
        onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(withId(R.id.color_picker_base_layout))
            .check(matches(isDisplayed()))
        ColorPickerViewInteraction.onColorPickerView()
            .check(matches(isDisplayed()))
    }

    @Test
    fun testStandardColorDoesNotChangeOnCancelButtonPress() {
        val initialColor = toolReference!!.tool!!.drawPaint.color
        onColorPickerView()
            .performOpenColorPicker()
        onColorPickerView()
            .performClickColorPickerPresetSelectorButton(0)
        ColorPickerViewInteraction.onColorPickerView()
            .onNegativeButton()
            .perform(click())
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(initialColor)
    }

    @Test
    fun testStandardColorDoesChangeOnCancel() {
        val initialColor = toolReference!!.tool!!.drawPaint.color
        onColorPickerView()
            .performOpenColorPicker()
        onColorPickerView()
            .performClickColorPickerPresetSelectorButton(0)
        ColorPickerViewInteraction.onColorPickerView()
            .perform(ViewActions.pressBack())
        ToolPropertiesInteraction.onToolProperties()
            .checkDoesNotMatchColor(initialColor)
    }

    @Test
    fun testColorOnlyUpdatesOncePerColorPickerIntent() {
        val initialColor = toolReference!!.tool!!.drawPaint.color
        onColorPickerView()
            .performOpenColorPicker()
        onColorPickerView()
            .performClickColorPickerPresetSelectorButton(0)
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(initialColor)
        onColorPickerView()
            .performClickColorPickerPresetSelectorButton(1)
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(initialColor)
        onColorPickerView()
            .performClickColorPickerPresetSelectorButton(2)
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(initialColor)
        ColorPickerViewInteraction.onColorPickerView()
            .perform(ViewActions.pressBack())
        ToolPropertiesInteraction.onToolProperties()
            .checkDoesNotMatchColor(initialColor)
    }

    @Test
    fun testColorPickerRemainsOpenOnOrientationChange() {
        onColorPickerView()
            .performOpenColorPicker()
        launchActivityRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        ColorPickerViewInteraction.onColorPickerView()
            .check(matches(isDisplayed()))
    }

    @Test
    fun testColorPickerTabRestoredOnOrientationChange() {
        onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(
            Matchers.allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        )
            .perform(click())
        launchActivityRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        Espresso.onView(
            withClassName(
                Matchers.containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        )
            .check(matches(isDisplayed()))
    }

    @Test
    fun testColorPickerInitializesRgbTabTransparentColor() {
        val presetColors =
            launchActivityRule.activity.resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors)
        onColorPickerView()
            .performOpenColorPicker()
        onColorPickerView()
            .performClickColorPickerPresetSelectorButton(presetColors.length() - 1)
        onColorPickerView()
            .onPositiveButton()
            .perform(click())
        onColorPickerView()
            .performOpenColorPicker()
        onView(
            Matchers.allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(click())
        onView(withId(R.id.color_picker_rgb_red_value)).check(
            matches(
                withText(
                    Integer.toString(Color.red(Color.TRANSPARENT))
                )
            )
        )
        onView(withId(R.id.color_picker_rgb_green_value)).check(
            matches(
                withText(
                    Integer.toString(Color.green(Color.TRANSPARENT))
                )
            )
        )
        onView(withId(R.id.color_picker_rgb_blue_value)).check(
            matches(
                withText(
                    Integer.toString(Color.blue(Color.TRANSPARENT))
                )
            )
        )
        onView(withId(R.id.color_picker_rgb_alpha_value)).check(
            matches(
                withText(
                    Integer.toString((Color.alpha(Color.TRANSPARENT) / 2.55f).toInt())
                )
            )
        )
        presetColors.recycle()
    }

    @Test
    fun testInsertInvalidHexInputAndSlideSeekbar() {
        onColorPickerView()
            .performOpenColorPicker()
        onView(
            Matchers.allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(click())
        onView(withId(R.id.color_picker_color_rgb_hex))
            .perform(replaceText("#FFFF0000xxxx"))
        onView(withId(R.id.color_picker_color_rgb_seekbar_blue))
            .perform(scrollTo(), UiInteractions.touchCenterRight())
        onView(withId(R.id.color_picker_color_rgb_hex))
            .perform(scrollTo())
        onView(withId(R.id.color_picker_color_rgb_hex)).check(
            matches(
                withText(String.format("#FF%06X", 0xFFFFFF and -0xffff01))
            )
        )
    }

    @Test
    fun testPipetteButtonIsDisplayed() {
        onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(withId(R.id.color_picker_pipette_btn))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.color_picker_pipette)))
    }

    @Test
    fun testColorViewsAreDisplayed() {
        onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(withId(R.id.color_picker_new_color_view))
            .check(matches(isDisplayed()))
            .check(matches(UiMatcher.withBackgroundColor(Color.BLACK)))
        Espresso.onView(withId(R.id.color_picker_current_color_view))
            .check(matches(isDisplayed()))
            .check(matches(UiMatcher.withBackgroundColor(Color.BLACK)))
    }

    @Test
    fun alphaValueIsSetInSliderWhenChangedInSeekBar() {
        val idlingResource = launchActivityRule.activity.idlingResource
        IdlingRegistry.getInstance().register(idlingResource)
        onColorPickerView()
            .performOpenColorPicker()
        onView(
            Matchers.allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(click())
        onView(withId(R.id.color_picker_color_rgb_seekbar_alpha))
            .perform(UiInteractions.touchCenterMiddle())
        onView(
            Matchers.allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_preset)
            )
        ).perform(scrollTo(), click())
        onColorPickerView()
            .onPositiveButton()
            .perform(click())
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.parseColor("#7F000000"))
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun alphaValueIsSetInSeekBarWhenChangedInSlider() {
        onColorPickerView()
            .performOpenColorPicker()
        onView(
            Matchers.allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_preset)
            )
        ).perform(click())
        Espresso.onView(withId(R.id.color_alpha_slider))
            .perform(scrollTo(), UiInteractions.touchCenterMiddle())
        onView(
            Matchers.allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(click())
        onView(withId(R.id.color_picker_rgb_alpha_value)).check(
            matches(
                withText("50")
            )
        )
    }

    @Test
    fun testPreserveZoomAfterPipetteUsage() {
        val perspective = launchActivityRule.activity.perspective
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        val scale = 4f
        perspective.scale = scale
        onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(withId(R.id.color_picker_pipette_btn))
            .perform(click())
        Espresso.onView(withId(R.id.doneAction)).perform(click())
        ColorPickerViewInteraction.onColorPickerView()
            .performCloseColorPickerWithDialogButton()
        Assert.assertEquals(scale, perspective.scale, Float.MIN_VALUE)
    }

    @Test
    fun testColorHistoryShowsPresetSelectorColors() {
        val resources = launchActivityRule.activity.resources
        val presetColors =
            resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors)
        run {
            var counterColors = 0
            while (counterColors < presetColors.length() && counterColors < ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY) {
                ColorPickerViewInteraction.onColorPickerView()
                    .performOpenColorPicker()
                ColorPickerViewInteraction.onColorPickerView()
                    .performClickColorPickerPresetSelectorButton(counterColors)
                ColorPickerViewInteraction.onColorPickerView()
                    .onPositiveButton()
                    .perform(click())
                counterColors++
            }
        }
        var counterColors = 0
        while (counterColors < presetColors.length() && counterColors < ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY) {
            onColorPickerView()
                .performOpenColorPicker()
            ColorPickerViewInteraction.onColorPickerView()
                .performClickOnHistoryColor(ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY - 1)
            onColorPickerView()
                .onPositiveButton()
                .perform(click())
            val arrayColor = presetColors.getColor(counterColors, Color.BLACK)
            val selectedColor = Objects.requireNonNull(
                toolReference!!.tool
            )!!.drawPaint.color
            Assert.assertEquals(
                "Color in history doesn't match selection",
                arrayColor.toLong(),
                selectedColor.toLong()
            )
            counterColors++
        }
        presetColors.recycle()
    }

    @Test
    fun testColorHistorySelectMoreThanMaxHistoryColors() {
        val resources = launchActivityRule.activity.resources
        val presetColors =
            resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors)
        var counterColors = 0
        while (counterColors < presetColors.length() && counterColors <= ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY) {
            onColorPickerView()
                .performOpenColorPicker()
            onColorPickerView()
                .performClickColorPickerPresetSelectorButton(counterColors)
            onColorPickerView()
                .onPositiveButton()
                .perform(click())
            counterColors++
        }
        onColorPickerView()
            .performOpenColorPicker()
        var historyCounter = 0
        var colorCounter = ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY
        while (historyCounter < ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY) {
            ColorPickerViewInteraction.onColorPickerView()
                .checkHistoryColor(historyCounter, presetColors.getColor(colorCounter, Color.BLACK))
            historyCounter++
            colorCounter--
        }
        presetColors.recycle()
    }

    @Test
    fun testColorHistoryShowsRGBSelectorColors() {
        launchActivityRule.activity
        onColorPickerView()
            .performOpenColorPicker()
        onView(
            Matchers.allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(click())
        onView(
            withClassName(
                Matchers.containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        ).check(matches(isDisplayed()))
        onView(withId(R.id.color_picker_color_rgb_seekbar_red))
            .perform(scrollTo(), UiInteractions.touchCenterRight())
        onView(withId(R.id.color_picker_color_rgb_seekbar_green))
            .perform(scrollTo(), UiInteractions.touchCenterRight())
        onView(withId(R.id.color_picker_color_rgb_seekbar_blue))
            .perform(scrollTo(), UiInteractions.touchCenterRight())
        onView(withId(R.id.color_picker_color_rgb_seekbar_alpha))
            .perform(scrollTo(), UiInteractions.touchCenterRight())
        onColorPickerView()
            .onPositiveButton()
            .perform(click())
        onColorPickerView()
            .performOpenColorPicker()
        ColorPickerViewInteraction.onColorPickerView().checkHistoryColor(0, -0x1)
        onView(
            Matchers.allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(click())
        onView(
            withClassName(
                Matchers.containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        ).check(matches(isDisplayed()))
        onView(withId(R.id.color_picker_color_rgb_seekbar_red))
            .perform(scrollTo(), touchCenterLeft())
        onView(withId(R.id.color_picker_color_rgb_seekbar_green))
            .perform(scrollTo(), touchCenterLeft())
        onView(withId(R.id.color_picker_color_rgb_seekbar_blue))
            .perform(scrollTo(), touchCenterLeft())
        onView(withId(R.id.color_picker_color_rgb_seekbar_alpha))
            .perform(scrollTo(), touchCenterLeft())
        onColorPickerView()
            .onPositiveButton()
            .perform(click())
        onColorPickerView()
            .performOpenColorPicker()
        ColorPickerViewInteraction.onColorPickerView().checkHistoryColor(0, 0x00000000)
    }

    @Test
    fun testColorHistoryPreservedWhenClickingNewImage() {
        val resources = launchActivityRule.activity.resources
        val presetColors =
            resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors)
        var counterColors = 0
        while (counterColors < presetColors.length() && counterColors < ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY) {
            onColorPickerView()
                .performOpenColorPicker()
            onColorPickerView()
                .performClickColorPickerPresetSelectorButton(counterColors)
            onColorPickerView()
                .onPositiveButton()
                .perform(click())
            counterColors++
        }
        TopBarViewInteraction.onTopBarView().performOpenMoreOptions()
        Espresso.onView(withText(R.string.menu_new_image)).perform(click())
        onView(withText(R.string.discard_button_text))
            .perform(click())
        onColorPickerView()
            .performOpenColorPicker()
        onColorPickerView()
            .checkHistoryColor(3, presetColors.getColor(0, Color.BLACK))
        presetColors.recycle()
    }

    @Test
    fun testColorHistoryDeletedWhenRestartingApp() {
        val resources = launchActivityRule.activity.resources
        val presetColors =
            resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors)
        onColorPickerView()
            .performOpenColorPicker()
        var counterColors = 0
        while (counterColors < presetColors.length() && counterColors < ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY) {
            onColorPickerView()
                .performClickColorPickerPresetSelectorButton(counterColors)
            counterColors++
        }
        launchActivityRule.finishActivity()
        launchActivityRule.launchActivity(Intent())
        onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(withId(R.id.color_history_text_view)).check(
            matches(
                Matchers.not(isDisplayed())
            )
        )
        presetColors.recycle()
    }

    @Test
    fun testSaveColorHistoryInCatrobatFile() {
        val activity = launchActivityRule.activity
        val resources = activity.resources
        val presetColors =
            resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors)
        run {
            var counterColors = 0
            while (counterColors < presetColors.length() && counterColors < ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY) {
                ColorPickerViewInteraction.onColorPickerView()
                    .performOpenColorPicker()
                ColorPickerViewInteraction.onColorPickerView()
                    .performClickColorPickerPresetSelectorButton(counterColors)
                ColorPickerViewInteraction.onColorPickerView()
                    .onPositiveButton()
                    .perform(click())
                counterColors++
            }
        }
        saveCatrobatImage()
        val uri = activity.model.savedPictureUri
        launchActivityRule.finishActivity()
        var intent = Intent()
        intent.putExtra(PAINTROID_PICTURE_PATH, "")
        intent.putExtra(PAINTROID_PICTURE_NAME, IMAGE_NAME)
        launchActivityRuleWithIntent.launchActivity(intent)
        intent = Intent()
        intent.data = uri
        val resultOK = ActivityResult(Activity.RESULT_OK, intent)
        Intents.intending(IntentMatchers.hasAction(Intent.ACTION_GET_CONTENT)).respondWith(resultOK)
        var counterColors = 0
        while (counterColors + ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY < presetColors.length() && counterColors < ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY) {
            onColorPickerView()
                .performOpenColorPicker()
            onColorPickerView()
                .performClickColorPickerPresetSelectorButton(counterColors + ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY)
            onColorPickerView()
                .onPositiveButton()
                .perform(click())
            counterColors++
        }
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        TopBarViewInteraction.onTopBarView().performOpenMoreOptions()
        onView(withText(R.string.menu_load_image))
            .perform(click())
        onView(withText(R.string.menu_replace_image))
            .perform(click())
        onView(withText(R.string.discard_button_text))
            .perform(click())
        onColorPickerView()
            .performOpenColorPicker()
        onColorPickerView()
            .checkHistoryColor(3, presetColors.getColor(0, Color.BLACK))
        presetColors.recycle()
        if (!deletionFileList.isEmpty() && deletionFileList[0] != null && deletionFileList[0]!!
                .exists()
        ) {
            assertTrue(deletionFileList[0]!!.delete())
        }
    }

    private fun saveCatrobatImage() {
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        onView(withText(R.string.menu_save_image))
            .perform(scrollTo(), click())
        onView(withId(R.id.pocketpaint_save_dialog_spinner))
            .perform(click())
        Espresso.onData(
            AllOf.allOf(
                Matchers.`is`<Any>(
                    Matchers.instanceOf<Any>(
                        String::class.java
                    )
                ),
                Matchers.`is`<String>(CATROBAT_IMAGE_ENDING)
            )
        ).inRoot(RootMatchers.isPlatformPopup()).perform(click())
        onView(withId(R.id.pocketpaint_image_name_save_text))
            .perform(replaceText(IMAGE_NAME))
        onView(withText(R.string.save_button_text))
            .perform(click())
    }

    companion object {
        private val TAB_VIEW_PRESET_SELECTOR_CLASS = PresetSelectorView::class.java.simpleName
        private val TAB_VIEW_HSV_SELECTOR_CLASS = HSVColorPickerView::class.java.simpleName
        private val TAB_VIEW_RGBA_SELECTOR_CLASS = RgbSelectorView::class.java.simpleName
        private const val IMAGE_NAME = "colorDialogTestCatrobatImage"
        private const val TEXT_RGB_MIN = "0"
        private const val TEXT_RGB_MAX = "255"
        private const val TEXT_ALPHA_MIN = "0"
        private const val TEXT_ALPHA_MAX = "100"
        private const val TEXT_PERCENT_SIGN = "%"
    }
}