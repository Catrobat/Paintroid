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
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.view.Display;
import android.view.WindowManager;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.command.Command;
import at.tugraz.ist.paintroid.command.implementation.PathCommand;
import at.tugraz.ist.paintroid.command.implementation.PointCommand;

public class CursorTool extends BaseToolWithShape {

	protected Path pathToDraw;
	protected PointF previousEventCoordinate = null;
	protected PointF movedDistance;
	protected Paint linePaint;
	private PointF actualCursorPosition;
	private final int CURSOR_LINES = 4;
	private final int CURSOR_PART_LENGTH;
	private boolean draw = false;

	public CursorTool(Context context, ToolType toolType) {
		super(context, toolType);

		pathToDraw = new Path();
		pathToDraw.incReserve(1);
		linePaint = new Paint();
		linePaint.setStrokeWidth(5);

		previousEventCoordinate = new PointF(0f, 0f);
		movedDistance = new PointF(0f, 0f);

		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		float displayWidth = display.getWidth();
		float displayHeight = display.getHeight();
		float displayMinLength = Math.min(displayWidth, displayHeight);
		this.CURSOR_PART_LENGTH = (int) ((displayMinLength / (this.CURSOR_LINES * 5 * PaintroidApplication.CURRENT_PERSPECTIVE
				.getScale())));
		actualCursorPosition = new PointF();
		actualCursorPosition.set(toolPosition);
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		pathToDraw.moveTo(this.actualCursorPosition.x, this.actualCursorPosition.y);
		previousEventCoordinate.set(coordinate);
		movedDistance.set(0, 0);
		return true;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		final float vectorCX = coordinate.x - previousEventCoordinate.x;
		final float vectorCY = coordinate.y - previousEventCoordinate.y;

		final float newCursorPositionX = this.actualCursorPosition.x + vectorCX;
		final float newCursorPositionY = this.actualCursorPosition.y + vectorCY;

		if (draw) {
			final float cx = (this.actualCursorPosition.x + newCursorPositionX) / 2f;
			final float cy = (this.actualCursorPosition.y + newCursorPositionY) / 2f;

			pathToDraw.quadTo(this.actualCursorPosition.x, this.actualCursorPosition.y, cx, cy);
			pathToDraw.incReserve(1);
		}

		movedDistance.set(movedDistance.x + Math.abs(coordinate.x - previousEventCoordinate.x),
				movedDistance.y + Math.abs(coordinate.y - previousEventCoordinate.y));

		previousEventCoordinate.set(coordinate.x, coordinate.y);
		actualCursorPosition.set(newCursorPositionX, newCursorPositionY);
		return true;
	}

	@Override
	public boolean handleUp(PointF coordinate) {

		movedDistance.set(movedDistance.x + Math.abs(coordinate.x - previousEventCoordinate.x),
				movedDistance.y + Math.abs(coordinate.y - previousEventCoordinate.y));

		if (draw) {
			if (PaintroidApplication.MOVE_TOLLERANCE < movedDistance.x
					|| PaintroidApplication.MOVE_TOLLERANCE < movedDistance.y) {
				addPathCommand(this.actualCursorPosition);
			} else {
				draw = false;
			}
		} else {
			if (PaintroidApplication.MOVE_TOLLERANCE >= movedDistance.x
					&& PaintroidApplication.MOVE_TOLLERANCE >= movedDistance.y) {
				draw = true;
				addPointCommand(actualCursorPosition);
			}
		}
		return true;
	}

	@Override
	public int getAttributeButtonResource(int buttonNumber) {
		if (buttonNumber == 0) {
			return R.drawable.ic_menu_more_cursor_64;
		}
		return super.getAttributeButtonResource(buttonNumber);
	}

	@Override
	public void resetInternalState() {
		pathToDraw.rewind();
	}

