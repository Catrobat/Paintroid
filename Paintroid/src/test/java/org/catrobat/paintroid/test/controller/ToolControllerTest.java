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

package org.catrobat.paintroid.test.controller;

import android.graphics.Color;
import android.graphics.Paint;

import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.controller.DefaultToolController;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolFactory;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolReference;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.options.ToolOptionsViewController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.catrobat.paintroid.tools.Tool.StateChange.NEW_IMAGE_LOADED;
import static org.catrobat.paintroid.tools.Tool.StateChange.RESET_INTERNAL_STATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ToolControllerTest {
	@Mock
	public ToolReference toolReference;
	@Mock
	public ToolOptionsViewController toolOptionsViewController;
	@Mock
	public ToolFactory toolFactory;
	@Mock
	public CommandManager commandManager;
	@Mock
	public Workspace workspace;
	@Mock
	public ToolPaint toolPaint;
	@Mock
	public ContextCallback contextCallback;

	@InjectMocks
	public DefaultToolController toolController;

	@Test
	public void testSetUp() {
		verifyZeroInteractions(toolReference, toolOptionsViewController, toolFactory, commandManager, workspace, toolPaint,
				contextCallback);
		assertNotNull(toolController);
	}

	@Test
	public void testIsBrushDefaultTool() {
		Tool tool = mock(Tool.class);
		when(tool.getToolType()).thenReturn(ToolType.BRUSH);
		when(toolReference.get()).thenReturn(tool);

		assertTrue(toolController.isDefaultTool());
	}

	@Test
	public void testAllOtherToolsAreNotDefault() {
		Tool tool = mock(Tool.class);
		when(tool.getToolType()).thenReturn(
				ToolType.PIPETTE,
				ToolType.UNDO,
				ToolType.REDO,
				ToolType.FILL,
				ToolType.STAMP,
				ToolType.LINE,
				ToolType.CURSOR,
				ToolType.IMPORTPNG,
				ToolType.TRANSFORM,
				ToolType.ERASER,
				ToolType.SHAPE,
				ToolType.TEXT,
				ToolType.LAYER,
				ToolType.COLORCHOOSER,
				ToolType.HAND
		);
		when(toolReference.get()).thenReturn(tool);

		assertFalse(toolController.isDefaultTool());
		assertFalse(toolController.isDefaultTool());
		assertFalse(toolController.isDefaultTool());
		assertFalse(toolController.isDefaultTool());
		assertFalse(toolController.isDefaultTool());
		assertFalse(toolController.isDefaultTool());
		assertFalse(toolController.isDefaultTool());
		assertFalse(toolController.isDefaultTool());
		assertFalse(toolController.isDefaultTool());
		assertFalse(toolController.isDefaultTool());
		assertFalse(toolController.isDefaultTool());
		assertFalse(toolController.isDefaultTool());
		assertFalse(toolController.isDefaultTool());
		assertFalse(toolController.isDefaultTool());
		assertFalse(toolController.isDefaultTool());
	}

	@Test
	public void testHideToolOptionsCallsToolOptionsViewController() {
		toolController.hideToolOptionsView();

		verify(toolOptionsViewController).hide();
		verifyNoMoreInteractions(toolOptionsViewController);
	}

	@Test
	public void testToolOptionsViewControllerWhenOptionsVisibleReturnsTrue() {
		when(toolOptionsViewController.isVisible()).thenReturn(true);

		assertTrue(toolController.toolOptionsViewVisible());
	}

	@Test
	public void testToolOptionsViewControllerWhenOptionsNotVisibleReturnsFalse() {
		assertFalse(toolController.toolOptionsViewVisible());
	}

	@Test
	public void testResetToolInternalStateCallsResetInternalState() {
		Tool tool = mock(Tool.class);
		when(toolReference.get()).thenReturn(tool);

		toolController.resetToolInternalState();

		verify(tool).resetInternalState(RESET_INTERNAL_STATE);
	}

	@Test
	public void testResetToolInternalStateOnImageLoadedCallsResetInternalState() {
		Tool tool = mock(Tool.class);
		when(toolReference.get()).thenReturn(tool);

		toolController.resetToolInternalStateOnImageLoaded();

		verify(tool).resetInternalState(NEW_IMAGE_LOADED);
	}

	@Test
	public void testGetToolColorReturnsColor() {
		Tool tool = mock(Tool.class);
		Paint paint = mock(Paint.class);
		when(toolReference.get()).thenReturn(tool);
		when(tool.getDrawPaint()).thenReturn(paint);
		when(paint.getColor()).thenReturn(Color.CYAN);

		assertEquals(Color.CYAN, toolController.getToolColor());
	}

	@Test
	public void testGetToolTypeReturnsToolType() {
		Tool tool = mock(Tool.class);
		when(toolReference.get()).thenReturn(tool);
		when(tool.getToolType()).thenReturn(ToolType.BRUSH, ToolType.ERASER);

		assertEquals(ToolType.BRUSH, toolController.getToolType());
		assertEquals(ToolType.ERASER, toolController.getToolType());
	}

	@Test
	public void testDisableToolOptionsCallsOptions() {
		toolController.disableToolOptionsView();

		verify(toolOptionsViewController).disable();
	}

	@Test
	public void testEnableToolOptionsCallsOptions() {
		toolController.enableToolOptionsView();

		verify(toolOptionsViewController).enable();
	}

	@Test
	public void testToggleToolOptionsWhenNotVisibleThenShowOptions() {
		toolController.toggleToolOptionsView();

		verify(toolOptionsViewController).show();
	}

	@Test
	public void testToggleToolOptionsWhenVisibleThenHideOptions() {
		when(toolOptionsViewController.isVisible()).thenReturn(true);

		toolController.toggleToolOptionsView();

		verify(toolOptionsViewController).hide();
	}

	@Test
	public void testHasToolOptions() {
		Tool mock = mock(Tool.class);
		when(toolReference.get()).thenReturn(mock);
		when(mock.getToolType()).thenReturn(ToolType.BRUSH, ToolType.IMPORTPNG);

		assertTrue(toolController.hasToolOptionsView());
		assertFalse(toolController.hasToolOptionsView());
	}
}
