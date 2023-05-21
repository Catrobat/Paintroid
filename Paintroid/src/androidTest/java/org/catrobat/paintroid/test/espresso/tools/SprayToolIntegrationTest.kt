/*
 * Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
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

import android.graphics.Color
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.UiMatcher.withProgress
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.ui.tools.MIN_RADIUS
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SprayToolIntegrationTest {
    private val transparentColor = Color.parseColor("#3B000000")

    @get:Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    @Before
    fun setUp() {
        ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.SPRAY)
    }

    @Test
    fun testEmptyRadius() {
        val emptyString = ""
        onView(withId(R.id.pocketpaint_radius_text))
            .perform(replaceText(emptyString))
            .check(matches(withText(emptyString)))

        onView(withId(R.id.pocketpaint_spray_radius_seek_bar))
            .check(matches(withProgress(MIN_RADIUS)))
    }

    @Test
    fun testSprayToolColor() {
        onToolBarView()
            .performSelectTool(ToolType.SPRAY)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testSprayToolTransparentColor() {
        onToolBarView()
            .performSelectTool(ToolType.SPRAY)
        onToolProperties()
            .setColor(transparentColor)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(transparentColor, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testSprayToolWithHandleMoveColor() {
        onToolBarView()
            .performSelectTool(ToolType.SPRAY)
        onToolProperties()
            .setColor(Color.BLACK)
        onDrawingSurfaceView()
            .perform(UiInteractions.swipe(DrawingSurfaceLocationProvider.MIDDLE, DrawingSurfaceLocationProvider.BOTTOM_MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testSprayToolWithHandleMoveTransparentColor() {
        onToolBarView()
            .performSelectTool(ToolType.SPRAY)
        onToolProperties()
            .setColor(transparentColor)
        onDrawingSurfaceView()
            .perform(UiInteractions.swipe(DrawingSurfaceLocationProvider.MIDDLE, DrawingSurfaceLocationProvider.BOTTOM_MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(transparentColor, BitmapLocationProvider.MIDDLE)
    }
}
