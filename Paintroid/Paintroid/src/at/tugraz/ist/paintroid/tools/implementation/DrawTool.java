package at.tugraz.ist.paintroid.tools.implementation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import at.tugraz.ist.paintroid.MainActivity.ToolType;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.commandmanagement.Command;
import at.tugraz.ist.paintroid.commandmanagement.implementation.PathCommand;
import at.tugraz.ist.paintroid.commandmanagement.implementation.PointCommand;

public class DrawTool extends BaseTool {
	protected Path pathToDraw;
	protected PointF previousEventCoordinate = new PointF();
	protected PointF initialEventCoordinate = new PointF();
	protected PointF movedDistance = new PointF(0, 0);

	public DrawTool(Context context) {
		super(context);
		pathToDraw = new Path();
		pathToDraw.incReserve(1);
	}

	@Override
	protected void setToolType() {
		this.toolType = ToolType.BRUSH;
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
		initialEventCoordinate.set(coordinate.x, coordinate.y);
		previousEventCoordinate.set(coordinate.x, coordinate.y);
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
		// movedDistance.set(movedDistance.x + Math.abs(coordinate.x - previousEventCoordinate.x),
		// Math.abs(movedDistance.y - previousEventCoordinate.y));
		movedDistance.set(movedDistance.x + Math.abs(coordinate.x - previousEventCoordinate.x),
				movedDistance.y + Math.abs(coordinate.y - previousEventCoordinate.y));
		previousEventCoordinate.set(coordinate.x, coordinate.y);
		return true;
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		if (coordinate == null) {
			return false;
		}
		// movedDistance.set(movedDistance.x + Math.abs(coordinate.x - previousEventCoordinate.x),
		// Math.abs(movedDistance.y - previousEventCoordinate.y));
		movedDistance.set(movedDistance.x + Math.abs(coordinate.x - previousEventCoordinate.x),
				movedDistance.y + Math.abs(coordinate.y - previousEventCoordinate.y));
		boolean returnValue;
		if (PaintroidApplication.MOVE_TOLLERANCE < movedDistance.x
				|| PaintroidApplication.MOVE_TOLLERANCE < movedDistance.y) {
			returnValue = addPathCommand(coordinate);
		} else {
			returnValue = addPointCommand(initialEventCoordinate);
		}
		return returnValue;
	}

	protected boolean addPathCommand(PointF coordinate) {
		// if (commandHandler == null) {
		// Log.e(PaintroidApplication.TAG, "DrawTool null: " + commandHandler + " " + coordinate);
		// return false;
		// }
		pathToDraw.lineTo(coordinate.x, coordinate.y);
		Command command = new PathCommand(drawPaint, pathToDraw);
		PaintroidApplication.COMMAND_HANDLER.commitCommand(command);
		return true;
	}

	protected boolean addPointCommand(PointF coordinate) {
		// if (commandHandler == null) {
		// Log.e(PaintroidApplication.TAG, "DrawTool null: " + commandHandler + " " + coordinate);
		// return false;
		// }
		Command command = new PointCommand(drawPaint, coordinate);
		PaintroidApplication.COMMAND_HANDLER.commitCommand(command);
		return true;
	}

	@Override
	public void onAppliedToBitmap() {
		pathToDraw.rewind();
	}
}
