package at.tugraz.ist.paintroid.test.junit.stubs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Path;

public class PathStub extends Path {

	protected Map<String, List<List<Object>>> calls;

	public PathStub() {
		super();
		calls = new HashMap<String, List<List<Object>>>();
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

	@Override
	public void reset() {
		Throwable throwable = new Throwable();
		addCall(throwable, new ArrayList<Object>());
	}

	@Override
	public void moveTo(float x, float y) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(x);
		arguments.add(y);
		addCall(throwable, arguments);
	}

	@Override
	public void quadTo(float x1, float y1, float x2, float y2) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(x1);
		arguments.add(y1);
		arguments.add(x2);
		arguments.add(y2);
		addCall(throwable, arguments);
	}

	@Override
	public void lineTo(float x, float y) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(x);
		arguments.add(y);
		addCall(throwable, arguments);
	}
}
