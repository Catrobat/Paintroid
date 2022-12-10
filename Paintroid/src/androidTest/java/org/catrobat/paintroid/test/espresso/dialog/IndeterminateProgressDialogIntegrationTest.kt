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
package org.catrobat.paintroid.test.espresso.dialog

import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.graphics.PointF
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IndeterminateProgressDialogIntegrationTest {
    @get:Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    private lateinit var dialog: DialogFragment

    @Before
    fun setUp() {
        dialog = IndeterminateProgressDialog()
        dialog.show(activityTestRule.activity.supportFragmentManager, "PROGRESS_TAG_TEST")
    }

    @After
    fun tearDown() {
        dialog.dismiss()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Test
    fun testDialogIsShown() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Espresso.onView(withId(R.id.pocketpaint_progress_bar))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Test
    fun testDialogIsNotCancelableOnBack() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Espresso.pressBack()
            Espresso.onView(withId(R.id.pocketpaint_progress_bar))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Test
    fun testDialogIsNotCancelable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val metrics = Resources.getSystem().displayMetrics
            val point = PointF(-metrics.widthPixels / 4f, -metrics.heightPixels / 4f)
            Espresso.onView(withId(R.id.pocketpaint_progress_bar))
                .perform(UiInteractions.touchAt(point))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Test
    fun testDialogIsRotateAble() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            activityTestRule.activity.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            activityTestRule.activity.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
}
