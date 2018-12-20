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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PointF;

import org.catrobat.paintroid.CurrentToolWrapper;
import org.catrobat.paintroid.DrawingSurfaceWrapper;
import org.catrobat.paintroid.LayerModelWrapper;
import org.catrobat.paintroid.PerspectiveWrapper;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.listener.BrushPickerView;
import org.catrobat.paintroid.tools.ToolType;

public class LineTool extends BaseTool {

	protected PointF initialEventCoordinate;
	protected PointF currentCoordinate;
	protected boolean pathInsideBitmap;
	private BrushPickerView brushPickerView;

	public LineTool(Context context, ToolType toolType, DrawingSurfaceWrapper drawingSurfaceWrapper,
					CurrentToolWrapper currentToolWrapper, PerspectiveWrapper perspectiveWrapper,
					LayerModelWrapper layerModelWrapper, CommandManager commandManager) {
		super(context, toolType, drawingSurfaceWrapper, currentToolWrapper, perspectiveWrapper, layerModelWrapper, commandManager);
	}

	@Override
	public void draw(Canvas canvas) {
		if (initialEventCoordinate == null || currentCoordinate == null) {
			return;
		}

		setPaintColor(CANVAS_PAINT.getColor());

		canvas.save();
		canvas.clipRect(0, 0,
				drawingSurfaceWrapper.getBitmapWidth(),
				drawingSurfaceWrapper.getBitmapHeight());
		if (CANVAS_PAINT.getAlpha() == 0x00) {
			CANVAS_PAINT.setColor(Color.BLACK);
			canvas.drawLine(initialEventCoordinate.x,
					initialEventCoordinate.y, currentCoordinate.x,
					currentCoordinate.y, CANVAS_PAINT);
			CANVAS_PAINT.setColor(Color.TRANSPARENT);
		} else {
			canvas.drawLine(initialEventCoordinate.x,
					initialEventCoordinate.y, currentCoordinate.x,
					currentCoordinate.y, BITMAP_PAINT);
		}
		canvas.restore();
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		if (coordinate == null) {
			return false;
		}
		initialEventCoordinate = new PointF(coordinate.x, coordinate.y);
		previousEventCoordinate = new PointF(coordinate.x, coordinate.y);
		pathInsideBitmap = false;

		pathInsideBitmap = checkPathInsideBitmap(coordinate);
		return true;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		currentCoordinate = new PointF(coordinate.x, coordinate.y);
		if (!pathInsideBitmap && checkPathInsideBitmap(coordinate)) {
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

		if (!pathInsideBitmap && checkPathInsideBitmap(coordinate)) {
			pathInsideBitmap = true;
		}

		if (pathInsideBitmap) {
			Command command = commandFactory.createPathCommand(BITMAP_PAINT, finalPath);
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
		brushPickerView.invalidate();
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
	}
}
