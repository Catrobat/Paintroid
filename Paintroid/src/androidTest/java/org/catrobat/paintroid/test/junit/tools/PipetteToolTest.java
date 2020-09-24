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

package org.catrobat.paintroid.test.junit.tools;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;

import org.catrobat.paintroid.colorpicker.OnColorPickedListener;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.implementation.PipetteTool;
import org.catrobat.paintroid.tools.options.ToolOptionsVisibilityController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class PipetteToolTest {
	private static final int X_COORDINATE_RED = 1;
	private static final int X_COORDINATE_GREEN = 3;
	private static final int X_COORDINATE_BLUE = 5;
	private static final int X_COORDINATE_PART_TRANSPARENT = 7;

	@Mock
	private CommandManager commandManager;
	@Mock
	private OnColorPickedListener listener;
	@Mock
	private ToolPaint toolPaint;
	@Mock
	private Workspace workspace;
	@Mock
	private ToolOptionsVisibilityController toolOptionsViewController;
	@Mock
	private ContextCallback contextCallback;

	private PipetteTool toolToTest;

	@Before
	public void setUp() {
		Bitmap bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
		bitmap.setPixel(X_COORDINATE_RED, 0, Color.RED);
		bitmap.setPixel(X_COORDINATE_GREEN, 0, Color.GREEN);
		bitmap.setPixel(X_COORDINATE_BLUE, 0, Color.BLUE);
		bitmap.setPixel(X_COORDINATE_PART_TRANSPARENT, 0, 0xAAAAAAAA);

		when(workspace.getBitmapOfAllLayers()).thenReturn(bitmap);
		when(workspace.contains(any(PointF.class))).thenAnswer(new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock invocation) {
				PointF argument = invocation.getArgument(0);
				return argument.x >= 0 && argument.y >= 0 && argument.x < 10 && argument.y < 10;
			}
		});

		toolToTest = new PipetteTool(contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager, listener);
	}

	@Test
	public void testHandleDown() {
		toolToTest.handleDown(new PointF(X_COORDINATE_RED, 0));
		toolToTest.handleMove(new PointF(X_COORDINATE_PART_TRANSPARENT, 0));

		InOrder inOrderToolPaint = inOrder(toolPaint);
		inOrderToolPaint.verify(toolPaint).setColor(Color.RED);
		inOrderToolPaint.verify(toolPaint).setColor(0xAAAAAAAA);

		InOrder inOrderListener = inOrder(listener);
		inOrderListener.verify(listener).colorChanged(Color.RED);
		inOrderListener.verify(listener).colorChanged(0xAAAAAAAA);
	}

	@Test
	public void testHandleMove() {
		toolToTest.handleDown(new PointF(X_COORDINATE_RED, 0));
		toolToTest.handleMove(new PointF(X_COORDINATE_RED + 1, 0));
		toolToTest.handleMove(new PointF(X_COORDINATE_GREEN, 0));
		toolToTest.handleMove(new PointF(X_COORDINATE_PART_TRANSPARENT, 0));

		InOrder inOrderToolPaint = inOrder(toolPaint);
		inOrderToolPaint.verify(toolPaint).setColor(Color.RED);
		inOrderToolPaint.verify(toolPaint).setColor(Color.TRANSPARENT);
		inOrderToolPaint.verify(toolPaint).setColor(Color.GREEN);
		inOrderToolPaint.verify(toolPaint).setColor(0xAAAAAAAA);

		InOrder inOrderListener = inOrder(listener);
		inOrderListener.verify(listener).colorChanged(Color.RED);
		inOrderListener.verify(listener).colorChanged(Color.TRANSPARENT);
		inOrderListener.verify(listener).colorChanged(Color.GREEN);
		inOrderListener.verify(listener).colorChanged(0xAAAAAAAA);
	}

	@Test
	public void testHandleUp() {
		toolToTest.handleUp(new PointF(X_COORDINATE_BLUE, 0));
		toolToTest.handleUp(new PointF(X_COORDINATE_PART_TRANSPARENT, 0));

		InOrder inOrderToolPaint = Mockito.inOrder(toolPaint);
		inOrderToolPaint.verify(toolPaint).setColor(Color.BLUE);
		inOrderToolPaint.verify(toolPaint).setColor(0xAAAAAAAA);

		InOrder inOrderListener = Mockito.inOrder(listener);
		inOrderListener.verify(listener).colorChanged(Color.BLUE);
		inOrderListener.verify(listener).colorChanged(0xAAAAAAAA);
	}

	@Test
	public void testShouldReturnCorrectToolType() {
		assertThat(toolToTest.getToolType(), is(ToolType.PIPETTE));
	}

	@Test
	public void testShouldReturnCorrectColorForForTopButtonIfColorIsTransparent() {
		toolToTest.handleUp(new PointF(0, 0));

		verify(toolPaint).setColor(Color.TRANSPARENT);
	}

	@Test
	public void testShouldReturnCorrectColorForForTopButtonIfColorIsRed() {
		toolToTest.handleUp(new PointF(X_COORDINATE_RED, 0));

		verify(toolPaint).setColor(Color.RED);
	}
}
