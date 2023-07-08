/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.paintroid.test.espresso.tools

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.UiInteractions.swipeAccurate
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RequiresApi(api = Build.VERSION_CODES.P)
@RunWith(AndroidJUnit4::class)
class BrushToolIntegrationTest {

    private lateinit var activity: MainActivity

    @get:Rule
    var activityScenarioRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)

    private fun getActivity(): MainActivity {
        lateinit var activity: MainActivity
        activityScenarioRule.scenario.onActivity {
            activity = it
        }
        return activity
    }

    @Before
    fun setUp() {
        activity = getActivity()
    }

    @Test
    fun drawOnlyOneLineWithSmoothingAlgorithm() {
        val commandManager = activity.commandManager
        val previousCommandCount = commandManager.getUndoCommandCount()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
                .perform(swipeAccurate(DrawingSurfaceLocationProvider.MIDDLE, DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT))
        val updatedCommandCount = commandManager.getUndoCommandCount()
        assertEquals(previousCommandCount + 1, updatedCommandCount)
    }
}
