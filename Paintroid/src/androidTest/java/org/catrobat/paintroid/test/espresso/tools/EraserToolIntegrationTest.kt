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
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.EspressoUtils.DEFAULT_STROKE_WIDTH
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.UiMatcher
import org.catrobat.paintroid.test.espresso.util.wrappers.BrushPickerViewInteraction.onBrushPickerView
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolType
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EraserToolIntegrationTest {
    @Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    @Test
    fun testEraseOnEmptyBitmap() {
        onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        onToolBarView().performSelectTool(ToolType.ERASER)
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testEraseSinglePixel() {
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        onToolBarView().performSelectTool(ToolType.ERASER)
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testSwitchingBetweenBrushAndEraser() {
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        onToolBarView().performSelectTool(ToolType.ERASER)
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        onToolBarView().performSelectTool(ToolType.BRUSH)
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        onToolBarView().performSelectTool(ToolType.ERASER)
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testChangeEraserBrushSize() {
        var newStrokeWidth = 90
        onBrushPickerView().onStrokeWidthSeekBar()
            .perform(UiInteractions.setProgress(newStrokeWidth))
            .check(ViewAssertions.matches(UiMatcher.withProgress(newStrokeWidth)))
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        onToolBarView().performSelectTool(ToolType.ERASER)
        onBrushPickerView().onStrokeWidthSeekBar()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        ViewMatchers.isDisplayed(),
                        UiMatcher.withProgress(newStrokeWidth)
                    )
                )
            )
        onBrushPickerView().onStrokeWidthTextView()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        ViewMatchers.isDisplayed(), ViewMatchers.withText(
                            newStrokeWidth.toString()
                        )
                    )
                )
            )
        newStrokeWidth = 80
        onBrushPickerView().onStrokeWidthSeekBar()
            .perform(UiInteractions.setProgress(newStrokeWidth))
            .check(ViewAssertions.matches(UiMatcher.withProgress(newStrokeWidth)))
        onToolBarView().performCloseToolOptionsView()
        onToolProperties().checkStrokeWidth(80f)
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testChangeEraserBrushForm() {
        onBrushPickerView()
            .onStrokeWidthSeekBar()
            .perform(UiInteractions.setProgress(70))
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        onBrushPickerView()
            .onStrokeWidthSeekBar()
            .perform(UiInteractions.setProgress(50))
        onToolBarView().performSelectTool(ToolType.ERASER)
        onBrushPickerView().onStrokeCapSquareView().perform(ViewActions.click())
        onToolBarView().performCloseToolOptionsView()
        onToolProperties().checkCap(Cap.SQUARE)
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testRestorePreviousToolSettings() {
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        onToolBarView().performSelectTool(ToolType.ERASER)
        onBrushPickerView().onStrokeWidthTextView()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        ViewMatchers.isDisplayed(), ViewMatchers.withText(
                            TEXT_DEFAULT_STROKE_WIDTH
                        )
                    )
                )
            )
        onBrushPickerView().onStrokeWidthSeekBar()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        ViewMatchers.isDisplayed(),
                        UiMatcher.withProgress(DEFAULT_STROKE_WIDTH)
                    )
                )
            )
        val newStrokeWidth = 80
        onBrushPickerView().onStrokeWidthSeekBar()
            .perform(UiInteractions.setProgress(newStrokeWidth))
            .check(ViewAssertions.matches(UiMatcher.withProgress(newStrokeWidth)))
        onBrushPickerView().onStrokeCapSquareView().perform(ViewActions.click())
        onToolProperties()
            .checkStrokeWidth(newStrokeWidth.toFloat())
            .checkCap(Cap.SQUARE)
        onToolBarView()
            .performCloseToolOptionsView()
            .performOpenToolOptionsView()
        onBrushPickerView().onStrokeWidthSeekBar()
            .check(ViewAssertions.matches(UiMatcher.withProgress(newStrokeWidth)))
        val eraserStrokeWidth = 60
        onBrushPickerView().onStrokeWidthSeekBar()
            .perform(UiInteractions.setProgress(eraserStrokeWidth))
            .check(ViewAssertions.matches(UiMatcher.withProgress(eraserStrokeWidth)))
        onBrushPickerView().onStrokeCapRoundView().perform(ViewActions.click())
        onToolProperties()
            .checkStrokeWidth(eraserStrokeWidth.toFloat())
            .checkCap(Cap.ROUND)
        onToolBarView()
            .performSelectTool(ToolType.BRUSH)
        onBrushPickerView().onStrokeWidthSeekBar()
            .check(ViewAssertions.matches(UiMatcher.withProgress(eraserStrokeWidth)))
        onBrushPickerView().onStrokeCapRoundView()
            .check(ViewAssertions.matches(ViewMatchers.isSelected()))
        onToolProperties()
            .checkCap(Cap.ROUND)
            .checkStrokeWidth(eraserStrokeWidth.toFloat())
    }

    companion object { private const val TEXT_DEFAULT_STROKE_WIDTH = DEFAULT_STROKE_WIDTH.toString() }
}
