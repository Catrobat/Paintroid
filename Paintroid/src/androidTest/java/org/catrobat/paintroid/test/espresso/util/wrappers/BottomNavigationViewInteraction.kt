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

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.UiMatcher
import org.catrobat.paintroid.test.espresso.util.UiMatcher.withDrawable
import org.catrobat.paintroid.tools.ToolType
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matchers

class BottomNavigationViewInteraction private constructor() :
    CustomViewInteraction(onView(withId(R.id.pocketpaint_bottom_navigation))) {
    fun onToolsClicked(): ViewInteraction {
        return onView(
            allOf(
                withId(R.id.icon),
                isDescendantOfA(withId(R.id.action_tools))
            )
        )
            .perform(click())
    }

    fun onCurrentClicked(): ViewInteraction {
        return onView(
            allOf(
                withId(R.id.icon),
                isDescendantOfA(withId(R.id.action_current_tool))
            )
        )
            .perform(click())
    }

    fun checkShowsCurrentTool(toolType: ToolType): ViewInteraction {
        onView(
            allOf(
                withId(R.id.icon),
                isDescendantOfA(withId(R.id.action_current_tool))
            )
        )
            .check(matches(withDrawable(toolType.drawableResource)))
        return onView(withId(R.id.action_current_tool))
            .check(matches(hasDescendant(withText(toolType.nameResource))))
    }

    fun onColorClicked(): ViewInteraction {
        return onView(
            Matchers.allOf(
                withId(R.id.icon),
                isDescendantOfA(withId(R.id.action_color_picker))
            )
        )
            .perform(click())
    }

    fun onLayersClicked(): ViewInteraction {
        return onView(
            Matchers.allOf(
                withId(R.id.icon),
                isDescendantOfA(withId(R.id.action_layers))
            )
        )
            .perform(click())
    }

    companion object {
        @JvmStatic
		fun onBottomNavigationView(): BottomNavigationViewInteraction {
            return BottomNavigationViewInteraction()
        }
    }
}