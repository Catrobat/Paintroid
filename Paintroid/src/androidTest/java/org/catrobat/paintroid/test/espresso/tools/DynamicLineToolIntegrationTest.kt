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

package org.catrobat.paintroid.test.espresso.tools

import android.graphics.Color
import android.graphics.Paint
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.*
import org.catrobat.paintroid.test.espresso.util.wrappers.*
import org.catrobat.paintroid.test.utils.TestUtils
import org.catrobat.paintroid.tools.ToolType
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DynamicLineToolIntegrationTest {

    @get:Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)
    private var idlingResource: CountingIdlingResource? = null

    @Before
    fun setUp() {
        idlingResource = launchActivityRule.activity.idlingResource
        IdlingRegistry.getInstance().register(idlingResource)
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.DYNAMICLINE)
        TopBarViewInteraction.onTopBarView().performClickCheckmark()
    }

    @After
    fun tearDown() { IdlingRegistry.getInstance().unregister(idlingResource) }

    @Test
    fun testIfCurrentToolIsShownInBottomNavigation() {
        BottomNavigationViewInteraction.onBottomNavigationView().checkShowsCurrentTool(ToolType.DYNAMICLINE)
    }

    @Test
    fun testVerticalLineColor() {
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        DrawingSurfaceInteraction.onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE))

        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testHorizontalLineColor() {
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testDiagonalLineColor() {
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testChangeLineToolForm() {
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_stroke_ibtn_rect)).perform(ViewActions.click())
        ToolPropertiesInteraction.onToolProperties()
            .checkStrokeWidth(EspressoUtils.DEFAULT_STROKE_WIDTH.toFloat())
            .checkCap(Paint.Cap.SQUARE)
    }

    @Test
    fun testCheckmarkLineFeature() {
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        TopBarViewInteraction.onTopBarView().onCheckmarkButton()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_checkmark),
                        ViewMatchers.isEnabled()
                    )
                )
            )
        TopBarViewInteraction.onTopBarView().performClickCheckmark()
        DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testCheckmarkLineFeatureChangeColor() {
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        ToolPropertiesInteraction.onToolProperties()
            .setColor(Color.GREEN)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.GREEN, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.GREEN, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        TopBarViewInteraction.onTopBarView().onCheckmarkButton()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_checkmark),
                        ViewMatchers.isEnabled()
                    )
                )
            )
        ToolPropertiesInteraction.onToolProperties().setColor(Color.RED)
        TopBarViewInteraction.onTopBarView().performClickCheckmark()
        DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.RED, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testCheckmarkLineFeatureChangeShape() {
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        ToolPropertiesInteraction.onToolProperties().setCap(Paint.Cap.SQUARE)
        ToolPropertiesInteraction.onToolProperties().checkCap(Paint.Cap.SQUARE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        TopBarViewInteraction.onTopBarView().onCheckmarkButton()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_checkmark),
                        ViewMatchers.isEnabled()
                    )
                )
            )
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)
        TopBarViewInteraction.onTopBarView().performClickCheckmark()
        DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testCheckmarkLineFeatureChangeStrokeWidth() {
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        ToolPropertiesInteraction.onToolProperties().setStrokeWidth(80f)
        ToolPropertiesInteraction.onToolProperties().checkStrokeWidth(80f)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        TopBarViewInteraction.onTopBarView().onCheckmarkButton()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_checkmark),
                        ViewMatchers.isEnabled()
                    )
                )
            )
        TopBarViewInteraction.onTopBarView().performClickCheckmark()
        DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testConnectedLinesFeature() {
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_TOP_LEFT)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        TopBarViewInteraction.onTopBarView().performClickPlus()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE)
        TopBarViewInteraction.onTopBarView().performClickPlus()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT))
        DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_TOP_LEFT)
        TopBarViewInteraction.onTopBarView().performClickCheckmark()
    }

    @Test
    fun testConnectedLinesFeatureDrawingLine() {
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        TopBarViewInteraction.onTopBarView().performClickPlus()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE)
        TopBarViewInteraction.onTopBarView().performClickPlus()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_TOP_LEFT)
        TopBarViewInteraction.onTopBarView().performClickCheckmark()
    }

    // andere funktion jetz
//    @Test
    fun testClickingUndoOnceOnConnectedLines() {
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_TOP_LEFT)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        TopBarViewInteraction.onTopBarView().performClickPlus()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        TopBarViewInteraction.onTopBarView().performUndo()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        TopBarViewInteraction.onTopBarView().performClickCheckmark()
    }

    @Test
    fun testClickingUndoMoreThanOnceOnConnectedLines() {
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        TopBarViewInteraction.onTopBarView().performClickPlus()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT)
        TopBarViewInteraction.onTopBarView().performUndo()
        TopBarViewInteraction.onTopBarView().performUndo()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        TopBarViewInteraction.onTopBarView().performClickCheckmark()
    }

    @Test
    fun testUndoWithDrawingConnectedLines() {
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        TopBarViewInteraction.onTopBarView().performClickPlus()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT)
        TopBarViewInteraction.onTopBarView().performUndo()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT)
        TopBarViewInteraction.onTopBarView().performClickPlus()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT))
        TopBarViewInteraction.onTopBarView().performUndo()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_BOTTOM_LEFT)
        TopBarViewInteraction.onTopBarView().performClickCheckmark()
    }

    @Test
    fun testColorChangesInConnectedLineMode() {
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        TopBarViewInteraction.onTopBarView().performClickPlus()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT))
        TestUtils.selectColorInDialog(0)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(
                Color.parseColor("#FF0074CD"),
                BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT
            )
        TestUtils.selectColorInDialog(1)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(
                Color.parseColor("#FF00B4F1"),
                BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT
            )
        TopBarViewInteraction.onTopBarView().performUndo()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(
                Color.parseColor("#FF0074CD"),
                BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT
            )
        TopBarViewInteraction.onTopBarView().performRedo()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(
                Color.parseColor("#FF00B4F1"),
                BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT
            )
        TopBarViewInteraction.onTopBarView().performUndo()
        TopBarViewInteraction.onTopBarView().performUndo()
        TopBarViewInteraction.onTopBarView().performUndo()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT)
        TopBarViewInteraction.onTopBarView().performUndo()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        TopBarViewInteraction.onTopBarView().performRedo()
        TopBarViewInteraction.onTopBarView().performRedo()
        TopBarViewInteraction.onTopBarView().performRedo()
        TopBarViewInteraction.onTopBarView().performRedo()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(
                Color.parseColor("#FF0074CD"),
                BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT
            )
        TopBarViewInteraction.onTopBarView().performClickCheckmark()
    }

    @Test
    fun testColorChangesAndQuittingConnectedLineMode() {
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        TopBarViewInteraction.onTopBarView().performClickPlus()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT))
        TestUtils.selectColorInDialog(0)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(
                Color.parseColor("#FF0074CD"),
                BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT
            )
        TestUtils.selectColorInDialog(1)
        TestUtils.selectColorInDialog(2)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(
                Color.parseColor("#FF078707"),
                BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT
            )
        TopBarViewInteraction.onTopBarView().performUndo()
        TopBarViewInteraction.onTopBarView().performUndo()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(
                Color.parseColor("#FF00B4F1"),
                BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT
            )
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(
                Color.parseColor("#FF0074CD"),
                BitmapLocationProvider.HALFWAY_BOTTOM_LEFT
            )
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(
                Color.parseColor("#FF0074CD"),
                BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE
            )
        TopBarViewInteraction.onTopBarView().performClickCheckmark()
    }

}
