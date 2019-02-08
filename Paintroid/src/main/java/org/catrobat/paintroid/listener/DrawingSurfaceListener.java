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

import android.graphics.Point;
import android.graphics.PointF;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.Tool.StateChange;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.Perspective;

import java.util.EnumSet;

import static org.catrobat.paintroid.tools.ToolType.FILL;
import static org.catrobat.paintroid.tools.ToolType.PIPETTE;
import static org.catrobat.paintroid.tools.ToolType.TRANSFORM;

public class DrawingSurfaceListener implements OnTouchListener {
	private static final float DRAWER_EDGE_SIZE = 20;
	private TouchMode touchMode;

	private float pointerDistance;
	private final AutoScrollTask autoScrollTask;

	private float xMidPoint;
	private float yMidPoint;

	private PointF canvasTouchPoint;
	private PointF eventTouchPoint;
	private boolean ignoreTouch;
	private int drawerEdgeSize;

	public DrawingSurfaceListener(AutoScrollTask autoScrollTask, float displayDensity) {
		this.touchMode = TouchMode.DRAW;
		this.autoScrollTask = autoScrollTask;
		drawerEdgeSize = (int) (DRAWER_EDGE_SIZE * displayDensity + 0.5f);

		canvasTouchPoint = new PointF();
		eventTouchPoint = new PointF();
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

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		Tool currentTool = PaintroidApplication.currentTool;
		DrawingSurface drawingSurface = (DrawingSurface) view;
		Perspective perspective = PaintroidApplication.perspective;

		canvasTouchPoint.x = event.getX();
		canvasTouchPoint.y = event.getY();
		eventTouchPoint.x = canvasTouchPoint.x;
		eventTouchPoint.y = canvasTouchPoint.y;

		perspective.convertToCanvasFromSurface(canvasTouchPoint);

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (eventTouchPoint.x < drawerEdgeSize || view.getWidth() - eventTouchPoint.x < drawerEdgeSize) {
					ignoreTouch = true;
					return true;
				}

				currentTool.handleTouch(canvasTouchPoint, MotionEvent.ACTION_DOWN);

				autoScrollTask.setEventPoint(eventTouchPoint.x, eventTouchPoint.y);
				autoScrollTask.setViewDimensions(view.getWidth(), view.getHeight());
				autoScrollTask.start();

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
					autoScrollTask.setEventPoint(eventTouchPoint.x, eventTouchPoint.y);
					autoScrollTask.setViewDimensions(view.getWidth(), view.getHeight());

					currentTool.handleTouch(canvasTouchPoint, MotionEvent.ACTION_MOVE);
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
						perspective.multiplyScale(scale);
					}

					float xOld = xMidPoint;
					float yOld = yMidPoint;
					calculateMidPoint(event);
					if (xOld > 0 && xMidPoint != xOld || yOld > 0 && yMidPoint != yOld) {
						perspective.translate(xMidPoint - xOld, yMidPoint - yOld);
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
					currentTool.handleTouch(canvasTouchPoint, MotionEvent.ACTION_UP);
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

	public static class AutoScrollTask implements Runnable {
		// IMPORTANT: If the SCROLL_INTERVAL_FACTOR is chosen too low,
		// espresso will wait forever for the handler queue to be empty on long touch events.
		private static final int SCROLL_INTERVAL_FACTOR = 40;
		private static final float STEP = 2f;

		private boolean running;
		private float pointX;
		private float pointY;
		private int width;
		private int height;
		private EnumSet<ToolType> ignoredTools = EnumSet.of(PIPETTE, FILL, TRANSFORM);
		private final PointF newMovePoint;

		private final Handler handler;
		private AutoScrollTaskCallback callback;

		public AutoScrollTask(Handler handler, AutoScrollTaskCallback callback) {
			this.handler = handler;
			this.callback = callback;
			running = false;
			newMovePoint = new PointF();
		}

		public void setEventPoint(float pointX, float pointY) {
			this.pointX = pointX;
			this.pointY = pointY;
		}

		public void setViewDimensions(int width, int height) {
			this.width = width;
			this.height = height;
		}

		public void start() {
			if (running || width == 0 || height == 0) {
				throw new IllegalStateException();
			} else if (ignoredTools.contains(callback.getCurrentToolType())) {
				return;
			}
			running = true;
			run();
		}

		public void stop() {
			if (running) {
				running = false;
				handler.removeCallbacks(this);
			}
		}

		public boolean isRunning() {
			return running;
		}

		private int calculateScrollInterval(float scale) {
			return (int) (SCROLL_INTERVAL_FACTOR / Math.cbrt(scale));
		}

		@Override
		public void run() {
			Point autoScrollDirection = callback.getToolAutoScrollDirection(pointX, pointY, width, height);

			if (autoScrollDirection.x != 0 || autoScrollDirection.y != 0) {
				newMovePoint.x = pointX;
				newMovePoint.y = pointY;
				callback.convertToCanvasFromSurface(newMovePoint);

				if (callback.isPointOnCanvas((int) newMovePoint.x, (int) newMovePoint.y)) {
					callback.translatePerspective(autoScrollDirection.x * STEP, autoScrollDirection.y * STEP);
					callback.handleToolMove(newMovePoint);
					callback.refreshDrawingSurface();
				}
			}
			handler.postDelayed(this, calculateScrollInterval(callback.getPerspectiveScale()));
		}
	}

	public interface AutoScrollTaskCallback {
		boolean isPointOnCanvas(int pointX, int pointY);

		void refreshDrawingSurface();

		void handleToolMove(PointF coordinate);

		Point getToolAutoScrollDirection(float pointX, float pointY, int screenWidth, int screenHeight);

		float getPerspectiveScale();

		void translatePerspective(float dx, float dy);

		void convertToCanvasFromSurface(PointF surfacePoint);

		ToolType getCurrentToolType();
	}
}
