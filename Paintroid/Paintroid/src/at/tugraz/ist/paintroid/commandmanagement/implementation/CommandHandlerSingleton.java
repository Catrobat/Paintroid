/*
 *   This file is part of Paintroid, a software part of the Catroid project.
 *   Copyright (C) 2010  Catroid development team
 *   <http://code.google.com/p/catroid/wiki/Credits>
 *
 *   Paintroid is free software: you can redistribute it and/or modify it
 *   under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.paintroid.commandmanagement.implementation;

import java.util.Vector;

import android.util.Log;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.commandmanagement.Command;
import at.tugraz.ist.paintroid.commandmanagement.CommandHandler;

public class CommandHandlerSingleton implements CommandHandler {
	// http://www.javaworld.com/javaworld/jw-04-2003/jw-0425-designpatterns.html?page=1
	// !TO AVOID A GARBAGE COLLECTOR CLEANUP OF THIS CLASS KEEP A REFERENCE IN THE MainActivity (and
	// only there)
	public static CommandHandlerSingleton COMMAND_HANDLER_SINGLETON_INSTANCE;
	private volatile Vector<Command> commandQueue;

	static {
		COMMAND_HANDLER_SINGLETON_INSTANCE = new CommandHandlerSingleton();
		// TODO instantiate UndoRedo Class here? & keep a reference
	}

	private CommandHandlerSingleton() {
		// to access this object use the public static COMMAND_HANDLER_SINGLETON_INSTANCE member
		// !NOTICE avoid references to this member - use the member directly
		// (commandHandlerSingeltonInstance.<method>)
		// otherwise you may block the garbage collector from cleaning up YOUR object.
		commandQueue = new Vector<Command>();
	}

	@Override
	public Command getNextCommand() {
		Log.d(PaintroidApplication.TAG, "CommandHandlerSingleton.getNextCommand");
		Command nextCommand = null;
		// TODO deliver the next command from the command queue
		if (commandQueue.isEmpty()) {
			return nextCommand;
		}

		synchronized (commandQueue) {
			nextCommand = commandQueue.firstElement();
			commandQueue.remove(0);
		}
		// TODO if undo/redo is realized via commands delete one bitmap from undo redo stack move it
		// to redo
		return nextCommand;
	}

	@Override
	public boolean commitCommand(Command commandObject) {
		Log.d(PaintroidApplication.TAG, "CommandHandlerSingleton.commitCommand");
		if (commandObject == null) {
			return false;
		}
		// TODO put a path on the undo/redo stack (if necessary) ? will be done when Command has
		// finished executing
		return this.commandQueue.add(commandObject);
	}

	@Override
	public synchronized void clearCommandHandlerQueue() {
		// TODO check if (where) undoRedo has to be reseted
		if (this.commandQueue != null) {
			this.commandQueue.clear();
		} else {
			this.commandQueue = new Vector<Command>();
		}
	}

}
