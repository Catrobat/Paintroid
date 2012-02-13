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
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.commandmanagement.Command;
import at.tugraz.ist.paintroid.commandmanagement.UndoRedo;
import at.tugraz.ist.paintroid.commandmanagement.implementation.CommandUndoRedo;
import at.tugraz.ist.paintroid.ui.DrawingSurface;
import at.tugraz.ist.paintroid.ui.Perspective;

public class DrawingSurfaceView extends SurfaceView implements DrawingSurface {
	private DrawingSurfaceThread drawingThread;
	private Bitmap workingBitmap;
	private final Canvas workingBitmapCanvas;
	private boolean surfaceIsOK;
	private final Paint checkeredPattern;
	private final Paint clearPaint;
	protected Perspective surfacePerspective;
	protected UndoRedo undoRedo;

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
		surfaceViewCanvas.drawPaint(checkeredPattern);

		Command command = PaintroidApplication.COMMAND_HANDLER.getNextCommand();
		if (command != null) {
			command.run(workingBitmapCanvas);
			undoRedo.addCommand(command, workingBitmap);
			surfaceViewCanvas.drawBitmap(workingBitmap, 0, 0, null);
			PaintroidApplication.CURRENT_TOOL.resetInternalState();
		} else {
			surfaceViewCanvas.drawBitmap(workingBitmap, 0, 0, null);
			PaintroidApplication.CURRENT_TOOL.draw(surfaceViewCanvas);
		}
	}

	public DrawingSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getHolder().addCallback(this);

		workingBitmapCanvas = new Canvas();

		Bitmap checkerboard = BitmapFactory.decodeResource(getResources(), R.drawable.checkeredbg);
		BitmapShader shader = new BitmapShader(checkerboard, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		checkeredPattern = new Paint();
		checkeredPattern.setShader(shader);

		clearPaint = new Paint();
		clearPaint.setColor(Color.TRANSPARENT);
		clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
	}

	@Override
	public void setBitmap(Bitmap bitmap) {
		changeBitmap(bitmap);
		if (surfaceIsOK) {
			undoRedo.addDrawing(workingBitmap);
			drawingThread.start();
		}
	}

	protected void changeBitmap(Bitmap bitmap) {
		workingBitmap = bitmap;
		workingBitmapCanvas.setBitmap(bitmap);
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
		Log.w(PaintroidApplication.TAG, "DrawingSurfaceView.surfaceChanged");

		surfaceIsOK = true;
		surfacePerspective.reset(holder);

		if (workingBitmap != null) {
			undoRedo.addDrawing(workingBitmap);
			drawingThread.start();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.w(PaintroidApplication.TAG, "DrawingSurfaceView.surfaceCreated");
		drawingThread = new DrawingSurfaceThread(new DrawLoop());
		undoRedo = new CommandUndoRedo(this.getContext());
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.w(PaintroidApplication.TAG, "DrawingSurfaceView.surfaceDestroyed");
		drawingThread.stop();
		undoRedo.clear();
	}

	@Override
	public void clearBitmap() {
		workingBitmap.eraseColor(Color.TRANSPARENT);
	}

	public void undo() {
		changeBitmap(undoRedo.undo());
	}
}
