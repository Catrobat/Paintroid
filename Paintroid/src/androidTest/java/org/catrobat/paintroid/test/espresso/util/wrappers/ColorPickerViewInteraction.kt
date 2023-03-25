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
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.catrobat.paintroid.R
import org.catrobat.paintroid.colorpicker.ColorHistoryView
import org.catrobat.paintroid.colorpicker.PresetSelectorView
import org.catrobat.paintroid.test.espresso.util.UiMatcher.hasTablePosition
import org.catrobat.paintroid.test.espresso.util.UiMatcher.withBackgroundColor
import org.catrobat.paintroid.test.espresso.util.wrappers.BottomNavigationViewInteraction.onBottomNavigationView
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.Matchers

class ColorPickerViewInteraction private constructor() :
    CustomViewInteraction(onView(withId(R.id.color_picker_view))) {
    fun onPositiveButton(): ViewInteraction {
        return onView(withId(android.R.id.button1)) // to avoid following exception when running on emulator:
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
        return onView(withId(android.R.id.button2)) // to avoid following exception when running on emulator:
            // Caused by: java.lang.SecurityException:
            // Injecting to another application requires INJECT_EVENTS permission
            .perform(closeSoftKeyboard())
    }

    fun clickPipetteButton(): ViewInteraction {
        return onView(withId(R.id.color_picker_pipette_btn))
            .perform(click())
    }

    fun checkCurrentViewColor(color: Int) {
        onView(withId(R.id.color_picker_current_color_view))
            .check(matches(withBackgroundColor(color)))
    }

    fun checkNewColorViewColor(color: Int) {
        onView(withId(R.id.color_picker_new_color_view))
            .check(matches(withBackgroundColor(color)))
    }

    fun performCloseColorPickerWithDialogButton(): ColorPickerViewInteraction {
        check(matches(isDisplayed()))
        onPositiveButton()
            .perform(click())
        return this
    }

    fun performClickColorPickerPresetSelectorButton(buttonPosition: Int): ColorPickerViewInteraction {
        val colorButtonRowPosition = buttonPosition / COLOR_PICKER_BUTTONS_PER_ROW
        val colorButtonColPosition = buttonPosition % COLOR_PICKER_BUTTONS_PER_ROW
        onView(
            allOf(
                isDescendantOfA(
                    withClassName(
                        Matchers.containsString(
                            PresetSelectorView::class.java.simpleName
                        )
                    )
                ),
                isDescendantOfA(isAssignableFrom(TableLayout::class.java)),
                isDescendantOfA(isAssignableFrom(TableRow::class.java)),
                hasTablePosition(colorButtonRowPosition, colorButtonColPosition)
            )
        )
            .perform(closeSoftKeyboard())
            .perform(scrollTo())
            .perform(click())
        return this
    }

    fun performClickOnHistoryColor(buttonPosition: Int): ColorPickerViewInteraction {
        val colorButtonColPosition = buttonPosition % COLOR_PICKER_BUTTONS_PER_ROW
        onView(
            allOf(
                isDescendantOfA(
                    withClassName(
                        containsString(
                            ColorHistoryView::class.java.simpleName
                        )
                    )
                ),
                isDescendantOfA(isAssignableFrom(TableLayout::class.java)),
                isDescendantOfA(isAssignableFrom(TableRow::class.java)),
                hasTablePosition(0, colorButtonColPosition)
            )
        )
            .perform(closeSoftKeyboard())
            .perform(scrollTo())
            .perform(click())
        return this
    }

    fun checkHistoryColor(buttonPosition: Int, color: Int) {
        val colorButtonColPosition = buttonPosition % COLOR_PICKER_BUTTONS_PER_ROW
        onView(
            allOf(
                isDescendantOfA(
                    withClassName(
                        containsString(
                            ColorHistoryView::class.java.simpleName
                        )
                    )
                ),
                isDescendantOfA(isAssignableFrom(TableLayout::class.java)),
                isDescendantOfA(isAssignableFrom(TableRow::class.java)),
                hasTablePosition(0, colorButtonColPosition)
            )
        )
            .check(matches(withBackgroundColor(color)))
    }

    companion object {
        private const val COLOR_PICKER_BUTTONS_PER_ROW = 4
        const val MAXIMUM_COLORS_IN_HISTORY = 4
        @JvmStatic
        fun onColorPickerView(): ColorPickerViewInteraction = ColorPickerViewInteraction()
    }
}
