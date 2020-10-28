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

package org.catrobat.paintroid.test.listener;

import android.graphics.Point;
import android.graphics.PointF;
import android.os.Handler;

import org.catrobat.paintroid.listener.DrawingSurfaceListener.AutoScrollTask;
import org.catrobat.paintroid.listener.DrawingSurfaceListener.AutoScrollTaskCallback;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.catrobat.paintroid.test.utils.PointFAnswer.setPointFTo;
import static org.catrobat.paintroid.test.utils.PointFMatcher.pointFEquals;
import static org.catrobat.paintroid.tools.ToolType.FILL;
import static org.catrobat.paintroid.tools.ToolType.PIPETTE;
import static org.catrobat.paintroid.tools.ToolType.TRANSFORM;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AutoScrollTaskTest {

	@Mock
	private Handler handler;

	@Mock
	private AutoScrollTaskCallback callback;

	@InjectMocks
	private AutoScrollTask autoScrollTask;

	@Test
	public void testSetUp() {
		verifyZeroInteractions(handler, callback);
		assertFalse(autoScrollTask.isRunning());
	}

	@Test
	public void testRun() {
		Point autoScrollDirection = mock(Point.class);
		when(callback.getToolAutoScrollDirection(anyFloat(), anyFloat(), anyInt(), anyInt()))
				.thenReturn(autoScrollDirection);

		autoScrollTask.run();

		verify(callback).getToolAutoScrollDirection(anyFloat(), anyFloat(), anyInt(), anyInt());
		verify(handler).postDelayed(eq(autoScrollTask), anyLong());
	}

	@Test
	public void testRunAutoScrollLeft() {
		Point autoScrollDirection = mock(Point.class);
		autoScrollDirection.x = -1;
		when(callback.getToolAutoScrollDirection(3f, 5f, 39, 42))
				.thenReturn(autoScrollDirection);
		when(callback.isPointOnCanvas(7, 11)).thenReturn(true);
		setPointFTo(7f, 11f).when(callback).convertToCanvasFromSurface(pointFEquals(3f, 5f));

		autoScrollTask.setEventPoint(3f, 5f);
		autoScrollTask.setViewDimensions(39, 42);
		autoScrollTask.run();

		verify(callback).translatePerspective(-1 * 2f, 0);
		verify(callback).handleToolMove(pointFEquals(7f, 11f));
		verify(callback).refreshDrawingSurface();

		verify(handler).postDelayed(eq(autoScrollTask), anyLong());
	}

	@Test
	public void testRunAutoScrollLeftWhenNotOnCanvas() {
		Point autoScrollDirection = mock(Point.class);
		autoScrollDirection.x = -1;
		when(callback.getToolAutoScrollDirection(3f, 5f, 39, 42))
				.thenReturn(autoScrollDirection);
		setPointFTo(7f, 11f).when(callback).convertToCanvasFromSurface(pointFEquals(3f, 5f));

		autoScrollTask.setEventPoint(3f, 5f);
		autoScrollTask.setViewDimensions(39, 42);
		autoScrollTask.run();

		verify(callback, never()).translatePerspective(anyFloat(), anyFloat());
		verify(callback, never()).handleToolMove(any(PointF.class));
		verify(callback, never()).refreshDrawingSurface();

		verify(handler).postDelayed(eq(autoScrollTask), anyLong());
	}

	@Test
	public void testRunAutoScrollUp() {
		Point autoScrollDirection = mock(Point.class);
		autoScrollDirection.y = -1;
		when(callback.getToolAutoScrollDirection(3f, 5f, 39, 42))
				.thenReturn(autoScrollDirection);
		when(callback.isPointOnCanvas(7, 11)).thenReturn(true);
		setPointFTo(7f, 11f).when(callback).convertToCanvasFromSurface(pointFEquals(3f, 5f));

		autoScrollTask.setEventPoint(3f, 5f);
		autoScrollTask.setViewDimensions(39, 42);
		autoScrollTask.run();

		verify(callback).translatePerspective(0, -1 * 2f);
		verify(callback).handleToolMove(pointFEquals(7f, 11f));
		verify(callback).refreshDrawingSurface();

		verify(handler).postDelayed(eq(autoScrollTask), anyLong());
	}

	@Test
	public void testRunAutoScrollUpWhenNotOnCanvas() {
		Point autoScrollDirection = mock(Point.class);
		autoScrollDirection.y = -1;
		when(callback.getToolAutoScrollDirection(3f, 5f, 39, 42))
				.thenReturn(autoScrollDirection);
		setPointFTo(7f, 11f).when(callback).convertToCanvasFromSurface(pointFEquals(3f, 5f));

		autoScrollTask.setEventPoint(3f, 5f);
		autoScrollTask.setViewDimensions(39, 42);
		autoScrollTask.run();

		verify(callback, never()).translatePerspective(anyFloat(), anyFloat());
		verify(callback, never()).handleToolMove(any(PointF.class));
		verify(callback, never()).refreshDrawingSurface();

		verify(handler).postDelayed(eq(autoScrollTask), anyLong());
	}

	@Test
	public void testStop() {
		autoScrollTask.stop();
		verifyZeroInteractions(handler, callback);

		assertFalse(autoScrollTask.isRunning());
	}

	@Test
	public void testStart() {
		autoScrollTask.setEventPoint(3f, 5f);
		autoScrollTask.setViewDimensions(39, 42);

		Point autoScrollDirection = mock(Point.class);
		when(callback.getToolAutoScrollDirection(anyFloat(), anyFloat(), anyInt(), anyInt()))
				.thenReturn(autoScrollDirection);

		autoScrollTask.start();

		verify(handler).postDelayed(eq(autoScrollTask), anyLong());
		assertTrue(autoScrollTask.isRunning());
	}

	@Test(expected = IllegalStateException.class)
	public void testStartWhenAlreadyRunning() {
		autoScrollTask.setEventPoint(3f, 5f);
		autoScrollTask.setViewDimensions(39, 42);

		Point autoScrollDirection = mock(Point.class);
		when(callback.getToolAutoScrollDirection(anyFloat(), anyFloat(), anyInt(), anyInt()))
				.thenReturn(autoScrollDirection);

		autoScrollTask.start();
		autoScrollTask.start();
	}

	@Test(expected = IllegalStateException.class)
	public void testStartWithZeroWidth() {
		autoScrollTask.setViewDimensions(0, 42);
		autoScrollTask.start();
	}

	@Test(expected = IllegalStateException.class)
	public void testStartWithZeroHeight() {
		autoScrollTask.setViewDimensions(42, 0);
		autoScrollTask.start();
	}

	@Test
	public void testStartIgnoredTools() {
		when(callback.getCurrentToolType()).thenReturn(PIPETTE, FILL, TRANSFORM);

		autoScrollTask.setEventPoint(3f, 5f);
		autoScrollTask.setViewDimensions(39, 42);

		autoScrollTask.start();
		autoScrollTask.start();
		autoScrollTask.start();

		assertFalse(autoScrollTask.isRunning());
		verifyZeroInteractions(handler);
	}

	@Test
	public void testStopAfterStart() {
		autoScrollTask.setEventPoint(3f, 5f);
		autoScrollTask.setViewDimensions(39, 42);

		Point autoScrollDirection = mock(Point.class);
		when(callback.getToolAutoScrollDirection(anyFloat(), anyFloat(), anyInt(), anyInt()))
				.thenReturn(autoScrollDirection);

		autoScrollTask.start();
		autoScrollTask.stop();

		verify(handler).postDelayed(eq(autoScrollTask), anyLong());
		verify(handler).removeCallbacks(autoScrollTask);
		assertFalse(autoScrollTask.isRunning());
	}
}
