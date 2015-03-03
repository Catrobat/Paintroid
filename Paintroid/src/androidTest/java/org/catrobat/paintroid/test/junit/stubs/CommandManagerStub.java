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

package org.catrobat.paintroid.test.junit.stubs;

import java.util.ArrayList;
import java.util.List;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandManager;

import android.graphics.Bitmap;

public class CommandManagerStub extends BaseStub implements CommandManager {

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
	public void resetAndClear() {

	}

	@Override
	public void setOriginalBitmap(Bitmap bitmap) {
		// TODO Auto-generated method stub

	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub

	}

	@Override
	public void redo() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasCommands() {
		return true;
	}

	@Override
	public int getNumberOfCommands() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasNextCommand() {
		// TODO Auto-generated method stub
		return false;
	}

}
