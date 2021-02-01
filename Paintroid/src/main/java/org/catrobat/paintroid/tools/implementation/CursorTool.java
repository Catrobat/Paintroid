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
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.common.CommonBrushChangedListener;
import org.catrobat.paintroid.tools.common.CommonBrushPreviewListener;
import org.catrobat.paintroid.tools.options.BrushToolOptionsView;
import org.catrobat.paintroid.tools.options.ToolOptionsVisibilityController;

import androidx.annotation.VisibleForTesting;

import static org.catrobat.paintroid.tools.common.Constants.MOVE_TOLERANCE;

public class CursorTool extends BaseToolWithShape {

	private static final float DEFAULT_TOOL_STROKE_WIDTH = 5f;
	private static final float MINIMAL_TOOL_STROKE_WIDTH = 1f;
	private static final float MAXIMAL_TOOL_STROKE_WIDTH = 10f;
	private static final int CURSOR_LINES = 4;

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public Path pathToDraw;
	private boolean pointInsideBitmap;
	private int cursorToolPrimaryShapeColor;
	@VisibleForTesting
	public int cursorToolSecondaryShapeColor;
	@VisibleForTesting
	public boolean toolInDrawMode = false;
	private BrushToolOptionsView brushToolOptionsView;

	public CursorTool(BrushToolOptionsView brushToolOptionsView, ContextCallback contextCallback, ToolOptionsVisibilityController toolOptionsViewController,
			ToolPaint toolPaint, Workspace workspace, CommandManager commandManager) {
		super(contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager);
		this.brushToolOptionsView = brushToolOptionsView;

		pathToDraw = new Path();
		pathToDraw.incReserve(1);
		cursorToolPrimaryShapeColor = contextCallback.getColor(R.color.pocketpaint_main_cursor_tool_inactive_primary_color);
		cursorToolSecondaryShapeColor = Color.LTGRAY;
		pointInsideBitmap = false;

		brushToolOptionsView.setBrushChangedListener(new CommonBrushChangedListener(this));
		brushToolOptionsView.setBrushPreviewListener(new CommonBrushPreviewListener(toolPaint, getToolType()));
		brushToolOptionsView.setCurrentPaint(toolPaint.getPaint());
	}

	@Override
	public void changePaintColor(int color) {
		super.changePaintColor(color);
		if (toolInDrawMode) {
			cursorToolSecondaryShapeColor = toolPaint.getColor();
		}
		if (brushToolOptionsView != null) {
			brushToolOptionsView.invalidate();
		}
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		pathToDraw.moveTo(this.toolPosition.x, this.toolPosition.y);
		previousEventCoordinate.set(coordinate);
		movedDistance.set(0, 0);
		pointInsideBitmap = false;

		pointInsideBitmap = workspace.contains(toolPosition);
		return true;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		final float deltaX = coordinate.x - previousEventCoordinate.x;
		final float deltaY = coordinate.y - previousEventCoordinate.y;
		previousEventCoordinate.set(coordinate.x, coordinate.y);
		pointInsideBitmap = pointInsideBitmap || workspace.contains(toolPosition);

		PointF newToolPosition = calculateNewClampedToolPosition(deltaX, deltaY);

		if (toolInDrawMode) {
			float dx = (toolPosition.x + newToolPosition.x) / 2f;
			float dy = (toolPosition.y + newToolPosition.y) / 2f;

			pathToDraw.quadTo(toolPosition.x, toolPosition.y, dx, dy);
			pathToDraw.incReserve(1);
		}

		toolPosition.set(newToolPosition);
		movedDistance.offset(Math.abs(deltaX), Math.abs(deltaY));
		return true;
	}

	private PointF calculateNewClampedToolPosition(float deltaX, float deltaY) {
		PointF newToolPosition = new PointF(toolPosition.x + deltaX, toolPosition.y + deltaY);

		PointF toolSurfacePosition = workspace.getSurfacePointFromCanvasPoint(newToolPosition);
		int surfaceWidth = workspace.getSurfaceWidth();
		int surfaceHeight = workspace.getSurfaceHeight();

		boolean positionOutsideBounds = !contains(toolSurfacePosition, surfaceWidth, surfaceHeight);
		if (positionOutsideBounds) {
			toolSurfacePosition.x = clamp(toolSurfacePosition.x, 0, surfaceWidth);
			toolSurfacePosition.y = clamp(toolSurfacePosition.y, 0, surfaceHeight);
			newToolPosition.set(workspace.getCanvasPointFromSurfacePoint(toolSurfacePosition));
		}
		return newToolPosition;
	}

	private float clamp(float value, float min, float max) {
		return Math.min(max, Math.max(value, min));
	}

	private boolean contains(PointF point, int width, int height) {
		return point.x >= 0 && point.y >= 0 && point.x < width && point.y < height;
	}

	@Override
	public boolean handleUp(PointF coordinate) {

		if (!pointInsideBitmap && workspace.contains(toolPosition)) {
			pointInsideBitmap = true;
		}

		movedDistance.set(
				movedDistance.x + Math.abs(coordinate.x - previousEventCoordinate.x),
				movedDistance.y + Math.abs(coordinate.y - previousEventCoordinate.y));

		handleDrawMode();
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
	public void onClickOnButton() {
	}

	@Override
	public void draw(Canvas canvas) {
		if (toolInDrawMode) {
			canvas.save();
			canvas.clipRect(0, 0, workspace.getWidth(), workspace.getHeight());
			canvas.drawPath(pathToDraw, toolPaint.getPreviewPaint());
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

		RectF bounds = new RectF();
		pathToDraw.computeBounds(bounds, true);
		bounds.inset(-toolPaint.getStrokeWidth(), -toolPaint.getStrokeWidth());

		if (workspace.intersectsWith(bounds)) {
			Command command = commandFactory.createPathCommand(toolPaint.getPaint(), pathToDraw);
			commandManager.addCommand(command);
			return true;
		}

		resetInternalState(StateChange.RESET_INTERNAL_STATE);
		return false;
	}

	protected boolean addPointCommand(PointF coordinate) {
		if (!pointInsideBitmap) {
			resetInternalState(StateChange.RESET_INTERNAL_STATE);
			return false;
		}
		Command command = commandFactory.createPointCommand(toolPaint.getPaint(), coordinate);
		commandManager.addCommand(command);
		return true;
	}

	private void handleDrawMode() {

		if (toolInDrawMode) {
			if (MOVE_TOLERANCE < movedDistance.x || MOVE_TOLERANCE < movedDistance.y) {
				addPathCommand(toolPosition);
				cursorToolSecondaryShapeColor = toolPaint.getColor();
			} else {
				contextCallback.showNotification(R.string.cursor_draw_inactive);
				toolInDrawMode = false;
				cursorToolSecondaryShapeColor = Color.LTGRAY;
			}
		} else {
			if (MOVE_TOLERANCE >= movedDistance.x && MOVE_TOLERANCE >= movedDistance.y) {
				contextCallback.showNotification(R.string.cursor_draw_active);
				toolInDrawMode = true;
				cursorToolSecondaryShapeColor = toolPaint.getColor();
				addPointCommand(toolPosition);
			}
		}
	}
}
