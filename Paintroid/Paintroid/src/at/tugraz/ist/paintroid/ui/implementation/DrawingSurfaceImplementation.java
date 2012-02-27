/*
 *   This file is part of Paintroid, a software part of the Catroid project.
 *   Copyright (C) 2010  Catroid development team
 *   <http://code.google.com/p/catroid/wiki/Credits>
 *
 *   Paintroid is free software: you can redistribute it and/or modify it
 *   under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

	private DrawingSurfaceThread drawingThread;
	private Bitmap workingBitmap;
	private final Rect workingBitmapRect;
	private final Canvas workingBitmapCanvas;
	private final Paint framePaint;
	private final Paint clearPaint;
	protected Perspective surfacePerspective;
	// protected UndoRedo undoRedo;
	protected boolean surfaceCanBeUsed;

	private class DrawLoop implements Runnable {
		@Override
		public void run() {
			SurfaceHolder holder = getHolder();
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

	private void doDraw(Canvas surfaceViewCanvas) {
		surfacePerspective.applyToCanvas(surfaceViewCanvas);

		surfaceViewCanvas.drawColor(BACKGROUND_COLOR);
		surfaceViewCanvas.drawRect(workingBitmapRect, BaseTool.CHECKERED_PATTERN);
		surfaceViewCanvas.drawRect(workingBitmapRect, framePaint);

		Command command = PaintroidApplication.COMMAND_HANDLER.getNextCommand();
		if (command != null) {
			command.run(workingBitmapCanvas, workingBitmap);
			// undoRedo.addCommand(command);
			surfaceViewCanvas.drawBitmap(workingBitmap, 0, 0, null);
			PaintroidApplication.CURRENT_TOOL.resetInternalState();
		} else {
			surfaceViewCanvas.drawBitmap(workingBitmap, 0, 0, null);
			PaintroidApplication.CURRENT_TOOL.draw(surfaceViewCanvas, true);
		}
	}

	public DrawingSurfaceImplementation(Context context, AttributeSet attrs) {
		super(context, attrs);
		getHolder().addCallback(this);

		workingBitmapRect = new Rect();
		workingBitmapCanvas = new Canvas();
		// undoRedo = new UndoRedoImplementation(this.getContext());

		framePaint = new Paint();
		framePaint.setColor(Color.BLACK);
		framePaint.setStyle(Paint.Style.STROKE);

		clearPaint = new Paint();
		clearPaint.setColor(Color.TRANSPARENT);
		clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putParcelable(BUNDLE_INSTANCE_STATE, super.onSaveInstanceState());
		bundle.putSerializable(BUNDLE_PERSPECTIVE, surfacePerspective);
		return bundle;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			surfacePerspective = (Perspective) bundle.getSerializable(BUNDLE_PERSPECTIVE);
			super.onRestoreInstanceState(bundle.getParcelable(BUNDLE_INSTANCE_STATE));
		} else {
			super.onRestoreInstanceState(state);
		}
	}

	@Override
	public void resetBitmap(Bitmap bitmap) {
		PaintroidApplication.COMMAND_HANDLER.resetAndClear();
		PaintroidApplication.COMMAND_HANDLER.setOriginalBitmap(bitmap);
		surfacePerspective.resetScaleAndTranslation();
		setBitmap(bitmap);
		if (surfaceCanBeUsed) {
			drawingThread.start();
		}
	}

	@Override
	public void setBitmap(Bitmap bitmap) {
		if (workingBitmap != null) {
			workingBitmap.recycle();
		}
		workingBitmap = bitmap;
		workingBitmapCanvas.setBitmap(bitmap);
		workingBitmapRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
	}

	@Override
	public Bitmap getBitmap() {
		return workingBitmap;
	}

	@Override
	public void setPerspective(Perspective perspective) {
		surfacePerspective = perspective;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.w(PaintroidApplication.TAG, "DrawingSurfaceView.surfaceChanged"); // TODO remove logging

		surfaceCanBeUsed = true;

		surfacePerspective.setSurfaceHolder(holder);

		if (workingBitmap != null) {
			drawingThread.start();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.w(PaintroidApplication.TAG, "DrawingSurfaceView.surfaceCreated"); // TODO remove logging

		drawingThread = new DrawingSurfaceThread(new DrawLoop());
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.w(PaintroidApplication.TAG, "DrawingSurfaceView.surfaceDestroyed"); // TODO remove logging

		drawingThread.stop();
	}

	@Override
	public void clearBitmap() {
		workingBitmap.eraseColor(Color.TRANSPARENT);
	}

	@Override
	public int getBitmapColor(PointF coordinate) {
		return workingBitmap.getPixel((int) coordinate.x, (int) coordinate.y);
	}
}
