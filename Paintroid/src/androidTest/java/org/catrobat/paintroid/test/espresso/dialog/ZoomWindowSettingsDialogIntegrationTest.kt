/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.paintroid.test.espresso.dialog

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.dialog.ZoomWindowSettingsDialog
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ZoomWindowSettingsDialogIntegrationTest {
    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun dialogCanBeRestoredAfterActivityRecreation() {
        activityScenarioRule.scenario.onActivity { activity ->
            ZoomWindowSettingsDialog().showNow(
                activity.supportFragmentManager,
                ZOOM_WINDOW_SETTINGS_DIALOG_TAG
            )
        }

        activityScenarioRule.scenario.recreate()

        activityScenarioRule.scenario.onActivity { activity ->
            assertNotNull(
                activity.supportFragmentManager.findFragmentByTag(
                    ZOOM_WINDOW_SETTINGS_DIALOG_TAG
                )
            )
        }
    }

    companion object {
        private const val ZOOM_WINDOW_SETTINGS_DIALOG_TAG = "ZOOM_WINDOW_SETTINGS_DIALOG_TEST"
    }
}
