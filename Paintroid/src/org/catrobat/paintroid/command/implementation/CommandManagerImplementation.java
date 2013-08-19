/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
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

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.command.UndoRedoManager;
import org.catrobat.paintroid.command.UndoRedoManager.StatusMode;
import org.catrobat.paintroid.command.implementation.layer.DeleteLayerCommand;
import org.catrobat.paintroid.command.implementation.layer.SwitchLayerCommand;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.Log;

public class CommandManagerImplementation implements CommandManager, Observer {
	private static final int MAX_COMMANDS = 512;

	private final LinkedList<Command> mCommandList;
	private int mCommandCounter;
	private int mCommandIndex;
	private Bitmap mOriginalBitmap;

	public CommandManagerImplementation() {
		mCommandList = new LinkedList<Command>();
		// The first command in the list is needed to clear the image when
		// rolling back commands.
		mCommandList.add(new ClearCommand());
		mCommandCounter = 1;
		mCommandIndex = 1;
	}

	@Override
	public boolean hasCommands() {
		return mCommandCounter > 1;
	}

	@Override
	public void setOriginalBitmap(Bitmap bitmap) {
		mOriginalBitmap = bitmap.copy(Config.ARGB_8888, true);
		// If we use some custom bitmap, this first command is used to restore
		// it (instead of clear).
		mCommandList.removeFirst().freeResources();
		mCommandList.addFirst(new BitmapCommand(mOriginalBitmap, false));
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
		// Switch-LayerCommand shall not be saved
		if (command instanceof SwitchLayerCommand) {
			command.run(null, null);
			this.resetIndex();
			return mCommandList != null;
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

		((BaseCommand) command).addObserver(this);

		command.setCommandLayer(PaintroidApplication.currentLayer);

		int position = findLastCallIndex(mCommandList,
				PaintroidApplication.currentLayer);
		mCommandList.add(position, command);
		this.resetIndex();
		return mCommandList.get(position) != null;
	}

	private boolean isLayerCommand(Command command) {
		return command instanceof SwitchLayerCommand
				|| command instanceof DeleteLayerCommand;
	}

	private int findLastCallIndex(LinkedList<Command> mCommandList,
			int currentLayer) {
		printList();
		if (mCommandList.size() == 1) {
			return 1;
		} else {
			mCommandList = sortList(mCommandList);
			for (int i = mCommandList.size() - 1; i >= 1; i--) {
				if (mCommandList.get(i).getCommandLayer() == currentLayer) {
					return i + 1;
				}
			}
			return 1;
		}
	}

	private void printList() {
		for (int i = 0; i < mCommandList.size(); i++) {
			Log.i(PaintroidApplication.TAG, i + ":"
					+ mCommandList.get(i).toString() + " ; "
					+ mCommandList.get(i).getCommandLayer());
		}

	}

	public LinkedList<Command> sortList(LinkedList<Command> cl) {
		Command firstCommand = cl.removeFirst();
		Collections.sort(cl, new Comparator<Command>() {
			@Override
			public int compare(Command o1, Command o2) {
				if (o1.getCommandLayer() > o2.getCommandLayer()) {
					return -1;
				}
				if (o1.getCommandLayer() < o2.getCommandLayer()) {
					return 1;
				}
				return 0;
			}
		});
		cl.addFirst(firstCommand);
		return cl;
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
	}

	private synchronized void deleteFailedCommand(Command command) {
		int indexOfCommand = mCommandList.indexOf(command);
		((BaseCommand) mCommandList.remove(indexOfCommand)).freeResources();
		mCommandCounter--;
		mCommandIndex--;
		if (mCommandCounter == 1) {
			UndoRedoManager.getInstance().update(
					UndoRedoManager.StatusMode.DISABLE_UNDO);
		}
	}

	@Override
	public void update(Observable observable, Object data) {
		if (data instanceof BaseCommand.NOTIFY_STATES) {
			if (BaseCommand.NOTIFY_STATES.COMMAND_FAILED == data) {
				if (observable instanceof Command) {
					deleteFailedCommand((Command) observable);
				}
			}
		}
	}

	@Override
	public LinkedList<Command> getCommands() {
		return mCommandList;
	}

	@Override
	public void decrementCounter() {
		mCommandCounter--;
		if (mCommandCounter == 1) {
			UndoRedoManager.getInstance().update(
					UndoRedoManager.StatusMode.DISABLE_UNDO);
		}

	}

	@Override
	public void resetIndex() {
		mCommandIndex = 0;
		Log.i(PaintroidApplication.TAG, "reset");
	}
}
