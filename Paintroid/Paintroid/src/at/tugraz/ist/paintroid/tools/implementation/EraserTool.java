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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;

public class EraserTool extends DrawTool {

	protected Paint mPreviousPaint;

	public EraserTool(Context context, ToolType toolType) {
		super(context, toolType);

		mPreviousPaint = new Paint(PaintroidApplication.CURRENT_TOOL.getDrawPaint());

		changePaintColor(Color.TRANSPARENT);
		canvasPaint.setStrokeCap(mPreviousPaint.getStrokeCap());
		canvasPaint.setStrokeWidth(mPreviousPaint.getStrokeWidth());
	}

	@Override
	public void draw(Canvas canvas, boolean useCanvasTransparencyPaint) {
		super.draw(canvas, useCanvasTransparencyPaint);
	}

	@Override
	public boolean handleDown(PointF coordinate) {
		return (super.handleDown(coordinate));
	}

	@Override
	public int getAttributeButtonResource(int buttonNumber) {
		switch (buttonNumber) {
			case INDEX_BUTTON_MAIN:
				return R.drawable.ic_menu_more_eraser_64;
			case INDEX_BUTTON_ATTRIBUTE_2:
				return super.getAttributeButtonResource(buttonNumber);
			default:
				return 0;
		}
	}

	@Override
	public int getAttributeButtonColor(int buttonNumber) {
		switch (buttonNumber) {
			case INDEX_BUTTON_MAIN:
				return super.getAttributeButtonColor(buttonNumber);
			case INDEX_BUTTON_ATTRIBUTE_2:
				return super.getAttributeButtonColor(buttonNumber);
			default:
				return Color.TRANSPARENT;
		}
	}

	@Override
	public void attributeButtonClick(int buttonNumber) {
		switch (buttonNumber) {
			case INDEX_BUTTON_ATTRIBUTE_2:
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
	public void resetInternalState() {
		super.resetInternalState();
	}

	@Override
	public Paint getDrawPaint() {
		return new Paint(this.mPreviousPaint);
	}

	@Override
	public void setDrawPaint(Paint paint) {
		// previous paint object has already been saved in constructor
	}
}