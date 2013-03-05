/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
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
import java.util.Observer;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.ui.Statusbar;

public class StatusbarStub extends Statusbar {

	protected BaseStub baseStub;

	public StatusbarStub(MainActivity mainActivty, boolean openFromCatroid) {
		super(mainActivty, openFromCatroid);
		baseStub = new BaseStub();
	}

	public int getCallCount(String methodName) {
		return baseStub.getCallCount(methodName);
	}

	public List<Object> getCall(String methodName, int count) {
		return baseStub.getCall(methodName, count);
	}

	public void setReturnValue(String methodName, Object returnValue) {
		baseStub.setReturnValue(methodName, returnValue);
	}

	@Override
	public Tool getCurrentTool() {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		baseStub.addCall(throwable, arguments);
		return (Tool) baseStub.getReturnValue(throwable);
	}

	@Override
	public void setTool(Tool tool) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(tool);
		baseStub.addCall(throwable, arguments);
	}

	@Override
	public void addObserver(Observer observer) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(observer);
		baseStub.addCall(throwable, arguments);
	}

}
