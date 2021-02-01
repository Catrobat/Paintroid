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
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

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

public class LineTool extends BaseTool {

	private PointF initialEventCoordinate;
	private PointF currentCoordinate;
	private BrushToolOptionsView brushToolOptionsView;

	public LineTool(BrushToolOptionsView brushToolOptionsView, ContextCallback contextCallback, ToolOptionsVisibilityController toolOptionsViewController,
			ToolPaint toolPaint, Workspace workspace, CommandManager commandManager) {
		super(contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager);
		this.brushToolOptionsView = brushToolOptionsView;

		brushToolOptionsView.setBrushChangedListener(new CommonBrushChangedListener(this));
		brushToolOptionsView.setBrushPreviewListener(new CommonBrushPreviewListener(toolPaint, getToolType()));
		brushToolOptionsView.setCurrentPaint(toolPaint.getPaint());
	}

	@Override
	public void draw(Canvas canvas) {
		if (initialEventCoordinate == null || currentCoordinate == null) {
			return;
		}

		canvas.save();
		canvas.clipRect(0, 0, workspace.getWidth(), workspace.getHeight());
		canvas.drawLine(initialEventCoordinate.x,
				initialEventCoordinate.y, currentCoordinate.x,
				currentCoordinate.y, toolPaint.getPreviewPaint());
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
		return true;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		currentCoordinate = new PointF(coordinate.x, coordinate.y);
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

		RectF bounds = new RectF();
		finalPath.computeBounds(bounds, true);
		bounds.inset(-toolPaint.getStrokeWidth(), -toolPaint.getStrokeWidth());

		if (workspace.intersectsWith(bounds)) {
			Command command = commandFactory.createPathCommand(toolPaint.getPaint(), finalPath);
			commandManager.addCommand(command);
		}
		resetInternalState();
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
		brushToolOptionsView.invalidate();
	}

	@VisibleForTesting (otherwise = VisibleForTesting.NONE)
	public PointF getInitialEventCoordinate() {
		return initialEventCoordinate;
	}

	@VisibleForTesting (otherwise = VisibleForTesting.NONE)
	public PointF getCurrentCoordinate() {
		return currentCoordinate;
	}
}
