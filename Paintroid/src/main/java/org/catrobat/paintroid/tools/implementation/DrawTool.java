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
import android.widget.LinearLayout;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.command.implementation.PathCommand;
import org.catrobat.paintroid.command.implementation.PointCommand;
import org.catrobat.paintroid.dialog.LayersDialog;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.ToolType;

public class DrawTool extends BaseTool {

	protected final Path pathToDraw;
	protected PointF mInitialEventCoordinate;
	protected final PointF movedDistance;
	protected boolean pathInsideBitmap;

	public DrawTool(Context context, ToolType toolType) {
		super(context, toolType);
		pathToDraw = new Path();
		pathToDraw.incReserve(1);
		movedDistance = new PointF(0f, 0f);
		pathInsideBitmap = false;
	}

	@Override
	public void draw(Canvas canvas) {
		changePaintColor(mCanvasPaint.getColor());

		if(PaintroidApplication.currentTool.getToolType() == ToolType.ERASER
				&& mCanvasPaint.getColor() != Color.TRANSPARENT)
			changePaintColor(Color.TRANSPARENT);

		if (mCanvasPaint.getColor() == Color.TRANSPARENT) {
			mCanvasPaint.setColor(Color.BLACK);
			canvas.drawPath(pathToDraw, mCanvasPaint);
			mCanvasPaint.setColor(Color.TRANSPARENT);
		} else {
			canvas.drawPath(pathToDraw, mBitmapPaint);
		}
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		if (coordinate == null) {
			return false;
		}
		mInitialEventCoordinate = new PointF(coordinate.x, coordinate.y);
		mPreviousEventCoordinate = new PointF(coordinate.x, coordinate.y);
		pathToDraw.moveTo(coordinate.x, coordinate.y);
		movedDistance.set(0, 0);
		pathInsideBitmap = false;

		pathInsideBitmap = checkPathInsideBitmap(coordinate);
		return true;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		if (mInitialEventCoordinate == null || mPreviousEventCoordinate == null || coordinate == null) {
			return false;
		}
		pathToDraw.quadTo(mPreviousEventCoordinate.x,
				mPreviousEventCoordinate.y, coordinate.x, coordinate.y);
		pathToDraw.incReserve(1);
		movedDistance.set(
				movedDistance.x + Math.abs(coordinate.x - mPreviousEventCoordinate.x),
				movedDistance.y + Math.abs(coordinate.y - mPreviousEventCoordinate.y));
		mPreviousEventCoordinate.set(coordinate.x, coordinate.y);

		if (pathInsideBitmap == false && checkPathInsideBitmap(coordinate)) {
			pathInsideBitmap = true;
		}
		return true;
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		if (mInitialEventCoordinate == null || mPreviousEventCoordinate == null || coordinate == null) {
			return false;
		}

		if (pathInsideBitmap == false && checkPathInsideBitmap(coordinate)) {
			pathInsideBitmap = true;
		}

		movedDistance.set(
				movedDistance.x + Math.abs(coordinate.x - mPreviousEventCoordinate.x),
				movedDistance.y + Math.abs(coordinate.y - mPreviousEventCoordinate.y));
		boolean returnValue;
		if (MOVE_TOLERANCE < movedDistance.x || MOVE_TOLERANCE < movedDistance.y) {
			returnValue = addPathCommand(coordinate);
		} else {
			returnValue = addPointCommand(mInitialEventCoordinate);
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

	@Override
	public void resetInternalState() {
		pathToDraw.rewind();
		mInitialEventCoordinate = null;
		mPreviousEventCoordinate = null;
	}

	@Override
	public void setupToolOptions() {
		addBrushPickerToToolOptions();
	}
}
