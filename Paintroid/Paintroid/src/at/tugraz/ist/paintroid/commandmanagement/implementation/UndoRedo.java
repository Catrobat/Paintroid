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

package at.tugraz.ist.paintroid.commandmanagement.implementation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

@Deprecated
public class UndoRedo {
	private Vector<UndoStackObject> undoStack;
	private Vector<RedoStackObject> redoStack;
	private Context mContext;

	public UndoRedo(Context context) {
		mContext = context;
		undoStack = new Vector<UndoStackObject>();
		redoStack = new Vector<RedoStackObject>();
		clear();
	}

	public synchronized Bitmap undo() {
		UndoStackObject undoStackObject = undoStack.get(undoStack.size() - 1);
		Bitmap undoBitmap;

		if (!undoStackObject.hasActions() && undoStack.size() > 1) {
			if (redoStack.size() == 1) {
				UndoStackObject actualUndoStackObject = undoStack.get(undoStack.size() - 1);
				saveBitmapToTemp(actualUndoStackObject.getAndRemoveBitmap(), undoStack.size() - 1);
			}
			RedoStackObject newRedoStackObject = new RedoStackObject();
			redoStack.add(newRedoStackObject);
			undoStack.remove(undoStack.size() - 1);
			UndoStackObject previousUndoStackObject = undoStack.get(undoStack.size() - 1);

			Bitmap cachedBitmap = getBitmapFromTemp(undoStack.size() - 1);

			if (cachedBitmap == null) {
				return null;
			}
			previousUndoStackObject.addBitmap(cachedBitmap);
			undoBitmap = previousUndoStackObject.drawAll();
		} else {
			undoBitmap = undoStackObject.undo(redoStack.get(redoStack.size() - 1));
		}
		return undoBitmap;
	}

	public synchronized Bitmap redo() {
		RedoStackObject redoStackObject = redoStack.get(redoStack.size() - 1);
		Bitmap redoBitmap;
		UndoStackObject undoStackObject;

		if (!redoStackObject.hasActions() && redoStack.size() > 1) {
			undoStackObject = new UndoStackObject();

			Bitmap cachedBitmap = getBitmapFromTemp(undoStack.size());

			if (cachedBitmap == null) {
				return null;
			}
			undoStackObject.addBitmap(cachedBitmap);

			undoStack.add(undoStackObject);
			if (undoStack.size() > 1) {
				UndoStackObject previousUndoStackObject = undoStack.get(undoStack.size() - 2);
				previousUndoStackObject.removeBitmap();
			}
			redoStack.remove(redoStack.size() - 1);
		} else if (redoStackObject.hasActions()) {
			undoStackObject = undoStack.get(undoStack.size() - 1);
			undoStackObject.addAction(redoStackObject.getAndRemoveLastAction());
		} else {
			return null;
		}
		redoBitmap = undoStackObject.drawAll();
		return redoBitmap;
	}

	public synchronized void addDrawing(Bitmap bitmap) {
		clearRedoStack();
		UndoStackObject undoStackObject = new UndoStackObject();
		undoStackObject.addBitmap(bitmap);
		undoStack.add(undoStackObject);

		if (undoStack.size() > 1) {
			UndoStackObject previousUndoStackObject = undoStack.get(undoStack.size() - 2);
			saveBitmapToTemp(previousUndoStackObject.getAndRemoveBitmap(), undoStack.size() - 2);
		}
	}

	public synchronized void addPath(Path path, Paint paint) {
		clearRedoStack();
		UndoStackObject undoStackObject = undoStack.get(undoStack.size() - 1);
		undoStackObject.addPath(path, paint);
	}

	public synchronized void addPoint(int x, int y, Paint paint) {
		clearRedoStack();
		UndoStackObject undoStackObject = undoStack.get(undoStack.size() - 1);
		undoStackObject.addPoint(x, y, paint);
	}

	public synchronized void clear() {
		undoStack.clear();
		clearRedoStack();
	}

	private synchronized void clearRedoStack() {
		redoStack.clear();
		RedoStackObject newRedoStackObject = new RedoStackObject();
		redoStack.add(newRedoStackObject);
	}

