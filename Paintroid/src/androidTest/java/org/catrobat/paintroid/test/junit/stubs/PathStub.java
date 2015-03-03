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

import android.graphics.Path;

public class PathStub extends Path {

	protected BaseStub baseStub;

	public PathStub() {
		super();
		baseStub = new BaseStub();
	}

	public int getCallCount(String methodName) {
		return baseStub.getCallCount(methodName);
	}

	public List<Object> getCall(String methodName, int count) {
		return baseStub.getCall(methodName, count);
	}

	@Override
	public void reset() {
		Throwable throwable = new Throwable();
		baseStub.addCall(throwable, new ArrayList<Object>());
	}

	@Override
	public void rewind() {
		Throwable throwable = new Throwable();
		baseStub.addCall(throwable, new ArrayList<Object>());
	}

	@Override
	public void moveTo(float x, float y) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(x);
		arguments.add(y);
		baseStub.addCall(throwable, arguments);
	}

	@Override
	public void quadTo(float x1, float y1, float x2, float y2) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(x1);
		arguments.add(y1);
		arguments.add(x2);
		arguments.add(y2);
		baseStub.addCall(throwable, arguments);
	}

	@Override
	public void lineTo(float x, float y) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(x);
		arguments.add(y);
		baseStub.addCall(throwable, arguments);
	}
}
