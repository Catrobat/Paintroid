/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  Paintroid: An image manipulation application for Android, part of the
 *  Catroid project and Catroid suite of software.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.command.undoredo.implementation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import at.tugraz.ist.paintroid.command.Command;
import at.tugraz.ist.paintroid.command.UndoRedo;

/**
 * FIXME this implementation and the stack classes do not look very good.
 */
public class UndoRedoImplementation implements UndoRedo {
	private LinkedList<UndoStackObject> mUndoStack;
	private LinkedList<RedoStackObject> mRedoStack;
	private Context mContext;

	public UndoRedoImplementation(Context context) {
		mContext = context;
		mUndoStack = new LinkedList<UndoStackObject>();
		mRedoStack = new LinkedList<RedoStackObject>();
	}

	@Override
	public Bitmap undo() {
		UndoStackObject undoStackObject = mUndoStack.get(mUndoStack.size() - 1);
		Bitmap undoBitmap;

		if (!undoStackObject.hasCommands() && mUndoStack.size() > 1) {
			if (mRedoStack.size() == 1) {
				UndoStackObject actualUndoStackObject = mUndoStack.get(mUndoStack.size() - 1);
				saveBitmapToTemp(actualUndoStackObject.getAndRemoveBitmap(), mUndoStack.size() - 1);
			}
			RedoStackObject newRedoStackObject = new RedoStackObject();
			mRedoStack.add(newRedoStackObject);
			mUndoStack.remove(mUndoStack.size() - 1);
			UndoStackObject previousUndoStackObject = mUndoStack.get(mUndoStack.size() - 1);

			Bitmap cachedBitmap = getBitmapFromTemp(mUndoStack.size() - 1);

			if (cachedBitmap == null) {
				return null;
			}
			previousUndoStackObject.addBitmap(cachedBitmap);
			undoBitmap = previousUndoStackObject.drawAll();
		} else {
			undoBitmap = undoStackObject.undo(mRedoStack.get(mRedoStack.size() - 1));
		}
		return undoBitmap;
	}

	@Override
	public Bitmap redo() {
		RedoStackObject redoStackObject = mRedoStack.get(mRedoStack.size() - 1);
		Bitmap redoBitmap;
		UndoStackObject undoStackObject;

		if (!redoStackObject.hasCommands() && mRedoStack.size() > 1) {
			undoStackObject = new UndoStackObject();

			Bitmap cachedBitmap = getBitmapFromTemp(mUndoStack.size());

			if (cachedBitmap == null) {
				return null;
			}
			undoStackObject.addBitmap(cachedBitmap);

			mUndoStack.add(undoStackObject);
			if (mUndoStack.size() > 1) {
				UndoStackObject previousUndoStackObject = mUndoStack.get(mUndoStack.size() - 2);
				previousUndoStackObject.removeBitmap();
			}
			mRedoStack.removeLast();
		} else if (redoStackObject.hasCommands()) {
			undoStackObject = mUndoStack.getLast();
			undoStackObject.addCommand(redoStackObject.getAndRemoveLastCommand());
		} else {
			return null;
		}
		redoBitmap = undoStackObject.drawAll();
		return redoBitmap;
	}

	@Override
	public void addDrawing(Bitmap bitmap) {
		clearRedoStack();
		UndoStackObject undoStackObject = new UndoStackObject();
		undoStackObject.addBitmap(bitmap);
		mUndoStack.add(undoStackObject);

		if (mUndoStack.size() > 1) {
			UndoStackObject previousUndoStackObject = mUndoStack.get(mUndoStack.size() - 2);
			saveBitmapToTemp(previousUndoStackObject.getAndRemoveBitmap(), mUndoStack.size() - 2);
		}
	}

	@Override
	public void addCommand(Command command, Bitmap bitmap) {
		clearRedoStack();
		if (command.isUndoable()) {
			UndoStackObject undoStackObject = mUndoStack.get(mUndoStack.size() - 1);
			undoStackObject.addCommand(command);
		} else {
			addDrawing(bitmap);
		}

	}

	@Override
	public void clearStacks() {
		mUndoStack.clear();
		clearRedoStack();
	}

	private void clearRedoStack() {
		mRedoStack.clear();
		RedoStackObject newRedoStackObject = new RedoStackObject();
		mRedoStack.add(newRedoStackObject);
	}

	private void saveBitmapToTemp(Bitmap bitmap, int filename) {
		File outputFile = new File(mContext.getCacheDir(), filename + ".png");

		try {
			FileOutputStream out = new FileOutputStream(outputFile);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			Log.e("PAINTROID", "FileNotFoundException: " + e);
		} catch (IOException e) {
			Log.e("PAINTROID", "FileNotFoundException: " + e);
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

		options.inJustDecodeBounds = false;

		Bitmap currentImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		int[] pixels = new int[width * height];
		BitmapFactory.decodeFile(path, options).getPixels(pixels, 0, width, 0, 0, width, height);

		currentImage.setPixels(pixels, 0, width, 0, 0, width, height);

		return currentImage;
	}
}
