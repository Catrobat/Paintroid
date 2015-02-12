/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.tools.implementation;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.PathCommand;
import org.catrobat.paintroid.command.implementation.PointCommand;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.TopBar.ToolButtonIDs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PointF;

public class DrawTool extends BaseTool {
	// TODO put in PaintroidApplication and scale dynamically depending on
	// screen resolution.
	public static final int STROKE_1 = 1;
	public static final int STROKE_5 = 5;
	public static final int STROKE_15 = 15;
	public static final int STROKE_25 = 25;

	protected final Path pathToDraw;
	protected PointF mInitialEventCoordinate;
	protected final PointF movedDistance;

	public DrawTool(Context context, ToolType toolType) {
		super(context, toolType);
		pathToDraw = new Path();
		pathToDraw.incReserve(1);
		movedDistance = new PointF(0f, 0f);
	}

	@Override
	public void draw(Canvas canvas) {
		changePaintColor(mCanvasPaint.getColor());
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
		return true;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		if (mInitialEventCoordinate == null || mPreviousEventCoordinate == null
				|| coordinate == null) {
			return false;
		}
		pathToDraw.quadTo(mPreviousEventCoordinate.x,
				mPreviousEventCoordinate.y, coordinate.x, coordinate.y);
		pathToDraw.incReserve(1);
		movedDistance.set(
				movedDistance.x
						+ Math.abs(coordinate.x - mPreviousEventCoordinate.x),
				movedDistance.y
						+ Math.abs(coordinate.y - mPreviousEventCoordinate.y));
		mPreviousEventCoordinate.set(coordinate.x, coordinate.y);

		return true;
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		if (mInitialEventCoordinate == null || mPreviousEventCoordinate == null
				|| coordinate == null) {
			return false;
		}
		movedDistance.set(
				movedDistance.x
						+ Math.abs(coordinate.x - mPreviousEventCoordinate.x),
				movedDistance.y
						+ Math.abs(coordinate.y - mPreviousEventCoordinate.y));
		boolean returnValue;
		if (MOVE_TOLERANCE < movedDistance.x
				|| MOVE_TOLERANCE < movedDistance.y) {
			returnValue = addPathCommand(coordinate);
		} else {
			returnValue = addPointCommand(mInitialEventCoordinate);
		}
		return returnValue;
	}

	protected boolean addPathCommand(PointF coordinate) {
		pathToDraw.lineTo(coordinate.x, coordinate.y);
		Command command = new PathCommand(mBitmapPaint, pathToDraw);
		PaintroidApplication.commandManager.commitCommand(command);
		return true;
	}

	protected boolean addPointCommand(PointF coordinate) {
		Command command = new PointCommand(mBitmapPaint, coordinate);
		PaintroidApplication.commandManager.commitCommand(command);
		return true;
	}

	@Override
	public int getAttributeButtonResource(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
		case BUTTON_ID_PARAMETER_TOP:
			return getStrokeColorResource();
		case BUTTON_ID_PARAMETER_BOTTOM_1:
			return R.drawable.icon_menu_strokes;
		case BUTTON_ID_PARAMETER_BOTTOM_2:
			return R.drawable.icon_menu_color_palette;
		default:
			return super.getAttributeButtonResource(buttonNumber);
		}
	}

	@Override
	public void attributeButtonClick(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
		case BUTTON_ID_PARAMETER_BOTTOM_1:
			showBrushPicker();
			break;
		case BUTTON_ID_PARAMETER_BOTTOM_2:
		case BUTTON_ID_PARAMETER_TOP:
			showColorPicker();
			break;
		default:
			break;
		}
	}

	@Override
	public void resetInternalState() {
		pathToDraw.rewind();
		mInitialEventCoordinate = null;
		mPreviousEventCoordinate = null;
	}
}
