/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
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
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.TopBar.ToolButtonIDs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

public class CursorTool extends BaseToolWithShape {

	private static final float DEFAULT_TOOL_STROKE_WIDTH = 5f;
	private static final float MINIMAL_TOOL_STROKE_WIDTH = 1f;
	private static final float MAXIMAL_TOOL_STROKE_WIDTH = 10f;
	private static final int CURSOR_LINES = 4;

	protected Path pathToDraw;
	private int mPrimaryShapeColor;
	private int mSecondaryShapeColor;

	private boolean toolInDrawMode = false;

	public CursorTool(Context context, ToolType toolType) {
		super(context, toolType);

		pathToDraw = new Path();
		pathToDraw.incReserve(1);
		mPrimaryShapeColor = PaintroidApplication.applicationContext
				.getResources().getColor(
						R.color.cursor_tool_deactive_primary_color);
		mSecondaryShapeColor = Color.LTGRAY;
	}

	@Override
	public void changePaintColor(int color) {
		super.changePaintColor(color);
		if (toolInDrawMode) {
			mSecondaryShapeColor = mBitmapPaint.getColor();
		}
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

		float newCursorPositionX = this.mToolPosition.x + vectorCX;
		float newCursorPositionY = this.mToolPosition.y + vectorCY;

		PointF cursorSurfacePosition = PaintroidApplication.perspective
				.getSurfacePointFromCanvasPoint(new PointF(newCursorPositionX,
						newCursorPositionY));

		float surfaceWidth = PaintroidApplication.drawingSurface.getWidth();
		float surfaceHeight = PaintroidApplication.drawingSurface.getHeight();

		boolean slowCursor = false;
		if (cursorSurfacePosition.x > surfaceWidth) {
			cursorSurfacePosition.x = surfaceWidth;
			slowCursor = true;
		} else if (cursorSurfacePosition.x < 0) {
			cursorSurfacePosition.x = 0;
			slowCursor = true;
		}
		if (cursorSurfacePosition.y > surfaceHeight) {
			cursorSurfacePosition.y = surfaceHeight;
			slowCursor = true;
		} else if (cursorSurfacePosition.y < 0) {
			cursorSurfacePosition.y = 0;
			slowCursor = true;
		}

		if (slowCursor) {
			PointF cursorCanvasPosition = PaintroidApplication.perspective
					.getCanvasPointFromSurfacePoint(cursorSurfacePosition);
			newCursorPositionX = cursorCanvasPosition.x;
			newCursorPositionY = cursorCanvasPosition.y;
		}

		mToolPosition.set(newCursorPositionX, newCursorPositionY);

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
		return true;
	}

	@Override
	public boolean handleUp(PointF coordinate) {

		mMovedDistance.set(
				mMovedDistance.x
						+ Math.abs(coordinate.x - mPreviousEventCoordinate.x),
				mMovedDistance.y
						+ Math.abs(coordinate.y - mPreviousEventCoordinate.y));

		handleDrawMode();

		return true;
	}

	@Override
	public void resetInternalState() {
		pathToDraw.rewind();
	}

