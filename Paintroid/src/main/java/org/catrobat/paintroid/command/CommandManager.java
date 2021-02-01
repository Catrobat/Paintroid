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

package org.catrobat.paintroid.command;

public interface CommandManager {

	void addCommandListener(CommandListener commandListener);

	void removeCommandListener(CommandListener commandListener);

	boolean isUndoAvailable();

	boolean isRedoAvailable();

	void addCommand(Command command);

	void undo();

	void redo();

	void reset();

	void shutdown();

	void setInitialStateCommand(Command command);

	boolean isBusy();

	interface CommandListener {
		void commandPostExecute();
	}
}
