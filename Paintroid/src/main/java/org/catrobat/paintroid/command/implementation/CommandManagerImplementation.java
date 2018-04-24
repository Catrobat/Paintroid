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

import android.os.Handler;
import android.os.Looper;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.command.LayerBitmapCommand;
import org.catrobat.paintroid.eventlistener.OnActiveLayerChangedListener;
import org.catrobat.paintroid.eventlistener.OnLayerEventListener;
import org.catrobat.paintroid.eventlistener.OnUpdateTopBarListener;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.tools.Layer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;

public class CommandManagerImplementation implements CommandManager, Observer {
	private static final int INIT_APP_LAYER_COUNT = 1;
	private static final int NUM_LAYER_COMMANDS_FOR_DELETE = 25;
	private LinkedList<LayerCommand> layerOperationsCommandList;
	private LinkedList<LayerCommand> layerOperationsUndoCommandList;
	private ArrayList<LayerBitmapCommand> drawBitmapCommandsAtLayer;
	private boolean initialized;
	private OnUpdateTopBarListener updateTopBarListener;
	private ArrayList<OnActiveLayerChangedListener> changeActiveLayerListener;
	private OnLayerEventListener onLayerEventListener;

	public CommandManagerImplementation() {
		if (PaintroidApplication.layerOperationsCommandList != null) {
			layerOperationsCommandList = PaintroidApplication.layerOperationsCommandList;
			layerOperationsUndoCommandList = PaintroidApplication.layerOperationsUndoCommandList;
			drawBitmapCommandsAtLayer = PaintroidApplication.drawBitmapCommandsAtLayer;
		} else {
			layerOperationsCommandList = new LinkedList<>();
			layerOperationsUndoCommandList = new LinkedList<>();
			drawBitmapCommandsAtLayer = new ArrayList<>();
		}
		initialized = false;
	}

	@Override
	public void setUpdateTopBarListener(OnUpdateTopBarListener listener) {
		updateTopBarListener = listener;
	}

	@Override
	public void addChangeActiveLayerListener(OnActiveLayerChangedListener listener) {
		if (changeActiveLayerListener == null) {
			changeActiveLayerListener = new ArrayList<>();
		}

		changeActiveLayerListener.add(listener);
	}

	@Override
	public void setLayerEventListener(OnLayerEventListener listener) {
		onLayerEventListener = listener;
	}

	@Override
	public void commitCommandToLayer(LayerCommand layerCommand, Command bitmapCommand) {
		synchronized (layerOperationsCommandList) {
			clearUndoCommandList();
			enableUndo(true);

			ArrayList<LayerBitmapCommand> result = getLayerBitmapCommands(layerCommand.getLayer().getLayerID());
			result.get(0).commitCommandToLayer(bitmapCommand);
			layerCommand.setLayersBitmapCommands(result);
		}

		drawingSurfaceRedraw();
	}

	@Override
	public LayerBitmapCommand getLayerBitmapCommand(LayerCommand layerCommand) {
		ArrayList<LayerBitmapCommand> result = getLayerBitmapCommands(layerCommand.getLayer().getLayerID());
		return result.get(0);
	}

	@Override
	public void commitAddLayerCommand(LayerCommand layerCommand) {
		synchronized (layerOperationsCommandList) {
			clearUndoCommandList();

			LayerBitmapCommand bitmapCommand = new LayerBitmapCommandImpl(layerCommand);
			layerCommand.setLayersBitmapCommands(convertLayerBitmapCommandToList(bitmapCommand));

			drawBitmapCommandsAtLayer.add(bitmapCommand);

			layerCommand.setLayerCommandType(CommandType.ADD_LAYER);
			layerOperationsCommandList.addLast(layerCommand);

			for (LayerBitmapCommand layerBitmapCommand : drawBitmapCommandsAtLayer) {
				layerBitmapCommand.addCommandToList(layerCommand);
			}

			if (layerOperationsCommandList.size() > INIT_APP_LAYER_COUNT) {
				enableUndo(true);
			}
		}

		drawingSurfaceRedraw();
	}

	@Override
	public void commitRemoveLayerCommand(LayerCommand layerCommand) {
		synchronized (layerOperationsCommandList) {
			clearUndoCommandList();
			enableUndo(true);

			ArrayList<LayerBitmapCommand> result = getLayerBitmapCommands(layerCommand.getLayer().getLayerID());
			layerCommand.setLayersBitmapCommands(result);

			int id = layerCommand.getLayer().getLayerID();
			int pos = LayerListener.getInstance().getAdapter().getPosition(id);
			layerCommand.setOldLayerPosition(pos);

			drawBitmapCommandsAtLayer.remove(result.get(0));

			layerCommand.setLayerCommandType(CommandType.REMOVE_LAYER);
			layerOperationsCommandList.addLast(layerCommand);

			for (LayerBitmapCommand layerBitmapCommand : drawBitmapCommandsAtLayer) {
				layerBitmapCommand.addCommandToList(layerCommand);
			}
		}

		drawingSurfaceRedraw();
	}

