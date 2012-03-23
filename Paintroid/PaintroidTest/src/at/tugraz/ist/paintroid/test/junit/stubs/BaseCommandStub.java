package at.tugraz.ist.paintroid.test.junit.stubs;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import at.tugraz.ist.paintroid.command.implementation.BaseCommand;

public class BaseCommandStub extends BaseCommand {

	protected BaseStub mBaseStub;

	public BaseCommandStub() {
		super();
		mBaseStub = new BaseStub();
	}

	public BaseCommandStub(Paint paint) {
		super(paint);
		mBaseStub = new BaseStub();
	}

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(canvas);
		arguments.add(bitmap);
		mBaseStub.addCall(throwable, arguments);
	}

	public int getCallCount(String methodName) {
		return mBaseStub.getCallCount(methodName);
	}

	public List<Object> getCall(String methodName, int count) {
		return mBaseStub.getCall(methodName, count);
	}

	public void storeBitmapStub() {
		storeBitmap();
	}
}
