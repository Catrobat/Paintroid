/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2013 The Catrobat Team
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseStub {

	protected Map<String, List<List<Object>>> calls;
	protected Map<String, Object> returnValues;

	public BaseStub() {
		calls = new HashMap<String, List<List<Object>>>();
		returnValues = new HashMap<String, Object>();
	}

	public int getCallCount(String methodName) {
		if (!calls.containsKey(methodName)) {
			return 0;
		}
		return calls.get(methodName).size();
	}

	public List<Object> getCall(String methodName, int count) {
		if (!calls.containsKey(methodName)) {
			return null;
		}
		List<List<Object>> call = calls.get(methodName);
		return call.get(count);
	}

	public void setReturnValue(String methodName, Object returnValue) {
		returnValues.put(methodName, returnValue);
	}

	protected void addCall(Throwable throwable, List<Object> arguments) {
		StackTraceElement[] elements = throwable.getStackTrace();
		String methodName = elements[0].getMethodName();
		if (!calls.containsKey(methodName)) {
			List<List<Object>> newCall = new ArrayList<List<Object>>();
			newCall.add(arguments);
			calls.put(methodName, newCall);
		} else {
			List<List<Object>> call = calls.get(methodName);
			call.add(arguments);
		}
	}

	protected Object getReturnValue(Throwable throwable) {
		StackTraceElement[] elements = throwable.getStackTrace();
		String methodName = elements[0].getMethodName();
		if (returnValues.containsKey(methodName)) {
			Object returnValue = returnValues.get(methodName);
			return returnValue;
		}
		return null;
	}
}
