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

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import android.widget.SeekBar
import androidx.test.espresso.PerformException
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.EspressoUtils.waitForToast
import org.catrobat.paintroid.test.espresso.util.MainActivityHelper
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView
import org.catrobat.paintroid.test.espresso.util.wrappers.LayerMenuViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.TransformToolOptionsViewInteraction.onTransformToolOptionsView
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape
import org.catrobat.paintroid.tools.implementation.MAXIMUM_BITMAP_SIZE_FACTOR
import org.catrobat.paintroid.tools.implementation.TransformTool
import org.catrobat.paintroid.ui.Perspective
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.lessThan
import org.hamcrest.Matchers.not
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@SuppressWarnings("LargeClass")
@RunWith(AndroidJUnit4::class)
class TransformToolIntegrationTest {
    @get:Rule
    var launchActivityRule = ActivityTestRule(
        MainActivity::class.java
    )

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    private var displayWidth = 0
    private var displayHeight = 0
    private var initialWidth = 0
    private var initialHeight = 0
    private var maxBitmapSize = 0
    private lateinit var perspective: Perspective
    private lateinit var layerModel: LayerContracts.Model
    private lateinit var toolReference: ToolReference
    private lateinit var mainActivity: MainActivity
    private lateinit var activityHelper: MainActivityHelper

    private var toolSelectionBoxHeight: Float
        get() {
            return (toolReference.tool as BaseToolWithRectangleShape).boxHeight
        }
        private set(height) {
            (toolReference.tool as BaseToolWithRectangleShape).boxHeight = height
        }

    private var toolSelectionBoxWidth: Float
        get() {
            return (toolReference.tool as BaseToolWithRectangleShape).boxWidth
        }
        private set(width) {
            (toolReference.tool as BaseToolWithRectangleShape).boxWidth = width
        }

    private fun getSurfacePointFromCanvasPoint(point: PointF): PointF =
        perspective.getSurfacePointFromCanvasPoint(point)

    private fun setToolSelectionBoxDimensions(width: Float, height: Float) {
        val currentTool = toolReference.tool as BaseToolWithRectangleShape
        currentTool.boxWidth = width
        currentTool.boxHeight = height
    }

    private val toolPosition: PointF
        get() = (toolReference.tool as BaseToolWithShape).toolPosition

    private fun setToolPosition(x: Float, y: Float) {
        (toolReference.tool as BaseToolWithShape).toolPosition[x] = y
    }

    private fun newPointF(point: PointF): PointF = PointF(point.x, point.y)

    @Before
    fun setUp() {
        mainActivity = launchActivityRule.activity
        activityHelper = MainActivityHelper(mainActivity)
        perspective = mainActivity.perspective
        layerModel = mainActivity.layerModel
        toolReference = mainActivity.toolReference
        displayWidth = activityHelper.displayWidth
        displayHeight = activityHelper.displayHeight
        maxBitmapSize = displayHeight * displayWidth * MAXIMUM_BITMAP_SIZE_FACTOR.toInt()
        val workingBitmap = layerModel.currentLayer!!.bitmap!!
        initialWidth = workingBitmap.width
        initialHeight = workingBitmap.height
        onToolBarView()
            .performSelectTool(ToolType.BRUSH)
    }

    @Test
    fun testAutoCrop() {
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        runBlocking {
            onTransformToolOptionsView()
                .performAutoCrop()
            delay(1000)
        }
        assertThat(toolSelectionBoxWidth, lessThan(initialWidth.toFloat()))
        assertThat(toolSelectionBoxHeight, lessThan(initialHeight.toFloat()))
    }

    @Test(expected = PerformException::class)
    fun testToolsClosedAfterAutoCrop() {
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        runBlocking {
            onTransformToolOptionsView().performAutoCrop()
            delay(1000)
            onTransformToolOptionsView().performAutoCrop()
        }
    }

    @Test
    fun testAutoCropOnEmptyBitmap() {
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        val position = newPointF(toolPosition)
        runBlocking {
            onTransformToolOptionsView()
                .performAutoCrop()
            delay(1000)
        }
        assertEquals(initialWidth.toFloat(), toolSelectionBoxWidth, Float.MIN_VALUE)
        assertEquals(initialHeight.toFloat(), toolSelectionBoxHeight, Float.MIN_VALUE)
        assertEquals(position, toolPosition)
        onDrawingSurfaceView()
            .checkBitmapDimension(initialWidth, initialHeight)
            .checkLayerDimensions(initialWidth, initialHeight)
    }

