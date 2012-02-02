package at.tugraz.ist.paintroid.test.junit.stubs;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import at.tugraz.ist.paintroid.dialog.colorpicker.ColorPickerDialog;

public class ColorPickerStub extends ColorPickerDialog {

	protected BaseStub baseStub;

	public ColorPickerStub(Context context, OnColorPickedListener listener) {
		super(context, listener);
		baseStub = new BaseStub();
	}

	public int getCallCount(String methodName) {
		return baseStub.getCallCount(methodName);
	}

	public List<Object> getCall(String methodName, int count) {
		return baseStub.getCall(methodName, count);
	}

	@Override
	public void show() {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		baseStub.addCall(throwable, arguments);
	}

	@Override
	public void setInitialColor(int color) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(color);
		baseStub.addCall(throwable, arguments);
	}

}
