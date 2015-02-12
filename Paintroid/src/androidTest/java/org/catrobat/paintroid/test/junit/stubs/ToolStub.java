/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.junit.stubs;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.TopBar.ToolButtonIDs;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Point;
import android.graphics.PointF;

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

	public void setReturnValue(String methodName, Object returnValue) {
		baseStub.setReturnValue(methodName, returnValue);
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
	public int getAttributeButtonResource(ToolButtonIDs buttonNumber) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(buttonNumber);
		baseStub.addCall(throwable, arguments);
		Integer returnValue = (Integer) baseStub.getReturnValue(throwable);
		if (returnValue == null)
			return 0;
		return returnValue.intValue();
	}

	@Override
	public int getAttributeButtonColor(ToolButtonIDs buttonNumber) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(buttonNumber);
		baseStub.addCall(throwable, arguments);
		Integer returnValue = (Integer) baseStub.getReturnValue(throwable);
		if (returnValue == null)
			return 0;
		return returnValue.intValue();
	}

	@Override
	public void resetInternalState(StateChange stateChange) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		baseStub.addCall(throwable, arguments);
	}

	@Override
	public void addObserver(Observer observer) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(observer);
		baseStub.addCall(throwable, arguments);
	}

	@Override
	public void attributeButtonClick(ToolButtonIDs buttonNumber) {
		Throwable throwable = new Throwable();
		List<Object> arguments = new ArrayList<Object>();
		arguments.add(buttonNumber);
		baseStub.addCall(throwable, arguments);
	}

	@Override
	public Point getAutoScrollDirection(float pointX, float pointY, int screenWidth, int screenHeight) {
		// TODO Auto-generated method stub
		return null;
	}

}
