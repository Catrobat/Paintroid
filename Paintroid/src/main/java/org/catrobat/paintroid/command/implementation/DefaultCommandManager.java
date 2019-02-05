/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.command.implementation;

import android.graphics.Canvas;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.common.CommonFactory;
import org.catrobat.paintroid.contract.LayerContracts;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

public class DefaultCommandManager implements CommandManager {
	private List<CommandListener> commandListeners = new ArrayList<>();
	private Deque<Command> redoCommandList = new ArrayDeque<>();
	private Deque<Command> undoCommandList = new ArrayDeque<>();
	private Command initialStateCommand;

	private final CommonFactory commonFactory;
	private final LayerContracts.Model layerModel;

	public DefaultCommandManager(CommonFactory commonFactory, LayerContracts.Model layerModel) {
		this.commonFactory = commonFactory;
		this.layerModel = layerModel;
	}

	@Override
	public void addCommandListener(CommandListener commandListener) {
		commandListeners.add(commandListener);
	}

	@Override
	public void removeCommandListener(CommandListener commandListener) {
		commandListeners.remove(commandListener);
	}

	@Override
	public boolean isUndoAvailable() {
		return !undoCommandList.isEmpty();
	}

	@Override
	public boolean isRedoAvailable() {
		return !redoCommandList.isEmpty();
	}

	@Override
	public void addCommand(Command command) {
		redoCommandList.clear();
		undoCommandList.addFirst(command);

		LayerContracts.Layer currentLayer = layerModel.getCurrentLayer();
		Canvas canvas = commonFactory.createCanvas();
		canvas.setBitmap(currentLayer.getBitmap());
		command.run(canvas, layerModel);

		notifyCommandExecuted();
	}

	@Override
	public void undo() {
		Command command = undoCommandList.pop();
		redoCommandList.addFirst(command);

		layerModel.reset();

		Canvas canvas = commonFactory.createCanvas();

		if (initialStateCommand != null) {
			initialStateCommand.run(canvas, layerModel);
		}

		Iterator<Command> iterator = undoCommandList.descendingIterator();
		while (iterator.hasNext()) {
			LayerContracts.Layer currentLayer = layerModel.getCurrentLayer();
			canvas.setBitmap(currentLayer.getBitmap());
			iterator.next().run(canvas, layerModel);
		}

		notifyCommandExecuted();
	}

	@Override
	public void redo() {
		Command command = redoCommandList.pop();
		undoCommandList.addFirst(command);

		LayerContracts.Layer currentLayer = layerModel.getCurrentLayer();
		Canvas canvas = commonFactory.createCanvas();
		canvas.setBitmap(currentLayer.getBitmap());
		command.run(canvas, layerModel);

		notifyCommandExecuted();
	}

	@Override
	public void reset() {
		undoCommandList.clear();
		redoCommandList.clear();
		layerModel.reset();

		if (initialStateCommand != null) {
			Canvas canvas = commonFactory.createCanvas();
			initialStateCommand.run(canvas, layerModel);
		}

		notifyCommandExecuted();
	}

	@Override
	public void shutdown() {
	}

	@Override
	public void setInitialStateCommand(Command command) {
		initialStateCommand = command;
	}

	@Override
	public boolean isBusy() {
		return false;
	}

	private void notifyCommandExecuted() {
		for (CommandListener listener : commandListeners) {
			listener.commandPostExecute();
		}
	}
}