	private void saveBitmapToTemp(Bitmap bitmap, int filename) {
		File outputFile = new File(mContext.getCacheDir(), filename + ".png");

		try {
			FileOutputStream out = new FileOutputStream(outputFile);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			Log.d("PAINTROID", "FileNotFoundException: " + e);
		} catch (IOException e) {
			Log.d("PAINTROID", "FileNotFoundException: " + e);
		}
	}

	private Bitmap getBitmapFromTemp(int filename) {
		if (filename < 0) {
			filename = 0;
		}
		String path = mContext.getCacheDir().getAbsolutePath() + "/" + String.valueOf(filename) + ".png";

		File bitmapFile = new File(path);
		if (!bitmapFile.exists()) {
			return null;
		}

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		int width = options.outWidth;
		int height = options.outHeight;

		int size = width > height ? width : height;

		if (size > 1000) {
			size = Character.getNumericValue(Integer.toString(size).charAt(0));

			options.inSampleSize = size + 1;
			BitmapFactory.decodeFile(path, options);
			width = options.outWidth;
			height = options.outHeight;
		}
		options.inJustDecodeBounds = false;

		Bitmap currentImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		int[] pixels = new int[width * height];
		BitmapFactory.decodeFile(path, options).getPixels(pixels, 0, width, 0, 0, width, height);

		currentImage.setPixels(pixels, 0, width, 0, 0, width, height);

		return currentImage;
	}

	protected abstract class StackObject {
		protected Vector<Action> actions;

		public StackObject() {
			this.actions = new Vector<Action>();
		}

		public void addAction(Action action) {
			this.actions.add(action);
		}

		public boolean hasActions() {
			return this.actions.size() != 0;
		}

		protected abstract class Action {
			protected Paint paint;

			public abstract void draw(Canvas canvas);
		}
	}

	private class UndoStackObject extends StackObject {
		protected Bitmap bitmap;

		public UndoStackObject() {
			super();
			this.bitmap = null;
		}

		public void addBitmap(Bitmap bitmap) {
			this.bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
		}

		public Bitmap getAndRemoveBitmap() {
			Bitmap bitmap = this.bitmap;
			removeBitmap();
			return bitmap;
		}

		public void removeBitmap() {
			this.bitmap = null;
		}

		public void addPath(Path path, Paint paint) {
			Path copyOfPath = new Path();
			copyOfPath.set(path);
			Paint copyOfPaint = new Paint();
			copyOfPaint.set(paint);
			Action pathAction = new PathAction(copyOfPath, copyOfPaint);
			this.actions.add(pathAction);
		}

		public void addPoint(int x, int y, Paint paint) {
			Paint copyOfPaint = new Paint();
			copyOfPaint.set(paint);
			Action pointAction = new PointAction(x, y, copyOfPaint);
			this.actions.add(pointAction);
		}

		public Bitmap undo(RedoStackObject redoStackObject) {
			if (this.actions.size() > 0) {
				Action action = this.actions.get(this.actions.size() - 1);
				redoStackObject.addAction(action);
				this.actions.remove(this.actions.size() - 1);
			}
			return drawAll();
		}

		public Bitmap drawAll() {
			if (bitmap == null) {
				return null;
			}
			Bitmap undoBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
			Canvas canvas = new Canvas(undoBitmap);
			if (this.actions.size() >= 1) {
				for (Action action : this.actions) {
					action.draw(canvas);
				}
			}
			return undoBitmap;
		}

		protected class PathAction extends Action {
			protected Path path;

			public PathAction(Path path, Paint paint) {
				this.path = path;
				this.paint = paint;
			}

			@Override
			public void draw(Canvas canvas) {
				canvas.drawPath(this.path, this.paint);
			}
		}

		protected class PointAction extends Action {
			protected int x;
			protected int y;

			public PointAction(int x, int y, Paint paint) {
				this.x = x;
				this.y = y;
				this.paint = paint;
			}

			@Override
			public void draw(Canvas canvas) {
				canvas.drawPoint(this.x, this.y, this.paint);
			}
		}
	}

	private class RedoStackObject extends StackObject {
		public Action getAndRemoveLastAction() {
			Action action = this.actions.get(this.actions.size() - 1);
			this.actions.remove(this.actions.size() - 1);
			return action;
		}
	}
}
