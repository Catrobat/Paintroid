/*
 *   This file is part of Paintroid, a software part of the Catroid project.
 *   Copyright (C) 2010  Catroid development team
 *   <http://code.google.com/p/catroid/wiki/Credits>
 *
 *   Paintroid is free software: you can redistribute it and/or modify it
 *   under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.tools.implementation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;
import android.view.Display;
import at.tugraz.ist.paintroid.MainActivity.ToolType;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.commandmanagement.Command;
import at.tugraz.ist.paintroid.commandmanagement.implementation.PathCommand;
import at.tugraz.ist.paintroid.commandmanagement.implementation.PointCommand;

public class CursorTool extends BaseToolWithShape {

	protected Path pathToDraw;
	protected PointF previousEventCoordinate = new PointF();
	protected PointF movedDistance = new PointF(0, 0);
	protected Paint linePaint;
	private ToolState state;
	private PointF actualCursorPosition = new PointF();
	private long timeLastHandleDown = 0;
	private int handleDownEvents = 0;
	private final int doubleClickPeriodMillis = 400;
	private final int CURSOR_LINES = 4;
	private int CURSOR_PART_LENGTH = 15;

	private enum ToolState {
		INACTIVE, ACTIVE, DRAW;
	}

	public CursorTool(Context context) {
		super(context);
		pathToDraw = new Path();
		pathToDraw.incReserve(1);
		linePaint = new Paint();
		linePaint.setStrokeWidth(Math.max((this.drawPaint.getStrokeWidth() / 2), 1));
		// TODO correct width and height of device (-ToolBarHeight/zoom/move) would be nice
		Display display = ((android.view.WindowManager) context.getSystemService(context.WINDOW_SERVICE))
				.getDefaultDisplay();
		float displayWidth = display.getWidth();
		float displayHeight = display.getHeight();
		float displayMinLength = Math.min(displayWidth, displayHeight);
		this.CURSOR_PART_LENGTH = (int) (displayMinLength / (this.CURSOR_LINES * 5));
		actualCursorPosition.set(displayWidth / 2, displayHeight / 2);
		this.state = ToolState.INACTIVE;
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		if (coordinate == null) {
			return false;
		}
		switch (this.state) {
			case INACTIVE:
				if (this.isDoubleClickEvent()) {
					pathToDraw.moveTo(this.actualCursorPosition.x, this.actualCursorPosition.y);
					movedDistance.set(0, 0);
					this.state = ToolState.ACTIVE;
				}
				previousEventCoordinate.set(coordinate.x, coordinate.y);
				break;
			case ACTIVE:
				previousEventCoordinate.set(coordinate);
				if (this.isDoubleClickEvent()) {
					this.state = ToolState.INACTIVE;
					movedDistance.set(0, 0);
					break;
				}

				this.state = ToolState.DRAW;
				pathToDraw.moveTo(this.actualCursorPosition.x, this.actualCursorPosition.y);
				movedDistance.set(0, 0);
				break;
			case DRAW:
				// 2nd Finger? ignore
				break;
		}
		this.timeLastHandleDown = System.currentTimeMillis();
		this.handleDownEvents++;
		return true;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		if (previousEventCoordinate == null || coordinate == null) {
			return false;
		}
		final float vectorCX = coordinate.x - previousEventCoordinate.x;
		final float vectorCY = coordinate.y - previousEventCoordinate.y;

		final float newCursorPositionX = this.actualCursorPosition.x + vectorCX;
		final float newCursorPositionY = this.actualCursorPosition.y + vectorCY;

		switch (this.state) {
			case INACTIVE:
				break;
			case ACTIVE:
			case DRAW:
				this.state = ToolState.DRAW;

				final float cx = (this.actualCursorPosition.x + newCursorPositionX) / 2;
				final float cy = (this.actualCursorPosition.y + newCursorPositionY) / 2;

				pathToDraw.quadTo(this.actualCursorPosition.x, this.actualCursorPosition.y, cx, cy);
				pathToDraw.incReserve(1);
				movedDistance.set(movedDistance.x + Math.abs(newCursorPositionX - this.actualCursorPosition.x),
						movedDistance.y + Math.abs(newCursorPositionY - this.actualCursorPosition.x));

				break;
		}
		previousEventCoordinate.set(coordinate.x, coordinate.y);
		actualCursorPosition.set(newCursorPositionX, newCursorPositionY);
		return true;
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		boolean returnValue = true;
		switch (this.state) {
			case INACTIVE:
				// 2nd finger ignore?
				break;
			case ACTIVE:
				// 2nd finger ignore?
				break;
			case DRAW:
				final float vectorCX = coordinate.x - previousEventCoordinate.x;
				final float vectorCY = coordinate.y - previousEventCoordinate.y;

				final float newCursorPositionX = this.actualCursorPosition.x + vectorCX;
				final float newCursorPositionY = this.actualCursorPosition.y + vectorCY;

				movedDistance.set(movedDistance.x + Math.abs(newCursorPositionX - this.actualCursorPosition.x),
						movedDistance.y + Math.abs(newCursorPositionY - this.actualCursorPosition.y));

				if (PaintroidApplication.MOVE_TOLLERANCE < movedDistance.x
						|| PaintroidApplication.MOVE_TOLLERANCE < movedDistance.y) {
					returnValue = addPathCommand(this.actualCursorPosition);
				} else {
					returnValue = addPointCommand(this.actualCursorPosition);
				}
				previousEventCoordinate.set(coordinate.x, coordinate.y);
				actualCursorPosition.set(newCursorPositionX, newCursorPositionY);
				this.state = ToolState.ACTIVE;
				break;
		}
		return returnValue;
	}

	@Override
	public void resetInternalState() {
		pathToDraw.rewind();
	}

	@Override
	public void drawShape(Canvas canvas) {
		float strokeWidth = Math.max((drawPaint.getStrokeWidth() / 2), 1);
		// float cursorPartLength = 15f;
		float radius = strokeWidth + 4f;
		this.linePaint.setStrokeWidth(strokeWidth);
		this.linePaint.setColor(primaryShapeColor);
		canvas.drawCircle(this.actualCursorPosition.x, this.actualCursorPosition.y, radius, linePaint);
		this.linePaint.setColor(secondaryShapeColor);
		canvas.drawCircle(this.actualCursorPosition.x, this.actualCursorPosition.y, radius - 4f, linePaint);

		for (int line_nr = 0; line_nr < CURSOR_LINES; line_nr++) {
			if ((line_nr % 2) == 0) {
				this.linePaint.setColor(secondaryShapeColor);
			} else {
				this.linePaint.setColor(primaryShapeColor);
			}

			canvas.drawLine(this.actualCursorPosition.x - strokeWidth - 4f - CURSOR_PART_LENGTH * line_nr,
					this.actualCursorPosition.y, this.actualCursorPosition.x - strokeWidth - 4f - CURSOR_PART_LENGTH
							* (line_nr + 1), this.actualCursorPosition.y, linePaint);
			canvas.drawLine(this.actualCursorPosition.x + strokeWidth + 4f + CURSOR_PART_LENGTH * line_nr,
					this.actualCursorPosition.y, this.actualCursorPosition.x + strokeWidth + 4f + CURSOR_PART_LENGTH
							* (line_nr + 1), this.actualCursorPosition.y, linePaint);
			canvas.drawLine(this.actualCursorPosition.x, this.actualCursorPosition.y + radius + CURSOR_PART_LENGTH
					* line_nr, this.actualCursorPosition.x, this.actualCursorPosition.y + radius + CURSOR_PART_LENGTH
					* (line_nr + 1), linePaint);
			canvas.drawLine(this.actualCursorPosition.x, this.actualCursorPosition.y - radius - CURSOR_PART_LENGTH
					* line_nr, this.actualCursorPosition.x, this.actualCursorPosition.y - radius - CURSOR_PART_LENGTH
					* (line_nr + 1), linePaint);
		}
	}

	@Override
	protected void setToolType() {
		this.toolType = ToolType.CURSOR;
	}

	@Override
	public void draw(Canvas canvas) {
		this.drawShape(canvas);
		if ((this.state == ToolState.ACTIVE) || (this.state == ToolState.DRAW)) {
			canvas.drawPath(pathToDraw, drawPaint);
		}
	}

	protected boolean addPathCommand(PointF coordinate) {
		pathToDraw.lineTo(coordinate.x, coordinate.y);
		Command command = new PathCommand(drawPaint, pathToDraw);
		return PaintroidApplication.COMMAND_HANDLER.commitCommand(command);
	}

	protected boolean addPointCommand(PointF coordinate) {
		Command command = new PointCommand(drawPaint, coordinate);
		return PaintroidApplication.COMMAND_HANDLER.commitCommand(command);
	}

	private boolean isDoubleClickEvent() {
		Log.d("PAINTROID", "TIME in millis: " + (System.currentTimeMillis() - this.timeLastHandleDown));
		if (this.handleDownEvents > 1
				&& (System.currentTimeMillis() - this.timeLastHandleDown) < this.doubleClickPeriodMillis) {
			Log.d("PAINTROID", this.handleDownEvents + "clicks");
			this.handleDownEvents = 0;
			return true;
		} else {
			return false;
		}
	}

}
