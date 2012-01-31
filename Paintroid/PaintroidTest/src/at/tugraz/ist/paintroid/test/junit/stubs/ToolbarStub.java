package at.tugraz.ist.paintroid.test.junit.stubs;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import at.tugraz.ist.paintroid.tools.Tool;
import at.tugraz.ist.paintroid.ui.Toolbar;

public class ToolbarStub extends Observable implements Toolbar {

	protected BaseStub baseStub;

	public ToolbarStub() {
		super();
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

}
