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

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.tools.Tool.StateChange;
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

	private PointF mScreenPoint;

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

		PointF touchPoint = new PointF(event.getX(), event.getY());
		mScreenPoint = new PointF(touchPoint.x, touchPoint.y);
		mPerspective.convertFromScreenToCanvas(touchPoint);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			PaintroidApplication.currentTool.handleDown(touchPoint);
			moveThread = new MoveThread();
			moveThread.start();
			moveThread.setCalculationVariables(event.getX(), event.getY(),
					view.getWidth(), view.getHeight());
			// calcTranslation(event.getX(), event.getY(), view.getWidth(),
			// view.getHeight());
			break;
		case MotionEvent.ACTION_MOVE:
			if (event.getPointerCount() == 1) {
				if (System.nanoTime() < (mZoomTimeStamp + BLOCKING_TIME)) {
					break;
				}
				mTouchMode = TouchMode.DRAW;
				moveThread.setCalculationVariables(event.getX(), event.getY(),
						view.getWidth(), view.getHeight());
				// moveThread.setScreenPoint(screenPoint);
				// calcTranslation(event.getX(), event.getY(), view.getWidth(),
				// view.getHeight());
				PaintroidApplication.currentTool.handleMove(touchPoint);

			} else {
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
			moveThread.kill();
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

	// private void calcTranslation(float positionX, float positionY, int width,
	// int height) {
	// int border = 100;
	//
	// int deltaX = 0;
	// int deltaY = 0;
	//
	// if (moveThread != null) {
	// if (positionX < border) {
	// deltaX = 2;
	// }
	// if (positionX > width - border) {
	// deltaX = -2;
	// }
	//
	// if (positionY < border) {
	// deltaY = 2;
	// }
	//
	// if (positionY > height - border) {
	// deltaY = -2;
	// }
	// moveThread.setTranslation(deltaX, deltaY);
	// }
	//
	// }

	private class MoveThread extends Thread {

		private int step = 2;

		private boolean running = true;

		private float pointX;
		private float pointY;
		private int width;
		private int height;

		protected void setCalculationVariables(float pointX, float pointY,
				int width, int height) {
			this.pointX = pointX;
			this.pointY = pointY;
			this.width = width;
			this.height = height;
		}

		protected void kill() {
			running = false;
		}

		@Override
		public void run() {
			while (running) {
				Point autoScrollDirection = PaintroidApplication.currentTool
						.getAutoScrollDirection(pointX, pointY, width, height);
				PaintroidApplication.perspective.translate(
						autoScrollDirection.x * step, autoScrollDirection.y
								* step);
				PointF newMovePoint = new PointF(pointX, pointY);
				PaintroidApplication.perspective
						.convertFromScreenToCanvas(newMovePoint);
				PaintroidApplication.currentTool.handleMove(newMovePoint);
				try {
					sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
