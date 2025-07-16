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
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.EspressoUtils.assertOnView
import org.catrobat.paintroid.tools.ToolType
import org.hamcrest.Matchers.allOf
import org.catrobat.paintroid.test.espresso.util.UiMatcher
import org.catrobat.paintroid.test.espresso.util.ViewRobot

class BottomNavigationViewInteraction private constructor() :
    CustomViewInteraction(Espresso.onView(withId(R.id.pocketpaint_bottom_navigation))) {
    fun onToolsClicked(): ViewInteraction {
        return Espresso.onView(
            allOf(
                withId(R.id.icon),
                ViewMatchers.isDescendantOfA(withId(R.id.action_tools))
            )
        )
            .perform(ViewActions.click())
    }

    fun onCurrentClicked(): ViewInteraction {
        return Espresso.onView(
            allOf(
                withId(R.id.icon),
                ViewMatchers.isDescendantOfA(withId(R.id.action_current_tool))
            )
        )
            .perform(ViewActions.click())
    }

    fun checkShowsCurrentTool(toolType: ToolType) {
        val viewRobot = ViewRobot()
        var matcher = allOf(withId(R.id.icon),
                ViewMatchers.isDescendantOfA(withId(R.id.action_current_tool)),
                UiMatcher.withDrawable(toolType.drawableResource)
        )
        var assertion = ViewAssertions.matches(isDisplayed())
        viewRobot.assertOnView(matcher, assertion)

        matcher = allOf(withId(R.id.action_current_tool))
        assertion = ViewAssertions.matches(allOf(ViewMatchers.hasDescendant(withText(toolType.nameResource)), isDisplayed()))
        assertOnView(matcher, assertion)
    }

    /*fun checkShowsCurrentTool(toolType: ToolType): ViewInteraction {
        Espresso.onView(
            allOf(
                withId(R.id.icon),
                ViewMatchers.isDescendantOfA(withId(R.id.action_current_tool))
            )
        )
            .check(ViewAssertions.matches(UiMatcher.withDrawable(toolType.drawableResource)))
        return Espresso.onView(withId(R.id.action_current_tool))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(toolType.nameResource))))
    }*/

    fun onColorClicked(): ViewInteraction {
        return Espresso.onView(
            allOf(
                withId(R.id.icon),
                ViewMatchers.isDescendantOfA(withId(R.id.action_color_picker))
            )
        )
            .perform(ViewActions.click())
    }

    fun onLayersClicked(): ViewInteraction {
        return Espresso.onView(
            allOf(
                withId(R.id.icon),
                ViewMatchers.isDescendantOfA(withId(R.id.action_layers))
            )
        )
            .perform(ViewActions.click())
    }

    companion object {
        @JvmStatic
        fun onBottomNavigationView() = BottomNavigationViewInteraction()
    }
}
