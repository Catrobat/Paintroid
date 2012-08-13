/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  Paintroid: An image manipulation application for Android, part of the
 *  Catroid project and Catroid suite of software.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.tools.implementation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.command.Command;
import at.tugraz.ist.paintroid.command.implementation.PathCommand;
import at.tugraz.ist.paintroid.command.implementation.PointCommand;
import at.tugraz.ist.paintroid.ui.button.ToolbarButton.ToolButtonIDs;

public class DrawTool extends BaseTool {
	// TODO put in PaintroidApplication and scale dynamically depending on screen resolution.
	public static final int STROKE_1 = 1;
	public static final int STROKE_5 = 5;
	public static final int STROKE_15 = 15;
	public static final int STROKE_25 = 25;

	protected final Path pathToDraw;
	protected PointF previousEventCoordinate;
	protected PointF initialEventCoordinate;
	protected final PointF movedDistance;

	public DrawTool(Context context, ToolType toolType) {
		super(context, toolType);
		pathToDraw = new Path();
		pathToDraw.incReserve(1);
		movedDistance = new PointF(0f, 0f);
	}

	@Override
	public void draw(Canvas canvas, boolean useCanvasTransparencyPaint) {
		if (useCanvasTransparencyPaint) {
			canvas.drawPath(pathToDraw, canvasPaint);
		} else {
			canvas.drawPath(pathToDraw, bitmapPaint);
		}
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		if (coordinate == null) {
			return false;
		}
		initialEventCoordinate = new PointF(coordinate.x, coordinate.y);
		previousEventCoordinate = new PointF(coordinate.x, coordinate.y);
		pathToDraw.moveTo(coordinate.x, coordinate.y);
		movedDistance.set(0, 0);
		return true;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		if (initialEventCoordinate == null || previousEventCoordinate == null || coordinate == null) {
			return false;
		}
		final float cx = (previousEventCoordinate.x + coordinate.x) / 2;
		final float cy = (previousEventCoordinate.y + coordinate.y) / 2;
		pathToDraw.quadTo(previousEventCoordinate.x, previousEventCoordinate.y, cx, cy);
		pathToDraw.incReserve(1);
		movedDistance.set(movedDistance.x + Math.abs(coordinate.x - previousEventCoordinate.x),
				movedDistance.y + Math.abs(coordinate.y - previousEventCoordinate.y));
		previousEventCoordinate.set(coordinate.x, coordinate.y);
		return true;
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		if (initialEventCoordinate == null || previousEventCoordinate == null || coordinate == null) {
			return false;
		}
		movedDistance.set(movedDistance.x + Math.abs(coordinate.x - previousEventCoordinate.x),
				movedDistance.y + Math.abs(coordinate.y - previousEventCoordinate.y));
		boolean returnValue;
		if (PaintroidApplication.MOVE_TOLLERANCE < movedDistance.x
				|| PaintroidApplication.MOVE_TOLLERANCE < movedDistance.y) {
			returnValue = addPathCommand(coordinate);
		} else {
			returnValue = addPointCommand(initialEventCoordinate);
		}
		return returnValue;
	}

	protected boolean addPathCommand(PointF coordinate) {
		pathToDraw.lineTo(coordinate.x, coordinate.y);
		Command command = new PathCommand(bitmapPaint, pathToDraw);
		PaintroidApplication.COMMAND_MANAGER.commitCommand(command);
		return true;
	}

	protected boolean addPointCommand(PointF coordinate) {
		Command command = new PointCommand(bitmapPaint, coordinate);
		PaintroidApplication.COMMAND_MANAGER.commitCommand(command);
		return true;
	}

	@Override
	public int getAttributeButtonResource(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
			case BUTTON_ID_PARAMETER_TOP_1:
				return getStrokeWidthResource();
			case BUTTON_ID_PARAMETER_TOP_2:
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
			case BUTTON_ID_PARAMETER_TOP_1:
				showBrushPicker();
				break;
			case BUTTON_ID_PARAMETER_BOTTOM_2:
			case BUTTON_ID_PARAMETER_TOP_2:
				showColorPicker();
				break;
			default:
				break;
		}
	}

	@Override
	public void resetInternalState() {
		pathToDraw.rewind();
		initialEventCoordinate = null;
		previousEventCoordinate = null;
	}
}
