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

import android.graphics.PointF;
import android.view.MotionEvent;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.listener.DrawingSurfaceListener;
import org.catrobat.paintroid.listener.DrawingSurfaceListener.AutoScrollTask;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.Perspective;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.catrobat.paintroid.test.utils.PointFMatcher.pointFEquals;
import static org.catrobat.paintroid.tools.Tool.StateChange.MOVE_CANCELED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DrawingSurfaceListenerTest {

	@Mock
	private AutoScrollTask autoScrollTask;

	@Mock
	private Tool currentTool;

	@Mock
	private Perspective perspective;

	@InjectMocks
	private DrawingSurfaceListener drawingSurfaceListener;

	@Before
	public void setUp() {
		PaintroidApplication.currentTool = currentTool;
		PaintroidApplication.perspective = perspective;
	}

	@Test
	public void testSetUp() {
		verifyZeroInteractions(currentTool, perspective, autoScrollTask);
	}

	@Test
	public void testOnTouchDown() {
		DrawingSurface drawingSurface = mock(DrawingSurface.class);
		MotionEvent motionEvent = mock(MotionEvent.class);

		when(motionEvent.getAction()).thenReturn(MotionEvent.ACTION_DOWN);
		when(motionEvent.getX()).thenReturn(3f);
		when(motionEvent.getY()).thenReturn(5f);

		when(drawingSurface.getWidth()).thenReturn(7);
		when(drawingSurface.getHeight()).thenReturn(11);

		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);

		verify(perspective).convertToCanvasFromSurface(pointFEquals(3f, 5f));
		verify(currentTool).handleTouch(pointFEquals(3f, 5f), eq(MotionEvent.ACTION_DOWN));

		verify(autoScrollTask).setViewDimensions(7, 11);
		verify(autoScrollTask).setEventPoint(3f, 5f);
		verify(autoScrollTask).start();

		verifyNoMoreInteractions(autoScrollTask, currentTool, perspective);
	}

	@Test
	public void testOnTouchMoveInDrawMode() {
		DrawingSurface drawingSurface = mock(DrawingSurface.class);
		MotionEvent motionEvent = mock(MotionEvent.class);

		when(motionEvent.getAction()).thenReturn(MotionEvent.ACTION_MOVE);
		when(motionEvent.getX()).thenReturn(5f);
		when(motionEvent.getY()).thenReturn(3f);
		when(motionEvent.getPointerCount()).thenReturn(1);

		when(drawingSurface.getWidth()).thenReturn(7);
		when(drawingSurface.getHeight()).thenReturn(11);

		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);

		verify(perspective).convertToCanvasFromSurface(pointFEquals(5f, 3f));
		verify(currentTool).handleTouch(pointFEquals(5f, 3f), eq(MotionEvent.ACTION_MOVE));
		verify(autoScrollTask).setEventPoint(5f, 3f);
		verify(autoScrollTask).setViewDimensions(7, 11);
		verifyNoMoreInteractions(autoScrollTask);
	}

	@Test
	public void testOnTouchMoveInDrawModeDoesNotStopAutoScroll() {
		DrawingSurface drawingSurface = mock(DrawingSurface.class);
		MotionEvent motionEvent = mock(MotionEvent.class);

		when(motionEvent.getAction()).thenReturn(MotionEvent.ACTION_MOVE);
		when(motionEvent.getX()).thenReturn(5f);
		when(motionEvent.getY()).thenReturn(3f);
		when(motionEvent.getPointerCount()).thenReturn(1);

		when(drawingSurface.getWidth()).thenReturn(7);
		when(drawingSurface.getHeight()).thenReturn(11);

		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);

		verify(autoScrollTask, never()).stop();
		verify(autoScrollTask, never()).isRunning();
	}

	@Test
	public void testOnTouchMoveInDrawModeAfterPinch() {
		DrawingSurface drawingSurface = mock(DrawingSurface.class);
		MotionEvent motionEvent = mock(MotionEvent.class);

		when(motionEvent.getAction()).thenReturn(MotionEvent.ACTION_MOVE);
		when(motionEvent.getX()).thenReturn(5f);
		when(motionEvent.getY()).thenReturn(3f);
		when(motionEvent.getPointerCount()).thenReturn(2, 1);

		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);
		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);
		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);

		verify(currentTool, never()).handleTouch(any(PointF.class), anyInt());
		verify(autoScrollTask, never()).setEventPoint(anyFloat(), anyFloat());
		verify(autoScrollTask, never()).setViewDimensions(anyInt(), anyInt());
	}

	@Test
	public void testOnTouchMoveInPinchMode() {
		DrawingSurface drawingSurface = mock(DrawingSurface.class);
		MotionEvent motionEvent = mock(MotionEvent.class);

		when(motionEvent.getAction()).thenReturn(MotionEvent.ACTION_MOVE);
		when(motionEvent.getX()).thenReturn(7f);
		when(motionEvent.getY()).thenReturn(11f);
		when(motionEvent.getPointerCount()).thenReturn(2);

		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);
		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);
		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);

		verify(currentTool, never()).handleTouch(any(PointF.class), anyInt());
		verify(autoScrollTask, never()).setEventPoint(anyFloat(), anyFloat());
		verify(autoScrollTask, never()).setViewDimensions(anyInt(), anyInt());

		verify(perspective, never()).translate(anyFloat(), anyFloat());
		verify(perspective, never()).multiplyScale(anyFloat());
	}

	@Test
	public void testOnTouchMoveInPinchModeStopsAutoScroll() {
		DrawingSurface drawingSurface = mock(DrawingSurface.class);
		MotionEvent motionEvent = mock(MotionEvent.class);

		when(motionEvent.getAction()).thenReturn(MotionEvent.ACTION_MOVE);
		when(motionEvent.getPointerCount()).thenReturn(2);
		when(autoScrollTask.isRunning()).thenReturn(true);

		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);

		verify(autoScrollTask).stop();
	}

	@Test
	public void testOnTouchMoveTranslateInPinchMode() {
		DrawingSurface drawingSurface = mock(DrawingSurface.class);
		MotionEvent motionEvent = mock(MotionEvent.class);

		when(motionEvent.getAction()).thenReturn(MotionEvent.ACTION_MOVE);

		when(motionEvent.getX(0)).thenReturn(7f);
		when(motionEvent.getY(0)).thenReturn(11f);
		when(motionEvent.getX(1)).thenReturn(23f);
		when(motionEvent.getY(1)).thenReturn(29f);

		when(motionEvent.getPointerCount()).thenReturn(2);

		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);

		when(motionEvent.getX(0)).thenReturn(17f);
		when(motionEvent.getY(0)).thenReturn(1f);
		when(motionEvent.getX(1)).thenReturn(33f);
		when(motionEvent.getY(1)).thenReturn(19f);

		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);
		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);

		verify(currentTool, never()).handleTouch(any(PointF.class), anyInt());
		verify(autoScrollTask, never()).setEventPoint(anyFloat(), anyFloat());
		verify(autoScrollTask, never()).setViewDimensions(anyInt(), anyInt());

		verify(perspective).translate(10, -10);
		verify(perspective, never()).multiplyScale(anyFloat());
	}

	@Test
	public void testOnTouchMoveScaleInPinchMode() {
		DrawingSurface drawingSurface = mock(DrawingSurface.class);
		MotionEvent motionEvent = mock(MotionEvent.class);

		when(motionEvent.getAction()).thenReturn(MotionEvent.ACTION_MOVE);

		when(motionEvent.getX(0)).thenReturn(2f);
		when(motionEvent.getY(0)).thenReturn(2f);
		when(motionEvent.getX(1)).thenReturn(4f);
		when(motionEvent.getY(1)).thenReturn(4f);

		when(motionEvent.getPointerCount()).thenReturn(2);

		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);

		when(motionEvent.getX(0)).thenReturn(1f);
		when(motionEvent.getY(0)).thenReturn(1f);
		when(motionEvent.getX(1)).thenReturn(5f);
		when(motionEvent.getY(1)).thenReturn(5f);

		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);

		when(motionEvent.getX(0)).thenReturn(2f);
		when(motionEvent.getY(0)).thenReturn(2f);
		when(motionEvent.getX(1)).thenReturn(4f);
		when(motionEvent.getY(1)).thenReturn(4f);

		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);

		verify(currentTool, never()).handleTouch(any(PointF.class), anyInt());
		verify(autoScrollTask, never()).setEventPoint(anyFloat(), anyFloat());
		verify(autoScrollTask, never()).setViewDimensions(anyInt(), anyInt());

		verify(perspective, never()).translate(anyFloat(), anyFloat());
		verify(perspective).multiplyScale((2f - 4f) / (1f - 5f));
		verify(perspective).multiplyScale((1f - 5f) / (2f - 4f));
	}

	@Test
	public void testOnTouchUp() {
		DrawingSurface drawingSurface = mock(DrawingSurface.class);
		MotionEvent motionEvent = mock(MotionEvent.class);

		when(motionEvent.getAction()).thenReturn(MotionEvent.ACTION_UP);
		when(motionEvent.getX()).thenReturn(3f);
		when(motionEvent.getY()).thenReturn(5f);

		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);

		verify(currentTool).handleTouch(pointFEquals(3f, 5f), eq(MotionEvent.ACTION_UP));
		verifyNoMoreInteractions(currentTool);
	}

	@Test
	public void testOnTouchUpAfterPinchResetsTool() {
		DrawingSurface drawingSurface = mock(DrawingSurface.class);
		MotionEvent motionEvent = mock(MotionEvent.class);

		when(motionEvent.getAction()).thenReturn(MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP);
		when(motionEvent.getX()).thenReturn(3f);
		when(motionEvent.getY()).thenReturn(5f);
		when(motionEvent.getPointerCount()).thenReturn(2);

		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);
		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);

		verify(currentTool, times(2)).resetInternalState(MOVE_CANCELED);
		verify(currentTool, never()).handleTouch(any(PointF.class), anyInt());

		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);

		verify(currentTool).handleTouch(pointFEquals(3f, 5f), eq(MotionEvent.ACTION_UP));
	}

	@Test
	public void testOnTouchUpStopsAutoScroll() {
		DrawingSurface drawingSurface = mock(DrawingSurface.class);
		MotionEvent motionEvent = mock(MotionEvent.class);

		when(autoScrollTask.isRunning()).thenReturn(true);
		when(motionEvent.getAction()).thenReturn(MotionEvent.ACTION_UP);

		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);

		verify(autoScrollTask).stop();
	}
}
