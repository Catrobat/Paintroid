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

package org.catrobat.paintroid.test.controller

import android.graphics.Color
import android.graphics.Paint
import androidx.test.espresso.idling.CountingIdlingResource
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.controller.DefaultToolController
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.Tool
import org.catrobat.paintroid.tools.Tool.StateChange
import org.catrobat.paintroid.tools.ToolFactory
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner.Silent::class)
class ToolControllerTest {
    @Mock
    lateinit var toolReference: ToolReference

    @Mock
    lateinit var toolOptionsViewController: ToolOptionsViewController

    @Mock
    lateinit var toolFactory: ToolFactory

    @Mock
    lateinit var commandManager: CommandManager

    @Mock
    lateinit var workspace: Workspace

    @Mock
    lateinit var toolPaint: ToolPaint

    @Mock
    lateinit var contextCallback: ContextCallback
    lateinit var idlingResource: CountingIdlingResource
    private lateinit var toolController: DefaultToolController

    @Before
    fun init() {
        idlingResource = CountingIdlingResource("name")
        toolController = DefaultToolController(
            toolReference,
            toolOptionsViewController,
            toolFactory,
            commandManager,
            workspace,
            idlingResource,
            toolPaint,
            contextCallback
        )
    }

    @Test
    fun testSetUp() {
        Mockito.verifyZeroInteractions(
            toolReference, toolOptionsViewController, toolFactory,
            commandManager, workspace, toolPaint, contextCallback
        )
        Assert.assertNotNull(toolController)
    }

    @Test
    fun testIsBrushDefaultTool() {
        val tool = Mockito.mock(Tool::class.java)
        Mockito.`when`(tool.toolType).thenReturn(ToolType.BRUSH)
        Mockito.`when`(toolReference.tool).thenReturn(tool)
        Assert.assertTrue(toolController.isDefaultTool)
    }

    @Test
    fun testAllOtherToolsAreNotDefault() {
        val tool = Mockito.mock(Tool::class.java)
        Mockito.`when`(tool.toolType).thenReturn(
            ToolType.PIPETTE,
            ToolType.UNDO,
            ToolType.REDO,
            ToolType.FILL,
            ToolType.CLIPBOARD,
            ToolType.LINE,
            ToolType.CURSOR,
            ToolType.IMPORTPNG,
            ToolType.TRANSFORM,
            ToolType.ERASER,
            ToolType.SHAPE,
            ToolType.TEXT,
            ToolType.LAYER,
            ToolType.COLORCHOOSER,
            ToolType.HAND,
            ToolType.WATERCOLOR,
            ToolType.SMUDGE
        )
        Mockito.`when`(toolReference.tool).thenReturn(tool)

        Assert.assertFalse(toolController.isDefaultTool)
        Assert.assertFalse(toolController.isDefaultTool)
        Assert.assertFalse(toolController.isDefaultTool)
        Assert.assertFalse(toolController.isDefaultTool)
        Assert.assertFalse(toolController.isDefaultTool)
        Assert.assertFalse(toolController.isDefaultTool)
        Assert.assertFalse(toolController.isDefaultTool)
        Assert.assertFalse(toolController.isDefaultTool)
        Assert.assertFalse(toolController.isDefaultTool)
        Assert.assertFalse(toolController.isDefaultTool)
        Assert.assertFalse(toolController.isDefaultTool)
        Assert.assertFalse(toolController.isDefaultTool)
        Assert.assertFalse(toolController.isDefaultTool)
        Assert.assertFalse(toolController.isDefaultTool)
        Assert.assertFalse(toolController.isDefaultTool)
        Assert.assertFalse(toolController.isDefaultTool)
        Assert.assertFalse(toolController.isDefaultTool)
    }

    @Test
    fun testHideToolOptionsCallsToolOptionsViewController() {
        toolController.hideToolOptionsView()
        Mockito.verify(toolOptionsViewController).hide()
        Mockito.verifyNoMoreInteractions(toolOptionsViewController)
    }

    @Test
    fun testToolOptionsViewControllerWhenOptionsVisibleReturnsTrue() {
        Mockito.`when`(toolOptionsViewController.isVisible).thenReturn(true)
        Assert.assertTrue(toolController.toolOptionsViewVisible())
    }

    @Test
    fun testToolOptionsViewControllerWhenOptionsNotVisibleReturnsFalse() =
        Assert.assertFalse(toolController.toolOptionsViewVisible())

    @Test
    fun testResetToolInternalStateCallsResetInternalState() {
        val tool = Mockito.mock(Tool::class.java)
        Mockito.`when`(toolReference.tool).thenReturn(tool)
        toolController.resetToolInternalState()
        Mockito.verify(tool).resetInternalState(StateChange.RESET_INTERNAL_STATE)
    }

    @Test
    fun testResetToolInternalStateOnImageLoadedCallsResetInternalState() {
        val tool = Mockito.mock(Tool::class.java)
        Mockito.`when`(toolReference.tool).thenReturn(tool)
        toolController.resetToolInternalStateOnImageLoaded()
        Mockito.verify(tool).resetInternalState(StateChange.NEW_IMAGE_LOADED)
    }

    @Test
    fun testGetToolColorReturnsColor() {
        val tool = Mockito.mock(Tool::class.java)
        val paint = Mockito.mock(Paint::class.java)
        Mockito.`when`(toolReference.tool).thenReturn(tool)
        Mockito.`when`(tool.drawPaint).thenReturn(paint)
        Mockito.`when`(paint.color).thenReturn(Color.CYAN)
        Assert.assertEquals(Color.CYAN, toolController.toolColor)
    }

    @Test
    fun testGetToolTypeReturnsToolType() {
        val tool = Mockito.mock(Tool::class.java)
        Mockito.`when`(toolReference.tool).thenReturn(tool)
        Mockito.`when`(tool.toolType).thenReturn(ToolType.BRUSH, ToolType.ERASER)
        Assert.assertEquals(ToolType.BRUSH, toolController.toolType)
        Assert.assertEquals(ToolType.ERASER, toolController.toolType)
    }

    @Test
    fun testDisableToolOptionsCallsOptions() {
        toolController.disableToolOptionsView()
        Mockito.verify(toolOptionsViewController).disable()
    }

    @Test
    fun testEnableToolOptionsCallsOptions() {
        toolController.enableToolOptionsView()
        Mockito.verify(toolOptionsViewController).enable()
    }

    @Test
    fun testToggleToolOptionsWhenNotVisibleThenShowOptions() {
        toolController.toggleToolOptionsView()
        Mockito.verify(toolOptionsViewController).show()
    }

    @Test
    fun testToggleToolOptionsWhenVisibleThenHideOptions() {
        Mockito.`when`(toolOptionsViewController.isVisible).thenReturn(true)
        toolController.toggleToolOptionsView()
        Mockito.verify(toolOptionsViewController).hide()
    }

    @Test
    fun testHasToolOptions() {
        val mock = Mockito.mock(Tool::class.java)
        Mockito.`when`(toolReference.tool).thenReturn(mock)
        Mockito.`when`(mock.toolType).thenReturn(ToolType.BRUSH, ToolType.IMPORTPNG)
        Assert.assertTrue(toolController.hasToolOptionsView())
        Assert.assertFalse(toolController.hasToolOptionsView())
    }
}
