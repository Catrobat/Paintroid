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
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import at.tugraz.ist.paintroid.ui.DrawingSurface;

public class DrawingSurfaceView extends SurfaceView implements DrawingSurface, SurfaceHolder.Callback {
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

	public DrawingSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		drawingThread = new DrawingSurfaceThread(new DrawLoop());
	}

	@Override
	public void setBitmap(Bitmap bitmap) {
		surfaceBitmap = bitmap;
		drawingThread.start();
	}

	@Override
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
