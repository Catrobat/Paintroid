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

package org.catrobat.paintroid.test.espresso.dialog

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
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
import org.catrobat.paintroid.test.espresso.util.UiMatcher
import org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolReference
import org.hamcrest.Matchers
import org.hamcrest.core.AllOf
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.text.MessageFormat.format
import java.util.Objects

@Suppress("LargeClass")
@RunWith(AndroidJUnit4::class)
class ColorDialogIntegrationTest {
    @get:Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    var launchActivityRuleWithIntent = IntentsTestRule(MainActivity::class.java, false, false)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    private var toolReference: ToolReference? = null
    private val deletionFileList: List<File?> = ArrayList()
    @Before
    fun setUp() = launchActivityRule.activity.toolReference.also { toolReference = it }

    private fun getColorById(colorId: Int): Int = launchActivityRule.activity.resources.getColor(colorId)

    @Test
    fun testStandardTabSelected() {
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_PRESET_SELECTOR_CLASS
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testCorrectColorAfterApplyWithoutNewColorSelected() {
        val initialPaint = toolReference?.tool?.drawPaint
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
            .onPositiveButton()
            .perform(ViewActions.click())
        Assert.assertEquals(
            initialPaint?.color?.toLong(),
            toolReference?.tool?.drawPaint?.color?.toLong()
        )
    }

    @Test
    fun testTabsAreSelectable() {
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_PRESET_SELECTOR_CLASS
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_hsv)
            )
        ).perform(ViewActions.click())
        Espresso.onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_HSV_SELECTOR_CLASS
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        Espresso.onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_preset)
            )
        ).perform(ViewActions.scrollTo(), ViewActions.click())
        Espresso.onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_PRESET_SELECTOR_CLASS
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun dontShowAlphaRelatedStuffFromCatroidFormulaEditor() {
        launchActivityRule.activity.model.isOpenedFromCatroid = true
        launchActivityRule.activity.model.isOpenedFromFormulaEditorInCatroid = true
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_base_layout))
            .perform(ViewActions.swipeUp())
        Espresso.onView(ViewMatchers.withId(R.id.color_alpha_slider))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_hsv)
            )
        ).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_base_layout))
            .perform(ViewActions.swipeUp())
        Espresso.onView(ViewMatchers.withId(R.id.color_alpha_slider))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
        ColorPickerViewInteraction.onColorPickerView()
            .onPositiveButton()
            .perform(ViewActions.click())
        val currentSelectColor = toolReference?.tool?.drawPaint?.color
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_base_layout))
            .perform(ViewActions.swipeUp())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_alpha_row))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.withText(
                        format(
                            "#%02X%02X%02X",
                            currentSelectColor?.let {
                                Color.red(it)
                            },
                            currentSelectColor?.let {
                                Color.green(it)
                            },
                            currentSelectColor?.let {
                                Color.blue(it)
                            }
                        )
                    )
                )
            )
    }

    @Test
    fun showAlphaSliderFromCatroid() {
        launchActivityRule.activity.model.isOpenedFromCatroid = true
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_base_layout))
            .perform(ViewActions.swipeUp())
        Espresso.onView(ViewMatchers.withId(R.id.color_alpha_slider))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_hsv)
            )
        ).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_base_layout))
            .perform(ViewActions.swipeUp())
        Espresso.onView(ViewMatchers.withId(R.id.color_alpha_slider))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun showAlphaSliderIfNotCatroidFlagSet() {
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_base_layout))
            .perform(ViewActions.swipeUp())
        Espresso.onView(ViewMatchers.withId(R.id.color_alpha_slider))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_hsv)
            )
        ).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_base_layout))
            .perform(ViewActions.swipeUp())
        Espresso.onView(ViewMatchers.withId(R.id.color_alpha_slider))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun dontShowAlphaSliderInRgb() {
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_base_layout))
            .perform(ViewActions.swipeUp())
        Espresso.onView(ViewMatchers.withId(R.id.color_alpha_slider))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
    }

    @Test
    fun testColorSelectionChangesNewColorViewColor() {
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        val resources = launchActivityRule.activity.resources
        val presetColors =
            resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors)
        for (counterColors in 0 until presetColors.length()) {
            ColorPickerViewInteraction.onColorPickerView()
                .performClickColorPickerPresetSelectorButton(counterColors)
            val arrayColor = presetColors.getColor(counterColors, Color.BLACK)
            Espresso.onView(
                Matchers.allOf(
                    ViewMatchers.withId(R.id.color_picker_new_color_view),
                    Matchers.instanceOf(
                        View::class.java
                    )
                )
            )
                .check(ViewAssertions.matches(UiMatcher.withBackgroundColor(arrayColor)))
        }
        presetColors.recycle()
    }

    @Test
    fun testColorNewColorViewChangesStandard() {
        val resources = launchActivityRule.activity.resources
        val presetColors =
            resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors)
        for (counterColors in 0 until presetColors.length()) {
            ColorPickerViewInteraction.onColorPickerView()
                .performOpenColorPicker()
            ColorPickerViewInteraction.onColorPickerView()
                .performClickColorPickerPresetSelectorButton(counterColors)
            ColorPickerViewInteraction.onColorPickerView()
                .onPositiveButton()
                .perform(ViewActions.click())
            val arrayColor = presetColors.getColor(counterColors, Color.BLACK)
            val selectedColor = toolReference?.tool?.drawPaint?.color
            Assert.assertEquals(
                "Color in array and selected color not the same",
                arrayColor.toLong(),
                selectedColor?.toLong()
            )
        }
        presetColors.recycle()
    }

    @Test
    fun testCurrentColorViewHasInitialColor() {
        val selectedColor = toolReference?.tool?.drawPaint?.color
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        if (selectedColor != null) {
            ColorPickerViewInteraction.onColorPickerView()
                .checkCurrentViewColor(selectedColor)
        }
    }

    @Test
    fun testCurrentColorViewDoesNotChangeColor() {
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        val initialColor = toolReference?.tool?.drawPaint?.color
        val resources = launchActivityRule.activity.resources
        val presetColors =
            resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors)
        for (counterColors in 0 until presetColors.length()) {
            ColorPickerViewInteraction.onColorPickerView()
                .performClickColorPickerPresetSelectorButton(counterColors)
            if (initialColor != null) {
                Espresso.onView(
                    Matchers.allOf(
                        ViewMatchers.withId(R.id.color_picker_current_color_view),
                        Matchers.instanceOf(
                            View::class.java
                        )
                    )
                ).check(ViewAssertions.matches(UiMatcher.withBackgroundColor(initialColor)))
            }
        }
        presetColors.recycle()
    }

    @Test
    fun testColorPickerDialogOnBackPressedSelectedColorShouldChange() {
        val initialColor = toolReference?.tool?.drawPaint?.color
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_preset)
            )
        ).perform(ViewActions.click())
        Espresso.onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_PRESET_SELECTOR_CLASS
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        val presetColors =
            launchActivityRule.activity.resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors)
        val colorToSelectIndex = presetColors.length() / 2
        val colorToSelect = presetColors.getColor(colorToSelectIndex, Color.WHITE)
        presetColors.recycle()
        Assert.assertNotEquals(
            "Selected color should not be the same as the initial color",
            colorToSelect.toLong(),
            initialColor?.toLong()
        )
        ColorPickerViewInteraction.onColorPickerView()
            .performClickColorPickerPresetSelectorButton(colorToSelectIndex)

        // Close color picker dialog
        Espresso.onView(ViewMatchers.isRoot()).perform(ViewActions.pressBack())
        val currentSelectedColor = toolReference?.tool?.drawPaint?.color
        Assert.assertEquals(
            "Selected color has not changed",
            colorToSelect.toLong(),
            currentSelectedColor?.toLong()
        )
    }

    @Suppress("LongMethod")
    @Test
    fun testIfRGBSeekBarsDoChangeColor() {
        val resources = launchActivityRule.activity.resources
        val presetColors =
            resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors)
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_preset)
            )
        ).perform(ViewActions.click())
        Espresso.onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_PRESET_SELECTOR_CLASS
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        ColorPickerViewInteraction.onColorPickerView()
            .performClickColorPickerPresetSelectorButton(0)
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        Espresso.onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_textview_red))
            .perform(ViewActions.scrollTo())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_textview_red)).check(
            ViewAssertions.matches(
                Matchers.allOf(
                    ViewMatchers.isDisplayed(),
                    ViewMatchers.withText(R.string.color_red),
                    UiMatcher.withTextColor(getColorById(R.color.pocketpaint_color_picker_rgb_red))
                )
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_textview_green)).check(
            ViewAssertions.matches(
                Matchers.allOf(
                    ViewMatchers.isDisplayed(),
                    ViewMatchers.withText(R.string.color_green),
                    UiMatcher.withTextColor(getColorById(R.color.pocketpaint_color_picker_rgb_green))
                )
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_textview_blue)).check(
            ViewAssertions.matches(
                Matchers.allOf(
                    ViewMatchers.isDisplayed(),
                    ViewMatchers.withText(R.string.color_blue),
                    UiMatcher.withTextColor(getColorById(R.color.pocketpaint_color_picker_rgb_blue))
                )
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_textview_alpha))
            .perform(ViewActions.scrollTo())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_textview_alpha)).check(
            ViewAssertions.matches(
                Matchers.allOf(
                    ViewMatchers.isDisplayed(),
                    ViewMatchers.withText(R.string.color_alpha),
                    UiMatcher.withTextColor(getColorById(R.color.pocketpaint_color_picker_rgb_alpha))
                )
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_textview_red))
            .perform(ViewActions.scrollTo())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_red))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_green))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_blue))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_alpha))
            .perform(ViewActions.scrollTo())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_alpha))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex))
            .perform(ViewActions.scrollTo())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_red_value))
            .perform(ViewActions.scrollTo())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_red_value))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_green_value))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_blue_value))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_alpha_value))
            .perform(ViewActions.scrollTo())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_alpha_value))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withText(TEXT_PERCENT_SIGN),
                ViewMatchers.hasSibling(ViewMatchers.withId(R.id.color_picker_rgb_alpha_value))
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        val currentSelectedColor = presetColors.getColor(0, Color.BLACK)
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_red_value))
            .perform(ViewActions.scrollTo())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_red_value)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    Color.red(currentSelectedColor).toString()
                )
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_green_value)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    Color.green(currentSelectedColor).toString()
                )
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_blue_value)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    Color.blue(currentSelectedColor).toString()
                )
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_alpha_value))
            .perform(ViewActions.scrollTo())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_alpha_value)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    (Color.alpha(currentSelectedColor) / 2.55f).toInt().toString()
                )
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_red))
            .perform(ViewActions.scrollTo(), UiInteractions.touchCenterLeft())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_red_value)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    TEXT_RGB_MIN
                )
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_red))
            .perform(ViewActions.scrollTo(), UiInteractions.touchCenterRight())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_red_value)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    TEXT_RGB_MAX
                )
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_green))
            .perform(ViewActions.scrollTo(), UiInteractions.touchCenterLeft())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_green_value)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    TEXT_RGB_MIN
                )
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_green))
            .perform(ViewActions.scrollTo(), UiInteractions.touchCenterRight())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_green_value)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    TEXT_RGB_MAX
                )
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_blue))
            .perform(ViewActions.scrollTo(), UiInteractions.touchCenterLeft())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_blue_value)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    TEXT_RGB_MIN
                )
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_blue))
            .perform(ViewActions.scrollTo(), UiInteractions.touchCenterRight())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_blue_value)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    TEXT_RGB_MAX
                )
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_alpha))
            .perform(ViewActions.scrollTo(), UiInteractions.touchCenterLeft())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_alpha_value)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    TEXT_ALPHA_MIN
                )
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_alpha))
            .perform(ViewActions.scrollTo(), UiInteractions.touchCenterRight())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_alpha_value)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    TEXT_ALPHA_MAX
                )
            )
        )

        // Select color red #FFFF0000 by using hex input
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex))
            .perform(ViewActions.replaceText("#FFFF0000"))
        ColorPickerViewInteraction.onColorPickerView()
            .checkNewColorViewColor(Color.RED)
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.scrollTo(), ViewActions.click())

        // Select color blue #FF0000FF by using seekbars
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_red))
            .perform(ViewActions.scrollTo(), UiInteractions.touchCenterLeft())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_green))
            .perform(ViewActions.scrollTo(), UiInteractions.touchCenterLeft())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_blue))
            .perform(ViewActions.scrollTo(), UiInteractions.touchCenterRight())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_alpha))
            .perform(ViewActions.scrollTo(), UiInteractions.touchCenterRight())
        ColorPickerViewInteraction.onColorPickerView()
            .onPositiveButton()
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Assert.assertNotEquals(
            "Selected color changed to blue from black",
            toolReference?.tool?.drawPaint?.color?.toLong(),
            Color.BLACK.toLong()
        )
        Assert.assertEquals(
            "Selected color is not blue",
            toolReference?.tool?.drawPaint?.color?.toLong(),
            Color.BLUE.toLong()
        )
    }

    @Test
    fun testHEXEditTextMaxInputLength() {
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        Espresso.onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex))
            .perform(ViewActions.replaceText("#0123456789ABCDEF01234"))
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(format("#0123456789ABCDEF0"))
            )
        )
    }

    @Suppress("LongMethod")
    @Test
    fun testHEXUpdatingOnColorChange() {
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_preset)
            )
        ).perform(ViewActions.click())
        Espresso.onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_PRESET_SELECTOR_CLASS
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        ColorPickerViewInteraction.onColorPickerView()
            .performClickColorPickerPresetSelectorButton(10)
        ColorPickerViewInteraction.onColorPickerView()
            .onPositiveButton()
            .perform(ViewActions.click())
        var currentSelectColor = toolReference?.tool?.drawPaint?.color
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        Espresso.onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        if (currentSelectColor != null) {
            Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex)).check(
                ViewAssertions.matches(
                    ViewMatchers.withText(format("#FF%06X", 0xFFFFFF and currentSelectColor))
                )
            )
        }
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_hsv)
            )
        ).perform(ViewActions.scrollTo(), ViewActions.click())
        Espresso.onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_HSV_SELECTOR_CLASS
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        ColorPickerViewInteraction.onColorPickerView()
            .perform(ViewActions.scrollTo(), UiInteractions.touchCenterMiddle())
        ColorPickerViewInteraction.onColorPickerView()
            .onPositiveButton()
            .perform(ViewActions.click())
        currentSelectColor = toolReference?.tool?.drawPaint?.color
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        Espresso.onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        if (currentSelectColor != null) {
            Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex)).check(
                ViewAssertions.matches(
                    ViewMatchers.withText(format("#FF%06X", 0xFFFFFF and currentSelectColor))
                )
            )
        }
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_red))
            .perform(ViewActions.scrollTo(), UiInteractions.touchCenterLeft())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_blue))
            .perform(ViewActions.scrollTo(), UiInteractions.touchCenterLeft())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_green))
            .perform(ViewActions.scrollTo(), UiInteractions.touchCenterLeft())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_alpha))
            .perform(ViewActions.scrollTo(), UiInteractions.touchCenterRight())
        ColorPickerViewInteraction.onColorPickerView()
            .onPositiveButton()
            .perform(ViewActions.click())
        currentSelectColor = toolReference?.tool?.drawPaint?.color
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        Espresso.onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        if (currentSelectColor != null) {
            Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex)).check(
                ViewAssertions.matches(
                    ViewMatchers.withText(format("#FF%06X", 0xFFFFFF and currentSelectColor))
                )
            )
        }
    }

    @Test
    fun testHEXEditTextMarkingWrongInput() {
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        Espresso.onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // set to invalid length of 6 (alpha missing)
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex))
            .perform(ViewActions.replaceText("#FF0000"))
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.hasTextColor(R.color.pocketpaint_color_picker_hex_wrong_value_red)
                )
            )

        // set to invalid value
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex))
            .perform(ViewActions.replaceText("#FFXXYYZZ"))
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.hasTextColor(R.color.pocketpaint_color_picker_hex_wrong_value_red)
                )
            )

        // set to invalid value (# missing)
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex))
            .perform(ViewActions.replaceText("FF000000"))
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.hasTextColor(R.color.pocketpaint_color_picker_hex_wrong_value_red)
                )
            )
    }

    @Test
    fun testHEXEditTextMarkingCorrectInputAfterWrongInput() {
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        Espresso.onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // set to invalid length of 6 (alpha missing)
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex))
            .perform(ViewActions.replaceText("#FF0000"))

        // set correct HEX value
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex))
            .perform(ViewActions.replaceText("#FF000000"))
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.hasTextColor(R.color.pocketpaint_color_picker_hex_correct_black)
                )
            )
    }

    @Test
    fun testHEXEditTextInitialColorIsSetCorrectly() {
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        Espresso.onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Inital text color should be black
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.hasTextColor(R.color.pocketpaint_color_picker_hex_correct_black)
                )
            )
    }

    @Test
    fun testOpenColorPickerOnClickOnColorButton() {
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_base_layout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        ColorPickerViewInteraction.onColorPickerView()
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testStandardColorDoesNotChangeOnCancelButtonPress() {
        val initialColor = toolReference?.tool?.drawPaint?.color
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        ColorPickerViewInteraction.onColorPickerView()
            .performClickColorPickerPresetSelectorButton(0)
        ColorPickerViewInteraction.onColorPickerView()
            .onNegativeButton()
            .perform(ViewActions.click())
        if (initialColor != null) {
            ToolPropertiesInteraction.onToolProperties()
                .checkMatchesColor(initialColor)
        }
    }

    @Test
    fun testStandardColorDoesChangeOnCancel() {
        val initialColor = toolReference?.tool?.drawPaint?.color
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        ColorPickerViewInteraction.onColorPickerView()
            .performClickColorPickerPresetSelectorButton(0)
        ColorPickerViewInteraction.onColorPickerView()
            .perform(ViewActions.pressBack())
        if (initialColor != null) {
            ToolPropertiesInteraction.onToolProperties()
                .checkDoesNotMatchColor(initialColor)
        }
    }

    @Test
    fun testColorOnlyUpdatesOncePerColorPickerIntent() {
        val initialColor = toolReference?.tool?.drawPaint?.color
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        ColorPickerViewInteraction.onColorPickerView()
            .performClickColorPickerPresetSelectorButton(0)
        if (initialColor != null) {
            ToolPropertiesInteraction.onToolProperties()
                .checkMatchesColor(initialColor)
        }
        ColorPickerViewInteraction.onColorPickerView()
            .performClickColorPickerPresetSelectorButton(1)
        if (initialColor != null) {
            ToolPropertiesInteraction.onToolProperties()
                .checkMatchesColor(initialColor)
        }
        ColorPickerViewInteraction.onColorPickerView()
            .performClickColorPickerPresetSelectorButton(2)
        if (initialColor != null) {
            ToolPropertiesInteraction.onToolProperties()
                .checkMatchesColor(initialColor)
        }
        ColorPickerViewInteraction.onColorPickerView()
            .perform(ViewActions.pressBack())
        if (initialColor != null) {
            ToolPropertiesInteraction.onToolProperties()
                .checkDoesNotMatchColor(initialColor)
        }
    }

    @Test
    fun testColorPickerRemainsOpenOnOrientationChange() {
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        launchActivityRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        ColorPickerViewInteraction.onColorPickerView()
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testColorPickerTabRestoredOnOrientationChange() {
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        )
            .perform(ViewActions.click())
        launchActivityRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        Espresso.onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        )
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testColorPickerInitializesRgbTabTransparentColor() {
        val presetColors =
            launchActivityRule.activity.resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors)
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        ColorPickerViewInteraction.onColorPickerView()
            .performClickColorPickerPresetSelectorButton(presetColors.length() - 1)
        ColorPickerViewInteraction.onColorPickerView()
            .onPositiveButton()
            .perform(ViewActions.click())
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_red_value)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    Color.red(Color.TRANSPARENT).toString()
                )
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_green_value)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    Color.green(Color.TRANSPARENT).toString()
                )
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_blue_value)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    Color.blue(Color.TRANSPARENT).toString()
                )
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_alpha_value)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    (Color.alpha(Color.TRANSPARENT) / 2.55f).toInt().toString()
                )
            )
        )
        presetColors.recycle()
    }

    @Test
    fun testInsertInvalidHexInputAndSlideSeekbar() {
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex))
            .perform(ViewActions.replaceText("#FFFF0000xxxx"))
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_blue))
            .perform(ViewActions.scrollTo(), UiInteractions.touchCenterRight())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex))
            .perform(ViewActions.scrollTo())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(format("#FF%06X", 0xFFFFFF and -0xffff01))
            )
        )
    }

    @Test
    fun testPipetteButtonIsDisplayed() {
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_pipette_btn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.color_picker_pipette)))
    }

    @Test
    fun testColorViewsAreDisplayed() {
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_new_color_view))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(UiMatcher.withBackgroundColor(Color.BLACK)))
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_current_color_view))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(UiMatcher.withBackgroundColor(Color.BLACK)))
    }

    @Test
    fun alphaValueIsSetInSliderWhenChangedInSeekBar() {
        val idlingResource = launchActivityRule.activity.idlingResource
        IdlingRegistry.getInstance().register(idlingResource)
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_alpha))
            .perform(UiInteractions.touchCenterMiddle())
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_preset)
            )
        ).perform(ViewActions.scrollTo(), ViewActions.click())
        ColorPickerViewInteraction.onColorPickerView()
            .onPositiveButton()
            .perform(ViewActions.click())
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.parseColor("#7F000000"))
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun alphaValueIsSetInSeekBarWhenChangedInSlider() {
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_preset)
            )
        ).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.color_alpha_slider))
            .perform(ViewActions.scrollTo(), UiInteractions.touchCenterMiddle())
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_rgb_alpha_value)).check(
            ViewAssertions.matches(
                ViewMatchers.withText("50")
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
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_pipette_btn))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.doneAction)).perform(ViewActions.click())
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
            while (
                counterColors < presetColors.length() &&
                counterColors < ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY
            ) {
                ColorPickerViewInteraction.onColorPickerView()
                    .performOpenColorPicker()
                ColorPickerViewInteraction.onColorPickerView()
                    .performClickColorPickerPresetSelectorButton(counterColors)
                ColorPickerViewInteraction.onColorPickerView()
                    .onPositiveButton()
                    .perform(ViewActions.click())
                counterColors++
            }
        }
        var counterColors = 0
        while (
            counterColors < presetColors.length() &&
            counterColors < ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY
        ) {
            ColorPickerViewInteraction.onColorPickerView()
                .performOpenColorPicker()
            ColorPickerViewInteraction.onColorPickerView()
                .performClickOnHistoryColor(ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY - 1)
            ColorPickerViewInteraction.onColorPickerView()
                .onPositiveButton()
                .perform(ViewActions.click())
            val arrayColor = presetColors.getColor(counterColors, Color.BLACK)
            val selectedColor = Objects.requireNonNull(toolReference?.tool)?.drawPaint?.color
            Assert.assertEquals(
                "Color in history doesn't match selection",
                arrayColor.toLong(),
                selectedColor?.toLong()
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
        while (
            counterColors < presetColors.length() &&
            counterColors <= ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY
        ) {
            ColorPickerViewInteraction.onColorPickerView()
                .performOpenColorPicker()
            ColorPickerViewInteraction.onColorPickerView()
                .performClickColorPickerPresetSelectorButton(counterColors)
            ColorPickerViewInteraction.onColorPickerView()
                .onPositiveButton()
                .perform(ViewActions.click())
            counterColors++
        }
        ColorPickerViewInteraction.onColorPickerView()
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
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        Espresso.onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_red))
            .perform(ViewActions.scrollTo(), UiInteractions.touchCenterRight())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_green))
            .perform(ViewActions.scrollTo(), UiInteractions.touchCenterRight())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_blue))
            .perform(ViewActions.scrollTo(), UiInteractions.touchCenterRight())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_alpha))
            .perform(ViewActions.scrollTo(), UiInteractions.touchCenterRight())
        ColorPickerViewInteraction.onColorPickerView()
            .onPositiveButton()
            .perform(ViewActions.click())
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        ColorPickerViewInteraction.onColorPickerView().checkHistoryColor(0, -0x1)
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        Espresso.onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_red))
            .perform(ViewActions.scrollTo(), UiInteractions.touchCenterLeft())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_green))
            .perform(ViewActions.scrollTo(), UiInteractions.touchCenterLeft())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_blue))
            .perform(ViewActions.scrollTo(), UiInteractions.touchCenterLeft())
        Espresso.onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_alpha))
            .perform(ViewActions.scrollTo(), UiInteractions.touchCenterLeft())
        ColorPickerViewInteraction.onColorPickerView()
            .onPositiveButton()
            .perform(ViewActions.click())
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        ColorPickerViewInteraction.onColorPickerView().checkHistoryColor(0, 0x00000000)
    }

    @Test
    fun testColorHistoryPreservedWhenClickingNewImage() {
        val resources = launchActivityRule.activity.resources
        val presetColors =
            resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors)
        var counterColors = 0
        while (
            counterColors < presetColors.length() &&
            counterColors < ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY
        ) {
            ColorPickerViewInteraction.onColorPickerView()
                .performOpenColorPicker()
            ColorPickerViewInteraction.onColorPickerView()
                .performClickColorPickerPresetSelectorButton(counterColors)
            ColorPickerViewInteraction.onColorPickerView()
                .onPositiveButton()
                .perform(ViewActions.click())
            counterColors++
        }
        TopBarViewInteraction.onTopBarView().performOpenMoreOptions()
        Espresso.onView(ViewMatchers.withText(R.string.menu_new_image)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.discard_button_text))
            .perform(ViewActions.click())
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        ColorPickerViewInteraction.onColorPickerView()
            .checkHistoryColor(3, presetColors.getColor(0, Color.BLACK))
        presetColors.recycle()
    }

    @Test
    fun testColorHistoryDeletedWhenRestartingApp() {
        val resources = launchActivityRule.activity.resources
        val presetColors =
            resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors)
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        var counterColors = 0
        while (
            counterColors < presetColors.length() &&
            counterColors < ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY
        ) {
            ColorPickerViewInteraction.onColorPickerView()
                .performClickColorPickerPresetSelectorButton(counterColors)
            counterColors++
        }
        launchActivityRule.finishActivity()
        launchActivityRule.launchActivity(Intent())
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(ViewMatchers.withId(R.id.color_history_text_view)).check(
            ViewAssertions.matches(
                Matchers.not(ViewMatchers.isDisplayed())
            )
        )
        presetColors.recycle()
    }

    @Suppress("LongMethod")
    @Test
    fun testSaveColorHistoryInCatrobatFile() {
        val activity = launchActivityRule.activity
        val resources = activity.resources
        val presetColors =
            resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors)
        run {
            var counterColors = 0
            while (
                counterColors < presetColors.length() &&
                counterColors < ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY
            ) {
                ColorPickerViewInteraction.onColorPickerView()
                    .performOpenColorPicker()
                ColorPickerViewInteraction.onColorPickerView()
                    .performClickColorPickerPresetSelectorButton(counterColors)
                ColorPickerViewInteraction.onColorPickerView()
                    .onPositiveButton()
                    .perform(ViewActions.click())
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
        while (
            counterColors + ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY < presetColors.length() &&
            counterColors < ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY
        ) {
            ColorPickerViewInteraction.onColorPickerView()
                .performOpenColorPicker()
            ColorPickerViewInteraction.onColorPickerView()
                .performClickColorPickerPresetSelectorButton(
                    counterColors + ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY
                )
            ColorPickerViewInteraction.onColorPickerView()
                .onPositiveButton()
                .perform(ViewActions.click())
            counterColors++
        }
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        TopBarViewInteraction.onTopBarView().performOpenMoreOptions()
        Espresso.onView(ViewMatchers.withText(R.string.menu_load_image))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.menu_replace_image))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.discard_button_text))
            .perform(ViewActions.click())
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        ColorPickerViewInteraction.onColorPickerView()
            .checkHistoryColor(3, presetColors.getColor(0, Color.BLACK))
        presetColors.recycle()
        if (deletionFileList.isNotEmpty() && deletionFileList[0] != null && deletionFileList[0]?.exists() == true
        ) {
            deletionFileList[0]?.delete()?.let { Assert.assertTrue(it) }
        }
    }

    private fun saveCatrobatImage() {
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        Espresso.onView(ViewMatchers.withText(R.string.menu_save_image))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_save_dialog_spinner))
            .perform(ViewActions.click())
        Espresso.onData(
            AllOf.allOf(
                Matchers.`is`(
                    Matchers.instanceOf<Any>(
                        String::class.java
                    )
                ),
                Matchers.`is`<String>(CATROBAT_IMAGE_ENDING)
            )
        ).inRoot(RootMatchers.isPlatformPopup()).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_image_name_save_text))
            .perform(ViewActions.replaceText(IMAGE_NAME))
        Espresso.onView(ViewMatchers.withText(R.string.save_button_text))
            .perform(ViewActions.click())
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
