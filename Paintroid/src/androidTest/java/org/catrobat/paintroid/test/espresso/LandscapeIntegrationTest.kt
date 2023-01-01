/*
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
@file:Suppress("DEPRECATION")

package org.catrobat.paintroid.test.espresso

import android.content.Context
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.graphics.Color
import androidx.annotation.ArrayRes
import androidx.annotation.ColorInt
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.colorpicker.HSVColorPickerView
import org.catrobat.paintroid.colorpicker.PresetSelectorView
import org.catrobat.paintroid.colorpicker.RgbSelectorView
import org.catrobat.paintroid.test.espresso.util.UiMatcher.withBackground
import org.catrobat.paintroid.test.espresso.util.UiMatcher.withBackgroundColor
import org.catrobat.paintroid.test.espresso.util.wrappers.BottomNavigationViewInteraction.onBottomNavigationView
import org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction.onColorPickerView
import org.catrobat.paintroid.test.espresso.util.wrappers.OptionsMenuViewInteraction.onOptionsMenu
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.Tool
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LandscapeIntegrationTest {
    private var mainActivity: MainActivity? = null
    private var idlingResource: CountingIdlingResource? = null

    @get:Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    private val currentTool: Tool?
        get() = mainActivity?.toolReference?.tool
    private val toolOptionsViewController: ToolOptionsViewController?
        get() = mainActivity?.toolOptionsViewController

    @Before
    fun setUp() {
        mainActivity = activityTestRule.activity
        idlingResource = mainActivity?.idlingResource
        IdlingRegistry.getInstance().register(idlingResource)
    }

    @After
    fun tearDown() { IdlingRegistry.getInstance().unregister(idlingResource) }

    @Test
    fun testLandscapeMode() {
        setOrientation(SCREEN_ORIENTATION_LANDSCAPE)
        setOrientation(SCREEN_ORIENTATION_PORTRAIT)
    }

    @Test
    fun testTools() {
        setOrientation(SCREEN_ORIENTATION_LANDSCAPE)
        for (toolType in ToolType.values()) {
            val tool = toolType == ToolType.IMPORTPNG ||
                toolType == ToolType.COLORCHOOSER ||
                toolType == ToolType.REDO ||
                toolType == ToolType.UNDO ||
                toolType == ToolType.LAYER ||
                !toolType.hasOptions()
            if (tool) { continue }
            onToolBarView()
                .performSelectTool(toolType)
            if (toolOptionsViewController?.isVisible?.not() == true) {
                onToolBarView()
                    .performClickSelectedToolButton()
            }
            onBottomNavigationView()
                .onCurrentClicked()
            onView(withId(R.id.pocketpaint_layout_tool_specific_options))
                .check(matches(not(isDisplayed())))
        }
    }

    @Test
    fun testCorrectSelectionInBothOrientationsBrushTool() {
        val toolType = ToolType.BRUSH
        onToolBarView()
            .performSelectTool(toolType)
        setOrientation(SCREEN_ORIENTATION_LANDSCAPE)
        assertEquals(toolType, currentTool?.toolType)
    }

    @Test
    fun testCorrectSelectionInBothOrientationsCursorTool() {
        val toolType = ToolType.CURSOR
        onToolBarView()
            .performSelectTool(toolType)
        setOrientation(SCREEN_ORIENTATION_LANDSCAPE)
        assertEquals(toolType, currentTool?.toolType)
    }

    @Test
    fun testCorrectSelectionInBothOrientationsTransformTool() {
        val toolType = ToolType.TRANSFORM
        onToolBarView()
            .performSelectTool(toolType)
        setOrientation(SCREEN_ORIENTATION_LANDSCAPE)
        assertEquals(toolType, currentTool?.toolType)
    }

    @Test
    fun testCorrectSelectionInBothOrientationsFillTool() {
        val toolType = ToolType.FILL
        onToolBarView()
            .performSelectTool(toolType)
        setOrientation(SCREEN_ORIENTATION_LANDSCAPE)
        assertEquals(toolType, currentTool?.toolType)
    }

    @Test
    fun testCorrectSelectionInBothOrientationsHandTool() {
        val toolType = ToolType.HAND
        onToolBarView()
            .performSelectTool(toolType)
        setOrientation(SCREEN_ORIENTATION_LANDSCAPE)
        assertEquals(toolType, currentTool?.toolType)
    }

    @Test
    fun testCorrectSelectionInBothOrientationsEraserTool() {
        val toolType = ToolType.ERASER
        onToolBarView()
            .performSelectTool(toolType)
        setOrientation(SCREEN_ORIENTATION_LANDSCAPE)
        assertEquals(toolType, currentTool?.toolType)
    }

    @Test
    fun testCorrectSelectionInBothOrientationsLineTool() {
        val toolType = ToolType.LINE
        onToolBarView()
            .performSelectTool(toolType)
        setOrientation(SCREEN_ORIENTATION_LANDSCAPE)
        assertEquals(toolType, currentTool?.toolType)
    }

    @Test
    fun testCorrectSelectionInBothOrientationsPipetteTool() {
        val toolType = ToolType.PIPETTE
        onToolBarView()
            .performSelectTool(toolType)
        setOrientation(SCREEN_ORIENTATION_LANDSCAPE)
        assertEquals(toolType, currentTool?.toolType)
    }

    @Test
    fun testCorrectSelectionInBothOrientationsShapeTool() {
        val toolType = ToolType.SHAPE
        onToolBarView()
            .performSelectTool(toolType)
        setOrientation(SCREEN_ORIENTATION_LANDSCAPE)
        assertEquals(toolType, currentTool?.toolType)
    }

    @Test
    fun testCorrectSelectionInBothOrientationsStampTool() {
        val toolType = ToolType.STAMP
        onToolBarView()
            .performSelectTool(toolType)
        setOrientation(SCREEN_ORIENTATION_LANDSCAPE)
        assertEquals(toolType, currentTool?.toolType)
    }

    @Test
    fun testCorrectSelectionInBothOrientationsTextTool() {
        val toolType = ToolType.TEXT
        onToolBarView()
            .performSelectTool(toolType)
        setOrientation(SCREEN_ORIENTATION_LANDSCAPE)
        assertEquals(toolType, currentTool?.toolType)
    }

    @Test
    fun testMoreOptionsDrawerAppearsAndAllItemsExist() {
        setOrientation(SCREEN_ORIENTATION_LANDSCAPE)
        onTopBarView()
            .performOpenMoreOptions()
        onOptionsMenu()
            .checkItemExists(R.string.menu_load_image)
            .checkItemExists(R.string.menu_hide_menu)
            .checkItemExists(R.string.help_title)
            .checkItemExists(R.string.pocketpaint_menu_about)
            .checkItemExists(R.string.menu_rate_us)
            .checkItemExists(R.string.menu_save_image)
            .checkItemExists(R.string.menu_save_copy)
            .checkItemExists(R.string.menu_new_image)
            .checkItemExists(R.string.share_image_menu)
            .checkItemDoesNotExist(R.string.menu_discard_image)
    }

    @Test
    fun testOpenColorPickerDialogInLandscape() {
        setOrientation(SCREEN_ORIENTATION_LANDSCAPE)
        onColorPickerView()
            .performOpenColorPicker()
        onView(withId(R.id.color_picker_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testOpenColorPickerDialogChooseColorInLandscape() {
        setOrientation(SCREEN_ORIENTATION_LANDSCAPE)
        onColorPickerView()
            .performOpenColorPicker()
        val colors = getColorArrayFromResource(
            activityTestRule.activity,
            R.array.pocketpaint_color_picker_preset_colors
        )
        for (i in colors.indices) {
            onColorPickerView()
                .performClickColorPickerPresetSelectorButton(i)
            if (colors[i] != Color.TRANSPARENT) {
                onView(withId(R.id.color_picker_new_color_view))
                    .perform(scrollTo())
                    .check(matches(withBackgroundColor(colors[i])))
            }
        }
    }

    @Test
    fun testOpenColorPickerDialogApplyColorInLandscape() {
        setOrientation(SCREEN_ORIENTATION_LANDSCAPE)
        val colors = getColorArrayFromResource(
            activityTestRule.activity,
            R.array.pocketpaint_color_picker_preset_colors
        )
        for (i in colors.indices) {
            onColorPickerView().performOpenColorPicker()
            onColorPickerView().performClickColorPickerPresetSelectorButton(i)
            onColorPickerView()
                .onPositiveButton()
                .perform(scrollTo())
                .perform(click())
            val selectedColor = currentTool?.drawPaint?.color
            assertEquals(colors[i], selectedColor)
        }
    }

    @Test
    fun testColorPickerCancelButtonKeepsColorInLandscape() {
        val initialColor = currentTool?.drawPaint?.color
        onColorPickerView().performOpenColorPicker()
        onColorPickerView().performClickColorPickerPresetSelectorButton(2)
        if (initialColor != null) {
            onColorPickerView().checkCurrentViewColor(initialColor)
        }
        setOrientation(SCREEN_ORIENTATION_LANDSCAPE)
        if (initialColor != null) {
            onColorPickerView().checkCurrentViewColor(initialColor)
        }
    }

    @Test
    fun testScrollToColorChooserOk() {
        setOrientation(SCREEN_ORIENTATION_LANDSCAPE)
        onColorPickerView()
            .performOpenColorPicker()
        onView(withText(R.string.color_picker_apply))
            .perform(scrollTo())
    }

    @Test
    fun testColorPickerDialogSwitchTabsInLandscape() {
        setOrientation(SCREEN_ORIENTATION_LANDSCAPE)
        onColorPickerView()
            .performOpenColorPicker()
        onView(
            withClassName(
                `is`(
                    PresetSelectorView::class.java.name
                )
            )
        )
            .check(matches(isDisplayed()))
        onView(
            allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_hsv)
            )
        )
            .perform(click())
        onView(
            withClassName(
                `is`(
                    HSVColorPickerView::class.java.name
                )
            )
        )
            .check(matches(isDisplayed()))
        onView(
            allOf(
                withId(R.id.color_picker_tab_icon),
                withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        )
            .perform(click())
        onView(
            withClassName(
                `is`(
                    RgbSelectorView::class.java.name
                )
            )
        )
            .check(matches(isDisplayed()))
        pressBack()
    }

    @Test
    fun testFullscreenPortraitOrientationChangeWithBrush() {
        onTopBarView()
            .performOpenMoreOptions()
        onView(withText(R.string.menu_hide_menu)).perform(click())
        setOrientation(SCREEN_ORIENTATION_LANDSCAPE)
        pressBack()
        onToolBarView()
            .performCloseToolOptionsView()
            .performOpenToolOptionsView()
    }

    @Test
    fun testFullscreenLandscapeOrientationChangeWithBrush() {
        setOrientation(SCREEN_ORIENTATION_LANDSCAPE)
        onTopBarView()
            .performOpenMoreOptions()
        onView(withText(R.string.menu_hide_menu)).perform(click())
        setOrientation(SCREEN_ORIENTATION_PORTRAIT)
        pressBack()
    }

    @Test
    fun testFullscreenPortraitOrientationChangeWithShape() {
        onToolBarView()
            .performSelectTool(ToolType.SHAPE)
        onTopBarView()
            .performOpenMoreOptions()
        onView(withText(R.string.menu_hide_menu)).perform(click())
        setOrientation(SCREEN_ORIENTATION_LANDSCAPE)
        pressBack()
        onToolBarView()
            .performCloseToolOptionsView()
            .performOpenToolOptionsView()
    }

    @Test
    fun testFullscreenLandscapeOrientationChangeWithShape() {
        onToolBarView()
            .performSelectTool(ToolType.SHAPE)
        setOrientation(SCREEN_ORIENTATION_LANDSCAPE)
        onTopBarView()
            .performOpenMoreOptions()
        onView(withText(R.string.menu_hide_menu)).perform(click())
        setOrientation(SCREEN_ORIENTATION_PORTRAIT)
        pressBack()
        onToolBarView()
            .performCloseToolOptionsView()
            .performOpenToolOptionsView()
    }

    @Test
    fun testIfCurrentToolIsShownInBottomNavigation() {
        setOrientation(SCREEN_ORIENTATION_LANDSCAPE)
        for (toolType in ToolType.values()) {
            val tools = toolType == ToolType.IMPORTPNG ||
                toolType == ToolType.COLORCHOOSER ||
                toolType == ToolType.REDO ||
                toolType == ToolType.UNDO ||
                toolType == ToolType.LAYER ||
                !toolType.hasOptions()
            if (tools) { continue }
            onToolBarView()
                .performSelectTool(toolType)
            onBottomNavigationView()
                .checkShowsCurrentTool(toolType)
        }
    }

    private fun setOrientation(orientation: Int) { activityTestRule.activity.requestedOrientation = orientation }

    companion object {
        @ColorInt
        private fun getColorArrayFromResource(context: Context, @ArrayRes id: Int): IntArray {
            val typedColors = context.resources.obtainTypedArray(id)
            return try {
                @ColorInt val colors = IntArray(typedColors.length())
                for (i in 0 until typedColors.length()) {
                    colors[i] = typedColors.getColor(i, Color.BLACK)
                }
                colors
            } finally {
                typedColors.recycle()
            }
        }
    }
}
