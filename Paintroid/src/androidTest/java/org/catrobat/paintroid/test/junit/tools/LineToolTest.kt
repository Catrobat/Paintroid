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

import android.graphics.PointF
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.implementation.LineTool
import org.catrobat.paintroid.tools.options.BrushToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsVisibilityController
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class LineToolTest {
    private val toolPaint = Mockito.mock(ToolPaint::class.java)
    private val commandManager = Mockito.mock(CommandManager::class.java)
    private val workspace = Mockito.mock(Workspace::class.java)
    private val brushToolOptions = Mockito.mock(BrushToolOptionsView::class.java)
    private val toolOptionsController = Mockito.mock(ToolOptionsVisibilityController::class.java)
    private val contextCallback = Mockito.mock(ContextCallback::class.java)
    private lateinit var tool: LineTool

    @Before
    fun setUp() {
        tool = LineTool(
            brushToolOptions,
            contextCallback,
            toolOptionsController,
            toolPaint,
            workspace,
            commandManager
        )
    }

    @Test
    fun testInternalStateGetsResetWithPathOuterWorkspace() {
        tool.handleDown(PointF(-1f, -1f))
        tool.handleUp(PointF(-2f, -2f))
        Assert.assertEquals(tool.currentCoordinate, null)
        Assert.assertEquals(tool.initialEventCoordinate, null)
    }

    @Test
    fun testInternalStateGetsResetWithPathInWorkspace() {
        tool.handleDown(PointF(1f, 1f))
        tool.handleUp(PointF(2f, 2f))
        Assert.assertEquals(tool.currentCoordinate, null)
        Assert.assertEquals(tool.initialEventCoordinate, null)
    }

    @Test
    fun testInternalStateHandMoveNullPoint() {
        tool.handleDown(PointF(1f, 1f))
        tool.handleMove(null)
        Assert.assertEquals(tool.currentCoordinate, null)
        val point = PointF(2f, 2f)
        tool.handleMove(point)
        Assert.assertEquals(tool.currentCoordinate, point)
    }
}
