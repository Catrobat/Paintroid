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

import java.util.*;

import android.graphics.Bitmap;

public class UndoRedo {
	private List<Bitmap> undoStack;
	private List<Bitmap> redoStack;
	final int maxSteps = 10;
	
	/**
	 * Constructor
	 */
	public UndoRedo()
	{
		undoStack = Collections.synchronizedList(new ArrayList<Bitmap>());
	  redoStack = Collections.synchronizedList(new ArrayList<Bitmap>());
	}
	
	/**
	 * Gets the last bitmap from the stack, puts it in the
	 * redo stack and returns a copy from it
	 * 
	 * @return last added bitmap
	 */
	public Bitmap undo()
	{
	    if(undoStack.size() > 1)
	    {
	    	Bitmap undoBitmap = (Bitmap) undoStack.get(undoStack.size()-2);
	    	undoStack.remove(undoStack.size()-1);
	      redoStack.add(undoBitmap);
	      return undoBitmap.copy(Bitmap.Config.ARGB_8888, true);
	    }
	    if(undoStack.size() == 1)
	    {
	    	Bitmap undoBitmap = (Bitmap) undoStack.get(undoStack.size()-1);
	      return undoBitmap.copy(Bitmap.Config.ARGB_8888, true);
	    }
	    return null;
	}
	
	/**
	 * Gets the last bitmap from the redo stack, puts it in the
	 * undo stack and returns a copy from it
	 * 
	 * @return last added bitmap
	 */
	public Bitmap redo()
	{
		if(redoStack.size() > 0)
	  {
	     Bitmap redoBitmap = (Bitmap) redoStack.get(redoStack.size()-1);
	     redoStack.remove(redoStack.size()-1);
	     undoStack.add(redoBitmap);
	     return redoBitmap.copy(Bitmap.Config.ARGB_8888, true);
	  }
	  return null;
	}
	
	/**
	 * Adds a bitmap to the undo stack
	 * 
	 * @param bitmap bitmap to add
	 */
	public void addDrawing(Bitmap bitmap)
	{
		Bitmap stackBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
		if(undoStack.size() < maxSteps)
  		undoStack.add(stackBitmap);
		else
		{
			for(int i = 0; i < maxSteps-1; i++)
			{				
				undoStack.set(i, undoStack.get(i+1));
			}
			undoStack.set(maxSteps-1, stackBitmap);
		}		
		redoStack.clear();
	}
	
	public void clear()
	{
		undoStack.clear();
		redoStack.clear();
	}
}
