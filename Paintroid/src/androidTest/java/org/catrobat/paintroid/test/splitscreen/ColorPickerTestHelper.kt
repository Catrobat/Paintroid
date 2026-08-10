package org.catrobat.paintroid.test.splitscreen

import android.content.res.Resources
import android.graphics.Color
import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.colorpicker.HSVColorPickerView
import org.catrobat.paintroid.colorpicker.PresetSelectorView
import org.catrobat.paintroid.colorpicker.RgbSelectorView
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.UiMatcher
import org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction
import org.hamcrest.Matchers

object ColorPickerTestHelper {
    private val TAB_VIEW_PRESET_SELECTOR_CLASS = PresetSelectorView::class.java.simpleName
    private val TAB_VIEW_HSV_SELECTOR_CLASS = HSVColorPickerView::class.java.simpleName
    private val TAB_VIEW_RGBA_SELECTOR_CLASS = RgbSelectorView::class.java.simpleName

    private lateinit var activity: MainActivity
    private lateinit var resources: Resources

    fun setupEnvironment(mainActivity: MainActivity) {
        activity = mainActivity
        resources = mainActivity.resources
    }

    fun testStandardTabSelected() {
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    PresetSelectorView::class.java.simpleName
                )
            )
        ).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed())
        )
    }

    fun testTabsAreSelectable() {
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_PRESET_SELECTOR_CLASS
                )
            )
        ).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed())
        )
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_hsv)
            )
        ).perform(
            ViewActions.click()
        )
        Espresso.onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_HSV_SELECTOR_CLASS
                )
            )
        ).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed())
        )
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(
            ViewActions.click()
        )
        Espresso.onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        ).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed())
        )
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_preset)
            )
        ).perform(
            ViewActions.scrollTo(), ViewActions.click()
        )
        Espresso.onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_PRESET_SELECTOR_CLASS
                )
            )
        ).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed())
        )
    }

    fun testColorSelectionChangesNewColorViewColor() {
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        val presetColors =
            resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors)
        for (counterColors in 0 until presetColors.length()) {
            ColorPickerViewInteraction.onColorPickerView()
                .performClickColorPickerPresetSelectorButton(counterColors)
            val arrayColor = presetColors.getColor(counterColors, Color.BLACK)
            Espresso.onView(
                Matchers.allOf(
                    ViewMatchers.withId(R.id.color_picker_new_color_view), Matchers.instanceOf(
                        View::class.java
                    )
                )
            )
                .check(ViewAssertions.matches(UiMatcher.withBackgroundColor(arrayColor)))
        }
        presetColors.recycle()
    }

    fun testColorHistoryShowsRGBSelectorColors() {
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.color_picker_tab_icon),
                UiMatcher.withBackground(R.drawable.ic_color_picker_tab_rgba)
            )
        ).perform(
            ViewActions.click()
        )
        Espresso.onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        ).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed())
        )
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
        ).perform(
            ViewActions.click()
        )
        Espresso.onView(
            ViewMatchers.withClassName(
                Matchers.containsString(
                    TAB_VIEW_RGBA_SELECTOR_CLASS
                )
            )
        ).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed())
        )
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
}
