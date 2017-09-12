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


import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.command.LayerBitmapCommand;
import org.catrobat.paintroid.eventlistener.OnActiveLayerChangedListener;
import org.catrobat.paintroid.eventlistener.OnLayerEventListener;
import org.catrobat.paintroid.eventlistener.OnRefreshLayerDialogListener;
import org.catrobat.paintroid.eventlistener.OnUpdateTopBarListener;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.tools.Layer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;

public class CommandManagerImplementation implements CommandManager, Observer {
	private static final int INIT_APP_lAYER_COUNT = 1;

	public enum CommandType {COMMIT_LAYER_BITMAP_COMMAND
		,ADD_LAYER
		,REMOVE_LAYER
		,MERGE_LAYERS
		,NO_LAYER_COMMAND}


	private LinkedList<LayerCommand> mLayerOperationsCommandList;
	private LinkedList<LayerCommand> mLayerOperationsUndoCommandList;
	private ArrayList<LayerBitmapCommand> mDrawBitmapCommandsAtLayer;
	private boolean initialized;

	private OnRefreshLayerDialogListener mRefreshLayerDialogListener;
	private OnUpdateTopBarListener mUpdateTopBarListener;
	private ArrayList<OnActiveLayerChangedListener> mChangeActiveLayerListener;
	private OnLayerEventListener mOnLayerEventListener;

	public CommandManagerImplementation()
	{
		if(PaintroidApplication.layerOperationsCommandList != null){
			mLayerOperationsCommandList = PaintroidApplication.layerOperationsCommandList;
			mLayerOperationsUndoCommandList = PaintroidApplication.layerOperationsUndoCommandList;
			mDrawBitmapCommandsAtLayer = PaintroidApplication.drawBitmapCommandsAtLayer;
		}
		else {
			mLayerOperationsCommandList = new LinkedList<>();
			mLayerOperationsUndoCommandList = new LinkedList<>();
			mDrawBitmapCommandsAtLayer = new ArrayList<>();
		}
		initialized = false;
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

		}

