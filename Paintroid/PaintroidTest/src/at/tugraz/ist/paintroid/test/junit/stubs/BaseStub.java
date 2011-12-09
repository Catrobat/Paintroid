package at.tugraz.ist.paintroid.test.junit.stubs;

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

	public void setReturnValue(String methodName, Boolean returnValue) {
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

	protected boolean getBooleanReturnValue(Throwable throwable) {
		StackTraceElement[] elements = throwable.getStackTrace();
		String methodName = elements[0].getMethodName();
		if (returnValues.containsKey(methodName)) {
			Object returnValue = returnValues.get(methodName);
			if (returnValue instanceof Boolean) {
				return ((Boolean) returnValue).booleanValue();
			}
		}
		return false;
	}
}
