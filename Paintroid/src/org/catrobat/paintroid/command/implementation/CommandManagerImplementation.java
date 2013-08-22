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
import org.catrobat.paintroid.command.implementation.layer.ChangeLayerCommand;
import org.catrobat.paintroid.command.implementation.layer.DeleteLayerCommand;
import org.catrobat.paintroid.command.implementation.layer.HideLayerCommand;
import org.catrobat.paintroid.command.implementation.layer.ShowLayerCommand;
import org.catrobat.paintroid.command.implementation.layer.SwitchLayerCommand;
import org.catrobat.paintroid.dialog.layerchooser.LayerChooserDialog;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.Log;

public class CommandManagerImplementation implements CommandManager, Observer {
	private static final int MAX_COMMANDS = 512;

	private final LinkedList<Command> mCommandList;
	private int mCommandCounter;
	private int mCommandIndex;
	private Bitmap mOriginalBitmap;

	private int lastLayer;

	public CommandManagerImplementation() {
		mCommandList = new LinkedList<Command>();
		// The first command in the list is needed to clear the image when
		// rolling back commands.
		mCommandList.add(new ClearCommand());
		mCommandCounter = 1;
		mCommandIndex = 1;
		lastLayer = 0;
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

			if (mCommandList.get(mCommandIndex).isDeleted()
					|| mCommandList.get(mCommandIndex).isHidden()
					|| mCommandList.get(mCommandIndex).isUndone()) {
				mCommandIndex++;

				return getNextCommand();
			}
			return mCommandList.get(mCommandIndex++);
		} else {
			return null;
		}
	}

	@Override
	public synchronized boolean commitCommand(Command command) {

		UndoRedoManager.getInstance().update(StatusMode.DISABLE_REDO);

		// Switch-Layer-Command & Hide-/Show-Layer-Command & Change-Layer-
		// Command shall not be saved and just run once
		if (command instanceof SwitchLayerCommand
				|| command instanceof ShowLayerCommand
				|| command instanceof HideLayerCommand
				|| command instanceof ChangeLayerCommand) {
			command.run(null, null);

			this.resetIndex();
			return mCommandList != null;
		}
		// The Delete-Layer-Command shall run on the unsorted current
		// commandlist and then be added
		else if (command instanceof DeleteLayerCommand) {
			command.run(null, null);
			int position = findLastCallIndexSorted(mCommandList,
					PaintroidApplication.currentLayer, true);
			mCommandList.add(position, command);
			mCommandCounter++;
			command.setCommandLayer(PaintroidApplication.currentLayer);
			this.resetIndex();
			return mCommandList.get(position) != null;
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

		// First remove any previously undone commands from the top of the
		// queue.

		for (int i = 1; i < mCommandList.size(); i++) {
			if (mCommandList.get(i).isUndone()
					|| mCommandList.get(i).isDeleted()) {
				mCommandList.remove(i).freeResources();
				mCommandCounter--;
			}
		}

		((BaseCommand) command).addObserver(this);
		command.setCommandLayer(PaintroidApplication.currentLayer);

		int position = findLastCallIndexSorted(mCommandList,
				PaintroidApplication.currentLayer, false);
		mCommandList.add(position, command);
		this.resetIndex();

		return mCommandList.get(position) != null;
	}

	private int findLastCallIndexSorted(LinkedList<Command> mCommandList,
			int currentLayer, boolean withUndone) {

		if (mCommandList.size() == 1) {
			return 1;
		} else {
			if (currentLayer != lastLayer || mCommandList.size() == 2) {
				mCommandList = sortList(mCommandList);
			}
			if (withUndone == false) {
				for (int i = mCommandList.size() - 1; i >= 1; i--) {
					if (mCommandList.get(i).getCommandLayer() == currentLayer
							&& mCommandList.get(i).isUndone() == false) {
						lastLayer = currentLayer;
						return i + 1;
					}
				}
			} else {
				for (int i = 1; i < mCommandList.size(); i++) {
					if (mCommandList.get(i).getCommandLayer() == currentLayer
							&& mCommandList.get(i).isUndone() == true) {
						lastLayer = currentLayer;
						return i;
					}
				}
			}
		}

		return 1;
	}

	private int findLastCallIndexUnSorted(LinkedList<Command> mCommandList,
			int currentLayer) {
		printList();
		if (mCommandList.size() == 1) {
			return 1;
		} else {
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
					+ mCommandList.get(i).getCommandLayer() + " "
					+ mCommandList.get(i).isUndone() + " ");
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
		showAllCommands();
		boolean match = false;
		int offset = 0;
		int i = 1;
		while (i < mCommandList.size() - offset) {
			if (mCommandList.get(i) instanceof DeleteLayerCommand) {
				DeleteLayerCommand dlc = (DeleteLayerCommand) mCommandList
						.get(i);
				dlc.getData().selected = false;
				LayerChooserDialog.layer_data.add(dlc.getLayerIndex(),
						dlc.getData());
				dlc.reverseDeletion(dlc.getLayerIndex());
				dlc.setUndone(true);
				match = true;
				this.resetIndex();
				offset++;
				i--;
				mCommandCounter--;
			}
			i++;
		}
		if (match == false) {

			int pos = findLastCallIndexSorted(mCommandList,
					PaintroidApplication.currentLayer, false);
			Log.i(PaintroidApplication.TAG, " " + pos);
			if (pos > 1) {
				if (mCommandCounter > 1) {
					this.resetIndex();

					mCommandList.get(pos - 1).setUndone(true);
					UndoRedoManager.getInstance().update(
							UndoRedoManager.StatusMode.ENABLE_REDO);

					if (!hasUndosLeft(pos)) {
						UndoRedoManager.getInstance().update(
								UndoRedoManager.StatusMode.DISABLE_UNDO);
					}
				}
			}
		}
	}

	@Override
	public boolean hasUndosLeft(int pos) {
		for (int i = 1; i < pos; i++) {
			if (mCommandList.get(i).getCommandLayer() == PaintroidApplication.currentLayer
					&& !mCommandList.get(i).isUndone()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasRedosLeft(int pos) {
		for (int i = mCommandList.size() - 1; i > pos; i--) {
			if (mCommandList.get(i).getCommandLayer() == PaintroidApplication.currentLayer
					&& mCommandList.get(i).isUndone()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public synchronized void redo() {

		int pos = findLastCallIndexSorted(mCommandList,
				PaintroidApplication.currentLayer, true);
		Log.i(PaintroidApplication.TAG, " " + pos + " --- " + mCommandCounter);

		if (pos > 0 && pos < mCommandList.size()) {
			if (mCommandCounter > 1) {

				this.resetIndex();

				UndoRedoManager.getInstance().update(
						UndoRedoManager.StatusMode.ENABLE_UNDO);

				mCommandList.get(pos).setUndone(false);

				if (!hasRedosLeft(pos)) {
					UndoRedoManager.getInstance().update(
							UndoRedoManager.StatusMode.DISABLE_REDO);
				}
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

	private void showAllCommands() {
		for (int j = 0; j < PaintroidApplication.commandManager.getCommands()
				.size(); j++) {
			Log.i(PaintroidApplication.TAG,
					String.valueOf(j)
							+ " "
							+ PaintroidApplication.commandManager.getCommands()
									.get(j).toString()
							+ " "
							+ String.valueOf(PaintroidApplication.commandManager
									.getCommands().get(j).getCommandLayer())
							+ " "
							+ PaintroidApplication.commandManager.getCommands()
									.get(j).isDeleted());
		}

	}

	@Override
	public void resetIndex() {
		mCommandIndex = 0;
	}
}
