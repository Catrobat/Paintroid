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

import org.catrobat.paintroid.listener.DrawingSurfaceListener;
import org.catrobat.paintroid.listener.DrawingSurfaceListener.AutoScrollTask;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.options.ToolOptionsViewController;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.catrobat.paintroid.test.utils.PointFMatcher.pointFEquals;
import static org.catrobat.paintroid.tools.Tool.StateChange.MOVE_CANCELED;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DrawingSurfaceListenerTest {

	private static final float DISPLAY_DENSITY = 1.5f;
	@Mock
	private AutoScrollTask autoScrollTask;

	@Mock
	private Tool currentTool;

	@Mock
	private ToolOptionsViewController toolOptionsViewController;

	@Mock
	private DrawingSurfaceListener.DrawingSurfaceListenerCallback callback;

	@Mock
	private MotionEvent motionEvent;

	private DrawingSurfaceListener drawingSurfaceListener;

	private final Float initalPositionX = 1.f;
	private final Float initalPositionY = 1.f;
	private final Float firstMovementPositionX = 5.f;
	private final Float firstMovementPositionY = 5.f;
	private final Float secondMovementPositionX = 6.f;
	private final Float secondMovementPositionY = 6.f;
	private final Float actionUpMovementPositionX = 7.f;
	private final Float actionUpMovementPositionY = 7.f;
	private final int width = 97;
	private final int height = 11;
	private final long firstMovementTimestamp = 150;
	private final long secondMovementTimestamp = 170;
	private final long actionUpTimestamp = 175;

	@Before
	public void setUp() {
		when(callback.getCurrentTool())
				.thenReturn(currentTool);
		when(callback.getToolOptionsViewController())
				.thenReturn(toolOptionsViewController);

		when(motionEvent.getDownTime()).thenReturn((long) 0);

		drawingSurfaceListener = new DrawingSurfaceListener(autoScrollTask, callback, DISPLAY_DENSITY);
	}

	@Test
	public void testSetUp() {
		verifyZeroInteractions(currentTool, callback, autoScrollTask);
	}

	private void triggerTouchDownEvent(float startPositionX, float startPositionY, DrawingSurface drawingSurface) {
		when(motionEvent.getAction()).thenReturn(MotionEvent.ACTION_DOWN);
		when(motionEvent.getX()).thenReturn(startPositionX);
		when(motionEvent.getY()).thenReturn(startPositionY);
		when(drawingSurface.getWidth()).thenReturn(width);
		when(drawingSurface.getHeight()).thenReturn(height);
		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);
	}

	private void triggerMovementEvent(float positionX, float positionY, DrawingSurface drawingSurface) {
		when(motionEvent.getAction()).thenReturn(MotionEvent.ACTION_MOVE);
		when(motionEvent.getX()).thenReturn(positionX);
		when(motionEvent.getY()).thenReturn(positionY);
		when(motionEvent.getPointerCount()).thenReturn(1);

		when(drawingSurface.getWidth()).thenReturn(width);
		when(drawingSurface.getHeight()).thenReturn(height);

		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);
	}

	private void triggerMovementEventWithTimestamp(float positionX, float positionY, DrawingSurface drawingSurface, long timestamp) {
		when(motionEvent.getEventTime()).thenReturn(timestamp);
		triggerMovementEvent(positionX, positionY, drawingSurface);
	}

	public void triggerTouchUpEvent(long timestamp, DrawingSurface drawingSurface) {
		when(motionEvent.getEventTime()).thenReturn(timestamp);
		when(motionEvent.getAction()).thenReturn(MotionEvent.ACTION_UP);
		when(motionEvent.getX()).thenReturn(actionUpMovementPositionX);
		when(motionEvent.getY()).thenReturn(actionUpMovementPositionY);
		when(drawingSurface.getWidth()).thenReturn(width);
		when(drawingSurface.getHeight()).thenReturn(height);
		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);
	}

	@Test
	public void testOnTouchDown() {
		DrawingSurface drawingSurface = mock(DrawingSurface.class);

		triggerTouchDownEvent(41f, 5f, drawingSurface);

		verify(callback).convertToCanvasFromSurface(pointFEquals(41f, 5f));
		verify(currentTool).handleDown(pointFEquals(41f, 5f));

		verify(autoScrollTask).setViewDimensions(width, height);
		verify(autoScrollTask).setEventPoint(41f, 5f);
		verify(autoScrollTask).start();

		verifyNoMoreInteractions(autoScrollTask, currentTool);
		verify(callback, never()).multiplyPerspectiveScale(anyFloat());
		verify(callback, never()).translatePerspective(anyFloat(), anyFloat());
	}

	@Test
	public void testOnTouchDownIgnoredIfInsideDrawerLeftEdge() {
		DrawingSurface drawingSurface = mock(DrawingSurface.class);
		MotionEvent motionEvent = mock(MotionEvent.class);

		when(motionEvent.getAction()).thenReturn(MotionEvent.ACTION_DOWN);
		when(motionEvent.getX()).thenReturn(20 * DISPLAY_DENSITY - 1);
		when(motionEvent.getY()).thenReturn(5f);

		boolean onTouchResult = drawingSurfaceListener.onTouch(drawingSurface, motionEvent);

		assertFalse(onTouchResult);
		verifyNoMoreInteractions(autoScrollTask, currentTool);
	}

	@Test
	public void testOnTouchDownIgnoredIfInsideDrawerRightEdge() {
		DrawingSurface drawingSurface = mock(DrawingSurface.class);
		MotionEvent motionEvent = mock(MotionEvent.class);

		when(motionEvent.getAction()).thenReturn(MotionEvent.ACTION_DOWN);
		when(motionEvent.getX()).thenReturn(67f);
		when(motionEvent.getY()).thenReturn(5f);

		when(drawingSurface.getWidth()).thenReturn((int) (67 + 20 * DISPLAY_DENSITY - 1));

		boolean onTouchResult = drawingSurfaceListener.onTouch(drawingSurface, motionEvent);

		assertFalse(onTouchResult);
		verifyNoMoreInteractions(autoScrollTask, currentTool);
	}

	@Test
	public void testOnTouchMoveInDrawMode() {
		DrawingSurface drawingSurface = mock(DrawingSurface.class);

		triggerMovementEvent(5f, 3f, drawingSurface);

		verify(callback).convertToCanvasFromSurface(pointFEquals(5f, 3f));
		verify(currentTool).handleMove(pointFEquals(5f, 3f));
		verify(autoScrollTask).setEventPoint(5f, 3f);
		verify(autoScrollTask).setViewDimensions(width, height);
		verifyNoMoreInteractions(autoScrollTask);
	}

	@Test
	public void testOnTouchMoveInDrawModeDoesNotStopAutoScroll() {
		DrawingSurface drawingSurface = mock(DrawingSurface.class);

		triggerMovementEvent(5f, 3f, drawingSurface);

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

		verify(currentTool, never()).handleMove(any(PointF.class));
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

		verify(currentTool, never()).handleMove(any(PointF.class));
		verify(autoScrollTask, never()).setEventPoint(anyFloat(), anyFloat());
		verify(autoScrollTask, never()).setViewDimensions(anyInt(), anyInt());

		verify(callback, never()).translatePerspective(anyFloat(), anyFloat());
		verify(callback, never()).multiplyPerspectiveScale(anyFloat());
	}

	@Test
	public void testOnTouchMoveInHandMode() {
		DrawingSurface drawingSurface = mock(DrawingSurface.class);
		MotionEvent motionEvent = mock(MotionEvent.class);

		when(motionEvent.getAction()).thenReturn(MotionEvent.ACTION_MOVE);

		when(motionEvent.getX()).thenReturn(7f);
		when(motionEvent.getY()).thenReturn(11f);
		when(motionEvent.getPointerCount()).thenReturn(1);
		when(currentTool.handToolMode()).thenReturn(true);

		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);
		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);
		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);

		verify(currentTool, never()).handleUp(any(PointF.class));
		verify(currentTool, never()).handleDown(any(PointF.class));
		verify(currentTool, never()).handleMove(any(PointF.class));
		verify(autoScrollTask, never()).setEventPoint(anyFloat(), anyFloat());
		verify(autoScrollTask, never()).setViewDimensions(anyInt(), anyInt());

		verify(callback, never()).translatePerspective(anyFloat(), anyFloat());
		verify(callback, never()).multiplyPerspectiveScale(anyFloat());
	}

	@Test
	public void testOnTouchMoveInPinchModeStopsAutoScroll() {
		DrawingSurface drawingSurface = mock(DrawingSurface.class);
		MotionEvent motionEvent = mock(MotionEvent.class);

		when(motionEvent.getAction()).thenReturn(MotionEvent.ACTION_MOVE);
		when(motionEvent.getPointerCount()).thenReturn(2);
		when(motionEvent.getX()).thenReturn(50f);
		when(autoScrollTask.isRunning()).thenReturn(true);

		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);

		verify(autoScrollTask).stop();
	}

	@Test
	public void testOnTouchMoveInHandModeStopsAutoScroll() {
		DrawingSurface drawingSurface = mock(DrawingSurface.class);
		MotionEvent motionEvent = mock(MotionEvent.class);

		when(motionEvent.getAction()).thenReturn(MotionEvent.ACTION_MOVE);
		when(motionEvent.getPointerCount()).thenReturn(1);
		when(currentTool.handToolMode()).thenReturn(true);
		when(motionEvent.getX()).thenReturn(50f);
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

		verify(currentTool, never()).handleMove(any(PointF.class));
		verify(autoScrollTask, never()).setEventPoint(anyFloat(), anyFloat());
		verify(autoScrollTask, never()).setViewDimensions(anyInt(), anyInt());

		verify(callback).translatePerspective(10, -10);
		verify(callback, never()).multiplyPerspectiveScale(anyFloat());
	}

	@Test
	public void testOnTouchMoveTranslateInHandMode() {
		DrawingSurface drawingSurface = mock(DrawingSurface.class);
		MotionEvent motionEvent = mock(MotionEvent.class);

		when(motionEvent.getAction()).thenReturn(MotionEvent.ACTION_MOVE);

		when(motionEvent.getX()).thenReturn(7f);
		when(motionEvent.getY()).thenReturn(11f);

		when(motionEvent.getPointerCount()).thenReturn(1);
		when(currentTool.handToolMode()).thenReturn(true);

		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);

		when(motionEvent.getX()).thenReturn(17f);
		when(motionEvent.getY()).thenReturn(1f);

		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);
		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);

		verify(currentTool, never()).handleUp(any(PointF.class));
		verify(currentTool, never()).handleDown(any(PointF.class));
		verify(currentTool, never()).handleMove(any(PointF.class));
		verify(autoScrollTask, never()).setEventPoint(anyFloat(), anyFloat());
		verify(autoScrollTask, never()).setViewDimensions(anyInt(), anyInt());

		verify(callback).translatePerspective(10, -10);
		verify(callback, never()).multiplyPerspectiveScale(anyFloat());
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

		verify(currentTool, never()).handleMove(any(PointF.class));
		verify(autoScrollTask, never()).setEventPoint(anyFloat(), anyFloat());
		verify(autoScrollTask, never()).setViewDimensions(anyInt(), anyInt());

		verify(callback, never()).translatePerspective(anyFloat(), anyFloat());
		verify(callback).multiplyPerspectiveScale((2f - 4f) / (1f - 5f));
		verify(callback).multiplyPerspectiveScale((1f - 5f) / (2f - 4f));
	}

	@Test
	public void testOnTouchUp() {
		DrawingSurface drawingSurface = mock(DrawingSurface.class);
		MotionEvent motionEvent = mock(MotionEvent.class);

		when(motionEvent.getAction()).thenReturn(MotionEvent.ACTION_UP);
		when(motionEvent.getX()).thenReturn(3f);
		when(motionEvent.getY()).thenReturn(5f);

		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);

		verify(currentTool).handleUp(pointFEquals(3f, 5f));
		verify(currentTool).setDrawTime(anyLong());
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
		verify(currentTool, never()).handleMove(any(PointF.class));
		verify(currentTool, never()).handleUp(any(PointF.class));

		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);

		verify(currentTool).handleUp(pointFEquals(3f, 5f));
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

	@Test
	public void testDisableAutoScroll() {
		DrawingSurface drawingSurface = mock(DrawingSurface.class);
		MotionEvent motionEvent = mock(MotionEvent.class);

		when(motionEvent.getAction()).thenReturn(MotionEvent.ACTION_DOWN);
		when(motionEvent.getX()).thenReturn(41f);
		when(motionEvent.getY()).thenReturn(5f);

		when(drawingSurface.getWidth()).thenReturn(width);
		when(drawingSurface.getHeight()).thenReturn(height);

		drawingSurfaceListener.disableAutoScroll();
		drawingSurfaceListener.onTouch(drawingSurface, motionEvent);

		verify(autoScrollTask, never()).start();
	}

	@Test
	public void testTouchCorrection() {
		DrawingSurface drawingSurface = mock(DrawingSurface.class);
		triggerTouchDownEvent(initalPositionX, initalPositionY, drawingSurface);

		triggerMovementEventWithTimestamp(firstMovementPositionX, firstMovementPositionY, drawingSurface, firstMovementTimestamp);
		triggerMovementEventWithTimestamp(secondMovementPositionX, secondMovementPositionY, drawingSurface, secondMovementTimestamp);

		triggerTouchUpEvent(actionUpTimestamp, drawingSurface);
		verify(currentTool).handleUp(pointFEquals(firstMovementPositionX, firstMovementPositionY));
	}

	@Test
	public void testTouchCorrectionWithInvalidDelayBetweenMovements() {
		DrawingSurface drawingSurface = mock(DrawingSurface.class);
		long tooLateTimestamp = secondMovementTimestamp + 15;

		triggerTouchDownEvent(initalPositionX, initalPositionY, drawingSurface);

		triggerMovementEventWithTimestamp(firstMovementPositionX, firstMovementPositionY, drawingSurface, firstMovementTimestamp);
		triggerMovementEventWithTimestamp(secondMovementPositionX, secondMovementPositionY, drawingSurface, secondMovementTimestamp);

		triggerTouchUpEvent(tooLateTimestamp, drawingSurface);
		verify(currentTool).handleUp(pointFEquals(actionUpMovementPositionX, actionUpMovementPositionY));
	}

	@Test
	public void testTouchCorrectionWithTooBigDelayBeforeUP() {
		DrawingSurface drawingSurface = mock(DrawingSurface.class);
		long muchLaterTimestamp = 220;

		triggerTouchDownEvent(initalPositionX, initalPositionY, drawingSurface);

		triggerMovementEventWithTimestamp(firstMovementPositionX, firstMovementPositionY, drawingSurface, firstMovementTimestamp);
		triggerMovementEventWithTimestamp(secondMovementPositionX, secondMovementPositionY, drawingSurface, secondMovementTimestamp);

		triggerTouchUpEvent(muchLaterTimestamp, drawingSurface);
		verify(currentTool).handleUp(pointFEquals(actionUpMovementPositionX, actionUpMovementPositionY));
	}

	@Test
	public void testTouchCorrectionWithDistanceAboveJitterThreshold() {
		float farAwayPositionX = 20.0f;
		float farAwayPositionY = 20.0f;

		DrawingSurface drawingSurface = mock(DrawingSurface.class);

		triggerTouchDownEvent(initalPositionX, initalPositionY, drawingSurface);

		triggerMovementEventWithTimestamp(farAwayPositionX, farAwayPositionY, drawingSurface, firstMovementTimestamp);
		triggerMovementEventWithTimestamp(secondMovementPositionX, secondMovementPositionY, drawingSurface, secondMovementTimestamp);

		triggerTouchUpEvent(actionUpTimestamp, drawingSurface);
		verify(currentTool).handleUp(pointFEquals(actionUpMovementPositionX, actionUpMovementPositionY));
	}
}
