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
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.command.Command;
import at.tugraz.ist.paintroid.command.implementation.PathCommand;
import at.tugraz.ist.paintroid.command.implementation.PointCommand;

public class CursorTool extends BaseToolWithShape {

	private static final float DEFAULT_TOOL_STROKE_WIDTH = 5f;
	private static final float MINIMAL_TOOL_STROKE_WIDTH = 1f;
	private static final float MAXIMAL_TOOL_STROKE_WIDTH = 10f;
	private static final int COLOR_TRESHOLD = 50;
	private static final int CURSOR_LINES = 4;

	protected Path pathToDraw;

	private boolean toolInDrawMode = false;

	public CursorTool(Context context, ToolType toolType) {
		super(context, toolType);

		pathToDraw = new Path();
		pathToDraw.incReserve(1);
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		pathToDraw.moveTo(this.mToolPosition.x, this.mToolPosition.y);
		mPreviousEventCoordinate.set(coordinate);
		mMovedDistance.set(0, 0);
		return true;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		final float vectorCX = coordinate.x - mPreviousEventCoordinate.x;
		final float vectorCY = coordinate.y - mPreviousEventCoordinate.y;

		final float newCursorPositionX = this.mToolPosition.x + vectorCX;
		final float newCursorPositionY = this.mToolPosition.y + vectorCY;

		if (toolInDrawMode) {
			final float cx = (this.mToolPosition.x + newCursorPositionX) / 2f;
			final float cy = (this.mToolPosition.y + newCursorPositionY) / 2f;

			pathToDraw.quadTo(this.mToolPosition.x, this.mToolPosition.y, cx, cy);
			pathToDraw.incReserve(1);
		}

		mMovedDistance.set(mMovedDistance.x + Math.abs(coordinate.x - mPreviousEventCoordinate.x), mMovedDistance.y
				+ Math.abs(coordinate.y - mPreviousEventCoordinate.y));

		mPreviousEventCoordinate.set(coordinate.x, coordinate.y);
		mToolPosition.set(newCursorPositionX, newCursorPositionY);
		return true;
	}

