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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class UndoRedo {
	private int undoCount = 0;
	private int redoCount = 0;
	private Context mContext;
	
	/**
	 * Constructor
	 */
	public UndoRedo(Context context)
	{
		mContext = context;
	}
	
	/**
	 * Gets the last bitmap from the stack, puts it in the
	 * redo stack and returns a copy from it
	 * 
	 * @return last added bitmap
	 */
	public Bitmap undo()
	{
		if(undoCount > 1)
		{
			undoCount--;
			redoCount++;
		}
	    Bitmap undoBitmap = getBitmapFromTemp(undoCount);
	    return undoBitmap;
	}
	
	/**
	 * Gets the last bitmap from the redo stack, puts it in the
	 * undo stack and returns a copy from it
	 * 
	 * @return last added bitmap
	 */
	public Bitmap redo()
	{
		if(redoCount == 0)
		{
			return getBitmapFromTemp(undoCount);
		}
		undoCount++;
		redoCount--;
		Bitmap redoBitmap = getBitmapFromTemp(undoCount);
	    return redoBitmap;
	}
	
	/**
	 * Adds a bitmap to the undo stack
	 * 
	 * @param bitmap bitmap to add
	 */
	public void addDrawing(Bitmap bitmap)
	{
		undoCount++;
		redoCount = 0;
		saveBitmapToTemp(bitmap);
	}
	
	/**
	 * Clears undo and redo counter
	 * 
	 * 
	 */
	public void clear()
	{
		undoCount = 0;
		redoCount = 0;
	}
	
	/**
	 * saves a bitmap to the cache directory of the application
	 * with the name <undoCount>.png
	 * 
	 * @param bitmap to save
	 */
	public void saveBitmapToTemp(Bitmap bitmap)
	{
		if(undoCount < 1)
		{
			return;
		}
		File outputFile = new File(mContext.getCacheDir(), undoCount + ".png");
		
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
	public Bitmap getBitmapFromTemp(int bitmap_count)
	{
		if(bitmap_count < 1)
		{
			bitmap_count = 1;
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
	
}
