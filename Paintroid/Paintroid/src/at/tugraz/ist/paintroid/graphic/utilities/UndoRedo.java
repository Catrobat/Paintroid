/*    Catroid: An on-device graphical programming language for Android devices
 *    Copyright (C) 2010  Catroid development team
 *    (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.graphic.utilities;

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

/**
 * This class handels the undo and redo actions
 * 
 * Status: refactored 20.02.2011
 * 
 * @author PaintroidTeam
 * @version 6.0.4b
 */
public class UndoRedo {
	private Vector<UndoStackObject> undoStack;
	private Vector<RedoStackObject> redoStack;
	// Context of the application to get cache directory
	private Context mContext;

	/**
	 * Constructor
	 */
	public UndoRedo(Context context) {
		mContext = context;
		undoStack = new Vector<UndoStackObject>();
		redoStack = new Vector<RedoStackObject>();
		// initialize vectors to start value
		clear();
	}

	/**
	 * Gets the last action from the stack, puts it in the
	 * redo stack and returns the previous bitmap
	 * 
	 * @return previous bitmap
	 */
	public synchronized Bitmap undo() {
		UndoStackObject undoStackObject = undoStack.get(undoStack.size() - 1);
		Bitmap undoBitmap;
		// If no draw actions exist in the stack object and it is not the last
		// object on the stack
		if (!undoStackObject.hasActions() && undoStack.size() > 1) {
			// Redo stack is empty (initial size is 1)
			if (redoStack.size() == 1) {
				// Save bitmap to cache file that redo stack can access it later if needed
				// and remove bitmap from ram
				UndoStackObject actualUndoStackObject = undoStack.get(undoStack.size() - 1);
				saveBitmapToTemp(actualUndoStackObject.getAndRemoveBitmap(), undoStack.size() - 1);
			}
			RedoStackObject newRedoStackObject = new RedoStackObject();
			redoStack.add(newRedoStackObject);
			undoStack.remove(undoStack.size() - 1);
			UndoStackObject previousUndoStackObject = undoStack.get(undoStack.size() - 1);
			// load bitmap from cache file
			Bitmap cachedBitmap = getBitmapFromTemp(undoStack.size() - 1);
			// if cached bitmap doesn't exist on system do nothing 
			if (cachedBitmap == null)
				return null;
			previousUndoStackObject.addBitmap(cachedBitmap);
			// draw all paths on the bitmap
			undoBitmap = previousUndoStackObject.drawAll();
		} else // action exist in undo object
		{
			// draw all actions on the bitmap except last one
			undoBitmap = undoStackObject.undo(redoStack.get(redoStack.size() - 1));
		}
		return undoBitmap;
	}

	/**
	 * Gets the last action from the redo stack, puts it in the
	 * undo stack and returns the bitmap
	 * 
	 * @return redone bitmap
	 */
	public synchronized Bitmap redo() {
		RedoStackObject redoStackObject = redoStack.get(redoStack.size() - 1);
		Bitmap redoBitmap;
		UndoStackObject undoStackObject;
		// if redo stack object doesn't have any path and it is not the last object
		// on the stack
		if (!redoStackObject.hasActions() && redoStack.size() > 1) {
			undoStackObject = new UndoStackObject();
			// read bitmap from cache file
			Bitmap cachedBitmap = getBitmapFromTemp(undoStack.size());
			// if cached bitmap doesn't exist on system do nothing 
			if (cachedBitmap == null)
				return null;
			undoStackObject.addBitmap(cachedBitmap);
			// add bitmap to undo stack
			undoStack.add(undoStackObject);
			if (undoStack.size() > 1) {
				// remove bitmap from last undo stack object from ram
				UndoStackObject previousUndoStackObject = undoStack.get(undoStack.size() - 2);
				previousUndoStackObject.removeBitmap();
			}
			redoStack.remove(redoStack.size() - 1);
		} else if (redoStackObject.hasActions()) {
			undoStackObject = undoStack.get(undoStack.size() - 1);
			undoStackObject.addAction(redoStackObject.getAndRemoveLastAction());
		} else // no objects on redo stack
		{
			return null;
		}
		redoBitmap = undoStackObject.drawAll();
		return redoBitmap;
	}

