/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2021 The Catrobat Team
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

import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import kotlinx.coroutines.isActive
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.command.implementation.SprayCommand
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.Tool
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.implementation.STROKE_25
import org.catrobat.paintroid.tools.implementation.SprayTool
import org.catrobat.paintroid.tools.options.SprayToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

class SprayToolTest {
    private val toolPaint = Mockito.mock(ToolPaint::class.java)
    private val commandManager = Mockito.mock(CommandManager::class.java)
    private val workspace = Mockito.mock(Workspace::class.java)
    private val sprayToolOptionsView = Mockito.mock(SprayToolOptionsView::class.java)
    private val toolOptionsViewController =
        Mockito.mock(ToolOptionsViewController::class.java)
    private val contextCallback = Mockito.mock(ContextCallback::class.java)
    private lateinit var tool: SprayTool

    @Before
    fun setUp() {
        val paint = Paint()
        paint.color = Color.BLACK
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = STROKE_25

        Mockito.`when`(toolPaint.paint).thenReturn(paint)
        Mockito.`when`(workspace.width).thenReturn(200)
        Mockito.`when`(workspace.height).thenReturn(300)
        tool = SprayTool(
            sprayToolOptionsView,
            contextCallback,
            toolOptionsViewController,
            toolPaint,
            workspace,
            commandManager,
            0
        )
    }

    @After
    fun tearDown() {
        tool.resetInternalState(Tool.StateChange.ALL)
    }

    @Test
    fun coroutineStoppedOnHandleUp() {
        val event = PointF(0f, 0f)
        tool.handleDown(event)
        Assert.assertTrue(tool.sprayToolScope.isActive)
        tool.handleUp(event)
        Assert.assertFalse(tool.sprayToolScope.isActive)
    }

    @Test
    fun coroutineStoppedOnResetInternalState() {
        val event = PointF(0f, 0f)
        tool.handleDown(event)
        Assert.assertTrue(tool.sprayToolScope.isActive)
        tool.resetInternalState(Tool.StateChange.ALL)
        Assert.assertFalse(tool.sprayToolScope.isActive)
    }

    @Test
    fun testSprayCommandCreated() {
        val event = PointF(0f, 0f)
        tool.handleDown(event)
        Assert.assertTrue(tool.sprayToolScope.isActive)
        tool.handleUp(event)
        val argument = ArgumentCaptor.forClass(SprayCommand::class.java)
        Mockito.verify(commandManager).addCommand(argument.capture())
    }

    @Test
    fun testShouldReturnCorrectToolType() {
        Assert.assertEquals(ToolType.SPRAY, tool.toolType)
    }

    @Test
    fun testSprayedPointsClearedAfterCommandRun() {
        val event = PointF(0f, 0f)
        tool.handleDown(event)
        tool.resetInternalState(Tool.StateChange.ALL)
        Assert.assertTrue(tool.sprayedPoints.isEmpty())
    }

    @Test
    fun testSprayActiveTrueOnHandleDown() {
        val event = PointF(0f, 0f)
        tool.handleDown(event)
        Assert.assertTrue(tool.sprayActive)
    }

    @Test
    fun testSprayActiveFalseAfterCommandRun() {
        val event = PointF(0f, 0f)
        tool.handleDown(event)
        tool.resetInternalState(Tool.StateChange.ALL)
        Assert.assertFalse(tool.sprayActive)
    }

    @Test
    fun testHandleMoveDoesNothingButUpdateCoordinate() {
        val event = PointF(0f, 0f)
        tool.handleMove(event)
        Mockito.verify(commandManager, Mockito.never())
            .addCommand(ArgumentMatchers.any(Command::class.java))
        Assert.assertTrue(tool.sprayToolScope.isActive)
        Assert.assertFalse(tool.sprayActive)
    }
}
