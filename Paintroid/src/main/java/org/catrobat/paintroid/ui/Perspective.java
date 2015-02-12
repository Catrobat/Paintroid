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

package org.catrobat.paintroid.ui;

import java.io.Serializable;

import org.catrobat.paintroid.OptionsMenuActivity;
import org.catrobat.paintroid.PaintroidApplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

/**
 * The purpose of this class is to provide an independent interface to
 * manipulate the scale and translation of the DrawingSurface. The direct
 * manipulation of the Canvas is synchronized on the SurfaceHolder on which the
 * DrawingSurface must also synchronize its own drawing.
 */
public class Perspective implements Serializable {
	private static final long serialVersionUID = 7742690846128292452L;

	public static final float MIN_SCALE = 0.1f;
	public static final float MAX_SCALE = 100f;
	public static final float SCROLL_BORDER = 50f;
	private static final float BORDER_ZOOM_FACTOR = 0.95f;
	private static final float ACTION_BAR_HEIGHT = OptionsMenuActivity.ACTION_BAR_HEIGHT;

	private float mSurfaceWidth;
	private float mSurfaceHeight;
	private float mSurfaceCenterX;
	private float mSurfaceCenterY;
	private float mSurfaceScale;
	private float mSurfaceTranslationX;
	private float mSurfaceTranslationY;
	private float mBitmapWidth;
	private float mBitmapHeight;
	private float mScreenDensity;
	private boolean mIsFullscreen;
	private float mInitialTranslationX;
	private float mInitialTranslationY;

