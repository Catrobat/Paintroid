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

import android.os.AsyncTask;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.common.CommonFactory;
import org.catrobat.paintroid.contract.LayerContracts;

import java.util.ArrayList;
import java.util.List;

public class AsyncCommandManager implements CommandManager {
	private List<CommandListener> commandListeners = new ArrayList<>();
	private CommandManager commandManager = new DefaultCommandManager(new CommonFactory());
	private boolean running = true;

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
		return commandManager.isUndoAvailable();
	}

	@Override
	public boolean isRedoAvailable() {
		return commandManager.isRedoAvailable();
	}

	@Override
	public void addCommand(final Command command) {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected void onPreExecute() {
				notifyCommandPreExecute();
			}

			@Override
			protected Void doInBackground(Void... voids) {
				if (running) {
					final LayerContracts.Model model = PaintroidApplication.layerModel;
					synchronized (model) {
						commandManager.addCommand(command);
					}
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				notifyCommandPostExecute();
			}
		}.execute();
	}

	@Override
	public void undo() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected void onPreExecute() {
				notifyCommandPreExecute();
			}

			@Override
			protected Void doInBackground(Void... voids) {
				if (running) {
					final LayerContracts.Model model = PaintroidApplication.layerModel;
					synchronized (model) {
						commandManager.undo();
					}
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				notifyCommandPostExecute();
			}
		}.execute();
	}

	@Override
	public void redo() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				notifyCommandPreExecute();
			}

			@Override
			protected Void doInBackground(Void... voids) {
				if (running) {
					final LayerContracts.Model model = PaintroidApplication.layerModel;
					synchronized (model) {
						commandManager.redo();
					}
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				notifyCommandPostExecute();
			}
		}.execute();
	}

	@Override
	public void reset() {
		final LayerContracts.Model model = PaintroidApplication.layerModel;
		synchronized (model) {
			commandManager.reset();
		}
		notifyCommandPostExecute();
	}

	@Override
	public void shutdown() {
		running = false;
	}

	@Override
	public void setInitialStateCommand(Command command) {
		final LayerContracts.Model model = PaintroidApplication.layerModel;
		synchronized (model) {
			commandManager.setInitialStateCommand(command);
		}
	}

	private void notifyCommandPreExecute() {
		if (running) {
			for (CommandListener listener : commandListeners) {
				listener.commandPreExecute();
			}
		}
	}

	private void notifyCommandPostExecute() {
		if (running) {
			for (CommandListener listener : commandListeners) {
				listener.commandPostExecute();
			}
		}
	}
}
