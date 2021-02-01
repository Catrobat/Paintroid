/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.listener.DrawingSurfaceListener;
import org.catrobat.paintroid.listener.DrawingSurfaceListener.AutoScrollTask;
import org.catrobat.paintroid.listener.DrawingSurfaceListener.AutoScrollTaskCallback;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolReference;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.options.ToolOptionsViewController;

import java.util.ListIterator;

import androidx.core.content.ContextCompat;

public class DrawingSurface extends SurfaceView implements SurfaceHolder.Callback {
	private final Rect canvasRect = new Rect();
	private final Paint framePaint = new Paint();
	private final Paint checkeredPattern = new Paint();
	private final Object surfaceLock = new Object();
	private boolean surfaceDirty = false;
	private boolean surfaceReady = false;
	private int backgroundColor;

	private DrawingSurfaceThread drawingThread;
	private LayerContracts.Model layerModel;
	private Perspective perspective;
	private DrawingSurfaceListener drawingSurfaceListener;
	private ToolReference toolReference;
	private ToolOptionsViewController toolOptionsViewController;

	public DrawingSurface(Context context, AttributeSet attrSet) {
		super(context, attrSet);
		init();
	}

	public DrawingSurface(Context context) {
		super(context);
		init();
	}

	private void init() {
		backgroundColor = ContextCompat.getColor(getContext(),
				R.color.pocketpaint_main_drawing_surface_background);

		framePaint.setColor(Color.BLACK);
		framePaint.setStyle(Paint.Style.STROKE);
		framePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

		Bitmap checkerboard = BitmapFactory.decodeResource(getResources(), R.drawable.pocketpaint_checkeredbg);
		BitmapShader shader = new BitmapShader(checkerboard, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		checkeredPattern.setShader(shader);
		checkeredPattern.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

		Handler handler = new Handler(Looper.getMainLooper());
		AutoScrollTask autoScrollTask = new AutoScrollTask(handler, new AutoScrollTaskCallbackImpl());
		float density = getResources().getDisplayMetrics().density;
		DrawingSurfaceListener.DrawingSurfaceListenerCallback callback = new DrawingSurfaceListener.DrawingSurfaceListenerCallback() {
			@Override
			public Tool getCurrentTool() {
				return toolReference.get();
			}

			@Override
			public void multiplyPerspectiveScale(float factor) {
				perspective.multiplyScale(factor);
			}

			@Override
			public void translatePerspective(float x, float y) {
				perspective.translate(x, y);
			}

			@Override
			public void convertToCanvasFromSurface(PointF surfacePoint) {
				perspective.convertToCanvasFromSurface(surfacePoint);
			}

			@Override
			public ToolOptionsViewController getToolOptionsViewController() {
				return toolOptionsViewController;
			}
		};
		drawingSurfaceListener = new DrawingSurfaceListener(autoScrollTask, callback, density);
		setOnTouchListener(drawingSurfaceListener);
	}

	public void setArguments(LayerContracts.Model layerModel, Perspective perspective, ToolReference toolReference, ToolOptionsViewController toolOptionsViewController) {
		this.layerModel = layerModel;
		this.perspective = perspective;
		this.toolReference = toolReference;
		this.toolOptionsViewController = toolOptionsViewController;
	}

	private synchronized void doDraw(Canvas surfaceViewCanvas) {
		final LayerContracts.Model model = layerModel;

		synchronized (model) {
			if (surfaceReady) {

				canvasRect.set(0, 0, model.getWidth(), model.getHeight());

				perspective.applyToCanvas(surfaceViewCanvas);

				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
					surfaceViewCanvas.drawColor(backgroundColor, PorterDuff.Mode.SRC);
				} else {
					surfaceViewCanvas.save();
					surfaceViewCanvas.clipOutRect(canvasRect);
					surfaceViewCanvas.drawColor(backgroundColor, PorterDuff.Mode.SRC);
					surfaceViewCanvas.restore();
				}

				surfaceViewCanvas.drawRect(canvasRect, checkeredPattern);
				surfaceViewCanvas.drawRect(canvasRect, framePaint);

				ListIterator<LayerContracts.Layer> iterator = model.listIterator(model.getLayerCount());
				while (iterator.hasPrevious()) {
					surfaceViewCanvas.drawBitmap(iterator.previous().getBitmap(), 0, 0, null);
				}

				Tool tool = toolReference.get();
				if (tool != null) {
					tool.draw(surfaceViewCanvas);
				}
			}
		}
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

	public void refreshDrawingSurface() {
		synchronized (surfaceLock) {
			surfaceDirty = true;
			surfaceLock.notify();
		}
	}

	public void enableAutoScroll() {
		drawingSurfaceListener.enableAutoScroll();
	}

	public void disableAutoScroll() {
		drawingSurfaceListener.disableAutoScroll();
	}

	public synchronized void setBitmap(Bitmap bitmap) {
		layerModel.getCurrentLayer().setBitmap(bitmap);
	}

	public synchronized boolean isPointOnCanvas(int pointX, int pointY) {
		return pointX > 0 && pointX < layerModel.getWidth()
				&& pointY > 0 && pointY < layerModel.getHeight();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		surfaceReady = true;
		ToolType currentToolType = toolReference.get().getToolType();

		if (currentToolType != ToolType.IMPORTPNG && currentToolType != ToolType.TRANSFORM && currentToolType != ToolType.TEXT) {
			perspective.resetScaleAndTranslation();
		}
		perspective.setSurfaceFrame(holder.getSurfaceFrame());

		if (drawingThread != null) {
			drawingThread.start();
		}

		refreshDrawingSurface();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		holder.setFormat(PixelFormat.RGBA_8888);

		if (drawingThread != null) {
			drawingThread.stop();
		}
		drawingThread = new DrawingSurfaceThread(this, new DrawLoop());
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		surfaceReady = false;
		if (drawingThread != null) {
			drawingThread.stop();
		}
	}

	private class AutoScrollTaskCallbackImpl implements AutoScrollTaskCallback {
		public boolean isPointOnCanvas(int pointX, int pointY) {
			return DrawingSurface.this.isPointOnCanvas(pointX, pointY);
		}

		public void refreshDrawingSurface() {
			DrawingSurface.this.refreshDrawingSurface();
		}

		public void handleToolMove(PointF coordinate) {
			toolReference.get().handleMove(coordinate);
		}

		public Point getToolAutoScrollDirection(float pointX, float pointY, int screenWidth, int screenHeight) {
			return toolReference.get().getAutoScrollDirection(pointX, pointY, screenWidth, screenHeight);
		}

		public float getPerspectiveScale() {
			return perspective.getScale();
		}

		public void translatePerspective(float dx, float dy) {
			perspective.translate(dx, dy);
		}

		public void convertToCanvasFromSurface(PointF surfacePoint) {
			perspective.convertToCanvasFromSurface(surfacePoint);
		}

		public ToolType getCurrentToolType() {
			return toolReference.get().getToolType();
		}
	}

	private class DrawLoop implements Runnable {
		final SurfaceHolder holder = getHolder();

		@Override
		public void run() {

			synchronized (surfaceLock) {
				if (!surfaceDirty && surfaceReady) {
					try {
						surfaceLock.wait();
					} catch (InterruptedException e) {
						return;
					}
				} else {
					surfaceDirty = false;
				}

				if (!surfaceReady) {
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
}
