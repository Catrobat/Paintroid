package org.catrobat.paintroid.tools.implementation;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.tools.ToolType;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;

public class MoveZoomTool extends BaseTool {

	public MoveZoomTool(Context context, ToolType toolType) {
		super(context, toolType);
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		mPreviousEventCoordinate = coordinate;
		return true;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		PaintroidApplication.perspective.translate(coordinate.x
				- mPreviousEventCoordinate.x, coordinate.y
				- mPreviousEventCoordinate.y);
		return false;
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resetInternalState() {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub

	}

}
