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
import android.graphics.Bitmap.Config;
import android.graphics.PointF;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.ViewConfiguration;

import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.implementation.StampTool;
import org.catrobat.paintroid.tools.options.StampToolOptionsView;
import org.catrobat.paintroid.tools.options.ToolOptionsVisibilityController;
import org.catrobat.paintroid.ui.Perspective;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StampToolTest {
	@Mock
	private ToolPaint toolPaint;
	@Mock
	private CommandManager commandManager;
	@Mock
	private Workspace workspace;
	@Mock
	private StampToolOptionsView stampToolOptions;
	@Mock
	private ToolOptionsVisibilityController toolOptionsViewController;
	@Mock
	private ContextCallback contextCallback;
	@Mock
	private DisplayMetrics displayMetrics;

	private StampTool tool;

	@Before
	public void setUp() {
		when(contextCallback.getDisplayMetrics()).thenReturn(displayMetrics);
		displayMetrics.widthPixels = 200;
		displayMetrics.heightPixels = 300;
		when(workspace.getScale()).thenReturn(1f);
		when(workspace.getWidth()).thenReturn(200);
		when(workspace.getHeight()).thenReturn(300);
		when(workspace.getPerspective()).thenReturn(new Perspective(200, 300));
		when(workspace.getCanvasPointFromSurfacePoint(any(PointF.class))).then(new Answer<PointF>() {
			@Override
			public PointF answer(InvocationOnMock invocation) {
				return invocation.getArgument(0);
			}
		});

		tool = new StampTool(stampToolOptions, contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager);
	}

	@Test
	public void testLongClickResetsToolPosition() throws InterruptedException {
		when(workspace.getBitmapOfCurrentLayer()).thenReturn(
				Bitmap.createBitmap(200, 200, Config.ARGB_8888));

		final float initialX = tool.toolPosition.x;
		final float initialY = tool.toolPosition.y;

		InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
			@Override
			public void run() {
				tool.handleDown(new PointF(initialX, initialY));
				tool.handleMove(new PointF(initialX + 2, initialY + 3));
			}
		});

		assertEquals(initialX + 2, tool.toolPosition.x, Float.MIN_VALUE);
		assertEquals(initialY + 3, tool.toolPosition.y, Float.MIN_VALUE);

		Thread.sleep(ViewConfiguration.getLongPressTimeout() + 50);

		assertEquals(initialX + 2, tool.toolPosition.x, Float.MIN_VALUE);
		assertEquals(initialY + 3, tool.toolPosition.y, Float.MIN_VALUE);
	}

	@Test
	public void testShouldReturnCorrectToolType() {
		assertEquals(ToolType.STAMP, tool.getToolType());
	}

	@Test
	public void testToolClicksOnTouchDownPosition() {
		Looper.prepare();

		float initialToolPositionX = tool.toolPosition.x;
		float initialToolPositionY = tool.toolPosition.y;

		tool.handleDown(new PointF(initialToolPositionX, initialToolPositionY));
		tool.handleMove(new PointF(initialToolPositionX + 9, initialToolPositionY + 9));
		tool.handleUp(new PointF(initialToolPositionX + 9, initialToolPositionY + 9));

		assertEquals(tool.toolPosition.x, initialToolPositionX, 0);
		assertEquals(tool.toolPosition.y, initialToolPositionY, 0);
	}
}
