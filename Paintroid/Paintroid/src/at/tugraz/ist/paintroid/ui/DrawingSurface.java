package at.tugraz.ist.paintroid.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DrawingSurface extends SurfaceView implements SurfaceHolder.Callback {
	private final DrawingSurfaceThread drawingThread;
	private Bitmap surfaceBitmap;

	private class DrawLoop implements Runnable {
		@Override
		public void run() {
			SurfaceHolder holder = getHolder();
			Canvas canvas = null;
			synchronized (holder) {
				try {
					canvas = getHolder().lockCanvas();
					// commandQueue.next().run();
					// canvas.drawBitmap(surfaceBitmap, 0, 0, null);
					// selectedTool.draw();
				} finally {
					if (canvas != null) {
						getHolder().unlockCanvasAndPost(canvas);
					}
				}
			}
		}
	}

	public DrawingSurface(Context context) {
		super(context);
		drawingThread = new DrawingSurfaceThread(new DrawLoop());
	}

	/**
	 * Sets the Bitmap to draw on and attempts to start the drawing thread.
	 * 
	 * @param bitmap The Bitmap to draw on.
	 */
	public void setBitmap(Bitmap bitmap) {
		surfaceBitmap = bitmap;
		drawingThread.start();
	}

	/**
	 * @return The actual instance of the Bitmap that is currently being drawn on.
	 */
	public Bitmap getBitmap() {
		return surfaceBitmap;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (surfaceBitmap != null) {
			drawingThread.start();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		drawingThread.setPaused(true);
	}
}
