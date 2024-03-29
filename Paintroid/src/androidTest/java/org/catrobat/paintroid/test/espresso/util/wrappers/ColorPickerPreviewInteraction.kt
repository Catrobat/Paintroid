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

import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.UiMatcher

class ColorPickerPreviewInteraction private constructor() :
        CustomViewInteraction(Espresso.onView(withId(R.id.previewSurface))) {
    fun onPositiveButton(): ViewInteraction {
        return Espresso.onView(withId(android.R.id.button1)) // to avoid following exception when running on emulator:
                // Caused by: java.lang.SecurityException:
                // Injecting to another application requires INJECT_EVENTS permission
                .perform(ViewActions.closeSoftKeyboard())
    }

    fun onNegativeButton(): ViewInteraction {
        return Espresso.onView(withId(android.R.id.button2)) // to avoid following exception when running on emulator:
                // Caused by: java.lang.SecurityException:
                // Injecting to another application requires INJECT_EVENTS permission
                .perform(ViewActions.closeSoftKeyboard())
    }

    fun checkColorPreviewColor(color: Int) {
        Espresso.onView(withId(R.id.colorPreview))
                .check(ViewAssertions.matches(UiMatcher.withBackgroundColor(color)))
    }

    fun performCloseColorPickerPreviewWithDoneButton(): ColorPickerPreviewInteraction {
        check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.doneAction))
                .perform(ViewActions.click())
        return this
    }

    fun performCloseColorPickerPreviewWithBackButtonDecline(): ColorPickerPreviewInteraction {
        check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.backAction))
                .perform(ViewActions.click())
        onNegativeButton()
                .perform(ViewActions.click())
        return this
    }

    fun performCloseColorPickerPreviewWithBackButtonAccept(): ColorPickerPreviewInteraction {
        check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.backAction))
                .perform(ViewActions.click())
        onPositiveButton()
                .perform(ViewActions.click())
        return this
    }

    fun assertShowColorPickerPreviewBackDialog() {
        check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.backAction))
                .perform(ViewActions.click())
        Espresso.onView(withId(android.R.id.button1))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(android.R.id.button2))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(R.string.color_picker_save_dialog_title))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(R.string.color_picker_save_dialog_msg))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    companion object {
        fun onColorPickerPreview(): ColorPickerPreviewInteraction = ColorPickerPreviewInteraction()
    }
}
