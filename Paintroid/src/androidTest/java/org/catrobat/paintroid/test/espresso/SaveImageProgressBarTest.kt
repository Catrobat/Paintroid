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
package org.catrobat.paintroid.test.espresso

import android.os.SystemClock
import androidx.core.widget.ContentLoadingProgressBar
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SaveImageProgressBarTest {

    @get:Rule
    val launchActivityRule = ActivityTestRule(MainActivity::class.java)

    private lateinit var activity: MainActivity

    companion object {
        private const val IMAGE_NAME = "fileName"
    }
    @Before
    fun setUp() {
        activity = launchActivityRule.activity
    }

    @Test
    fun testProgressBarShown() {
        val progressBar = activity.findViewById<ContentLoadingProgressBar>(R.id.pocketpaint_content_loading_progress_bar)
        progressBar.show()
        SystemClock.sleep(501)
        onView(withId(R.id.pocketpaint_content_loading_progress_bar))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        progressBar.hide()
    }

    @Test
    fun testProgressBarNotShown() {
        val progressBar = activity.findViewById<ContentLoadingProgressBar>(R.id.pocketpaint_content_loading_progress_bar)
        progressBar.show()
        onView(withId(R.id.pocketpaint_content_loading_progress_bar))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
        progressBar.hide()
    }

    @Test
    fun testProgressBarNotShownWhenSaving() {
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        onView(withText(R.string.menu_save_image))
            .perform(ViewActions.click())
        onView(withId(R.id.pocketpaint_image_name_save_text))
            .perform(replaceText(IMAGE_NAME))
        onView(withText(R.string.save_button_text))
            .perform(ViewActions.click())
        onView(withId(R.id.pocketpaint_content_loading_progress_bar))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
    }
}
