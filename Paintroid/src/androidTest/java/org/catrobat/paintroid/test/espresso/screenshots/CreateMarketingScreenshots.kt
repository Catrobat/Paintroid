/*
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2021 The Catrobat Team
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
package org.catrobat.paintroid.test.espresso.screenshots

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.UiMatcher
import org.catrobat.paintroid.test.espresso.util.wrappers.BottomNavigationViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.LayerMenuViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ShapeToolOptionsViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.drawable.DrawableShape
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.core.AllOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import tools.fastlane.screengrab.Screengrab
import tools.fastlane.screengrab.locale.LocaleTestRule

@RunWith(AndroidJUnit4::class)
class CreateMarketingScreenshots {

    @Rule
    @JvmField
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @Rule
    @JvmField
    val localeTestRule = LocaleTestRule()

    @Test
    fun createScreenshots() {

        ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.FILL)
        ToolPropertiesInteraction.onToolProperties()
            .setColorResource(R.color.pocketpaint_color_picker_blue1)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))

        screenshotTools()

        screenshotHeart()

        screenshotColorPicker()

        screenshotLayers()

        screenshotBrush()

        screenshotSaveMenu()

        screenshotAdvancedSettings()
    }

    private fun screenshotAdvancedSettings() {
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        Screengrab.screenshot("5_OverflowMenu")
        onView(withText(R.string.menu_advanced))
            .perform(click())
        Screengrab.screenshot("7_AdvancedMenu")
        onView(withText(R.string.cancel_button_text))
            .perform(click())
    }

    private fun screenshotSaveMenu() {
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        onView(withText(R.string.menu_save_image))
            .perform(click())
        onView(withId(R.id.pocketpaint_save_dialog_spinner))
            .perform(click())
        Screengrab.screenshot("6_SaveMenu")
        Espresso.onData(
            AllOf.allOf(
                Matchers.`is`(Matchers.instanceOf<Any>(String::class.java)),
                Matchers.`is`<String>("png")
            )
        ).inRoot(RootMatchers.isPlatformPopup()).perform(click())
        onView(withText(R.string.cancel_button_text))
            .perform(click())
    }

    private fun screenshotBrush() {
        Espresso.pressBack()
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.BRUSH)
        Screengrab.screenshot("0_Brush")
    }

    private fun screenshotLayers() {
        Espresso.pressBack()
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()

        Screengrab.screenshot("4_Layers")
    }

    private fun screenshotTools() {
        ToolBarViewInteraction.onToolBarView().onToolsClicked()
        Screengrab.screenshot("1_Tools")
    }

    private fun screenshotHeart() {
        Espresso.pressBack()
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .performSelectLayer(0)

        BottomNavigationViewInteraction.onBottomNavigationView()
            .onCurrentClicked()
        ToolPropertiesInteraction.onToolProperties()
            .setColorResource(R.color.pocketpaint_color_picker_red1)
        ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.SHAPE)
        ShapeToolOptionsViewInteraction.onShapeToolOptionsView()
            .performSelectShape(DrawableShape.HEART)
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        Screengrab.screenshot("3_Shapes")
    }

    private fun screenshotColorPicker() {
        ToolPropertiesInteraction.onToolProperties()
            .setColorResource(R.color.pocketpaint_color_picker_black)
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        onView(
            allOf(
                withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(click())
        onView(withId(R.id.color_picker_color_rgb_seekbar_blue)).perform(UiInteractions.touchCenterRight())
        closeSoftKeyboard()
        Screengrab.screenshot("2_ColorPicker")
    }
}
