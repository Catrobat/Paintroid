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
package org.catrobat.paintroid.test.junit.tools

import android.graphics.Paint
import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.Color
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.command.implementation.SmudgePathCommand
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.Tool
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.implementation.SmudgeTool
import org.catrobat.paintroid.tools.options.SmudgeToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito

class SmudgeToolTest {
    private val toolPaint = Mockito.mock(ToolPaint::class.java)
    private val commandManager = Mockito.mock(CommandManager::class.java)
    private val workspace = Mockito.mock(Workspace::class.java)
    private val smudgeToolOptionsView = Mockito.mock(SmudgeToolOptionsView::class.java)
    private val toolOptionsViewController = Mockito.mock(ToolOptionsViewController::class.java)
    private val contextCallback = Mockito.mock(ContextCallback::class.java)
    private lateinit var tool: SmudgeTool

    @Before
    fun setUp() {
        Mockito.`when`(toolPaint.strokeWidth).thenReturn(100f)
        Mockito.`when`(toolPaint.strokeCap).thenReturn(Paint.Cap.ROUND)

        val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        bitmap.setPixel(100, 100, Color.GREEN)
        Mockito.`when`(workspace.bitmapOfCurrentLayer).thenReturn(bitmap)

        tool = SmudgeTool(
            smudgeToolOptionsView,
            contextCallback,
            toolOptionsViewController,
            toolPaint,
            workspace,
            commandManager
        )
    }

    @After
    fun tearDown() {
        tool.resetInternalState(Tool.StateChange.ALL)
    }

    @Test
    fun testShouldReturnCorrectToolType() {
        Assert.assertEquals(ToolType.SMUDGE, tool.toolType)
    }

    @Test
    fun testNotExecutingSmudgeWhenBitmapHasNoColor() {
        Mockito.`when`(workspace.bitmapOfCurrentLayer)
            .thenReturn(Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888))
        val event = PointF(100f, 100f)
        Assert.assertFalse(tool.handleDown(event))
        Assert.assertFalse(tool.handleMove(event))
        Assert.assertFalse(tool.handleUp(event))
    }

    @Test
    fun testExecutingSmudgeWhenBitmapHasColor() {
        val event = PointF(100f, 100f)
        Assert.assertTrue(tool.handleDown(event))
        Assert.assertTrue(tool.handleMove(event))
        Assert.assertTrue(tool.handleUp(event))
    }

    @Test
    fun testSmudgePathCommandCreated() {
        val event = PointF(100f, 100f)
        tool.handleDown(event)
        val event2 = PointF(110f, 110f)
        tool.handleMove(event2)
        tool.handleUp(event2)
        val argument = ArgumentCaptor.forClass(SmudgePathCommand::class.java)
        Mockito.verify(commandManager).addCommand(argument.capture())
    }

    @Test
    fun testPointsArrayIsClearedAfterCommandRun() {
        val event = PointF(100f, 100f)
        tool.handleDown(event)
        val event2 = PointF(110f, 110f)
        tool.handleMove(event2)
        tool.handleUp(event2)
        Assert.assertTrue(tool.pointArray.isEmpty())
    }

    @Test
    fun testPressureUpdatedCorrectly() {
        tool.updatePressure(40)
        val pressure = tool.maxPressure
        tool.updatePressure(20)
        Assert.assertNotEquals(pressure, tool.maxPressure)
    }

    @Test
    fun testDragUpdatedCorrectly() {
        tool.updateDrag(80)
        val minSize = tool.minSmudgeSize
        tool.updateDrag(40)
        Assert.assertNotEquals(minSize, tool.minSmudgeSize)
    }
}
