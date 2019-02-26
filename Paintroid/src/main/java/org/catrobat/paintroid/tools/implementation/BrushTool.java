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
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandFactory;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.common.Constants;
import org.catrobat.paintroid.tools.options.BrushToolOptionsContract;
import org.catrobat.paintroid.tools.options.ToolOptionsControllerContract;

public class BrushTool implements Tool {
	private final ToolPaint toolPaint;
	private final Workspace workspace;
	private final CommandManager commandManager;
	private final CommandFactory commandFactory;
	private final int scrollTolerance;

	private final PointF drawToolMovedDistance;
	private PointF initialEventCoordinate;
	private boolean pathInsideBitmap;
	private BrushToolOptionsContract brushToolOptions;

	@VisibleForTesting
	public Path pathToDraw;

	private PointF previousEventCoordinate;

	public BrushTool(BrushToolOptionsContract brushToolOptions, ToolOptionsControllerContract toolOptionsController, ToolPaint toolPaint,
			Workspace workspace, CommandManager commandManager, CommandFactory commandFactory) {
		this.brushToolOptions = brushToolOptions;
		this.toolPaint = toolPaint;
		this.workspace = workspace;
		this.commandManager = commandManager;
		this.commandFactory = commandFactory;

		pathToDraw = new Path();
		pathToDraw.incReserve(1);
		drawToolMovedDistance = new PointF(0f, 0f);
		pathInsideBitmap = false;

		scrollTolerance = toolOptionsController.getScrollTolerance();
		brushToolOptions.setCurrentPaint(toolPaint.getPaint());

		setCallbacks();
	}

	private void setCallbacks() {
		brushToolOptions.setCallback(new BrushToolOptionsContract.Callback() {
			@Override
			public void setCap(Cap strokeCap) {
				changePaintStrokeCap(strokeCap);
			}

			@Override
			public void setStrokeWidth(int strokeWidth) {
				changePaintStrokeWidth(strokeWidth);
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
				return BrushTool.this.getToolType();
			}
		});
	}

	@Override
	public void draw(Canvas canvas) {
		toolPaint.setColor(toolPaint.getPreviewColor());

		if (getToolType() == ToolType.ERASER && toolPaint.getPreviewColor() != Color.TRANSPARENT) {
			toolPaint.setColor(Color.TRANSPARENT);
		}

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

	@Override
	public ToolType getToolType() {
		return ToolType.BRUSH;
	}

	@Override
	public void resetInternalState(StateChange stateChange) {
		if (getToolType().shouldReactToStateChange(stateChange)) {
			resetInternalState();
		}
	}

	@Override
	public Point getAutoScrollDirection(float pointX, float pointY, int viewWidth, int viewHeight) {
		int deltaX = 0;
		int deltaY = 0;

		if (pointX < scrollTolerance) {
			deltaX = 1;
		}
		if (pointX > viewWidth - scrollTolerance) {
			deltaX = -1;
		}

		if (pointY < scrollTolerance) {
			deltaY = 1;
		}

		if (pointY > viewHeight - scrollTolerance) {
			deltaY = -1;
		}

		return new Point(deltaX, deltaY);
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle bundle) {
	}

	@Override
	public void onRestoreInstanceState(@NonNull Bundle bundle) {
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		if (coordinate == null) {
			return false;
		}

		initialEventCoordinate = new PointF(coordinate.x, coordinate.y);
		previousEventCoordinate = new PointF(coordinate.x, coordinate.y);
		pathToDraw.moveTo(coordinate.x, coordinate.y);
		drawToolMovedDistance.set(0, 0);
		pathInsideBitmap = false;

		pathInsideBitmap = workspace.contains(coordinate);
		return true;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		if (initialEventCoordinate == null || previousEventCoordinate == null || coordinate == null) {
			return false;
		}
		pathToDraw.quadTo(previousEventCoordinate.x, previousEventCoordinate.y, coordinate.x, coordinate.y);
		pathToDraw.incReserve(1);
		drawToolMovedDistance.set(
				drawToolMovedDistance.x + Math.abs(coordinate.x - previousEventCoordinate.x),
				drawToolMovedDistance.y + Math.abs(coordinate.y - previousEventCoordinate.y));
		previousEventCoordinate.set(coordinate.x, coordinate.y);

		if (!pathInsideBitmap && workspace.contains(coordinate)) {
			pathInsideBitmap = true;
		}
		return true;
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		if (initialEventCoordinate == null || previousEventCoordinate == null || coordinate == null) {
			return false;
		}

		if (!pathInsideBitmap && workspace.contains(coordinate)) {
			pathInsideBitmap = true;
		}

		drawToolMovedDistance.set(
				drawToolMovedDistance.x + Math.abs(coordinate.x - previousEventCoordinate.x),
				drawToolMovedDistance.y + Math.abs(coordinate.y - previousEventCoordinate.y));
		boolean returnValue;
		if (Constants.MOVE_TOLERANCE < drawToolMovedDistance.x || Constants.MOVE_TOLERANCE < drawToolMovedDistance.y) {
			returnValue = addPathCommand(coordinate);
		} else {
			returnValue = addPointCommand(initialEventCoordinate);
		}
		return returnValue;
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

	private void resetInternalState() {
		pathToDraw.rewind();
		initialEventCoordinate = null;
		previousEventCoordinate = null;
	}

	@Override
	public void changePaintColor(int color) {
		toolPaint.setColor(color);
		brushToolOptions.invalidate();
	}

	@Override
	public void changePaintStrokeWidth(int strokeWidth) {
		toolPaint.setStrokeWidth(strokeWidth);
	}

	@Override
	public void changePaintStrokeCap(Cap cap) {
		toolPaint.setStrokeCap(cap);
	}

	@Override
	public Paint getDrawPaint() {
		return toolPaint.getPaint();
	}

	@Override
	public void setDrawPaint(Paint paint) {
		toolPaint.setPaint(paint);
	}
}
