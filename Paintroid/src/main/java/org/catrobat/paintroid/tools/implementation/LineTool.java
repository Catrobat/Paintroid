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
import android.graphics.PointF;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandFactory;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.options.BrushToolOptionsContract;
import org.catrobat.paintroid.tools.options.ToolOptionsControllerContract;

public class LineTool extends BaseTool {

	private PointF initialEventCoordinate;
	private PointF currentCoordinate;
	private boolean pathInsideBitmap;

	private BrushToolOptionsContract brushToolOptions;

	public LineTool(BrushToolOptionsContract brushToolOptions, ContextCallback contextCallback, ToolOptionsControllerContract toolOptionsController, ToolPaint toolPaint, Workspace workspace, CommandManager commandManager, CommandFactory commandFactory) {
		super(contextCallback, toolOptionsController, toolPaint, workspace, commandManager, commandFactory);

		this.brushToolOptions = brushToolOptions;
		setCallbacks();
	}

	private void setCallbacks() {
		brushToolOptions.setCurrentPaint(toolPaint.getPaint());
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
				return LineTool.this.getToolType();
			}
		});
	}

	@Override
	public void draw(Canvas canvas) {
		if (initialEventCoordinate == null || currentCoordinate == null) {
			return;
		}

		toolPaint.setColor(toolPaint.getPreviewColor());

		canvas.save();
		canvas.clipRect(0, 0, workspace.getWidth(), workspace.getHeight());
		if (toolPaint.getPreviewPaint().getAlpha() == 0x00) {
			Paint previewPaint = toolPaint.getPreviewPaint();
			previewPaint.setColor(Color.BLACK);
			canvas.drawLine(initialEventCoordinate.x,
					initialEventCoordinate.y, currentCoordinate.x,
					currentCoordinate.y, previewPaint);
			previewPaint.setColor(Color.TRANSPARENT);
		} else {
			canvas.drawLine(initialEventCoordinate.x,
					initialEventCoordinate.y, currentCoordinate.x,
					currentCoordinate.y, toolPaint.getPaint());
		}
		canvas.restore();
	}

	@Override
	public ToolType getToolType() {
		return ToolType.LINE;
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		if (coordinate == null) {
			return false;
		}
		initialEventCoordinate = new PointF(coordinate.x, coordinate.y);
		previousEventCoordinate = new PointF(coordinate.x, coordinate.y);
		pathInsideBitmap = false;

		pathInsideBitmap = workspace.contains(coordinate);
		return true;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		currentCoordinate = new PointF(coordinate.x, coordinate.y);

		if (!pathInsideBitmap && workspace.contains(coordinate)) {
			pathInsideBitmap = true;
		}
		return true;
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		if (initialEventCoordinate == null || previousEventCoordinate == null
				|| coordinate == null) {
			return false;
		}
		Path finalPath = new Path();
		finalPath.moveTo(initialEventCoordinate.x, initialEventCoordinate.y);
		finalPath.lineTo(coordinate.x, coordinate.y);

		if (!pathInsideBitmap && workspace.contains(coordinate)) {
			pathInsideBitmap = true;
		}

		if (pathInsideBitmap) {
			Command command = commandFactory.createPathCommand(toolPaint.getPaint(), finalPath);
			commandManager.addCommand(command);
		}

		return true;
	}

	@Override
	public void resetInternalState() {
		initialEventCoordinate = null;
		currentCoordinate = null;
	}

	@Override
	public void changePaintColor(int color) {
		super.changePaintColor(color);
		brushToolOptions.invalidate();
	}
}
