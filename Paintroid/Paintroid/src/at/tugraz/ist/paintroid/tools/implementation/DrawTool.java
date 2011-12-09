package at.tugraz.ist.paintroid.tools.implementation;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import at.tugraz.ist.paintroid.commandmanagement.Command;
import at.tugraz.ist.paintroid.commandmanagement.implementation.PointCommand;

public class DrawTool extends BaseTool {

	public DrawTool(Paint paint) {
		super(paint);
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
		if (commandHandler == null || coordinate == null) {
			return false;
		}
		Command command = new PointCommand(drawPaint, coordinate);
		commandHandler.commitCommand(command);
		return true;
	}

	@Override
	public boolean handleUp(Point coordinate) {
		// TODO Auto-generated method stub
		return false;
	}

}
