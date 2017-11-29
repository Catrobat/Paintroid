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

package org.catrobat.paintroid.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.implementation.BaseTool;

import java.util.ArrayList;

public class DrawingSurface extends SurfaceView implements
		SurfaceHolder.Callback {
	private static final String TAG = DrawingSurface.class.getSimpleName();
	protected static final String BUNDLE_INSTANCE_STATE = "BUNDLE_INSTANCE_STATE";
	protected static final String BUNDLE_PERSPECTIVE = "BUNDLE_PERSPECTIVE";
	protected static final String BUNDLE_WORKING_BITMAP = "BUNDLE_WORKING_BITMAP";
	protected static final int BACKGROUND_COLOR = Color.LTGRAY;

	private DrawingSurfaceThread mDrawingThread;
	private Bitmap mWorkingBitmap;
	private Rect mWorkingBitmapRect;
	private Canvas mWorkingBitmapCanvas;
	private Paint mFramePaint;
	private Paint mClearPaint;
	protected boolean mSurfaceCanBeUsed;
	private Paint mOpacityPaint;

	private boolean lock;
	private boolean visible;

	private boolean drawingSurfaceDirtyFlag = false;
	private final Object drawingLock = new Object();


	public void setLock(boolean locked) {
		lock = locked;
	}

	public boolean getLock() {
		return lock;
	}

	public void setVisible(boolean visibility_to_set) {
		visible = visibility_to_set;
	}

	public boolean getVisible() {
		return visible;
	}

	public Canvas getCanvas() {
		return mWorkingBitmapCanvas;
	}

	private class DrawLoop implements Runnable {
		final SurfaceHolder holder = getHolder();

		@Override
		public void run() {

			synchronized (drawingLock) {
				if (!drawingSurfaceDirtyFlag && mSurfaceCanBeUsed) {
					try {
						drawingLock.wait();
					} catch (InterruptedException e) {
						Log.e(TAG, e.getMessage());
					}
				} else {
					drawingSurfaceDirtyFlag = false;
				}

				if (!mSurfaceCanBeUsed) {
					return;
				}
			}

			Canvas canvas = null;

			synchronized (holder) {
				try {
					canvas = holder.lockCanvas();
					if (canvas != null) {
						doDraw(canvas);
					}
				} finally {
					if (canvas != null) {
						holder.unlockCanvasAndPost(canvas);
					}
				}
			}
		}
	}

	private synchronized void doDraw(Canvas surfaceViewCanvas) {
		try {
			if (mWorkingBitmapRect == null || surfaceViewCanvas == null
					|| mWorkingBitmapCanvas == null || isWorkingBitmapRecycled()) {
				return;
			}

			PaintroidApplication.perspective.applyToCanvas(surfaceViewCanvas);
			surfaceViewCanvas.drawColor(BACKGROUND_COLOR);
			surfaceViewCanvas.drawRect(mWorkingBitmapRect,
					BaseTool.CHECKERED_PATTERN);
			surfaceViewCanvas.drawRect(mWorkingBitmapRect, mFramePaint);

			if (mWorkingBitmap != null && !mWorkingBitmap.isRecycled()
					&& mSurfaceCanBeUsed) {

				ArrayList<Layer> layers = LayerListener.getInstance().getAdapter().getLayers();
				mOpacityPaint = new Paint();

				for (int i = layers.size() - 1; i >= 0; i--) {
					surfaceViewCanvas.drawBitmap(layers.get(i).getImage(), 0, 0, mOpacityPaint);
				}
				PaintroidApplication.currentTool.draw(surfaceViewCanvas);
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	public DrawingSurface(Context context, AttributeSet attrSet) {
		super(context, attrSet);
		init();
	}

	public DrawingSurface(Context context) {
		super(context);
		init();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		getHolder().addCallback(this);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();

		getHolder().removeCallback(this);
	}

	private void init() {

		mWorkingBitmapRect = new Rect();
		mWorkingBitmapCanvas = new Canvas();

		mFramePaint = new Paint();
		mFramePaint.setColor(Color.BLACK);
		mFramePaint.setStyle(Paint.Style.STROKE);

		mClearPaint = new Paint();
		mClearPaint.setColor(Color.TRANSPARENT);
		mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		mOpacityPaint = new Paint();
		setLock(false);
		setVisible(true);
	}

	public void refreshDrawingSurface() {
		synchronized (drawingLock) {
			drawingSurfaceDirtyFlag = true;
			drawingLock.notify();
		}
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putParcelable(BUNDLE_INSTANCE_STATE, super.onSaveInstanceState());
		bundle.putParcelable(BUNDLE_WORKING_BITMAP, mWorkingBitmap);
		bundle.putSerializable(BUNDLE_PERSPECTIVE,
				PaintroidApplication.perspective);
		return bundle;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			PaintroidApplication.perspective = (Perspective) bundle
					.getSerializable(BUNDLE_PERSPECTIVE);
			resetBitmap((Bitmap) bundle.getParcelable(BUNDLE_WORKING_BITMAP));

			super.onRestoreInstanceState(bundle
					.getParcelable(BUNDLE_INSTANCE_STATE));
		} else {
			super.onRestoreInstanceState(state);
		}
	}

	public synchronized void resetBitmap(Bitmap bitmap) {
		PaintroidApplication.perspective.resetScaleAndTranslation();
		setBitmap(bitmap);

		if (mSurfaceCanBeUsed) {
			mDrawingThread.start();
		}
	}

	public synchronized void setBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			mWorkingBitmap = bitmap;
			mWorkingBitmapCanvas.setBitmap(mWorkingBitmap);
			mWorkingBitmapRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
		}
	}

	public synchronized Bitmap getBitmapCopy() {
		return !isWorkingBitmapRecycled() ? Bitmap.createBitmap(mWorkingBitmap) : null;
	}

	private boolean isWorkingBitmapRecycled() {
		return mWorkingBitmap == null || mWorkingBitmap.isRecycled();
	}

	public synchronized boolean isDrawingSurfaceBitmapValid() {
		return !isWorkingBitmapRecycled() && !mSurfaceCanBeUsed;
	}

	public synchronized boolean isPointOnCanvas(PointF point) {
		if (isWorkingBitmapRecycled()) {
			return false;
		}

		Rect boundsCanvas = mWorkingBitmapCanvas.getClipBounds();
		return boundsCanvas.contains((int) point.x, (int) point.y);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		mSurfaceCanBeUsed = true;
		PaintroidApplication.perspective.setSurfaceHolder(holder);

		if (mWorkingBitmap != null && mDrawingThread != null) {
			mDrawingThread.start();
		}

		PaintroidApplication.drawingSurface.refreshDrawingSurface();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mDrawingThread = new DrawingSurfaceThread(new DrawLoop());
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mSurfaceCanBeUsed = false;
		if (mDrawingThread != null) {
			mDrawingThread.stop();
		}
	}

	public int getPixel(PointF coordinate) {
		try {
			if (!isWorkingBitmapRecycled()) {
				return mWorkingBitmap.getPixel((int) coordinate.x,(int) coordinate.y);
			}
		} catch (IllegalArgumentException e) {
			Log.w(PaintroidApplication.TAG,
					"getBitmapColor coordinate out of bounds");
		}
		return Color.TRANSPARENT;
	}

	public void getPixels(int[] pixels, int offset, int stride, int x, int y,
						  int width, int height) {
		if (!isWorkingBitmapRecycled()) {
			mWorkingBitmap.getPixels(pixels, offset, stride, x, y, width, height);
		}
	}

	public int getBitmapWidth() {
		if (mWorkingBitmap == null) {
			return -1;
		}
		return mWorkingBitmap.getWidth();
	}

	public int getBitmapHeight() {
		if (mWorkingBitmap == null) {
			return -1;
		}
		return mWorkingBitmap.getHeight();
	}

	public boolean isBitmapNull() {
		return mWorkingBitmap == null;
	}
}
