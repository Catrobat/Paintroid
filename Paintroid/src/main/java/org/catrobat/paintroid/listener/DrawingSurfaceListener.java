/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.listener;

import java.util.EnumSet;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.tools.Tool.StateChange;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.Perspective;

import android.graphics.Point;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class DrawingSurfaceListener implements OnTouchListener {
	static enum TouchMode {
		DRAW, PINCH
	};

	private final int BLOCKING_TIME = 250 * 1000 * 1000;

	private final Perspective mPerspective;
	private float mPointerDistance;
	private PointF mPointerMean;
	private TouchMode mTouchMode;
	private long mZoomTimeStamp;
	private MoveThread moveThread;

	public DrawingSurfaceListener() {
		mPerspective = PaintroidApplication.perspective;
		mPointerMean = new PointF(0, 0);
		mTouchMode = TouchMode.DRAW;
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
		PointF touchPoint = mPerspective
				.getCanvasPointFromSurfacePoint(new PointF(event.getX(), event
						.getY()));

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			PaintroidApplication.currentTool.handleDown(touchPoint);

			moveThread = new MoveThread();
			moveThread.setCalculationVariables(event.getX(), event.getY(),
					view.getWidth(), view.getHeight());
			moveThread.start();

			break;
		case MotionEvent.ACTION_MOVE:
			if (event.getPointerCount() == 1) {
				if (System.nanoTime() < (mZoomTimeStamp + BLOCKING_TIME)) {
					break;
				}
				mTouchMode = TouchMode.DRAW;
				if (moveThread != null) {
					moveThread.setCalculationVariables(event.getX(),
							event.getY(), view.getWidth(), view.getHeight());
				}
				PaintroidApplication.currentTool.handleMove(touchPoint);

			} else {
				if (moveThread != null) {
					if (moveThread.scrolling
							&& (System.nanoTime() > (moveThread.threadStartTime + BLOCKING_TIME))) {
						break;
					} else {
						moveThread.kill();
						moveThread = null;
					}
				}
				mTouchMode = TouchMode.PINCH;

				float pointerDistanceOld = mPointerDistance;
				mPointerDistance = calculatePointerDistance(event);
				if (pointerDistanceOld > 0) {
					float scale = (mPointerDistance / pointerDistanceOld);
					mPerspective.multiplyScale(scale);
				}

				float xOld = mPointerMean.x;
				float yOld = mPointerMean.y;
				calculatePointerMean(event, mPointerMean);
				if (xOld > 0 || yOld > 0) {
					mPerspective.translate(mPointerMean.x - xOld,
							mPointerMean.y - yOld);
				}
				mZoomTimeStamp = System.nanoTime();
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			if (moveThread != null) {
				moveThread.kill();
			}
			moveThread = null;
			if (mTouchMode == TouchMode.DRAW) {
				PaintroidApplication.currentTool.handleUp(touchPoint);
			} else {
				PaintroidApplication.currentTool
						.resetInternalState(StateChange.MOVE_CANCELED);
			}
			mPointerDistance = 0;
			mPointerMean.set(0, 0);
			break;
		}
		return true;
	}

	private class MoveThread extends Thread {

		private static final int SCROLL_INTERVAL_FACTOR = 8; // the higher the
																// slower

		private int step = 1;

		private boolean running;
		private boolean scrolling;

		private float pointX;
		private float pointY;
		private int width;
		private int height;
		private long threadStartTime;
		private EnumSet<ToolType> ignoredTools = EnumSet.of(ToolType.PIPETTE,
				ToolType.FILL, ToolType.CROP, ToolType.FLIP, ToolType.MOVE,
				ToolType.ZOOM);

		protected MoveThread() {
			threadStartTime = System.nanoTime();
			running = !ignoredTools.contains(PaintroidApplication.currentTool
					.getToolType());
			scrolling = false;
		}

		protected void setCalculationVariables(float pointX, float pointY,
				int width, int height) {
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

		protected void kill() {
			running = false;
		}

		protected int calculateScrollInterval(float scale) {
			return (int) (SCROLL_INTERVAL_FACTOR / Math.pow(scale, 1 / 3));// approximate
																			// calculation
		}

		@Override
		public void run() {
			while (running) {
				Point autoScrollDirection = PaintroidApplication.currentTool
						.getAutoScrollDirection(pointX, pointY, width, height);

				if (autoScrollDirection.x != 0 || autoScrollDirection.y != 0) {
					scrolling = true;

					PaintroidApplication.perspective.translate(
							autoScrollDirection.x * step, autoScrollDirection.y
									* step);
					PointF newMovePoint = PaintroidApplication.perspective
							.getCanvasPointFromSurfacePoint(new PointF(pointX,
									pointY));
					PaintroidApplication.currentTool.handleMove(newMovePoint);
				}

				try {
					sleep(calculateScrollInterval(PaintroidApplication.perspective
							.getScale()));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				scrolling = false;
			}
		}
	}
}
