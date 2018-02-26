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
import android.support.annotation.VisibleForTesting;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.command.implementation.PathCommand;
import org.catrobat.paintroid.command.implementation.PointCommand;
import org.catrobat.paintroid.listener.BrushPickerView;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.ToolType;

public class DrawTool extends BaseTool {

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public Path pathToDraw;
	protected final PointF drawToolMovedDistance;
	protected PointF initialEventCoordinate;
	protected boolean pathInsideBitmap;
	@VisibleForTesting
	public BrushPickerView brushPickerView;

	public DrawTool(Context context, ToolType toolType) {
		super(context, toolType);
		pathToDraw = new Path();
		pathToDraw.incReserve(1);
		drawToolMovedDistance = new PointF(0f, 0f);
		pathInsideBitmap = false;
	}

	@Override
	public void draw(Canvas canvas) {
		setPaintColor(CANVAS_PAINT.getColor());

		if (PaintroidApplication.currentTool.getToolType() == ToolType.ERASER
				&& CANVAS_PAINT.getColor() != Color.TRANSPARENT) {
			setPaintColor(Color.TRANSPARENT);
		}

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

	@Override
	public void resetInternalState() {
		pathToDraw.rewind();
		initialEventCoordinate = null;
		previousEventCoordinate = null;
	}

	@Override
	public void setupToolOptions() {
		brushPickerView = new BrushPickerView(toolSpecificOptionsLayout);
		brushPickerView.setCurrentPaint(BITMAP_PAINT);
	}

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
