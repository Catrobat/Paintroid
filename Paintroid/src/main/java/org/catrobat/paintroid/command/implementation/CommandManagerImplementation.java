/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
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

import android.util.Pair;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.command.LayerBitmapCommand;
import org.catrobat.paintroid.eventlistener.OnActiveLayerChangedListener;
import org.catrobat.paintroid.eventlistener.OnLayerEventListener;
import org.catrobat.paintroid.eventlistener.OnRefreshLayerDialogListener;
import org.catrobat.paintroid.eventlistener.OnUpdateTopBarListener;
import org.catrobat.paintroid.tools.Layer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;

public class CommandManagerImplementation implements CommandManager, Observer {
	private static final int INIT_APP_lAYER_COUNT = 1;

	enum CommandType {COMMIT_LAYER_BITMAP_COMMAND
		,ADD_LAYER
		,REMOVE_LAYER
		,MERGE_LAYERS
		,CHANGE_LAYER_VISIBILITY
		,LOCK_LAYER
		,RENAME_LAYER}


	enum Action {UNDO, REDO}

	private LinkedList<Pair<CommandType, LayerCommand>> mLayerOperationsCommandList;
	private LinkedList<Pair<CommandType, LayerCommand>> mLayerOperationsUndoCommandList;
	private ArrayList<LayerBitmapCommand> mDrawBitmapCommandsAtLayer;

	private OnRefreshLayerDialogListener mRefreshLayerDialogListener;
	private OnUpdateTopBarListener mUpdateTopBarListener;
	private ArrayList<OnActiveLayerChangedListener> mChangeActiveLayerListener;
	private OnLayerEventListener mOnLayerEventListener;

	public CommandManagerImplementation()
	{
		mLayerOperationsCommandList = new LinkedList<Pair<CommandType, LayerCommand>>();
		mLayerOperationsUndoCommandList = new LinkedList<Pair<CommandType, LayerCommand>>();
		mDrawBitmapCommandsAtLayer = new ArrayList<LayerBitmapCommand>();
	}

	public void setRefreshLayerDialogListener(OnRefreshLayerDialogListener listener)
	{
		mRefreshLayerDialogListener = listener;
	}

	public void setUpdateTopBarListener(OnUpdateTopBarListener listener)
	{
		mUpdateTopBarListener = listener;
	}

	public void addChangeActiveLayerListener(OnActiveLayerChangedListener listener)
	{
		if(mChangeActiveLayerListener == null)
		{
			mChangeActiveLayerListener = new ArrayList<OnActiveLayerChangedListener>();
		}

		mChangeActiveLayerListener.add(listener);
	}

	public void setLayerEventListener(OnLayerEventListener listener)
	{
		mOnLayerEventListener = listener;
	}

	@Override
	public void commitCommandToLayer(LayerCommand layerCommand, Command bitmapCommand) {
		synchronized (mLayerOperationsCommandList) {
			clearUndoCommandList();
			enableUndo(true);

			ArrayList<LayerBitmapCommand> result = getLayerBitmapCommands(layerCommand.getLayer().getLayerID());
			result.get(0).commitCommandToLayer(bitmapCommand);
			layerCommand.setLayersBitmapCommands(result);

			mLayerOperationsCommandList.addLast(createLayerCommand(CommandType.COMMIT_LAYER_BITMAP_COMMAND, layerCommand));
		}

		drawingSurfaceRedraw();
		layerDialogRefreshView();
	}

	@Override
	public void commitAddLayerCommand(LayerCommand layerCommand) {
		synchronized (mLayerOperationsCommandList) {
			clearUndoCommandList();

			LayerBitmapCommand bitmapCommand = new LayerBitmapCommandImpl(layerCommand);
			layerCommand.setLayersBitmapCommands(convertLayerBitmapCommandToList(bitmapCommand));

			mDrawBitmapCommandsAtLayer.add(bitmapCommand);
			mLayerOperationsCommandList.addLast(createLayerCommand(CommandType.ADD_LAYER, layerCommand));

			if(mLayerOperationsCommandList.size() > INIT_APP_lAYER_COUNT) {
				enableUndo(true);
			}
		}

		drawingSurfaceRedraw();
	}

