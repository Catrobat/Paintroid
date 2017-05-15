/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.tools.implementation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.command.implementation.PathCommand;
import org.catrobat.paintroid.command.implementation.PointCommand;
import org.catrobat.paintroid.dialog.LayersDialog;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.ToolType;

public class CursorTool extends BaseToolWithShape {

	private static final float DEFAULT_TOOL_STROKE_WIDTH = 5f;
	private static final float MINIMAL_TOOL_STROKE_WIDTH = 1f;
	private static final float MAXIMAL_TOOL_STROKE_WIDTH = 10f;
	private static final int CURSOR_LINES = 4;

	protected Path pathToDraw;
	private int mPrimaryShapeColor;
	private int mSecondaryShapeColor;
	protected boolean pathInsideBitmap;
	private boolean toolInDrawMode = false;

	public CursorTool(Context context, ToolType toolType) {
		super(context, toolType);

		pathToDraw = new Path();
		pathToDraw.incReserve(1);
		mPrimaryShapeColor = PaintroidApplication.applicationContext
				.getResources().getColor(
						R.color.cursor_tool_deactive_primary_color);
		mSecondaryShapeColor = Color.LTGRAY;
		pathInsideBitmap = false;
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
		pathInsideBitmap = false;

		pathInsideBitmap = checkPathInsideBitmap(coordinate);
		return true;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		final float vectorCX = coordinate.x - mPreviousEventCoordinate.x;
		final float vectorCY = coordinate.y - mPreviousEventCoordinate.y;

		float newCursorPositionX = this.mToolPosition.x + vectorCX;
		float newCursorPositionY = this.mToolPosition.y + vectorCY;

		if (pathInsideBitmap == false && checkPathInsideBitmap(coordinate)) {
			pathInsideBitmap = true;
		}

		PointF cursorSurfacePosition = PaintroidApplication.perspective
				.getSurfacePointFromCanvasPoint(new PointF(newCursorPositionX, newCursorPositionY));

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

			pathToDraw.quadTo(this.mToolPosition.x, this.mToolPosition.y, cx, cy);
			pathToDraw.incReserve(1);
		}

		mMovedDistance.set(
				mMovedDistance.x + Math.abs(coordinate.x - mPreviousEventCoordinate.x),
				mMovedDistance.y + Math.abs(coordinate.y - mPreviousEventCoordinate.y));

		mPreviousEventCoordinate.set(coordinate.x, coordinate.y);
		return true;
	}

	@Override
	public boolean handleUp(PointF coordinate) {

		if (pathInsideBitmap == false && checkPathInsideBitmap(coordinate)) {
			pathInsideBitmap = true;
		}

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
		float brushStrokeWidth = Math.max((mBitmapPaint.getStrokeWidth() / 2f), 1f);

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
		if (!pathInsideBitmap) {
			PaintroidApplication.currentTool.resetInternalState(StateChange.RESET_INTERNAL_STATE);
			return false;
		}
		Layer layer = LayerListener.getInstance().getCurrentLayer();
		Command command = new PathCommand(mBitmapPaint, pathToDraw);
		PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), command);
		return true;
	}

	protected boolean addPointCommand(PointF coordinate) {
		if (!pathInsideBitmap) {
			PaintroidApplication.currentTool.resetInternalState(StateChange.RESET_INTERNAL_STATE);
			return false;
		}
		Layer layer = LayerListener.getInstance().getCurrentLayer();
		Command command = new PointCommand(mBitmapPaint, coordinate);
		PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), command);
		return true;
	}

	private void handleDrawMode() {

		if (toolInDrawMode) {
			if (MOVE_TOLERANCE < mMovedDistance.x
					|| MOVE_TOLERANCE < mMovedDistance.y) {
				addPathCommand(mToolPosition);
				mSecondaryShapeColor = mBitmapPaint.getColor();
			} else {
				Toast.makeText(mContext, R.string.cursor_draw_inactive, Toast.LENGTH_SHORT).show();
				toolInDrawMode = false;
				mSecondaryShapeColor = Color.LTGRAY;
			}
		} else {
			if (MOVE_TOLERANCE >= mMovedDistance.x
					&& MOVE_TOLERANCE >= mMovedDistance.y) {
				Toast.makeText(mContext, R.string.cursor_draw_active, Toast.LENGTH_SHORT).show();
				toolInDrawMode = true;
				mSecondaryShapeColor = mBitmapPaint.getColor();
				addPointCommand(mToolPosition);
			}
		}
	}

	@Override
	public void setupToolOptions() {
		addBrushPickerToToolOptions();
	}

}