    @Test
    fun testAutoCropOnFilledBitmap() {
        onToolBarView()
            .performSelectTool(ToolType.FILL)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        val width = layerModel.currentLayer!!.bitmap!!.width
        val height = layerModel.currentLayer!!.bitmap!!.height
        val position = newPointF(toolPosition)
        runBlocking {
            onTransformToolOptionsView()
                .performAutoCrop()
            delay(1000)
        }
        assertEquals(width.toFloat(), toolSelectionBoxWidth, Float.MIN_VALUE)
        assertEquals(height.toFloat(), toolSelectionBoxHeight, Float.MIN_VALUE)
        assertEquals(position, toolPosition)
        onDrawingSurfaceView()
            .checkBitmapDimension(width, height)
            .checkLayerDimensions(width, height)
    }

    @Test
    fun testToolsMenuClosedOnApply() {
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        onTransformToolOptionsView()
            .moveSliderTo(50)
        onTransformToolOptionsView()
            .performApplyResize()
        onTransformToolOptionsView()
            .checkIsNotDisplayed()
    }

    @Test
    fun testAutoTextIsShown() {
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        onTransformToolOptionsView()
            .checkAutoDisplayed()
    }

    @Test
    fun testWhenNoPixelIsOnBitmap() {
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
            .performCloseToolOptionsView()
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION))
        onDrawingSurfaceView()
            .checkBitmapDimension(initialWidth, initialHeight)
            .checkLayerDimensions(initialWidth, initialHeight)
    }

    @LargeTest
    @Test
    fun testWhenNoPixelIsOnBitmapToasts() {
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
            .performCloseToolOptionsView()
        waitForToast(withText(R.string.transform_info_text), 1000)
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        waitForToast(withText(R.string.resize_nothing_to_resize), 1000)
    }

    @Test
    fun testChangeCroppingHeightAndCheckWidth() {
        onToolBarView()
            .performSelectTool(ToolType.HAND)
        onDrawingSurfaceView()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.MIDDLE,
                    DrawingSurfaceLocationProvider.TOP_MIDDLE
                )
            )

        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
            .performCloseToolOptionsView()

        val boundingBoxWidth: Float = toolSelectionBoxWidth
        val boundingBoxHeight: Float = toolSelectionBoxHeight

        onDrawingSurfaceView()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.BOTTOM_MIDDLE,
                    DrawingSurfaceLocationProvider.MIDDLE
                )
            )

        assertEquals(boundingBoxWidth, toolSelectionBoxWidth, Float.MIN_VALUE)
        assertThat(boundingBoxHeight, greaterThan(toolSelectionBoxHeight))
    }

    @Test
    fun testMoveCroppingBordersOnEmptyBitmapAndDoCrop() {
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
            .performCloseToolOptionsView()
        val width = initialWidth / 2
        val height = initialHeight / 2
        setToolSelectionBoxDimensions(width.toFloat(), height.toFloat())
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        onDrawingSurfaceView()
            .checkBitmapDimension(width, height)
            .checkLayerDimensions(width, height)
    }

    @Test
    fun testIfOnePixelIsFound() {
        val workingBitmap = layerModel.currentLayer!!.bitmap!!
        workingBitmap.setPixel(initialWidth / 2, initialHeight / 2, Color.BLACK)
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        runBlocking {
            onTransformToolOptionsView().performAutoCrop()
            delay(1000)
        }
        assertEquals(1f, toolSelectionBoxWidth, Float.MIN_VALUE)
        assertEquals(1f, toolSelectionBoxHeight, Float.MIN_VALUE)
        TopBarViewInteraction.onTopBarView().performClickCheckmark()
        onDrawingSurfaceView()
            .checkBitmapDimension(1, 1)
    }

    @Test
    fun testIfMultiplePixelAreFound() {
        val workingBitmap = layerModel.currentLayer!!.bitmap!!
        workingBitmap.setPixel(1, 1, Color.BLACK)
        workingBitmap.setPixel(initialWidth - 1, initialHeight - 1, Color.BLACK)
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        runBlocking {
            onTransformToolOptionsView()
                .performAutoCrop()
            delay(1000)
        }
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        onDrawingSurfaceView()
            .checkBitmapDimension(initialWidth - 1, initialHeight - 1)
    }

    @Test
    fun testIfDrawingSurfaceBoundsAreFoundAndNotCropped() {
        val workingBitmap = layerModel.currentLayer!!.bitmap!!
        workingBitmap.setPixel(initialWidth / 2, 0, Color.BLACK)
        workingBitmap.setPixel(0, initialHeight / 2, Color.BLACK)
        workingBitmap.setPixel(initialWidth - 1, initialHeight / 2, Color.BLACK)
        workingBitmap.setPixel(initialWidth / 2, initialHeight - 1, Color.BLACK)
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        runBlocking {
            onTransformToolOptionsView()
                .performAutoCrop()
            delay(1000)
        }
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION))
        onDrawingSurfaceView()
            .checkBitmapDimension(initialWidth, initialHeight)
    }

    @Test
    fun testIfClickOnCanvasCrops() {
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        var workingBitmap = layerModel.currentLayer!!.bitmap!!
        workingBitmap.eraseColor(Color.BLACK)
        for (indexWidth in 0 until initialWidth) {
            workingBitmap.setPixel(indexWidth, 0, Color.TRANSPARENT)
        }
        runBlocking {
            onTransformToolOptionsView()
                .performAutoCrop()
            delay(1000)
        }
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        onDrawingSurfaceView()
            .checkBitmapDimension(initialWidth, --initialHeight)
        workingBitmap = layerModel.currentLayer!!.bitmap!!
        for (indexWidth in 0 until initialWidth) {
            workingBitmap.setPixel(indexWidth, initialHeight - 1, Color.TRANSPARENT)
        }
        onToolBarView()
            .performOpenToolOptionsView()
        runBlocking {
            onTransformToolOptionsView()
                .performAutoCrop()
            delay(1000)
        }
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        onDrawingSurfaceView()
            .checkBitmapDimension(initialWidth, --initialHeight)
        workingBitmap = layerModel.currentLayer!!.bitmap!!
        for (indexHeight in 0 until initialHeight) {
            workingBitmap.setPixel(0, indexHeight, Color.TRANSPARENT)
        }
        onToolBarView()
            .performOpenToolOptionsView()
        runBlocking {
            onTransformToolOptionsView()
                .performAutoCrop()
            delay(1000)
        }
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        onDrawingSurfaceView()
            .checkBitmapDimension(--initialWidth, initialHeight)
        workingBitmap = layerModel.currentLayer!!.bitmap!!
        for (indexHeight in 0 until initialHeight) {
            workingBitmap.setPixel(initialWidth - 1, indexHeight, Color.TRANSPARENT)
        }
        onToolBarView()
            .performOpenToolOptionsView()
        runBlocking {
            onTransformToolOptionsView()
                .performAutoCrop()
            delay(1000)
        }
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        onDrawingSurfaceView()
            .checkBitmapDimension(--initialWidth, initialHeight)
    }

    @Test
    fun testSmallBitmapResizing() {
        val workingBitmap = layerModel.currentLayer!!.bitmap!!
        workingBitmap.setPixel(initialWidth / 2, initialHeight / 2, Color.BLACK)
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        runBlocking {
            onTransformToolOptionsView()
                .performAutoCrop()
            delay(1000)
        }
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        onDrawingSurfaceView()
            .checkBitmapDimension(1, 1)
        setToolSelectionBoxDimensions(initialWidth.toFloat(), initialHeight.toFloat())
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        onDrawingSurfaceView()
            .checkBitmapDimension(initialWidth, initialHeight)
    }

    @Test
    fun testCenterBitmapAfterCropAndUndo() {
        drawPlus(layerModel.currentLayer!!.bitmap!!, initialWidth / 2)
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        runBlocking {
            onTransformToolOptionsView()
                .performAutoCrop()
            delay(1000)
        }
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        val croppedBitmap = layerModel.currentLayer!!.bitmap!!
        assertThat(initialHeight, greaterThan(croppedBitmap.height))
        assertThat(initialWidth, greaterThan(croppedBitmap.width))
        TopBarViewInteraction.onTopBarView()
            .performUndo()
        val undoBitmap = layerModel.currentLayer!!.bitmap!!
        assertEquals(
            "undoBitmap.getHeight should be initialHeight",
            undoBitmap.height.toLong(),
            initialHeight.toLong()
        )
        assertEquals(
            "undoBitmap.getWidth should be initialWidth",
            undoBitmap.width.toLong(),
            initialWidth.toLong()
        )
    }

    @Test
    fun testIfBordersAreAlignedCorrectAfterCrop() {
        drawPlus(layerModel.currentLayer!!.bitmap!!, initialWidth / 2)
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        runBlocking {
            onTransformToolOptionsView()
                .performAutoCrop()
            delay(1000)
        }
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION))
        val croppedBitmap = layerModel.currentLayer!!.bitmap!!
        val width = croppedBitmap.width
        val height = croppedBitmap.height
        val tool = toolReference.tool as TransformTool
        assertEquals(0.0f, tool.resizeBoundWidthXLeft, Float.MIN_VALUE)
        assertEquals(width - 1f, tool.resizeBoundWidthXRight, Float.MIN_VALUE)
        assertEquals(0.0f, tool.resizeBoundHeightYTop, Float.MIN_VALUE)
        assertEquals(height - 1f, tool.resizeBoundHeightYBottom, Float.MIN_VALUE)
    }

    @Test
    fun testMoveLeftCroppingBorderAndDoCrop() {
        drawPlus(layerModel.currentLayer!!.bitmap!!, initialWidth / 2)
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        runBlocking {
            onTransformToolOptionsView()
                .performAutoCrop()
            delay(1000)
        }
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        val height = layerModel.currentLayer!!.bitmap!!.height
        val toolPosition = toolPosition
        val newSelectionBoxWidth: Float = toolSelectionBoxWidth / 2
        toolSelectionBoxWidth = newSelectionBoxWidth
        toolPosition.x += newSelectionBoxWidth / 2
        runBlocking {
            TopBarViewInteraction.onTopBarView()
                .performClickCheckmark()
        }
        onDrawingSurfaceView()
            .checkBitmapDimension(newSelectionBoxWidth.toInt(), height)
    }

    @Test
    fun testMoveRightCroppingBorderAndDoCrop() {
        drawPlus(layerModel.currentLayer!!.bitmap!!, initialWidth / 2)
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        runBlocking {
            onTransformToolOptionsView().performAutoCrop()
            delay(1000)
        }
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        val height = layerModel.currentLayer!!.bitmap!!.height
        val toolPosition = toolPosition
        val newSelectionBoxWidth: Float = toolSelectionBoxWidth / 2
        toolSelectionBoxWidth = newSelectionBoxWidth
        toolPosition.x -= newSelectionBoxWidth / 2
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        onDrawingSurfaceView()
            .checkBitmapDimension(newSelectionBoxWidth.toInt(), height)
    }

    @Test
    fun testMoveTopCroppingBorderAndDoCrop() {
        drawPlus(layerModel.currentLayer!!.bitmap!!, initialWidth / 2)
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        runBlocking {
            onTransformToolOptionsView()
                .performAutoCrop()
            delay(1000)
        }
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        val width = layerModel.currentLayer!!.bitmap!!.width
        val toolPosition = toolPosition
        val newSelectionBoxHeight: Float = toolSelectionBoxHeight / 2
        toolSelectionBoxHeight = newSelectionBoxHeight
        toolPosition.y += newSelectionBoxHeight / 2
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        onDrawingSurfaceView()
            .checkBitmapDimension(width, newSelectionBoxHeight.toInt())
    }

    @Test
    fun testMoveBottomCroppingBorderAndDoCrop() {
        drawPlus(layerModel.currentLayer!!.bitmap!!, initialWidth / 2)
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        runBlocking {
            onTransformToolOptionsView()
                .performAutoCrop()
            delay(1000)
        }
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        val width = layerModel.currentLayer!!.bitmap!!.width
        val toolPosition = toolPosition
        val newSelectionBoxHeight: Float = toolSelectionBoxHeight / 2
        toolSelectionBoxHeight = newSelectionBoxHeight
        toolPosition.y += newSelectionBoxHeight / 2
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        onDrawingSurfaceView()
            .checkBitmapDimension(width, newSelectionBoxHeight.toInt())
    }

    @SuppressWarnings("LongMethod")
    @Test
    fun testCropFromEverySideOnFilledBitmap() {
        onToolBarView()
            .performSelectTool(ToolType.FILL)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        runBlocking {
            onTransformToolOptionsView()
                .performAutoCrop()
            delay(1000)
        }
        onDrawingSurfaceView()
            .checkBitmapDimension(initialWidth, initialHeight)
        var width = initialWidth
        var height = initialHeight
        var cropSize = initialWidth / 8
        width -= cropSize
        layerModel.currentLayer!!.bitmap!!.setPixels(
            IntArray(cropSize * height),
            0,
            cropSize,
            0,
            0,
            cropSize,
            height
        )
        onToolBarView()
            .performOpenToolOptionsView()
        runBlocking {
            onTransformToolOptionsView()
                .performAutoCrop()
            delay(1000)
        }
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        onDrawingSurfaceView()
            .checkBitmapDimension(width, height)
        cropSize = initialHeight / 8
        height -= cropSize
        layerModel.currentLayer!!.bitmap!!.setPixels(
            IntArray(cropSize * width),
            0, width, 0, 0, width, cropSize
        )
        onToolBarView()
            .performOpenToolOptionsView()
        runBlocking {
            onTransformToolOptionsView()
                .performAutoCrop()
            delay(1000)
        }
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        onDrawingSurfaceView()
            .checkBitmapDimension(width, height)
        cropSize = initialWidth / 8
        width -= cropSize
        layerModel.currentLayer!!.bitmap!!.setPixels(
            IntArray(cropSize * height),
            0,
            cropSize,
            width,
            0,
            cropSize,
            height
        )
        onToolBarView()
            .performOpenToolOptionsView()
        runBlocking {
            onTransformToolOptionsView()
                .performAutoCrop()
            delay(1000)
        }
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        onDrawingSurfaceView()
            .checkBitmapDimension(width, height)
        cropSize = initialHeight / 8
        height -= cropSize
        layerModel.currentLayer!!.bitmap!!.setPixels(
            IntArray(cropSize * width),
            0,
            width,
            0,
            height,
            width,
            cropSize
        )
        onToolBarView()
            .performOpenToolOptionsView()
        runBlocking {
            onTransformToolOptionsView()
                .performAutoCrop()
            delay(1000)
        }
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        onDrawingSurfaceView()
            .checkBitmapDimension(width, height)
    }

    @Test
    fun testResizeBordersMatchBitmapBordersAfterCrop() {
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
            .performCloseToolOptionsView()
        drawPlus(layerModel.currentLayer!!.bitmap!!, initialWidth / 2)
        setToolSelectionBoxDimensions(initialWidth / 8f, initialHeight / 8f)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION))
        val croppedBitmap = layerModel.currentLayer!!.bitmap!!
        val height = croppedBitmap.height
        val width = croppedBitmap.width
        val tool = toolReference.tool as TransformTool
        assertEquals(0.0f, tool.resizeBoundWidthXLeft, Float.MIN_VALUE)
        assertEquals(width - 1f, tool.resizeBoundWidthXRight, Float.MIN_VALUE)
        assertEquals(0.0f, tool.resizeBoundHeightYTop, Float.MIN_VALUE)
        assertEquals(height - 1f, tool.resizeBoundHeightYBottom, Float.MIN_VALUE)
    }

    @Test
    fun testMaxImageResolution() {
        val maxWidth = maxBitmapSize / initialHeight
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
            .performCloseToolOptionsView()
        perspective.multiplyScale(.25f)
        val dragFrom =
            getSurfacePointFromCanvasPoint(PointF(initialWidth.toFloat(), initialHeight.toFloat()))
        val dragTo = getSurfacePointFromCanvasPoint(
            PointF(maxWidth + 10f, initialHeight.toFloat())
        )
        onDrawingSurfaceView()
            .perform(UiInteractions.swipe(dragFrom, dragTo))
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION))
        val enlargedBitmap = layerModel.currentLayer!!.bitmap!!
        val bitmapSize = enlargedBitmap.height + enlargedBitmap.width
        assertTrue(bitmapSize < maxBitmapSize)
    }

    @LargeTest
    @Test
    fun testMaxImageResolutionToast() {
        val maxWidth = maxBitmapSize / initialHeight
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
            .performCloseToolOptionsView()
        val zoomFactor = perspective.scaleForCenterBitmap * .25f
        perspective.scale = zoomFactor
        val dragFrom =
            getSurfacePointFromCanvasPoint(PointF(initialWidth.toFloat(), initialHeight.toFloat()))
        val dragTo = getSurfacePointFromCanvasPoint(
            PointF(maxWidth + 10f, initialHeight.toFloat())
        )
        onDrawingSurfaceView()
            .perform(UiInteractions.swipe(dragFrom, dragTo))
        waitForToast(
            withText(R.string.resize_max_image_resolution_reached),
            1000
        )
    }

    @Test
    fun testEnlargeEverySideAndCheckEnlargedColor() {
        onToolBarView()
            .performSelectTool(ToolType.FILL)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
            .performCloseToolOptionsView()
        val toolPosition = newPointF(toolPosition)
        var pixels: IntArray
        setToolPosition(toolPosition.x - 1, toolPosition.y)
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        var height: Int = layerModel.currentLayer!!.bitmap!!.height
        pixels = IntArray(height)
        layerModel.currentLayer!!.bitmap!!.getPixels(pixels, 0, 1, 0, 0, 1, height)
        for (pixel in pixels) {
            assertEquals(Color.TRANSPARENT.toLong(), pixel.toLong())
        }
        setToolPosition(toolPosition.x + 1, toolPosition.y)
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        var width: Int = layerModel.currentLayer!!.bitmap!!.width
        height = layerModel.currentLayer!!.bitmap!!.height
        pixels = IntArray(height)
        layerModel.currentLayer!!.bitmap!!.getPixels(pixels, 0, 1, width - 1, 0, 1, height)
        for (pixel in pixels) {
            assertEquals(Color.TRANSPARENT.toLong(), pixel.toLong())
        }
        setToolPosition(toolPosition.x, toolPosition.y - 1)
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        width = layerModel.currentLayer!!.bitmap!!.width
        pixels = IntArray(width)
        layerModel.currentLayer!!.bitmap!!.getPixels(pixels, 0, width, 0, 0, width, 1)
        for (pixel in pixels) {
            assertEquals(Color.TRANSPARENT.toLong(), pixel.toLong())
        }
        setToolPosition(toolPosition.x, toolPosition.y + 1)
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        width = layerModel.currentLayer!!.bitmap!!.width
        height = layerModel.currentLayer!!.bitmap!!.height
        pixels = IntArray(width)
        layerModel.currentLayer!!.bitmap!!.getPixels(pixels, 0, width, 0, height - 1, width, 1)
        for (pixel in pixels) {
            assertEquals(Color.TRANSPARENT.toLong(), pixel.toLong())
        }
    }

    @Test
    fun testResizeWithPartialOverlapping() {
        onToolBarView()
            .performSelectTool(ToolType.FILL)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
            .performCloseToolOptionsView()
        setToolPosition(initialWidth.toFloat(), initialHeight.toFloat())
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        onDrawingSurfaceView()
            .checkBitmapDimension(initialWidth, initialHeight)
        onToolBarView()
            .performOpenToolOptionsView()
        runBlocking {
            onTransformToolOptionsView()
                .performAutoCrop()
            delay(1000)
        }
        assertEquals(initialWidth / 2f, toolSelectionBoxWidth, Float.MIN_VALUE)
        assertEquals(initialHeight / 2f, toolSelectionBoxHeight, Float.MIN_VALUE)
    }

    @Test
    fun testResizeBoxCompletelyOutsideBitmap() {
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
            .performCloseToolOptionsView()
        perspective.multiplyScale(.25f)
        setToolPosition(initialWidth + initialHeight / 2f, initialHeight + initialHeight / 2f)
        setToolSelectionBoxDimensions((initialWidth / 2).toFloat(), (initialHeight / 2).toFloat())
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION))
        onDrawingSurfaceView()
            .checkBitmapDimension(initialWidth, initialHeight)
    }

    @Test
    fun testResizeBoxCompletelyOutsideBitmapToast() {
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
            .performCloseToolOptionsView()
        perspective.multiplyScale(.25f)
        setToolPosition(initialWidth + initialHeight / 2f, initialHeight + initialHeight / 2f)
        TopBarViewInteraction.onTopBarView()
            .performClickCheckmark()
        waitForToast(withText(R.string.resize_nothing_to_resize), 1000)
    }

    @Test
    fun testRotateMultipleLayers() {
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .performAddLayer()
            .performClose()
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        onTransformToolOptionsView()
            .performRotateClockwise()
        onDrawingSurfaceView()
            .checkBitmapDimension(initialHeight, initialWidth)
        onTransformToolOptionsView()
            .performRotateCounterClockwise()
        onDrawingSurfaceView()
            .checkBitmapDimension(initialWidth, initialHeight)
    }

    @Test
    fun testRotateMultipleLayersUndoRedo() {
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .performAddLayer()
            .performClose()
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        onTransformToolOptionsView()
            .performRotateClockwise()
        onDrawingSurfaceView()
            .checkBitmapDimension(initialHeight, initialWidth)
        onToolBarView()
            .performCloseToolOptionsView()
        TopBarViewInteraction.onTopBarView()
            .performUndo()
        onDrawingSurfaceView()
            .checkBitmapDimension(initialWidth, initialHeight)
        TopBarViewInteraction.onTopBarView()
            .performRedo()
        onDrawingSurfaceView()
            .checkBitmapDimension(initialHeight, initialWidth)
    }

    @Test
    fun testRotateLeft() {
        ToolPropertiesInteraction.onToolProperties()
            .setColorResource(R.color.pocketpaint_color_picker_green1)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT))
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        onTransformToolOptionsView()
            .performRotateCounterClockwise()
        onToolBarView()
            .performSelectTool(ToolType.PIPETTE)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT))
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColorResource(R.color.pocketpaint_color_picker_green1)
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        onTransformToolOptionsView()
            .performRotateCounterClockwise()
        onToolBarView()
            .performSelectTool(ToolType.PIPETTE)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT))
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColorResource(R.color.pocketpaint_color_picker_green1)
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        onTransformToolOptionsView()
            .performRotateCounterClockwise()
        onToolBarView()
            .performSelectTool(ToolType.PIPETTE)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT))
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColorResource(R.color.pocketpaint_color_picker_green1)
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        onTransformToolOptionsView()
            .performRotateCounterClockwise()
        onToolBarView()
            .performSelectTool(ToolType.PIPETTE)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT))
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColorResource(R.color.pocketpaint_color_picker_green1)
    }

    @Test
    fun testRotateRight() {
        ToolPropertiesInteraction.onToolProperties()
            .setColorResource(R.color.pocketpaint_color_picker_green1)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT))
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        onTransformToolOptionsView()
            .performRotateClockwise()
        onToolBarView()
            .performSelectTool(ToolType.PIPETTE)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT))
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColorResource(R.color.pocketpaint_color_picker_green1)
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        onTransformToolOptionsView()
            .performRotateClockwise()
        onToolBarView()
            .performSelectTool(ToolType.PIPETTE)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT))
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColorResource(R.color.pocketpaint_color_picker_green1)
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        onTransformToolOptionsView()
            .performRotateClockwise()
        onToolBarView()
            .performSelectTool(ToolType.PIPETTE)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT))
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColorResource(R.color.pocketpaint_color_picker_green1)
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        onTransformToolOptionsView()
            .performRotateClockwise()
        onToolBarView()
            .performSelectTool(ToolType.PIPETTE)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT))
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColorResource(R.color.pocketpaint_color_picker_green1)
    }

    @Test
    fun testRotateMultipleColors() {
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT))
        ToolPropertiesInteraction.onToolProperties()
            .setColorResource(R.color.pocketpaint_color_picker_green1)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT))
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        onTransformToolOptionsView()
            .performRotateClockwise()
        onToolBarView()
            .performSelectTool(ToolType.PIPETTE)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT))
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.BLACK)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT))
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColorResource(R.color.pocketpaint_color_picker_green1)
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        onTransformToolOptionsView()
            .performRotateClockwise()
        onToolBarView()
            .performSelectTool(ToolType.PIPETTE)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT))
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.BLACK)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT))
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColorResource(R.color.pocketpaint_color_picker_green1)
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        onTransformToolOptionsView()
            .performRotateClockwise()
        onToolBarView()
            .performSelectTool(ToolType.PIPETTE)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT))
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.BLACK)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT))
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColorResource(R.color.pocketpaint_color_picker_green1)
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        onTransformToolOptionsView()
            .performRotateClockwise()
        onToolBarView()
            .performSelectTool(ToolType.PIPETTE)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT))
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColor(Color.BLACK)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT))
        ToolPropertiesInteraction.onToolProperties()
            .checkMatchesColorResource(R.color.pocketpaint_color_picker_green1)
    }

    @Test
    fun testRotateMultipleLayersUndoRedoWhenRotatingWasNotLastCommand() {
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
            .performAddLayer()
            .performClose()
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        onTransformToolOptionsView()
            .performRotateClockwise()
        onDrawingSurfaceView()
            .checkBitmapDimension(initialHeight, initialWidth)
        onToolBarView()
            .performSelectTool(ToolType.BRUSH)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE))
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performSelectLayer(1)
            .performClose()
        onDrawingSurfaceView()
            .checkLayerDimensions(initialHeight, initialWidth)
        TopBarViewInteraction.onTopBarView().onUndoButton()
            .check(ViewAssertions.matches(isEnabled()))
        TopBarViewInteraction.onTopBarView().onRedoButton()
            .check(ViewAssertions.matches(not(isEnabled())))
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performSelectLayer(0)
        TopBarViewInteraction.onTopBarView().onUndoButton()
            .check(ViewAssertions.matches(isEnabled()))
        TopBarViewInteraction.onTopBarView().onRedoButton()
            .check(ViewAssertions.matches(not(isEnabled())))
        LayerMenuViewInteraction.onLayerMenuView()
            .performSelectLayer(1)
            .performClose()
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performSelectLayer(2)
        onDrawingSurfaceView()
            .checkLayerDimensions(initialHeight, initialWidth)
    }

    @Test
    fun testResizeImage() {
        val width = layerModel.width
        val height = layerModel.height
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        onTransformToolOptionsView()
            .moveSliderTo(50)
        onTransformToolOptionsView()
            .performApplyResize()
        val newWidth = (width.toFloat() / 100 * 50).toInt()
        val newHeight = (height.toFloat() / 100 * 50).toInt()
        assertEquals(newWidth.toLong(), layerModel.width.toLong())
        assertEquals(newHeight.toLong(), layerModel.height.toLong())
    }

    @Test
    fun testTryResizeImageToSizeZero() {
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        onTransformToolOptionsView()
            .moveSliderTo(1)
        onTransformToolOptionsView()
            .performApplyResize()
        val heightAfterCrop = layerModel.height
        val widthAfterCrop = layerModel.width
        onToolBarView()
            .performSelectTool(ToolType.BRUSH)
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        onTransformToolOptionsView()
            .moveSliderTo(1)
        onTransformToolOptionsView()
            .performApplyResize()
        val heightAfterSecondCrop = layerModel.height
        val widthAfterSecondCrop = layerModel.width
        waitForToast(
            withText(R.string.resize_cannot_resize_to_this_size),
            1000
        )
        assertThat(heightAfterCrop, equalTo(heightAfterSecondCrop))
        assertThat(widthAfterCrop, equalTo(widthAfterSecondCrop))
    }

    @Test
    fun testSeekBarAndTextViewTheSame() {
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        var seekBar =
            mainActivity.findViewById<SeekBar>(R.id.pocketpaint_transform_resize_seekbar)
        var progress = seekBar.progress
        onTransformToolOptionsView()
            .checkPercentageTextMatches(progress)
        onTransformToolOptionsView()
            .moveSliderTo(1)
        onToolBarView()
            .performSelectTool(ToolType.BRUSH)
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        seekBar =
            launchActivityRule.activity.findViewById(R.id.pocketpaint_transform_resize_seekbar)
        progress = seekBar.progress
        onTransformToolOptionsView()
            .checkPercentageTextMatches(progress)
        onTransformToolOptionsView()
            .performApplyResize()
        onTransformToolOptionsView()
            .moveSliderTo(50)
        progress = seekBar.progress
        onTransformToolOptionsView()
            .checkPercentageTextMatches(progress)
        onToolBarView()
            .performSelectTool(ToolType.BRUSH)
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        onTransformToolOptionsView()
            .performApplyResize()
        seekBar =
            launchActivityRule.activity.findViewById(R.id.pocketpaint_transform_resize_seekbar)
        progress = seekBar.progress
        onTransformToolOptionsView()
            .checkPercentageTextMatches(progress)
    }

    @Test
    fun testTransformToolDoesNotResetPerspectiveScale() {
        val scale = 2.0f
        perspective.scale = scale
        perspective.surfaceTranslationX = 50f
        perspective.surfaceTranslationY = 200f
        mainActivity.refreshDrawingSurface()
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        assertEquals(scale, perspective.scale, 0.0001f)
    }

    @Test
    fun testTransformToolSetCenterCloseCenter() {
        drawPlus(layerModel.currentLayer!!.bitmap!!, initialWidth / 2)
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)

        runBlocking {
            onTransformToolOptionsView().performSetCenterClick()
        }
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.BOTTOM_RIGHT_CLOSE_CENTER))
        runBlocking {
            TopBarViewInteraction.onTopBarView().performClickCheckmark()
            delay(1500)
        }
        assertThat(toolSelectionBoxWidth, lessThan(initialWidth.toFloat()))
        assertThat(toolSelectionBoxHeight, lessThan(initialHeight.toFloat()))
    }

    @Test
    fun testTransformToolSetCenterFarCenter() {
        drawPlus(layerModel.currentLayer!!.bitmap!!, initialWidth / 2)

        onToolBarView()
            .performSelectTool(ToolType.HAND)
        onDrawingSurfaceView()
            .perform(
                UiInteractions.swipe(
                    DrawingSurfaceLocationProvider.MIDDLE,
                    DrawingSurfaceLocationProvider.TOP_MIDDLE
                )
            )

        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)

        runBlocking {
            onTransformToolOptionsView().performSetCenterClick()
        }

        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.BOTTOM_RIGHT_CORNER))
        runBlocking {
            TopBarViewInteraction.onTopBarView().performClickCheckmark()
            delay(1500)
        }
        assertThat(toolSelectionBoxWidth, greaterThan(initialWidth.toFloat()))
        assertThat(toolSelectionBoxHeight, greaterThan(initialHeight.toFloat()))
    }

    @Test
    fun testClickingOnCheckmarkDoesNotResetZoomOrPlacement() {
        drawPlus(layerModel.currentLayer!!.bitmap!!, initialWidth / 2)
        perspective.translate(100f, 100f)
        perspective.multiplyScale(0.1f)
        val oldScale = perspective.scale
        val oldTranslationX = perspective.surfaceTranslationX
        val oldTranslationY = perspective.surfaceTranslationY
        onToolBarView()
            .performSelectTool(ToolType.TRANSFORM)
        runBlocking {
            onTransformToolOptionsView().performAutoCrop()
        }
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.BOTTOM_RIGHT_CORNER))
        runBlocking {
            TopBarViewInteraction.onTopBarView().performClickCheckmark()
        }
        assertEquals(oldScale, perspective.scale)
        assertEquals(oldTranslationY, perspective.surfaceTranslationY)
        assertEquals(oldTranslationX, perspective.surfaceTranslationX)
    }

    companion object {
        private fun drawPlus(bitmap: Bitmap, lineLength: Int) {
            val horizontalStartX = bitmap.width / 4
            val horizontalStartY = bitmap.height / 2
            val verticalStartX = bitmap.width / 2
            val verticalStartY = bitmap.height / 2 - lineLength / 2
            val pixelsColorArray = IntArray(10 * lineLength)
            for (indexColorArray in pixelsColorArray.indices) {
                pixelsColorArray[indexColorArray] = Color.BLACK
            }
            bitmap.setPixels(
                pixelsColorArray,
                0,
                lineLength,
                horizontalStartX,
                horizontalStartY,
                lineLength,
                10
            )
            bitmap.setPixels(
                pixelsColorArray,
                0,
                10,
                verticalStartX,
                verticalStartY,
                10,
                lineLength
            )
        }
    }
}
