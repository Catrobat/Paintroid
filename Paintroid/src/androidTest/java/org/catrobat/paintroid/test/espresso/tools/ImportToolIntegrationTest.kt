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
@file:Suppress("DEPRECATION")

package org.catrobat.paintroid.test.espresso.tools

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.rtl.util.RtlActivityTestRule
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolType
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImportToolIntegrationTest {

    @get:Rule
    var launchActivityRule: ActivityTestRule<MainActivity> = RtlActivityTestRule(MainActivity::class.java, "ar")

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    private var mainActivity: MainActivity? = null
    @Before
    fun setUp() {
        mainActivity = launchActivityRule.activity
        ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.IMPORTPNG)
    }

    @Test
    fun testImportDialogShownOnImportToolSelected() {
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_dialog_import_stickers))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_dialog_import_gallery))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testImportDialogDismissedOnCancelClicked() {
        Espresso.onView(ViewMatchers.withText(R.string.pocketpaint_cancel))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_dialog_import_stickers))
            .check(ViewAssertions.doesNotExist())
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_dialog_import_gallery))
            .check(ViewAssertions.doesNotExist())
    }

    @Test
    fun testImportDoesNotResetPerspectiveScale() {
        Espresso.onView(ViewMatchers.withText(R.string.pocketpaint_cancel))
            .perform(ViewActions.click())
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.BRUSH)
        val scale = 2.0f
        mainActivity?.perspective?.scale = scale
        mainActivity?.refreshDrawingSurface()
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.IMPORTPNG)
        Espresso.onView(ViewMatchers.withText(R.string.pocketpaint_cancel))
            .perform(ViewActions.click())
        mainActivity?.perspective?.let { Assert.assertEquals(scale, it.scale, Float.MIN_VALUE) }
    }
}
