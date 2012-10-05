/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.tools.implementation;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.PathCommand;
import org.catrobat.paintroid.command.implementation.PointCommand;
import org.catrobat.paintroid.ui.button.ToolbarButton.ToolButtonIDs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

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

			pathToDraw.quadTo(this.mToolPosition.x, this.mToolPosition.y, cx,
					cy);
			pathToDraw.incReserve(1);
		}

		mMovedDistance.set(
				mMovedDistance.x
						+ Math.abs(coordinate.x - mPreviousEventCoordinate.x),
				mMovedDistance.y
						+ Math.abs(coordinate.y - mPreviousEventCoordinate.y));

		mPreviousEventCoordinate.set(coordinate.x, coordinate.y);
		mToolPosition.set(newCursorPositionX, newCursorPositionY);
		return true;
	}

	@Override
	public boolean handleUp(PointF coordinate) {

		mMovedDistance.set(
				mMovedDistance.x
						+ Math.abs(coordinate.x - mPreviousEventCoordinate.x),
				mMovedDistance.y
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
	public void resetInternalState() {
		pathToDraw.rewind();
	}

	@Override
	public void drawShape(Canvas canvas) {
		int shapeStep = 0;
		Log.i(PaintroidApplication.TAG, "drawShape" + shapeStep++);
		float brushStrokeWidth = Math.max((mBitmapPaint.getStrokeWidth() / 2f),
				1f);
		Log.i(PaintroidApplication.TAG, "drawShape" + shapeStep++);
		float strokeWidth = getStrokeWidthForZoom(new Float(
				DEFAULT_TOOL_STROKE_WIDTH),
				new Float(MINIMAL_TOOL_STROKE_WIDTH), new Float(
						MAXIMAL_TOOL_STROKE_WIDTH));
		Log.i(PaintroidApplication.TAG, "drawShape" + shapeStep++);
		float cursorPartLength = strokeWidth * 2;

		float innerCircleRadius = brushStrokeWidth + (strokeWidth / 2f);
		float outerCircleRadius = innerCircleRadius + strokeWidth;
		// Log.i(PaintroidApplication.TAG, "drawShape" + shapeStep++);
		mLinePaint.setColor(mPrimaryShapeColor);
		// Log.i(PaintroidApplication.TAG, "drawShape" + shapeStep++);
		mLinePaint.setStyle(Style.STROKE);
		// Log.i(PaintroidApplication.TAG, "drawShape" + shapeStep++);
		mLinePaint.setStrokeWidth(strokeWidth);
		// Log.i(PaintroidApplication.TAG, "drawShape" + shapeStep++);
		Cap strokeCap = mBitmapPaint.getStrokeCap();
		// Log.i(PaintroidApplication.TAG, "drawShape" + shapeStep++);

		if (isColorSimilar(mBitmapPaint.getColor(), mSecondaryShapeColor)) {
			// Log.i(PaintroidApplication.TAG, "drawShape a" + shapeStep++);
			int colorToSwitch = mPrimaryShapeColor;
			// Log.i(PaintroidApplication.TAG, "drawShape a" + shapeStep++);
			mPrimaryShapeColor = mSecondaryShapeColor;
			// Log.i(PaintroidApplication.TAG, "drawShape a" + shapeStep++);
			mSecondaryShapeColor = colorToSwitch;
			// Log.i(PaintroidApplication.TAG, "drawShape a" + shapeStep++);
		}

		if (strokeCap.equals(Cap.ROUND)) {
			// Log.i(PaintroidApplication.TAG, "drawShape b" + shapeStep++);
			canvas.drawCircle(this.mToolPosition.x, this.mToolPosition.y,
					outerCircleRadius, mLinePaint);
			// Log.i(PaintroidApplication.TAG, "drawShape b" + shapeStep++);
			mLinePaint.setColor(mSecondaryShapeColor);
			// Log.i(PaintroidApplication.TAG, "drawShape b" + shapeStep++);
			canvas.drawCircle(this.mToolPosition.x, this.mToolPosition.y,
					innerCircleRadius, mLinePaint);
			// Log.i(PaintroidApplication.TAG, "drawShape b" + shapeStep++);
			if (toolInDrawMode) {
				// Log.i(PaintroidApplication.TAG, "drawShape c" +
				// shapeStep++);
				mLinePaint.setColor(mBitmapPaint.getColor());
				// Log.i(PaintroidApplication.TAG, "drawShape c" +
				// shapeStep++);
				mLinePaint.setStyle(Style.FILL);
				// Log.i(PaintroidApplication.TAG, "drawShape c" +
				// shapeStep++);
				canvas.drawCircle(mToolPosition.x, mToolPosition.y,
						innerCircleRadius - (strokeWidth / 2f), mLinePaint);
				// Log.i(PaintroidApplication.TAG, "drawShape c" +
				// shapeStep++);
			}
		} else {
			// Log.i(PaintroidApplication.TAG, "drawShape d" + shapeStep++);
			RectF strokeRect = new RectF(
					(this.mToolPosition.x - outerCircleRadius),
					(this.mToolPosition.y - outerCircleRadius),
					(this.mToolPosition.x + outerCircleRadius),
					(this.mToolPosition.y + outerCircleRadius));
			// Log.i(PaintroidApplication.TAG, "drawShape d" + shapeStep++);
			canvas.drawRect(strokeRect, mLinePaint);
			// Log.i(PaintroidApplication.TAG, "drawShape d" + shapeStep++);
			strokeRect.set((this.mToolPosition.x - innerCircleRadius),
					(this.mToolPosition.y - innerCircleRadius),
					(this.mToolPosition.x + innerCircleRadius),
					(this.mToolPosition.y + innerCircleRadius));
			// Log.i(PaintroidApplication.TAG, "drawShape d" + shapeStep++);
			mLinePaint.setColor(mSecondaryShapeColor);
			// Log.i(PaintroidApplication.TAG, "drawShape d" + shapeStep++);
			canvas.drawRect(strokeRect, mLinePaint);
			// Log.i(PaintroidApplication.TAG, "drawShape d" + shapeStep++);
			if (toolInDrawMode) {
				// Log.i(PaintroidApplication.TAG, "drawShape e" +
				// shapeStep++);
				mLinePaint.setColor(mBitmapPaint.getColor());
				// Log.i(PaintroidApplication.TAG, "drawShape e" +
				// shapeStep++);
				mLinePaint.setStyle(Style.FILL);
				// Log.i(PaintroidApplication.TAG, "drawShape e" +
				// shapeStep++);
				strokeRect
						.set((this.mToolPosition.x - innerCircleRadius + (strokeWidth / 2f)),
								(this.mToolPosition.y - innerCircleRadius + (strokeWidth / 2f)),
								(this.mToolPosition.x + innerCircleRadius - (strokeWidth / 2f)),
								(this.mToolPosition.y + innerCircleRadius - (strokeWidth / 2f)));
				canvas.drawRect(strokeRect, mLinePaint);
				// Log.i(PaintroidApplication.TAG, "drawShape e" +
				// shapeStep++);
			}
		}

		// Log.i(PaintroidApplication.TAG, "drawShape f" + shapeStep++);
		// DRAW outer target lines
		this.mLinePaint.setStyle(Style.FILL);
		// Log.i(PaintroidApplication.TAG, "drawShape f" + shapeStep++);
		float startLineLengthAddition = (strokeWidth / 2f);
		float endLineLengthAddition = cursorPartLength + strokeWidth;
		// Log.i(PaintroidApplication.TAG, "drawShape f" + shapeStep++);
		for (int line_nr = 0; line_nr < CURSOR_LINES; line_nr++, startLineLengthAddition = (strokeWidth / 2f)
				+ cursorPartLength * line_nr, endLineLengthAddition = strokeWidth
				+ cursorPartLength * (line_nr + 1f)) {
			// Log.i(PaintroidApplication.TAG, "drawShape f" + shapeStep++
			// + " line_nr" + line_nr);
			if ((line_nr % 2) == 0) {
				this.mLinePaint.setColor(mSecondaryShapeColor);
			} else {
				this.mLinePaint.setColor(mPrimaryShapeColor);
			}
			// Log.i(PaintroidApplication.TAG, "drawShape f" + shapeStep++
			// + " line_nr" + line_nr);

			// LEFT
			canvas.drawLine(this.mToolPosition.x - outerCircleRadius
					- startLineLengthAddition, this.mToolPosition.y,
					this.mToolPosition.x - outerCircleRadius
							- endLineLengthAddition, this.mToolPosition.y,
					mLinePaint);
			// Log.i(PaintroidApplication.TAG, "drawShape f" + shapeStep++
			// + " line_nr" + line_nr);
			// RIGHT
			canvas.drawLine(this.mToolPosition.x + outerCircleRadius
					+ startLineLengthAddition, this.mToolPosition.y,
					this.mToolPosition.x + outerCircleRadius
							+ endLineLengthAddition, this.mToolPosition.y,
					mLinePaint);
			// Log.i(PaintroidApplication.TAG, "drawShape f" + shapeStep++
			// + " line_nr" + line_nr);
			// BOTTOM
			canvas.drawLine(this.mToolPosition.x, this.mToolPosition.y
					+ outerCircleRadius + startLineLengthAddition,
					this.mToolPosition.x, this.mToolPosition.y
							+ outerCircleRadius + endLineLengthAddition,
					mLinePaint);
			// Log.i(PaintroidApplication.TAG, "drawShape f" + shapeStep++
			// + " line_nr" + line_nr);
			// TOP
			canvas.drawLine(this.mToolPosition.x, this.mToolPosition.y
					- outerCircleRadius - startLineLengthAddition,
					this.mToolPosition.x, this.mToolPosition.y
							- outerCircleRadius - endLineLengthAddition,
					mLinePaint);
			// Log.i(PaintroidApplication.TAG, "drawShape f" + shapeStep++
			// + " line_nr" + line_nr);
		}
		Log.i(PaintroidApplication.TAG, "drawShape END");
	}

	private boolean isColorSimilar(int baseColor, int colorToCompare) {

		int[] baseColorValues = { Color.red(baseColor), Color.green(baseColor),
				Color.blue(baseColor) };

		int[] colorValuesToCompare = { Color.red(colorToCompare),
				Color.green(colorToCompare), Color.blue(colorToCompare) };
		for (int index = 0; index < baseColorValues.length; index++) {
			if (isInTreshold(baseColorValues[index],
					colorValuesToCompare[index]) == false) {
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
		if (!mDoDraw) {
			return;
		}
		Log.i(PaintroidApplication.TAG, "Cursor draw");
		if (toolInDrawMode) {
			if (useCanvasTransparencyPaint) {
				Log.i(PaintroidApplication.TAG, "Cursor draw a");
				canvas.drawPath(pathToDraw, mCanvasPaint);
			} else {
				Log.i(PaintroidApplication.TAG, "Cursor draw b");
				canvas.drawPath(pathToDraw, mBitmapPaint);
			}
		}
		drawShape(canvas);
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

	@Override
	public void attributeButtonClick(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
		case BUTTON_ID_PARAMETER_BOTTOM_1:
		case BUTTON_ID_PARAMETER_TOP_1:
			showBrushPicker();
			break;
		case BUTTON_ID_PARAMETER_BOTTOM_2:
		case BUTTON_ID_PARAMETER_TOP_2:
			showColorPicker();
			break;
		default:
			break;
		}
	}

	@Override
	public int getAttributeButtonResource(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
		case BUTTON_ID_PARAMETER_TOP_1:
			return getStrokeWidthResource();
		case BUTTON_ID_PARAMETER_TOP_2:
			return getStrokeColorResource();
		case BUTTON_ID_PARAMETER_BOTTOM_1:
			return R.drawable.icon_menu_strokes;
		case BUTTON_ID_PARAMETER_BOTTOM_2:
			return R.drawable.icon_menu_color_palette;
		default:
			return super.getAttributeButtonResource(buttonNumber);
		}
	}
}