	/**
	 * Adds a new bitmap to the undo stack
	 * 
	 * @param bitmap
	 *            bitmap to add
	 */
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

	/**
	 * Adds a path to the undo stack
	 * 
	 * @param path
	 *            path to add
	 * @param paint
	 *            size, color and shape of the path
	 */
	public synchronized void addPath(Path path, Paint paint) {
		clearRedoStack();
		UndoStackObject undoStackObject = undoStack.get(undoStack.size() - 1);
		undoStackObject.addPath(path, paint);
	}

	/**
	 * Adds a point to the undo stack
	 * 
	 * @param x
	 *            x-coordinate
	 * @param y
	 *            y-coordinate
	 * @param paint
	 *            size, color and shape of the point
	 */
	public synchronized void addPoint(int x, int y, Paint paint) {
		clearRedoStack();
		UndoStackObject undoStackObject = undoStack.get(undoStack.size() - 1);
		undoStackObject.addPoint(x, y, paint);
	}

	/**
	 * Clears undo and redo stack
	 * 
	 */
	public synchronized void clear() {
		undoStack.clear();
		clearRedoStack();
	}

	/**
	 * Clears redo stack and adds a first empty object
	 * 
	 */
	private synchronized void clearRedoStack() {
		redoStack.clear();
		RedoStackObject newRedoStackObject = new RedoStackObject();
		redoStack.add(newRedoStackObject);
	}

	/**
	 * Saves a bitmap to the cache directory of the application
	 * 
	 * @param bitmap
	 *            bitmap to save
	 * @param filename
	 *            defines the name of the bitmap
	 */
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

	/**
	 * Reads a bitmap from the cache directory
	 * 
	 * @param filename
	 *            name of the bitmap
	 * @return the read bitmap
	 */
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

		// if the image is too large we subsample it
		if (size > 1000) {

			// we use the thousands digit to dynamically define the sample size
			size = Character.getNumericValue(Integer.toString(size).charAt(0));

			options.inSampleSize = size + 1;
			BitmapFactory.decodeFile(path, options);
			width = options.outWidth;
			height = options.outHeight;
		}
		options.inJustDecodeBounds = false;

		Bitmap currentImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		// we have to load each pixel for alpha transparency to work with photos
		int[] pixels = new int[width * height];
		BitmapFactory.decodeFile(path, options).getPixels(pixels, 0, width, 0, 0, width, height);

		currentImage.setPixels(pixels, 0, width, 0, 0, width, height);

