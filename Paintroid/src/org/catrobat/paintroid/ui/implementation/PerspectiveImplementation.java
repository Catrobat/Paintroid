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

package org.catrobat.paintroid.ui.implementation;

import org.catrobat.paintroid.MenuFileActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.ui.Perspective;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
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
public class PerspectiveImplementation implements Perspective {
	private static final long serialVersionUID = 7742690846128292452L;

	public static final float MIN_SCALE = 0.1f;
	public static final float MAX_SCALE = 20f;
	public static final float SCROLL_BORDER = 50f;
	private static final float ACTION_BAR_HEIGHT = MenuFileActivity.ACTION_BAR_HEIGHT;

	private float mSurfaceWidth;
	private float mSurfaceHeight;
	private float mSurfaceCenterX;
	private float mSurfaceCenterY;
	private float mSurfaceScale;
	private float mSurfaceTranslationX;
	private float mSurfaceTranslationY;
	private float mScreenWidth;
	private float mScreenHeight;
	private float mBitmapWidth;
	private float mBitmapHeight;
	private float mScreenDensity;
	private boolean mIsFullscreen;

	public PerspectiveImplementation(SurfaceHolder holder) {
		setSurfaceHolder(holder);
		mSurfaceScale = 1f;
		DisplayMetrics metrics = new DisplayMetrics();
		Display display = ((WindowManager) PaintroidApplication.APPLICATION_CONTEXT
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		display.getMetrics(metrics);
		mScreenWidth = metrics.widthPixels;
		mScreenHeight = metrics.heightPixels;
		mScreenDensity = metrics.density;
		mIsFullscreen = false;
	}

	@Override
	public synchronized void setSurfaceHolder(SurfaceHolder holder) {
		Rect surfaceFrame = holder.getSurfaceFrame();
		mSurfaceWidth = surfaceFrame.right;
		mSurfaceHeight = surfaceFrame.bottom;
		mSurfaceCenterX = surfaceFrame.exactCenterX();
		mSurfaceCenterY = surfaceFrame.exactCenterY();
	}

	@Override
	public synchronized void resetScaleAndTranslation() {

		mSurfaceScale = 1f;

		if (mSurfaceWidth == 0 || mSurfaceHeight == 0) {
			mSurfaceTranslationX = 0f;
			mSurfaceTranslationY = 0f;
		}

		else {
			mBitmapWidth = PaintroidApplication.DRAWING_SURFACE
					.getBitmapWidth();
			mBitmapHeight = PaintroidApplication.DRAWING_SURFACE
					.getBitmapHeight();
			mSurfaceTranslationX = mScreenWidth / 2 - mBitmapWidth / 2;
			float actionbarHeight = ACTION_BAR_HEIGHT * mScreenDensity;

			mSurfaceTranslationY = mScreenHeight / 2 - mBitmapHeight / 2;

			if (!mIsFullscreen) {
				mSurfaceTranslationY -= actionbarHeight;
			}

			mSurfaceScale = getScaleForCenterBitmap();
		}

	}

	@Override
	public synchronized void setScale(float scale) {
		if (scale >= MIN_SCALE) {
			mSurfaceScale = scale;
		} else {
			mSurfaceScale = MIN_SCALE;
		}
	}

	@Override
	public synchronized void multiplyScale(float factor) {
		mSurfaceScale *= factor;
		if (mSurfaceScale < MIN_SCALE) {
			mSurfaceScale = MIN_SCALE;
		} else if (mSurfaceScale > MAX_SCALE) {
			mSurfaceScale = MAX_SCALE;
		}
	}

	@Override
	public synchronized void translate(float dx, float dy) {
		mSurfaceTranslationX += dx / mSurfaceScale;
		mSurfaceTranslationY += dy / mSurfaceScale;

		float xmax = (mSurfaceWidth - mSurfaceCenterX - SCROLL_BORDER)
				/ mSurfaceScale + mSurfaceCenterX;
		if (mSurfaceTranslationX > xmax) {
			mSurfaceTranslationX = xmax;
		} else if (mSurfaceTranslationX < -xmax) {
			mSurfaceTranslationX = -xmax;
		}

		float ymax = (mSurfaceHeight - mSurfaceCenterY - SCROLL_BORDER)
				/ mSurfaceScale + mSurfaceCenterY;
		if (mSurfaceTranslationY > ymax) {
			mSurfaceTranslationY = ymax;
		} else if (mSurfaceTranslationY < -ymax) {
			mSurfaceTranslationY = -ymax;
		}
	}

	@Override
	public synchronized void convertFromScreenToCanvas(Point p) {
		p.x = (int) ((p.x - mSurfaceCenterX) / mSurfaceScale + mSurfaceCenterX - mSurfaceTranslationX);
		p.y = (int) ((p.y - mSurfaceCenterY) / mSurfaceScale + mSurfaceCenterY - mSurfaceTranslationY);
	}

	@Override
	public synchronized void convertFromScreenToCanvas(PointF p) {
		p.x = (p.x - mSurfaceCenterX) / mSurfaceScale + mSurfaceCenterX
				- mSurfaceTranslationX;
		p.y = (p.y - mSurfaceCenterY) / mSurfaceScale + mSurfaceCenterY
				- mSurfaceTranslationY;
	}

	@Override
	public synchronized void applyToCanvas(Canvas canvas) {
		canvas.scale(mSurfaceScale, mSurfaceScale, mSurfaceCenterX,
				mSurfaceCenterY);
		canvas.translate(mSurfaceTranslationX, mSurfaceTranslationY);
	}

	@Override
	public float getScale() {
		return this.mSurfaceScale;
	}

	@Override
	public float getScaleForCenterBitmap() {

		float ratioDependentScale;
		float screenSizeRatio = mScreenWidth / mScreenHeight;
		float bitmapSizeRatio = mBitmapWidth / mBitmapHeight;

		if (screenSizeRatio > bitmapSizeRatio) {
			ratioDependentScale = mScreenHeight / mBitmapHeight;
		} else {
			ratioDependentScale = mScreenWidth / mBitmapWidth;
		}

		if (ratioDependentScale > 1f) {
			ratioDependentScale = 1f;
		}
		if (ratioDependentScale < MIN_SCALE) {
			ratioDependentScale = MIN_SCALE;
		}

		return ratioDependentScale;
	}

	@Override
	public void setFullscreen(boolean isFullscreen) {
		mIsFullscreen = isFullscreen;
	}
}
