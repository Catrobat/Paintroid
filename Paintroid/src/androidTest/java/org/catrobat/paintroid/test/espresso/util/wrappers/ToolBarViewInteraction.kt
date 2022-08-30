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
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.EspressoUtils.mainActivity
import org.catrobat.paintroid.test.espresso.util.wrappers.BottomNavigationViewInteraction.Companion.onBottomNavigationView
import org.catrobat.paintroid.tools.ToolType
import org.hamcrest.Matchers

class ToolBarViewInteraction private constructor() :
    CustomViewInteraction(Espresso.onView(withId(R.id.pocketpaint_toolbar))) {
    private fun onSelectedToolButton(): ViewInteraction =
        Espresso.onView(currentToolType?.let { withId(it.toolButtonID) })

    private fun onToolOptionsView(): ViewInteraction =
        Espresso.onView(withId(R.id.pocketpaint_layout_tool_specific_options))

    fun performClickSelectedToolButton(): ToolBarViewInteraction {
        onBottomNavigationView()
            .onToolsClicked()
        onSelectedToolButton()
            .perform(ViewActions.click())
        return this
    }

    fun onToolsClicked(): ToolBarViewInteraction {
        onBottomNavigationView()
            .onToolsClicked()
        return this
    }

    fun performSelectTool(toolType: ToolType): ToolBarViewInteraction {
        if (currentToolType !== toolType) {
            onBottomNavigationView()
                .onToolsClicked()
            Espresso.onView(withId(toolType.toolButtonID))
                .perform(ViewActions.click())
        }
        return this
    }

    fun performOpenToolOptionsView(): ToolBarViewInteraction {
        onToolOptionsView()
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
        onBottomNavigationView()
            .onCurrentClicked()
        return this
    }

    fun performCloseToolOptionsView(): ToolBarViewInteraction {
        onToolOptionsView()
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onBottomNavigationView()
            .onCurrentClicked()
        return this
    }
    private val currentToolType: ToolType?
        get() = mainActivity.toolReference as ToolType?

    companion object {
        fun onToolBarView(): ToolBarViewInteraction = ToolBarViewInteraction()
    }
}
