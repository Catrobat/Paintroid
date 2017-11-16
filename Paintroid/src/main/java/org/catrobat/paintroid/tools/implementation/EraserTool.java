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

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.ColorInt;
import android.view.View;
import android.widget.ImageButton;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.button.ColorButton;

public class EraserTool extends DrawTool {

	private ColorButton mColorButton;
	private View mColorPickerPalette;
	private @ColorInt int mPreviousColor;

	public EraserTool(Context context, ToolType toolType) {
		super(context, toolType);

		mPreviousColor = Color.MAGENTA;
		displayEraserInsteadOfSelectedColor();
	}

	private void displayEraserInsteadOfSelectedColor() {
		mColorButton = (ColorButton) ((Activity) mContext).findViewById(R.id.btn_top_color);
		mColorButton.setImageResource(R.drawable.icon_topbar_eraser);

		mColorPickerPalette = ((Activity) mContext).findViewById(R.id.btn_top_color_palette);
		mColorPickerPalette.setVisibility(View.INVISIBLE);

		mColorButton.setDrawSelectedColor(false);
	}

	@Override
	public Paint getDrawPaint() {
		Paint paint = super.getDrawPaint();
		paint.setColor(mPreviousColor);
		return paint;
	}

	@Override
	public void setDrawPaint(Paint paint) {
		super.setDrawPaint(paint);
		mPreviousColor = paint.getColor();
	}

	@Override
	public void leaveTool() {
		super.leaveTool();
		mColorButton.resetDrawSelectedColor();
		mColorButton.setImageResource(0);
		mColorPickerPalette.setVisibility(View.VISIBLE);
	}
}