	@Override
	public void commitMergeLayerCommand(LayerCommand layerCommand) {
		synchronized (layerOperationsCommandList) {
			clearUndoCommandList();
			enableUndo(true);

			ArrayList<LayerBitmapCommand> result = getLayerBitmapCommands(layerCommand.getLayersToMerge());
			layerCommand.setLayersBitmapCommands(result);

			LayerBitmapCommand bitmapCommand = new LayerBitmapCommandImpl(layerCommand);
			for (LayerBitmapCommand manager : result) {
				bitmapCommand.copyLayerCommands(manager.getLayerCommands());
				drawBitmapCommandsAtLayer.remove(manager);
			}

			drawBitmapCommandsAtLayer.add(bitmapCommand);
			layerCommand.setLayerCommandType(CommandType.MERGE_LAYERS);
			layerOperationsCommandList.addLast(layerCommand);

			for (LayerBitmapCommand layerBitmapCommand : drawBitmapCommandsAtLayer) {
				layerBitmapCommand.addCommandToList(layerCommand);
			}
		}

		drawingSurfaceRedraw();
	}

	private ArrayList<LayerBitmapCommand> convertLayerBitmapCommandToList(LayerBitmapCommand command) {
		ArrayList<LayerBitmapCommand> result = new ArrayList<>(1);
		result.add(command);
		return result;
	}

	@Override
	public ArrayList<LayerBitmapCommand> getLayerBitmapCommands(int layerId) {
		ArrayList<Integer> ids = new ArrayList<>(1);
		ids.add(layerId);
		return getLayerBitmapCommands(ids);
	}

	private ArrayList<LayerBitmapCommand> getLayerBitmapCommands(ArrayList<Integer> layerIds) {
		synchronized (drawBitmapCommandsAtLayer) {
			ArrayList<LayerBitmapCommand> result = new ArrayList<>();

			for (LayerBitmapCommand layerBitmapCommand : drawBitmapCommandsAtLayer) {
				for (int id : layerIds) {
					if (layerBitmapCommand.getLayer().getLayerID() == id) {
						result.add(layerBitmapCommand);
					}
				}
			}

			return result;
		}
	}

	@Override
	public synchronized void resetAndClear(boolean clearLayerBitmapCommandsList) {
		layerOperationsCommandList.clear();
		layerOperationsUndoCommandList.clear();
		drawBitmapCommandsAtLayer.clear();
		enableRedo(false);
		enableUndo(false);
	}

	@Override
	public boolean checkIfDrawn() {
		return drawBitmapCommandsAtLayer.get(0).moreCommands();
	}

	@Override
	public void undo() {
		synchronized (layerOperationsCommandList) {
			if (layerOperationsCommandList.size() > INIT_APP_LAYER_COUNT) {
				LayerCommand command = layerOperationsCommandList.removeLast();
				layerOperationsUndoCommandList.addFirst(command);
				processLayerUndo(command);
				enableRedo(true);

				if (layerOperationsCommandList.size() == INIT_APP_LAYER_COUNT) {
					onFirstCommandReached();
				}
			}
		}
	}

	private void onFirstCommandReached() {
		changeActiveLayer(layerOperationsCommandList.get(0).getLayer());
		enableUndo(false);
	}

	@Override
	public void redo() {
	}

	private void clearUndoCommandList() {
		synchronized (layerOperationsCommandList) {
			enableRedo(false);

			for (Iterator<LayerCommand> layerCommandIterator = layerOperationsUndoCommandList.iterator(); layerCommandIterator.hasNext(); ) {
				LayerCommand layerCommand = layerCommandIterator.next();
				for (LayerBitmapCommand layerBitmapCommand : drawBitmapCommandsAtLayer) {
					layerBitmapCommand.getLayerUndoCommands().remove(layerCommand);
				}
			}

			layerOperationsUndoCommandList.clear();
		}
	}

	@Override
	public void processLayerUndo(LayerCommand command) {
		switch (command.getLayerCommandType()) {
			case ADD_LAYER:
				handleRemoveLayer(command);
				break;
			case REMOVE_LAYER:
				handleAddLayer(command);
				break;
			case MERGE_LAYERS:
				handleUnmerge(command);
				break;
		}
	}

