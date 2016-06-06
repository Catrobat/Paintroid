/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PointF;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.command.implementation.PathCommand;
import org.catrobat.paintroid.dialog.LayersDialog;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.TopBar.ToolButtonIDs;

public class LineTool extends BaseTool {

	protected PointF mInitialEventCoordinate;
	protected PointF mCurrentCoordinate;
	protected boolean pathInsideBitmap;

	public LineTool(Context context, ToolType toolType) {
		super(context, toolType);
	}

	@Override
	public void draw(Canvas canvas) {
		if (mInitialEventCoordinate == null || mCurrentCoordinate == null) {
			return;
		}

		changePaintColor(mCanvasPaint.getColor());

		if (mCanvasPaint.getAlpha() == 0x00) {
			mCanvasPaint.setColor(Color.BLACK);
			canvas.drawLine(mInitialEventCoordinate.x,
					mInitialEventCoordinate.y, mCurrentCoordinate.x,
					mCurrentCoordinate.y, mCanvasPaint);
			mCanvasPaint.setColor(Color.TRANSPARENT);
		} else {
			canvas.drawLine(mInitialEventCoordinate.x,
					mInitialEventCoordinate.y, mCurrentCoordinate.x,
					mCurrentCoordinate.y, mBitmapPaint);
		}
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		if (coordinate == null) {
			return false;
		}
		mInitialEventCoordinate = new PointF(coordinate.x, coordinate.y);
		mPreviousEventCoordinate = new PointF(coordinate.x, coordinate.y);
		pathInsideBitmap = false;

		if ((coordinate.x < PaintroidApplication.drawingSurface.getBitmapWidth()) && (coordinate.y < PaintroidApplication.drawingSurface
				.getBitmapHeight()) && (coordinate.x > 0) && (coordinate.y > 0)) {
			pathInsideBitmap = true;
		}
		return true;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		mCurrentCoordinate = new PointF(coordinate.x, coordinate.y);
		if (pathInsideBitmap == false && (coordinate.x < PaintroidApplication.drawingSurface.getBitmapWidth()) &&
				(coordinate.y < PaintroidApplication.drawingSurface.getBitmapHeight()) && (coordinate.x > 0) && (coordinate.y > 0)) {
			pathInsideBitmap = true;
		}
		return true;
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		if (mInitialEventCoordinate == null || mPreviousEventCoordinate == null
				|| coordinate == null) {
			return false;
		}
		Path finalPath = new Path();
		finalPath.moveTo(mInitialEventCoordinate.x, mInitialEventCoordinate.y);
		finalPath.lineTo(coordinate.x, coordinate.y);

		if (pathInsideBitmap == false && (coordinate.x < PaintroidApplication.drawingSurface.getBitmapWidth()) &&
				(coordinate.y < PaintroidApplication.drawingSurface.getBitmapHeight()) && (coordinate.x > 0) && (coordinate.y > 0)) {
			pathInsideBitmap = true;
		}

		if (pathInsideBitmap)
		{
			Command command = new PathCommand(mBitmapPaint, finalPath);
			Layer layer = LayersDialog.getInstance().getCurrentLayer();
			PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), command);
		}

//		PaintroidApplication.drawingSurface.getSurfaceViewDrawTrigger().redraw();
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
		mInitialEventCoordinate = null;
		mCurrentCoordinate = null;
	}
}
