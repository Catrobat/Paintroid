package at.tugraz.ist.paintroid.tools.implementation;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

public class DrawTool extends BaseTool {

	public DrawTool(int color, Paint paint) {
		super(color, paint);
	}

	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean handleDown(Point coordinate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleMove(Point deltaMove) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleTab(Point coordinate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleUp(Point coordinate) {
		// TODO Auto-generated method stub
		return false;
	}

}
