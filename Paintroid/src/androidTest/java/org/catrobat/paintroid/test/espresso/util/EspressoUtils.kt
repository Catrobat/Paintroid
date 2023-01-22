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
package org.catrobat.paintroid.test.espresso.util

import android.Manifest
import android.R
import android.app.Activity
import android.content.res.Configuration
import android.graphics.PointF
import android.os.Build
import android.util.TypedValue
import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import org.catrobat.paintroid.MainActivity
import org.hamcrest.Matcher
import org.junit.Assert

object EspressoUtils {
    const val DEFAULT_STROKE_WIDTH = 25
    val mainActivity: MainActivity
        get() {
            val resumedActivities: MutableList<Activity> = ArrayList()
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                val activitiesInStage = ActivityLifecycleMonitorRegistry.getInstance()
                    .getActivitiesInStage(Stage.RESUMED)
                resumedActivities.addAll(activitiesInStage)
            }
            return resumedActivities[0] as MainActivity
        }

    @get:Deprecated("Do not use this in new tests")
    val actionbarHeight: Float
        get() {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val tv = TypedValue()
            context.theme.resolveAttribute(R.attr.actionBarSize, tv, true)
            val resources = context.resources
            val metrics = resources.displayMetrics
            return TypedValue.complexToDimensionPixelSize(tv.data, metrics).toFloat()
        }

    @get:Deprecated("Do not use this in new tests")
    val statusbarHeight: Float
        get() {
            val resources = InstrumentationRegistry.getInstrumentation().targetContext.resources
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            Assert.assertNotEquals(0, resourceId.toLong())
            return resources.getDimensionPixelSize(resourceId).toFloat()
        }

    @Deprecated("Do not use this in new tests")
    fun getSurfacePointFromScreenPoint(screenPoint: PointF) {
        PointF(screenPoint.x, screenPoint.y - actionbarHeight - statusbarHeight)
    }

    @Deprecated("Do not use this in new tests")
    fun getScreenPointFromSurfaceCoordinates(pointX: Float, pointY: Float) {
        PointF(pointX, pointY + statusbarHeight + actionbarHeight)
    }

    @SuppressWarnings("SwallowedException")
    fun waitForToast(viewMatcher: Matcher<View?>?, duration: Int) {
        val waitTime = System.currentTimeMillis() + duration
        val viewInteraction = Espresso.onView(viewMatcher).inRoot(UiMatcher.isToast())
        while (System.currentTimeMillis() < waitTime) {
            try {
                viewInteraction.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                return
            } catch (e: NoMatchingViewException) {
                Espresso.onView(ViewMatchers.isRoot()).perform(UiInteractions.waitFor(250))
            }
        }
        viewInteraction.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    val configuration: Configuration
        get() = InstrumentationRegistry.getInstrumentation().targetContext.resources.configuration

    fun grantPermissionRulesVersionCheck(): GrantPermissionRule {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            GrantPermissionRule.grant(Manifest.permission.READ_MEDIA_IMAGES)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            GrantPermissionRule.grant(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }
}
