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

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.colorpicker.HSVColorPickerView
import org.catrobat.paintroid.colorpicker.PresetSelectorView
import org.catrobat.paintroid.colorpicker.RgbSelectorView
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.UiMatcher
import org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction.onColorPickerView
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolReference
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ColorDialogIntegrationTest {
    @Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    private var toolReference: ToolReference? = null

    @Before
    fun setUp() { toolReference = launchActivityRule.activity.toolReference }

    private fun getColorById(colorId: Int): Int = launchActivityRule.activity.resources.getColor(colorId)

    @Test
    fun testStandardTabSelected() {
        onColorPickerView().performOpenColorPicker()
        onView(
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
        onColorPickerView()
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
        onColorPickerView().performOpenColorPicker()
        onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_PRESET_SELECTOR_CLASS
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_hsv)
            )
        ).perform(ViewActions.click())
        onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_HSV_SELECTOR_CLASS
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_preset)
            )
        ).perform(ViewActions.click())
        onView(
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

        onColorPickerView().performOpenColorPicker()
        onView(ViewMatchers.withId(R.id.color_picker_base_layout)).perform(ViewActions.swipeUp())
        onView(ViewMatchers.withId(R.id.color_alpha_slider))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
        onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_hsv)
            )
        ).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.color_picker_base_layout)).perform(ViewActions.swipeUp())
        onView(ViewMatchers.withId(R.id.color_alpha_slider))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
        onColorPickerView()
            .onPositiveButton()
            .perform(ViewActions.click())

        val currentSelectColor = toolReference?.tool?.drawPaint?.color

        onColorPickerView().performOpenColorPicker()
        onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.color_picker_base_layout))
            .perform(ViewActions.swipeUp())
        onView(ViewMatchers.withId(R.id.color_picker_alpha_row))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.withText(
                        String.format(
                            "#%02X%02X%02X",
                            currentSelectColor?.let {
                                Color.red(
                                    it
                                )
                            },
                            currentSelectColor?.let {
                                Color.green(
                                    it
                                )
                            },
                            currentSelectColor?.let {
                                Color.blue(
                                    it
                                )
                            }
                        )
                    )
                )
            )
    }

    @Test
    fun showAlphaSliderFromCatroid() {
        launchActivityRule.activity.model.isOpenedFromCatroid = true
        onColorPickerView().performOpenColorPicker()
        onView(ViewMatchers.withId(R.id.color_picker_base_layout)).perform(ViewActions.swipeUp())
        onView(ViewMatchers.withId(R.id.color_alpha_slider)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_hsv)
            )
        ).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.color_picker_base_layout)).perform(ViewActions.swipeUp())
        onView(ViewMatchers.withId(R.id.color_alpha_slider)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun showAlphaSliderIfNotCatroidFlagSet() {
        onColorPickerView().performOpenColorPicker()
        onView(ViewMatchers.withId(R.id.color_picker_base_layout)).perform(ViewActions.swipeUp())
        onView(ViewMatchers.withId(R.id.color_alpha_slider)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_hsv)
            )
        ).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.color_picker_base_layout)).perform(ViewActions.swipeUp())
        onView(ViewMatchers.withId(R.id.color_alpha_slider)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun dontShowAlphaSliderInRgb() {
        onColorPickerView().performOpenColorPicker()
        onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.color_picker_base_layout)).perform(ViewActions.swipeUp())
        onView(ViewMatchers.withId(R.id.color_alpha_slider))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
    }

    @Test
    fun testColorSelectionChangesNewColorViewColor() {
        onColorPickerView().performOpenColorPicker()
        val resources = launchActivityRule.activity.resources
        val presetColors =
            resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors)
        for (counterColors in 0 until presetColors.length()) {
            onColorPickerView().performClickColorPickerPresetSelectorButton(counterColors)
            val arrayColor = presetColors.getColor(counterColors, Color.BLACK)
            onView(
                Matchers.allOf(
                    ViewMatchers.withId(R.id.color_picker_new_color_view),
                    Matchers.instanceOf(View::class.java)
                )
            )
                .check(ViewAssertions.matches(UiMatcher.withBackgroundColor(arrayColor)))
        }
        presetColors.recycle()
    }

    @Test
    fun testColorNewColorViewChangesStandard() {
        val resources = launchActivityRule.activity.resources
        val presetColors = resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors)
        for (counterColors in 0 until presetColors.length()) {
            onColorPickerView().performOpenColorPicker()
            onColorPickerView().performClickColorPickerPresetSelectorButton(counterColors)
            onColorPickerView()
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
        onColorPickerView().performOpenColorPicker()
        if (selectedColor != null) { onColorPickerView().checkCurrentViewColor(selectedColor) }
    }

    @Test
    fun testCurrentColorViewDoesNotChangeColor() {
        onColorPickerView().performOpenColorPicker()
        val initialColor = toolReference?.tool?.drawPaint?.color
        val resources = launchActivityRule.activity.resources
        val presetColors = resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors)

        for (counterColors in 0 until presetColors.length()) {
            onColorPickerView().performClickColorPickerPresetSelectorButton(counterColors)
            onView(
                Matchers.allOf(
                    ViewMatchers.withId(R.id.color_picker_current_color_view),
                    Matchers.instanceOf(View::class.java)
                )
            )
                .check(ViewAssertions.matches(initialColor?.let { UiMatcher.withBackgroundColor(it) }))
        }
        presetColors.recycle()
    }

    @Test
    fun testColorPickerDialogOnBackPressedSelectedColorShouldChange() {
        val initialColor = toolReference?.tool?.drawPaint?.color
        onColorPickerView().performOpenColorPicker()
        onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_preset)
            )
        ).perform(ViewActions.click())
        onView(
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
        onColorPickerView()
            .performClickColorPickerPresetSelectorButton(colorToSelectIndex)

        // Close color picker dialog
        onView(ViewMatchers.isRoot()).perform(ViewActions.pressBack())
        val currentSelectedColor = toolReference?.tool?.drawPaint?.color
        Assert.assertEquals(
            "Selected color has not changed",
            colorToSelect.toLong(),
            currentSelectedColor?.toLong()
        )
    }

    @Test
    fun testIfRGBSeekBarsDoChangeColor() {
        val resources = launchActivityRule.activity.resources
        val presetColors = resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors)

        onColorPickerView().performOpenColorPicker()
        onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_preset)
            )
        ).perform(ViewActions.click())
        onView(ViewMatchers.withClassName(Matchers.containsString(TAB_VIEW_PRESET_SELECTOR_CLASS))).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onColorPickerView().performClickColorPickerPresetSelectorButton(0)
        onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        onView(ViewMatchers.withClassName(Matchers.containsString(TAB_VIEW_RGBA_SELECTOR_CLASS))).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_textview_red)).check(
            ViewAssertions.matches(
                Matchers.allOf(
                    ViewMatchers.isDisplayed(),
                    ViewMatchers.withText(R.string.color_red),
                    UiMatcher.withTextColor(getColorById(R.color.pocketpaint_color_picker_rgb_red))
                )
            )
        )
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_textview_green)).check(
            ViewAssertions.matches(
                Matchers.allOf(
                    ViewMatchers.isDisplayed(),
                    ViewMatchers.withText(R.string.color_green),
                    UiMatcher.withTextColor(getColorById(R.color.pocketpaint_color_picker_rgb_green))
                )
            )
        )
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_textview_blue)).check(
            ViewAssertions.matches(
                Matchers.allOf(
                    ViewMatchers.isDisplayed(),
                    ViewMatchers.withText(R.string.color_blue),
                    UiMatcher.withTextColor(getColorById(R.color.pocketpaint_color_picker_rgb_blue))
                )
            )
        )
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_textview_alpha)).check(
            ViewAssertions.matches(
                Matchers.allOf(
                    ViewMatchers.isDisplayed(),
                    ViewMatchers.withText(R.string.color_alpha),
                    UiMatcher.withTextColor(getColorById(R.color.pocketpaint_color_picker_rgb_alpha))
                )
            )
        )
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_red)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_green)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_blue)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_alpha)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.color_picker_rgb_red_value)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.color_picker_rgb_green_value)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.color_picker_rgb_blue_value)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.color_picker_rgb_alpha_value)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(
            Matchers.allOf(
                ViewMatchers.withText(TEXT_PERCENT_SIGN),
                ViewMatchers.hasSibling(ViewMatchers.withId(R.id.color_picker_rgb_alpha_value))
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        val currentSelectedColor = presetColors.getColor(0, Color.BLACK)
        onView(ViewMatchers.withId(R.id.color_picker_rgb_red_value)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(Color.red(currentSelectedColor).toString())
            )
        )
        onView(ViewMatchers.withId(R.id.color_picker_rgb_green_value)).check(
            ViewAssertions.matches(ViewMatchers.withText(Color.green(currentSelectedColor).toString()))
        )
        onView(ViewMatchers.withId(R.id.color_picker_rgb_blue_value)).check(
            ViewAssertions.matches(ViewMatchers.withText(Color.blue(currentSelectedColor).toString()))
        )
        onView(ViewMatchers.withId(R.id.color_picker_rgb_alpha_value)).check(
            ViewAssertions.matches(ViewMatchers.withText((Color.alpha(currentSelectedColor) / 2.55f).toInt().toString()))
        )
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_red)).perform(UiInteractions.touchCenterLeft())
        onView(ViewMatchers.withId(R.id.color_picker_rgb_red_value)).check(ViewAssertions.matches(ViewMatchers.withText(TEXT_RGB_MIN)))
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_red)).perform(UiInteractions.touchCenterRight())
        onView(ViewMatchers.withId(R.id.color_picker_rgb_red_value)).check(ViewAssertions.matches(ViewMatchers.withText(TEXT_RGB_MAX)))
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_green)).perform(UiInteractions.touchCenterLeft())
        onView(ViewMatchers.withId(R.id.color_picker_rgb_green_value)).check(ViewAssertions.matches(ViewMatchers.withText(TEXT_RGB_MIN)))
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_green)).perform(UiInteractions.touchCenterRight())
        onView(ViewMatchers.withId(R.id.color_picker_rgb_green_value)).check(ViewAssertions.matches(ViewMatchers.withText(TEXT_RGB_MAX)))
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_blue)).perform(UiInteractions.touchCenterLeft())
        onView(ViewMatchers.withId(R.id.color_picker_rgb_blue_value)).check(ViewAssertions.matches(ViewMatchers.withText(TEXT_RGB_MIN)))
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_blue)).perform(UiInteractions.touchCenterRight())
        onView(ViewMatchers.withId(R.id.color_picker_rgb_blue_value)).check(ViewAssertions.matches(ViewMatchers.withText(TEXT_RGB_MAX)))
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_alpha)).perform(UiInteractions.touchCenterLeft())
        onView(ViewMatchers.withId(R.id.color_picker_rgb_alpha_value)).check(ViewAssertions.matches(ViewMatchers.withText(TEXT_ALPHA_MIN)))
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_alpha)).perform(UiInteractions.touchCenterRight())
        onView(ViewMatchers.withId(R.id.color_picker_rgb_alpha_value)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    TEXT_ALPHA_MAX
                )
            )
        )

        // Select color red #FFFF0000 by using hex input
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex)).perform(ViewActions.replaceText("#FFFF0000"))
        onColorPickerView().checkNewColorViewColor(Color.RED)
        onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())

        // Select color blue #FF0000FF by using seekbars
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_red)).perform(UiInteractions.touchCenterLeft())
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_green)).perform(UiInteractions.touchCenterLeft())
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_blue)).perform(UiInteractions.touchCenterRight())
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_alpha)).perform(UiInteractions.touchCenterRight())
        onColorPickerView().onPositiveButton().perform(ViewActions.click())
        Assert.assertNotEquals("Selected color changed to blue from black", toolReference?.tool?.drawPaint?.color?.toLong(), Color.BLACK.toLong())
        Assert.assertEquals("Selected color is not blue", toolReference?.tool?.drawPaint?.color?.toLong(), Color.BLUE.toLong())
    }

    @Test
    fun testHEXEditTextMaxInputLength() {
        onColorPickerView().performOpenColorPicker()
        onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        onView(
            ViewMatchers.withClassName(Matchers.containsString(TAB_VIEW_RGBA_SELECTOR_CLASS))
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex))
            .perform(ViewActions.replaceText("#0123456789ABCDEF01234"))
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex)).check(
            ViewAssertions.matches(ViewMatchers.withText(String.format("#0123456789ABCDEF0")))
        )
    }

    @Test
    fun testHEXUpdatingOnColorChange() {
        onColorPickerView().performOpenColorPicker()
        onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_preset)
            )
        ).perform(ViewActions.click())
        onView(ViewMatchers.withClassName(Matchers.containsString(TAB_VIEW_PRESET_SELECTOR_CLASS)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onColorPickerView().performClickColorPickerPresetSelectorButton(10)
        onColorPickerView().onPositiveButton().perform(ViewActions.click())
        var currentSelectColor: Int? = toolReference?.tool?.drawPaint?.color
        onColorPickerView().performOpenColorPicker()
        onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        onView(
            ViewMatchers.withClassName(Matchers.containsString(TAB_VIEW_RGBA_SELECTOR_CLASS))
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        if (currentSelectColor != null) {
            onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex)).check(
                ViewAssertions.matches(ViewMatchers.withText(String.format("#FF%06X", 0xFFFFFF and currentSelectColor.toInt())))
            )
        }
        onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_hsv)
            )
        ).perform(ViewActions.click())
        onView(
            ViewMatchers.withClassName(Matchers.containsString(TAB_VIEW_HSV_SELECTOR_CLASS))
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onColorPickerView().perform(UiInteractions.touchCenterMiddle())
        onColorPickerView().onPositiveButton().perform(ViewActions.click())
        currentSelectColor = toolReference?.tool?.drawPaint?.color
        onColorPickerView().performOpenColorPicker()
        onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        onView(
            ViewMatchers.withClassName(Matchers.containsString(TAB_VIEW_RGBA_SELECTOR_CLASS))
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        if (currentSelectColor != null) {
            onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex)).check(
                ViewAssertions.matches(ViewMatchers.withText(String.format("#FF%06X", 0xFFFFFF and currentSelectColor.toInt())))
            )
        }
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_red))
            .perform(UiInteractions.touchCenterLeft())
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_blue))
            .perform(UiInteractions.touchCenterLeft())
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_green))
            .perform(UiInteractions.touchCenterLeft())
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_alpha))
            .perform(UiInteractions.touchCenterRight())
        onColorPickerView().onPositiveButton().perform(ViewActions.click())
        currentSelectColor = toolReference?.tool?.drawPaint?.color
        onColorPickerView().performOpenColorPicker()
        onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        onView(
            ViewMatchers.withClassName(Matchers.containsString(TAB_VIEW_RGBA_SELECTOR_CLASS))
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        if (currentSelectColor != null) {
            onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex)).check(
                ViewAssertions.matches(ViewMatchers.withText(String.format("#FF%06X", 0xFFFFFF and currentSelectColor.toInt())))
            )
        }
    }

    @Test
    fun testHEXEditTextMarkingWrongInput() {
        onColorPickerView().performOpenColorPicker()
        onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // set to invalid length of 6 (alpha missing)
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex)).perform(ViewActions.replaceText("#FF0000"))
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex))
            .check(ViewAssertions.matches(ViewMatchers.hasTextColor(R.color.pocketpaint_color_picker_hex_wrong_value_red)))

        // set to invalid value
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex)).perform(ViewActions.replaceText("#FFXXYYZZ"))
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex))
            .check(ViewAssertions.matches(ViewMatchers.hasTextColor(R.color.pocketpaint_color_picker_hex_wrong_value_red)))

        // set to invalid value (# missing)
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex)).perform(ViewActions.replaceText("FF000000"))
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex))
            .check(ViewAssertions.matches(ViewMatchers.hasTextColor(R.color.pocketpaint_color_picker_hex_wrong_value_red)))
    }

    @Test
    fun testHEXEditTextMarkingCorrectInputAfterWrongInput() {
        onColorPickerView().performOpenColorPicker()
        onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        onView(ViewMatchers.withClassName(Matchers.containsString(TAB_VIEW_RGBA_SELECTOR_CLASS)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // set to invalid length of 6 (alpha missing)
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex))
            .perform(ViewActions.replaceText("#FF0000"))

        // set correct HEX value
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex))
            .perform(ViewActions.replaceText("#FF000000"))
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex))
            .check(ViewAssertions.matches(ViewMatchers.hasTextColor(R.color.pocketpaint_color_picker_hex_correct_black)))
    }

    @Test
    fun testHEXEditTextInitialColorIsSetCorrectly() {
        onColorPickerView().performOpenColorPicker()
        onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        onView(ViewMatchers.withClassName(Matchers.containsString(TAB_VIEW_RGBA_SELECTOR_CLASS))
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Initial text color should be black
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex))
            .check(ViewAssertions.matches(ViewMatchers.hasTextColor(R.color.pocketpaint_color_picker_hex_correct_black)))
    }

    @Test
    fun testOpenColorPickerOnClickOnColorButton() {
        onColorPickerView().performOpenColorPicker()
        onView(ViewMatchers.withId(R.id.color_picker_base_layout)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onColorPickerView().check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testStandardColorDoesNotChangeOnCancelButtonPress() {
        val initialColor = toolReference?.tool?.drawPaint?.color
        onColorPickerView().performOpenColorPicker()
        onColorPickerView().performClickColorPickerPresetSelectorButton(0)
        onColorPickerView().onNegativeButton().perform(ViewActions.click())
        if (initialColor != null) { onToolProperties().checkMatchesColor(initialColor) }
    }

    @Test
    fun testStandardColorDoesChangeOnCancel() {
        val initialColor = toolReference?.tool?.drawPaint?.color
        onColorPickerView().performOpenColorPicker()
        onColorPickerView().performClickColorPickerPresetSelectorButton(0)
        onColorPickerView().perform(ViewActions.pressBack())
        if (initialColor != null) { onToolProperties().checkDoesNotMatchColor(initialColor) }
    }

    @Test
    fun testColorOnlyUpdatesOncePerColorPickerIntent() {
        val initialColor = toolReference?.tool?.drawPaint?.color
        onColorPickerView().performOpenColorPicker()
        onColorPickerView().performClickColorPickerPresetSelectorButton(0)
        if (initialColor != null) { onToolProperties().checkMatchesColor(initialColor) }
        onColorPickerView().performClickColorPickerPresetSelectorButton(1)
        if (initialColor != null) { onToolProperties().checkMatchesColor(initialColor) }
        onColorPickerView().performClickColorPickerPresetSelectorButton(2)
        if (initialColor != null) { onToolProperties().checkMatchesColor(initialColor) }
        onColorPickerView().perform(ViewActions.pressBack())
        if (initialColor != null) { onToolProperties().checkDoesNotMatchColor(initialColor) }
    }

    @Test
    fun testColorPickerRemainsOpenOnOrientationChange() {
        onColorPickerView().performOpenColorPicker()
        launchActivityRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onColorPickerView().check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testColorPickerTabRestoredOnOrientationChange() {
        onColorPickerView().performOpenColorPicker()
        onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        )
            .perform(ViewActions.click())
        launchActivityRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onView(ViewMatchers.withClassName(Matchers.containsString(TAB_VIEW_RGBA_SELECTOR_CLASS)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testColorPickerInitializesRgbTabTransparentColor() {
        val presetColors =
            launchActivityRule.activity.resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors)
        onColorPickerView().performOpenColorPicker()
        onColorPickerView().performClickColorPickerPresetSelectorButton(presetColors.length() - 1)
        onColorPickerView().onPositiveButton().perform(ViewActions.click())
        onColorPickerView().performOpenColorPicker()
        onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.color_picker_rgb_red_value)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    Color.red(Color.TRANSPARENT).toString()
                )
            )
        )
        onView(ViewMatchers.withId(R.id.color_picker_rgb_green_value)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    Color.green(Color.TRANSPARENT).toString()
                )
            )
        )
        onView(ViewMatchers.withId(R.id.color_picker_rgb_blue_value)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    Color.blue(Color.TRANSPARENT).toString()
                )
            )
        )
        onView(ViewMatchers.withId(R.id.color_picker_rgb_alpha_value)).check(
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
        onColorPickerView().performOpenColorPicker()
        onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex))
            .perform(ViewActions.replaceText("#FFFF0000xxxx"))
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_blue))
            .perform(UiInteractions.touchCenterRight())
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_hex)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(String.format("#FF%06X", 0xFFFFFF and -0xffff01))
            )
        )
    }

    @Test
    fun testPipetteButtonIsDisplayed() {
        onColorPickerView().performOpenColorPicker()
        onView(ViewMatchers.withId(R.id.color_picker_pipette_btn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.color_picker_pipette)))
    }

    @Test
    fun testColorViewsAreDisplayed() {
        onColorPickerView().performOpenColorPicker()
        onView(ViewMatchers.withId(R.id.color_picker_new_color_view))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(UiMatcher.withBackgroundColor(Color.BLACK)))
        onView(ViewMatchers.withId(R.id.color_picker_current_color_view))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(UiMatcher.withBackgroundColor(Color.BLACK)))
    }

    @Test
    fun alphaValueIsSetInSliderWhenChangedInSeekBar() {
        val idlingResource = launchActivityRule.activity.idlingResource
        IdlingRegistry.getInstance().register(idlingResource)
        onColorPickerView().performOpenColorPicker()
        onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.color_picker_color_rgb_seekbar_alpha))
            .perform(UiInteractions.touchCenterMiddle())
        onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_preset)
            )
        ).perform(ViewActions.scrollTo(), ViewActions.click())
        onColorPickerView().onPositiveButton().perform(ViewActions.click())
        onToolProperties().checkMatchesColor(Color.parseColor("#7F000000"))
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun alphaValueIsSetInSeekBarWhenChangedInSlider() {
        onColorPickerView().performOpenColorPicker()
        onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_preset)
            )
        ).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.color_alpha_slider))
            .perform(ViewActions.scrollTo(), UiInteractions.touchCenterMiddle())
        onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.color_picker_rgb_alpha_value)).check(
            ViewAssertions.matches(
                ViewMatchers.withText("50")
            )
        )
    }

    @Test
    fun testPreserveZoomAfterPipetteUsage() {
        val perspective = launchActivityRule.activity.perspective
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        val scale = 4f

        perspective.scale = scale
        onColorPickerView().performOpenColorPicker()
        onView(ViewMatchers.withId(R.id.color_picker_pipette_btn)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.doneAction)).perform(ViewActions.click())
        onColorPickerView().performCloseColorPickerWithDialogButton()
        Assert.assertEquals(scale, perspective.scale, Float.MIN_VALUE)
    }

    companion object {
        private val TAB_VIEW_PRESET_SELECTOR_CLASS = PresetSelectorView::class.java.simpleName
        private val TAB_VIEW_HSV_SELECTOR_CLASS = HSVColorPickerView::class.java.simpleName
        private val TAB_VIEW_RGBA_SELECTOR_CLASS = RgbSelectorView::class.java.simpleName
        private const val TEXT_RGB_MIN = "0"
        private const val TEXT_RGB_MAX = "255"
        private const val TEXT_ALPHA_MIN = "0"
        private const val TEXT_ALPHA_MAX = "100"
        private const val TEXT_PERCENT_SIGN = "%"
    }
}
