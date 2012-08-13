/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  Paintroid: An image manipulation application for Android, part of the
 *  Catroid project and Catroid suite of software.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.tools.implementation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import at.tugraz.ist.paintroid.PaintroidApplication;
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
	private boolean toolInDrawMode = false;
	private final int COLOR_TRESHOLD = 50;

	public CursorTool(Context context, ToolType toolType) {
		super(context, toolType);

		pathToDraw = new Path();
		pathToDraw.incReserve(1);
		linePaint = new Paint();
		linePaint.setStrokeWidth(5);

		previousEventCoordinate = new PointF(0f, 0f);
		movedDistance = new PointF(0f, 0f);

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

		if (toolInDrawMode) {
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

		if (toolInDrawMode) {
			if (PaintroidApplication.MOVE_TOLLERANCE < movedDistance.x
					|| PaintroidApplication.MOVE_TOLLERANCE < movedDistance.y) {
				addPathCommand(this.actualCursorPosition);
			} else {
				toolInDrawMode = false;
			}
		} else {
			if (PaintroidApplication.MOVE_TOLLERANCE >= movedDistance.x
					&& PaintroidApplication.MOVE_TOLLERANCE >= movedDistance.y) {
				toolInDrawMode = true;
				addPointCommand(actualCursorPosition);
			}
		}
		return true;
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
		float cursorPartLength = strokeWidth * 2;
		if (strokeWidth < 1f) {
			strokeWidth = 1f;
		} else if (strokeWidth > 2 * baseValue) {
			strokeWidth = 2 * baseValue;
		}
		float innerCircleRadius = brushStrokeWidth + (strokeWidth / 2f);
		float outerCircleRadius = innerCircleRadius + strokeWidth;

		linePaint.setColor(primaryShapeColor);
		linePaint.setStyle(Style.STROKE);
		linePaint.setStrokeWidth(strokeWidth);
		Cap strokeCap = bitmapPaint.getStrokeCap();

		if (isColorSimilar(bitmapPaint.getColor(), secondaryShapeColor)) {
			int colorToSwitch = primaryShapeColor;
			primaryShapeColor = secondaryShapeColor;
			secondaryShapeColor = colorToSwitch;
		}

		if (strokeCap.equals(strokeCap.ROUND)) {
			canvas.drawCircle(this.actualCursorPosition.x, this.actualCursorPosition.y, outerCircleRadius, linePaint);
			this.linePaint.setColor(secondaryShapeColor);
			canvas.drawCircle(this.actualCursorPosition.x, this.actualCursorPosition.y, innerCircleRadius, linePaint);
			if (toolInDrawMode) {
				linePaint.setColor(bitmapPaint.getColor());
				linePaint.setStyle(Style.FILL);
				canvas.drawCircle(actualCursorPosition.x, actualCursorPosition.y, innerCircleRadius
						- (strokeWidth / 2f), linePaint);
			}
		} else {
			RectF strokeRect = new RectF((this.actualCursorPosition.x - outerCircleRadius),
					(this.actualCursorPosition.y - outerCircleRadius),
					(this.actualCursorPosition.x + outerCircleRadius),
					(this.actualCursorPosition.y + outerCircleRadius));
			canvas.drawRect(strokeRect, linePaint);
			strokeRect.set((this.actualCursorPosition.x - innerCircleRadius),
					(this.actualCursorPosition.y - innerCircleRadius),
					(this.actualCursorPosition.x + innerCircleRadius),
					(this.actualCursorPosition.y + innerCircleRadius));
			linePaint.setColor(secondaryShapeColor);
			canvas.drawRect(strokeRect, linePaint);
			if (toolInDrawMode) {
				linePaint.setColor(bitmapPaint.getColor());
				linePaint.setStyle(Style.FILL);
				strokeRect.set((this.actualCursorPosition.x - innerCircleRadius + (strokeWidth / 2f)),
						(this.actualCursorPosition.y - innerCircleRadius + (strokeWidth / 2f)),
						(this.actualCursorPosition.x + innerCircleRadius - (strokeWidth / 2f)),
						(this.actualCursorPosition.y + innerCircleRadius - (strokeWidth / 2f)));
				canvas.drawRect(strokeRect, linePaint);
			}
		}

		// DRAW outer target lines
		this.linePaint.setStyle(Style.FILL);
		float startLineLengthAddition = (strokeWidth / 2f);
		float endLineLengthAddition = cursorPartLength + strokeWidth;
		for (int line_nr = 0; line_nr < CURSOR_LINES; line_nr++, startLineLengthAddition = (strokeWidth / 2f)
				+ cursorPartLength * line_nr, endLineLengthAddition = strokeWidth + cursorPartLength * (line_nr + 1f)) {
			if ((line_nr % 2) == 0) {
				this.linePaint.setColor(secondaryShapeColor);
			} else {
				this.linePaint.setColor(primaryShapeColor);
			}

			// LEFT
			canvas.drawLine(this.actualCursorPosition.x - outerCircleRadius - startLineLengthAddition,
					this.actualCursorPosition.y, this.actualCursorPosition.x - outerCircleRadius
							- endLineLengthAddition, this.actualCursorPosition.y, linePaint);
			// RIGHT
			canvas.drawLine(this.actualCursorPosition.x + outerCircleRadius + startLineLengthAddition,
					this.actualCursorPosition.y, this.actualCursorPosition.x + outerCircleRadius
							+ endLineLengthAddition, this.actualCursorPosition.y, linePaint);

			// BOTTOM
			canvas.drawLine(this.actualCursorPosition.x, this.actualCursorPosition.y + outerCircleRadius
					+ startLineLengthAddition, this.actualCursorPosition.x, this.actualCursorPosition.y
					+ outerCircleRadius + endLineLengthAddition, linePaint);

			// TOP
			canvas.drawLine(this.actualCursorPosition.x, this.actualCursorPosition.y - outerCircleRadius
					- startLineLengthAddition, this.actualCursorPosition.x, this.actualCursorPosition.y
					- outerCircleRadius - endLineLengthAddition, linePaint);
		}
	}

	private boolean isColorSimilar(int baseColor, int colorToCompare) {

		int[] baseColorValues = { Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor) };

		int[] colorValuesToCompare = { Color.red(colorToCompare), Color.green(colorToCompare),
				Color.blue(colorToCompare) };
		for (int index = 0; index < baseColorValues.length; index++) {
			if (isInTreshold(baseColorValues[index], colorValuesToCompare[index]) == false) {
				return false;
			}
		}
		return true;
	}

	private boolean isInTreshold(int baseValue, int valueToCompare) {
		if (Math.abs((baseValue - valueToCompare)) < COLOR_TRESHOLD) {
			return true;
		}

		return false;
	}

	@Override
	public void draw(Canvas canvas, boolean useCanvasTransparencyPaint) {
		if (toolInDrawMode) {
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
