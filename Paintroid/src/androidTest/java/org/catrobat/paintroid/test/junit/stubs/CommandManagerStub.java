/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.junit.stubs;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.command.LayerBitmapCommand;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.eventlistener.OnActiveLayerChangedListener;
import org.catrobat.paintroid.eventlistener.OnLayerEventListener;
import org.catrobat.paintroid.eventlistener.OnUpdateTopBarListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CommandManagerStub extends BaseStub implements CommandManager {

	@Override
	public void setUpdateTopBarListener(OnUpdateTopBarListener listener) {
	}

	@Override
	public void addChangeActiveLayerListener(OnActiveLayerChangedListener listener) {
	}

	@Override
	public void setLayerEventListener(OnLayerEventListener listener) {
	}

	@Override
	public void commitCommandToLayer(LayerCommand layerCommand, Command bitmapCommand) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<>();
		arguments.add(layerCommand);
		arguments.add(bitmapCommand);
		addCall(throwable, arguments);
		enableUndo(true);
	}

	@Override
	public void commitAddLayerCommand(LayerCommand layerCommand) {
	}

	@Override
	public void commitRemoveLayerCommand(LayerCommand layerCommand) {
	}

	@Override
	public void commitMergeLayerCommand(LayerCommand layerCommand) {
	}

	@Override
	public void resetAndClear(boolean clearLayerBitmapCommandsList) {
	}

	@Override
	public LayerBitmapCommand getLayerBitmapCommand(LayerCommand layerCommand) {
		return null;
	}

	@Override
	public boolean checkIfDrawn() {
		return false;
	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub
		enableRedo(true);
	}

	@Override
	public void redo() {
		// TODO Auto-generated method stub
		enableUndo(true);
	}

	@Override
	public ArrayList<LayerBitmapCommand> getLayerBitmapCommands(int layerId) {
		return null;
	}

	@Override
	public void enableUndo(boolean enable) {
	}

	@Override
	public void enableRedo(boolean enable) {
	}

	@Override
	public void setInitialized(boolean value) {
	}

	@Override
	public boolean isUndoCommandListEmpty() {

		return false;
	}

	@Override
	public boolean isRedoCommandListEmpty() {
		return false;
	}

	@Override
	public void processLayerUndo(LayerCommand command) {
	}

	@Override
	public void processLayerRedo(LayerCommand command) {
	}

	@Override
	public boolean isCommandManagerInitialized() {
		return false;
	}

	@Override
	public LinkedList<LayerCommand> getLayerOperationsCommandList() {
		return null;
	}

	@Override
	public LinkedList<LayerCommand> getLayerOperationsUndoCommandList() {
		return null;
	}

	@Override
	public ArrayList<LayerBitmapCommand> getDrawBitmapCommandsAtLayer() {
		return null;
	}

	@Override
	public void addLayerCommandToUndoList() {
	}

	@Override
	public void addLayerCommandToRedoList() {
	}

	@Override
	public void deleteLayerCommandFromDrawBitmapCommandsAtLayer(LayerCommand layerCommand) {
	}

	@Override
	public void addLayerCommandToDrawBitmapCommandsAtLayer(LayerCommand layerCommand) {
	}

	@Override
	public void deleteCommandFirstDeletedLayer() {
	}
}