	@Override
	public void processLayerRedo(LayerCommand command) {
		switch (command.getLayerCommandType()) {
			case ADD_LAYER:
				handleAddLayer(command);
				break;
			case REMOVE_LAYER:
				handleRemoveLayer(command);
				break;
			case MERGE_LAYERS:
				handleMerge(command);
				break;
		}
	}

	private void handleAddLayer(LayerCommand command) {
		drawBitmapCommandsAtLayer.add(command.getLayersBitmapCommands().get(0));
		addLayer(command.getLayer());

		if (command.getOldLayerPosition() != -1) {
			moveLayer(0, command.getOldLayerPosition());
		}

		if (command.getLayerCommandType() == CommandType.REMOVE_LAYER) {
			command.getLayersBitmapCommands().get(0).getLayerUndoCommands().add(command);
		}
		LayerListener.getInstance().updateButtonResource();
	}

	private void handleRemoveLayer(LayerCommand command) {
		drawBitmapCommandsAtLayer.remove(command.getLayersBitmapCommands().get(0));
		removeLayer(command.getLayer());
		int pos = LayerListener.getInstance().getAdapter().getLayers().size() - 1;
		changeActiveLayer(LayerListener.getInstance().getAdapter().getLayers().get(pos));
		LayerListener.getInstance().updateButtonResource();
	}

	/**
	 * Undo - Redo operations are reflections of one another. By merge the previously  merged layer
	 * needs to be re-added along with its LayerBitmapCommand, while origin layers need to be
	 * removed along with their LayerBitmapCommands.
	 *
	 * @param command Layer command containing merged layer and its LayerBitmapCommand.
	 */
	private void handleMerge(LayerCommand command) {
		ArrayList<LayerBitmapCommand> result = getLayerBitmapCommands(command.getLayersToMerge());

		for (LayerBitmapCommand bitmapCommand : result) {
			removeLayer(bitmapCommand.getLayer());
			drawBitmapCommandsAtLayer.remove(bitmapCommand);
		}

		addLayer(command.getLayer());
		drawBitmapCommandsAtLayer.add(command.getLayersBitmapCommands().get(0));

		command.setLayersBitmapCommands(result);

		changeActiveLayer(command.getLayer());
		drawingSurfaceRedraw();
		LayerListener.getInstance().updateButtonResource();
	}

	/**
	 * Undo - Redo operations are reflections of one another. By un-merge the previously merged layer
	 * needs to be removed along with its LayerBitmapCommand, while origin layers need to be
	 * re-added along with their LayerBitmapCommands.
	 *
	 * @param command Layer command containing origin layers and their LayerBitmapCommands.
	 */
	private void handleUnmerge(LayerCommand command) {
		ArrayList<LayerBitmapCommand> result = getLayerBitmapCommands(command.getLayer().getLayerID());

		drawBitmapCommandsAtLayer.remove(result.get(0));
		removeLayer(command.getLayer());

		ListIterator<LayerBitmapCommand> iterator = command.getLayersBitmapCommands().listIterator();
		LayerBitmapCommand bitmapCommand;
		while (iterator.hasNext()) {
			bitmapCommand = iterator.next();
			addLayer(bitmapCommand.getLayer());
			drawBitmapCommandsAtLayer.add(bitmapCommand);

			bitmapCommand.getLayerUndoCommands().add(command);

			iterator.remove();
		}

		command.setLayersBitmapCommands(result);

		changeActiveLayer(LayerListener.getInstance().getAdapter().getLayers().get(0));
		drawingSurfaceRedraw();
		LayerListener.getInstance().updateButtonResource();
	}

	@Override
	public boolean isCommandManagerInitialized() {
		return initialized;
	}

	@Override
	public void setInitialized(boolean value) {
		initialized = value;
	}

	@Override
	public boolean isUndoCommandListEmpty() {
		return drawBitmapCommandsAtLayer.isEmpty() && layerOperationsCommandList.isEmpty();
	}

	@Override
	public boolean isRedoCommandListEmpty() {
		return layerOperationsUndoCommandList.isEmpty();
	}

	private synchronized void deleteFailedCommand() {
	}

	private void drawingSurfaceRedraw() {
	}

