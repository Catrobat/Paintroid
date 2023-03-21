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

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.wrappers.*
import org.catrobat.paintroid.test.espresso.util.wrappers.ClipboardToolViewInteraction.Companion.onClipboardToolViewInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.drawable.DrawableShape
import org.catrobat.paintroid.tools.drawable.DrawableStyle
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape
import org.catrobat.paintroid.tools.implementation.ClipboardTool
import org.catrobat.paintroid.ui.Perspective
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ClipboardToolIntegrationTest {
    @get:Rule
    var launchActivityRule = ActivityTestRule(
        MainActivity::class.java
    )

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    private var workspace: Workspace? = null
    private var perspective: Perspective? = null
    private var toolReference: ToolReference? = null
    private var mainActivity: MainActivity? = null
    @Before
    fun setUp() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.BRUSH)
        mainActivity = launchActivityRule.activity
        workspace = mainActivity?.workspace
        perspective = mainActivity?.perspective
        toolReference = mainActivity?.toolReference
    }

    @Test
    fun testBorders() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.SHAPE)
        ShapeToolOptionsViewInteraction.onShapeToolOptionsView()
            .performSelectShape(DrawableShape.RECTANGLE)
            .performSelectShapeDrawType(DrawableStyle.STROKE)
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.CLIPBOARD)
        val clipboardTool = toolReference!!.tool as ClipboardTool?
        clipboardTool!!.boxHeight -= 25f
        clipboardTool!!.boxWidth -= 25f
        onClipboardToolViewInteraction()
            .performCopy()
        val topLeft = clipboardTool!!.drawingBitmap!!.getPixel(0, 0)
        val topRight =
            clipboardTool.drawingBitmap!!.getPixel(clipboardTool.drawingBitmap!!.width - 1, 0)
        val bottomLeft =
            clipboardTool.drawingBitmap!!.getPixel(0, clipboardTool.drawingBitmap!!.height - 1)
        val bottomRight = clipboardTool.drawingBitmap!!.getPixel(
            clipboardTool.drawingBitmap!!.width - 1,
            clipboardTool.drawingBitmap!!.height - 1
        )
        Assert.assertEquals(topLeft.toLong(), Color.BLACK.toLong())
        Assert.assertEquals(topRight.toLong(), Color.BLACK.toLong())
        Assert.assertEquals(bottomLeft.toLong(), Color.BLACK.toLong())
        Assert.assertEquals(bottomRight.toLong(), Color.BLACK.toLong())
    }

    @Test
    fun testClipboardToolConsidersLayerOpacity() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.SHAPE)
        ShapeToolOptionsViewInteraction.onShapeToolOptionsView()
            .performSelectShape(DrawableShape.RECTANGLE)
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.CLIPBOARD)
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performSetOpacityTo(50, 0)
            .performClose()
        onClipboardToolViewInteraction()
            .performCopy()
        val clipboardTool = toolReference!!.tool as ClipboardTool?
        val fiftyPercentOpacityBlack = Color.argb(255 / 2, 0, 0, 0)
        val centerPixel = clipboardTool!!.drawingBitmap!!.getPixel(
            clipboardTool.drawingBitmap!!.width / 2,
            clipboardTool.drawingBitmap!!.height / 2
        )
        Assert.assertEquals(centerPixel.toLong(), Color.BLACK.toLong())
        onClipboardToolViewInteraction()
            .performPaste()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(fiftyPercentOpacityBlack, BitmapLocationProvider.MIDDLE)
        onClipboardToolViewInteraction()
            .performCut()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        onClipboardToolViewInteraction()
            .performPaste()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(fiftyPercentOpacityBlack, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testCopyPixel() {
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.CLIPBOARD)
        onClipboardToolViewInteraction()
            .performCopy()
        val clipboardTool = toolReference!!.tool as ClipboardTool?
        clipboardTool!!.toolPosition[clipboardTool.toolPosition.x] =
            clipboardTool.toolPosition.y * .5f
        onClipboardToolViewInteraction()
            .performPaste()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(
                Color.BLACK,
                clipboardTool.toolPosition.x,
                clipboardTool.toolPosition.y
            )
    }

    @Test
    fun testCutAndPastePixel() {
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.CLIPBOARD)
        onClipboardToolViewInteraction()
            .performCut()
        val clipboardTool = toolReference!!.tool as ClipboardTool?
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(
                Color.TRANSPARENT,
                clipboardTool!!.toolPosition.x,
                clipboardTool.toolPosition.y
            )
        onClipboardToolViewInteraction()
            .performPaste()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(
                Color.BLACK,
                clipboardTool.toolPosition.x,
                clipboardTool.toolPosition.y
            )
    }

    @Test
    fun testClipboardToolNotCapturingOtherLayers() {
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.CLIPBOARD)
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
        LayerMenuViewInteraction.onLayerMenuView()
            .performClose()
        onClipboardToolViewInteraction()
            .performCopy()
        val clipboardTool = toolReference!!.tool as ClipboardTool?
        clipboardTool!!.toolPosition[clipboardTool.toolPosition.x] =
            clipboardTool.toolPosition.y * .5f
        onClipboardToolViewInteraction()
            .performPaste()
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(
                Color.TRANSPARENT,
                clipboardTool.toolPosition.x,
                clipboardTool.toolPosition.y * .5f
            )
    }

    @Test
    fun testClipboardToolOutsideDrawingSurface() {
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        val bitmapWidth = workspace!!.width
        val bitmapHeight = workspace!!.height
        perspective!!.scale = SCALE_25
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.CLIPBOARD)
        val clipboardTool = toolReference!!.tool as ClipboardTool?
        val toolPosition = PointF(perspective!!.surfaceCenterX, perspective!!.surfaceCenterY)
        clipboardTool!!.toolPosition.set(toolPosition)
        clipboardTool.boxWidth = (bitmapWidth * STAMP_RESIZE_FACTOR).toInt().toFloat()
        clipboardTool.boxHeight = (bitmapHeight * STAMP_RESIZE_FACTOR).toInt().toFloat()
        onClipboardToolViewInteraction()
            .performPaste()
        Assert.assertNotNull(clipboardTool.drawingBitmap)
    }

    @Test
    fun testBitmapSavedOnOrientationChange() {
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.CLIPBOARD)
        val emptyBitmap =
            Bitmap.createBitmap((toolReference!!.tool as BaseToolWithRectangleShape?)!!.drawingBitmap!!)
        onClipboardToolViewInteraction()
            .performCopy()
        val expectedBitmap =
            Bitmap.createBitmap((toolReference!!.tool as BaseToolWithRectangleShape?)!!.drawingBitmap!!)
        Assert.assertFalse(expectedBitmap.sameAs(emptyBitmap))
        mainActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        val actualBitmap =
            Bitmap.createBitmap((toolReference!!.tool as BaseToolWithRectangleShape?)!!.drawingBitmap!!)
        Assert.assertTrue(expectedBitmap.sameAs(actualBitmap))
    }

    @Test
    fun testClipboardToolDoesNotResetPerspectiveScale() {
        val scale = 2.0f
        perspective!!.scale = scale
        perspective!!.surfaceTranslationX = 50f
        perspective!!.surfaceTranslationY = 200f
        mainActivity!!.refreshDrawingSurface()
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.CLIPBOARD)
        Assert.assertEquals(scale, perspective!!.scale, 0.0001f)
    }

    companion object {
        private const val SCALE_25 = 0.25f
        private const val STAMP_RESIZE_FACTOR = 1.5f
    }
}