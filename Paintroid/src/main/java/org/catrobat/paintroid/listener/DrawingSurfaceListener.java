/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.listener;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.tools.Tool.StateChange;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.Perspective;

import java.util.EnumSet;

public class DrawingSurfaceListener implements OnTouchListener {
	private static final int BLOCKING_TIME = 250 * 1000 * 1000;

	private float pointerDistance;
	private PointF pointerMean;
	private TouchMode touchMode;
	private long zoomTimeStamp;
	private MoveThread moveThread;

	public DrawingSurfaceListener() {
		pointerMean = new PointF(0, 0);
		touchMode = TouchMode.DRAW;
	}

	private float calculatePointerDistance(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	private void calculatePointerMean(MotionEvent event, PointF p) {
		float x = (event.getX(0) + event.getX(1)) / 2f;
		float y = (event.getY(0) + event.getY(1)) / 2f;
		p.set(x, y);
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		DrawingSurface drawingSurface = (DrawingSurface) view;
		Perspective perspective = PaintroidApplication.perspective;
		PointF touchPoint = perspective
				.getCanvasPointFromSurfacePoint(new PointF(event.getX(), event.getY()));
		if (drawingSurface.getLock()) {
			touchMode = TouchMode.LOCK;
		}
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				PaintroidApplication.currentTool.handleTouch(touchPoint, MotionEvent.ACTION_DOWN);

				moveThread = new MoveThread();
				moveThread.setCalculationVariables(event.getX(), event.getY(),
						view.getWidth(), view.getHeight());
				moveThread.start();

				break;
			case MotionEvent.ACTION_MOVE:
				if (event.getPointerCount() == 1) {
					if (System.nanoTime() < (zoomTimeStamp + BLOCKING_TIME)) {
						break;
					}
					touchMode = TouchMode.DRAW;
					if (moveThread != null) {
						moveThread.setCalculationVariables(event.getX(),
								event.getY(), view.getWidth(), view.getHeight());
					}
					PaintroidApplication.currentTool.handleTouch(touchPoint, MotionEvent.ACTION_MOVE);
				} else {
					if (moveThread != null && System.nanoTime() > moveThread.threadStartTime + BLOCKING_TIME) {
						break;
					}

					if (moveThread != null) {
						if (moveThread.scrolling
								&& (System.nanoTime() > (moveThread.threadStartTime + BLOCKING_TIME))) {
							break;
						} else {
							moveThread.kill();
							moveThread = null;
						}
					}
					touchMode = TouchMode.PINCH;

					float pointerDistanceOld = pointerDistance;
					pointerDistance = calculatePointerDistance(event);
					if (pointerDistanceOld > 0) {
						float scale = (pointerDistance / pointerDistanceOld);
						perspective.multiplyScale(scale);
					}

					float xOld = pointerMean.x;
					float yOld = pointerMean.y;
					calculatePointerMean(event, pointerMean);
					if (xOld > 0 || yOld > 0) {
						perspective.translate(pointerMean.x - xOld, pointerMean.y - yOld);
					}
					zoomTimeStamp = System.nanoTime();
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				if (moveThread != null) {
					moveThread.kill();
				}
				moveThread = null;
				if (touchMode == TouchMode.DRAW) {
					PaintroidApplication.currentTool.handleTouch(touchPoint, MotionEvent.ACTION_UP);
				} else {
					PaintroidApplication.currentTool.resetInternalState(StateChange.MOVE_CANCELED);
				}
				pointerDistance = 0;
				pointerMean.set(0, 0);
				break;
		}
		drawingSurface.refreshDrawingSurface();
		return true;
	}

	enum TouchMode {
		DRAW, PINCH, LOCK
	}

	private class MoveThread extends Thread {

		private static final int SCROLL_INTERVAL_FACTOR = 8;

		private int step = 1;

		private boolean running;
		private boolean scrolling;

		private float pointX;
		private float pointY;
		private int width;
		private int height;
		private long threadStartTime;
		private EnumSet<ToolType> ignoredTools = EnumSet.of(ToolType.PIPETTE,
				ToolType.FILL, ToolType.TRANSFORM);

		MoveThread() {
			threadStartTime = System.nanoTime();
			running = !ignoredTools.contains(PaintroidApplication.currentTool
					.getToolType());
			scrolling = false;
		}

		void setCalculationVariables(float pointX, float pointY, int width, int height) {
			this.pointX = pointX;
			this.pointY = pointY;
			this.width = width;
			this.height = height;
		}

		@Override
		public synchronized void start() {
			if (width == 0 || height == 0) {
				throw new IllegalStateException(
						"MoveThread could not be started. Illegal width and/or height values.");
			}
			super.start();
		}

		void kill() {
			running = false;
		}

		private int calculateScrollInterval(float scale) {
			return (int) (SCROLL_INTERVAL_FACTOR / Math.pow(scale, 1 / 3));
		}

		@Override
		public void run() {
			PointF newMovePoint = new PointF();
			while (running) {
				Point autoScrollDirection = PaintroidApplication.currentTool
						.getAutoScrollDirection(pointX, pointY, width, height);

				if (autoScrollDirection.x != 0 || autoScrollDirection.y != 0) {
					scrolling = true;

					newMovePoint.set(pointX, pointY);
					PaintroidApplication.perspective.convertToCanvasFromSurface(newMovePoint);

					if (PaintroidApplication.drawingSurface.isPointOnCanvas(newMovePoint)) {

						PaintroidApplication.perspective.translate(autoScrollDirection.x * step,
								autoScrollDirection.y * step);

						PaintroidApplication.currentTool.handleMove(newMovePoint);
					}
				}

				try {
					sleep(calculateScrollInterval(PaintroidApplication.perspective.getScale()));
				} catch (InterruptedException e) {
					Log.e(DrawingSurfaceListener.class.getSimpleName(), e.getMessage());
				}
				scrolling = false;
				touchMode = TouchMode.DRAW;
			}
		}
	}
}
