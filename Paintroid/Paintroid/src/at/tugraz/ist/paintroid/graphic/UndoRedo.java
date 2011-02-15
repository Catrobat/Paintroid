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

package at.tugraz.ist.paintroid.graphic;

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

public class UndoRedo {
	private Vector<UndoStackObject> undoStack;
	private Vector<RedoStackObject> redoStack;
	private Context mContext;
	
	/**
	 * Constructor
	 */
	public UndoRedo(Context context)
	{
		mContext = context;
		undoStack = new Vector<UndoStackObject>();
		redoStack = new Vector<RedoStackObject>();
		clear();
	}
	
	/**
	 * Gets the last bitmap from the stack, puts it in the
	 * redo stack and returns a copy from it
	 * 
	 * @return last added bitmap
	 */
	public synchronized Bitmap undo()
	{
		UndoStackObject undoStackObject = undoStack.get(undoStack.size()-1);
		Bitmap undoBitmap;
		if(!undoStackObject.PathExists() && undoStack.size() > 1)
		{
			if(redoStack.size() == 1)
			{
				UndoStackObject actualUndoStackObject = undoStack.get(undoStack.size()-1);
				saveBitmapToTemp(actualUndoStackObject.getAndRemoveBitmap(), undoStack.size()-1);
			}
			RedoStackObject newRedoStackObject = new RedoStackObject();
			redoStack.add(newRedoStackObject);
			undoStack.remove(undoStack.size()-1);
			UndoStackObject previousUndoStackObject = undoStack.get(undoStack.size()-1);
			previousUndoStackObject.addBitmap(getBitmapFromTemp(undoStack.size()-1));
			undoBitmap = previousUndoStackObject.drawAll();
		}
		else
		{
			undoBitmap = undoStackObject.undo(redoStack.get(redoStack.size()-1));
		}
	    return undoBitmap;
	}
	
	/**
	 * Gets the last bitmap from the redo stack, puts it in the
	 * undo stack and returns a copy from it
	 * 
	 * @return last added bitmap
	 */
	public synchronized Bitmap redo()
	{
		RedoStackObject redoStackObject = redoStack.get(redoStack.size()-1);
		Bitmap redoBitmap;
		UndoStackObject undoStackObject;
		if(!redoStackObject.PathExists() && redoStack.size() > 1)
		{
			undoStackObject = new UndoStackObject();
			undoStackObject.addBitmap(getBitmapFromTemp(undoStack.size()));
			undoStack.add(undoStackObject);
			if(undoStack.size() > 1)
			{
				UndoStackObject previousUndoStackObject = undoStack.get(undoStack.size()-2);
				previousUndoStackObject.removeBitmap();
			}
			redoStack.remove(redoStack.size()-1);
		}
		else if(redoStackObject.PathExists())
		{
			undoStackObject = undoStack.get(undoStack.size()-1);
			if(redoStackObject.getLastPath() != null)
			{
				undoStackObject.addPath(redoStackObject.getLastPath(), redoStackObject.getLastPaint());
			}
			else
			{
				undoStackObject.addPoint(redoStackObject.getLastX(), redoStackObject.getLastY(), redoStackObject.getLastPaint());
			}
			redoStackObject.removeLastPath();
		}
		else
		{
			undoStackObject = undoStack.get(undoStack.size()-1);
		}
		redoBitmap = undoStackObject.drawAll();
	    return redoBitmap;
	}
	
	/**
	 * Adds a bitmap to the undo stack
	 * 
	 * @param bitmap bitmap to add
	 */
	public synchronized void addDrawing(Bitmap bitmap)
	{
		clearRedoStack();
		UndoStackObject undoStackObject = new UndoStackObject();
		undoStackObject.addBitmap(bitmap);
		undoStack.add(undoStackObject);
		
		if(undoStack.size() > 1)
		{
			UndoStackObject previousUndoStackObject = undoStack.get(undoStack.size()-2);
			saveBitmapToTemp(previousUndoStackObject.getAndRemoveBitmap(), undoStack.size()-2);
		}
	}
	
	/**
	 * Adds a path to the undo stack
	 * 
	 * @param path path to add
	 * @param paint size, color and shape of the path
	 */
	public synchronized void addPath(Path path, Paint paint)
	{
		clearRedoStack();
		UndoStackObject undoStackObject = undoStack.get(undoStack.size()-1);
		undoStackObject.addPath(path, paint);
	}
	
	/**
	 * Adds a point to the undo stack
	 * 
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param paint size, color and shape of the point
	 */
	public synchronized void addPoint(int x, int y, Paint paint)
	{
		clearRedoStack();
		UndoStackObject undoStackObject = undoStack.get(undoStack.size()-1);
		undoStackObject.addPoint(x, y, paint);
	}
	
	/**
	 * Clears undo and redo counter
	 * 
	 * 
	 */
	public synchronized void clear()
	{
		undoStack.clear();
		clearRedoStack();
	}
	
	private synchronized void clearRedoStack()
	{
		redoStack.clear();
		RedoStackObject newRedoStackObject = new RedoStackObject();
		redoStack.add(newRedoStackObject);
	}
	
