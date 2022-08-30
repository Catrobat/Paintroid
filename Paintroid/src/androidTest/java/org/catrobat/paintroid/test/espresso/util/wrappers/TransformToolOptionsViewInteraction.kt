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

import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.hamcrest.Description
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher

class TransformToolOptionsViewInteraction private constructor() :
    CustomViewInteraction(Espresso.onView(withId(R.id.pocketpaint_layout_tool_options))) {
    fun performSetCenterClick(): TransformToolOptionsViewInteraction {
        Espresso.onView(withId(R.id.pocketpaint_transform_set_center_btn))
            .perform(ViewActions.click())
        return this
    }

    fun performAutoCrop(): TransformToolOptionsViewInteraction {
        Espresso.onView(withId(R.id.pocketpaint_transform_auto_crop_btn))
            .perform(ViewActions.click())
        return this
    }

    fun checkAutoDisplayed(): TransformToolOptionsViewInteraction {
        Espresso.onView(withText(R.string.transform_auto_crop_text))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        return this
    }

    fun performRotateClockwise(): TransformToolOptionsViewInteraction {
        Espresso.onView(withId(R.id.pocketpaint_transform_rotate_right_btn))
            .perform(ViewActions.click())
        return this
    }

    fun performRotateCounterClockwise(): TransformToolOptionsViewInteraction {
        Espresso.onView(withId(R.id.pocketpaint_transform_rotate_left_btn))
            .perform(ViewActions.click())
        return this
    }

    fun performFlipVertical(): TransformToolOptionsViewInteraction {
        Espresso.onView(withId(R.id.pocketpaint_transform_flip_vertical_btn))
            .perform(ViewActions.click())
        return this
    }

    fun moveSliderTo(moveTo: Int): TransformToolOptionsViewInteraction {
        Espresso.onView(withId(R.id.pocketpaint_transform_resize_seekbar))
            .perform(UiInteractions.setProgress(moveTo))
        return this
    }

    fun performApplyResize(): TransformToolOptionsViewInteraction {
        Espresso.onView(withId(R.id.pocketpaint_transform_apply_resize_btn))
            .perform(ViewActions.click())
        return this
    }

    fun performEditResizeTextField(size: String?): TransformToolOptionsViewInteraction {
        Espresso.onView(withId(R.id.pocketpaint_transform_resize_percentage_text))
            .perform(ViewActions.replaceText(size))
        return this
    }

    fun checkPercentageTextMatches(expected: Int): TransformToolOptionsViewInteraction {
        Espresso.onView(withId(R.id.pocketpaint_transform_resize_percentage_text))
            .check(ViewAssertions.matches(withText(expected.toString())))
        return this
    }

    fun checkLayerWidthMatches(expected: Int): TransformToolOptionsViewInteraction {
        Espresso.onView(withId(R.id.pocketpaint_transform_width_value))
            .check(ViewAssertions.matches(hasValueEqualTo(expected.toString())))
        return this
    }

    private fun hasValueEqualTo(content: String): TypeSafeMatcher<View?> {
        return object : TypeSafeMatcher<View?>() {
            override fun describeTo(description: Description) {
                description.appendText("Has EditText/TextView the value:  $content")
            }

            public override fun matchesSafely(view: View?): Boolean {
                if (view !is TextView && view !is EditText) { return false }
                if (view is EditText) { return view.text.toString() == content }
                return (view as TextView).text.toString() == content
            }
        }
    }

    fun checkLayerHeightMatches(expected: Int): TransformToolOptionsViewInteraction {
        Espresso.onView(withId(R.id.pocketpaint_transform_height_value))
            .check(ViewAssertions.matches(hasValueEqualTo(expected.toString())))
        return this
    }

    fun checkIsNotDisplayed(): TransformToolOptionsViewInteraction {
        Espresso.onView(withId(R.id.pocketpaint_layout_tool_options))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
        return this
    }

    companion object {
        fun onTransformToolOptionsView(): TransformToolOptionsViewInteraction = TransformToolOptionsViewInteraction()
    }
}
