/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
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
import org.catrobat.paintroid.ui.Perspective;

import android.graphics.PointF;
import android.util.FloatMath;
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

	public DrawingSurfaceListener() {
		mPerspective = PaintroidApplication.perspective;
		mPointerMean = new PointF(0, 0);
		mTouchMode = TouchMode.DRAW;
	}

	private float calculatePointerDistance(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	private void calculatePointerMean(MotionEvent event, PointF p) {
		float x = (event.getX(0) + event.getX(1)) / 2f;
		float y = (event.getY(0) + event.getY(1)) / 2f;
		p.set(x, y);
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {

		PointF touchPoint = new PointF(event.getX(), event.getY());
		mPerspective.convertFromScreenToCanvas(touchPoint);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			PaintroidApplication.currentTool.handleDown(touchPoint);
			break;
		case MotionEvent.ACTION_MOVE:
			if (event.getPointerCount() == 1) {
				if (System.nanoTime() < (mZoomTimeStamp + BLOCKING_TIME)) {
					break;
				}
				mTouchMode = TouchMode.DRAW;
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
			// Log.d(PaintroidApplication.TAG,
			// "DrawingSurfaceListener.onTouch UP"); // TODO remove logging
			if (mTouchMode == TouchMode.DRAW) {
				PaintroidApplication.currentTool.handleUp(touchPoint);
			} else {
				PaintroidApplication.currentTool.resetInternalState();
			}
			mPointerDistance = 0;
			mPointerMean.set(0, 0);
			break;
		}
		return true;
	}
}
