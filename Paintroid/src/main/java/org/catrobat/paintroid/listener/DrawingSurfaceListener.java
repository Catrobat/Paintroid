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

package org.catrobat.paintroid.listener;

import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.Tool.StateChange;
import org.catrobat.paintroid.tools.options.ToolOptionsControllerContract;
import org.catrobat.paintroid.ui.DrawingSurface;

public class DrawingSurfaceListener implements OnTouchListener {
	private static final float DRAWER_EDGE_SIZE = 20;
	private final DrawingSurfaceListenerCallback callback;
	private TouchMode touchMode;

	private float pointerDistance;
	private final AutoScrollTask autoScrollTask;

	private float xMidPoint;
	private float yMidPoint;

	private PointF canvasTouchPoint;
	private PointF eventTouchPoint;

	private boolean ignoreTouch;
	private int drawerEdgeSize;
	private boolean autoScroll = true;

	public DrawingSurfaceListener(AutoScrollTask autoScrollTask, DrawingSurfaceListenerCallback callback, float displayDensity) {
		this.autoScrollTask = autoScrollTask;
		this.callback = callback;
		drawerEdgeSize = (int) (DRAWER_EDGE_SIZE * displayDensity + 0.5f);
		touchMode = TouchMode.DRAW;

		eventTouchPoint = new PointF();
		canvasTouchPoint = new PointF();
	}

	private float calculatePointerDistance(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.hypot(x, y);
	}

	private void calculateMidPoint(MotionEvent event) {
		xMidPoint = (event.getX(0) + event.getX(1)) / 2f;
		yMidPoint = (event.getY(0) + event.getY(1)) / 2f;
	}

	public void enableAutoScroll() {
		autoScroll = true;
	}

	public void disableAutoScroll() {
		autoScroll = false;
		if (autoScrollTask.isRunning()) {
			autoScrollTask.stop();
		}
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		ToolOptionsControllerContract toolOptionsController = callback.getToolOptionsController();

		if (toolOptionsController.isVisible()) {
			toolOptionsController.hideAnimated();
			ignoreTouch = true;
			return true;
		}

		DrawingSurface drawingSurface = (DrawingSurface) view;
		Tool currentTool = callback.getCurrentTool();

		canvasTouchPoint.x = event.getX();
		canvasTouchPoint.y = event.getY();
		eventTouchPoint.x = canvasTouchPoint.x;
		eventTouchPoint.y = canvasTouchPoint.y;

		callback.convertToCanvasFromSurface(canvasTouchPoint);

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (eventTouchPoint.x < drawerEdgeSize || view.getWidth() - eventTouchPoint.x < drawerEdgeSize) {
					ignoreTouch = true;
					return true;
				}

				currentTool.handleDown(canvasTouchPoint);

				if (autoScroll) {
					autoScrollTask.setEventPoint(eventTouchPoint.x, eventTouchPoint.y);
					autoScrollTask.setViewDimensions(view.getWidth(), view.getHeight());
					autoScrollTask.start();
				}

				break;
			case MotionEvent.ACTION_MOVE:
				if (ignoreTouch) {
					return true;
				}
				if (event.getPointerCount() == 1) {
					if (touchMode == TouchMode.PINCH) {
						break;
					}

					touchMode = TouchMode.DRAW;
					if (autoScroll) {
						autoScrollTask.setEventPoint(eventTouchPoint.x, eventTouchPoint.y);
						autoScrollTask.setViewDimensions(view.getWidth(), view.getHeight());
					}

					currentTool.handleMove(canvasTouchPoint);
				} else {
					if (autoScrollTask.isRunning()) {
						autoScrollTask.stop();
					}
					if (touchMode == TouchMode.DRAW) {
						currentTool.resetInternalState(StateChange.MOVE_CANCELED);
					}

					touchMode = TouchMode.PINCH;

					float pointerDistanceOld = pointerDistance;
					pointerDistance = calculatePointerDistance(event);
					if (pointerDistanceOld > 0 && pointerDistanceOld != pointerDistance) {
						float scale = (pointerDistance / pointerDistanceOld);
						callback.multiplyPerspectiveScale(scale);
					}

					float xOld = xMidPoint;
					float yOld = yMidPoint;
					calculateMidPoint(event);
					if (xOld > 0 && xMidPoint != xOld || yOld > 0 && yMidPoint != yOld) {
						callback.translatePerspective(xMidPoint - xOld, yMidPoint - yOld);
					}
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				if (ignoreTouch) {
					ignoreTouch = false;
					return true;
				}

				if (autoScrollTask.isRunning()) {
					autoScrollTask.stop();
				}

				if (touchMode == TouchMode.DRAW) {
					currentTool.handleUp(canvasTouchPoint);
				} else {
					currentTool.resetInternalState(StateChange.MOVE_CANCELED);
				}

				pointerDistance = 0;
				xMidPoint = 0;
				yMidPoint = 0;
				touchMode = TouchMode.DRAW;
				break;
		}

		drawingSurface.refreshDrawingSurface();
		return true;
	}

	enum TouchMode {
		DRAW, PINCH
	}

	public interface DrawingSurfaceListenerCallback {
		Tool getCurrentTool();

		ToolOptionsControllerContract getToolOptionsController();

		void multiplyPerspectiveScale(float factor);

		void translatePerspective(float x, float y);

		void convertToCanvasFromSurface(PointF surfacePoint);
	}
}
