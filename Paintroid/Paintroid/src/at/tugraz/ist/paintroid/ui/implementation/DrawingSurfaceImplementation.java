/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  Paintroid: An image manipulation application for Android, part of the
 *  Catroid project and Catroid suite of software.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.ui.implementation;

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
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.command.Command;
import at.tugraz.ist.paintroid.tools.implementation.BaseTool;
import at.tugraz.ist.paintroid.ui.DrawingSurface;
import at.tugraz.ist.paintroid.ui.Perspective;

public class DrawingSurfaceImplementation extends SurfaceView implements DrawingSurface {
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

	private void doDraw(Canvas surfaceViewCanvas) {
		int logCount = 0;
		Log.d(PaintroidApplication.TAG, "DrawingSurfaceImplementation.doDraw" + logCount++);
		PaintroidApplication.CURRENT_PERSPECTIVE.applyToCanvas(surfaceViewCanvas);
		Log.d(PaintroidApplication.TAG, "DrawingSurfaceImplementation.doDraw" + logCount++);
		surfaceViewCanvas.drawColor(BACKGROUND_COLOR);
		surfaceViewCanvas.drawRect(mWorkingBitmapRect, BaseTool.CHECKERED_PATTERN);
		surfaceViewCanvas.drawRect(mWorkingBitmapRect, mFramePaint);
		Log.d(PaintroidApplication.TAG, "DrawingSurfaceImplementation.doDraw" + logCount++);
		Command command = PaintroidApplication.COMMAND_MANAGER.getNextCommand();
		while (command != null && mWorkingBitmap != null && mWorkingBitmapCanvas != null
				&& mWorkingBitmap.isRecycled() == false) {
			Log.d(PaintroidApplication.TAG, "DrawingSurfaceImplementation.doDraw" + logCount++);
			command.run(mWorkingBitmapCanvas, mWorkingBitmap);
			Log.d(PaintroidApplication.TAG, "DrawingSurfaceImplementation.doDraw" + logCount++);
			surfaceViewCanvas.drawBitmap(mWorkingBitmap, 0, 0, null);
			Log.d(PaintroidApplication.TAG, "DrawingSurfaceImplementation.doDraw" + logCount++);
			PaintroidApplication.CURRENT_TOOL.resetInternalState();
			Log.d(PaintroidApplication.TAG, "DrawingSurfaceImplementation.doDraw" + logCount++);
			command = PaintroidApplication.COMMAND_MANAGER.getNextCommand();
			Log.d(PaintroidApplication.TAG, "DrawingSurfaceImplementation.doDraw" + logCount++);
		}
		Log.d(PaintroidApplication.TAG, "DrawingSurfaceImplementation.doDraw" + logCount++);
		if (mWorkingBitmap != null && !mWorkingBitmap.isRecycled()) {
			Log.d(PaintroidApplication.TAG, "DrawingSurfaceImplementation.doDraw" + logCount++);
			surfaceViewCanvas.drawBitmap(mWorkingBitmap, 0, 0, null);
			Log.d(PaintroidApplication.TAG, "DrawingSurfaceImplementation.doDraw" + logCount++);
			PaintroidApplication.CURRENT_TOOL.draw(surfaceViewCanvas, true);
			Log.d(PaintroidApplication.TAG, "DrawingSurfaceImplementation.doDraw" + logCount++);
		}
		Log.d(PaintroidApplication.TAG, "DrawingSurfaceImplementation.doDraw" + logCount++);
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
		bundle.putSerializable(BUNDLE_PERSPECTIVE, PaintroidApplication.CURRENT_PERSPECTIVE);
		return bundle;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			PaintroidApplication.CURRENT_PERSPECTIVE = (Perspective) bundle.getSerializable(BUNDLE_PERSPECTIVE);
			super.onRestoreInstanceState(bundle.getParcelable(BUNDLE_INSTANCE_STATE));
		} else {
			super.onRestoreInstanceState(state);
		}
	}

	@Override
	public void resetBitmap(Bitmap bitmap) {
		PaintroidApplication.COMMAND_MANAGER.resetAndClear();
		PaintroidApplication.COMMAND_MANAGER.setOriginalBitmap(bitmap);
		PaintroidApplication.CURRENT_PERSPECTIVE.resetScaleAndTranslation();
		setBitmap(bitmap);
		if (mSurfaceCanBeUsed) {
			mDrawingThread.start();
		}
	}

	@Override
	public synchronized void setBitmap(Bitmap bitmap) {
		if (mWorkingBitmap != null) {
			mWorkingBitmap.recycle();
		}
		if (bitmap != null) {
			mWorkingBitmap = bitmap;
			mWorkingBitmapCanvas.setBitmap(bitmap);
			mWorkingBitmapRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
		}
	}

	@Override
	public Bitmap getBitmap() {
		return mWorkingBitmap;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.w(PaintroidApplication.TAG, "DrawingSurfaceView.surfaceChanged"); // TODO remove logging

		mSurfaceCanBeUsed = true;

		PaintroidApplication.CURRENT_PERSPECTIVE.setSurfaceHolder(holder);

		if (mWorkingBitmap != null && mDrawingThread != null) {
			mDrawingThread.start();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.w(PaintroidApplication.TAG, "DrawingSurfaceView.surfaceCreated"); // TODO remove logging

		mDrawingThread = new DrawingSurfaceThread(new DrawLoop());
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.w(PaintroidApplication.TAG, "DrawingSurfaceView.surfaceDestroyed"); // TODO remove logging
		if (mDrawingThread != null) {
			mDrawingThread.stop();
		}
	}

	@Override
	public int getBitmapColor(PointF coordinate) {
		try {
			return mWorkingBitmap.getPixel((int) coordinate.x, (int) coordinate.y);
		} catch (IllegalArgumentException e) {
			Log.w(PaintroidApplication.TAG, "getBitmapColor coordinate out of bounds");
		}
		return Color.TRANSPARENT;
	}

	public void recycleBitmap() {
		mWorkingBitmap.recycle();
	}
}
