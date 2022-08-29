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

import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.command.implementation.PointCommand
import org.catrobat.paintroid.test.junit.stubs.PathStub
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.implementation.CursorTool
import org.catrobat.paintroid.tools.options.BrushToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import org.catrobat.paintroid.ui.Perspective
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

@RunWith(MockitoJUnitRunner::class)
class CursorToolTest {
    @Mock
    private lateinit var commandManager: CommandManager

    @Mock
    private lateinit var toolPaint: ToolPaint

    @Mock
    private lateinit var workspace: Workspace

    @Mock
    private lateinit var brushToolOptionsView: BrushToolOptionsView

    @Mock
    private lateinit var toolOptionsViewController: ToolOptionsViewController

    @Mock
    private lateinit var contextCallback: ContextCallback
    private lateinit var toolToTest: CursorTool
    private lateinit var idlingResource: CountingIdlingResource

    @get:Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
        idlingResource = launchActivityRule.activity.idlingResource
        IdlingRegistry.getInstance().register(idlingResource)
        val paint = Paint()
        Mockito.`when`(toolPaint.paint).thenReturn(paint)
        Mockito.`when`(workspace.height).thenReturn(1920)
        Mockito.`when`(workspace.width).thenReturn(1080)
        Mockito.`when`(workspace.perspective).thenReturn(Perspective(1080, 1920))
        toolToTest = CursorTool(
            brushToolOptionsView,
            contextCallback, toolOptionsViewController, toolPaint, workspace,
            idlingResource, commandManager, 0
        )
    }

    @After
    fun tearDown() { IdlingRegistry.getInstance().unregister(idlingResource) }

    @Test
    fun testShouldReturnCorrectToolType() {
        val toolType = toolToTest.toolType
        Assert.assertEquals(ToolType.CURSOR, toolType)
    }

    @Test
    fun testShouldActivateCursorOnTapEvent() {
        val point = PointF(5f, 5f)
        Mockito.`when`(workspace.contains(ArgumentMatchers.any(PointF::class.java))).thenReturn(true)
        Assert.assertTrue(toolToTest.handleDown(point))
        Assert.assertTrue(toolToTest.handleUp(point))
        Mockito.verify(commandManager).addCommand(ArgumentMatchers.isA(PointCommand::class.java))
        Assert.assertTrue(toolToTest.toolInDrawMode)
    }

    @Test
    fun testShouldActivateCursorOnTapEventOutsideDrawingSurface() {
        Mockito.`when`(workspace.contains(toolToTest.toolPosition)).thenReturn(true)
        val point = PointF(-5f, -5f)
        Assert.assertTrue(toolToTest.handleDown(point))
        Assert.assertTrue(toolToTest.handleUp(point))
        Mockito.verify(commandManager).addCommand(ArgumentMatchers.isA(PointCommand::class.java))
        Assert.assertTrue(toolToTest.toolInDrawMode)
    }

    @Test
    fun testShouldNotActivateCursorOnTapEvent() {
        val pointDown = PointF(0f, 0f)
        val pointUp = PointF(pointDown.x + MOVE_TOLERANCE + 1, pointDown.y + MOVE_TOLERANCE + 1)

        // +/+
        Assert.assertTrue(toolToTest.handleDown(pointDown))
        Assert.assertTrue(toolToTest.handleUp(pointUp))
        Mockito.verify(commandManager, Mockito.never()).addCommand(ArgumentMatchers.any(Command::class.java))
        Assert.assertFalse(toolToTest.toolInDrawMode)

        // +/0
        pointUp[pointDown.x + MOVE_TOLERANCE + 1] =
            pointDown.y
        Assert.assertTrue(toolToTest.handleDown(pointDown))
        Assert.assertTrue(toolToTest.handleUp(pointUp))
        Mockito.verify(commandManager, Mockito.never()).addCommand(ArgumentMatchers.any(Command::class.java))
        Assert.assertFalse(toolToTest.toolInDrawMode)

        // 0/+
        pointUp[pointDown.x] = pointDown.y + MOVE_TOLERANCE + 1
        Assert.assertTrue(toolToTest.handleDown(pointDown))
        Assert.assertTrue(toolToTest.handleUp(pointUp))
        Mockito.verify(commandManager, Mockito.never()).addCommand(ArgumentMatchers.any(Command::class.java))
        Assert.assertFalse(toolToTest.toolInDrawMode)

        // -/-
        pointUp[pointDown.x - MOVE_TOLERANCE - 1] = pointDown.y - MOVE_TOLERANCE - 1
        Assert.assertTrue(toolToTest.handleDown(pointDown))
        Assert.assertTrue(toolToTest.handleUp(pointUp))
        Mockito.verify(commandManager, Mockito.never()).addCommand(ArgumentMatchers.any(Command::class.java))
        Assert.assertFalse(toolToTest.toolInDrawMode)
    }

    @Test
    fun testShouldMovePathOnUpEvent() {
        Mockito.`when`(workspace.surfaceHeight).thenReturn(1920)
        Mockito.`when`(workspace.surfaceWidth).thenReturn(1080)
        Mockito.`when`(
            workspace.getSurfacePointFromCanvasPoint(ArgumentMatchers.any(PointF::class.java))
        ).thenAnswer { invocation ->
            copyPointF(invocation.getArgument<Any>(0) as PointF)
        }
        val event1 = PointF(0f, 0f)
        val event2 = PointF(MOVE_TOLERANCE, MOVE_TOLERANCE)
        val event3 = PointF(MOVE_TOLERANCE * 2, -MOVE_TOLERANCE)
        val testCursorPosition = PointF()
        val actualCursorPosition = toolToTest.toolPosition
        Assert.assertNotNull(actualCursorPosition)
        testCursorPosition.set(actualCursorPosition)
        val pathStub = PathStub()
        toolToTest.pathToDraw = pathStub
        Assert.assertFalse(toolToTest.toolInDrawMode)

        // e1
        var returnValue = toolToTest.handleDown(event1)
        Assert.assertTrue(returnValue)
        Assert.assertFalse(toolToTest.toolInDrawMode)
        returnValue = toolToTest.handleUp(event1)
        Assert.assertTrue(toolToTest.toolInDrawMode)
        Assert.assertTrue(returnValue)
        Assert.assertEquals(testCursorPosition.x.toDouble(), actualCursorPosition.x.toDouble(), Double.MIN_VALUE)
        Assert.assertEquals(testCursorPosition.y.toDouble(), actualCursorPosition.y.toDouble(), Double.MIN_VALUE)

        // e2
        returnValue = toolToTest.handleMove(event2)
        val vectorCX = event2.x - event1.x
        val vectorCY = event2.y - event1.y
        testCursorPosition[testCursorPosition.x + vectorCX] = testCursorPosition.y + vectorCY
        Assert.assertEquals(testCursorPosition.x.toDouble(), actualCursorPosition.x.toDouble(), Double.MIN_VALUE)
        Assert.assertEquals(testCursorPosition.y.toDouble(), actualCursorPosition.y.toDouble(), Double.MIN_VALUE)
        Assert.assertTrue(toolToTest.toolInDrawMode)
        Assert.assertTrue(returnValue)

        // e3
        returnValue = toolToTest.handleUp(event3)
        Assert.assertTrue(toolToTest.toolInDrawMode)
        Assert.assertTrue(returnValue)
        Assert.assertEquals(testCursorPosition.x.toDouble(), actualCursorPosition.x.toDouble(), Double.MIN_VALUE)
        Assert.assertEquals(testCursorPosition.y.toDouble(), actualCursorPosition.y.toDouble(), Double.MIN_VALUE)
        val stub = pathStub.stub
        Mockito.verify(stub).moveTo(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat())
        Mockito.verify(stub).quadTo(
            ArgumentMatchers.anyFloat(),
            ArgumentMatchers.anyFloat(),
            ArgumentMatchers.anyFloat(),
            ArgumentMatchers.anyFloat()
        )
        Mockito.verify(stub).lineTo(testCursorPosition.x, testCursorPosition.y)
    }

    @Test
    fun testShouldCheckIfColorChangesIfToolIsActive() {
        Mockito.`when`(workspace.contains(toolToTest.toolPosition)).thenReturn(true)
        Mockito.`when`(toolPaint.color).thenReturn(Color.RED)
        Assert.assertFalse(toolToTest.toolInDrawMode)

        val point = PointF(200f, 200f)
        toolToTest.handleDown(point)
        toolToTest.handleUp(point)
        Assert.assertTrue(toolToTest.toolInDrawMode)
        Assert.assertEquals(Color.RED.toLong(), toolToTest.cursorToolSecondaryShapeColor.toLong())
        toolToTest.handleDown(point)
        toolToTest.handleUp(point)
        Assert.assertFalse(toolToTest.toolInDrawMode)
        Assert.assertEquals(Color.LTGRAY.toLong(), toolToTest.cursorToolSecondaryShapeColor.toLong())
        Mockito.`when`(toolPaint.color).thenReturn(Color.GREEN)
        toolToTest.handleDown(point)
        toolToTest.handleUp(point)
        Assert.assertTrue(toolToTest.toolInDrawMode)
        Assert.assertEquals(Color.GREEN.toLong(), toolToTest.cursorToolSecondaryShapeColor.toLong())
        toolToTest.handleDown(point)
        toolToTest.handleUp(point)
        Assert.assertFalse(toolToTest.toolInDrawMode)
        Assert.assertEquals(Color.LTGRAY.toLong(), toolToTest.cursorToolSecondaryShapeColor.toLong())

        // test if color also changes if cursor already active
        toolToTest.handleDown(point)
        toolToTest.handleUp(point)
        Assert.assertTrue(toolToTest.toolInDrawMode)
        Mockito.`when`(toolPaint.color).thenReturn(Color.CYAN)
        toolToTest.changePaintColor(Color.CYAN)
        Assert.assertEquals(Color.CYAN.toLong(), toolToTest.cursorToolSecondaryShapeColor.toLong())
    }

    companion object {
        private const val MOVE_TOLERANCE = 0.1f
        private fun copyPointF(point: PointF): PointF = PointF(point.x, point.y)
    }
}