	@Override
	public void enableUndo(final boolean enable) {
		if (updateTopBarListener != null) {
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					updateTopBarListener.onUndoEnabled(enable);
				}
			});
		}
	}

	@Override
	public void enableRedo(final boolean enable) {
		if (updateTopBarListener != null) {
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					updateTopBarListener.onRedoEnabled(enable);
				}
			});
		}
	}

	private void changeActiveLayer(Layer layer) {
		if (changeActiveLayerListener != null) {
			for (OnActiveLayerChangedListener listener : changeActiveLayerListener) {
				listener.onActiveLayerChanged(layer);
			}
		}
	}

	private void removeLayer(Layer layer) {
		if (onLayerEventListener != null) {
			onLayerEventListener.onLayerRemoved(layer);
		}
	}

	private void addLayer(Layer layer) {
		if (onLayerEventListener != null) {
			onLayerEventListener.onLayerAdded(layer);
		}
	}

	private void moveLayer(int startPos, int targetPos) {
		if (onLayerEventListener != null) {
			onLayerEventListener.onLayerMoved(startPos, targetPos);
		}
	}

	@Override
	public void update(Observable observable, Object data) {
		if (data instanceof BaseCommand.NotifyStates
				&& BaseCommand.NotifyStates.COMMAND_FAILED == data
				&& observable instanceof Command) {
			deleteFailedCommand();
		}
	}

	@Override
	public LinkedList<LayerCommand> getLayerOperationsCommandList() {
		return layerOperationsCommandList;
	}

	@Override
	public LinkedList<LayerCommand> getLayerOperationsUndoCommandList() {
		return layerOperationsUndoCommandList;
	}

	@Override
	public ArrayList<LayerBitmapCommand> getDrawBitmapCommandsAtLayer() {
		return drawBitmapCommandsAtLayer;
	}

	@Override
	public void addLayerCommandToUndoList() {
		synchronized (layerOperationsCommandList) {
			if (layerOperationsCommandList.size() > 1) {
				LayerCommand command = layerOperationsCommandList.removeLast();
				layerOperationsUndoCommandList.addFirst(command);
			}
		}
	}

	@Override
	public void addLayerCommandToRedoList() {
		synchronized (layerOperationsCommandList) {
			synchronized (layerOperationsUndoCommandList) {
				LayerCommand command = layerOperationsUndoCommandList.removeFirst();
				layerOperationsCommandList.addLast(command);
			}
		}
	}

	@Override
	public void deleteLayerCommandFromDrawBitmapCommandsAtLayer(LayerCommand layerCommand) {
		synchronized (drawBitmapCommandsAtLayer) {
			for (LayerBitmapCommand layerBitmapCommandRunner : drawBitmapCommandsAtLayer) {
				((LayerBitmapCommandImpl) layerBitmapCommandRunner).addLayerCommandToUndoList(layerCommand);
			}
		}
	}

	@Override
	public void addLayerCommandToDrawBitmapCommandsAtLayer(LayerCommand layerCommand) {
		synchronized (drawBitmapCommandsAtLayer) {
			for (LayerBitmapCommand layerBitmapCommandRunner : drawBitmapCommandsAtLayer) {
				((LayerBitmapCommandImpl) layerBitmapCommandRunner).addLayerCommandToRedoList(layerCommand);
			}
		}
	}

	@Override
	public void deleteCommandFirstDeletedLayer() {
		synchronized (layerOperationsCommandList) {

			if (layerOperationsCommandList.size() < NUM_LAYER_COMMANDS_FOR_DELETE) {
				return;
			}

			int layerID = -1;

			for (Iterator<LayerCommand> layerCommandIterator = layerOperationsCommandList.iterator(); layerCommandIterator.hasNext(); ) {
				LayerCommand layerCommand = layerCommandIterator.next();
				if (layerCommand.getLayerCommandType() == CommandType.REMOVE_LAYER) {
					layerID = layerCommand.getLayer().getLayerID();
					deleteCommandFromEveryList(layerCommand);
					layerCommandIterator.remove();
					break;
				}
			}

			if (layerID != -1) {
				for (Iterator<LayerCommand> layerCommandIterator = layerOperationsCommandList.iterator(); layerCommandIterator.hasNext(); ) {
					LayerCommand layerCommand = layerCommandIterator.next();
					if (layerCommand.getLayerCommandType() == CommandType.ADD_LAYER && layerCommand.getLayer().getLayerID() == layerID) {
						deleteCommandFromEveryList(layerCommand);
						layerCommandIterator.remove();
					}
				}
			}
		}
	}

	public void deleteCommandFromEveryList(LayerCommand layerCommand) {
		for (Iterator<LayerBitmapCommand> layerBitmapCommandIterator = drawBitmapCommandsAtLayer.iterator(); layerBitmapCommandIterator.hasNext(); ) {
			LayerBitmapCommand layerBitmapCommand = layerBitmapCommandIterator.next();
			layerBitmapCommand.getLayerCommands().remove(layerCommand);
			layerBitmapCommand.getLayerUndoCommands().remove(layerCommand);
		}
		layerOperationsUndoCommandList.remove(layerCommand);
	}

	public enum CommandType {
		COMMIT_LAYER_BITMAP_COMMAND, ADD_LAYER, REMOVE_LAYER, MERGE_LAYERS, NO_LAYER_COMMAND
	}
}
