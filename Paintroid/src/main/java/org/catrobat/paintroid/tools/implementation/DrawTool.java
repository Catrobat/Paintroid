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
import android.graphics.Path;
import android.graphics.PointF;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.command.implementation.PathCommand;
import org.catrobat.paintroid.command.implementation.PointCommand;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.ToolType;

public class DrawTool extends BaseTool {

	protected final Path pathToDraw;
	protected final PointF drawToolMovedDistance;
	protected PointF initialEventCoordinate;
	protected boolean pathInsideBitmap;

	public DrawTool(Context context, ToolType toolType) {
		super(context, toolType);
		pathToDraw = new Path();
		pathToDraw.incReserve(1);
		drawToolMovedDistance = new PointF(0f, 0f);
		pathInsideBitmap = false;
	}

	@Override
	public void draw(Canvas canvas) {
		setPaintColor(canvasPaint.getColor());

		if (PaintroidApplication.currentTool.getToolType() == ToolType.ERASER
				&& canvasPaint.getColor() != Color.TRANSPARENT) {
			setPaintColor(Color.TRANSPARENT);
		}

		canvas.save();
		canvas.clipRect(0, 0,
				PaintroidApplication.drawingSurface.getBitmapWidth(),
				PaintroidApplication.drawingSurface.getBitmapHeight());
		if (canvasPaint.getColor() == Color.TRANSPARENT) {
			canvasPaint.setColor(Color.BLACK);
			canvas.drawPath(pathToDraw, canvasPaint);
			canvasPaint.setColor(Color.TRANSPARENT);
		} else {
			canvas.drawPath(pathToDraw, bitmapPaint);
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
		pathToDraw.moveTo(coordinate.x, coordinate.y);
		drawToolMovedDistance.set(0, 0);
		pathInsideBitmap = false;

		pathInsideBitmap = checkPathInsideBitmap(coordinate);
		return true;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		if (initialEventCoordinate == null || previousEventCoordinate == null || coordinate == null) {
			return false;
		}
		pathToDraw.quadTo(previousEventCoordinate.x,
				previousEventCoordinate.y, coordinate.x, coordinate.y);
		pathToDraw.incReserve(1);
		drawToolMovedDistance.set(
				drawToolMovedDistance.x + Math.abs(coordinate.x - previousEventCoordinate.x),
				drawToolMovedDistance.y + Math.abs(coordinate.y - previousEventCoordinate.y));
		previousEventCoordinate.set(coordinate.x, coordinate.y);

		if (!pathInsideBitmap && checkPathInsideBitmap(coordinate)) {
			pathInsideBitmap = true;
		}
		return true;
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		if (initialEventCoordinate == null || previousEventCoordinate == null || coordinate == null) {
			return false;
		}

		if (!pathInsideBitmap && checkPathInsideBitmap(coordinate)) {
			pathInsideBitmap = true;
		}

		drawToolMovedDistance.set(
				drawToolMovedDistance.x + Math.abs(coordinate.x - previousEventCoordinate.x),
				drawToolMovedDistance.y + Math.abs(coordinate.y - previousEventCoordinate.y));
		boolean returnValue;
		if (MOVE_TOLERANCE < drawToolMovedDistance.x || MOVE_TOLERANCE < drawToolMovedDistance.y) {
			returnValue = addPathCommand(coordinate);
		} else {
			returnValue = addPointCommand(initialEventCoordinate);
		}
		return returnValue;
	}

	protected boolean addPathCommand(PointF coordinate) {
		pathToDraw.lineTo(coordinate.x, coordinate.y);
		if (!pathInsideBitmap) {
			PaintroidApplication.currentTool.resetInternalState(StateChange.RESET_INTERNAL_STATE);
			return false;
		}
		Layer layer = LayerListener.getInstance().getCurrentLayer();
		Command command = new PathCommand(bitmapPaint, pathToDraw);
		PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), command);
		return true;
	}

	protected boolean addPointCommand(PointF coordinate) {
		if (!pathInsideBitmap) {
			PaintroidApplication.currentTool.resetInternalState(StateChange.RESET_INTERNAL_STATE);
			return false;
		}
		Layer layer = LayerListener.getInstance().getCurrentLayer();
		Command command = new PointCommand(bitmapPaint, coordinate);
		PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), command);
		return true;
	}

	@Override
	public void resetInternalState() {
		pathToDraw.rewind();
		initialEventCoordinate = null;
		previousEventCoordinate = null;
	}

	@Override
	public void setupToolOptions() {
		addBrushPickerToToolOptions();
	}
}
