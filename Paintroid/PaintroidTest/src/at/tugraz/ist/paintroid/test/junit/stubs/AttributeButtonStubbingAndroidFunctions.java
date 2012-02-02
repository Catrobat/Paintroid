package at.tugraz.ist.paintroid.test.junit.stubs;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import at.tugraz.ist.paintroid.ui.button.AttributeButton;

public class AttributeButtonStubbingAndroidFunctions extends AttributeButton {

	protected BaseStub baseStub;

	public AttributeButtonStubbingAndroidFunctions(Context context) {
		super(context);
		baseStub = new BaseStub();
	}

	public int getCallCount(String methodName) {
		return baseStub.getCallCount(methodName);
	}

	public List<Object> getCall(String methodName, int count) {
		return baseStub.getCall(methodName, count);
	}

	@Override
	public void setBackgroundColor(int color) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(color);
		baseStub.addCall(throwable, arguments);
	}

	@Override
	public void setBackgroundResource(int resource) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(resource);
		baseStub.addCall(throwable, arguments);
	}

}