		return currentImage;
	}

	/**
	 * General class for the undo and redo stacks
	 * 
	 */
	protected abstract class StackObject {
		// actions to draw
		protected Vector<Action> actions;

		/**
		 * Constructor
		 */
		public StackObject() {
			this.actions = new Vector<Action>();
		}

		/**
		 * Adds a new action to the stack object
		 * 
		 * @param action
		 *            action to add
		 */
		public void addAction(Action action) {
			this.actions.add(action);
		}

		/**
		 * Checks if an action exists
		 * 
		 * @return true if action exists, else false
		 */
		public boolean hasActions() {
			return this.actions.size() != 0;
		}

		/**
		 * Class used to store an action
		 */
		protected abstract class Action {

			protected Paint paint;

			/**
			 * Abstract class draw
			 * Draws the action on the canvas
			 * 
			 * @param canvas
			 *            to draw on
			 */
			public abstract void draw(Canvas canvas);

		}
	}

	/**
	 * Class for handling the undo actions
	 * 
	 */
	private class UndoStackObject extends StackObject {
		protected Bitmap bitmap;

		/**
		 * Constructor
		 */
		public UndoStackObject() {
			super();
			this.bitmap = null;
		}

		/**
		 * Adds a bitmap to the stack object
		 * 
		 * @param bitmap
		 *            bitmap to add
		 */
		public void addBitmap(Bitmap bitmap) {
			this.bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
		}

		/**
		 * Returns the bitmap and removes it from
		 * the object
		 * 
		 * @return bitmap
		 */
		public Bitmap getAndRemoveBitmap() {
			Bitmap bitmap = this.bitmap;
			removeBitmap();
			return bitmap;
		}

		/**
		 * Removes bitmap from object
		 * 
		 */
		public void removeBitmap() {
			this.bitmap = null;
		}

		/**
		 * Adds a path action to the object
		 * 
		 * @param path
		 *            path to add
		 * @param paint
		 *            paint used to draw the path
		 */
		public void addPath(Path path, Paint paint) {
			Path copyOfPath = new Path();
			copyOfPath.set(path);
			Paint copyOfPaint = new Paint();
			copyOfPaint.set(paint);
			Action pathAction = new PathAction(copyOfPath, copyOfPaint);
			this.actions.add(pathAction);
		}

		/**
		 * Adds a point action to the object
		 * 
		 * @param x
		 *            x-coordinate
		 * @param y
		 *            y-coordinate
		 * @param paint
		 *            paint used to draw the point
		 */
		public void addPoint(int x, int y, Paint paint) {
			Paint copyOfPaint = new Paint();
			copyOfPaint.set(paint);
			Action pointAction = new PointAction(x, y, copyOfPaint);
			this.actions.add(pointAction);
		}

		/**
		 * Removes last added action from the object, adds it to the
		 * redo object and draws all remaining actions on the bitmap and
		 * returns the result @see drawAll()
		 * 
		 * @param redoStackObject
		 *            redo object to add the action
		 * @return result bitmap
		 */
		public Bitmap undo(RedoStackObject redoStackObject) {
			if (this.actions.size() > 0) {
				Action action = this.actions.get(this.actions.size() - 1);
				redoStackObject.addAction(action);
				this.actions.remove(this.actions.size() - 1);
			}
			return drawAll();
		}

		/**
		 * Draws all actions on the bitmap and returns the result
		 * 
		 * @return result bitmap
		 */
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

			/**
			 * Constructor for a path action
			 * 
			 * @param path
			 *            path to add
			 * @param paint
			 *            paint to add
			 */
			public PathAction(Path path, Paint paint) {
				this.path = path;
				this.paint = paint;
			}

			/**
			 * Draws the path on the canvas
			 * 
			 * @param canvas
			 *            canvas to draw on
			 */
			public void draw(Canvas canvas) {
				canvas.drawPath(this.path, this.paint);
			}
		}

		protected class PointAction extends Action {
			// Coordinates of a point
			protected int x;
			protected int y;

			/**
			 * Constructor for a point action
			 * 
			 * @param x
			 *            x-coordinate
			 * @param y
			 *            y-coordinate
			 * @param paint
			 *            paint to add
			 */
			public PointAction(int x, int y, Paint paint) {
				this.x = x;
				this.y = y;
				this.paint = paint;
			}

			/**
			 * Draws the point on the canvas
			 * 
			 * @param canvas
			 *            canvas to draw on
			 */
			public void draw(Canvas canvas) {
				canvas.drawPoint(this.x, this.y, this.paint);
			}
		}
	}

	/**
	 * Class for handling the redo actions
	 * 
	 */
	private class RedoStackObject extends StackObject {
		/**
		 * Returns and removes the last added action
		 * 
		 * @return action
		 */
		public Action getAndRemoveLastAction() {
			Action action = this.actions.get(this.actions.size() - 1);
			this.actions.remove(this.actions.size() - 1);
			return action;
		}
	}
}
