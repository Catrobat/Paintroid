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
import android.support.annotation.VisibleForTesting;
import android.widget.Toast;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.command.implementation.PathCommand;
import org.catrobat.paintroid.command.implementation.PointCommand;
import org.catrobat.paintroid.listener.BrushPickerView;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.ToastFactory;

public class CursorTool extends BaseToolWithShape {

	private static final float DEFAULT_TOOL_STROKE_WIDTH = 5f;
	private static final float MINIMAL_TOOL_STROKE_WIDTH = 1f;
	private static final float MAXIMAL_TOOL_STROKE_WIDTH = 10f;
	private static final int CURSOR_LINES = 4;

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public Path pathToDraw;
	private boolean pathInsideBitmap;
	private int cursorToolPrimaryShapeColor;
	@VisibleForTesting
	public int cursorToolSecondaryShapeColor;
	@VisibleForTesting
	public boolean toolInDrawMode = false;
	private BrushPickerView brushPickerView;

	public CursorTool(Context context, ToolType toolType) {
		super(context, toolType);

		pathToDraw = new Path();
		pathToDraw.incReserve(1);
		cursorToolPrimaryShapeColor = context.getResources().getColor(
						R.color.cursor_tool_deactive_primary_color);
		cursorToolSecondaryShapeColor = Color.LTGRAY;
		pathInsideBitmap = false;
	}

	@Override
	public void changePaintColor(int color) {
		super.changePaintColor(color);
		if (toolInDrawMode) {
			cursorToolSecondaryShapeColor = BITMAP_PAINT.getColor();
		}
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		pathToDraw.moveTo(this.toolPosition.x, this.toolPosition.y);
		previousEventCoordinate.set(coordinate);
		movedDistance.set(0, 0);
		pathInsideBitmap = false;

		pathInsideBitmap = checkPathInsideBitmap(coordinate);
		return true;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		final float vectorCX = coordinate.x - previousEventCoordinate.x;
		final float vectorCY = coordinate.y - previousEventCoordinate.y;

		float newCursorPositionX = this.toolPosition.x + vectorCX;
		float newCursorPositionY = this.toolPosition.y + vectorCY;

		if (!pathInsideBitmap && checkPathInsideBitmap(coordinate)) {
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

		toolPosition.set(newCursorPositionX, newCursorPositionY);

		if (toolInDrawMode) {
			final float cx = (this.toolPosition.x + newCursorPositionX) / 2f;
			final float cy = (this.toolPosition.y + newCursorPositionY) / 2f;

			pathToDraw.quadTo(this.toolPosition.x, this.toolPosition.y, cx, cy);
			pathToDraw.incReserve(1);
		}

		movedDistance.set(
				movedDistance.x + Math.abs(coordinate.x - previousEventCoordinate.x),
				movedDistance.y + Math.abs(coordinate.y - previousEventCoordinate.y));

		previousEventCoordinate.set(coordinate.x, coordinate.y);
		return true;
	}

	@Override
	public boolean handleUp(PointF coordinate) {

		if (!pathInsideBitmap && checkPathInsideBitmap(coordinate)) {
			pathInsideBitmap = true;
		}

		movedDistance.set(
				movedDistance.x
						+ Math.abs(coordinate.x - previousEventCoordinate.x),
				movedDistance.y
						+ Math.abs(coordinate.y - previousEventCoordinate.y));

		handleDrawMode();
		return true;
	}

	@Override
	public void resetInternalState() {
		pathToDraw.rewind();
	}

	@Override
	public void drawShape(Canvas canvas) {
		float brushStrokeWidth = Math.max((BITMAP_PAINT.getStrokeWidth() / 2f), 1f);

		float strokeWidth = getStrokeWidthForZoom(DEFAULT_TOOL_STROKE_WIDTH,
				MINIMAL_TOOL_STROKE_WIDTH, MAXIMAL_TOOL_STROKE_WIDTH);
		float cursorPartLength = strokeWidth * 2;

		float innerCircleRadius = brushStrokeWidth + (strokeWidth / 2f);
		float outerCircleRadius = innerCircleRadius + strokeWidth;

		linePaint.setColor(cursorToolPrimaryShapeColor);
		linePaint.setStyle(Style.STROKE);
		linePaint.setStrokeWidth(strokeWidth);
		Cap strokeCap = BITMAP_PAINT.getStrokeCap();

		if (strokeCap.equals(Cap.ROUND)) {
			canvas.drawCircle(this.toolPosition.x, this.toolPosition.y,
					outerCircleRadius, linePaint);
			linePaint.setColor(Color.LTGRAY);

			canvas.drawCircle(this.toolPosition.x, this.toolPosition.y,
					innerCircleRadius, linePaint);

			linePaint.setColor(Color.TRANSPARENT);
			linePaint.setStyle(Style.FILL);
			canvas.drawCircle(toolPosition.x, toolPosition.y,
					innerCircleRadius - (strokeWidth / 2f), linePaint);
		} else {
			RectF strokeRect = new RectF(
					(this.toolPosition.x - outerCircleRadius),
					(this.toolPosition.y - outerCircleRadius),
					(this.toolPosition.x + outerCircleRadius),
					(this.toolPosition.y + outerCircleRadius));
			canvas.drawRect(strokeRect, linePaint);
			strokeRect.set((this.toolPosition.x - innerCircleRadius),
					(this.toolPosition.y - innerCircleRadius),
					(this.toolPosition.x + innerCircleRadius),
					(this.toolPosition.y + innerCircleRadius));
			linePaint.setColor(Color.LTGRAY);
			canvas.drawRect(strokeRect, linePaint);

			linePaint.setColor(Color.TRANSPARENT);
			linePaint.setStyle(Style.FILL);
			strokeRect
					.set((this.toolPosition.x - innerCircleRadius + (strokeWidth / 2f)),
							(this.toolPosition.y - innerCircleRadius + (strokeWidth / 2f)),
							(this.toolPosition.x + innerCircleRadius - (strokeWidth / 2f)),
							(this.toolPosition.y + innerCircleRadius - (strokeWidth / 2f)));
			canvas.drawRect(strokeRect, linePaint);
		}

		// DRAW outer target lines
		linePaint.setStyle(Style.FILL);
		float startLineLengthAddition = (strokeWidth / 2f);
		float endLineLengthAddition = cursorPartLength + strokeWidth;
		for (int lineNr = 0; lineNr < CURSOR_LINES; lineNr++, startLineLengthAddition = (strokeWidth / 2f)
				+ cursorPartLength * lineNr, endLineLengthAddition = strokeWidth
				+ cursorPartLength * (lineNr + 1f)) {
			if ((lineNr % 2) == 0) {
				linePaint.setColor(cursorToolSecondaryShapeColor);
			} else {
				linePaint.setColor(cursorToolPrimaryShapeColor);
			}

			// LEFT
			canvas.drawLine(this.toolPosition.x - outerCircleRadius
							- startLineLengthAddition, this.toolPosition.y,
					this.toolPosition.x - outerCircleRadius
							- endLineLengthAddition, this.toolPosition.y,
					linePaint);
			// RIGHT
			canvas.drawLine(this.toolPosition.x + outerCircleRadius
							+ startLineLengthAddition, this.toolPosition.y,
					this.toolPosition.x + outerCircleRadius
							+ endLineLengthAddition, this.toolPosition.y,
					linePaint);

			// BOTTOM
			canvas.drawLine(this.toolPosition.x, this.toolPosition.y
							+ outerCircleRadius + startLineLengthAddition,
					this.toolPosition.x, this.toolPosition.y
							+ outerCircleRadius + endLineLengthAddition,
					linePaint);

			// TOP
			canvas.drawLine(this.toolPosition.x, this.toolPosition.y
							- outerCircleRadius - startLineLengthAddition,
					this.toolPosition.x, this.toolPosition.y
							- outerCircleRadius - endLineLengthAddition,
					linePaint);
		}
	}

	@Override
	public void draw(Canvas canvas) {
		setPaintColor(CANVAS_PAINT.getColor());
		if (toolInDrawMode) {
			canvas.save();
			canvas.clipRect(0, 0,
					PaintroidApplication.drawingSurface.getBitmapWidth(),
					PaintroidApplication.drawingSurface.getBitmapHeight());
			if (CANVAS_PAINT.getColor() == Color.TRANSPARENT) {
				CANVAS_PAINT.setColor(Color.BLACK);
				canvas.drawPath(pathToDraw, CANVAS_PAINT);
				CANVAS_PAINT.setColor(Color.TRANSPARENT);
			} else {
				canvas.drawPath(pathToDraw, BITMAP_PAINT);
			}
			canvas.restore();
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
		Command command = new PathCommand(BITMAP_PAINT, pathToDraw);
		PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), command);
		return true;
	}

