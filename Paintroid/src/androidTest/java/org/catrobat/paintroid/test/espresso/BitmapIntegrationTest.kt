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

@file:Suppress("DEPRECATION")

package org.catrobat.paintroid.test.espresso

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolType
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BitmapIntegrationTest {
    @get:Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    @Before
    fun setUp() { onToolBarView().performSelectTool(ToolType.BRUSH) }

    @Test
    fun drawingSurfaceBitmapIsDisplaySize() {
        val activity = launchActivityRule.activity
        val workspace = activity.workspace
        val bitmapWidth = workspace.width
        val bitmapHeight = workspace.height
        val metrics = activity.resources.displayMetrics
        Assert.assertEquals(metrics.widthPixels.toLong(), bitmapWidth.toLong())
        Assert.assertEquals(metrics.heightPixels.toLong(), bitmapHeight.toLong())
    }
}
