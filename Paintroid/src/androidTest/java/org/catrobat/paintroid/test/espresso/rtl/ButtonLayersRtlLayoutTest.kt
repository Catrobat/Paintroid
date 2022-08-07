/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

@file:Suppress("DEPRECATION")

package org.catrobat.paintroid.test.espresso.rtl

import org.junit.runner.RunWith
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.test.espresso.rtl.util.RtlActivityTestRule
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.test.espresso.util.wrappers.BottomNavigationViewInteraction
import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers
import org.catrobat.paintroid.R
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.action.ViewActions
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class ButtonLayersRtlLayoutTest {
    @Rule
    var mainActivityActivityTestRule: ActivityTestRule<MainActivity> = RtlActivityTestRule(MainActivity::class.java, "ar")

    @Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    @Test
    fun testButtonLayers() {
        BottomNavigationViewInteraction.onBottomNavigationView().onLayersClicked()
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_nav_view_layer))
            .check(ViewAssertions.matches(ViewMatchers.isCompletelyDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_layer_side_nav_button_add))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_layer_side_nav_button_delete))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_stroke_ibtn_circle))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_stroke_ibtn_rect))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))
            .perform(ViewActions.click())
    }
}
