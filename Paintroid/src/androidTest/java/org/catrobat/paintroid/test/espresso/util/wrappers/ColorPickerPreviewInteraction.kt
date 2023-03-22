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
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.UiMatcher
import org.catrobat.paintroid.test.espresso.util.UiMatcher.withBackgroundColor

class ColorPickerPreviewInteraction private constructor() :
    CustomViewInteraction(onView(ViewMatchers.withId(R.id.previewSurface))) {
    fun onPositiveButton(): ViewInteraction {
        return onView(withId(android.R.id.button1)) // to avoid following exception when running on emulator:
            // Caused by: java.lang.SecurityException:
            // Injecting to another application requires INJECT_EVENTS permission
            .perform(closeSoftKeyboard())
    }

    fun onNegativeButton(): ViewInteraction {
        return onView(withId(android.R.id.button2)) // to avoid following exception when running on emulator:
            // Caused by: java.lang.SecurityException:
            // Injecting to another application requires INJECT_EVENTS permission
            .perform(closeSoftKeyboard())
    }

    fun checkColorPreviewColor(color: Int) {
        onView(withId(R.id.colorPreview))
            .check(matches(withBackgroundColor(color)))
    }

    fun performCloseColorPickerPreviewWithDoneButton(): ColorPickerPreviewInteraction {
        check(matches(isDisplayed()))
        onView(withId(R.id.doneAction))
            .perform(click())
        return this
    }

    fun performCloseColorPickerPreviewWithBackButtonDecline(): ColorPickerPreviewInteraction {
        check(matches(isDisplayed()))
        onView(withId(R.id.backAction))
            .perform(click())
        onNegativeButton()
            .perform(click())
        return this
    }

    fun performCloseColorPickerPreviewWithBackButtonAccept(): ColorPickerPreviewInteraction {
        check(matches(isDisplayed()))
        onView(withId(R.id.backAction))
            .perform(click())
        onPositiveButton()
            .perform(click())
        return this
    }

    fun assertShowColorPickerPreviewBackDialog() {
        check(matches(isDisplayed()))
        onView(withId(R.id.backAction))
            .perform(click())
        onView(withId(android.R.id.button1))
            .check(matches(isDisplayed()))
        onView(withId(android.R.id.button2))
            .check(matches(isDisplayed()))
        onView(ViewMatchers.withText(R.string.color_picker_save_dialog_title))
            .check(matches(isDisplayed()))
        onView(ViewMatchers.withText(R.string.color_picker_save_dialog_msg))
            .check(matches(isDisplayed()))
    }

    companion object {
        fun onColorPickerPreview(): ColorPickerPreviewInteraction {
            return ColorPickerPreviewInteraction()
        }
    }
}