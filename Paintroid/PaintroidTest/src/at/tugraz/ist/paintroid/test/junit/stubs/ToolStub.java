package at.tugraz.ist.paintroid.test.junit.stubs;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.PointF;
import at.tugraz.ist.paintroid.MainActivity.ToolType;
import at.tugraz.ist.paintroid.tools.Tool;

public class ToolStub extends Observable implements Tool {

	protected BaseStub baseStub;

	public ToolStub() {
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
	public boolean handleDown(PointF coordinate) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(coordinate);
		baseStub.addCall(throwable, arguments);
		Boolean returnValue = (Boolean) baseStub.getReturnValue(throwable);
		if (returnValue == null)
			return true;
		return returnValue.booleanValue();
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(coordinate);
		baseStub.addCall(throwable, arguments);
		Boolean returnValue = (Boolean) baseStub.getReturnValue(throwable);
		if (returnValue == null)
			return true;
		return returnValue.booleanValue();
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(coordinate);
		baseStub.addCall(throwable, arguments);
		Boolean returnValue = (Boolean) baseStub.getReturnValue(throwable);
		if (returnValue == null)
			return true;
		return returnValue.booleanValue();
	}

	@Override
	public void changePaintColor(int color) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(new Integer(color));
		baseStub.addCall(throwable, arguments);
	}

	@Override
	public void changePaintStrokeWidth(int strokeWidth) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(new Integer(strokeWidth));
		baseStub.addCall(throwable, arguments);
	}

	@Override
	public void changePaintStrokeCap(Cap cap) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(cap);
		baseStub.addCall(throwable, arguments);
	}

	@Override
	public void setDrawPaint(Paint paint) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(paint);
		baseStub.addCall(throwable, arguments);
	}

	@Override
	public Paint getDrawPaint() {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		baseStub.addCall(throwable, arguments);
		return (Paint) baseStub.getReturnValue(throwable);
	}

	@Override
	public void draw(Canvas canvas) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(canvas);
		baseStub.addCall(throwable, arguments);
	}

	@Override
	public ToolType getToolType() {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		baseStub.addCall(throwable, arguments);
		return (ToolType) baseStub.getReturnValue(throwable);
	}

	@Override
	public int getAttributeButtonResource() {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		baseStub.addCall(throwable, arguments);
		Integer returnValue = (Integer) baseStub.getReturnValue(throwable);
		if (returnValue == null)
			return 0;
		return returnValue.intValue();
	}

	@Override
	public int getAttributeButtonColor() {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		baseStub.addCall(throwable, arguments);
		Integer returnValue = (Integer) baseStub.getReturnValue(throwable);
		if (returnValue == null)
			return 0;
		return returnValue.intValue();
	}

}
