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

package at.tugraz.ist.paintroid.test.junit.stubs;

import java.util.ArrayList;
import java.util.List;

import at.tugraz.ist.paintroid.command.Command;
import at.tugraz.ist.paintroid.command.CommandHandler;

public class CommandHandlerStub extends BaseStub implements CommandHandler {

	@Override
	public boolean commitCommand(Command commandObject) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(commandObject);
		addCall(throwable, arguments);
		Boolean returnValue = (Boolean) getReturnValue(throwable);
		if (returnValue == null)
			return true;
		return returnValue.booleanValue();
	}

	@Override
	public Command getNextCommand() {
		Throwable throwable = new Throwable();
		addCall(throwable, new ArrayList<Object>());
		return null;
	}

	@Override
	public void clearCommandQueue() {

	}

}
