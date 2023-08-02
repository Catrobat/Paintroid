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
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.EspressoUtils
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.UiMatcher
import org.catrobat.paintroid.test.espresso.util.wrappers.BottomNavigationViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.catrobat.paintroid.test.utils.TestUtils
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.implementation.DynamicLineTool
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DynamicLineToolIntegrationTest {

    private val colorStringIndex0 = "#FF0074CD"
//    private val colorStringIndex1 = "#FF00B4F1"
    private val colorStringIndex2 = "#FF078707"
    private val colorStringBlack = "#FF000000"
    private val colorStringTransparent = "#00000000"
    private val colorStringGreen = "#FF00FF00"
    private val colorStringRed = "#FFFF0000"

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
        checkPixelColor(colorStringTransparent, BitmapLocationProvider.MIDDLE)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE)

        checkPixelColor(colorStringBlack, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testHorizontalLineColor() {
        checkPixelColor(colorStringTransparent, BitmapLocationProvider.MIDDLE)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE)

        checkPixelColor(colorStringBlack, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testDiagonalLineColor() {
        checkPixelColor(colorStringTransparent, BitmapLocationProvider.MIDDLE)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT)

        checkPixelColor(colorStringBlack, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testChangeStrokeCap() {
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_stroke_ibtn_rect)).perform(ViewActions.click())
        ToolPropertiesInteraction.onToolProperties()
            .checkStrokeWidth(EspressoUtils.DEFAULT_STROKE_WIDTH.toFloat())
            .checkCap(Paint.Cap.SQUARE)
    }

    @Test
    fun testCheckmarkLineFeature() {
        checkPixelColor(colorStringTransparent, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE)
        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        checkPixelColor(colorStringTransparent, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE)
        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)

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
        checkPixelColor(colorStringBlack, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testCheckmarkLineFeatureChangeColor() {
        ToolPropertiesInteraction.onToolProperties().setColor(Color.parseColor(colorStringBlack))
        checkPixelColor(colorStringTransparent, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE)
        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)

        ToolPropertiesInteraction.onToolProperties().setColor(Color.parseColor(colorStringGreen))
        checkPixelColor(colorStringGreen, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        checkPixelColor(colorStringTransparent, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE)
        checkPixelColor(colorStringGreen, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)

        TopBarViewInteraction.onTopBarView().onCheckmarkButton()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_checkmark),
                        ViewMatchers.isEnabled()
                    )
                )
            )

        ToolPropertiesInteraction.onToolProperties().setColor(Color.parseColor(colorStringRed))
        TopBarViewInteraction.onTopBarView().performClickCheckmark()
        checkPixelColor(colorStringRed, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testCheckmarkLineFeatureChangeShape() {
        ToolPropertiesInteraction.onToolProperties().setColor(Color.parseColor(colorStringBlack))
        checkPixelColor(colorStringTransparent, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE)
        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)

        ToolPropertiesInteraction.onToolProperties().setCap(Paint.Cap.SQUARE)
        ToolPropertiesInteraction.onToolProperties().checkCap(Paint.Cap.SQUARE)
        checkPixelColor(colorStringTransparent, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE)
        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)

        TopBarViewInteraction.onTopBarView().onCheckmarkButton()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_checkmark),
                        ViewMatchers.isEnabled()
                    )
                )
            )

        ToolPropertiesInteraction.onToolProperties().setColor(Color.parseColor(colorStringBlack))
        TopBarViewInteraction.onTopBarView().performClickCheckmark()
        checkPixelColor(colorStringBlack, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testCheckmarkLineFeatureChangeStrokeWidth() {
        ToolPropertiesInteraction.onToolProperties().setColor(Color.parseColor(colorStringBlack))
        checkPixelColor(colorStringTransparent, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE)
        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)

        ToolPropertiesInteraction.onToolProperties().setStrokeWidth(80f)
        ToolPropertiesInteraction.onToolProperties().checkStrokeWidth(80f)
        checkPixelColor(colorStringTransparent, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE)
        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)

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
        checkPixelColor(colorStringBlack, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testConnectedLinesFeature() {
        ToolPropertiesInteraction.onToolProperties().setColor(Color.parseColor(colorStringBlack))

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_TOP_LEFT)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE)
        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)

        TopBarViewInteraction.onTopBarView().performClickPlus()

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE)
        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE)

        TopBarViewInteraction.onTopBarView().performClickPlus()

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_TOP_LEFT)

        TopBarViewInteraction.onTopBarView().performClickCheckmark()
    }

    @Test
    fun testConnectedLinesFeatureDrawingLine() {
        ToolPropertiesInteraction.onToolProperties().setColor(Color.parseColor(colorStringBlack))

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE)
        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)

        TopBarViewInteraction.onTopBarView().performClickPlus()

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE)
        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE)

        TopBarViewInteraction.onTopBarView().performClickPlus()

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_TOP_LEFT)

        TopBarViewInteraction.onTopBarView().performClickCheckmark()
    }

    @Test
    fun testClickingUndoOnceOnConnectedLines() {
        ToolPropertiesInteraction.onToolProperties().setColor(Color.parseColor(colorStringBlack))

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_TOP_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE)
        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)

        TopBarViewInteraction.onTopBarView().performClickPlus()

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE)
        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)

        TopBarViewInteraction.onTopBarView().performUndo()

        checkPixelColor(colorStringTransparent, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        checkPixelColor(colorStringTransparent, BitmapLocationProvider.MIDDLE)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE)
        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)

        TopBarViewInteraction.onTopBarView().performClickCheckmark()
    }

    @Test
    fun testClickingUndoMoreThanOnceOnConnectedLines() {
        ToolPropertiesInteraction.onToolProperties().setColor(Color.parseColor(colorStringBlack))

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE)
        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE)
        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        checkPixelColor(colorStringBlack, BitmapLocationProvider.MIDDLE)

        TopBarViewInteraction.onTopBarView().performClickPlus()

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT)
        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT)

        TopBarViewInteraction.onTopBarView().performUndo()
        TopBarViewInteraction.onTopBarView().performUndo()

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE)
        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        checkPixelColor(colorStringTransparent, BitmapLocationProvider.MIDDLE)

        TopBarViewInteraction.onTopBarView().performClickCheckmark()
    }

    @Test
    fun testUndoWithDrawingConnectedLines() {
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE)

        TopBarViewInteraction.onTopBarView().performClickPlus()

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT)

        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT) // Check for black color

        TopBarViewInteraction.onTopBarView().performUndo()

        checkPixelColor(colorStringTransparent, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT) // Check for transparent color

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT)

        checkPixelColor(colorStringTransparent, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT) // Check for transparent color

        TopBarViewInteraction.onTopBarView().performClickPlus()

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT)

        TopBarViewInteraction.onTopBarView().performUndo()

        checkPixelColor(colorStringTransparent, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT) // Check for transparent color
        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_BOTTOM_LEFT) // Check for black color

        TopBarViewInteraction.onTopBarView().performClickCheckmark()
    }

    @Test
    fun testColorChangesAndQuittingConnectedLineMode() {
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE)

        TopBarViewInteraction.onTopBarView().performClickPlus()

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT)

        TestUtils.selectColorInDialog(0)

        checkPixelColor(colorStringIndex0, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT)

        TestUtils.selectColorInDialog(1)
        TestUtils.selectColorInDialog(2)

        checkPixelColor(colorStringIndex2, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT)

        TopBarViewInteraction.onTopBarView().performUndo()
        TopBarViewInteraction.onTopBarView().performUndo()

        checkPixelColor(colorStringIndex0, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT)
        checkPixelColor(colorStringIndex0, BitmapLocationProvider.HALFWAY_BOTTOM_LEFT)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT)
        checkPixelColor(colorStringIndex0, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT)

        TopBarViewInteraction.onTopBarView().performClickCheckmark()
    }

    @Test
    fun testTwoVerticesAfterOneLine() {
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE)

        val currentTool = launchActivityRule.activity.defaultToolController.currentTool as DynamicLineTool
        Assert.assertEquals(2, currentTool.vertexStack.size)
    }

    @Test
    fun testTwoVerticesAfterOneLineWhichIsAdjusted() {
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE)
        touchAt(DrawingSurfaceLocationProvider.BOTTOM_MIDDLE)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE)

        val currentTool = launchActivityRule.activity.defaultToolController.currentTool as DynamicLineTool
        Assert.assertEquals(2, currentTool.vertexStack.size)
    }

    @Test
    fun testThreeVerticesAfterTwoLines() {
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT)

        TopBarViewInteraction.onTopBarView().performClickPlus()

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE)

        val currentTool = launchActivityRule.activity.defaultToolController.currentTool as DynamicLineTool
        Assert.assertEquals(3, currentTool.vertexStack.size)
    }

    @Test
    fun testThreeVerticesAfterTwoLineWhichIsAdjusted() {
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT)

        TopBarViewInteraction.onTopBarView().performClickPlus()

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT)

        val currentTool = launchActivityRule.activity.defaultToolController.currentTool as DynamicLineTool
        Assert.assertEquals(3, currentTool.vertexStack.size)
    }

    @Test
    fun testMoveSourceVertex() {
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT)

        checkPixelColor(colorStringTransparent, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)

        swipe(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT, DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT)

        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
    }

    @Test
    fun testMoveEndVertex() {
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT)

        checkPixelColor(colorStringTransparent, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)

        swipe(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT, DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT)

        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
    }

    @Test
    fun testMoveMiddleVertex() {
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE)

        TopBarViewInteraction.onTopBarView().performClickPlus()

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT)

        checkPixelColor(colorStringTransparent, BitmapLocationProvider.HALFWAY_TOP_MIDDLE)

        swipe(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE, DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT)

        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_TOP_MIDDLE)
    }

    @Test
    fun testMoveAllThreeVertices() {
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE)

        TopBarViewInteraction.onTopBarView().performClickPlus()

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT)

        checkPixelColor(colorStringTransparent, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        checkPixelColor(colorStringTransparent, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT)

        swipe(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT, DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT)
        swipe(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE, DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE)
        swipe(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT, DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT)

        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        checkPixelColor(colorStringBlack, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT)
    }

    @Test
    fun testUndoOnlyPathResetsVertexStack() {
        val currentTool = launchActivityRule.activity.defaultToolController.currentTool as DynamicLineTool
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT)

        Assert.assertEquals(2, currentTool.vertexStack.size)

        TopBarViewInteraction.onTopBarView().performUndo()

        Assert.assertEquals(0, currentTool.vertexStack.size)
    }

    @Test
    fun testUndoOnlyPathRebuildsVertexStackAfterCheckmark() {
        val currentTool = launchActivityRule.activity.defaultToolController.currentTool as DynamicLineTool
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT)

        Assert.assertEquals(2, currentTool.vertexStack.size)

        TopBarViewInteraction.onTopBarView().performClickCheckmark()

        Assert.assertEquals(0, currentTool.vertexStack.size)

        TopBarViewInteraction.onTopBarView().performUndo()

        Assert.assertEquals(2, currentTool.vertexStack.size)
    }

    @Test
    fun testUndoSecondPathDecreasesVertexStackByOne() {
        val currentTool = launchActivityRule.activity.defaultToolController.currentTool as DynamicLineTool
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT)

        Assert.assertEquals(2, currentTool.vertexStack.size)

        TopBarViewInteraction.onTopBarView().performClickPlus()

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT)

        Assert.assertEquals(3, currentTool.vertexStack.size)

        TopBarViewInteraction.onTopBarView().performUndo()

        Assert.assertEquals(2, currentTool.vertexStack.size)
    }

    @Test
    fun testUndoAfterTwoPathsRebuildsVertexStackAfterCheckmark() {
        val currentTool = launchActivityRule.activity.defaultToolController.currentTool as DynamicLineTool
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT)

        Assert.assertEquals(2, currentTool.vertexStack.size)

        TopBarViewInteraction.onTopBarView().performClickPlus()

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT)

        Assert.assertEquals(3, currentTool.vertexStack.size)

        TopBarViewInteraction.onTopBarView().performClickCheckmark()
        TopBarViewInteraction.onTopBarView().performUndo()

        Assert.assertEquals(3, currentTool.vertexStack.size)
    }

    @Test
    fun testTwiceUndoResetsVertexStack() {
        val currentTool = launchActivityRule.activity.defaultToolController.currentTool as DynamicLineTool
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT)

        Assert.assertEquals(2, currentTool.vertexStack.size)

        TopBarViewInteraction.onTopBarView().performClickPlus()

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT)

        Assert.assertEquals(3, currentTool.vertexStack.size)

        TopBarViewInteraction.onTopBarView().performUndo()
        TopBarViewInteraction.onTopBarView().performUndo()

        Assert.assertEquals(0, currentTool.vertexStack.size)
    }

    @Test
    fun testRedoOnceRebuildsVertexStack() {
        val currentTool = launchActivityRule.activity.defaultToolController.currentTool as DynamicLineTool
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT)

        Assert.assertEquals(2, currentTool.vertexStack.size)

        TopBarViewInteraction.onTopBarView().performUndo()

        Assert.assertEquals(0, currentTool.vertexStack.size)

        TopBarViewInteraction.onTopBarView().performRedo()

        Assert.assertEquals(2, currentTool.vertexStack.size)
    }

    @Test
    fun testRedoTwiceRebuildsVertexStack() {
        val currentTool = launchActivityRule.activity.defaultToolController.currentTool as DynamicLineTool
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT)
        TopBarViewInteraction.onTopBarView().performClickPlus()
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT)

        Assert.assertEquals(3, currentTool.vertexStack.size)

        performUndo(2)

        Assert.assertEquals(0, currentTool.vertexStack.size)

        performRedo(2)

        Assert.assertEquals(3, currentTool.vertexStack.size)
    }

    @Test
    fun testUndoAfterSecondLineSequenceRebuildsOnlySecondVertexStack() {
        val currentTool = launchActivityRule.activity.defaultToolController.currentTool as DynamicLineTool
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT)
        TopBarViewInteraction.onTopBarView().performClickPlus()
        touchAt(DrawingSurfaceLocationProvider.MIDDLE)

        Assert.assertEquals(3, currentTool.vertexStack.size)

        TopBarViewInteraction.onTopBarView().performClickCheckmark()

        Assert.assertEquals(0, currentTool.vertexStack.size)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT)

        Assert.assertEquals(2, currentTool.vertexStack.size)

        TopBarViewInteraction.onTopBarView().performClickCheckmark()

        Assert.assertEquals(0, currentTool.vertexStack.size)

        TopBarViewInteraction.onTopBarView().performUndo()

        Assert.assertEquals(2, currentTool.vertexStack.size)
    }

    @Test
    fun testUndoUntilFirstLineSequenceRebuildsOnlyFirstVertexStack() {
        val currentTool = launchActivityRule.activity.defaultToolController.currentTool as DynamicLineTool
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT)
        TopBarViewInteraction.onTopBarView().performClickPlus()
        touchAt(DrawingSurfaceLocationProvider.MIDDLE)

        Assert.assertEquals(3, currentTool.vertexStack.size)

        TopBarViewInteraction.onTopBarView().performClickCheckmark()

        Assert.assertEquals(0, currentTool.vertexStack.size)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT)

        Assert.assertEquals(2, currentTool.vertexStack.size)

        TopBarViewInteraction.onTopBarView().performClickCheckmark()

        Assert.assertEquals(0, currentTool.vertexStack.size)

        performUndo(3)

        Assert.assertEquals(3, currentTool.vertexStack.size)
    }

    @Test
    fun testUndoAndRedoTwoPathSequences() {
        val currentTool = launchActivityRule.activity.defaultToolController.currentTool as DynamicLineTool
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT)
        TopBarViewInteraction.onTopBarView().performClickPlus()
        touchAt(DrawingSurfaceLocationProvider.MIDDLE)

        Assert.assertEquals(3, currentTool.vertexStack.size)

        TopBarViewInteraction.onTopBarView().performClickCheckmark()
        Assert.assertEquals(0, currentTool.vertexStack.size)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT)
        TopBarViewInteraction.onTopBarView().performClickPlus()
        touchAt(DrawingSurfaceLocationProvider.MIDDLE)

        Assert.assertEquals(3, currentTool.vertexStack.size)

        TopBarViewInteraction.onTopBarView().performClickCheckmark()
        Assert.assertEquals(0, currentTool.vertexStack.size)

        performUndo(6)

        Assert.assertEquals(0, currentTool.vertexStack.size)

        performRedo(2)

        Assert.assertEquals(3, currentTool.vertexStack.size)

        performRedo(2)

        Assert.assertEquals(3, currentTool.vertexStack.size)

        TopBarViewInteraction.onTopBarView().performClickCheckmark()

        Assert.assertEquals(0, currentTool.vertexStack.size)
    }

    @Test
    fun testUndoAfterToolChangeSetsDynamicLineToolAndRebuildsVertexStack() {
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT)

        TopBarViewInteraction.onTopBarView().performClickCheckmark()

        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.SPRAY)
        Assert.assertEquals(ToolType.SPRAY, launchActivityRule.activity.defaultToolController.currentTool?.toolType)

        touchAt(DrawingSurfaceLocationProvider.MIDDLE)

        performUndo(2)

        Assert.assertEquals(ToolType.DYNAMICLINE, launchActivityRule.activity.defaultToolController.currentTool?.toolType)
        val currentTool = launchActivityRule.activity.defaultToolController.currentTool as DynamicLineTool
        Assert.assertEquals(2, currentTool.vertexStack.size)
    }

    @Test
    fun testRedoFromDifferentActiveToolSwitchesToDynamicLineToolAndRebuildsVertexStack() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.SPRAY)
        Assert.assertEquals(ToolType.SPRAY, launchActivityRule.activity.defaultToolController.currentTool?.toolType)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE)
        touchAt(DrawingSurfaceLocationProvider.MIDDLE)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE)

        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.DYNAMICLINE)
        Assert.assertEquals(ToolType.DYNAMICLINE, launchActivityRule.activity.defaultToolController.currentTool?.toolType)

        TopBarViewInteraction.onTopBarView().performClickCheckmark()

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT)

        TopBarViewInteraction.onTopBarView().performClickCheckmark()

        performUndo(5)

        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.SPRAY)
        Assert.assertEquals(ToolType.SPRAY, launchActivityRule.activity.defaultToolController.currentTool?.toolType)

        performRedo(4)

        Assert.assertEquals(ToolType.DYNAMICLINE, launchActivityRule.activity.defaultToolController.currentTool?.toolType)
        val currentTool = launchActivityRule.activity.defaultToolController.currentTool as DynamicLineTool
        Assert.assertEquals(2, currentTool.vertexStack.size)
    }

    @Test
    fun testRedoIsAvailableAfterUndo() {
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT)
        TopBarViewInteraction.onTopBarView().performClickPlus()
        touchAt(DrawingSurfaceLocationProvider.MIDDLE)

        performUndo(1)

        Assert.assertTrue(launchActivityRule.activity.commandManager.isRedoAvailable)
    }

    @Test
    fun testRedoIsClearedAfterUndoAndMovingVertex() {
        ToolPropertiesInteraction.onToolProperties().setColor(Color.BLACK)

        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT)
        touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT)
        TopBarViewInteraction.onTopBarView().performClickPlus()
        touchAt(DrawingSurfaceLocationProvider.MIDDLE)

        performUndo(1)

        Assert.assertTrue(launchActivityRule.activity.commandManager.isRedoAvailable)

        swipe(DrawingSurfaceLocationProvider.HALFWAY_TOP_RIGHT, DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT)

        Assert.assertFalse(launchActivityRule.activity.commandManager.isRedoAvailable)
    }

    private fun checkPixelColor(colorString: String, position: BitmapLocationProvider) {
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.parseColor(colorString), position)
    }

    private fun touchAt(position: DrawingSurfaceLocationProvider) {
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(position))
    }

    private fun swipe(from: DrawingSurfaceLocationProvider, to: DrawingSurfaceLocationProvider) {
        touchAt(from)
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.swipe(from, to))
        touchAt(to)
    }

    private fun performUndo(times: Int) {
        repeat(times) {
            TopBarViewInteraction.onTopBarView().performUndo()
        }
    }

    private fun performRedo(times: Int) {
        repeat(times) {
            TopBarViewInteraction.onTopBarView().performRedo()
        }
    }
}