	@Override
	public void commitRemoveLayerCommand(LayerCommand layerCommand) {
		synchronized (mLayerOperationsCommandList) {
			clearUndoCommandList();
			enableUndo(true);

			ArrayList<LayerBitmapCommand> result = getLayerBitmapCommands(layerCommand.getLayer().getLayerID());
			layerCommand.setLayersBitmapCommands(getLayerBitmapCommands(layerCommand.getLayer().getLayerID()));

			mDrawBitmapCommandsAtLayer.remove(result.get(0));
			mLayerOperationsCommandList.addLast(createLayerCommand(CommandType.REMOVE_LAYER, layerCommand));
		}

		drawingSurfaceRedraw();
	}

	@Override
	public void commitMergeLayerCommand(LayerCommand layerCommand) {
		synchronized (mLayerOperationsCommandList) {
			clearUndoCommandList();
			enableUndo(true);

			ArrayList<LayerBitmapCommand> result = getLayerBitmapCommands(layerCommand.getLayersToMerge());
			layerCommand.setLayersBitmapCommands(result);

			LayerBitmapCommand bitmapCommand = new LayerBitmapCommandImpl(layerCommand);
			for (LayerBitmapCommand manager: result) {
				bitmapCommand.copyLayerCommands(manager.getLayerCommands());
				mDrawBitmapCommandsAtLayer.remove(manager);
			}

			mDrawBitmapCommandsAtLayer.add(bitmapCommand);
			mLayerOperationsCommandList.addLast(createLayerCommand(CommandType.MERGE_LAYERS, layerCommand));
		}

		drawingSurfaceRedraw();
	}


	@Override
	public void commitLayerVisibilityCommand(LayerCommand layerCommand) {
		synchronized (mLayerOperationsCommandList) {
			clearUndoCommandList();
			enableUndo(true);

			mLayerOperationsCommandList.addLast(createLayerCommand(CommandType.CHANGE_LAYER_VISIBILITY, layerCommand));
		}

		drawingSurfaceRedraw();
	}

	@Override
	public void commitLayerLockCommand(LayerCommand layerCommand) {
		synchronized (mLayerOperationsCommandList) {
			clearUndoCommandList();
			enableUndo(true);

			mLayerOperationsCommandList.addLast(createLayerCommand(CommandType.LOCK_LAYER, layerCommand));
		}
	}

	@Override
	public void commitRenameLayerCommand(LayerCommand layerCommand) {
		synchronized (mLayerOperationsCommandList) {
			clearUndoCommandList();
			enableUndo(true);

			mLayerOperationsCommandList.addLast(createLayerCommand(CommandType.RENAME_LAYER, layerCommand));
		}
	}

	private ArrayList<LayerBitmapCommand> convertLayerBitmapCommandToList(LayerBitmapCommand command) {
		ArrayList<LayerBitmapCommand> result = new ArrayList<LayerBitmapCommand>(1);
		result.add(command);
		return  result;
	}

	private ArrayList<LayerBitmapCommand> getLayerBitmapCommands(int layerId) {
		ArrayList<Integer> ids = new ArrayList<Integer>(1);
		ids.add(layerId);
		return getLayerBitmapCommands(ids);
	}

	private ArrayList<LayerBitmapCommand> getLayerBitmapCommands(ArrayList<Integer> layerIds) {
		synchronized (mDrawBitmapCommandsAtLayer) {
			ArrayList<LayerBitmapCommand> result = new ArrayList<LayerBitmapCommand>();

			for (LayerBitmapCommand layerBitmapCommand : mDrawBitmapCommandsAtLayer) {
				for(int id : layerIds) {
					if (layerBitmapCommand.getLayer().getLayerID() == id) {
						result.add(layerBitmapCommand);
					}
				}
			}

			return result;
		}
	}

	private Pair<CommandType, LayerCommand> createLayerCommand(CommandType operation, LayerCommand layerCommand) {
		return new Pair<CommandType, LayerCommand>(operation, layerCommand);
	}

	@Override
	public synchronized void resetAndClear(boolean clearLayerBitmapCommandsList) {
		mLayerOperationsCommandList.clear();
		mLayerOperationsUndoCommandList.clear();
		mDrawBitmapCommandsAtLayer.clear();
		enableRedo(false);
		enableUndo(false);
	}

	@Override
	public boolean checkIfDrawn() {
		if (mDrawBitmapCommandsAtLayer.get(0).moreCommands())
			return true;

		return false;
	}

	@Override
	public void undo() {
		synchronized (mLayerOperationsCommandList) {
			if (mLayerOperationsCommandList.size() > INIT_APP_lAYER_COUNT) {
				Pair<CommandType, LayerCommand> command = mLayerOperationsCommandList.removeLast();
				mLayerOperationsUndoCommandList.addFirst(command);
				processCommand(command, Action.UNDO);
				enableRedo(true);

				if(mLayerOperationsCommandList.size() == INIT_APP_lAYER_COUNT) {
					onFirstCommandReached();
				}
			}
		}
	}

