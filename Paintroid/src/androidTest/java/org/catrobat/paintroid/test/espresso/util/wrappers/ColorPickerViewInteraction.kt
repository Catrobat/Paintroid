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
package org.catrobat.paintroid.test.espresso.util.wrappers

import android.widget.TableLayout
import android.widget.TableRow
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.catrobat.paintroid.R
import org.catrobat.paintroid.colorpicker.ColorHistoryView
import org.catrobat.paintroid.colorpicker.PresetSelectorView
import org.catrobat.paintroid.test.espresso.util.UiMatcher
import org.hamcrest.Matchers
import org.catrobat.paintroid.test.espresso.util.wrappers.BottomNavigationViewInteraction.Companion.onBottomNavigationView

class ColorPickerViewInteraction private constructor() :
    CustomViewInteraction(Espresso.onView(withId(R.id.color_picker_view))) {
    fun onPositiveButton(): ViewInteraction {
        return Espresso.onView(withId(android.R.id.button1)) // to avoid following exception when running on emulator:
            // Caused by: java.lang.SecurityException:
            // Injecting to another application requires INJECT_EVENTS permission
            .perform(ViewActions.closeSoftKeyboard())
    }

    fun performOpenColorPicker(): ColorPickerViewInteraction {
        onBottomNavigationView()
            .onColorClicked()
        return this
    }

    fun onNegativeButton(): ViewInteraction {
        return Espresso.onView(withId(android.R.id.button2)) // to avoid following exception when running on emulator:
            // Caused by: java.lang.SecurityException:
            // Injecting to another application requires INJECT_EVENTS permission
            .perform(ViewActions.closeSoftKeyboard())
    }

    fun clickPipetteButton(): ViewInteraction {
        return Espresso.onView(withId(R.id.color_picker_pipette_btn))
            .perform(ViewActions.click())
    }

    fun checkCurrentViewColor(color: Int) {
        Espresso.onView(withId(R.id.color_picker_current_color_view))
            .check(ViewAssertions.matches(UiMatcher.withBackgroundColor(color)))
    }

    fun checkNewColorViewColor(color: Int) {
        Espresso.onView(withId(R.id.color_picker_new_color_view))
            .check(ViewAssertions.matches(UiMatcher.withBackgroundColor(color)))
    }

    fun performCloseColorPickerWithDialogButton(): ColorPickerViewInteraction {
        check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onPositiveButton()
            .perform(ViewActions.click())
        return this
    }

    fun performClickColorPickerPresetSelectorButton(buttonPosition: Int): ColorPickerViewInteraction {
        val colorButtonRowPosition = buttonPosition / COLOR_PICKER_BUTTONS_PER_ROW
        val colorButtonColPosition = buttonPosition % COLOR_PICKER_BUTTONS_PER_ROW
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.isDescendantOfA(
                    ViewMatchers.withClassName(
                        Matchers.containsString(
                            PresetSelectorView::class.java.simpleName
                        )
                    )
                ),
                ViewMatchers.isDescendantOfA(
                    ViewMatchers.isAssignableFrom(
                        TableLayout::class.java
                    )
                ),
                ViewMatchers.isDescendantOfA(
                    ViewMatchers.isAssignableFrom(
                        TableRow::class.java
                    )
                ),
                UiMatcher.hasTablePosition(colorButtonRowPosition, colorButtonColPosition)
            )
        )
            .perform(ViewActions.closeSoftKeyboard())
            .perform(ViewActions.scrollTo())
            .perform(ViewActions.click())
        return this
    }

    fun performClickOnHistoryColor(buttonPosition: Int): ColorPickerViewInteraction {
        val colorButtonColPosition = buttonPosition % COLOR_PICKER_BUTTONS_PER_ROW
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.isDescendantOfA(
                    ViewMatchers.withClassName(
                        Matchers.containsString(
                            ColorHistoryView::class.java.simpleName
                        )
                    )
                ),
                ViewMatchers.isDescendantOfA(
                    ViewMatchers.isAssignableFrom(
                        TableLayout::class.java
                    )
                ),
                ViewMatchers.isDescendantOfA(
                    ViewMatchers.isAssignableFrom(
                        TableRow::class.java
                    )
                ),
                UiMatcher.hasTablePosition(0, colorButtonColPosition)
            )
        )
            .perform(ViewActions.closeSoftKeyboard())
            .perform(ViewActions.scrollTo())
            .perform(ViewActions.click())
        return this
    }

    fun checkHistoryColor(buttonPosition: Int, color: Int) {
        val colorButtonColPosition = buttonPosition % COLOR_PICKER_BUTTONS_PER_ROW
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.isDescendantOfA(
                    ViewMatchers.withClassName(
                        Matchers.containsString(
                            ColorHistoryView::class.java.simpleName
                        )
                    )
                ),
                ViewMatchers.isDescendantOfA(
                        ViewMatchers.isAssignableFrom(
                        TableLayout::class.java
                    )
                ),
                ViewMatchers.isDescendantOfA(
                    ViewMatchers.isAssignableFrom(
                        TableRow::class.java
                    )
                ),
                UiMatcher.hasTablePosition(0, colorButtonColPosition)
            )
        )
            .check(ViewAssertions.matches(UiMatcher.withBackgroundColor(color)))
    }

    companion object {
        private const val COLOR_PICKER_BUTTONS_PER_ROW = 4
        const val MAXIMUM_COLORS_IN_HISTORY = 4
        fun onColorPickerView(): ColorPickerViewInteraction = ColorPickerViewInteraction()
    }
}
