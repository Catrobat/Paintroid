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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import at.tugraz.ist.paintroid.command.Command;

@Deprecated
class UndoStackObject extends StackObject {
	protected Bitmap mBitmap;

	public UndoStackObject() {
		super();
	}

	public void addBitmap(Bitmap bitmap) {
		mBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
	}

	public Bitmap getAndRemoveBitmap() {
		// FIXME what is this supposed to do? What about recycle?
		Bitmap bitmap = mBitmap;
		removeBitmap();
		return bitmap;
	}

	public void removeBitmap() {
		// FIXME what is this supposed to do? What about recycle?
		mBitmap = null;
	}

	@Override
	public void addCommand(Command command) {
		mCommands.add(command);
	}

	public Bitmap undo(RedoStackObject redoStackObject) {
		if (!mCommands.isEmpty()) {
			Command command = mCommands.removeLast();
			redoStackObject.addCommand(command);
		}
		return drawAll();
	}

	public Bitmap drawAll() {
		if (mBitmap == null) {
			return null;
		} else {
			Bitmap undoBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
			Canvas canvas = new Canvas(undoBitmap);

			for (int i = 0; i < mCommands.size(); i++) {
				mCommands.get(i).run(canvas, mBitmap);
			}

			return undoBitmap;
		}
	}
}