	protected boolean addPointCommand(PointF coordinate) {
		if (!pathInsideBitmap) {
			PaintroidApplication.currentTool.resetInternalState(StateChange.RESET_INTERNAL_STATE);
			return false;
		}
		Layer layer = LayerListener.getInstance().getCurrentLayer();
		Command command = new PointCommand(BITMAP_PAINT, coordinate);
		PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), command);
		return true;
	}

	private void handleDrawMode() {

		if (toolInDrawMode) {
			if (MOVE_TOLERANCE < movedDistance.x
					|| MOVE_TOLERANCE < movedDistance.y) {
				addPathCommand(toolPosition);
				cursorToolSecondaryShapeColor = BITMAP_PAINT.getColor();
			} else {
				ToastFactory.makeText(context, R.string.cursor_draw_inactive, Toast.LENGTH_SHORT).show();
				toolInDrawMode = false;
				cursorToolSecondaryShapeColor = Color.LTGRAY;
			}
		} else {
			if (MOVE_TOLERANCE >= movedDistance.x
					&& MOVE_TOLERANCE >= movedDistance.y) {
				ToastFactory.makeText(context, R.string.cursor_draw_active, Toast.LENGTH_SHORT).show();
				toolInDrawMode = true;
				cursorToolSecondaryShapeColor = BITMAP_PAINT.getColor();
				addPointCommand(toolPosition);
			}
		}
	}

	@Override
	public void setupToolOptions() {
		brushPickerView = new BrushPickerView(toolSpecificOptionsLayout);
		brushPickerView.setCurrentPaint(BITMAP_PAINT);
	}

	@Override
	public void startTool() {
		super.startTool();
		brushPickerView.addBrushChangedListener(onBrushChangedListener);
	}

	@Override
	public void leaveTool() {
		super.leaveTool();
		brushPickerView.removeBrushChangedListener(onBrushChangedListener);
		brushPickerView.removeListeners();
	}
}
