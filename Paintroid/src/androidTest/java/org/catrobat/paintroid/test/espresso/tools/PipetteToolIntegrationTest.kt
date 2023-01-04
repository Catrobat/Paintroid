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
import org.junit.runner.RunWith
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.junit.Before
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerPreviewInteraction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.wrappers.LayerMenuViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.junit.Rule
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class PipetteToolIntegrationTest {
    @Rule
    var launchActivityRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    @Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    @Before
    fun setUp() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.BRUSH)
    }

    @Test
    fun testOnEmptyBitmapPipetteTools() {
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.BLACK)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.PIPETTE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.TRANSPARENT)
    }

    @Test
    fun testOnEmptyBitmapPipetteColorPicker() {
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.BLACK)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.TOP_MIDDLE,
                    DrawingSurfaceLocationProvider.BOTTOM_MIDDLE
                )
            )
        ColorPickerViewInteraction.onColorPickerView()
            .clickPipetteButton()
        ColorPickerPreviewInteraction.onColorPickerPreview()
            .perform(UiInteractions.touchAt(GeneralLocation.CENTER))
        ColorPickerPreviewInteraction.onColorPickerPreview()
            .checkColorPreviewColor(Color.TRANSPARENT)
    }

    @Test
    fun testPipetteToolAfterBrushOnSingleLayer() {
        ToolPropertiesInteraction.onToolProperties()
            .setColor(Color.RED)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.RED, BitmapLocationProvider.MIDDLE)
        ToolPropertiesInteraction.onToolProperties()
            .setColorResource(R.color.pocketpaint_color_picker_transparent)
            .checkMatchesColor(Color.TRANSPARENT)
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.PIPETTE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.RED)
    }

    @Test
    fun testPipetteColorPickerAfterBrushOnSingleLayer() {
        ToolPropertiesInteraction.onToolProperties()
            .setColor(Color.RED)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.RED, BitmapLocationProvider.MIDDLE)
        ToolPropertiesInteraction.onToolProperties()
            .setColorResource(R.color.pocketpaint_color_picker_transparent)
            .checkMatchesColor(Color.TRANSPARENT)
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.TOP_MIDDLE,
                    DrawingSurfaceLocationProvider.BOTTOM_MIDDLE
                )
            )
        ColorPickerViewInteraction.onColorPickerView()
            .clickPipetteButton()
        ColorPickerPreviewInteraction.onColorPickerPreview()
            .perform(UiInteractions.touchAt(GeneralLocation.CENTER))
        ColorPickerPreviewInteraction.onColorPickerPreview()
            .checkColorPreviewColor(Color.RED)
    }

    @Test
    fun testPipetteToolAfterBrushOnMultiLayer() {
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .performClose()
        ToolPropertiesInteraction.onToolProperties()
            .setColor(Color.TRANSPARENT)
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.PIPETTE)
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.TRANSPARENT)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.BLACK)
    }

    @Test
    fun testPipetteColorPickerAfterBrushOnMultiLayer() {
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .performClose()
        ToolPropertiesInteraction.onToolProperties()
            .setColor(Color.TRANSPARENT)
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
            .checkCurrentViewColor(Color.TRANSPARENT)
        ColorPickerViewInteraction.onColorPickerView()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.TOP_MIDDLE,
                    DrawingSurfaceLocationProvider.BOTTOM_MIDDLE
                )
            )
        ColorPickerViewInteraction.onColorPickerView()
            .clickPipetteButton()
        ColorPickerPreviewInteraction.onColorPickerPreview()
            .checkColorPreviewColor(Color.TRANSPARENT)
        ColorPickerPreviewInteraction.onColorPickerPreview()
            .perform(UiInteractions.touchAt(GeneralLocation.CENTER))
        ColorPickerPreviewInteraction.onColorPickerPreview()
            .checkColorPreviewColor(Color.BLACK)
    }

    @Test
    fun testPipetteColorPickerAfterBrushOnSingleLayerAcceptColor() {
        ToolPropertiesInteraction.onToolProperties()
            .setColor(Color.RED)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.RED, BitmapLocationProvider.MIDDLE)
        ToolPropertiesInteraction.onToolProperties()
            .setColorResource(R.color.pocketpaint_color_picker_transparent)
            .checkMatchesColor(Color.TRANSPARENT)
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.TOP_MIDDLE,
                    DrawingSurfaceLocationProvider.BOTTOM_MIDDLE
                )
            )
        ColorPickerViewInteraction.onColorPickerView()
            .clickPipetteButton()
        ColorPickerPreviewInteraction.onColorPickerPreview()
            .perform(UiInteractions.touchAt(GeneralLocation.CENTER))
        ColorPickerPreviewInteraction.onColorPickerPreview()
            .performCloseColorPickerPreviewWithDoneButton()
        ColorPickerViewInteraction.onColorPickerView()
            .checkNewColorViewColor(Color.RED)
    }

    @Test
    fun testPipetteColorPickerShowDoneDialog() {
        ToolPropertiesInteraction.onToolProperties()
            .setColor(Color.BLACK)
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.TOP_MIDDLE,
                    DrawingSurfaceLocationProvider.BOTTOM_MIDDLE
                )
            )
        ColorPickerViewInteraction.onColorPickerView()
            .clickPipetteButton()
        ColorPickerPreviewInteraction.onColorPickerPreview()
            .perform(UiInteractions.touchAt(GeneralLocation.CENTER))
        ColorPickerPreviewInteraction.onColorPickerPreview()
            .assertShowColorPickerPreviewBackDialog()
    }

    @Test
    fun testPipetteColorPickerAfterBrushOnSingleLayerRejectColorWithDoneDialog() {
        ToolPropertiesInteraction.onToolProperties()
            .setColor(Color.RED)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.RED, BitmapLocationProvider.MIDDLE)
        ToolPropertiesInteraction.onToolProperties()
            .setColorResource(R.color.pocketpaint_color_picker_transparent)
            .checkMatchesColor(Color.TRANSPARENT)
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.TOP_MIDDLE,
                    DrawingSurfaceLocationProvider.BOTTOM_MIDDLE
                )
            )
        ColorPickerViewInteraction.onColorPickerView()
            .clickPipetteButton()
        ColorPickerPreviewInteraction.onColorPickerPreview()
            .perform(UiInteractions.touchAt(GeneralLocation.CENTER))
        ColorPickerPreviewInteraction.onColorPickerPreview()
            .performCloseColorPickerPreviewWithBackButtonDecline()
        ColorPickerViewInteraction.onColorPickerView()
            .checkNewColorViewColor(Color.TRANSPARENT)
    }

    @Test
    fun testPipetteColorPickerAfterBrushOnSingleLayerAcceptColorWithDoneDialog() {
        ToolPropertiesInteraction.onToolProperties()
            .setColor(Color.RED)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.RED, BitmapLocationProvider.MIDDLE)
        ToolPropertiesInteraction.onToolProperties()
            .setColorResource(R.color.pocketpaint_color_picker_transparent)
            .checkMatchesColor(Color.TRANSPARENT)
        ColorPickerViewInteraction.onColorPickerView()
            .performOpenColorPicker()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.TOP_MIDDLE,
                    DrawingSurfaceLocationProvider.BOTTOM_MIDDLE
                )
            )
        ColorPickerViewInteraction.onColorPickerView()
            .clickPipetteButton()
        ColorPickerPreviewInteraction.onColorPickerPreview()
            .perform(UiInteractions.touchAt(GeneralLocation.CENTER))
        ColorPickerPreviewInteraction.onColorPickerPreview()
            .performCloseColorPickerPreviewWithBackButtonAccept()
        ColorPickerViewInteraction.onColorPickerView()
            .checkNewColorViewColor(Color.RED)
    }

    @Test
    fun testPipetteAfterUndo() {
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.PIPETTE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.BLACK)
        TopBarViewInteraction.onTopBarView()
            .performUndo()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.TRANSPARENT)
    }

    @Test
    fun testPipetteAfterRedo() {
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.PIPETTE)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.BLACK)
        TopBarViewInteraction.onTopBarView()
            .performUndo()
        TopBarViewInteraction.onTopBarView()
            .performRedo()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.BLACK)
    }
}