	@Override
	public void drawShape(Canvas canvas) {
		float brushStrokeWidth = Math.max((bitmapPaint.getStrokeWidth() / 2f), 1f);
		float displayScale = context.getResources().getDisplayMetrics().density;
		float baseValue = 5;

		float strokeWidth = (baseValue * displayScale) / PaintroidApplication.CURRENT_PERSPECTIVE.getScale();
		if (strokeWidth < 1f) {
			strokeWidth = 1f;
		} else if (strokeWidth > 2 * baseValue) {
			strokeWidth = 2 * baseValue;
		}
		// float upperStrokeWidthBorder =
		// float strokeWidthAddition = (4f / PaintroidApplication.CURRENT_PERSPECTIVE.getScale());
		float innerCircleRadius = brushStrokeWidth + (strokeWidth / 2f);
		float outerCircleRadius = innerCircleRadius + strokeWidth;

		// Log.d(PaintroidApplication.TAG, "Scale: " + PaintroidApplication.CURRENT_PERSPECTIVE.getScale());
		this.linePaint.setColor(primaryShapeColor);
		this.linePaint.setStyle(Style.STROKE);
		this.linePaint.setStrokeWidth(strokeWidth);
		canvas.drawCircle(this.actualCursorPosition.x, this.actualCursorPosition.y, outerCircleRadius, linePaint);
		this.linePaint.setColor(secondaryShapeColor);
		canvas.drawCircle(this.actualCursorPosition.x, this.actualCursorPosition.y, innerCircleRadius, linePaint);
		this.linePaint.setStyle(Style.FILL);

		for (int line_nr = 0; line_nr < CURSOR_LINES; line_nr++) {
			if ((line_nr % 2) == 0) {
				this.linePaint.setColor(secondaryShapeColor);
			} else {
				this.linePaint.setColor(primaryShapeColor);
			}

			/*
			 * canvas.drawLine(this.actualCursorPosition.x - baseStrokeWidth - strokeWidthAddition - CURSOR_PART_LENGTH
			 * line_nr, this.actualCursorPosition.y, this.actualCursorPosition.x - baseStrokeWidth - strokeWidthAddition
			 * - CURSOR_PART_LENGTH * (line_nr + 1), this.actualCursorPosition.y, linePaint);
			 * canvas.drawLine(this.actualCursorPosition.x + baseStrokeWidth + strokeWidthAddition + CURSOR_PART_LENGTH
			 * line_nr, this.actualCursorPosition.y, this.actualCursorPosition.x + baseStrokeWidth + strokeWidthAddition
			 * + CURSOR_PART_LENGTH * (line_nr + 1), this.actualCursorPosition.y, linePaint);
			 */
			canvas.drawLine(this.actualCursorPosition.x, this.actualCursorPosition.y + outerCircleRadius
					+ CURSOR_PART_LENGTH * line_nr, this.actualCursorPosition.x, this.actualCursorPosition.y
					+ outerCircleRadius + CURSOR_PART_LENGTH * (line_nr + 1), linePaint);
			canvas.drawLine(this.actualCursorPosition.x, this.actualCursorPosition.y - outerCircleRadius
					- CURSOR_PART_LENGTH * line_nr, this.actualCursorPosition.x, this.actualCursorPosition.y
					- outerCircleRadius - CURSOR_PART_LENGTH * (line_nr + 1), linePaint);
		}
	}

	@Override
	public void draw(Canvas canvas, boolean useCanvasTransparencyPaint) {
		if (draw) {
			if (useCanvasTransparencyPaint) {
				canvas.drawPath(pathToDraw, canvasPaint);
			} else {
				canvas.drawPath(pathToDraw, bitmapPaint);
			}
		}
		this.drawShape(canvas);
	}

	protected boolean addPathCommand(PointF coordinate) {
		pathToDraw.lineTo(coordinate.x, coordinate.y);
		Command command = new PathCommand(bitmapPaint, pathToDraw);
		return PaintroidApplication.COMMAND_MANAGER.commitCommand(command);
	}

	protected boolean addPointCommand(PointF coordinate) {
		Command command = new PointCommand(bitmapPaint, coordinate);
		return PaintroidApplication.COMMAND_MANAGER.commitCommand(command);
	}
}
