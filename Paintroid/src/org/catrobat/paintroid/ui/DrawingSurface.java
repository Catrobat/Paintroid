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

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.tools.implementation.BaseTool;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class DrawingSurface extends SurfaceView implements
		SurfaceHolder.Callback {
	protected static final String BUNDLE_INSTANCE_STATE = "BUNDLE_INSTANCE_STATE";
	protected static final String BUNDLE_PERSPECTIVE = "BUNDLE_PERSPECTIVE";
	protected static final int BACKGROUND_COLOR = Color.LTGRAY;

	private DrawingSurfaceThread mDrawingThread;
	private Bitmap mWorkingBitmap;
	private Bitmap[] mAllBitmaps;
	private Canvas[] mAllCanvas;

	private Rect mWorkingBitmapRect;
	private Paint mFramePaint;
	private Paint mClearPaint;
	protected boolean mSurfaceCanBeUsed;

	// private final static Paint mCheckeredPattern =
	// BaseTool.CHECKERED_PATTERN;

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
		if (mAllBitmaps != null) {
			for (int i = 0; i < mAllBitmaps.length; i++) {

				if (mAllBitmaps[i] != null) {
					mAllBitmaps[i].recycle();
				}
			}
		}
	}

	public synchronized void recycleCanvas() {
		for (int i = 0; i < mAllCanvas.length; i++) {

			if (mAllCanvas[i] != null) {
				mAllCanvas[i] = null;
			}
		}
	}

	private synchronized void doDraw(Canvas surfaceViewCanvas) {
		try {
			if (mWorkingBitmapRect == null
					|| surfaceViewCanvas == null
					|| mAllBitmaps[PaintroidApplication.currentLayer] == null
					|| mAllCanvas[PaintroidApplication.currentLayer] == null
					|| mAllBitmaps[PaintroidApplication.currentLayer]
							.isRecycled()) {
				return;
			}
			PaintroidApplication.perspective.applyToCanvas(surfaceViewCanvas);
			surfaceViewCanvas.drawColor(BACKGROUND_COLOR);
			surfaceViewCanvas.drawRect(mWorkingBitmapRect,
					BaseTool.CHECKERED_PATTERN);
			surfaceViewCanvas.drawRect(mWorkingBitmapRect, mFramePaint);
			Command command = null;

			while (mSurfaceCanBeUsed
					&& (command = PaintroidApplication.commandManager
							.getNextCommand()) != null) {

				command.run(mAllCanvas[PaintroidApplication.currentLayer],
						mAllBitmaps[PaintroidApplication.currentLayer]);

				surfaceViewCanvas.drawBitmap(
						mAllBitmaps[PaintroidApplication.currentLayer], 0, 0,
						null);

				PaintroidApplication.currentTool.resetInternalState();
			}

			if (mAllBitmaps[PaintroidApplication.currentLayer] != null
					&& !mAllBitmaps[PaintroidApplication.currentLayer]
							.isRecycled() && mSurfaceCanBeUsed) {

				for (int i = mAllBitmaps.length - 1; i >= 0; i--) {
					surfaceViewCanvas.drawBitmap(mAllBitmaps[i], 0, 0, null);
				}

				PaintroidApplication.currentTool.draw(surfaceViewCanvas);

			}
		} catch (Exception catchAllException) {
			Log.e(PaintroidApplication.TAG, "DrawingSurface:"
					+ catchAllException.getMessage() + "\r\n"
					+ catchAllException.toString());
			catchAllException.printStackTrace();
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

	private void init() {
		getHolder().addCallback(this);

		mWorkingBitmapRect = new Rect();

		mFramePaint = new Paint();
		mFramePaint.setColor(Color.BLACK);
		mFramePaint.setStyle(Paint.Style.STROKE);

		mClearPaint = new Paint();
		mClearPaint.setColor(Color.TRANSPARENT);
		mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
	}

	public synchronized void initLayer() {
		int numLayers = 5;

		recycleBitmap();

		mAllBitmaps = new Bitmap[numLayers];
		mAllCanvas = new Canvas[numLayers];

		for (int k = 0; k < numLayers; k++) {
			mAllBitmaps[k] = Bitmap.createBitmap(getScreenSize().x,
					getScreenSize().y, Config.ARGB_8888);
			mAllCanvas[k] = new Canvas(mAllBitmaps[k]);
		}
		mWorkingBitmap = mAllBitmaps[0];
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putParcelable(BUNDLE_INSTANCE_STATE, super.onSaveInstanceState());
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
			super.onRestoreInstanceState(bundle
					.getParcelable(BUNDLE_INSTANCE_STATE));
		} else {
			super.onRestoreInstanceState(state);
		}
	}

	public synchronized void resetBitmap(Bitmap bitmap) {
		PaintroidApplication.commandManager.resetAndClear();
		PaintroidApplication.commandManager.setOriginalBitmap(bitmap);
		setBitmap(bitmap);
		PaintroidApplication.perspective.resetScaleAndTranslation();
		if (mSurfaceCanBeUsed) {

			mDrawingThread.start();
		}
	}

	public synchronized void setBitmap(Bitmap bitmap) {
		if (mWorkingBitmap != null && bitmap != null) {
			// mWorkingBitmap.recycle();
		}
		if (bitmap != null) {
			mWorkingBitmap = bitmap;
			mAllBitmaps[PaintroidApplication.currentLayer] = bitmap;
			mAllCanvas[PaintroidApplication.currentLayer].setBitmap(bitmap);
			mWorkingBitmapRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
			PaintroidApplication.perspective.resetScaleAndTranslation();
		}
	}

	public synchronized Bitmap getBitmapCopy() {

		Bitmap fullBitmap = Bitmap.createBitmap(getScreenSize().x,
				getScreenSize().y, Config.ARGB_8888);
		Canvas fullBitmapCanvas = new Canvas(fullBitmap);

		for (int i = mAllBitmaps.length - 1; i >= 0; i--) {
			if (mAllBitmaps[i] != null && !mAllBitmaps[i].isRecycled()) {
				fullBitmapCanvas.drawBitmap(mAllBitmaps[i], 0, 0, null);
			}
		}
		return fullBitmap;
	}

	public synchronized Bitmap getBitmapCopy(int pos) {
		if (mAllBitmaps[pos] != null && mAllBitmaps[pos].isRecycled() == false) {
			return Bitmap.createBitmap(mAllBitmaps[pos]);
		} else {
			return null;
		}
	}

	public synchronized Bitmap getBitmap(int pos) {
		if (mAllBitmaps[pos] != null && mAllBitmaps[pos].isRecycled() == false) {
			return mAllBitmaps[pos];
		} else {
			return null;
		}
	}

	public synchronized boolean isDrawingSurfaceBitmapValid() {
		if (mAllBitmaps[PaintroidApplication.currentLayer] == null
				|| mAllBitmaps[PaintroidApplication.currentLayer].isRecycled()
				|| mSurfaceCanBeUsed == false) {
			return false;
		}
		return true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mSurfaceCanBeUsed = true;
		Log.w(PaintroidApplication.TAG, "DrawingSurfaceView.surfaceChanged"); // TODO
																				// remove
																				// logging
		PaintroidApplication.perspective.setSurfaceHolder(holder);

		if (mAllBitmaps[PaintroidApplication.currentLayer] != null
				&& mDrawingThread != null) {
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

	public int getPixel(PointF coordinate) {
		try {
			if (mAllBitmaps[PaintroidApplication.currentLayer] != null
					&& mAllBitmaps[PaintroidApplication.currentLayer]
							.isRecycled() == false) {
				return mAllBitmaps[PaintroidApplication.currentLayer].getPixel(
						(int) coordinate.x, (int) coordinate.y);
			}
		} catch (IllegalArgumentException e) {
			Log.w(PaintroidApplication.TAG,
					"getBitmapColor coordinate out of bounds");
		}
		return Color.TRANSPARENT;
	}

	public void getPixels(int[] pixels, int offset, int stride, int x, int y,
			int width, int height) {
		if (mAllBitmaps[PaintroidApplication.currentLayer] != null
				&& mAllBitmaps[PaintroidApplication.currentLayer].isRecycled() == false) {
			mAllBitmaps[PaintroidApplication.currentLayer].getPixels(pixels,
					offset, stride, x, y, width, height);
		}
	}

	public int getBitmapWidth() {
		if (mAllBitmaps[0] == null) {
			return -1;
		}
		return mAllBitmaps[0].getWidth();
	}

	public int getBitmapHeight() {
		if (mAllBitmaps[0] == null) {
			return -1;
		}
		return mAllBitmaps[0].getHeight();
	}

	@TargetApi(13)
	private Point getScreenSize() {

		Point p = new Point();
		WindowManager wm = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR1) {

			display.getSize(p);

		} else {

			p.x = display.getWidth(); // deprecated
			p.y = display.getHeight(); // deprecated
		}

		return p;
	}
}
