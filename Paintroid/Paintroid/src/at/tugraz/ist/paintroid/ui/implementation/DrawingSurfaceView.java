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
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.commandmanagement.Command;
import at.tugraz.ist.paintroid.ui.DrawingSurface;

public class DrawingSurfaceView extends SurfaceView implements DrawingSurface {
	private final DrawingSurfaceThread drawingThread;
	private Bitmap surfaceBitmap;
	private Canvas surfaceBitmapCanvas;
	private boolean surfaceHasBeenCreated;

	private class DrawLoop implements Runnable {
		@Override
		public void run() {
			SurfaceHolder holder = getHolder();
			Canvas canvas = null;
			synchronized (holder) {
				try {
					canvas = holder.lockCanvas();

					Command command = MainActivity.getCommandHandler().getNextCommand();
					if (command != null) {
						command.run(surfaceBitmapCanvas);
					}
					canvas.drawBitmap(surfaceBitmap, 0, 0, null);
					MainActivity.getCurrentTool().draw(canvas);
				} finally {
					if (canvas != null) {
						holder.unlockCanvasAndPost(canvas);
					}
				}
			}
		}
	}

	public DrawingSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getHolder().addCallback(this);
		drawingThread = new DrawingSurfaceThread(new DrawLoop());
	}

	@Override
	public void setBitmap(Bitmap bitmap) {
		surfaceBitmap = bitmap;
		surfaceBitmapCanvas = new Canvas(bitmap);
		if (surfaceHasBeenCreated) {
			drawingThread.start();
		}
	}

	@Override
	public Bitmap getBitmap() {
		return surfaceBitmap;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.w(PaintroidApplication.TAG, "DrawingSurfaceView.surfaceChanged");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.w(PaintroidApplication.TAG, "DrawingSurfaceView.surfaceCreated");
		surfaceHasBeenCreated = true;
		if (surfaceBitmap != null) {
			drawingThread.start();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.w(PaintroidApplication.TAG, "DrawingSurfaceView.surfaceDestroyed");
		drawingThread.setPaused(true);
	}
}
