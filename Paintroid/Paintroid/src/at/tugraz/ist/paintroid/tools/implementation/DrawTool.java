package at.tugraz.ist.paintroid.tools.implementation;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.commandmanagement.Command;
import at.tugraz.ist.paintroid.commandmanagement.CommandHandler;
import at.tugraz.ist.paintroid.commandmanagement.implementation.PathCommand;
import at.tugraz.ist.paintroid.commandmanagement.implementation.PointCommand;

public class DrawTool extends BaseTool {
	private static int RESERVE_POINTS = 20;

	protected Path pathToDraw;
	protected PointF previousEventCoordinate;
	protected PointF initialEventCoordinate;
	protected PointF movedDistance = new PointF(0, 0);

	public DrawTool(CommandHandler commandHandler) {
		super(commandHandler);
		pathToDraw = new Path();
		pathToDraw.incReserve(RESERVE_POINTS);
	}

	@Override
	protected void setToolType() {
		this.toolType = toolType.BRUSH;
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawPath(pathToDraw, drawPaint);
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		if (coordinate == null) {
			return false;
		}
		initialEventCoordinate = new PointF(coordinate.x, coordinate.y);
		previousEventCoordinate = new PointF(coordinate.x, coordinate.y);
		pathToDraw.rewind();
		pathToDraw.moveTo(coordinate.x, coordinate.y);
		movedDistance.set(0, 0);
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
		pathToDraw.incReserve(1);
		movedDistance.set(movedDistance.x + Math.abs(coordinate.x - previousEventCoordinate.x),
				Math.abs(movedDistance.y - previousEventCoordinate.y));
		previousEventCoordinate.set(coordinate.x, coordinate.y);
		return true;
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		if (coordinate == null) {
			return false;
		}
		movedDistance.set(movedDistance.x + Math.abs(coordinate.x - previousEventCoordinate.x),
				Math.abs(movedDistance.y - previousEventCoordinate.y));
		if (PaintroidApplication.MOVE_TOLLERANCE < movedDistance.x
				|| PaintroidApplication.MOVE_TOLLERANCE < movedDistance.y) {
			return addPathCommand(coordinate);
		} else {
			return addPointCommand(initialEventCoordinate);
		}
	}

	protected boolean addPathCommand(PointF coordinate) {
		if (commandHandler == null) {
			Log.e(PaintroidApplication.TAG, "DrawTool null: " + commandHandler + " " + coordinate);
			return false;
		}
		pathToDraw.lineTo(coordinate.x, coordinate.y);
		Command command = new PathCommand(drawPaint, pathToDraw);
		commandHandler.commitCommand(command);
		return true;
	}

	protected boolean addPointCommand(PointF coordinate) {
		if (commandHandler == null) {
			Log.e(PaintroidApplication.TAG, "DrawTool null: " + commandHandler + " " + coordinate);
			return false;
		}
		Command command = new PointCommand(drawPaint, coordinate);
		commandHandler.commitCommand(command);
		return true;
	}
}
