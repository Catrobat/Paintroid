package at.tugraz.ist.paintroid.tools.implementation;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import at.tugraz.ist.paintroid.commandmanagement.Command;
import at.tugraz.ist.paintroid.commandmanagement.implementation.PathCommand;
import at.tugraz.ist.paintroid.commandmanagement.implementation.PointCommand;

public class DrawTool extends BaseTool {

	protected Path pathToDraw;
	protected PointF previousEventCoordinate;

	public DrawTool(Paint paint) {
		super(paint);
		pathToDraw = new Path();
	}

	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean handleDown(PointF coordinate) {
		if (coordinate == null) {
			return false;
		}
		previousEventCoordinate = new PointF(coordinate.x, coordinate.y);
		pathToDraw.reset();
		pathToDraw.moveTo(coordinate.x, coordinate.y);
		return true;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		if (previousEventCoordinate == null || coordinate == null) {
			return false;
		}
		final float cx = (previousEventCoordinate.x + coordinate.x) / 2;
		final float cy = (previousEventCoordinate.y + coordinate.y) / 2;
		pathToDraw.quadTo(previousEventCoordinate.x, previousEventCoordinate.y, cx, cy);
		previousEventCoordinate.set(coordinate.x, coordinate.y);
		return true;
	}

	@Override
	public boolean handleTab(PointF coordinate) {
		if (commandHandler == null || coordinate == null) {
			return false;
		}
		Command command = new PointCommand(drawPaint, coordinate);
		commandHandler.commitCommand(command);
		return true;
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		if (commandHandler == null || coordinate == null) {
			return false;
		}
		pathToDraw.lineTo(coordinate.x, coordinate.y);
		Command command = new PathCommand(drawPaint, pathToDraw);
		commandHandler.commitCommand(command);
		return true;
	}

}