	/**
	 * saves a bitmap to the cache directory of the application
	 * with the name <undoCount>.png
	 * 
	 * @param bitmap to save
	 */
	private void saveBitmapToTemp(Bitmap bitmap, int bitmap_count)
	{
		File outputFile = new File(mContext.getCacheDir(), bitmap_count + ".png");
		
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
	 * reads the bitmap with the name <bitmapCount>.png from the
	 * cache directory
	 * 
	 * @param bitmap_count
	 * @return the read bitmap
	 */
	private Bitmap getBitmapFromTemp(int bitmap_count)
	{
		if(bitmap_count < 0)
		{
			bitmap_count = 0;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		String filename = mContext.getCacheDir().getAbsolutePath() + "/" + String.valueOf(bitmap_count) + ".png";
		BitmapFactory.decodeFile(filename, options);

		int width = options.outWidth;
		int height = options.outHeight;

		int size = width > height ? width : height;

		// if the image is too large we subsample it
		if (size > 1000) {

			// we use the thousands digit to dynamically define the sample size
			size = Character.getNumericValue(Integer.toString(size).charAt(0));

			options.inSampleSize = size + 1;
			BitmapFactory.decodeFile(filename, options);
			width = options.outWidth;
			height = options.outHeight;
		}
		options.inJustDecodeBounds = false;

		Bitmap currentImage = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);

		// we have to load each pixel for alpha transparency to work with photos
		int[] pixels = new int[width * height];
		BitmapFactory.decodeFile(filename, options).getPixels(pixels, 0,
				width, 0, 0, width, height);

		currentImage.setPixels(pixels, 0, width, 0, 0, width, height);

		return currentImage;
	}
	
	private class UndoStackObject
	{
		private Bitmap bitmap;
		protected Vector<PathAndPaint> pathAndPaint;
		
		/**
		 * Constructor
		 */
		public UndoStackObject()
		{
			bitmap = null;
			pathAndPaint = new Vector<PathAndPaint>();
		}

		public void addBitmap(Bitmap bitmap)
		{
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
		
		public void addPath(Path path, Paint paint)
		{
			Path copyOfPath = new Path();
			copyOfPath.set(path);
			Paint copyOfPaint = new Paint();
			copyOfPaint.set(paint);
			PathAndPaint pathAndPaint = new PathAndPaint(copyOfPath, copyOfPaint);
			this.pathAndPaint.add(pathAndPaint);
		}
		
		public void addPoint(int x, int y, Paint paint)
		{
			Paint copyOfPaint = new Paint();
			copyOfPaint.set(paint);
			PathAndPaint pathAndPaint = new PathAndPaint(x, y, copyOfPaint);
			this.pathAndPaint.add(pathAndPaint);
		}
		
		public Bitmap undo(RedoStackObject redoStackObject)
		{
			if(this.pathAndPaint.size() > 0)
			{
				PathAndPaint pathAndPaint = this.pathAndPaint.get(this.pathAndPaint.size()-1);
				if(pathAndPaint.path != null)
				{
					redoStackObject.addPath(pathAndPaint.path, pathAndPaint.paint);
				}
				else
				{
					redoStackObject.addPoint(pathAndPaint.x, pathAndPaint.y, pathAndPaint.paint);
				}
				this.pathAndPaint.remove(this.pathAndPaint.size()-1);
			}
			return drawAll();
		}
		
		public Bitmap drawAll()
		{
			if(bitmap == null)
			{
				return null;
			}
			Bitmap undoBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
			Canvas canvas = new Canvas(undoBitmap);
			if(this.pathAndPaint.size() >= 1)
			{
				for (PathAndPaint pathAndPaint : this.pathAndPaint) {
					if(pathAndPaint.path != null)
					{
						canvas.drawPath(pathAndPaint.path, pathAndPaint.paint);
					}
					else
					{
						canvas.drawPoint(pathAndPaint.x, pathAndPaint.y, pathAndPaint.paint);
					}
				}
			}
			return undoBitmap;
		}
		
		public boolean PathExists()
		{
			return this.pathAndPaint.size() != 0;
		}
		
		protected class PathAndPaint
		{
			public Path path = null;
			public Paint paint;
			public Integer x = null;
			public Integer y = null;
			
			public PathAndPaint(Path path, Paint paint)
			{
				this.path = path;
				this.paint = paint;
			}
			
			public PathAndPaint(int x, int y, Paint paint)
			{
				this.x = x;
				this.y = y;
				this.paint = paint;
			}
		}
	}
	
	private class RedoStackObject extends UndoStackObject
	{
		public Path getLastPath()
		{
			PathAndPaint pathAndPaint = this.pathAndPaint.get(this.pathAndPaint.size()-1);
			return pathAndPaint.path;
		}
		
		public int getLastX()
		{
			PathAndPaint pathAndPaint = this.pathAndPaint.get(this.pathAndPaint.size()-1);
			return pathAndPaint.x;
		}
		
		public int getLastY()
		{
			PathAndPaint pathAndPaint = this.pathAndPaint.get(this.pathAndPaint.size()-1);
			return pathAndPaint.y;
		}

		public Paint getLastPaint()
		{
			PathAndPaint pathAndPaint = this.pathAndPaint.get(this.pathAndPaint.size()-1);
			return pathAndPaint.paint;
		}
		
		public void removeLastPath() 
		{
			this.pathAndPaint.remove(this.pathAndPaint.size()-1);
		}
	}
	
}