	@Override
	public boolean handleUp(PointF coordinate) {

		mMovedDistance.set(mMovedDistance.x + Math.abs(coordinate.x - mPreviousEventCoordinate.x), mMovedDistance.y
				+ Math.abs(coordinate.y - mPreviousEventCoordinate.y));

		if (toolInDrawMode) {
			if (PaintroidApplication.MOVE_TOLLERANCE < mMovedDistance.x
					|| PaintroidApplication.MOVE_TOLLERANCE < mMovedDistance.y) {
				addPathCommand(this.mToolPosition);
			} else {
				toolInDrawMode = false;
			}
		} else {
			if (PaintroidApplication.MOVE_TOLLERANCE >= mMovedDistance.x
					&& PaintroidApplication.MOVE_TOLLERANCE >= mMovedDistance.y) {
				toolInDrawMode = true;
				addPointCommand(mToolPosition);
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
		float brushStrokeWidth = Math.max((mBitmapPaint.getStrokeWidth() / 2f), 1f);

		float strokeWidth = getStrokeWidthForZoom(DEFAULT_TOOL_STROKE_WIDTH, MINIMAL_TOOL_STROKE_WIDTH,
				MAXIMAL_TOOL_STROKE_WIDTH);
		float cursorPartLength = strokeWidth * 2;

		float innerCircleRadius = brushStrokeWidth + (strokeWidth / 2f);
		float outerCircleRadius = innerCircleRadius + strokeWidth;

		mLinePaint.setColor(primaryShapeColor);
		mLinePaint.setStyle(Style.STROKE);
		mLinePaint.setStrokeWidth(strokeWidth);
		Cap strokeCap = mBitmapPaint.getStrokeCap();

		if (isColorSimilar(mBitmapPaint.getColor(), secondaryShapeColor)) {
			int colorToSwitch = primaryShapeColor;
			primaryShapeColor = secondaryShapeColor;
			secondaryShapeColor = colorToSwitch;
		}

		if (strokeCap.equals(Cap.ROUND)) {
			canvas.drawCircle(this.mToolPosition.x, this.mToolPosition.y, outerCircleRadius, mLinePaint);
			this.mLinePaint.setColor(secondaryShapeColor);
			canvas.drawCircle(this.mToolPosition.x, this.mToolPosition.y, innerCircleRadius, mLinePaint);
			if (toolInDrawMode) {
				mLinePaint.setColor(mBitmapPaint.getColor());
				mLinePaint.setStyle(Style.FILL);
				canvas.drawCircle(mToolPosition.x, mToolPosition.y, innerCircleRadius - (strokeWidth / 2f), mLinePaint);
			}
		} else {
			RectF strokeRect = new RectF((this.mToolPosition.x - outerCircleRadius),
					(this.mToolPosition.y - outerCircleRadius), (this.mToolPosition.x + outerCircleRadius),
					(this.mToolPosition.y + outerCircleRadius));
			canvas.drawRect(strokeRect, mLinePaint);
			strokeRect.set((this.mToolPosition.x - innerCircleRadius), (this.mToolPosition.y - innerCircleRadius),
					(this.mToolPosition.x + innerCircleRadius), (this.mToolPosition.y + innerCircleRadius));
			mLinePaint.setColor(secondaryShapeColor);
			canvas.drawRect(strokeRect, mLinePaint);
			if (toolInDrawMode) {
				mLinePaint.setColor(mBitmapPaint.getColor());
				mLinePaint.setStyle(Style.FILL);
				strokeRect.set((this.mToolPosition.x - innerCircleRadius + (strokeWidth / 2f)), (this.mToolPosition.y
						- innerCircleRadius + (strokeWidth / 2f)),
						(this.mToolPosition.x + innerCircleRadius - (strokeWidth / 2f)), (this.mToolPosition.y
								+ innerCircleRadius - (strokeWidth / 2f)));
				canvas.drawRect(strokeRect, mLinePaint);
			}
		}

		// DRAW outer target lines
		this.mLinePaint.setStyle(Style.FILL);
		float startLineLengthAddition = (strokeWidth / 2f);
		float endLineLengthAddition = cursorPartLength + strokeWidth;
		for (int line_nr = 0; line_nr < CURSOR_LINES; line_nr++, startLineLengthAddition = (strokeWidth / 2f)
				+ cursorPartLength * line_nr, endLineLengthAddition = strokeWidth + cursorPartLength * (line_nr + 1f)) {
			if ((line_nr % 2) == 0) {
				this.mLinePaint.setColor(secondaryShapeColor);
			} else {
				this.mLinePaint.setColor(primaryShapeColor);
			}

			// LEFT
			canvas.drawLine(this.mToolPosition.x - outerCircleRadius - startLineLengthAddition, this.mToolPosition.y,
					this.mToolPosition.x - outerCircleRadius - endLineLengthAddition, this.mToolPosition.y, mLinePaint);
			// RIGHT
			canvas.drawLine(this.mToolPosition.x + outerCircleRadius + startLineLengthAddition, this.mToolPosition.y,
					this.mToolPosition.x + outerCircleRadius + endLineLengthAddition, this.mToolPosition.y, mLinePaint);

			// BOTTOM
			canvas.drawLine(this.mToolPosition.x, this.mToolPosition.y + outerCircleRadius + startLineLengthAddition,
					this.mToolPosition.x, this.mToolPosition.y + outerCircleRadius + endLineLengthAddition, mLinePaint);

			// TOP
			canvas.drawLine(this.mToolPosition.x, this.mToolPosition.y - outerCircleRadius - startLineLengthAddition,
					this.mToolPosition.x, this.mToolPosition.y - outerCircleRadius - endLineLengthAddition, mLinePaint);
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
				canvas.drawPath(pathToDraw, mCanvasPaint);
			} else {
				canvas.drawPath(pathToDraw, mBitmapPaint);
			}
		}
		this.drawShape(canvas);
	}

	protected boolean addPathCommand(PointF coordinate) {
		pathToDraw.lineTo(coordinate.x, coordinate.y);
		Command command = new PathCommand(mBitmapPaint, pathToDraw);
		return PaintroidApplication.COMMAND_MANAGER.commitCommand(command);
	}

	protected boolean addPointCommand(PointF coordinate) {
		Command command = new PointCommand(mBitmapPaint, coordinate);
		return PaintroidApplication.COMMAND_MANAGER.commitCommand(command);
	}
}
