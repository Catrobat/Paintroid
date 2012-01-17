package at.tugraz.ist.paintroid.test.junit.stubs;

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
