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

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.contract.LayerContracts;

import java.util.ArrayList;
import java.util.List;

public class AsyncCommandManager implements CommandManager {
	private List<CommandListener> commandListeners = new ArrayList<>();
	private CommandManager commandManager;
	private final LayerContracts.Model layerModel;
	private boolean shuttingDown;
	private boolean busy;

	public AsyncCommandManager(CommandManager commandManager, LayerContracts.Model layerModel) {
		this.commandManager = commandManager;
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
		return commandManager.isUndoAvailable();
	}

	@Override
	public boolean isRedoAvailable() {
		return commandManager.isRedoAvailable();
	}

	@Override
	public void addCommand(final Command command) {
		if (busy) {
			return;
		}

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected void onPreExecute() {
				notifyCommandPreExecute();
			}

			@Override
			protected Void doInBackground(Void... voids) {
				if (!shuttingDown) {
					synchronized (layerModel) {
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
		if (busy) {
			return;
		}

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected void onPreExecute() {
				busy = true;
			}

			@Override
			protected Void doInBackground(Void... voids) {
				if (!shuttingDown) {
					synchronized (layerModel) {
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
		if (busy) {
			return;
		}

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				busy = true;
			}

			@Override
			protected Void doInBackground(Void... voids) {
				if (!shuttingDown) {
					synchronized (layerModel) {
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
		synchronized (layerModel) {
			commandManager.reset();
		}
		notifyCommandPostExecute();
	}

	@Override
	public void shutdown() {
		shuttingDown = true;
	}

	@Override
	public void setInitialStateCommand(Command command) {
		synchronized (layerModel) {
			commandManager.setInitialStateCommand(command);
		}
	}

	@Override
	public boolean isBusy() {
		return busy;
	}

	private void notifyCommandPreExecute() {
		busy = true;
	}

	private void notifyCommandPostExecute() {
		busy = false;
		if (!shuttingDown) {
			for (CommandListener listener : commandListeners) {
				listener.commandPostExecute();
			}
		}
	}
}
