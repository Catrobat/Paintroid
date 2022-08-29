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

package org.catrobat.paintroid.test.junit.tools

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.colorpicker.OnColorPickedListener
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.implementation.PipetteTool
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner.Silent::class)
class PipetteToolTest {
    @Mock
    private lateinit var commandManager: CommandManager

    @Mock
    private lateinit var listener: OnColorPickedListener

    @Mock
    private lateinit var toolPaint: ToolPaint

    @Mock
    private lateinit var workspace: Workspace

    @Mock
    private lateinit var toolOptionsViewController: ToolOptionsViewController

    @Mock
    private lateinit var contextCallback: ContextCallback
    private lateinit var toolToTest: PipetteTool
    private lateinit var idlingResource: CountingIdlingResource

    @get:Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
        idlingResource = launchActivityRule.activity.idlingResource
        IdlingRegistry.getInstance().register(idlingResource)

        val bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
        bitmap.setPixel(X_COORDINATE_RED, 0, Color.RED)
        bitmap.setPixel(X_COORDINATE_GREEN, 0, Color.GREEN)
        bitmap.setPixel(X_COORDINATE_BLUE, 0, Color.BLUE)
        bitmap.setPixel(X_COORDINATE_PART_TRANSPARENT, 0, -0x55555556)
        Mockito.`when`(workspace.bitmapOfAllLayers).thenReturn(bitmap)
        Mockito.`when`(workspace.contains(ArgumentMatchers.any(PointF::class.java)))
            .thenAnswer { invocation ->
                val argument = invocation.getArgument<PointF>(0)
                argument.x >= 0 && argument.y >= 0 && argument.x < 10 && argument.y < 10
            }
        toolToTest = PipetteTool(
            contextCallback, toolOptionsViewController,
            toolPaint, workspace, idlingResource, commandManager, listener
        )
    }

    @After
    fun tearDown() { IdlingRegistry.getInstance().unregister(idlingResource) }

    @Test
    fun testHandleDown() {
        toolToTest.handleDown(PointF(X_COORDINATE_RED.toFloat(), 0f))
        toolToTest.handleMove(PointF(X_COORDINATE_PART_TRANSPARENT.toFloat(), 0f))

        val inOrderToolPaint = Mockito.inOrder(toolPaint)
        inOrderToolPaint.verify(toolPaint).color = Color.RED
        inOrderToolPaint.verify(toolPaint).color = -0x55555556

        val inOrderListener = Mockito.inOrder(listener)
        inOrderListener.verify(listener).colorChanged(Color.RED)
        inOrderListener.verify(listener).colorChanged(-0x55555556)
    }

    @Test
    fun testHandleMove() {
        toolToTest.handleDown(PointF(X_COORDINATE_RED.toFloat(), 0f))
        toolToTest.handleMove(PointF((X_COORDINATE_RED + 1).toFloat(), 0f))
        toolToTest.handleMove(PointF(X_COORDINATE_GREEN.toFloat(), 0f))
        toolToTest.handleMove(PointF(X_COORDINATE_PART_TRANSPARENT.toFloat(), 0f))

        val inOrderToolPaint = Mockito.inOrder(toolPaint)
        inOrderToolPaint.verify(toolPaint).color = Color.RED
        inOrderToolPaint.verify(toolPaint).color = Color.TRANSPARENT
        inOrderToolPaint.verify(toolPaint).color = Color.GREEN
        inOrderToolPaint.verify(toolPaint).color = -0x55555556

        val inOrderListener = Mockito.inOrder(listener)
        inOrderListener.verify(listener).colorChanged(Color.RED)
        inOrderListener.verify(listener).colorChanged(Color.TRANSPARENT)
        inOrderListener.verify(listener).colorChanged(Color.GREEN)
        inOrderListener.verify(listener).colorChanged(-0x55555556)
    }

    @Test
    fun testHandleUp() {
        toolToTest.handleUp(PointF(X_COORDINATE_BLUE.toFloat(), 0f))
        toolToTest.handleUp(PointF(X_COORDINATE_PART_TRANSPARENT.toFloat(), 0f))

        val inOrderToolPaint = Mockito.inOrder(toolPaint)
        inOrderToolPaint.verify(toolPaint).color = Color.BLUE
        inOrderToolPaint.verify(toolPaint).color = -0x55555556

        val inOrderListener = Mockito.inOrder(listener)
        inOrderListener.verify(listener).colorChanged(Color.BLUE)
        inOrderListener.verify(listener).colorChanged(-0x55555556)
    }

    @Test
    fun testShouldReturnCorrectToolType() { Assert.assertThat(toolToTest.toolType, Matchers.`is`(ToolType.PIPETTE)) }

    @Test
    fun testShouldReturnCorrectColorForForTopButtonIfColorIsTransparent() {
        toolToTest.handleUp(PointF(0f, 0f))
        Mockito.verify(toolPaint).color = Color.TRANSPARENT
    }

    @Test
    fun testShouldReturnCorrectColorForForTopButtonIfColorIsRed() {
        toolToTest.handleUp(PointF(X_COORDINATE_RED.toFloat(), 0f))
        Mockito.verify(toolPaint).color = Color.RED
    }

    companion object {
        private const val X_COORDINATE_RED = 1
        private const val X_COORDINATE_GREEN = 3
        private const val X_COORDINATE_BLUE = 5
        private const val X_COORDINATE_PART_TRANSPARENT = 7
    }
}
