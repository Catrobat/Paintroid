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

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.Perspective;

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

public class DrawingSurfaceImplementation extends SurfaceView implements
		DrawingSurface {
	protected static final String BUNDLE_INSTANCE_STATE = "BUNDLE_INSTANCE_STATE";
	protected static final String BUNDLE_PERSPECTIVE = "BUNDLE_PERSPECTIVE";
	protected static final int BACKGROUND_COLOR = Color.LTGRAY;

	private DrawingSurfaceThread mDrawingThread;
	private Bitmap mWorkingBitmap;
	private final Rect mWorkingBitmapRect;
	private final Canvas mWorkingBitmapCanvas;
	private final Paint mFramePaint;
	private final Paint mClearPaint;
	protected boolean mSurfaceCanBeUsed;

	private class DrawLoop implements Runnable {
		@Override
		public void run() {
			SurfaceHolder holder = getHolder();
			Canvas canvas = null;
			synchronized (holder) {
				try {
					canvas = holder.lockCanvas();
					if (canvas != null && mSurfaceCanBeUsed == true) {
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

	public synchronized void recycleBitmap() {
		if (mWorkingBitmap != null) {
			mWorkingBitmap.recycle();
		}
	}

	private synchronized void doDraw(Canvas surfaceViewCanvas) {
		try {
			PaintroidApplication.CURRENT_PERSPECTIVE
					.applyToCanvas(surfaceViewCanvas);
			surfaceViewCanvas.drawColor(BACKGROUND_COLOR);
			surfaceViewCanvas.drawRect(mWorkingBitmapRect,
					BaseTool.CHECKERED_PATTERN);
			surfaceViewCanvas.drawRect(mWorkingBitmapRect, mFramePaint);
			Command command = null;
			while (mSurfaceCanBeUsed
					&& mWorkingBitmap != null
					&& mWorkingBitmapCanvas != null
					&& mWorkingBitmap.isRecycled() == false
					&& (command = PaintroidApplication.COMMAND_MANAGER
							.getNextCommand()) != null) {

				command.run(mWorkingBitmapCanvas, mWorkingBitmap);
				surfaceViewCanvas.drawBitmap(mWorkingBitmap, 0, 0, null);
				PaintroidApplication.CURRENT_TOOL.resetInternalState();
			}

			if (mWorkingBitmap != null && !mWorkingBitmap.isRecycled()
					&& mSurfaceCanBeUsed) {
				surfaceViewCanvas.drawBitmap(mWorkingBitmap, 0, 0, null);
				PaintroidApplication.CURRENT_TOOL.draw(surfaceViewCanvas);
			}
		} catch (Exception catchAllException) {
			Log.e(PaintroidApplication.TAG, catchAllException.toString());
		}
	}

	public DrawingSurfaceImplementation(Context context, AttributeSet attrs) {
		super(context, attrs);
		getHolder().addCallback(this);

		mWorkingBitmapRect = new Rect();
		mWorkingBitmapCanvas = new Canvas();

		mFramePaint = new Paint();
		mFramePaint.setColor(Color.BLACK);
		mFramePaint.setStyle(Paint.Style.STROKE);

		mClearPaint = new Paint();
		mClearPaint.setColor(Color.TRANSPARENT);
		mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putParcelable(BUNDLE_INSTANCE_STATE, super.onSaveInstanceState());
		bundle.putSerializable(BUNDLE_PERSPECTIVE,
				PaintroidApplication.CURRENT_PERSPECTIVE);
		return bundle;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			PaintroidApplication.CURRENT_PERSPECTIVE = (Perspective) bundle
					.getSerializable(BUNDLE_PERSPECTIVE);
			super.onRestoreInstanceState(bundle
					.getParcelable(BUNDLE_INSTANCE_STATE));
		} else {
			super.onRestoreInstanceState(state);
		}
	}

	@Override
	public synchronized void resetBitmap(Bitmap bitmap) {
		PaintroidApplication.COMMAND_MANAGER.resetAndClear();
		PaintroidApplication.COMMAND_MANAGER.setOriginalBitmap(bitmap);
		setBitmap(bitmap);
		// PaintroidApplication.CURRENT_PERSPECTIVE.resetScaleAndTranslation();
		if (mSurfaceCanBeUsed) {
			mDrawingThread.start();
		}
	}

	@Override
	public synchronized void setBitmap(Bitmap bitmap) {
		if (mWorkingBitmap != null && bitmap != null) {
			mWorkingBitmap.recycle();
		}
		if (bitmap != null) {
			mWorkingBitmap = bitmap;
			mWorkingBitmapCanvas.setBitmap(bitmap);
			mWorkingBitmapRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
			// PaintroidApplication.CURRENT_PERSPECTIVE.resetScaleAndTranslation();
		}
	}

	@Override
	public synchronized Bitmap getBitmap() {
		if (mWorkingBitmap != null && mWorkingBitmap.isRecycled() == false) {
			return Bitmap.createBitmap(mWorkingBitmap);
		} else {
			return null;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mSurfaceCanBeUsed = true;
		Log.w(PaintroidApplication.TAG, "DrawingSurfaceView.surfaceChanged"); // TODO
																				// remove
																				// logging
		PaintroidApplication.CURRENT_PERSPECTIVE.setSurfaceHolder(holder);

		if (mWorkingBitmap != null && mDrawingThread != null) {
			mDrawingThread.start();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.w(PaintroidApplication.TAG, "DrawingSurfaceView.surfaceCreated"); // TODO
																				// remove
																				// logging

		mDrawingThread = new DrawingSurfaceThread(new DrawLoop());
	}

	@Override
	public synchronized void surfaceDestroyed(SurfaceHolder holder) {
		mSurfaceCanBeUsed = false;
		Log.w(PaintroidApplication.TAG, "DrawingSurfaceView.surfaceDestroyed"); // TODO
																				// remove
																				// logging
		if (mDrawingThread != null) {
			mDrawingThread.stop();
		}
	}

	@Override
	public int getBitmapColor(PointF coordinate) {
		try {
			if (mWorkingBitmap != null && mWorkingBitmap.isRecycled() == false) {
				return mWorkingBitmap.getPixel((int) coordinate.x,
						(int) coordinate.y);
			}
		} catch (IllegalArgumentException e) {
			Log.w(PaintroidApplication.TAG,
					"getBitmapColor coordinate out of bounds");
		}
		return Color.TRANSPARENT;
	}

	@Override
	public void getPixels(int[] pixels, int offset, int stride, int x, int y,
			int width, int height) {
		if (mWorkingBitmap != null && mWorkingBitmap.isRecycled() == false) {
			mWorkingBitmap.getPixels(pixels, offset, stride, x, y, width,
					height);
		}
	}

	@Override
	public int getBitmapWidth() {
		if (mWorkingBitmap == null) {
			return -1;
		}
		return mWorkingBitmap.getWidth();
	}

	@Override
	public int getBitmapHeight() {
		if (mWorkingBitmap == null) {
			return -1;
		}
		return mWorkingBitmap.getHeight();
	}
}