	@Override
	public void drawShape(Canvas canvas) {
		float brushStrokeWidth = Math.max((mBitmapPaint.getStrokeWidth() / 2f),
				1f);

		float strokeWidth = getStrokeWidthForZoom(DEFAULT_TOOL_STROKE_WIDTH,
				MINIMAL_TOOL_STROKE_WIDTH, MAXIMAL_TOOL_STROKE_WIDTH);
		float cursorPartLength = strokeWidth * 2;

		float innerCircleRadius = brushStrokeWidth + (strokeWidth / 2f);
		float outerCircleRadius = innerCircleRadius + strokeWidth;

		mLinePaint.setColor(mPrimaryShapeColor);
		mLinePaint.setStyle(Style.STROKE);
		mLinePaint.setStrokeWidth(strokeWidth);
		Cap strokeCap = mBitmapPaint.getStrokeCap();

		if (strokeCap.equals(Cap.ROUND)) {
			canvas.drawCircle(this.mToolPosition.x, this.mToolPosition.y,
					outerCircleRadius, mLinePaint);
			mLinePaint.setColor(Color.LTGRAY);

			canvas.drawCircle(this.mToolPosition.x, this.mToolPosition.y,
					innerCircleRadius, mLinePaint);

			mLinePaint.setColor(Color.TRANSPARENT);
			mLinePaint.setStyle(Style.FILL);
			canvas.drawCircle(mToolPosition.x, mToolPosition.y,
					innerCircleRadius - (strokeWidth / 2f), mLinePaint);
		} else {
			RectF strokeRect = new RectF(
					(this.mToolPosition.x - outerCircleRadius),
					(this.mToolPosition.y - outerCircleRadius),
					(this.mToolPosition.x + outerCircleRadius),
					(this.mToolPosition.y + outerCircleRadius));
			canvas.drawRect(strokeRect, mLinePaint);
			strokeRect.set((this.mToolPosition.x - innerCircleRadius),
					(this.mToolPosition.y - innerCircleRadius),
					(this.mToolPosition.x + innerCircleRadius),
					(this.mToolPosition.y + innerCircleRadius));
			mLinePaint.setColor(Color.LTGRAY);
			canvas.drawRect(strokeRect, mLinePaint);

			mLinePaint.setColor(Color.TRANSPARENT);
			mLinePaint.setStyle(Style.FILL);
			strokeRect
					.set((this.mToolPosition.x - innerCircleRadius + (strokeWidth / 2f)),
							(this.mToolPosition.y - innerCircleRadius + (strokeWidth / 2f)),
							(this.mToolPosition.x + innerCircleRadius - (strokeWidth / 2f)),
							(this.mToolPosition.y + innerCircleRadius - (strokeWidth / 2f)));
			canvas.drawRect(strokeRect, mLinePaint);
		}

		// DRAW outer target lines
		mLinePaint.setStyle(Style.FILL);
		float startLineLengthAddition = (strokeWidth / 2f);
		float endLineLengthAddition = cursorPartLength + strokeWidth;
		for (int line_nr = 0; line_nr < CURSOR_LINES; line_nr++, startLineLengthAddition = (strokeWidth / 2f)
				+ cursorPartLength * line_nr, endLineLengthAddition = strokeWidth
				+ cursorPartLength * (line_nr + 1f)) {
			if ((line_nr % 2) == 0) {
				mLinePaint.setColor(mSecondaryShapeColor);

			} else {
				mLinePaint.setColor(mPrimaryShapeColor);
			}

			// LEFT
			canvas.drawLine(this.mToolPosition.x - outerCircleRadius
					- startLineLengthAddition, this.mToolPosition.y,
					this.mToolPosition.x - outerCircleRadius
							- endLineLengthAddition, this.mToolPosition.y,
					mLinePaint);
			// RIGHT
			canvas.drawLine(this.mToolPosition.x + outerCircleRadius
					+ startLineLengthAddition, this.mToolPosition.y,
					this.mToolPosition.x + outerCircleRadius
							+ endLineLengthAddition, this.mToolPosition.y,
					mLinePaint);

			// BOTTOM
			canvas.drawLine(this.mToolPosition.x, this.mToolPosition.y
					+ outerCircleRadius + startLineLengthAddition,
					this.mToolPosition.x, this.mToolPosition.y
							+ outerCircleRadius + endLineLengthAddition,
					mLinePaint);

			// TOP
			canvas.drawLine(this.mToolPosition.x, this.mToolPosition.y
					- outerCircleRadius - startLineLengthAddition,
					this.mToolPosition.x, this.mToolPosition.y
							- outerCircleRadius - endLineLengthAddition,
					mLinePaint);
		}
	}

	@Override
	public void draw(Canvas canvas) {
		changePaintColor(mCanvasPaint.getColor());
		if (toolInDrawMode) {
			if (mCanvasPaint.getColor() == Color.TRANSPARENT) {
				mCanvasPaint.setColor(Color.BLACK);
				canvas.drawPath(pathToDraw, mCanvasPaint);
				mCanvasPaint.setColor(Color.TRANSPARENT);
			} else {
				canvas.drawPath(pathToDraw, mBitmapPaint);
			}
		}
		this.drawShape(canvas);
	}

	protected boolean addPathCommand(PointF coordinate) {
		pathToDraw.lineTo(coordinate.x, coordinate.y);
		Command command = new PathCommand(mBitmapPaint, pathToDraw);
		return PaintroidApplication.commandManager.commitCommand(command);
	}

	protected boolean addPointCommand(PointF coordinate) {
		Command command = new PointCommand(mBitmapPaint, coordinate);
		return PaintroidApplication.commandManager.commitCommand(command);
	}

	@Override
	public void attributeButtonClick(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
		case BUTTON_ID_PARAMETER_BOTTOM_1:
			showBrushPicker();
			break;
		case BUTTON_ID_PARAMETER_BOTTOM_2:
		case BUTTON_ID_PARAMETER_TOP:
			showColorPicker();
			break;
		default:
			break;
		}
	}

	@Override
	public int getAttributeButtonResource(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
		case BUTTON_ID_PARAMETER_TOP:
			return getStrokeColorResource();
		case BUTTON_ID_PARAMETER_BOTTOM_1:
			return R.drawable.icon_menu_strokes;
		case BUTTON_ID_PARAMETER_BOTTOM_2:
			return R.drawable.icon_menu_color_palette;
		default:
			return super.getAttributeButtonResource(buttonNumber);
		}
	}

	private void handleDrawMode() {

		if (toolInDrawMode) {
			if (MOVE_TOLERANCE < mMovedDistance.x
					|| MOVE_TOLERANCE < mMovedDistance.y) {
				addPathCommand(mToolPosition);
				mSecondaryShapeColor = mBitmapPaint.getColor();
			} else {
				toolInDrawMode = false;
				mSecondaryShapeColor = Color.LTGRAY;
			}
		} else {
			if (MOVE_TOLERANCE >= mMovedDistance.x
					&& MOVE_TOLERANCE >= mMovedDistance.y) {
				toolInDrawMode = true;
				mSecondaryShapeColor = mBitmapPaint.getColor();
				addPointCommand(mToolPosition);
			}
		}
	}
}