	private void onFirstCommandReached() {
		changeActiveLayer(mLayerOperationsCommandList.get(0).second.getLayer());
		enableUndo(false);
	}

	@Override
	public void redo() {
		synchronized (mLayerOperationsUndoCommandList) {
			if (mLayerOperationsUndoCommandList.size() != 0) {
				enableUndo(true);
				Pair<CommandType, LayerCommand> command = mLayerOperationsUndoCommandList.removeFirst();
				mLayerOperationsCommandList.addLast(command);
				processCommand(command, Action.REDO);
				if(mLayerOperationsUndoCommandList.size() == 0) {
					enableRedo(false);
				}
			}
		}
	}

	private void clearUndoCommandList() {
		synchronized (mLayerOperationsCommandList) {
			enableRedo(false);
			mLayerOperationsUndoCommandList.clear();
		}
	}

	private void processCommand(Pair<CommandType, LayerCommand> command, Action action) {
		switch (action) {
			case UNDO:
				processUndoCommand(command);
				break;
			case REDO:
				processRedoCommand(command);
				break;
		}
	}

	private void processUndoCommand(Pair<CommandType, LayerCommand> command) {
		switch (command.first) {
			case COMMIT_LAYER_BITMAP_COMMAND:
				handleUndoCommitLayerBitmapCommand(command.second);
				break;
			case ADD_LAYER:
				handleRemoveLayer(command.second);
				break;
			case REMOVE_LAYER:
				handleAddLayer(command.second);
				break;
			case MERGE_LAYERS:
				handleUnmerge(command.second);
				break;
			case CHANGE_LAYER_VISIBILITY:
				handleLayerVisibilityChanged(command.second);
				break;
			case LOCK_LAYER:
				handleLayerLockedChanged(command.second);
				break;
			case RENAME_LAYER:
				handleLayerRename(command.second);
				break;
		}
	}

	private void processRedoCommand(Pair<CommandType, LayerCommand> command) {
		switch (command.first) {
			case COMMIT_LAYER_BITMAP_COMMAND:
				handleRedoCommitLayerBitmapCommand(command.second);
				break;
			case ADD_LAYER:
				handleAddLayer(command.second);
				break;
			case REMOVE_LAYER:
				handleRemoveLayer(command.second);
				break;
			case MERGE_LAYERS:
				handleMerge(command.second);
				break;
			case CHANGE_LAYER_VISIBILITY:
				handleLayerVisibilityChanged(command.second);
				break;
			case LOCK_LAYER:
				handleLayerLockedChanged(command.second);
				break;
			case RENAME_LAYER:
				handleLayerRename(command.second);
				break;
		}
	}

	private void handleUndoCommitLayerBitmapCommand(LayerCommand command) {
		command.getLayersBitmapCommands().get(0).undo();
		changeActiveLayer(command.getLayer());
		drawingSurfaceRedraw();
	}

	private void handleRedoCommitLayerBitmapCommand(LayerCommand command) {
		command.getLayersBitmapCommands().get(0).redo();
		changeActiveLayer(command.getLayer());
		drawingSurfaceRedraw();
	}

	private void handleAddLayer(LayerCommand command) {
		mDrawBitmapCommandsAtLayer.add(command.getLayersBitmapCommands().get(0));
		addLayer(command.getLayer());

		changeActiveLayer(command.getLayer());
		layerDialogRefreshView();
		drawingSurfaceRedraw();
	}

	private void handleRemoveLayer(LayerCommand command) {
		mDrawBitmapCommandsAtLayer.remove(command.getLayersBitmapCommands().get(0));
		removeLayer(command.getLayer());

		changeActiveLayer(getNextExistingLayerInCommandList(command.getLayer().getLayerID()));
		layerDialogRefreshView();
		drawingSurfaceRedraw();
	}