		drawingSurfaceRedraw();
		layerDialogRefreshView();
	}

	@Override
	public void addCommandToList (LayerCommand layerCommand, Command command){

	}

	@Override
	public LayerBitmapCommand getLayerBitmapCommand(LayerCommand layerCommand){
		ArrayList<LayerBitmapCommand> result = getLayerBitmapCommands(layerCommand.getLayer().getLayerID());
		return result.get(0);
	}

	@Override
	public void commitAddLayerCommand(LayerCommand layerCommand) {
		synchronized (mLayerOperationsCommandList) {
			clearUndoCommandList();

			LayerBitmapCommand bitmapCommand = new LayerBitmapCommandImpl(layerCommand);
			layerCommand.setLayersBitmapCommands(convertLayerBitmapCommandToList(bitmapCommand));

			mDrawBitmapCommandsAtLayer.add(bitmapCommand);

			layerCommand.setmLayerCommandType(CommandType.ADD_LAYER);
			mLayerOperationsCommandList.addLast(layerCommand);

			for (LayerBitmapCommand layerBitmapCommand : mDrawBitmapCommandsAtLayer) {
				layerBitmapCommand.addCommandToList(layerCommand);
			}

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
			layerCommand.setLayersBitmapCommands(result);

			mDrawBitmapCommandsAtLayer.remove(result.get(0));

			layerCommand.setmLayerCommandType(CommandType.REMOVE_LAYER);
			mLayerOperationsCommandList.addLast(layerCommand);

			for (LayerBitmapCommand layerBitmapCommand : mDrawBitmapCommandsAtLayer) {
				layerBitmapCommand.addCommandToList(layerCommand);
			}

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
			layerCommand.setmLayerCommandType(CommandType.MERGE_LAYERS);
			mLayerOperationsCommandList.addLast(layerCommand);

			for (LayerBitmapCommand layerBitmapCommand : mDrawBitmapCommandsAtLayer) {
				layerBitmapCommand.addCommandToList(layerCommand);
			}
		}

		drawingSurfaceRedraw();
	}


	private ArrayList<LayerBitmapCommand> convertLayerBitmapCommandToList(LayerBitmapCommand command) {
		ArrayList<LayerBitmapCommand> result = new ArrayList<LayerBitmapCommand>(1);
		result.add(command);
		return  result;
	}

	public ArrayList<LayerBitmapCommand> getLayerBitmapCommands(int layerId) {
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
				LayerCommand command = mLayerOperationsCommandList.removeLast();
				mLayerOperationsUndoCommandList.addFirst(command);
				processLayerUndo(command);
				enableRedo(true);

				if(mLayerOperationsCommandList.size() == INIT_APP_lAYER_COUNT) {
					onFirstCommandReached();
				}
			}
		}
	}

	private void onFirstCommandReached() {
		changeActiveLayer(mLayerOperationsCommandList.get(0).getLayer());
		enableUndo(false);
	}

	@Override
	public void redo() {
	}

	private void clearUndoCommandList() {
		synchronized (mLayerOperationsCommandList) {
			enableRedo(false);
			mLayerOperationsUndoCommandList.clear();
		}
	}

	public void processLayerUndo(LayerCommand command) {
		switch (command.getmLayerCommandType()) {
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

	public void processLayerRedo(LayerCommand command) {
		switch (command.getmLayerCommandType()) {
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
		mDrawBitmapCommandsAtLayer.add(command.getLayersBitmapCommands().get(0));
		addLayer(command.getLayer());

		if (command.getmLayerCommandType() == CommandType.REMOVE_LAYER) {
			((LayerBitmapCommandImpl)command.getLayersBitmapCommands().get(0)).getLayerUndoCommands().add(command);
		}
		LayerListener.getInstance().updateButtonResource();
	}

	private void handleRemoveLayer(LayerCommand command) {
		mDrawBitmapCommandsAtLayer.remove(command.getLayersBitmapCommands().get(0));
		removeLayer(command.getLayer());
		int pos = LayerListener.getInstance().getAdapter().getLayers().size() - 1;
		changeActiveLayer(LayerListener.getInstance().getAdapter().getLayers().get(pos));
		LayerListener.getInstance().updateButtonResource();
	}

	/**
	 * Undo - Redo operations are reflections of one another. By merge the previously  merged layer
	 * needs to be re-added along with its LayerBitmapCommand, while origin layers need to be
	 * removed along with their LayerBitmapCommands.
	 * @param command Layer command containing merged layer and its LayerBitmapCommand.
	 */
	private void handleMerge(LayerCommand command) {
		ArrayList<LayerBitmapCommand> result = getLayerBitmapCommands(command.getLayersToMerge());

		for (LayerBitmapCommand bitmapCommand : result) {
			removeLayer(bitmapCommand.getLayer());
			mDrawBitmapCommandsAtLayer.remove(bitmapCommand);
		}

		addLayer(command.getLayer());
		mDrawBitmapCommandsAtLayer.add(command.getLayersBitmapCommands().get(0));

		command.setLayersBitmapCommands(result);

		changeActiveLayer(command.getLayer());
		layerDialogRefreshView();
		drawingSurfaceRedraw();
		LayerListener.getInstance().updateButtonResource();
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

			bitmapCommand.getLayerUndoCommands().add(command);

			iterator.remove();
		}

		command.setLayersBitmapCommands(result);

		changeActiveLayer(LayerListener.getInstance().getAdapter().getLayers().get(0));
		layerDialogRefreshView();
		drawingSurfaceRedraw();
		LayerListener.getInstance().updateButtonResource();
	}

	@Override
	public boolean isCommandManagerInitialized()
	{
		return initialized;
	}

	@Override
	public void setInitialized(boolean value)
	{
		 initialized = value;
	}

	@Override
	public boolean isUndoCommandListEmpty()
	{
		if(mDrawBitmapCommandsAtLayer.size() >0 || mLayerOperationsCommandList.size() > 0)
			return false;
		else
			return true;
	}

	@Override
	public boolean isRedoCommandListEmpty()
	{
		if(mLayerOperationsUndoCommandList.size() > 0)
			return false;
		else
			return true;
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
	@Override
	public void enableUndo(boolean enable) {
		if(mUpdateTopBarListener != null) {
			mUpdateTopBarListener.onUndoEnabled(enable);
		}
	}
	@Override
	public void enableRedo(boolean enable) {
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

	@Override
	public void storeCommandLists() {
		PaintroidApplication.layerOperationsCommandList = mLayerOperationsCommandList;
		PaintroidApplication.layerOperationsUndoCommandList = mLayerOperationsUndoCommandList;
		PaintroidApplication.drawBitmapCommandsAtLayer = mDrawBitmapCommandsAtLayer;
	}

	public LinkedList<LayerCommand> getLayerOperationsCommandList() {
		return mLayerOperationsCommandList;
	}

	public LinkedList<LayerCommand> getLayerOperationsUndoCommandList() {
		return mLayerOperationsUndoCommandList;
	}

	public ArrayList<LayerBitmapCommand> getDrawBitmapCommandsAtLayer() {
		return mDrawBitmapCommandsAtLayer;
	}

	public void addLayerCommandToUndoList() {
		synchronized (mLayerOperationsCommandList) {
			if(mLayerOperationsCommandList.size() > 1){
				LayerCommand command = mLayerOperationsCommandList.removeLast();
				mLayerOperationsUndoCommandList.addFirst(command);
			}
		}
	}

	public void addLayerCommandToRedoList() {
		synchronized (mLayerOperationsCommandList) {
			synchronized (mLayerOperationsUndoCommandList) {
				LayerCommand command = mLayerOperationsUndoCommandList.removeFirst();
				mLayerOperationsCommandList.addLast(command);
			}
		}
	}

	public void deleteLayerCommandFromDrawBitmapCommandsAtLayer(LayerCommand layerCommand) {
		synchronized (mDrawBitmapCommandsAtLayer) {
			for (LayerBitmapCommand layerBitmapCommandRunner : mDrawBitmapCommandsAtLayer) {
				((LayerBitmapCommandImpl)layerBitmapCommandRunner).addLayerCommandToUndoList(layerCommand);
			}
		}
	}

	public void addLayerCommandToDrawBitmapCommandsAtLayer(LayerCommand layerCommand) {
		synchronized (mDrawBitmapCommandsAtLayer) {
			for (LayerBitmapCommand layerBitmapCommandRunner : mDrawBitmapCommandsAtLayer) {
				((LayerBitmapCommandImpl)layerBitmapCommandRunner).addLayerCommandToRedoList(layerCommand);
			}
		}
	}
}
