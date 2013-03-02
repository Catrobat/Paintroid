/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.command.implementation;

import java.util.LinkedList;

import org.catrobat.paintroid.AutoSave;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.command.UndoRedoManager;
import org.catrobat.paintroid.command.UndoRedoManager.StatusMode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public class CommandManagerImplementation implements CommandManager {
	private static final int MAX_COMMANDS = 512;

	private final LinkedList<Command> mCommandList;
	private int mCommandCounter;
	private int mCommandIndex;
	private Bitmap mOriginalBitmap;

	// private final Canvas mOriginalBitmapCanvas;

	public CommandManagerImplementation(Context context) {
		// mOriginalBitmapCanvas = new Canvas();
		mCommandList = new LinkedList<Command>();
		// The first command in the list is needed to clear the image when
		// rolling back commands.
		mCommandList.add(new ClearCommand());
		mCommandCounter = 1;
		mCommandIndex = 1;
	}

	@Override
	public void setOriginalBitmap(Bitmap bitmap) {
		mOriginalBitmap = bitmap.copy(Config.ARGB_8888, true);
		// If we use some custom bitmap, this first command is used to restore
		// it (instead of clear).
		mCommandList.removeFirst().freeResources();
		mCommandList.addFirst(new BitmapCommand(mOriginalBitmap, false));
		// mOriginalBitmapCanvas.setBitmap(mOriginalBitmap);
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
		UndoRedoManager.getInstance().update(StatusMode.DISABLE_REDO);
		UndoRedoManager.getInstance().update(StatusMode.DISABLE_UNDO);
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
		// First remove any previously undone commands from the top of the
		// queue.
		if (mCommandCounter < mCommandList.size()) {
			for (int i = mCommandList.size(); i > mCommandCounter; i--) {
				mCommandList.removeLast().freeResources();
			}
			UndoRedoManager.getInstance().update(StatusMode.DISABLE_REDO);
		}

		if (mCommandCounter == MAX_COMMANDS) {
			// TODO handle this and don't return false. Hint: apply first
			// command to bitmap.
			return false;
		} else {
			mCommandCounter++;
			UndoRedoManager.getInstance().update(
					UndoRedoManager.StatusMode.ENABLE_UNDO);
		}

		AutoSave.trigger();
		return mCommandList.add(command);
	}

	@Override
	public synchronized void undo() {
		if (mCommandCounter > 1) {
			mCommandCounter--;
			mCommandIndex = 0;
			UndoRedoManager.getInstance().update(
					UndoRedoManager.StatusMode.ENABLE_REDO);
			if (mCommandCounter <= 1) {
				UndoRedoManager.getInstance().update(
						UndoRedoManager.StatusMode.DISABLE_UNDO);
			}
		}
		AutoSave.incrementCounter();
	}

	@Override
	public synchronized void redo() {
		if (mCommandCounter < mCommandList.size()) {
			mCommandIndex = mCommandCounter;
			mCommandCounter++;
			UndoRedoManager.getInstance().update(
					UndoRedoManager.StatusMode.ENABLE_UNDO);
			if (mCommandCounter == mCommandList.size()) {
				UndoRedoManager.getInstance().update(
						UndoRedoManager.StatusMode.DISABLE_REDO);
			}
		}
		AutoSave.trigger();
	}
}
