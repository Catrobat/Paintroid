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
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.command.implementation.LoadCommand;
import org.catrobat.paintroid.dialog.LayersDialog;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.catrobat.paintroid.ui.button.LayersAdapter;

public class DrawingSurface extends SurfaceView implements
		SurfaceHolder.Callback {
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
	public Bitmap mTestBitmap;

	// private final static Paint mCheckeredPattern =
	// BaseTool.CHECKERED_PATTERN;


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
		@Override
		public void run() {
			SurfaceHolder holder = getHolder();
			Canvas canvas = null;

			if (Build.VERSION.SDK_INT >= 18) { // TODO: set build flag
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					Log.w(PaintroidApplication.TAG, "DrawingSurface: sleeping thread was interrupted");
				}
			}

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
			//mWorkingBitmap.recycle();
		}
	}

	private synchronized void doDraw(Canvas surfaceViewCanvas) {
		try {
			if (mWorkingBitmapRect == null || surfaceViewCanvas == null
					|| mWorkingBitmap == null || mWorkingBitmapCanvas == null
					|| mWorkingBitmap.isRecycled()) {
				return;
			}
			PaintroidApplication.perspective.applyToCanvas(surfaceViewCanvas);
			surfaceViewCanvas.drawColor(BACKGROUND_COLOR);
			surfaceViewCanvas.drawRect(mWorkingBitmapRect,
					BaseTool.CHECKERED_PATTERN);
			surfaceViewCanvas.drawRect(mWorkingBitmapRect, mFramePaint);

			if (mWorkingBitmap != null && !mWorkingBitmap.isRecycled()
					&& mSurfaceCanBeUsed) {
				//LayersDialog layersDialog = LayersDialog.getInstance();
				//LayersAdapter layersAdapter = layersDialog.getAdapter();

				LayerListener layersListener = LayerListener.getInstance();
				LayersAdapter layersAdapter = layersListener.getAdapter();
				mOpacityPaint = new Paint();
				//mOpacityPaint.setAlpha(layersDialog.getCurrentLayer().getScaledOpacity());

				for (int i = layersAdapter.getCount() - 1; i >= 0; i--) {
					if (layersAdapter.getLayer(i).getVisible()) {
						mOpacityPaint.setAlpha(layersAdapter.getLayer(i).getScaledOpacity());
						if (!layersAdapter.getLayer(i).equals(layersListener.getCurrentLayer())) {
							Bitmap bitmapDrawable = layersAdapter.getLayer(i).getImage();
							surfaceViewCanvas.drawBitmap(bitmapDrawable, 0, 0, mOpacityPaint);
						} else {
							surfaceViewCanvas.drawBitmap(mWorkingBitmap, 0, 0, mOpacityPaint);
						}
					}
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
		if (mWorkingBitmap != null && bitmap != null) {
			//mWorkingBitmap.recycle();
		}
		if (bitmap != null) {
			mWorkingBitmap = bitmap;
			mWorkingBitmapCanvas.setBitmap(mWorkingBitmap);
			mWorkingBitmapRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
		}
	}

	public synchronized Bitmap getBitmapCopy() {
		if (mWorkingBitmap != null && mWorkingBitmap.isRecycled() == false) {
			return Bitmap.createBitmap(mWorkingBitmap);
		} else {
			return null;
		}
	}

	public synchronized int getWorkingBitmapOrientation(){
		if(mWorkingBitmap.getHeight() > mWorkingBitmap.getWidth())
			return Configuration.ORIENTATION_PORTRAIT;
		else
			return Configuration.ORIENTATION_LANDSCAPE;
	}

	public synchronized boolean isDrawingSurfaceBitmapValid() {
		if (mWorkingBitmap == null || mWorkingBitmap.isRecycled()
				|| mSurfaceCanBeUsed == false) {
			return false;
		}
		return true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		mSurfaceCanBeUsed = true;
		PaintroidApplication.perspective.setSurfaceHolder(holder);

		if (mWorkingBitmap != null && mDrawingThread != null) {
			mDrawingThread.start();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mDrawingThread = new DrawingSurfaceThread(new DrawLoop());
	}

	@Override
	public synchronized void surfaceDestroyed(SurfaceHolder holder) {
		mSurfaceCanBeUsed = false;
		if (mDrawingThread != null) {
			mDrawingThread.stop();
		}
	}

	public int getPixel(PointF coordinate) {
		try {
			if (mWorkingBitmap != null && mWorkingBitmap.isRecycled() == false) {
				return mWorkingBitmap.getPixel((int) coordinate.x,(int) coordinate.y);
			}
		} catch (IllegalArgumentException e) {
			Log.w(PaintroidApplication.TAG,
					"getBitmapColor coordinate out of bounds");
		}
		return Color.TRANSPARENT;
	}

	public int getVisiblePixel(PointF coordinate) {
		try {
			if (mTestBitmap != null && mTestBitmap.isRecycled() == false) {
				return mTestBitmap.getPixel((int) coordinate.x,
						(int) coordinate.y);
			}
		} catch (IllegalArgumentException e) {
			Log.w(PaintroidApplication.TAG,
					"getBitmapColor coordinate out of bounds");
		}
		return Color.TRANSPARENT;
	}

	public void getPixels(int[] pixels, int offset, int stride, int x, int y,
						  int width, int height) {
		if (mWorkingBitmap != null && mWorkingBitmap.isRecycled() == false) {
			mWorkingBitmap.getPixels(pixels, offset, stride, x, y, width,
					height);
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
		if(mWorkingBitmap == null)
			return true;
		else
			return false;
	}
}
