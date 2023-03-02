/*
* Paintroid: An image manipulation application for Android.
* Copyright (C) 2010-2015 The Catrobat Team
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

import android.graphics.Paint.Cap
import org.catrobat.paintroid.R
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.hamcrest.core.IsNot.not

class BrushToolOptionsViewInteraction private constructor() :
    CustomViewInteraction(onView(withId(R.id.pocketpaint_layout_tool_options))) {

    companion object {
        fun onBrushToolOptionsView(): BrushToolOptionsViewInteraction =
            BrushToolOptionsViewInteraction()
    }

    private fun getButtonIdFromShapeDrawType(strokeCapType: Cap): Int {
        return when (strokeCapType) {
            Cap.ROUND -> R.id.pocketpaint_stroke_ibtn_circle
            Cap.SQUARE -> R.id.pocketpaint_stroke_ibtn_rect
            else -> throw IllegalArgumentException("Unsupported Cap type: $strokeCapType")
        }
    }

    fun performSelectStrokeCapType(strokeCapType: Cap): BrushToolOptionsViewInteraction {
        onView(withId(getButtonIdFromShapeDrawType(strokeCapType)))
            .perform(click())
        return this
    }

    fun checkIfStrokeCapIsChecked(strokeCapType: Cap) {
        onView(withId(getButtonIdFromShapeDrawType(strokeCapType))).check(matches(isChecked()))
    }

    fun checkIfStrokeCapIsNotChecked(strokeCapType: Cap) {
        onView(withId(getButtonIdFromShapeDrawType(strokeCapType))).check(matches(not(isChecked())))
    }
}