	/**
	 * Undo - Redo operations are reflections of one another. By merge the previously  merged layer
	 * needs to be re-added along with its LayerBitmapCommand, while origin layers need to be
	 * removed along with their LayerBitmapCommands.
	 * @param command Layer command containing merged layer and its LayerBitmapCommand.
	 */
	private void handleMerge(LayerCommand command) {
		ArrayList<LayerBitmapCommand> result = getLayerBitmapCommands(command.getLayersToMerge());

		for (LayerBitmapCommand bitmapCommand: result) {
			removeLayer(bitmapCommand.getLayer());
			mDrawBitmapCommandsAtLayer.remove(bitmapCommand);
		}

		addLayer(command.getLayer());
		mDrawBitmapCommandsAtLayer.add(command.getLayersBitmapCommands().get(0));

		command.setLayersBitmapCommands(result);

		changeActiveLayer(command.getLayer());
		layerDialogRefreshView();
		drawingSurfaceRedraw();
	}

	/**
	 * Undo - Redo operations are reflections of one another. By un-merge the previously merged layer
	 * needs to be removed along with its LayerBitmapCommand, while origin layers need to be
	 * re-added along with their LayerBitmapCommands.
	 * @param command Layer command containing origin layers and their LayerBitmapCommands.
	 */
	private void handleUnmerge(LayerCommand command) {
		ArrayList<LayerBitmapCommand> result = getLayerBitmapCommands(command.getLayer().getLayerID());

		mDrawBitmapCommandsAtLayer.remove(result.get(0));
		removeLayer(command.getLayer());

		ListIterator<LayerBitmapCommand> iterator = command.getLayersBitmapCommands().listIterator();
		LayerBitmapCommand bitmapCommand;
		while (iterator.hasNext()) {
			bitmapCommand = iterator.next();
			addLayer(bitmapCommand.getLayer());
			mDrawBitmapCommandsAtLayer.add(bitmapCommand);
			iterator.remove();
		}

		command.setLayersBitmapCommands(result);

		changeActiveLayer(getNextExistingLayerInCommandList(command.getLayer().getLayerID()));
		layerDialogRefreshView();
		drawingSurfaceRedraw();
	}

	private void handleLayerVisibilityChanged(LayerCommand command) {
		command.getLayer().setVisible(!command.getLayer().getVisible());

		changeActiveLayer(command.getLayer());
		layerDialogRefreshView();
		drawingSurfaceRedraw();
	}

	private void handleLayerLockedChanged(LayerCommand command) {
		command.getLayer().setLocked(!command.getLayer().getLocked());

		changeActiveLayer(command.getLayer());
		layerDialogRefreshView();
	}

	private void handleLayerRename(LayerCommand command) {
		String layerName = command.getLayer().getName();
		command.getLayer().setName(command.getLayerNameHolder());
		command.setLayerNameHolder(layerName);

		changeActiveLayer(command.getLayer());
		layerDialogRefreshView();
	}

	private Layer getNextExistingLayerInCommandList(int originLayerId) {
		synchronized (mLayerOperationsCommandList) {
			ListIterator<Pair<CommandType, LayerCommand>> iterator = mLayerOperationsCommandList.listIterator(mLayerOperationsCommandList.size());

			Layer commandsLayer;
			while (iterator.hasPrevious()) {
				commandsLayer = iterator.previous().second.getLayer();

				if (commandsLayer.getLayerID() != originLayerId ) {
					if(getLayerBitmapCommands(commandsLayer.getLayerID()).size() == 1) {
						return commandsLayer;
					}
				}
			}

			return null;
		}
	}

	private synchronized void deleteFailedCommand(Command command) {

	}

	private void drawingSurfaceRedraw() {
		/*
		if(mRedrawSurfaceViewListener != null)
		{
			mRedrawSurfaceViewListener.onSurfaceViewRedraw();
		}
		*/
	}

	private void layerDialogRefreshView() {
		if(mRefreshLayerDialogListener != null) {
			mRefreshLayerDialogListener.onLayerDialogRefreshView();
		}
	}

	private void enableUndo(boolean enable) {
		if(mUpdateTopBarListener != null) {
			mUpdateTopBarListener.onUndoEnabled(enable);
		}
	}

	private void enableRedo(boolean enable) {
		if(mUpdateTopBarListener != null) {
			mUpdateTopBarListener.onRedoEnabled(enable);
		}
	}

	private void changeActiveLayer(Layer layer) {
		if(mChangeActiveLayerListener != null) {
			for(OnActiveLayerChangedListener listener : mChangeActiveLayerListener) {
				listener.onActiveLayerChanged(layer);
			}
		}
	}

	private void removeLayer(Layer layer) {
		if(mOnLayerEventListener != null) {
			mOnLayerEventListener.onLayerRemoved(layer);
		}
	}

	private void addLayer(Layer layer) {
		if(mOnLayerEventListener != null) {
			mOnLayerEventListener.onLayerAdded(layer);
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
}
