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
import org.catrobat.paintroid.R
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class TransformToolOptionsViewInteraction private constructor() :
    CustomViewInteraction(Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_layout_tool_options))) {

    fun performAutoCrop(): TransformToolOptionsViewInteraction {
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_transform_auto_crop_btn))
            .perform(ViewActions.click())
        return this
    }

    fun performRotateClockwise(): TransformToolOptionsViewInteraction {
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_transform_rotate_right_btn))
            .perform(ViewActions.click())
        return this
    }

    fun performFlipVertical(): TransformToolOptionsViewInteraction {
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_transform_flip_vertical_btn))
            .perform(ViewActions.click())
        return this
    }

    fun performFlipHorizontal(): TransformToolOptionsViewInteraction {
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_transform_flip_horizontal_btn))
            .perform(ViewActions.click())
        return this
    }

    fun checkLayerWidthMatches(expected: Int): TransformToolOptionsViewInteraction {
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_transform_width_value))
            .check(ViewAssertions.matches(hasValueEqualTo(expected.toString())))
        return this
    }

    private fun hasValueEqualTo(content: String): TypeSafeMatcher<View?> {
        return object : TypeSafeMatcher<View?>() {
            override fun describeTo(description: Description) { description.appendText("Has EditText/TextView the value:  $content") }

            public override fun matchesSafely(view: View?): Boolean {
                if (view !is TextView && view !is EditText) { return false }
                if (view is EditText) { return view.text.toString() == content }
                return (view as TextView).text.toString() == content
            }
        }
    }

    fun checkLayerHeightMatches(expected: Int): TransformToolOptionsViewInteraction {
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_transform_height_value))
            .check(ViewAssertions.matches(hasValueEqualTo(expected.toString())))
        return this
    }

    companion object {
        @JvmStatic
		fun onTransformToolOptionsView(): TransformToolOptionsViewInteraction { return TransformToolOptionsViewInteraction() }
    }
}
