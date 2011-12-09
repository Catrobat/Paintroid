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

import at.tugraz.ist.paintroid.commandmanagement.CommandHandler;

public class CommandHandlerSingleton implements CommandHandler /** TODO, UndoRedoInterface **/
{
	// http://www.javaworld.com/javaworld/jw-04-2003/jw-0425-designpatterns.html?page=1
	// !TO AVOID A GARBAGE COLLECTOR CLEANUP OF THIS CLASS KEEP A REFERENCE IN THE MainActivity (and
	// only there)
	public static CommandHandlerSingleton COMMAND_HANDLER_SINGLETON_INSTANCE;
	private volatile Vector<Runnable> commandQueue;

	static {
		COMMAND_HANDLER_SINGLETON_INSTANCE = new CommandHandlerSingleton();
		// TODO monitor/synchronized/Vector object is
		// synchronized? for commandQueue ???
		// TODO instantiate UndoRedo Class here? & keep a reference
	}

	private CommandHandlerSingleton() {
		// to access this object use the public static COMMAND_HANDLER_SINGLETON_INSTANCE member
		// !NOTICE avoid references to this member - use the member directly
		// (commandHandlerSingeltonInstance.<method>)
		// otherwise you may block the garbage collector from cleaning up YOUR object.
		commandQueue = new Vector<Runnable>();
	}

	@Override
	public Runnable getNextCommand() {
		Runnable nextCommand = null;
		// TODO deliver the next command from the command queue
		if (commandQueue.isEmpty()) {
			return nextCommand;
		}

		synchronized (commandQueue) {
			nextCommand = commandQueue.firstElement();
			commandQueue.remove(0);
		}
		// TODO check if the command should be deleted on time (from the command queue-(better)) or
		// later when the runeneable object has finished
		// TODO check if the undo stack should be filled at that point (or already when
		// commitCommand is called)
		// TODO if undo/redo is realized via commands delete one bitmap from undo redo stack move it
		// to redo
		return nextCommand;
	}

	@Override
	public boolean commitCommand(Runnable commandObject) {
		// TODO put a command in the command queue
		// TODO put a path on the undo/redo stack (if necessary) OR put it on the undo redo stack in
		// getNextCommand() (think would be better)
		// TODO eventually check from which interface this command came from to do some pre/post
		// operations
		return this.commandQueue.add(commandObject);
	}

}
