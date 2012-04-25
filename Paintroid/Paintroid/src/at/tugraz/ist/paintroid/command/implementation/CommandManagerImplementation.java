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

package at.tugraz.ist.paintroid.command.implementation;

import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import at.tugraz.ist.paintroid.command.Command;
import at.tugraz.ist.paintroid.command.CommandManager;

public class CommandManagerImplementation implements CommandManager {
	private static final int MAX_COMMANDS = 512;

	private final LinkedList<Command> mCommandList;
	private int mCommandCounter;
	private int mCommandIndex;
	private Bitmap mOriginalBitmap;
	private final Canvas mOriginalBitmapCanvas;

	public CommandManagerImplementation(Context context) {
		mOriginalBitmapCanvas = new Canvas();
		mCommandList = new LinkedList<Command>();
		// The first command in the list is needed to clear the image when rolling back commands.
		mCommandList.add(new ClearCommand());
		mCommandCounter = 1;
		mCommandIndex = 1;
	}

	@Override
	public void setOriginalBitmap(Bitmap bitmap) {
		mOriginalBitmap = bitmap.copy(Config.ARGB_8888, true);
		// If we use some custom bitmap, this first command is used to restore it (instead of clear).
		mCommandList.removeFirst().freeResources();
		mCommandList.addFirst(new BitmapCommand(mOriginalBitmap));
		mOriginalBitmapCanvas.setBitmap(mOriginalBitmap);
	}

	@Override
	public synchronized void resetAndClear() {
		if (mOriginalBitmap != null && !mOriginalBitmap.isRecycled()) {
			mOriginalBitmap.recycle();
			mOriginalBitmap = null;
		}
		for (int i = 0; i < mCommandList.size(); i++) {
			mCommandList.get(i).freeResources();
		}
		mCommandList.clear();
		mCommandList.add(new ClearCommand());
		mCommandCounter = 1;
		mCommandIndex = 1;
	}

	@Override
	public synchronized Command getNextCommand() {
		if (mCommandIndex < mCommandCounter) {
			return mCommandList.get(mCommandIndex++);
		} else {
			return null;
		}
	}

	@Override
	public synchronized boolean commitCommand(Command command) {
		// First remove any previously undone commands from the top of the queue.
		if (mCommandCounter < mCommandList.size()) {
			for (int i = mCommandList.size(); i > mCommandCounter; i--) {
				mCommandList.removeLast().freeResources();
			}
		}

		if (mCommandCounter == MAX_COMMANDS) {
			// TODO handle this and don't return false. Hint: apply first command to bitmap.
			return false;
		} else {
			mCommandCounter++;
		}

		return mCommandList.add(command);
	}

	@Override
	public synchronized void undo() {
		if (mCommandCounter > 1) {
			mCommandCounter--;
			mCommandIndex = 0;
		}
	}

	@Override
	public synchronized void redo() {
		if (mCommandCounter < mCommandList.size()) {
			mCommandIndex = mCommandCounter;
			mCommandCounter++;
		}
	}
}
