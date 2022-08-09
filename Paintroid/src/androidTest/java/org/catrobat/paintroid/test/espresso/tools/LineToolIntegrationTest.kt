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
import android.graphics.Paint.Cap
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.EspressoUtils.DEFAULT_STROKE_WIDTH
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.UiMatcher
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.test.utils.TestUtils.Companion.selectColorInDialog
import org.catrobat.paintroid.tools.ToolType
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LineToolIntegrationTest {
    @get:Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    private var idlingResource: CountingIdlingResource? = null

    @Before
    fun setUp() {
        idlingResource = launchActivityRule.activity.idlingResource
        IdlingRegistry.getInstance().register(idlingResource)
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.LINE)
        onTopBarView().performClickCheckmark()
    }

    @After
    fun tearDown() { IdlingRegistry.getInstance().unregister(idlingResource) }

    @Test
    fun testVerticalLineColor() {
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        onDrawingSurfaceView()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE,
                    DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE
                )
            )
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testHorizontalLineColor() {
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        onDrawingSurfaceView()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE,
                    DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE
                )
            )
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testDiagonalLineColor() {
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        onDrawingSurfaceView()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT,
                    DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT
                )
            )
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testChangeLineToolForm() {
        onView(ViewMatchers.withId(R.id.pocketpaint_stroke_ibtn_rect)).perform(ViewActions.click())
        onToolProperties()
            .checkStrokeWidth(DEFAULT_STROKE_WIDTH.toFloat())
            .checkCap(Cap.SQUARE)
    }

    @Test
    fun testCheckmarkLineFeature() {
        onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        onTopBarView().onCheckmarkButton()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_checkmark),
                        ViewMatchers.isEnabled()
                    )
                )
            )
        onTopBarView().performClickCheckmark()
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testCheckmarkLineFeatureChangeColor() {
        onToolProperties().setColor(Color.BLACK)
        onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        onToolProperties().setColor(Color.GREEN)
        onDrawingSurfaceView().checkPixelColor(Color.GREEN, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.GREEN, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        onTopBarView().onCheckmarkButton()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_checkmark),
                        ViewMatchers.isEnabled()
                    )
                )
            )
        onToolProperties().setColor(Color.RED)
        onTopBarView().performClickCheckmark()
        onDrawingSurfaceView().checkPixelColor(Color.RED, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testCheckmarkLineFeatureChangeShape() {
        onToolProperties().setColor(Color.BLACK)
        onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        onToolProperties().setCap(Cap.SQUARE)
        onToolProperties().checkCap(Cap.SQUARE)
        onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        onTopBarView().onCheckmarkButton()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_checkmark),
                        ViewMatchers.isEnabled()
                    )
                )
            )
        onToolProperties().setColor(Color.BLACK)
        onTopBarView().performClickCheckmark()
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testCheckmarkLineFeatureChangeStrokeWidth() {
        onToolProperties().setColor(Color.BLACK)
        onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        onToolProperties().setStrokeWidth(80f)
        onToolProperties().checkStrokeWidth(80f)
        onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        onTopBarView().onCheckmarkButton()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_checkmark),
                        ViewMatchers.isEnabled()
                    )
                )
            )
        onTopBarView().performClickCheckmark()
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testConnectedLinesFeature() {
        onToolProperties().setColor(Color.BLACK)
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT))
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_TOP_LEFT)
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        onTopBarView().performClickPlus()
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE)
        onTopBarView().performClickPlus()
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT))
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_TOP_LEFT)
        onTopBarView().performClickCheckmark()
    }

    @Test
    fun testConnectedLinesFeatureDrawingLine() {
        onToolProperties().setColor(Color.BLACK)
        onDrawingSurfaceView()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT,
                    DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE
                )
            )
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        onTopBarView().performClickPlus()
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE)
        onTopBarView().performClickPlus()
        onDrawingSurfaceView()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE,
                    DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT
                )
            )
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_TOP_LEFT)
        onTopBarView().performClickCheckmark()
    }

    @Test
    fun testConnectedLinesFeatureRedrawingLine() {
        onToolProperties().setColor(Color.BLACK)
        onDrawingSurfaceView()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT,
                    DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE
                )
            )
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        onDrawingSurfaceView()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT,
                    DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT
                )
            )
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT)
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        onTopBarView().performClickPlus()
        onDrawingSurfaceView()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE,
                    DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT
                )
            )
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_TOP_LEFT)
        onTopBarView().performClickCheckmark()
    }

    @Test
    fun testClickingUndoOnceOnConnectedLines() {
        onToolProperties().setColor(Color.BLACK)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT))
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_TOP_LEFT)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        onTopBarView().performClickPlus()
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        onTopBarView().performUndo()
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        onTopBarView().performClickCheckmark()
    }

    @Test
    fun testClickingUndoMoreThanOnceOnConnectedLines() {
        onToolProperties().setColor(Color.BLACK)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        onTopBarView().performClickPlus()
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT))
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT)
        onTopBarView().performUndo()
        onTopBarView().performUndo()
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        onTopBarView().performClickCheckmark()
    }

    @Test
    fun testUndoWithDrawingConnectedLines() {
        onToolProperties().setColor(Color.BLACK)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE))
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        onTopBarView().performClickPlus()
        onDrawingSurfaceView()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT,
                    DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT
                )
            )
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT)
        onTopBarView().performUndo()
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT)
        onDrawingSurfaceView()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT,
                    DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT
                )
            )
        onDrawingSurfaceView()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT,
                    DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT
                )
            )
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT)
        onTopBarView().performClickPlus()
        onDrawingSurfaceView()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT,
                    DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE
                )
            )
        onDrawingSurfaceView()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT,
                    DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT
                )
            )
        onTopBarView().performUndo()
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT)
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_BOTTOM_LEFT)
        onTopBarView().performClickCheckmark()
    }

    @Test
    fun testColorChangesInConnectedLineMode() {
        onToolProperties().setColor(Color.BLACK)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE))
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        onTopBarView().performClickPlus()
        onDrawingSurfaceView()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT,
                    DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT
                )
            )
        selectColorInDialog(0)
        onDrawingSurfaceView()
            .checkPixelColor(
                Color.parseColor("#FF0074CD"),
                BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT
            )
        selectColorInDialog(1)
        onDrawingSurfaceView()
            .checkPixelColor(
                Color.parseColor("#FF00B4F1"),
                BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT
            )
        onTopBarView().performUndo()
        onDrawingSurfaceView()
            .checkPixelColor(
                Color.parseColor("#FF0074CD"),
                BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT
            )
        onTopBarView().performRedo()
        onDrawingSurfaceView()
            .checkPixelColor(
                Color.parseColor("#FF00B4F1"),
                BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT
            )
        onTopBarView().performUndo()
        onTopBarView().performUndo()
        onTopBarView().performUndo()
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT)
        onTopBarView().performUndo()
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        onTopBarView().performRedo()
        onTopBarView().performRedo()
        onTopBarView().performRedo()
        onTopBarView().performRedo()
        onDrawingSurfaceView()
            .checkPixelColor(
                Color.parseColor("#FF0074CD"),
                BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT
            )
        onTopBarView().performClickCheckmark()
    }

    @Test
    fun testColorChangesAndQuittingConnectedLineMode() {
        onToolProperties().setColor(Color.BLACK)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE))
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        onTopBarView().performClickPlus()
        onDrawingSurfaceView()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT,
                    DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT
                )
            )
        selectColorInDialog(0)
        onDrawingSurfaceView()
            .checkPixelColor(
                Color.parseColor("#FF0074CD"),
                BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT
            )
        selectColorInDialog(1)
        selectColorInDialog(2)
        onDrawingSurfaceView()
            .checkPixelColor(
                Color.parseColor("#FF078707"),
                BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT
            )
        onTopBarView().performUndo()
        onTopBarView().performUndo()
        onDrawingSurfaceView()
            .checkPixelColor(
                Color.parseColor("#FF00B4F1"),
                BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT
            )
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT))
        onDrawingSurfaceView()
            .checkPixelColor(
                Color.parseColor("#FF0074CD"),
                BitmapLocationProvider.HALFWAY_BOTTOM_LEFT
            )
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT))
        onDrawingSurfaceView()
            .checkPixelColor(
                Color.parseColor("#FF0074CD"),
                BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE
            )
        onTopBarView().performClickCheckmark()
    }
}
