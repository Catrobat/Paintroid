/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2021 The Catrobat Team
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

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.catrobat.paintroid.R

class StampToolViewInteraction(viewInteraction: ViewInteraction) :
    CustomViewInteraction(viewInteraction) {

    companion object {
        fun onStampToolViewInteraction(): StampToolViewInteraction =
            StampToolViewInteraction(onView(withId(R.id.pocketpaint_layout_tool_specific_options)))
    }

    fun performCopy(): StampToolViewInteraction {
        onView(withId(R.id.action_copy))
            .perform(click())
        return this
    }

    fun performCut(): StampToolViewInteraction {
        onView(withId(R.id.action_cut))
            .perform(click())
        return this
    }

    fun performPaste(): StampToolViewInteraction {
        onView(withId(R.id.action_paste))
            .perform(click())
        return this
    }
}
