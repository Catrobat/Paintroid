package at.tugraz.ist.paintroid.test.junit.stubs;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import at.tugraz.ist.paintroid.dialog.BrushPickerDialog;

public class BrushPickerStub extends BrushPickerDialog {

	protected BaseStub baseStub;

	public BrushPickerStub(Context context, OnBrushChangedListener listener) {
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

}
