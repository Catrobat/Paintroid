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
package org.catrobat.paintroid.test.espresso.tools

import android.graphics.Color
import android.graphics.Paint.Cap
import android.view.View
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
import org.catrobat.paintroid.test.espresso.util.wrappers.BrushPickerViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolType
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EraserToolIntegrationTest {
    @get:Rule
    var launchActivityRule = ActivityTestRule(
        MainActivity::class.java
    )

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    @Test
    fun testEraseOnEmptyBitmap() {
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.ERASER)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testEraseSinglePixel() {
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.ERASER)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testSwitchingBetweenBrushAndEraser() {
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.ERASER)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.BRUSH)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.ERASER)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testChangeEraserBrushSize() {
        var newStrokeWidth = 90
        BrushPickerViewInteraction.onBrushPickerView().onStrokeWidthSeekBar()
            .perform(UiInteractions.setProgress(newStrokeWidth))
            .check(ViewAssertions.matches(UiMatcher.withProgress(newStrokeWidth)))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.ERASER)
        BrushPickerViewInteraction.onBrushPickerView().onStrokeWidthSeekBar()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        ViewMatchers.isDisplayed(),
                        UiMatcher.withProgress(newStrokeWidth)
                    )
                )
            )
        BrushPickerViewInteraction.onBrushPickerView().onStrokeWidthTextView()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        ViewMatchers.isDisplayed(), ViewMatchers.withText(
                            Integer.toString(newStrokeWidth)
                        )
                    )
                )
            )
        newStrokeWidth = 80
        BrushPickerViewInteraction.onBrushPickerView().onStrokeWidthSeekBar()
            .perform(UiInteractions.setProgress(newStrokeWidth))
            .check(ViewAssertions.matches(UiMatcher.withProgress(newStrokeWidth)))
        ToolBarViewInteraction.onToolBarView()
            .performCloseToolOptionsView()
        ToolPropertiesInteraction.onToolProperties()
            .checkStrokeWidth(80f)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testChangeEraserBrushForm() {
        BrushPickerViewInteraction.onBrushPickerView()
            .onStrokeWidthSeekBar()
            .perform(UiInteractions.setProgress(70))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        BrushPickerViewInteraction.onBrushPickerView()
            .onStrokeWidthSeekBar()
            .perform(UiInteractions.setProgress(50))
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.ERASER)
        BrushPickerViewInteraction.onBrushPickerView().onStrokeCapSquareView()
            .perform(ViewActions.click())
        ToolBarViewInteraction.onToolBarView()
            .performCloseToolOptionsView()
        ToolPropertiesInteraction.onToolProperties()
            .checkCap(Cap.SQUARE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testRestorePreviousToolSettings() {
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.ERASER)
        BrushPickerViewInteraction.onBrushPickerView().onStrokeWidthTextView()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        ViewMatchers.isDisplayed(), ViewMatchers.withText(
                            TEXT_DEFAULT_STROKE_WIDTH
                        )
                    )
                )
            )
        BrushPickerViewInteraction.onBrushPickerView().onStrokeWidthSeekBar()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf<View>(
                        ViewMatchers.isDisplayed(),
                        UiMatcher.withProgress(DEFAULT_STROKE_WIDTH)
                    )
                )
            )
        val newStrokeWidth = 80
        BrushPickerViewInteraction.onBrushPickerView().onStrokeWidthSeekBar()
            .perform(UiInteractions.setProgress(newStrokeWidth))
            .check(ViewAssertions.matches(UiMatcher.withProgress(newStrokeWidth)))
        BrushPickerViewInteraction.onBrushPickerView().onStrokeCapSquareView()
            .perform(ViewActions.click())
        ToolPropertiesInteraction.onToolProperties()
            .checkStrokeWidth(newStrokeWidth.toFloat())
            .checkCap(Cap.SQUARE)
        ToolBarViewInteraction.onToolBarView()
            .performCloseToolOptionsView()
            .performOpenToolOptionsView()
        BrushPickerViewInteraction.onBrushPickerView().onStrokeWidthSeekBar()
            .check(ViewAssertions.matches(UiMatcher.withProgress(newStrokeWidth)))
        val eraserStrokeWidth = 60
        BrushPickerViewInteraction.onBrushPickerView().onStrokeWidthSeekBar()
            .perform(UiInteractions.setProgress(eraserStrokeWidth))
            .check(ViewAssertions.matches(UiMatcher.withProgress(eraserStrokeWidth)))
        BrushPickerViewInteraction.onBrushPickerView().onStrokeCapRoundView()
            .perform(ViewActions.click())
        ToolPropertiesInteraction.onToolProperties()
            .checkStrokeWidth(eraserStrokeWidth.toFloat())
            .checkCap(Cap.ROUND)
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.BRUSH)
        BrushPickerViewInteraction.onBrushPickerView().onStrokeWidthSeekBar()
            .check(ViewAssertions.matches(UiMatcher.withProgress(eraserStrokeWidth)))
        BrushPickerViewInteraction.onBrushPickerView().onStrokeCapRoundView()
            .check(ViewAssertions.matches(ViewMatchers.isSelected()))
        ToolPropertiesInteraction.onToolProperties()
            .checkCap(Cap.ROUND)
            .checkStrokeWidth(eraserStrokeWidth.toFloat())
    }

    companion object {
        private val TEXT_DEFAULT_STROKE_WIDTH = Integer.toString(DEFAULT_STROKE_WIDTH)
    }
}