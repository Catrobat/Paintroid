package at.tugraz.ist.paintroid.test.junit.stubs;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import at.tugraz.ist.paintroid.command.implementation.BaseCommand;

public class BaseCommandStub extends BaseCommand {

	protected BaseStub baseStub;

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(canvas);
		arguments.add(bitmap);
		baseStub.addCall(throwable, arguments);
	}

	public int getCallCount(String methodName) {
		return baseStub.getCallCount(methodName);
	}

	public List<Object> getCall(String methodName, int count) {
		return baseStub.getCall(methodName, count);
	}

	public void storeBitmapStub() {
		storeBitmap();
	}
}