	public Perspective(SurfaceHolder holder) {
		setSurfaceHolder(holder);
		mSurfaceScale = 1f;
		DisplayMetrics metrics = new DisplayMetrics();
		Display display = ((WindowManager) PaintroidApplication.applicationContext
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		display.getMetrics(metrics);
		mScreenDensity = metrics.density;
		mIsFullscreen = false;
	}

	public synchronized void setSurfaceHolder(SurfaceHolder holder) {
		Rect surfaceFrame = holder.getSurfaceFrame();
		mSurfaceWidth = surfaceFrame.right;
		mSurfaceCenterX = surfaceFrame.exactCenterX();
		mSurfaceHeight = surfaceFrame.bottom;// - ACTION_BAR_HEIGHT *
												// mScreenDensity;
		mSurfaceCenterY = surfaceFrame.exactCenterY();
		resetScaleAndTranslation();
	}

	public synchronized void resetScaleAndTranslation() {

		float actionbarHeight = ACTION_BAR_HEIGHT * mScreenDensity;
		mBitmapWidth = PaintroidApplication.drawingSurface.getBitmapWidth();
		mBitmapHeight = PaintroidApplication.drawingSurface.getBitmapHeight();
		mSurfaceScale = 1f;

		if (mSurfaceWidth == 0 || mSurfaceHeight == 0) {
			mSurfaceTranslationX = 0f;
			mSurfaceTranslationY = -actionbarHeight;
		}

		else {
			mSurfaceTranslationX = mSurfaceWidth / 2 - mBitmapWidth / 2;
			mInitialTranslationX = mSurfaceTranslationX;

			mSurfaceTranslationY = (mSurfaceHeight / 2 - mBitmapHeight / 2);
			mInitialTranslationY = mSurfaceTranslationY;
		}

		float zoomFactor = (mIsFullscreen) ? 1.0f : BORDER_ZOOM_FACTOR;
		mSurfaceScale = getScaleForCenterBitmap() * zoomFactor;

	}

	public synchronized void setScale(float scale) {
		if (scale >= MIN_SCALE) {
			mSurfaceScale = scale;
		} else {
			mSurfaceScale = MIN_SCALE;
		}
	}

	public synchronized void multiplyScale(float factor) {
		mSurfaceScale *= factor;
		if (mSurfaceScale < MIN_SCALE) {
			mSurfaceScale = MIN_SCALE;
		} else if (mSurfaceScale > MAX_SCALE) {
			mSurfaceScale = MAX_SCALE;
		}
	}

	public synchronized void translate(float dx, float dy) {
		mSurfaceTranslationX += dx / mSurfaceScale;
		mSurfaceTranslationY += dy / mSurfaceScale;

		float xmax = (mBitmapWidth / 2)
				+ (((mSurfaceWidth / 2) - SCROLL_BORDER) / mSurfaceScale);
		if (mSurfaceTranslationX > (xmax + mInitialTranslationX)) {
			mSurfaceTranslationX = xmax + mInitialTranslationX;
		} else if (mSurfaceTranslationX < (-xmax + mInitialTranslationX)) {
			mSurfaceTranslationX = -xmax + mInitialTranslationX;
		}

		float ymax = (mBitmapHeight / 2)
				+ (((mSurfaceHeight / 2) - SCROLL_BORDER) / mSurfaceScale);
		if (mSurfaceTranslationY > (ymax + mInitialTranslationY)) {
			mSurfaceTranslationY = (ymax + mInitialTranslationY);
		} else if (mSurfaceTranslationY < (-ymax + mInitialTranslationY)) {
			mSurfaceTranslationY = -ymax + mInitialTranslationY;
		}
	}

	@Deprecated
	public synchronized void convertFromScreenToCanvas(PointF p) {
		p.x = (p.x - mSurfaceCenterX) / mSurfaceScale + mSurfaceCenterX
				- mSurfaceTranslationX;
		p.y = (p.y - mSurfaceCenterY) / mSurfaceScale + mSurfaceCenterY
				- mSurfaceTranslationY;
	}

	public synchronized PointF getCanvasPointFromSurfacePoint(
			PointF surfacePoint) {

		float canvasX = (surfacePoint.x - mSurfaceCenterX) / mSurfaceScale
				+ mSurfaceCenterX - mSurfaceTranslationX;
		float canvasY = (surfacePoint.y - mSurfaceCenterY) / mSurfaceScale
				+ mSurfaceCenterY - mSurfaceTranslationY;

		return new PointF(canvasX, canvasY);
	}

	@Deprecated
	public synchronized void convertFromCanvasToScreen(PointF p) {
		p.x = ((p.x + mSurfaceTranslationX - mSurfaceCenterX) * mSurfaceScale + mSurfaceCenterX);
		p.y = ((p.y + mSurfaceTranslationY - mSurfaceCenterY) * mSurfaceScale + mSurfaceCenterY);

	}

	public synchronized PointF getSurfacePointFromCanvasPoint(PointF canvasPoint) {

		float surfaceX = (canvasPoint.x + mSurfaceTranslationX - mSurfaceCenterX)
				* mSurfaceScale + mSurfaceCenterX;
		float surfaceY = (canvasPoint.y + mSurfaceTranslationY - mSurfaceCenterY)
				* mSurfaceScale + mSurfaceCenterY;

		return new PointF(surfaceX, surfaceY);
	}

	public synchronized void applyToCanvas(Canvas canvas) {
		canvas.scale(mSurfaceScale, mSurfaceScale, mSurfaceCenterX,
				mSurfaceCenterY);
		canvas.translate(mSurfaceTranslationX, mSurfaceTranslationY);
	}

	public float getScale() {
		return mSurfaceScale;
	}

	public float getScaleForCenterBitmap() {

		float ratioDependentScale;
		float screenSizeRatio = mSurfaceWidth / mSurfaceHeight;
		float bitmapSizeRatio = mBitmapWidth / mBitmapHeight;

		if (screenSizeRatio > bitmapSizeRatio) {
			ratioDependentScale = mSurfaceHeight / mBitmapHeight;
		} else {
			ratioDependentScale = mSurfaceWidth / mBitmapWidth;
		}

		if (ratioDependentScale > 1f) {
			ratioDependentScale = 1f;
		}
		if (ratioDependentScale < MIN_SCALE) {
			ratioDependentScale = MIN_SCALE;
		}

		return ratioDependentScale;
	}

	public void setFullscreen(boolean isFullscreen) {
		mIsFullscreen = isFullscreen;
		resetScaleAndTranslation();
	}

}
