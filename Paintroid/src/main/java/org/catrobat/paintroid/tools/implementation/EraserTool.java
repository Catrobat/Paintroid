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
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.TopBar.ToolButtonIDs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

public class EraserTool extends DrawTool {

	protected Paint mPreviousPaint;

	public EraserTool(Context context, ToolType toolType) {
		super(context, toolType);

		mPreviousPaint = new Paint(
				PaintroidApplication.currentTool.getDrawPaint());

		changePaintColor(Color.TRANSPARENT);

		mCanvasPaint.setStrokeCap(mPreviousPaint.getStrokeCap());
		mCanvasPaint.setStrokeWidth(mPreviousPaint.getStrokeWidth());

	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		return (super.handleDown(coordinate));
	}

	@Override
	public int getAttributeButtonResource(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
		case BUTTON_ID_PARAMETER_BOTTOM_2:
			return NO_BUTTON_RESOURCE;
		default:
			return super.getAttributeButtonResource(buttonNumber);
		}
	}

	@Override
	public int getAttributeButtonColor(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
		case BUTTON_ID_TOOL:
		case BUTTON_ID_PARAMETER_TOP:
			return super.getAttributeButtonColor(buttonNumber);
		default:
			return Color.TRANSPARENT;
		}
	}

	@Override
	public void attributeButtonClick(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
		case BUTTON_ID_PARAMETER_BOTTOM_1:
			super.attributeButtonClick(buttonNumber);
		default:
			break;
		}
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		return (super.handleMove(coordinate));
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		return (super.handleUp(coordinate));
	}

	@Override
	public void resetInternalState(StateChange stateChange) {
		super.resetInternalState(stateChange);
	}

	@Override
	public Paint getDrawPaint() {
		return new Paint(this.mPreviousPaint);
	}

	@Override
	public void setDrawPaint(Paint paint) {
		changePaintColor(Color.TRANSPARENT);
		// previous paint object has already been saved in constructor
	}
}
