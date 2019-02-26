/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.tools.implementation;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.VisibleForTesting;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandFactory;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.common.Constants;
import org.catrobat.paintroid.tools.options.BrushToolOptionsContract;
import org.catrobat.paintroid.tools.options.ToolOptionsControllerContract;

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
	private BrushToolOptionsContract brushToolOptions;

	public CursorTool(BrushToolOptionsContract brushToolOptions, ContextCallback contextCallback, ToolOptionsControllerContract toolOptionsController, ToolPaint toolPaint, Workspace workspace, CommandManager commandManager, CommandFactory commandFactory) {
		super(contextCallback, toolOptionsController, toolPaint, workspace, commandManager, commandFactory);

		this.brushToolOptions = brushToolOptions;

		pathToDraw = new Path();
		pathToDraw.incReserve(1);

		cursorToolPrimaryShapeColor = contextCallback.getColor(R.color.pocketpaint_main_cursor_tool_inactive_primary_color);
		cursorToolSecondaryShapeColor = Color.LTGRAY;
		pathInsideBitmap = false;

		brushToolOptions.setCurrentPaint(toolPaint.getPaint());
		setCallbacks();
	}

	private void setCallbacks() {
		brushToolOptions.setCallback(new BrushToolOptionsContract.Callback() {
			@Override
			public void setCap(Cap strokeCap) {
				changePaintStrokeCap(strokeCap);
				workspace.invalidate();
			}

			@Override
			public void setStrokeWidth(int strokeWidth) {
				changePaintStrokeWidth(strokeWidth);
				workspace.invalidate();
			}
		});
		brushToolOptions.setDrawerPreviewCallback(new BrushToolOptionsContract.PreviewCallback() {
			@Override
			public float getStrokeWidth() {
				return toolPaint.getStrokeWidth();
			}

			@Override
			public Cap getStrokeCap() {
				return toolPaint.getStrokeCap();
			}

			@Override
			public int getColor() {
				return toolPaint.getColor();
			}

			@Override
			public ToolType getToolType() {
				return CursorTool.this.getToolType();
			}
		});
	}

	@Override
	public void changePaintColor(int color) {
		super.changePaintColor(color);
		if (toolInDrawMode) {
			cursorToolSecondaryShapeColor = toolPaint.getColor();
		}
		if (brushToolOptions != null) {
			brushToolOptions.invalidate();
		}
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		pathToDraw.moveTo(toolPosition.x, toolPosition.y);
		previousEventCoordinate.set(coordinate);
		movedDistance.set(0, 0);
		pathInsideBitmap = false;

		pathInsideBitmap = workspace.contains(toolPosition);
		return true;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		final float vectorCX = coordinate.x - previousEventCoordinate.x;
		final float vectorCY = coordinate.y - previousEventCoordinate.y;

		float newCursorPositionX = toolPosition.x + vectorCX;
		float newCursorPositionY = toolPosition.y + vectorCY;

		if (!pathInsideBitmap && workspace.contains(toolPosition)) {
			pathInsideBitmap = true;
		}

		PointF cursorSurfacePosition = workspace.getSurfacePointFromCanvasPoint(new PointF(newCursorPositionX, newCursorPositionY));

		float surfaceWidth = workspace.getWidth();
		float surfaceHeight = workspace.getHeight();

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
			PointF cursorCanvasPosition = workspace.getCanvasPointFromSurfacePoint(cursorSurfacePosition);
			newCursorPositionX = cursorCanvasPosition.x;
			newCursorPositionY = cursorCanvasPosition.y;
		}

		toolPosition.set(newCursorPositionX, newCursorPositionY);

		if (toolInDrawMode) {
			final float cx = (toolPosition.x + newCursorPositionX) / 2f;
			final float cy = (toolPosition.y + newCursorPositionY) / 2f;

			pathToDraw.quadTo(toolPosition.x, toolPosition.y, cx, cy);
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

		if (!pathInsideBitmap && workspace.contains(toolPosition)) {
			pathInsideBitmap = true;
		}

		movedDistance.set(
				movedDistance.x + Math.abs(coordinate.x - previousEventCoordinate.x),
				movedDistance.y + Math.abs(coordinate.y - previousEventCoordinate.y));

		if (toolInDrawMode) {
			if (Constants.MOVE_TOLERANCE < movedDistance.x || Constants.MOVE_TOLERANCE < movedDistance.y) {
				addPathCommand(toolPosition);
				cursorToolSecondaryShapeColor = toolPaint.getColor();
			} else {
				contextCallback.showNotification(R.string.cursor_draw_inactive);
				toolInDrawMode = false;
				cursorToolSecondaryShapeColor = Color.LTGRAY;
			}
		} else {
			if (Constants.MOVE_TOLERANCE >= movedDistance.x && Constants.MOVE_TOLERANCE >= movedDistance.y) {
				contextCallback.showNotification(R.string.cursor_draw_active);
				toolInDrawMode = true;
				cursorToolSecondaryShapeColor = toolPaint.getColor();
				addPointCommand(toolPosition);
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
		float brushStrokeWidth = Math.max((toolPaint.getStrokeWidth() / 2f), 1f);

		float strokeWidth = getStrokeWidthForZoom(DEFAULT_TOOL_STROKE_WIDTH,
				MINIMAL_TOOL_STROKE_WIDTH, MAXIMAL_TOOL_STROKE_WIDTH);
		float cursorPartLength = strokeWidth * 2;

		float innerCircleRadius = brushStrokeWidth + (strokeWidth / 2f);
		float outerCircleRadius = innerCircleRadius + strokeWidth;

		linePaint.setColor(cursorToolPrimaryShapeColor);
		linePaint.setStyle(Style.STROKE);
		linePaint.setStrokeWidth(strokeWidth);
		Cap strokeCap = toolPaint.getStrokeCap();

		if (strokeCap.equals(Cap.ROUND)) {
			canvas.drawCircle(toolPosition.x, toolPosition.y,
					outerCircleRadius, linePaint);
			linePaint.setColor(Color.LTGRAY);

			canvas.drawCircle(toolPosition.x, toolPosition.y,
					innerCircleRadius, linePaint);

			linePaint.setColor(Color.TRANSPARENT);
			linePaint.setStyle(Style.FILL);
			canvas.drawCircle(toolPosition.x, toolPosition.y,
					innerCircleRadius - (strokeWidth / 2f), linePaint);
		} else {
			RectF strokeRect = new RectF(
					(toolPosition.x - outerCircleRadius),
					(toolPosition.y - outerCircleRadius),
					(toolPosition.x + outerCircleRadius),
					(toolPosition.y + outerCircleRadius));
			canvas.drawRect(strokeRect, linePaint);
			strokeRect.set((toolPosition.x - innerCircleRadius),
					(toolPosition.y - innerCircleRadius),
					(toolPosition.x + innerCircleRadius),
					(toolPosition.y + innerCircleRadius));
			linePaint.setColor(Color.LTGRAY);
			canvas.drawRect(strokeRect, linePaint);

			linePaint.setColor(Color.TRANSPARENT);
			linePaint.setStyle(Style.FILL);
			strokeRect.set((toolPosition.x - innerCircleRadius + (strokeWidth / 2f)),
							(toolPosition.y - innerCircleRadius + (strokeWidth / 2f)),
							(toolPosition.x + innerCircleRadius - (strokeWidth / 2f)),
							(toolPosition.y + innerCircleRadius - (strokeWidth / 2f)));
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
			canvas.drawLine(toolPosition.x - outerCircleRadius
							- startLineLengthAddition, toolPosition.y,
					toolPosition.x - outerCircleRadius
							- endLineLengthAddition, toolPosition.y,
					linePaint);
			// RIGHT
			canvas.drawLine(toolPosition.x + outerCircleRadius
							+ startLineLengthAddition, toolPosition.y,
					toolPosition.x + outerCircleRadius
							+ endLineLengthAddition, toolPosition.y,
					linePaint);

			// BOTTOM
			canvas.drawLine(toolPosition.x, toolPosition.y
							+ outerCircleRadius + startLineLengthAddition,
					toolPosition.x, toolPosition.y
							+ outerCircleRadius + endLineLengthAddition,
					linePaint);

			// TOP
			canvas.drawLine(toolPosition.x, toolPosition.y
							- outerCircleRadius - startLineLengthAddition,
					toolPosition.x, toolPosition.y
							- outerCircleRadius - endLineLengthAddition,
					linePaint);
		}
	}

	@Override
	public void draw(Canvas canvas) {
		toolPaint.setColor(toolPaint.getPreviewColor());
		if (toolInDrawMode) {
			canvas.save();
			canvas.clipRect(0, 0, workspace.getWidth(), workspace.getHeight());
			if (toolPaint.getPreviewColor() == Color.TRANSPARENT) {
				Paint previewPaint = toolPaint.getPreviewPaint();
				previewPaint.setColor(Color.BLACK);
				canvas.drawPath(pathToDraw, previewPaint);
				previewPaint.setColor(Color.TRANSPARENT);
			} else {
				canvas.drawPath(pathToDraw, toolPaint.getPaint());
			}
			canvas.restore();
		}
		drawShape(canvas);
	}

	@Override
	public ToolType getToolType() {
		return ToolType.CURSOR;
	}

	protected boolean addPathCommand(PointF coordinate) {
		pathToDraw.lineTo(coordinate.x, coordinate.y);
		if (!pathInsideBitmap) {
			resetInternalState(StateChange.RESET_INTERNAL_STATE);
			return false;
		}
		Command command = commandFactory.createPathCommand(toolPaint.getPaint(), pathToDraw);
		commandManager.addCommand(command);
		return true;
	}

	protected boolean addPointCommand(PointF coordinate) {
		if (!pathInsideBitmap) {
			resetInternalState(StateChange.RESET_INTERNAL_STATE);
			return false;
		}
		Command command = commandFactory.createPointCommand(toolPaint.getPaint(), coordinate);
		commandManager.addCommand(command);
		return true;
	}
}
