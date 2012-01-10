package at.tugraz.ist.paintroid.tools.implementation;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.commandmanagement.Command;
import at.tugraz.ist.paintroid.commandmanagement.implementation.PathCommand;
import at.tugraz.ist.paintroid.commandmanagement.implementation.PointCommand;
import at.tugraz.ist.paintroid.tools.Tool;

public class DrawTool extends BaseTool {
	private static int RESERVE_POINTS = 20;

	protected Path pathToDraw;
	protected PointF previousEventCoordinate;
	protected boolean wasMoved;

	public DrawTool() {
		pathToDraw = new Path();
		pathToDraw.incReserve(RESERVE_POINTS);
		drawPaint.setColor(Color.BLACK);
		drawPaint.setAntiAlias(true);
		drawPaint.setDither(true);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
		drawPaint.setStrokeWidth(Tool.stroke25);
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
		previousEventCoordinate = new PointF(coordinate.x, coordinate.y);
		pathToDraw.rewind();
		pathToDraw.moveTo(coordinate.x, coordinate.y);
		wasMoved = false;
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
		pathToDraw.incReserve(1);
		wasMoved = true;
		return true;
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		Log.d(PaintroidApplication.TAG, "DrawTool.handleUp");
		if (wasMoved) {
			return pathCommand(coordinate);
		} else {
			return pointCommand(coordinate);
		}
	}

	private boolean pathCommand(PointF coordinate) {
		if (commandHandler == null || coordinate == null) {
			Log.e(PaintroidApplication.TAG, "DrawTool null: " + commandHandler + " " + coordinate);
			return false;
		}
		pathToDraw.lineTo(coordinate.x, coordinate.y);
		Command command = new PathCommand(drawPaint, pathToDraw);
		commandHandler.commitCommand(command);
		return true;
	}

	private boolean pointCommand(PointF coordinate) {
		if (commandHandler == null || coordinate == null) {
			return false;
		}
		Command command = new PointCommand(drawPaint, coordinate);
		commandHandler.commitCommand(command);
		return true;
	}
}
