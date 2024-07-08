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

import android.graphics.Color
import android.graphics.Paint.Cap
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.EspressoUtils.DEFAULT_STROKE_WIDTH
import org.catrobat.paintroid.test.espresso.util.UiInteractions.setProgress
import org.catrobat.paintroid.test.espresso.util.UiInteractions.swipeAccurate
import org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt
import org.catrobat.paintroid.test.espresso.util.UiInteractions.waitFor
import org.catrobat.paintroid.test.espresso.util.UiMatcher.withProgress
import org.catrobat.paintroid.test.espresso.util.wrappers.BrushPickerViewInteraction.Companion.onBrushPickerView
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.Companion.onDrawingSurfaceView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.Companion.onToolBarView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction.Companion.onToolProperties
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.Companion.onTopBarView
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolType
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EraserToolIntegrationTest {
    @get:Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    @Test
    fun testEraseOnEmptyBitmap() {
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        onToolBarView()
            .performSelectTool(ToolType.ERASER)
        onDrawingSurfaceView()
            .perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testEraseSinglePixel() {
        onDrawingSurfaceView()
            .perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        onToolBarView()
            .performSelectTool(ToolType.ERASER)
        onDrawingSurfaceView()
            .perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testSwitchingBetweenBrushAndEraser() {
        onDrawingSurfaceView()
            .perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        onToolBarView()
            .performSelectTool(ToolType.ERASER)
        onDrawingSurfaceView()
            .perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        onToolBarView()
            .performSelectTool(ToolType.BRUSH)
        onDrawingSurfaceView()
            .perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        onToolBarView()
            .performSelectTool(ToolType.ERASER)
        onDrawingSurfaceView()
            .perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testChangeEraserBrushSize() {
        var newStrokeWidth = 90
        onBrushPickerView().onStrokeWidthSeekBar()
            .perform(setProgress(newStrokeWidth))
            .check(matches(withProgress(newStrokeWidth)))
        onDrawingSurfaceView()
            .perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        onToolBarView()
            .performSelectTool(ToolType.ERASER)
        onBrushPickerView().onStrokeWidthSeekBar()
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withProgress(newStrokeWidth)
                    )
                )
            )
        onBrushPickerView().onStrokeWidthTextView()
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withText(
                            newStrokeWidth.toString()
                        )
                    )
                )
            )
        newStrokeWidth = 80
        onBrushPickerView().onStrokeWidthSeekBar()
            .perform(setProgress(newStrokeWidth))
            .check(matches(withProgress(newStrokeWidth)))
        onToolBarView()
            .performCloseToolOptionsView()
        onToolProperties()
            .checkStrokeWidth(80f)
        onDrawingSurfaceView()
            .perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testChangeEraserBrushForm() {
        onBrushPickerView()
            .onStrokeWidthSeekBar()
            .perform(setProgress(70))
        onDrawingSurfaceView()
            .perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        onBrushPickerView()
            .onStrokeWidthSeekBar()
            .perform(setProgress(50))
        onToolBarView()
            .performSelectTool(ToolType.ERASER)
        onBrushPickerView().onStrokeCapSquareView()
            .perform(click())
        onToolBarView()
            .performCloseToolOptionsView()
        onToolProperties()
            .checkCap(Cap.SQUARE)
        onDrawingSurfaceView()
            .perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
    }

    @Suppress("LongMethod")
    @Test
    fun testRestorePreviousToolSettings() {
        onDrawingSurfaceView()
            .perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        onToolBarView()
            .performSelectTool(ToolType.ERASER)
        onBrushPickerView().onStrokeWidthTextView()
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withText(
                            TEXT_DEFAULT_STROKE_WIDTH
                        )
                    )
                )
            )
        onBrushPickerView().onStrokeWidthSeekBar()
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withProgress(DEFAULT_STROKE_WIDTH)
                    )
                )
            )
        val newStrokeWidth = 80
        onBrushPickerView().onStrokeWidthSeekBar()
            .perform(setProgress(newStrokeWidth))
            .check(matches(withProgress(newStrokeWidth)))
        onBrushPickerView().onStrokeCapSquareView()
            .perform(click())
        onToolProperties()
            .checkStrokeWidth(newStrokeWidth.toFloat())
            .checkCap(Cap.SQUARE)
        onToolBarView()
            .performCloseToolOptionsView()
            .performOpenToolOptionsView()
        onBrushPickerView().onStrokeWidthSeekBar()
            .check(matches(withProgress(newStrokeWidth)))
        val eraserStrokeWidth = 60
        onBrushPickerView().onStrokeWidthSeekBar()
            .perform(setProgress(eraserStrokeWidth))
            .check(matches(withProgress(eraserStrokeWidth)))
        onBrushPickerView().onStrokeCapRoundView()
            .perform(click())
        onToolProperties()
            .checkStrokeWidth(eraserStrokeWidth.toFloat())
            .checkCap(Cap.ROUND)
        onToolBarView()
            .performSelectTool(ToolType.BRUSH)
        onBrushPickerView().onStrokeWidthSeekBar()
            .check(matches(withProgress(eraserStrokeWidth)))
        onBrushPickerView().onStrokeCapRoundView()
            .check(matches(ViewMatchers.isSelected()))
        onToolProperties()
            .checkCap(Cap.ROUND)
            .checkStrokeWidth(eraserStrokeWidth.toFloat())
    }

    @Test
    fun fillBitmapEraseASpotThenFillTheErasedSpotCreatesNoCheckeredPattern() {
        onToolBarView()
            .performSelectTool(ToolType.FILL)
        onDrawingSurfaceView()
            .perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        onToolBarView()
            .performSelectTool(ToolType.ERASER)
        onDrawingSurfaceView().perform(
            swipeAccurate(
                DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE,
                DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE
            )
        )
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_TOP_MIDDLE)
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE)
        onToolBarView()
            .performSelectTool(ToolType.FILL)
        onDrawingSurfaceView()
            .perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_TOP_MIDDLE)
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE)
    }

    @Test
    fun fillBitmapEraseASpotThenFillTheErasedSpotUndoRedoNoCheckeredPattern() {
        onToolBarView()
            .performSelectTool(ToolType.FILL)
        onDrawingSurfaceView()
            .perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        onToolBarView()
            .performSelectTool(ToolType.ERASER)
        onDrawingSurfaceView().perform(
            swipeAccurate(
                DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE,
                DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE
            )
        )
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_TOP_MIDDLE)
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE)
        onToolBarView()
            .performSelectTool(ToolType.FILL)
        onDrawingSurfaceView()
            .perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_TOP_MIDDLE)
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE)
        onTopBarView().performUndo()
        Espresso.onView(ViewMatchers.isRoot()).perform(waitFor(2000))
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_TOP_MIDDLE)
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE)
        onTopBarView().performRedo()
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_TOP_MIDDLE)
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE)
    }

    companion object {
        private const val TEXT_DEFAULT_STROKE_WIDTH = DEFAULT_STROKE_WIDTH.toString()
    }
}
