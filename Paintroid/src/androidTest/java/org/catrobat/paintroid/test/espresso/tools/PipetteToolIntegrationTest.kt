/*
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso.tools

import android.graphics.Color
import androidx.test.espresso.action.GeneralLocation
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerPreviewInteraction.onColorPickerPreview
import org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction.onColorPickerView
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView
import org.catrobat.paintroid.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolType
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PipetteToolIntegrationTest {
    @get:Rule
    var launchActivityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    @Before
    fun setUp() { onToolBarView().performSelectTool(ToolType.BRUSH) }

    @Test
    fun testOnEmptyBitmapPipetteTools() {
        onToolProperties().checkMatchesColor(Color.BLACK)
        onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        onToolBarView().performSelectTool(ToolType.PIPETTE)
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onToolProperties().checkMatchesColor(Color.TRANSPARENT)
    }

    @Test
    fun testOnEmptyBitmapPipetteColorPicker() {
        onToolProperties().checkMatchesColor(Color.BLACK)
        onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        onColorPickerView()
            .performOpenColorPicker()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.TOP_MIDDLE,
                    DrawingSurfaceLocationProvider.BOTTOM_MIDDLE
                )
            )
        onColorPickerView().clickPipetteButton()
        onColorPickerPreview().perform(UiInteractions.touchAt(GeneralLocation.CENTER))
        onColorPickerPreview().checkColorPreviewColor(Color.TRANSPARENT)
    }

    @Test
    fun testPipetteToolAfterBrushOnSingleLayer() {
        onToolProperties().setColor(Color.RED)
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.RED, BitmapLocationProvider.MIDDLE)
        onToolProperties()
            .setColorResource(R.color.pocketpaint_color_picker_transparent)
            .checkMatchesColor(Color.TRANSPARENT)
        onToolBarView().performSelectTool(ToolType.PIPETTE)
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onToolProperties().checkMatchesColor(Color.RED)
    }

    @Test
    fun testPipetteColorPickerAfterBrushOnSingleLayer() {
        onToolProperties().setColor(Color.RED)
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.RED, BitmapLocationProvider.MIDDLE)
        onToolProperties()
            .setColorResource(R.color.pocketpaint_color_picker_transparent)
            .checkMatchesColor(Color.TRANSPARENT)
        onColorPickerView()
            .performOpenColorPicker()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.TOP_MIDDLE,
                    DrawingSurfaceLocationProvider.BOTTOM_MIDDLE
                )
            )
        onColorPickerView().clickPipetteButton()
        onColorPickerPreview().perform(UiInteractions.touchAt(GeneralLocation.CENTER))
        onColorPickerPreview().checkColorPreviewColor(Color.RED)
    }

    @Test
    fun testPipetteToolAfterBrushOnMultiLayer() {
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .performClose()
        onToolProperties().setColor(Color.TRANSPARENT)
        onToolBarView().performSelectTool(ToolType.PIPETTE)
        onToolProperties().checkMatchesColor(Color.TRANSPARENT)
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onToolProperties().checkMatchesColor(Color.BLACK)
    }

    @Test
    fun testPipetteColorPickerAfterBrushOnMultiLayer() {
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .performClose()
        onToolProperties().setColor(Color.TRANSPARENT)
        onColorPickerView()
            .performOpenColorPicker()
            .checkCurrentViewColor(Color.TRANSPARENT)
        onColorPickerView()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.TOP_MIDDLE,
                    DrawingSurfaceLocationProvider.BOTTOM_MIDDLE
                )
            )
        onColorPickerView().clickPipetteButton()
        onColorPickerPreview().checkColorPreviewColor(Color.TRANSPARENT)
        onColorPickerPreview().perform(UiInteractions.touchAt(GeneralLocation.CENTER))
        onColorPickerPreview().checkColorPreviewColor(Color.BLACK)
    }

    @Test
    fun testPipetteColorPickerAfterBrushOnSingleLayerAcceptColor() {
        onToolProperties().setColor(Color.RED)
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.RED, BitmapLocationProvider.MIDDLE)
        onToolProperties()
            .setColorResource(R.color.pocketpaint_color_picker_transparent)
            .checkMatchesColor(Color.TRANSPARENT)
        onColorPickerView()
            .performOpenColorPicker()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.TOP_MIDDLE,
                    DrawingSurfaceLocationProvider.BOTTOM_MIDDLE
                )
            )
        onColorPickerView().clickPipetteButton()
        onColorPickerPreview().perform(UiInteractions.touchAt(GeneralLocation.CENTER))
        onColorPickerPreview().performCloseColorPickerPreviewWithDoneButton()
        onColorPickerView().checkNewColorViewColor(Color.RED)
    }

    @Test
    fun testPipetteColorPickerShowDoneDialog() {
        onToolProperties().setColor(Color.BLACK)
        onColorPickerView()
            .performOpenColorPicker()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.TOP_MIDDLE,
                    DrawingSurfaceLocationProvider.BOTTOM_MIDDLE
                )
            )
        onColorPickerView().clickPipetteButton()
        onColorPickerPreview().perform(UiInteractions.touchAt(GeneralLocation.CENTER))
        onColorPickerPreview().assertShowColorPickerPreviewBackDialog()
    }

    @Test
    fun testPipetteColorPickerAfterBrushOnSingleLayerRejectColorWithDoneDialog() {
        onToolProperties().setColor(Color.RED)
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.RED, BitmapLocationProvider.MIDDLE)
        onToolProperties()
            .setColorResource(R.color.pocketpaint_color_picker_transparent)
            .checkMatchesColor(Color.TRANSPARENT)
        onColorPickerView()
            .performOpenColorPicker()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.TOP_MIDDLE,
                    DrawingSurfaceLocationProvider.BOTTOM_MIDDLE
                )
            )
        onColorPickerView().clickPipetteButton()
        onColorPickerPreview().perform(UiInteractions.touchAt(GeneralLocation.CENTER))
        onColorPickerPreview().performCloseColorPickerPreviewWithBackButtonDecline()
        onColorPickerView().checkNewColorViewColor(Color.TRANSPARENT)
    }

    @Test
    fun testPipetteColorPickerAfterBrushOnSingleLayerAcceptColorWithDoneDialog() {
        onToolProperties().setColor(Color.RED)
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView().checkPixelColor(Color.RED, BitmapLocationProvider.MIDDLE)
        onToolProperties()
            .setColorResource(R.color.pocketpaint_color_picker_transparent)
            .checkMatchesColor(Color.TRANSPARENT)
        onColorPickerView()
            .performOpenColorPicker()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.TOP_MIDDLE,
                    DrawingSurfaceLocationProvider.BOTTOM_MIDDLE
                )
            )
        onColorPickerView().clickPipetteButton()
        onColorPickerPreview().perform(UiInteractions.touchAt(GeneralLocation.CENTER))
        onColorPickerPreview().performCloseColorPickerPreviewWithBackButtonAccept()
        onColorPickerView().checkNewColorViewColor(Color.RED)
    }

    @Test
    fun testPipetteAfterUndo() {
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onToolBarView().performSelectTool(ToolType.PIPETTE)
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onToolProperties().checkMatchesColor(Color.BLACK)
        TopBarViewInteraction.onTopBarView().performUndo()
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onToolProperties().checkMatchesColor(Color.TRANSPARENT)
    }

    @Test
    fun testPipetteAfterRedo() {
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onToolBarView().performSelectTool(ToolType.PIPETTE)
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onToolProperties().checkMatchesColor(Color.BLACK)
        TopBarViewInteraction.onTopBarView().performUndo()
        TopBarViewInteraction.onTopBarView().performRedo()
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onToolProperties().checkMatchesColor(Color.BLACK)
    }
}
